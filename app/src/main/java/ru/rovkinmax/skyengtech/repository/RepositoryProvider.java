package ru.rovkinmax.skyengtech.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.rovkinmax.skyengtech.BuildConfig;

public final class RepositoryProvider {

    private static Retrofit retrofit;

    private static Retrofit authRetrofit;

    private static UserRepository userRepository;

    private static AuthRepository authRepository;

    public static void init(@NonNull Context context) {
        authRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(OkHttpProvider.provideClient(context))
                .build();
    }

    public static void initWithToken(@NonNull Context context, @NonNull String token, @NonNull String refreshToken) {
        authRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(OkHttpProvider.withToken(context, token))
                .build();
    }

    public static AuthRepository provideAuthRepo() {
        if (authRepository == null) {
            authRepository = new AuthRepository(authRetrofit.create(AuthRepository.AuthApi.class));
        }
        return authRepository;
    }

    public static UserRepository provideUserRepo() {
        if (userRepository == null) {
            userRepository = new UserRepository();
        }
        return userRepository;
    }
}
