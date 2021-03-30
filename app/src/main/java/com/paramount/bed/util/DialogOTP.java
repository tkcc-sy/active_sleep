package com.paramount.bed.util;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.VerificationProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BaseCompatibilityScreenActivity;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogOTP extends BaseCompatibilityScreenActivity implements VerificationProvider.PhoneUpdateValidateOTPListener {
    private VerificationProvider verificationProvider;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.etPIN)
    EditText etPIN;
    @BindView(R.id.btnOk)
    TextView btnOk;
    @BindView(R.id.btnCancel)
    TextView btnCancel;

    public Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_otp);
        ButterKnife.bind(this);
        verificationProvider = new VerificationProvider(this);
        String phoneNumber = getIntent().getStringExtra(IntentUtil.User.PHONE);
        btnOk.setOnClickListener((view) -> {
            String iPin = etPIN.getText().toString();
            if (isBadPIN(iPin)) return;
            btnOk.setEnabled(false);
            showProgress();
            verificationProvider.phoneUpdateValidateOTP(phoneNumber, iPin, DialogOTP.this);
        });
        setupView();
        btnCancel.setOnClickListener((view -> finish()));
    }

    private void setupView() {
        tvTitle.setText(LanguageProvider.getLanguage("UI000710C065"));
        etPIN.setHint(LanguageProvider.getLanguage("UI000710C066"));
        btnOk.setText(LanguageProvider.getLanguage("UI000710C067"));
        btnCancel.setText(LanguageProvider.getLanguage("UI000710C068"));
    }

    @Override
    public void onphoneUpdateValidateOTPSuccess(BaseResponse response) {
        runOnUiThread(() -> {
            hideProgress();
            if (response.isSucces()) {
                if (response.getMessage().equals("USR33-C000")) {
                    DialogUtil.createSimpleOkDialog(DialogOTP.this, "", LanguageProvider.getLanguage(response.getMessage()),
                            LanguageProvider.getLanguage("UI000710C085"), ((dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                setResult(1);
                                finish();
                            }));
                } else {
                    DialogUtil.createSimpleOkDialog(DialogOTP.this, "", LanguageProvider.getLanguage(response.getMessage())
                                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength()))
                            , LanguageProvider.getLanguage("UI000710C085"), ((dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                setResult(0);
                            }));
                }
            } else {
                DialogUtil.createSimpleOkDialog(DialogOTP.this, "", LanguageProvider.getLanguage(response.getMessage())
                                .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength()))
                        , LanguageProvider.getLanguage("UI000710C085"), ((dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            setResult(0);
                        }));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onPhoneUpdateValidateError(Throwable e) {
        runOnUiThread(() -> {
            setResult(0);
            hideProgress();
            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                DialogUtil.offlineDialog(DialogOTP.this, getApplicationContext());
            } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                DialogUtil.tokenExpireDialog(DialogOTP.this);
            }
        });
    }

    public void showProgress() {
        if (!DialogOTP.this.isFinishing()) {
            if (progressDialog == null || !progressDialog.isShowing()) {
                btnOk.setEnabled(false);
                btnCancel.setEnabled(false);
                progressDialog = new Dialog(DialogOTP.this);
                LayoutInflater inflater = LayoutInflater.from(DialogOTP.this);
                View iview = inflater.inflate(R.layout.ios_dialog_suv, null);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.setContentView(iview);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }
    }

    public void hideProgress() {
        if (!DialogOTP.this.isFinishing()) {
            if (progressDialog != null && progressDialog.isShowing()) {
                btnOk.setEnabled(true);
                btnCancel.setEnabled(true);
                progressDialog.dismiss();
            }
        }
    }

    private boolean isBadPIN(String pin) {
        return ValidationUtils.PIN.isBad(pin, new ValidationUtils.PIN.BadPINListener() {
            @Override
            public void onPINEmpty(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(DialogOTP.this, "", LanguageProvider.getLanguage("UI000710C082")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength))
                );
            }

            @Override
            public void onPINLengthWrong(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(DialogOTP.this, "", LanguageProvider.getLanguage("UI000710C083")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength))
                );
            }
        });
    }
}
