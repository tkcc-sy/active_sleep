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

public class ForgotIDPinActivity extends BaseActivity {
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
        setToolbarTitle(LanguageProvider.getLanguage("UI000497C001"));

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
        tvGuide.setText(LanguageProvider.getLanguage("UI000497C002").replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength())));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_forgot_id_pin;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @OnClick(R.id.btnValidateOtp)
    void onBtnValidateOTPClicked() {
        String vDataPIN = etOtp.getText().toString();
        if (isBadPIN(vDataPIN)) return;

        showLoading();
        mDisposable = userService.idValidateOTP(phoneNumber, vDataPIN, 1)
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
                        DialogUtil.createSimpleOkDialog(ForgotIDPinActivity.this, "", LanguageProvider.getLanguage(response.getMessage())
                                .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength())));
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            DialogUtil.offlineDialog(ForgotIDPinActivity.this, getApplicationContext());
                            return;
                        }
                        DialogUtil.serverFailed(ForgotIDPinActivity.this, "UI000802C097", "UI000802C098", "UI000802C099", "UI000802C100");
                    }
                });
    }

    public boolean isBadPIN(String pin) {
        return ValidationUtils.PIN.isBad(pin, new ValidationUtils.PIN.BadPINListener() {
            @Override
            public void onPINEmpty(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotIDPinActivity.this, "", LanguageProvider.getLanguage("UI000497C006")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength))
                );
            }

            @Override
            public void onPINLengthWrong(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotIDPinActivity.this, "", LanguageProvider.getLanguage("UI000497C006")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength))
                );
            }
        });
    }

    private void sendIntentData(String email) {
        Intent intent = new Intent(this, ForgotIDResetActivity.class);
        intent.putExtra(IntentConstant.EMAIL, email);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}