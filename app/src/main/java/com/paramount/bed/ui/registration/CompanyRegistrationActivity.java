package com.paramount.bed.ui.registration;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.UserRegistrationModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.ValidateCompanyResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ProgressDrawable;
import com.paramount.bed.util.ValidationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CompanyRegistrationActivity extends BaseActivity {
    private boolean nextEnabled = false;
    private int progressBarStep = 1;

    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.tvGuideBottom)
    TextView tvGuideBottom;

    @OnClick(R.id.btnBack)
    void goBack() {
        this.onBackPressed();
    }

    @BindView(R.id.etCompanyCode)
    EditText etCompanyCode;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @OnClick(R.id.btnNext)
    void goNext() {
        String companyCode = etCompanyCode.getText().toString();
        if (isBadCompany(companyCode)) return;
        showLoading();
        mDisposable = userService.validateCompany(companyCode, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<ValidateCompanyResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<ValidateCompanyResponse> validateResponse) {
                        hideLoading();
                        if (validateResponse.isSucces()) {
                            Intent welcomeIntent = new Intent(CompanyRegistrationActivity.this, TncActivity.class);
                            welcomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            String companyCode = etCompanyCode.getText().toString();
                            welcomeIntent.putExtra("companyCode", companyCode);
                            welcomeIntent.putExtra("companyName", validateResponse.getData().getCompany_name());
                            welcomeIntent.putExtra("companyId", validateResponse.getData().getId());
                            welcomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            RegistrationStepActivity.COMPANY_NAME = validateResponse.getData().getCompany_name();
                            RegistrationStepActivity.COMPANY_ID = validateResponse.getData().getId();
                            welcomeIntent.putExtra(IntentUtil.User.IS_KICK_USER, getIntent().getBooleanExtra(IntentUtil.User.IS_KICK_USER, false));
                            CompanyRegistrationActivity.this.startActivity(welcomeIntent);
                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                            UserRegistrationModel.setCompanyCode(etCompanyCode.getText().toString());
                        } else {
                            DialogUtil.createSimpleOkDialog(CompanyRegistrationActivity.this, "", LanguageProvider.getLanguage(validateResponse.getMessage())
                                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getCompanyCodeLength())));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            DialogUtil.offlineDialog(CompanyRegistrationActivity.this, getApplicationContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(CompanyRegistrationActivity.this);
                        } else {
                            DialogUtil.serverFailed(CompanyRegistrationActivity.this, "UI000802C109", "UI000802C110", "UI000802C111", "UI000802C112");
                        }
                    }
                });
    }

    public boolean isBadCompany(String companyCode) {
        return ValidationUtils.COMPANY.isBad(companyCode, new ValidationUtils.COMPANY.BadCompanyListener() {
            @Override
            public void onCompanyEmpty(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(CompanyRegistrationActivity.this, "", LanguageProvider.getLanguage("UI000250C010")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength)));
            }

            @Override
            public void onCompanyLengthWrong(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(CompanyRegistrationActivity.this, "", LanguageProvider.getLanguage("UI000250C008")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength)));
            }

            @Override
            public void onCompanyCharsWrong(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(CompanyRegistrationActivity.this, "", LanguageProvider.getLanguage("UI000250C010")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength)));
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        RegistrationStepActivity.PROGRESSBAR_SEGMENT_SUM = 15;
        setToolbarTitle(LanguageProvider.getLanguage("UI000250C001"));
        etCompanyCode.setLongClickable(false);
        etCompanyCode.setText(UserRegistrationModel.getCompanyCode());
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");
        if (StatusLogin.getUserLogin() == null) {
            RegistrationStepActivity.PROGRESSBAR_SEGMENT_SUM = 15;
        }
        Drawable d = new ProgressDrawable(fgColor, bgColor, RegistrationStepActivity.PROGRESSBAR_SEGMENT_SUM);
        progressBar.setProgressDrawable(d);
        progressBar.setProgress(100 * progressBarStep / RegistrationStepActivity.PROGRESSBAR_SEGMENT_SUM);

        applyLocalization();
        tvGuideBottom.setText(LanguageProvider.getLanguage("UI000250C005").replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getCompanyCodeLength())));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_company_registration;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!isLoading && !IOSDialogRight.getDialogVisibility()) {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
