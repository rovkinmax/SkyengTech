package ru.rovkinmax.skyengtech.view;

import android.support.annotation.NonNull;

public interface LoginByCodeView extends LoadingView, ErrorView {
    void setProceedEnable(boolean enable);

    void navigateToConfirmWithEmail();

    void navigateToConfirmWithPhone(@NonNull String phone);
}
