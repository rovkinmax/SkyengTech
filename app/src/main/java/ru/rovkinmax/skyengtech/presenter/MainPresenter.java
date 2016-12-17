package ru.rovkinmax.skyengtech.presenter;

import android.support.annotation.NonNull;

import ru.rovkinmax.skyengtech.repository.RepositoryProvider;
import ru.rovkinmax.skyengtech.rx.AsyncTransformer;
import ru.rovkinmax.skyengtech.rx.RxDecor;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.view.MainView;

public final class MainPresenter {

    @NonNull
    private final MainView view;

    @NonNull
    private final RxLifecycleProvider lifecycleProvider;

    public MainPresenter(@NonNull MainView view, @NonNull RxLifecycleProvider provider) {
        this.view = view;
        lifecycleProvider = provider;
    }

    public void logout() {
        RepositoryProvider.provideUserRepo()
                .deleteUser()
                .compose(AsyncTransformer.async())
                .compose(lifecycleProvider.lifecycle())
                .compose(RxDecor.loading(view))
                .subscribe(user -> view.navigateToLogin(), RxDecor.error(view));
    }
}
