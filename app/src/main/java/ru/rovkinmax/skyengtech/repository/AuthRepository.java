package ru.rovkinmax.skyengtech.repository;

import android.support.annotation.NonNull;
import android.util.Base64;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import ru.rovkinmax.skyengtech.model.RequestCodeResponse;
import ru.rovkinmax.skyengtech.model.TokenResponse;
import ru.rovkinmax.skyengtech.model.User;

public final class AuthRepository {
    private static final String TOKEN_BASIC = "Basic";

    @NonNull
    private final AuthApi api;

    AuthRepository(@NonNull AuthApi api) {
        this.api = api;
    }

    public Single<User> auth(@NonNull String login, @NonNull String password) {
        String token = Base64.encodeToString(String.format("%s:%s", login, password).getBytes(), Base64.NO_WRAP);
        return api.authByPass(String.format("%s %s", TOKEN_BASIC, token))
                .flatMap(authToken -> RepositoryProvider.provideUserRepo().saveUser(buildUser(login, authToken)));
    }

    public Single<User> confirm(@NonNull String email, @NonNull String code) {
        String token = Base64.encodeToString(String.format("%s:%s", email, code).getBytes(), Base64.NO_WRAP);
        return api.confirmCode(String.format("%s %s", TOKEN_BASIC, token))
                .flatMap(authToken -> RepositoryProvider.provideUserRepo().saveUser(buildUser(email, authToken)));
    }

    @NonNull
    private User buildUser(@NonNull String login, TokenResponse authToken) {
        User user = new User();
        user.setLogin(login);
        user.setToken(authToken.getToken());
        user.setRefreshToken(authToken.getRefreshToke());
        return user;
    }

    public Single<RequestCodeResponse> requestCode(@NonNull String email) {
        return api.requestCode(email);
    }


    interface AuthApi {
        @POST("auth")
        Single<TokenResponse> authByPass(@Header("Authorization") String authHeader);

        @GET("code")
        Single<RequestCodeResponse> requestCode(@Header("email") String email);

        @POST("confirm")
        Single<TokenResponse> confirmCode(@Header("Authorization") String authHeader);
    }
}
