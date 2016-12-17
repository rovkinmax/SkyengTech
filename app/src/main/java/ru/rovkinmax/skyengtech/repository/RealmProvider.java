package ru.rovkinmax.skyengtech.repository;

import android.support.annotation.NonNull;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmObject;

/**
 * @author Rovkin Max
 */
public final class RealmProvider {
    private RealmProvider() {
    }

    @NonNull
    public static Realm provideRealm() {
        return Realm.getDefaultInstance();
    }

    public static <T extends RealmModel> Single<T> queryFromRealm(Function<Realm, T> function) {
        return Single.defer(() -> {
            Realm realm = provideRealm();
            try {
                realm.beginTransaction();
                T result = function.apply(realm);
                T cloned = null;
                if (RealmObject.isValid(result)) {
                    cloned = realm.copyFromRealm(result);
                }

                realm.commitTransaction();
                if (cloned != null) {
                    return Single.just(cloned);
                }
            } catch (Exception e) {
                realm.cancelTransaction();
            } finally {
                realm.close();
            }
            return Single.error(new Exception("Something went wrong"));
        });
    }

    public static void closableTransaction(Consumer<Realm> consumer) throws Exception {
        Realm realm = provideRealm();
        try {
            realm.beginTransaction();
            consumer.accept(realm);
            realm.commitTransaction();
        } catch (Exception e) {
            realm.cancelTransaction();
            throw e;
        } finally {
            realm.close();
        }
    }
}
