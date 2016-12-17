package ru.rovkinmax.skyengtech.presenter;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import ru.rovkinmax.skyengtech.model.User;
import ru.rovkinmax.skyengtech.repository.RepositoryProvider;
import ru.rovkinmax.skyengtech.rx.AsyncTransformer;
import ru.rovkinmax.skyengtech.rx.RxDecor;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.utils.EmailUtil;
import ru.rovkinmax.skyengtech.view.LoginByPassView;

public final class LoginByPassPresenter {
    private static final int MIN_PASS_LENGTH = 3;

    @NonNull
    private final LoginByPassView view;

    @NonNull
    private final RxLifecycleProvider lifecycleProvider;

    public LoginByPassPresenter(@NonNull LoginByPassView view, @NonNull RxLifecycleProvider provider) {
        this.view = view;
        lifecycleProvider = provider;
    }

    public void validateCredentials(@NonNull String email, @NonNull String pass) {
        boolean isValid = EmailUtil.validateEmail(email) && pass.length() >= MIN_PASS_LENGTH;
        view.setEnableProceed(isValid);
    }

    public void login(@NonNull String email, @NonNull String pass) {
        provideAuth(email, pass)
                .compose(AsyncTransformer.async())
                .compose(lifecycleProvider.lifecycle())
                .compose(RxDecor.loading(view))
                .subscribe(view::navigateToMain, RxDecor.error(view));
    }

    @NonNull
    private Single<User> provideAuth(@NonNull String email, @NonNull String pass) {
        return RepositoryProvider.provideAuthRepo()
                .auth(email, pass);
    }
}
