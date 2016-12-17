package ru.rovkinmax.skyengtech.view;

import android.support.annotation.NonNull;

import ru.rovkinmax.skyengtech.model.User;

public interface SplashView {
    void navigateToMain(@NonNull User user);

    void navigateToLogin();
}
