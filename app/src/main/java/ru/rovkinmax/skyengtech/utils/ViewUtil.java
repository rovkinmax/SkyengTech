package ru.rovkinmax.skyengtech.utils;

import android.support.annotation.NonNull;
import android.view.View;

public final class ViewUtil {
    private ViewUtil() {
    }

    public static void updateViewEnabled(@NonNull View view, boolean enabled) {
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1.0f : 0.5f);
    }
}
