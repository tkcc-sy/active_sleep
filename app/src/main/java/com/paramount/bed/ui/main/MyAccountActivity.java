package com.paramount.bed.ui.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.AnswerResult;
import com.paramount.bed.data.model.ForceLogoutModel;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MattressHardnessSettingModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.RegisterStep;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.TutorialShowModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.ValidationEmailModel;
import com.paramount.bed.data.model.ValidationPhoneModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.front.SliderActivity;
import com.paramount.bed.ui.login.LoginEmailActivity;
import com.paramount.bed.util.DataBaseUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.StringUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;

public class MyAccountActivity extends BaseActivity {
    @BindView(R.id.textName)
    TextView textName;

    @BindView(R.id.textEmail)
    TextView textEmail;

    @BindView(R.id.textPhone)
    TextView textPhone;

    @BindView(R.id.textNameCaption)
    TextView textNameCaption;

    @BindView(R.id.textEmailCaption)
    TextView textEmailCaption;

    @BindView(R.id.textBirthday)
    TextView textBirthday;

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

    @BindView(R.id.SNSIcon)
    ImageView SNSIcon;

    @BindView(R.id.mattressSettingContainer)
    LinearLayout mattressSettingContainer;

    @BindView(R.id.textHardness)
    TextView textHardness;

    private Boolean editClicked = false;
    private boolean istextNameLongPressed = false;
    private boolean istextEmailLongPressed = false;
    public boolean statusFromLogin = false;

    @OnClick(R.id.btnBackBottom)
    void backBottom() {
        DialogUtil.createCustomYesNo(this, "",
                LanguageProvider.getLanguage("UI000700C013"),
                LanguageProvider.getLanguage("UI000700C015"),
                (dialogInterface, i) -> dialogInterface.dismiss(),
                LanguageProvider.getLanguage("UI000700C014"),
                (dialogInterface, i) -> clearData());
    }

