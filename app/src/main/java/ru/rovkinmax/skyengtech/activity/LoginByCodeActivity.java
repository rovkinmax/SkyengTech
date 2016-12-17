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

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.rovkinmax.skyengtech.R;
import ru.rovkinmax.skyengtech.dialog.LoadingDialog;
import ru.rovkinmax.skyengtech.presenter.LoginByCodePresenter;
import ru.rovkinmax.skyengtech.rx.RxError;
import ru.rovkinmax.skyengtech.rx.RxLifecycleProvider;
import ru.rovkinmax.skyengtech.rx.bindings.RxTextView;
import ru.rovkinmax.skyengtech.utils.ViewUtil;
import ru.rovkinmax.skyengtech.view.ErrorView;
import ru.rovkinmax.skyengtech.view.LoadingView;
import ru.rovkinmax.skyengtech.view.LoginByCodeView;


public final class LoginByCodeActivity extends AppCompatActivity implements LoginByCodeView {

    @BindView(R.id.etEmail)
    protected EditText etEmail;

    @BindView(R.id.btnGetCode)
    protected Button btnGetCode;

    @BindView(R.id.btnBack)
    protected Button btnBack;

    private LoginByCodePresenter presenter;

    private LoadingView loadingView;

    private ErrorView errorView;

    public static Intent makeIntent(@NonNull Context context) {
        return new Intent(context, LoginByCodeActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_login_by_code);
        ButterKnife.bind(this);
        loadingView = LoadingDialog.view(getFragmentManager());
        errorView = RxError.view(this);

        setSupportActionBar(ButterKnife.findById(this, R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        btnBack.setOnClickListener(view -> onBackPressed());

        presenter = new LoginByCodePresenter(this, RxLifecycleProvider.get(this));
        RxTextView.textChanges(etEmail)
                .map(CharSequence::toString)
                .subscribe(email -> presenter.validateEmail(email));

        btnGetCode.setOnClickListener(view -> presenter.requestCode(etEmail.getText().toString()));
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
    public void setProceedEnable(boolean enable) {
        ViewUtil.updateViewEnabled(btnGetCode, enable);
    }

    @Override
    public void navigateToConfirmWithEmail() {
        startActivity(CodeConfirmActivity.makeIntentWithEmail(this, etEmail.getText().toString()));
    }

    @Override
    public void navigateToConfirmWithPhone(@NonNull String phone) {
        startActivity(CodeConfirmActivity.makeIntentWithPhone(this, etEmail.getText().toString(), phone));
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
