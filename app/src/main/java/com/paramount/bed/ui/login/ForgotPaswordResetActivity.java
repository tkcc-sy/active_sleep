package com.paramount.bed.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.paramount.bed.R;
import com.paramount.bed.data.model.PasswordPolicyModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.PasswordPolicyProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.AutoSizeTextUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IntentConstant;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ProgressDrawable;
import com.paramount.bed.util.ValidationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ForgotPaswordResetActivity extends BaseActivity {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    @BindView(R.id.btnSave)
    Button btnSave;

    private PasswordPolicyProvider passwordPolicyProvider;
    public String iDataPasswordResetToken, companyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        passwordPolicyProvider = new PasswordPolicyProvider(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000493C001"));
        applyIntentData(getIntent());
        applyUI();
        applyUIListener();

        companyCode = UserLogin.getUserLogin() != null && UserLogin.getUserLogin().getCompanyCode() != null ? UserLogin.getUserLogin().getCompanyCode() : "";
        showLoading();
        passwordPolicyProvider.getPasswordPolicy(companyCode, (passwordPolicyModel, cc) -> {
            runOnUiThread(() -> {
                hideLoading();
                applyUIHint(passwordPolicyModel);
            });
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_forgot_pasword_reset;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    public void applyIntentData(Intent intent) {
        iDataPasswordResetToken = intent.getStringExtra(IntentConstant.PASSWORD_RESET_TOKEN);
    }

    public void applyUI() {
        int progressBarSegment = 3;
        int currentSegment = 3;
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");
        Drawable d = new ProgressDrawable(fgColor, bgColor, progressBarSegment);
        progressBar.setProgressDrawable(d);
        progressBar.setProgress(1000 * currentSegment / progressBarSegment);
        applyUIHint(PasswordPolicyModel.getFirst());
    }

    public void applyUIListener() {
        btnSave.setOnClickListener((v) -> doResetPassword());
    }

    public void applyUIHint(PasswordPolicyModel passwordPolicyModel) {
        String strReplace = LanguageProvider.getLanguage("UI000493C013").replace("%MIN_LEN%", passwordPolicyModel.minLength)
                .replace("%MAX_LEN%", passwordPolicyModel.maxLength)
                .replace("%ALLOWED_SYMBOLS%", passwordPolicyModel.allowedSymbols);
        etPassword.setHint(strReplace);
        String strReplaceConfirm = LanguageProvider.getLanguage("UI000493C014").replace("%MIN_LEN%", passwordPolicyModel.minLength)
                .replace("%MAX_LEN%", passwordPolicyModel.maxLength)
                .replace("%ALLOWED_SYMBOLS%", passwordPolicyModel.allowedSymbols);
        etConfirmPassword.setHint(strReplaceConfirm);
        AutoSizeTextUtil.setAutoSizeHint(etPassword);
        AutoSizeTextUtil.setAutoSizeHint(etConfirmPassword);
    }

    public void doResetPassword() {
        //Initializing
        String vDataPassword = etPassword.getText().toString();
        String vDataConfirmPassword = etConfirmPassword.getText().toString();

        //Validating
        if (isBadPassword(vDataPassword, vDataConfirmPassword)) return;

        //Executing
        nextOnlinePasswordCheck();
    }

    public boolean isBadPassword(String password, String confirmPassword) {
        return ValidationUtils.PASSWORD.isBad(password, confirmPassword, new ValidationUtils.PASSWORD.BadPasswordListener() {
            @Override
            public void onPasswordEmpty() {
                DialogUtil.createSimpleOkDialog(ForgotPaswordResetActivity.this, "", LanguageProvider.getLanguage("UI000493C006"));
            }

            @Override
            public void onPasswordNotMatch() {
                DialogUtil.createSimpleOkDialog(ForgotPaswordResetActivity.this, "", LanguageProvider.getLanguage("UI000493C010"));
            }
        });
    }

    public void nextOnlinePasswordCheck() {
        showLoading();
        String passwordType = etPassword.getText().toString();
        passwordPolicyProvider.checkPasswordPolicy(companyCode, passwordType, (isResponse, passwordPolicyModel, baseResponse, e) -> {
            runOnUiThread(() -> {
                hideLoading();
                applyUIHint(passwordPolicyModel);
                if (!isResponse) {
                    if (!NetworkUtil.isNetworkConnected(ForgotPaswordResetActivity.this)) {
                        DialogUtil.offlineDialog(ForgotPaswordResetActivity.this, ForgotPaswordResetActivity.this);
                        return;
                    }
                    if (MultipleDeviceUtil.isTokenExpired(e)) {
                        DialogUtil.tokenExpireDialog(ForgotPaswordResetActivity.this);
                        return;
                    }
                    DialogUtil.serverFailed(ForgotPaswordResetActivity.this, "UI000802C101", "UI000802C102", "UI000802C103", "UI000802C104");
                    return;
                }

                if (!baseResponse.isSucces()) {
                    String Lang = LanguageProvider.getLanguage(baseResponse.getMessage());
                    String strReplaceDialogue = Lang.replace("%MIN_LEN%", passwordPolicyModel.minLength)
                            .replace("%MAX_LEN%", passwordPolicyModel.maxLength)
                            .replace("%ALLOWED_SYMBOLS%", passwordPolicyModel.allowedSymbols);
                    DialogUtil.createSimpleOkDialog(ForgotPaswordResetActivity.this, "", strReplaceDialogue);
                    return;
                }
                nextOnlineResetPassword();
            });
        });
    }

    public void nextOnlineResetPassword() {
        showLoading();
        String password = etPassword.getText().toString();
        mDisposable = userService.passwordReset(password, iDataPasswordResetToken, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        hideLoading();
                        if (response.isSucces()) {
                            sendIntentData();
                            return;
                        }
                        DialogUtil.createSimpleOkDialog(ForgotPaswordResetActivity.this, "", LanguageProvider.getLanguage(response.getMessage()),
                                LanguageProvider.getLanguage("UI000802C003"), (dialogInterface, which) -> {
                                    dialogInterface.dismiss();
                                    if(response.getMessage().equalsIgnoreCase("USR13-C003")){
                                        setResult(1);
                                        finish();
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            DialogUtil.offlineDialog(ForgotPaswordResetActivity.this, getApplicationContext());
                            return;
                        }
                        DialogUtil.serverFailed(ForgotPaswordResetActivity.this, "UI000802C101", "UI000802C102", "UI000802C103", "UI000802C104");
                    }
                });
    }

    public void sendIntentData() {
        Intent intent = new Intent(this, LoginEmailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