    @OnClick(R.id.btnEdit)
    void edit() {
        if (!editClicked) {
            editClicked = true;
            Intent i = new Intent(this, MyAccountEditActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }

    }

    @OnClick(R.id.btnBack)
    void back() {
        finish();
    }

    @OnClick(R.id.btnLogout)
    void logout() {
        if (BuildConfig.DEMO_MODE) {
            Intent intent = new Intent(this, LoginEmailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("fromLogin", statusFromLogin);
            startActivity(intent);
            finish();

        } else {
            DialogUtil.createCustomYesNo(this, "",
                    LanguageProvider.getLanguage("UI000700C016"),
                    LanguageProvider.getLanguage("UI000700C018"),
                    (dialogInterface, i) -> dialogInterface.dismiss(),
                    LanguageProvider.getLanguage("UI000700C017"),
                    (dialogInterface, i) -> logoutFunction());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000700C001"));
        registerView();
        registerListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        editClicked = false;
        registerView();
        AlarmsQuizModule.run(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (HomeActivity.drawerLayout != null)
            HomeActivity.drawerLayout.closeDrawer(GravityCompat.START, false);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_account;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    private void clearData() {
        showLoading();
        mDisposable =
                userService.deleteUser(UserLogin.getUserLogin().getId(), 1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                            @Override
                            public void onSuccess(BaseResponse<String> response) {
                                hideLoading();
                                if (response.isSucces()) {
                                    //Wipe Data
                                    UserLogin.clear();
                                    UserLogin.init();
                                    StatusLogin.clear();
                                    DataBaseUtil.wipeData(MyAccountActivity.this,true);
                                    ApiClient.LogData.clearLogData(MyAccountActivity.this);
                                    ApiClient.LogData.setLoginStatus(MyAccountActivity.this, 0);
                                    Intent intent = new Intent(MyAccountActivity.this, SliderActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                    dismissNotification();
                                } else {
                                    DialogUtil.createSimpleOkDialog(MyAccountActivity.this, "", LanguageProvider.getLanguage(response.getMessage()));
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoading();
                                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                    DialogUtil.offlineDialog(MyAccountActivity.this, getApplicationContext());
                                } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                    UserLogin.clear();
                                    ValidationPhoneModel.clear();
                                    ValidationEmailModel.clear();
                                    AnswerResult.clear();
                                    UserLogin.init();
                                    StatusLogin.clear();
                                    TutorialShowModel.clear();
                                    ForceLogoutModel.clear();
                                    RegisterStep.clear();
                                    ApiClient.LogData.clearLogData(MyAccountActivity.this);
                                    ApiClient.LogData.setLoginStatus(MyAccountActivity.this, 0);
                                    Intent intent = new Intent(MyAccountActivity.this, SliderActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                    dismissNotification();
                                } else {
                                    DialogUtil.serverFailed(MyAccountActivity.this, "UI000802C033", "UI000802C034", "UI000802C035", "UI000802C036");
                                }
                                Log.d("abx", e.getMessage());
                            }
                        });

    }

    public void registerView() {
        textName.setText(StringUtil.nickName(UserLogin.getUserLogin().getNickname()));
        textPhone.setText(UserLogin.getUserLogin().getPhoneNumber());
        istextNameLongPressed = false;
        textEmail.setText(UserLogin.getUserLogin().getEmail());
        int iSNSProvider = UserLogin.getUserLogin().getSnsProvider();
        SNSIcon.setImageResource(iSNSProvider == 1 ? R.drawable.icon_sns_facebook : iSNSProvider == 2 ? R.drawable.icon_sns_twitter : R.drawable.icon_sns_email);
        SNSIcon.setColorFilter(Color.rgb(15, 89, 136));

        istextEmailLongPressed = false;
        String userBirhday = UserLogin.getUserLogin().getBirthDate() == null ? "" : UserLogin.getUserLogin().getBirthDate();
        textBirthday.setText(userBirhday.isEmpty() ? "-" : userBirhday.trim().replace("-", "/"));
        String gender = "";
        switch (UserLogin.getUserLogin().getGender()) {
            case 1:
                gender = LanguageProvider.getLanguage("UI000440C006");
                break;
            case 2:
                gender = LanguageProvider.getLanguage("UI000440C007");
                break;
            case 3:
                gender = LanguageProvider.getLanguage("UI000440C008");
                break;
            default:
                gender = "-";
                break;
        }
        textGender.setText(gender);
        String zipBegin = "";
        String zipLast = "";
        if (!UserLogin.getUserLogin().getZipCode().equals("")) {
            zipBegin = UserLogin.getUserLogin().getZipCode().replace("-", "").substring(0, 3);
            zipLast = UserLogin.getUserLogin().getZipCode().replace("-", "").substring(3, 7);
            textZip.setText(zipBegin + "-" + zipLast);
        } else {
            textZip.setText("-");
        }
        if (UserLogin.getUserLogin().getHeight() == 0) {
            textHeight.setText("-");
        } else {
            textHeight.setText(" " + UserLogin.getUserLogin().getHeight() + " cm");
        }

        if (UserLogin.getUserLogin().getWeight() == 0) {
            textWeight.setText("-");
        } else {
            textWeight.setText(" " + UserLogin.getUserLogin().getWeight() + " kg");
        }

        textCompanyCode.setText(UserLogin.getUserLogin().getCompanyCode());
        if (UserLogin.getUserLogin().getCompanyCode() == null || UserLogin.getUserLogin().getCompanyCode().equals("")) {
            LinearLayout linearCompanyCode = findViewById(R.id.wrap8);
            linearCompanyCode.setVisibility(GONE);
        }

        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        if(nemuriScanModel != null){
            mattressSettingContainer.setVisibility(nemuriScanModel.isMattressExist() ? View.VISIBLE : GONE);
            SettingModel settingModel = SettingModel.getSetting();
            MattressHardnessSettingModel userMattressSetting = FormPolicyModel.getPolicy().getMattressHardnessSettingById(settingModel.user_desired_hardness);
            textHardness.setText(userMattressSetting.getValue());
        }

    }

    public void registerListener() {
        textName.setOnLongClickListener((pView -> {
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
        textEmail.setOnLongClickListener((pView -> {
            // Do something when your hold starts here.
            textEmailCaption.setVisibility(View.GONE);
            istextEmailLongPressed = true;
            return true;
        }));
        textEmail.setOnTouchListener(((pView, pEvent) -> {
            pView.onTouchEvent(pEvent);
            // We're only interested in when the button is released.
            if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                // We're only interested in anything if our speak button is currently pressed.
                if (istextEmailLongPressed) {
                    // Do something when the button is released.
                    textEmailCaption.setVisibility(View.VISIBLE);
                    istextEmailLongPressed = false;
                }
            }
            return false;
        }));
    }

    private void logoutFunction() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // Call your Activity where you want to land after log out
            }
        }.execute();

        DataBaseUtil.wipeUserData(MyAccountActivity.this);
        UserLogin.logout();
        Intent intent = new Intent(MyAccountActivity.this, LoginEmailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("fromLogin", statusFromLogin);

        StatusLogin.clear();
        StatusLogin statusLogin = new StatusLogin();
        statusLogin.statusLogin = false;
        statusLogin.insert();
        ApiClient.LogData.clearLogData(MyAccountActivity.this);
        ApiClient.LogData.setLoginStatus(MyAccountActivity.this, 2);
        startActivity(intent);
        finish();

        dismissNotification();
    }

    public void dismissNotification() {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
