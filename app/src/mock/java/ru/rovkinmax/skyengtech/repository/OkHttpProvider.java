package ru.rovkinmax.skyengtech.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.BiFunction;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import ru.rovkinmax.skyengtech.BuildConfig;
import ru.rovkinmax.skyengtech.model.AuthFactory;
import timber.log.Timber;


public final class OkHttpProvider {

    private static final Random RANDOM = new SecureRandom();

    private static final Map<String, BiFunction<Context, Request, Response>> RESPONSES = new ConcurrentHashMap<>();

    static {
        RESPONSES.put("/auth", AuthFactory.authWithPassword());
        RESPONSES.put("/code", AuthFactory.requestCode());
        RESPONSES.put("/confirm", AuthFactory.authWithCode());
    }

    private OkHttpProvider() {
        //Not implemented
    }

    @NonNull
    public static OkHttpClient provideClient(@NonNull Context context) {
        return new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(chain -> {
                    final Request request = chain.request();
                    try {
                        final String encodedPath = request.url().encodedPath();

                        final BiFunction<Context, Request, Response> func = RESPONSES.get(encodedPath);
                        if (func != null) {
                            TimeUnit.MILLISECONDS.sleep(500 + RANDOM.nextInt(1000));
                            return func.apply(context, request);
                        }
                        return chain.proceed(chain.request()); // proxy to real method
                    } catch (Exception e) {
                        return OkHttpResponse.error(request, 500, e.getMessage());
                    }
                })
                .build();
    }

    @NonNull
    public static OkHttpClient withToken(@NonNull Context context, String authToken) {
        if (BuildConfig.DEBUG) {
            Timber.tag(OkHttpProvider.class.getSimpleName())
                    .d("User auth token: %s", authToken);
        }
        return provideClient(context)
                .newBuilder()
                .addInterceptor(chain -> {
                    //тут можно реализовать обновление токена по refreshToken если этот протухнет
                    return chain.proceed(chain.request());
                })
                .addInterceptor(chain -> {
                    final Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer" + authToken)
                            .build();
                    return chain.proceed(request);
                }).build();
    }
}
