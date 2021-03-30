package com.paramount.bed.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IntentConstant;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ProgressDrawable;
import com.paramount.bed.util.ValidationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ForgotPasswordPinActivity extends BaseActivity {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.etOtp)
    EditText etOtp;

    private String phoneNumber;
    private String validUntil;
    Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000492C001"));

        if (getIntent() != null) {
            phoneNumber = getIntent().getStringExtra(IntentConstant.PHONE_NUMBER);
            validUntil = getIntent().getStringExtra(IntentConstant.OTP_VALIDITY);
        }

        int progressBarSegment = 3;
        int currentSegment = 2;
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");
        Drawable d = new ProgressDrawable(fgColor, bgColor, progressBarSegment);
        progressBar.setProgressDrawable(d);
        progressBar.setProgress(1000 * currentSegment / progressBarSegment);
        applyView();
    }

    public void applyView() {
        TextView tvGuide = findViewById(R.id.tvGuide);
        tvGuide.setText(LanguageProvider.getLanguage("UI000492C002").replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength())));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_forgot_password_pin;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @OnClick(R.id.btnBack)
    void back() {
        this.onBackPressed();
    }

    @OnClick(R.id.btnValidateOtp)
    void onBtnValidateOTPClicked() {
        String vDataPIN = etOtp.getText().toString();
        if (isBadPIN(vDataPIN)) return;

        showLoading();
        mDisposable = userService.passwordValidateOTP(phoneNumber, vDataPIN, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        hideLoading();
                        if (response.isSucces()) {
                            sendIntentData(response.getData());
                            return;
                        }
                        DialogUtil.createSimpleOkDialog(ForgotPasswordPinActivity.this, "", LanguageProvider.getLanguage(response.getMessage())
                                .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength())));
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            DialogUtil.offlineDialog(ForgotPasswordPinActivity.this, getApplicationContext());
                            return;
                        }
                        DialogUtil.serverFailed(ForgotPasswordPinActivity.this, "UI000802C089", "UI000802C090", "UI000802C091", "UI000802C092");
                    }
                });
    }

    public boolean isBadPIN(String pin) {
        return ValidationUtils.PIN.isBad(pin, new ValidationUtils.PIN.BadPINListener() {
            @Override
            public void onPINEmpty(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotPasswordPinActivity.this, "", LanguageProvider.getLanguage("UI000492C006")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength))
                );
            }

            @Override
            public void onPINLengthWrong(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotPasswordPinActivity.this, "", LanguageProvider.getLanguage("UI000492C006")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength))
                );
            }
        });
    }

    private void sendIntentData(String passwordResetToken) {
        Intent intent = new Intent(this, ForgotPaswordResetActivity.class);
        intent.putExtra(IntentConstant.PASSWORD_RESET_TOKEN, passwordResetToken);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent,102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 102 && resultCode == 1){
            //token expired,back immidiately
            finish();
        }
    }
}