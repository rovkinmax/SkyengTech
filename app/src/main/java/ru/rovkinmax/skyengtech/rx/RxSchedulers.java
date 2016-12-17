package ru.rovkinmax.skyengtech.rx;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public final class RxSchedulers {

    private RxSchedulers() {
    }

    @NonNull
    public static Scheduler io() {
        return Schedulers.io();
    }

    @NonNull
    public static Scheduler main() {
        return AndroidSchedulers.mainThread();
    }

}
