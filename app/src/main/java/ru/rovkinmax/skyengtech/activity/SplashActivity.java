package ru.rovkinmax.skyengtech.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import io.reactivex.Single;
import io.realm.Realm;
import ru.rovkinmax.skyengtech.R;
import ru.rovkinmax.skyengtech.model.User;
import ru.rovkinmax.skyengtech.presenter.SplashPresenter;
import ru.rovkinmax.skyengtech.repository.RealmConfigurationProvider;
import ru.rovkinmax.skyengtech.repository.RepositoryProvider;
import ru.rovkinmax.skyengtech.rx.AsyncTransformer;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.view.SplashView;

public class SplashActivity extends AppCompatActivity implements SplashView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_splash);
        final SplashPresenter presenter = new SplashPresenter(this, RxLifecycleProvider.get(this));

        provideInitLib()
                .compose(AsyncTransformer.async())
                .compose(RxLifecycleProvider.get(this).lifecycle())
                .subscribe(any -> presenter.checkAuth());
    }

    private Single<Object> provideInitLib() {
        return Single.create(emitter -> {
            RepositoryProvider.init(getApplicationContext());
            Realm.init(getApplicationContext());
            Realm.setDefaultConfiguration(RealmConfigurationProvider.provideRealmConfig(getApplicationContext()));
            emitter.onSuccess(new Object());
        });
    }

    @Override
    public void navigateToMain(@NonNull User user) {
        startActivity(MainActivity.makeIntent(this));
    }

    @Override
    public void navigateToLogin() {
        startActivity(LoginByPassActivity.makeIntent(this));
    }
}
