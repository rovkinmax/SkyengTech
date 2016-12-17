package ru.rovkinmax.skyengtech.presenter;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import ru.rovkinmax.skyengtech.model.User;
import ru.rovkinmax.skyengtech.repository.RepositoryProvider;
import ru.rovkinmax.skyengtech.rx.AsyncTransformer;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.view.SplashView;

public final class SplashPresenter {
    @NonNull
    private final SplashView view;

    @NonNull
    private final RxLifecycleProvider lifecycleProvider;

    public SplashPresenter(@NonNull SplashView view, @NonNull RxLifecycleProvider provider) {
        this.view = view;
        lifecycleProvider = provider;
    }

    public void checkAuth() {
        provideUser()
                .compose(AsyncTransformer.async())
                .compose(lifecycleProvider.lifecycle())
                .subscribe(view::navigateToMain, error -> view.navigateToLogin());
    }

    @NonNull
    private Single<User> provideUser() {
        return RepositoryProvider.provideUserRepo().getUser();
    }
}
