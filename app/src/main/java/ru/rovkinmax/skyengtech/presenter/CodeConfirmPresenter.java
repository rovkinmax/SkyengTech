package ru.rovkinmax.skyengtech.presenter;

import android.os.CountDownTimer;
import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import ru.rovkinmax.skyengtech.model.User;
import ru.rovkinmax.skyengtech.repository.RepositoryProvider;
import ru.rovkinmax.skyengtech.rx.AsyncTransformer;
import ru.rovkinmax.skyengtech.rx.RxDecor;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.view.CodeConfirmView;

public final class CodeConfirmPresenter {
    private static final int CODE_LENGTH = 4;

    private static final long TIME_TO_RETRY = TimeUnit.SECONDS.toMillis(60);

    private static final long TIC_INTERVAL = TimeUnit.SECONDS.toMillis(1);

    private CountDownTimer timer = null;

    @NonNull
    private final CodeConfirmView view;

    @NonNull
    private final RxLifecycleProvider lifecycleProvider;

    public CodeConfirmPresenter(@NonNull CodeConfirmView view, @NonNull RxLifecycleProvider provider) {
        this.view = view;
        lifecycleProvider = provider;
    }

    public void sendCode(@NonNull String email, @NonNull String code) {
        RepositoryProvider.provideAuthRepo()
                .confirm(email, code)
                .compose(AsyncTransformer.async())
                .compose(lifecycleProvider.lifecycle())
                .compose(RxDecor.loading(view))
                .subscribe(this::dispatchUser, RxDecor.error(view));
    }

    private void dispatchUser(@NonNull User user) {
        stopTimer();
        view.navigateToMain(user);
    }

    public void validateCode(@NonNull String code) {
        view.setProceedEnable(code.length() == CODE_LENGTH);
    }

    public void startRetryTimer() {
        stopTimer();
        timer = new CountDownTimer(TIME_TO_RETRY, TIC_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                view.setSecondsToRetry(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                view.setEnableResend();
            }
        }.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void requestCode(@NonNull String email) {
        RepositoryProvider.provideAuthRepo()
                .requestCode(email)
                .compose(AsyncTransformer.async())
                .compose(lifecycleProvider.lifecycle())
                .compose(RxDecor.loading(view))
                .subscribe(token -> startRetryTimer(), RxDecor.error(view));
    }
}
