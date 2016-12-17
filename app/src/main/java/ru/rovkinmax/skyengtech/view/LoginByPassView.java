package ru.rovkinmax.skyengtech.view;

import android.support.annotation.NonNull;

import ru.rovkinmax.skyengtech.model.User;

public interface LoginByPassView extends LoadingView, ErrorView {
    void setEnableProceed(boolean enable);

    void navigateToMain(@NonNull User user);
}
