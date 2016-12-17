package ru.rovkinmax.skyengtech.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import io.reactivex.Single;
import ru.rovkinmax.skyengtech.model.RequestCodeResponse;
import ru.rovkinmax.skyengtech.repository.RepositoryProvider;
import ru.rovkinmax.skyengtech.rx.AsyncTransformer;
import ru.rovkinmax.skyengtech.rx.RxDecor;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.utils.EmailUtil;
import ru.rovkinmax.skyengtech.view.LoginByCodeView;

public final class LoginByCodePresenter {
    @NonNull
    private final LoginByCodeView view;

    @NonNull
    private final RxLifecycleProvider lifecycleProvider;

    public LoginByCodePresenter(@NonNull LoginByCodeView view, @NonNull RxLifecycleProvider provider) {
        this.view = view;
        lifecycleProvider = provider;
    }


    public void validateEmail(@NonNull String email) {
        view.setProceedEnable(EmailUtil.validateEmail(email));
    }

    public void requestCode(@NonNull String email) {
        provideRequestCode(email)
                .compose(AsyncTransformer.async())
                .compose(lifecycleProvider.lifecycle())
                .compose(RxDecor.loading(view))
                .subscribe(this::dispatchResponse, RxDecor.error(view));
    }

    private Single<RequestCodeResponse> provideRequestCode(@NonNull String email) {
        return RepositoryProvider.provideAuthRepo().requestCode(email);
    }

    private void dispatchResponse(@NonNull RequestCodeResponse response) {
        if (TextUtils.isEmpty(response.getPhone())) {
            view.navigateToConfirmWithEmail();
        } else {
            view.navigateToConfirmWithPhone(response.getPhone());
        }
    }
}
