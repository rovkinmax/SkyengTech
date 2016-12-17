package ru.rovkinmax.skyengtech.rx;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;

public class AsyncTransformer<T> implements ObservableTransformer<T, T>, SingleTransformer<T, T>, FlowableTransformer<T, T> {
    private AsyncTransformer() {
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.subscribeOn(RxSchedulers.io()).observeOn(RxSchedulers.main());
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.subscribeOn(RxSchedulers.io()).observeOn(RxSchedulers.main());
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.subscribeOn(RxSchedulers.io()).observeOn(RxSchedulers.main());
    }

    public static <T> AsyncTransformer<T> async() {
        return new AsyncTransformer<>();
    }
}
