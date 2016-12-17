package ru.rovkinmax.skyengtech;

import android.app.Application;

import timber.log.Timber;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Lifecycler.register(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
