package ru.rovkinmax.skyengtech.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * @author Rovkin Max
 */
public final class PrefsUtil {

    private static final String KEY_CODE = "CODE";

    private PrefsUtil() {
        //Not implemented
    }

    public static void saveCode(@NonNull Context context, @NonNull String code) {
        getEditor(context).putString(KEY_CODE, code).apply();
    }

    public static String getCode(@NonNull Context context) {
        return getPreferences(context).getString(KEY_CODE, "");
    }

    private static SharedPreferences.Editor getEditor(@NonNull Context context) {
        return getPreferences(context).edit();
    }

    private static SharedPreferences getPreferences(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
