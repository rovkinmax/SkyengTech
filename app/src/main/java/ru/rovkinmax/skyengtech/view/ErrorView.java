package ru.rovkinmax.skyengtech.view;

import android.support.annotation.NonNull;

public interface ErrorView {

    void showNetworkError();

    void showUnexpectedError();

    void showErrorMessage(@NonNull String message);

    void hideErrorMessage();
}
