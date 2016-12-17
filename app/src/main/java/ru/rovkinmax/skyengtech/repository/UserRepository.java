package ru.rovkinmax.skyengtech.repository;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import ru.rovkinmax.skyengtech.model.User;

public class UserRepository {
    public Single<User> getUser() {
        return RealmProvider.queryFromRealm(realm -> realm.where(User.class).findFirst());
    }

    public Single<User> saveUser(@NonNull User user) {
        return RealmProvider.queryFromRealm(realm -> realm.copyToRealmOrUpdate(user));
    }

    public Single<User> deleteUser() {
        return getUser().flatMap(user -> {
            RealmProvider.closableTransaction(realm -> realm.delete(User.class));
            return Single.just(user);
        });
    }
}
