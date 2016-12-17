package ru.rovkinmax.skyengtech.model;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class User implements RealmModel {
    @PrimaryKey
    private String login;

    private String token;

    private String refreshToken;

    public String getLogin() {
        return login;
    }

    public User setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getToken() {
        return token;
    }

    public User setToken(String token) {
        this.token = token;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public User setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }
}
