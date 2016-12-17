package ru.rovkinmax.skyengtech.model;

import android.content.Context;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.functions.BiFunction;
import okhttp3.Request;
import okhttp3.Response;
import ru.rovkinmax.skyengtech.repository.OkHttpResponse;

public final class AuthFactory {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BASIC_AUTH_EMAIL = "basic@m.com";

    private static final String BASIC_AUTH_PASS = "1010";

    private static final String EMAIL_CODE_TO_EMAIL = "mail@m.com";

    private static final String EMAIL_CODE_TO_PHONE = "phone@m.com";

    private static final String CODE_BY_PHONE = "0000";

    private static final String CODE_BY_EMAIL = "1111";

    private AuthFactory() {
    }

    public static BiFunction<Context, Request, Response> authWithPassword() {
        return (context, request) -> {
            try {
                String authHeaderRaw = request.header(AUTHORIZATION_HEADER).replace("Basic ", "");
                byte[] endcodedRaw = Base64.decode(authHeaderRaw.getBytes(), Base64.NO_WRAP);
                String encodedHeader = new String(endcodedRaw);
                String[] loginPass = encodedHeader.split(":");
                if (loginPass.length == 2 && BASIC_AUTH_EMAIL.equals(loginPass[0]) && BASIC_AUTH_PASS.equals(loginPass[1])) {
                    final InputStream stream = context.getAssets().open("auth.json");
                    try {
                        return OkHttpResponse.success(request, stream);
                    } finally {
                        stream.close();
                    }

                } else {
                    return OkHttpResponse.error(request, 401, "Неверный адрес электронной почты или пароль");
                }
            } catch (IOException e) {
                return OkHttpResponse.error(request, 500, e.getMessage());
            }
        };
    }

    public static BiFunction<Context, Request, Response> requestCode() {
        return (context, request) -> {
            try {
                String email = request.header("email");
                if (EMAIL_CODE_TO_EMAIL.equals(email) || EMAIL_CODE_TO_PHONE.equals(email)) {
                    String fileName = EMAIL_CODE_TO_EMAIL.equals(email) ? "code_request_email.json" : "code_request_phone.json";
                    final InputStream stream = context.getAssets().open(fileName);
                    try {
                        return OkHttpResponse.success(request, stream);
                    } finally {
                        stream.close();
                    }

                } else {
                    return OkHttpResponse.error(request, 401, "Неверный адрес электронной почты");
                }
            } catch (IOException e) {
                return OkHttpResponse.error(request, 500, e.getMessage());
            }
        };
    }

    public static BiFunction<Context, Request, Response> authWithCode() {
        return (context, request) -> {
            try {
                String authHeaderRaw = request.header(AUTHORIZATION_HEADER).replace("Basic ", "");
                byte[] endcodedRaw = Base64.decode(authHeaderRaw.getBytes(), Base64.NO_WRAP);
                String encodedHeader = new String(endcodedRaw);
                String[] loginPass = encodedHeader.split(":");
                if (loginPass.length == 2
                        && (EMAIL_CODE_TO_EMAIL.equals(loginPass[0]) && CODE_BY_EMAIL.equals(loginPass[1])
                        || (EMAIL_CODE_TO_PHONE.equals(loginPass[0]) && CODE_BY_PHONE.equals(loginPass[1])))) {
                    final InputStream stream = context.getAssets().open("auth.json");
                    try {
                        return OkHttpResponse.success(request, stream);
                    } finally {
                        stream.close();
                    }

                } else {
                    return OkHttpResponse.error(request, 401, "Неверный код подтверждения");
                }
            } catch (IOException e) {
                return OkHttpResponse.error(request, 500, e.getMessage());
            }
        };
    }
}
