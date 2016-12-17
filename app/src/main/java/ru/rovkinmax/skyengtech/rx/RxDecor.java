package ru.rovkinmax.skyengtech.rx;

import android.support.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Consumer;
import ru.rovkinmax.skyengtech.view.ErrorView;
import ru.rovkinmax.skyengtech.view.LoadingView;
import timber.log.Timber;

public final class RxDecor {

    private static final List<Class<?>> NETWORK_EXCEPTIONS = Arrays.asList(
            UnknownHostException.class,
            SocketTimeoutException.class
    );

    private RxDecor() {
        //Unnecessary constructor
    }

    @NonNull
    public static <T> LoadingViewTransformer<T> loading(@NonNull LoadingView view) {
        return new LoadingViewTransformer<>(view);
    }

    @NonNull
    public static Consumer<Throwable> error(@NonNull ErrorView view) {
        return e -> {
            Timber.d(e, "from RxDecor.error");
            if (e instanceof HttpException) {
                view.showErrorMessage(((HttpException) e).message());
            } else if (NETWORK_EXCEPTIONS.contains(e.getClass())) {
                view.showNetworkError();
            } else {
                view.showUnexpectedError();
            }
        };
    }

    public static class LoadingViewTransformer<T> implements ObservableTransformer<T, T>, SingleTransformer<T, T> {
        @NonNull
        private final LoadingView loadingView;

        public LoadingViewTransformer(@NonNull LoadingView view) {
            loadingView = view;
        }

        @Override
        public ObservableSource<T> apply(Observable<T> upstream) {
            return upstream
                    .doOnSubscribe((c) -> loadingView.showLoadingIndicator())
                    .doOnDispose(loadingView::hideLoadingIndicator)
                    .doOnTerminate(loadingView::hideLoadingIndicator);
        }

        @Override
        public SingleSource<T> apply(Single<T> upstream) {
            return upstream
                    .doOnSubscribe((c) -> loadingView.showLoadingIndicator())
                    .doOnDispose(loadingView::hideLoadingIndicator)
                    .doOnError((e) -> loadingView.hideLoadingIndicator())
                    .doOnSuccess((c) -> loadingView.hideLoadingIndicator());
        }
    }

}
