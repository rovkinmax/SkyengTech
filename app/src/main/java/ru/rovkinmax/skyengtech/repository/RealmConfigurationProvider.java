package ru.rovkinmax.skyengtech.repository;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.security.SecureRandom;

import io.realm.RealmConfiguration;
import ru.rovkinmax.skyengtech.BuildConfig;
import ru.rovkinmax.skyengtech.utils.EncryptionManager;
import ru.rovkinmax.skyengtech.utils.PrefsUtil;

public final class RealmConfigurationProvider {
    private static final String REALM_ALIAS = "Realm Key";

    private RealmConfigurationProvider() {
    }

    public static RealmConfiguration provideRealmConfig(Context context) {
        return new RealmConfiguration.Builder()
                .schemaVersion(BuildConfig.DATA_BASE_VERSION)
                .encryptionKey(getOrCreateRealmKey(context))
                .build();
    }

    private static byte[] getOrCreateRealmKey(Context context) {
        try {
            EncryptionManager encryptionManager = new EncryptionManager(context, REALM_ALIAS);

            String encodedRealmKey = PrefsUtil.getCode(context);
            if (TextUtils.isEmpty(encodedRealmKey)) {
                byte[] keyForRealm = generateKeyForRealm();
                byte[] encryptedKey = encryptionManager.encryptData(keyForRealm);
                PrefsUtil.saveCode(context, Base64.encodeToString(encryptedKey, Base64.NO_WRAP));
                return keyForRealm;
            }

            return encryptionManager.decryptData(Base64.decode(encodedRealmKey, Base64.NO_WRAP));
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    private static byte[] generateKeyForRealm() {
        byte[] keyForRealm = new byte[RealmConfiguration.KEY_LENGTH];
        new SecureRandom().nextBytes(keyForRealm);
        return keyForRealm;
    }
}
