package ru.rovkinmax.skyengtech.utils;

import android.support.annotation.NonNull;

import java.util.regex.Pattern;

public final class EmailUtil {
    private static Pattern EMAIL_PATTERN = Pattern.compile(".+@.+\\..+");

    private EmailUtil() {
    }

    public static boolean validateEmail(@NonNull String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
