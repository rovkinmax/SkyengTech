package ru.rovkinmax.skyengtech;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;


public class Lifecycler implements Application.ActivityLifecycleCallbacks {

    public static void register(@NonNull Application app) {
        app.registerActivityLifecycleCallbacks(new Lifecycler());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        RxLifecycleProvider.attach(activity.getFragmentManager());
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

}
