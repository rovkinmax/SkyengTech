package ru.rovkinmax.skyengtech.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.rovkinmax.skyengtech.R;
import ru.rovkinmax.skyengtech.dialog.LoadingDialog;
import ru.rovkinmax.skyengtech.model.User;
import ru.rovkinmax.skyengtech.presenter.CodeConfirmPresenter;
import ru.rovkinmax.skyengtech.repository.RepositoryProvider;
import ru.rovkinmax.skyengtech.rx.RxError;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.rx.bindings.RxTextView;
import ru.rovkinmax.skyengtech.utils.ViewUtil;
import ru.rovkinmax.skyengtech.view.CodeConfirmView;
import ru.rovkinmax.skyengtech.view.ErrorView;
import ru.rovkinmax.skyengtech.view.LoadingView;


public final class CodeConfirmActivity extends AppCompatActivity implements CodeConfirmView {
    private static final String KEY_PHONE = "PHONE";

    private static final String KEY_EMAIL = "EMAIL";

    @BindView(R.id.btnLogin)
    protected Button btnLogin;

    @BindView(R.id.btnResend)
    protected Button btnResend;

    @BindView(R.id.etCode)
    protected EditText etCode;

    @BindView(R.id.tvHeaderHint)
    protected TextView tvHeaderHint;

    private CodeConfirmPresenter presenter;

    private LoadingView loadingView;

    private ErrorView errorView;

    public static Intent makeIntentWithPhone(@NonNull Context context, @NonNull String email, @NonNull String phone) {
        return makeIntentWithEmail(context, email)
                .putExtra(KEY_PHONE, phone);
    }

    public static Intent makeIntentWithEmail(@NonNull Context context, @NonNull String email) {
        return new Intent(context, CodeConfirmActivity.class)
                .putExtra(KEY_EMAIL, email);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_confirm);
        ButterKnife.bind(this);

        loadingView = LoadingDialog.view(getFragmentManager());
        errorView = RxError.view(this);

        setSupportActionBar(ButterKnife.findById(this, R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String email = intent.getStringExtra(KEY_EMAIL);
        if (intent.hasExtra(KEY_PHONE)) {
            tvHeaderHint.setText(getString(R.string.confirm_hint_header_phone, intent.getStringExtra(KEY_PHONE)));
        } else {
            tvHeaderHint.setText(getString(R.string.confirm_hint_header_email, email));
        }

        presenter = new CodeConfirmPresenter(this, RxLifecycleProvider.get(this));

        btnLogin.setOnClickListener(view -> presenter.sendCode(email, etCode.getText().toString()));
        RxTextView.textChanges(etCode)
                .map(CharSequence::toString)
                .subscribe(code -> presenter.validateCode(code));
        presenter.startRetryTimer();
        btnResend.setOnClickListener(view -> presenter.requestCode(email));
    }

    @Override
    public void setProceedEnable(boolean enable) {
        ViewUtil.updateViewEnabled(btnLogin, enable);
    }

    @Override
    public void setSecondsToRetry(long seconds) {
        ViewUtil.updateViewEnabled(btnResend, false);
        btnResend.setText(getString(R.string.confirm_button_resend_time, seconds));
    }

    @Override
    public void setEnableResend() {
        ViewUtil.updateViewEnabled(btnResend, true);
        btnResend.setText(getString(R.string.confirm_button_resend));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void navigateToMain(@NonNull User user) {
        RepositoryProvider.initWithToken(getApplicationContext(), user.getToken(), user.getRefreshToken());
        startActivity(MainActivity.makeIntent(this));
    }
}