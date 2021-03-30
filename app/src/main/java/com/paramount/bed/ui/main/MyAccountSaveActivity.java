package com.paramount.bed.ui.main;

import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MattressHardnessSettingModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.EncryptUtils;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.StringUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static com.paramount.bed.ui.main.MyAccountEditActivity.isValidateAddress;

public class MyAccountSaveActivity extends BaseActivity {
    @BindView(R.id.textName)
    TextView textName;
    @BindView(R.id.textNameCaption)
    TextView textNameCaption;
    @BindView(R.id.textEmailCaption)
    TextView textEmailCaption;
    @BindView(R.id.textBirthday)
    TextView textBirthday;
    @BindView(R.id.textEmail)
    TextView textEmail;
    @BindView(R.id.textPhone)
    TextView textPhone;
    @BindView(R.id.textGender)
    TextView textGender;
    @BindView(R.id.textZip)
    TextView textZip;
    @BindView(R.id.textHeight)
    TextView textHeight;
    @BindView(R.id.textWeight)
    TextView textWeight;
    @BindView(R.id.textCompanyCode)
    TextView textCompanyCode;
    @BindView(R.id.btnSaveAccount)
    Button btnSaveAccount;
    @BindView(R.id.SNSIcon)
    ImageView SNSIcon;
    @BindView(R.id.mattressSettingContainer)
    LinearLayout mattressSettingContainer;
    @BindView(R.id.textHardness)
    TextView textHardness;
    private boolean istextNameLongPressed = false;
    private boolean istextEmailLongPressed = false;

    String iName, iBirthday, iEmail, iSNSToken, iPhoneNumber, iHeight, iWeight, iPassword, iAddress, finalAddress, iZIP;
    int iGender, iSNSProvider, mattressHardnessSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000720C001"));
        registerData();
        registerView();
        registerListener();
        isLoading = false;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_account_save;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    private void registerData() {
        iName = getIntent().getStringExtra(IntentUtil.User.NAME);
        iBirthday = getIntent().getStringExtra(IntentUtil.User.BIRTHDAY);
        iEmail = getIntent().getStringExtra(IntentUtil.User.EMAIL);
        iSNSProvider = getIntent().getIntExtra(IntentUtil.User.SNS_PROVIDER, 0);
        iSNSToken = getIntent().getStringExtra(IntentUtil.User.SNS_TOKEN);
        iPhoneNumber = getIntent().getStringExtra(IntentUtil.User.PHONE);
        iGender = getIntent().getIntExtra(IntentUtil.User.GENDER, 1);
        iHeight = getIntent().getStringExtra(IntentUtil.User.HEIGHT);
        iWeight = getIntent().getStringExtra(IntentUtil.User.WEIGHT);
        iPassword = getIntent().getStringExtra(IntentUtil.User.PASSWORD);
        iAddress = getIntent().getStringExtra(IntentUtil.User.ADDRESS);
        iZIP = getIntent().getStringExtra(IntentUtil.User.ZIP);
        mattressHardnessSetting = getIntent().getIntExtra(IntentUtil.User.MATTRESS_HARDNESS_ID,3);
    }

    private void registerView() {
        textName.setText(StringUtil.nickName(iName));
        textBirthday.setText(iBirthday);
        textEmail.setText(iEmail);
        textPhone.setText(iPhoneNumber);
        textGender.setText(getGenderLabel(iGender));

        SNSIcon.setImageResource(iSNSProvider == 1 ? R.drawable.icon_sns_facebook : iSNSProvider == 2 ? R.drawable.icon_sns_twitter : R.drawable.icon_sns_email);
        SNSIcon.setColorFilter(Color.rgb(15, 89, 136));

        istextNameLongPressed = false;
        istextEmailLongPressed = false;

        String strHeight = iHeight;
        textHeight.setText(strHeight.trim().length() == 0 || strHeight.trim().equals("0") ? "-" : strHeight + " cm");
        String strWeight = iWeight;
        textWeight.setText(strWeight.trim().length() == 0 || strWeight.trim().equals("0") ? "-" : strWeight + " kg");
        textCompanyCode.setText(UserLogin.getUserLogin().getCompanyCode());

        String zipBegin;
        String zipLast;
        if (!iZIP.equals("")) {
            zipBegin = iZIP.substring(0, 3);
            zipLast = iZIP.substring(3, 7);
            textZip.setText(zipBegin + "-" + zipLast);
        } else {
            textZip.setText("-");
        }

        LinearLayout linearCompanyCode = findViewById(R.id.wrap8);
        String iCompanyCode;
        try {
            iCompanyCode = UserLogin.getUserLogin().getCompanyCode();
        } catch (Exception e) {
            iCompanyCode = "";
        }
        if (iCompanyCode != null && iCompanyCode.trim().length() > 0) {
            linearCompanyCode.setVisibility(View.VISIBLE);
            textCompanyCode.setText(iCompanyCode);
        } else {
            linearCompanyCode.setVisibility(View.GONE);
        }
        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        if(nemuriScanModel != null){
            mattressSettingContainer.setVisibility(nemuriScanModel.isMattressExist() ? View.VISIBLE : GONE);
            MattressHardnessSettingModel userMattressSetting = FormPolicyModel.getPolicy().getMattressHardnessSettingById(mattressHardnessSetting);
            textHardness.setText(userMattressSetting.getValue());
        }
    }

