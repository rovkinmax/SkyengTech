package ru.rovkinmax.skyengtech.rx;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.annotation.NonNull;

import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;


/**
 * @author Rovkin Max
 */
public class RxLifecycleProvider {

    private Disposable disposable = null;

    private BehaviorSubject<Object> subject;

    RxLifecycleProvider() {
    }

    public static RxLifecycleProvider newInstance() {
        return new RxLifecycleProvider();
    }

    @NonNull
    public static RxLifecycleProvider get(@NonNull Activity activity) {
        return get(activity.getFragmentManager());
    }

    @NonNull
    private static RxLifecycleProvider get(@NonNull FragmentManager fm) {
        final RxFragmentBundle fragment = (RxFragmentBundle) fm.findFragmentByTag(RxFragmentBundle.class.getName());
        if (fragment == null) {
            throw new NullPointerException("RxLifecycleProvider not attached to FragmentManager");
        }
        return fragment.getRxProvider();
    }

    public static void attach(@NonNull FragmentManager fm) {
        if (fm.findFragmentByTag(RxFragmentBundle.class.getName()) == null) {
            fm.beginTransaction()
                    .add(new RxFragmentBundle(), RxFragmentBundle.class.getName())
                    .commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
    }

    @NonNull
    public <T> LifecycleTransformer<T> lifecycle() {
        if (subject == null || disposable == null) {
            subject = BehaviorSubject.create();
            disposable = subject.subscribe();
        }

        return new LifecycleTransformer<>(subject);
    }

    public void unsubscribe() {
        if (disposable != null && !disposable.isDisposed()) {
            subject.onNext(new Object());
            disposable.dispose();
        }
        disposable = null;
        subject = null;
    }

    public static class LifecycleTransformer<T> implements ObservableTransformer<T, T>, SingleTransformer<T, T>, FlowableTransformer<T, T> {
        private final Observable<Object> observable;

        private LifecycleTransformer(Observable<Object> observable) {
            this.observable = observable;
        }

        @Override

        public Publisher<T> apply(Flowable<T> upstream) {
            return upstream.takeUntil(observable.toFlowable(BackpressureStrategy.LATEST));
        }

        @Override
        public ObservableSource<T> apply(Observable<T> upstream) {
            return upstream.takeUntil(observable);
        }

        @Override
        public SingleSource<T> apply(Single<T> upstream) {
            return upstream.takeUntil(observable.firstOrError());
        }
    }

}
