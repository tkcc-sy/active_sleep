package com.paramount.bed.ui.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paramount.bed.BedApplication;
import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.LoginResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.util.DataBaseUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.LogUserAction;
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

public class ChangePhonePinActivity extends BaseActivity {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.etOtp)
    EditText etOtp;

    @BindView(R.id.btnValidateOtp)
    Button btnValidateOtp;

    String iDataPassword, iDataEmail, iDataPhoneNumber, iDataPINValidity;
    int iDataSNSProvider;
    boolean iDataIsKickUser;
    Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000804C001"));
        applyIntentData(getIntent());
        applyView();
        applyListener();
        applyViewGuide();
    }

    private void applyIntentData(Intent intent) {
        iDataPhoneNumber = intent.getStringExtra(IntentUtil.User.PHONE);
        iDataPINValidity = intent.getStringExtra(IntentUtil.Validity.PIN_VALIDITY);
        iDataEmail = intent.getStringExtra(IntentUtil.User.EMAIL);
        iDataPassword = intent.getStringExtra(IntentUtil.User.PASSWORD);
        iDataSNSProvider = intent.getIntExtra(IntentUtil.User.SNS_PROVIDER, 0);
        iDataIsKickUser = intent.getBooleanExtra(IntentUtil.User.IS_KICK_USER, false);
    }
    public void applyViewGuide() {
        TextView tvGuide = findViewById(R.id.tvGuide);
        tvGuide.setText(LanguageProvider.getLanguage("UI000804C002").replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength())));
    }

    private void applyView() {
        int progressBarSegment = 2;
        int currentSegment = 2;
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");
        Drawable d = new ProgressDrawable(fgColor, bgColor, progressBarSegment);
        progressBar.setProgressDrawable(d);
        progressBar.setProgress(1000 * currentSegment / progressBarSegment);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_change_phone_pin;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @OnClick(R.id.btnBack)
    void back() {
        this.onBackPressed();
    }

    private void applyListener() {
        btnValidateOtp.setOnClickListener((view -> doValidateOTP()));
    }

    private void doValidateOTP() {
        //Initializing
        String vDataPIN = etOtp.getText().toString().trim();

        //Validating
        if (ValidationUtils.PIN.isBad(vDataPIN, new ValidationUtils.PIN.BadPINListener() {
            @Override
            public void onPINEmpty(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(ChangePhonePinActivity.this, "", LanguageProvider.getLanguage("UI000804C005")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength)));
            }

            @Override
            public void onPINLengthWrong(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(ChangePhonePinActivity.this, "", LanguageProvider.getLanguage("UI000804C006")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength)));
            }
        })) return;

        //Executing
        showLoading();
        mDisposable = userService.ChangePhoneValidateOTPV2(iDataPhoneNumber, iDataEmail, etOtp.getText().toString(), iDataPassword, iDataSNSProvider, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<LoginResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<LoginResponse> response) {
                        hideLoading();
                        if (response.isSucces()) {
                            updateAccountData(response);
                            DialogUtil.createSimpleOkDialog(ChangePhonePinActivity.this, "", LanguageProvider.getLanguage("UI000804C009"), LanguageProvider.getLanguage("UI000804C010"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sendIntentData();
                                    dialogInterface.dismiss();
                                }
                            });
                            return;
                        }
                        DialogUtil.createSimpleOkDialog(ChangePhonePinActivity.this, "", LanguageProvider.getLanguage(response.getMessage())
                                .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength())));
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            DialogUtil.offlineDialog(ChangePhonePinActivity.this, getApplicationContext());
                            return;
                        }
                        DialogUtil.serverFailed(ChangePhonePinActivity.this, "UI000802C105", "UI000802C106", "UI000802C107", "UI000802C108");
                    }
                });
    }

    private void updateAccountData(BaseResponse<LoginResponse> response) {
        //Get Offline password before Clear
        String lastpasswordoffline = "";

        if (iDataIsKickUser) {
            LogUserAction.sendKickLog(userService, "kick_user_execute", "UI000804");
            DataBaseUtil.wipeData(ChangePhonePinActivity.this,true);
        }
        UserLogin.clear();
        UserLogin userLogin = new UserLogin();
        userLogin.setId(response.getData().getId());
        userLogin.setPasswordOffline(lastpasswordoffline);
        userLogin.setGroupId(response.getData().getGroup_id());
        userLogin.setGroupName(response.getData().getGroup_name());
        userLogin.setEmail(response.getData().getEmail());
        userLogin.setNickname(response.getData().getNickname());
        userLogin.setPassword(response.getData().getPassword());
        userLogin.setZipCode(response.getData().getZip_code());
        userLogin.setPrefecture(response.getData().getPrefecture());
        userLogin.setCity(response.getData().getCity());
        userLogin.setStreetAddress(response.getData().getStreet_address());
        userLogin.setBirthDate(response.getData().getBirth_date());
        userLogin.setGender(response.getData().getGender());
        userLogin.setPhoneNumber(response.getData().getPhone_number());
        userLogin.setSleepQuestionnaireId(response.getData().getSleep_questionnaire_id());
        userLogin.setOptionalQuestionnaireId(response.getData().getOptional_questionnaire_id());
        userLogin.setUserType(response.getData().getUser_type());
        userLogin.setUserActiveFrom(response.getData().getUser_active_from());
        userLogin.setUserActiveTo(response.getData().getUser_active_to());
        userLogin.setBlocked(response.getData().is_blocked());
        userLogin.setPasswordAttempt(response.getData().getPassword_attempt());
        userLogin.setCreatedDate(response.getData().getCreated_date());
        userLogin.setLastActivityDate(response.getData().getLast_activity_date());
        userLogin.setPhoneActivated(response.getData().isPhone_activated());
        userLogin.setSnsToken(response.getData().getSns_token());
        userLogin.setSnsProvider(response.getData().getSns_provider());
        userLogin.setCompanyId(response.getData().getCompany_id());
        userLogin.setHeight(response.getData().getHeight());
        userLogin.setWeight(response.getData().getWeight());
        userLogin.setRecommendationQuestionnaireId(response.getData().getRecommendation_questionnaire_id());
        userLogin.setScanSerialNumber(response.getData().getNs_serial_number());
        userLogin.setCompanyCode(response.getData().getCompany_code());
        userLogin.setLogin(true);
        userLogin.setApiToken(response.getData().getApi_token());
        BedApplication.token = response.getData().getApi_token();
        ApiClient.LogData.setLogData(ChangePhonePinActivity.this, userLogin);
        userLogin.insert();
    }

    public void sendIntentData() {
        Intent intent = new Intent(ChangePhonePinActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
