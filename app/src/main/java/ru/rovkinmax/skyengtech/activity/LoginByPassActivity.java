package ru.rovkinmax.skyengtech.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import ru.rovkinmax.skyengtech.R;
import ru.rovkinmax.skyengtech.dialog.LoadingDialog;
import ru.rovkinmax.skyengtech.model.User;
import ru.rovkinmax.skyengtech.presenter.LoginByPassPresenter;
import ru.rovkinmax.skyengtech.rx.RxError;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.rx.bindings.RxTextView;
import ru.rovkinmax.skyengtech.utils.ViewUtil;
import ru.rovkinmax.skyengtech.view.ErrorView;
import ru.rovkinmax.skyengtech.view.LoadingView;
import ru.rovkinmax.skyengtech.view.LoginByPassView;


public final class LoginByPassActivity extends AppCompatActivity implements LoginByPassView {

    private LoginByPassPresenter presenter;

    @BindView(R.id.etEmail)
    protected EditText etEmail;

    @BindView(R.id.etPass)
    protected EditText etPass;

    @BindView(R.id.btnLogin)
    protected Button btnLogin;

    @BindView(R.id.btnByCode)
    protected Button btnByCode;

    private LoadingView loadingView;

    private ErrorView errorView;

    public static Intent makeIntent(@NonNull Context context) {
        return new Intent(context, LoginByPassActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_login_by_pass);
        ButterKnife.bind(this);
        setSupportActionBar(ButterKnife.findById(this, R.id.toolbar));

        loadingView = LoadingDialog.view(getFragmentManager());
        errorView = RxError.view(this);

        presenter = new LoginByPassPresenter(this, RxLifecycleProvider.get(this));

        Observable<String> emailObservable = RxTextView.textChanges(etEmail).map(CharSequence::toString);
        Observable<String> passObservable = RxTextView.textChanges(etPass).map(CharSequence::toString);
        Observable.combineLatest(emailObservable, passObservable,
                (email, pass) -> {
                    presenter.validateCredentials(email, pass);
                    return new Object();
                })
                .subscribe();
        btnLogin.setOnClickListener(view -> presenter.login(etEmail.getText().toString(), etPass.getText().toString()));
        btnByCode.setOnClickListener(view -> startActivity(LoginByCodeActivity.makeIntent(LoginByPassActivity.this)));
    }

    @Override
    public void setEnableProceed(boolean enable) {
        ViewUtil.updateViewEnabled(btnLogin, enable);
    }

    @Override
    public void navigateToMain(@NonNull User user) {
        startActivity(MainActivity.makeIntent(this));
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
