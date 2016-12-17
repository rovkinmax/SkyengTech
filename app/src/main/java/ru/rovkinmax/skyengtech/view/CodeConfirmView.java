package ru.rovkinmax.skyengtech.view;

import android.support.annotation.NonNull;

import ru.rovkinmax.skyengtech.model.User;

public interface CodeConfirmView extends LoadingView, ErrorView {
    void setProceedEnable(boolean enable);

    void setSecondsToRetry(long seconds);

    void setEnableResend();

    void navigateToMain(@NonNull User user);
}
