package ru.rovkinmax.skyengtech.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.rovkinmax.skyengtech.R;
import ru.rovkinmax.skyengtech.dialog.LoadingDialog;
import ru.rovkinmax.skyengtech.presenter.MainPresenter;
import ru.rovkinmax.skyengtech.rx.RxError;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.sync.SyncService;
import ru.rovkinmax.skyengtech.view.ErrorView;
import ru.rovkinmax.skyengtech.view.LoadingView;
import ru.rovkinmax.skyengtech.view.MainView;


public class MainActivity extends AppCompatActivity implements MainView {

    @BindView(R.id.btnLogout)
    protected Button btnLogout;

    private MainPresenter presenter;

    private LoadingView loadingView;

    private ErrorView errorView;

    public static Intent makeIntent(Context context) {
        return new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        ButterKnife.bind(this);

        loadingView = LoadingDialog.view(getFragmentManager());
        errorView = RxError.view(this);

        presenter = new MainPresenter(this, RxLifecycleProvider.get(this));
        btnLogout.setOnClickListener(view -> presenter.logout());
        SyncService.startSync(this);
    }

    @Override
    public void navigateToLogin() {
        Intent intent = LoginByPassActivity.makeIntent(this)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void showNetworkError() {
        errorView.showNetworkError();
    }

    @Override
    public void showUnexpectedError() {
        errorView.showUnexpectedError();
    }

    @Override
    public void showErrorMessage(@NonNull String message) {
        errorView.showErrorMessage(message);
    }

    @Override
    public void hideErrorMessage() {
        errorView.hideErrorMessage();
    }

    @Override
    public void showLoadingIndicator() {
        loadingView.showLoadingIndicator();
    }

    @Override
    public void hideLoadingIndicator() {
        loadingView.hideLoadingIndicator();
    }
}
