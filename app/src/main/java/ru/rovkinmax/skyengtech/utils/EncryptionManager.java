package ru.rovkinmax.skyengtech.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.M)
public final class EncryptionManager {
    private static final String KEY_STORE_PROVIDER = "AndroidKeyStore";

    private static final String CIPHER_PROVIDER = "AndroidOpenSSL";

    private static final String DISTINGUISHED = "CN=Skyeng,O=Android Authority";

    private static final String TRANSFORMATION = String.format("%s/%s/%s",
            KeyProperties.KEY_ALGORITHM_RSA, KeyProperties.BLOCK_MODE_ECB, KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1);

    @NonNull
    private final Context context;

    @NonNull
    private final String alias;

    public EncryptionManager(@NonNull Context context, @NonNull String alias) {
        this.context = context.getApplicationContext();
        this.alias = alias;
    }

    public byte[] encryptData(byte[] data) throws Exception {
        KeyStore.PrivateKeyEntry entry = getOrCreateKey(context);
        return processByteArrayWithCipher(entry.getCertificate().getPublicKey(), data, Cipher.ENCRYPT_MODE);
    }

    public byte[] decryptData(byte[] data) throws Exception {
        KeyStore.PrivateKeyEntry entry = getOrCreateKey(context);
        return processByteArrayWithCipher(entry.getPrivateKey(), data, Cipher.DECRYPT_MODE);
    }

    private byte[] processByteArrayWithCipher(Key key, byte[] byteArray, int mode) throws Exception {
        Cipher cipher = provideCipher();
        cipher.init(mode, key);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherStream = new CipherOutputStream(outputStream, cipher);
        cipherStream.write(byteArray);
        cipherStream.close();
        byte[] processedKey = outputStream.toByteArray();
        outputStream.close();
        return processedKey;
    }

    private Cipher provideCipher() throws Exception {
        if (isPreM()) {
            return Cipher.getInstance(TRANSFORMATION, CIPHER_PROVIDER);
        } else {
            return Cipher.getInstance(TRANSFORMATION);
        }
    }

    private KeyStore.PrivateKeyEntry getOrCreateKey(Context context) throws Exception {
        KeyStore keystore = KeyStore.getInstance(KEY_STORE_PROVIDER);
        keystore.load(null);

        if (!keystore.containsAlias(alias)) {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE_PROVIDER);
            AlgorithmParameterSpec spec = isPreM() ? keyPairGeneratorSpecPreM(context) : keyPairGeneratorSpecM();
            generator.initialize(spec);
            generator.generateKeyPair();
        }
        return (KeyStore.PrivateKeyEntry) keystore.getEntry(alias, null);
    }


    private boolean isPreM() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    private KeyPairGeneratorSpec keyPairGeneratorSpecPreM(Context context) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 10);

        return new KeyPairGeneratorSpec.Builder(context)
                .setAlias(alias)
                .setSerialNumber(BigInteger.ONE)
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .setSubject(new X500Principal(DISTINGUISHED))
                .build();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private KeyGenParameterSpec keyPairGeneratorSpecM() {
        return new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                | KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build();
    }
}