    private void registerListener() {
        textName.setOnLongClickListener((view -> {
            textNameCaption.setVisibility(View.GONE);
            istextNameLongPressed = true;
            return true;
        }));
        textName.setOnTouchListener(((pView, pEvent) -> {
            pView.onTouchEvent(pEvent);
            if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                if (istextNameLongPressed) {
                    textNameCaption.setVisibility(View.VISIBLE);
                    istextNameLongPressed = false;
                }
            }
            return false;
        }));
        textEmail.setOnLongClickListener((view -> {
            textEmailCaption.setVisibility(View.GONE);
            istextEmailLongPressed = true;
            return true;
        }));

        textEmail.setOnTouchListener(((pView, pEvent) -> {
            pView.onTouchEvent(pEvent);
            if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                if (istextEmailLongPressed) {
                    textEmailCaption.setVisibility(View.VISIBLE);
                    istextEmailLongPressed = false;
                }
            }
            return false;
        }));
        btnSaveAccount.setOnClickListener((view -> updateUserAccount()));
    }

    private void updateUserAccount() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            DialogUtil.offlineDialog(this, getApplicationContext());
        } else {
            showProgress();
            finalAddress = "";
            if (textZip.getText().toString().replace("-", "")
                    .equals(UserLogin.getUserLogin().getZipCode())) {
                finalAddress = UserLogin.getUserLogin().getStreetAddress();
            } else if (isValidateAddress) {
                finalAddress = iAddress;
            }
            mDisposable =
                    userService.updateUser(String.valueOf(UserLogin.getUserLogin().getId()),
                            StringUtil.nickName(iName),
                            iBirthday,
                            iPassword,
                            textZip.getText().toString().replace("-", ""),
                            finalAddress,
                            iHeight,
                            iWeight,
                            iEmail,
                            iPhoneNumber,
                            iGender,
                            iSNSProvider,
                            iSNSToken,
                            mattressHardnessSetting,
                            1)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                                @Override
                                public void onSuccess(BaseResponse<String> response) {
                                    hideProgress();
                                    if (response.isSucces()) {
                                        UserLogin userLoginLog = new UserLogin();
                                        userLoginLog.setId(ApiClient.LogData.getLogUserId(MyAccountSaveActivity.this));
                                        userLoginLog.setCompanyId(ApiClient.LogData.getLogCompany(MyAccountSaveActivity.this));
                                        userLoginLog.setEmail(iEmail);
                                        userLoginLog.setNickname(StringUtil.nickName(iName));
                                        userLoginLog.setPhoneNumber(iPhoneNumber);
                                        ApiClient.LogData.setLogData(MyAccountSaveActivity.this, userLoginLog);

                                        UserLogin userLogin = new UserLogin();
                                        userLogin.setNickname(StringUtil.nickName(iName));
                                        userLogin.setPasswordOffline(EncryptUtils.encryptData(iPassword));

                                        //TODO:ChangePhoneNumber
                                        userLogin.setEmail(iEmail);
                                        userLogin.setPhoneNumber(iPhoneNumber);
                                        userLogin.setGender(iGender);
                                        userLogin.setSnsProvider(iSNSProvider);
                                        userLogin.setSnsToken(iSNSToken);

                                        userLogin.setBirthDate(iBirthday);
                                        userLogin.setPassword(iPassword);
                                        userLogin.setZipCode(iZIP);
                                        userLogin.setStreetAddress(finalAddress);
                                        String strHeight = iHeight.replace(" ", "");
                                        strHeight = strHeight.isEmpty() ? "0" : strHeight;
                                        userLogin.setHeight(Integer.valueOf(strHeight));
                                        String strWeight = iWeight.replace(" ", "");
                                        strWeight = strWeight.isEmpty() ? "0" : strWeight;
                                        userLogin.setWeight(Integer.valueOf(strWeight));
                                        userLogin.update();
                                        SettingModel.saveSetting("user_desired_hardness", String.valueOf(mattressHardnessSetting));
                                        finish();
                                        if (MyAccountEditActivity.activity != null)
                                            MyAccountEditActivity.activity.finish();
                                    } else {
                                        DialogUtil.createSimpleOkDialog(MyAccountSaveActivity.this, "", LanguageProvider.getLanguage(response.getMessage()));
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    hideProgress();
                                    if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                        DialogUtil.offlineDialog(MyAccountSaveActivity.this, getApplicationContext());
                                    } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                        DialogUtil.tokenExpireDialog(MyAccountSaveActivity.this);
                                    } else {
                                        DialogUtil.serverFailed(MyAccountSaveActivity.this, "UI000802C045", "UI000802C046", "UI000802C047", "UI000802C048");
                                    }
                                }
                            });
        }
    }

    private String getGenderLabel(int iGender) {
        String genderLabel;
        switch (iGender) {
            case 1:
                genderLabel = LanguageProvider.getLanguage("UI000720C013");
                break;
            case 2:
                genderLabel = LanguageProvider.getLanguage("UI000720C014");
                break;
            case 3:
                genderLabel = LanguageProvider.getLanguage("UI000720C015");
                break;
            default:
                genderLabel = "-";
                break;
        }
        return genderLabel;
    }
}