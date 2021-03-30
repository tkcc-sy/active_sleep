package com.paramount.bed.ui.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.paramount.bed.BedApplication;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.AnswerResult;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.UserRegistrationModel;
import com.paramount.bed.data.model.ValidationEmailModel;
import com.paramount.bed.data.model.ValidationPhoneModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.LoginResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.front.SliderActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.util.DataBaseUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.EncryptUtils;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.RxUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginEmailActivity extends BaseActivity {
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPassword)
    EditText etPassword;
    Disposable mDisposable;
    boolean statusFromLogin;

    @OnClick(R.id.btnBack)
    void back() {
        this.onBackPressed();
    }

    @OnClick(R.id.btnBackSlider)
    void backSlider() {
        Intent intent = new Intent(LoginEmailActivity.this, SliderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @OnClick(R.id.btnForgotID)
    void forgotID() {
        Intent intent = new Intent(LoginEmailActivity.this, ForgotIDInputActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        LoginEmailActivity.this.startActivity(intent);
    }

    @OnClick(R.id.btnLogin)
    void login() {
        doLogin(checkIfUserExist());
    }

    public boolean checkIfUserExist() {
        if (StatusLogin.getUserLogin() != null &&
                UserLogin.getUserLogin() != null &&
                UserLogin.getUserLogin().getEmail() != null &&
                !etEmail.getText().toString().equalsIgnoreCase(UserLogin.getUserLogin().getEmail())) {
            return true;
        }
        return false;
    }

    @OnClick(R.id.btnForgotPassword)
    void forgotPassword() {
        Intent intent = new Intent(LoginEmailActivity.this, ForgotPasswordInputActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        LoginEmailActivity.this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        statusFromLogin = false;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login_email;
    }

    @Override
    protected int getStatusBarTheme() {
        return this.STATUS_BAR_DARK;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("fromLogin", statusFromLogin);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtil.dispose(mDisposable);
    }

    private boolean validationEmail() {
        String email = etEmail.getText().toString().trim();
        Boolean iretun;
        String message;
        String title;
        if (email.isEmpty()) {
            message = LanguageProvider.getLanguage("UI000480C012");
            title = "";
            iretun = false;
            DialogUtil.createSimpleOkDialog(this, title, message);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            message = LanguageProvider.getLanguage("UI000480C008");
            title = "";
            iretun = false;
            DialogUtil.createSimpleOkDialog(this, title, message);
        } else {
            iretun = true;
        }
        return iretun;
    }

    private boolean validationPassword() {
        String password = etPassword.getText().toString().trim();
        Boolean iretun;
        String message;
        String title;
        if (password.isEmpty()) {
            message = LanguageProvider.getLanguage("UI000480C013");
            title = "";
            iretun = false;
            DialogUtil.createSimpleOkDialog(this, title, message);
        } else {
            iretun = true;
        }
        return iretun;
    }

    public void doLogin(boolean isClearing) {
        if (validationEmail() && validationPassword()) {
            showLoading();
            String loginType = "email";
            String loginEmail = etEmail.getText().toString();
            String loginPasssword = etPassword.getText().toString();
            String loginPhone = "";
            String loginNS = "";
            String loginToken = "";
            if (UserLogin.getUserLogin() != null && UserLogin.getUserLogin().getEmail() != null && loginEmail.equalsIgnoreCase(UserLogin.getUserLogin().getEmail())) {
                loginPhone = UserLogin.getUserLogin().getPhoneNumber();
                loginNS = UserLogin.getUserLogin().getScanSerialNumber();
                loginToken = UserLogin.getUserLogin().getSnsToken();
            }
            mDisposable =
                    userService.login(loginType, loginEmail, loginPasssword, loginToken, loginNS, loginPhone, 1)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(new DisposableSingleObserver<BaseResponse<LoginResponse>>() {
                                @Override
                                public void onSuccess(BaseResponse<LoginResponse> response) {
                                    hideLoading();
                                    if (response.isSucces()) {
                                        if (isClearing) {
                                            DialogUtil.createCustomYesNo(LoginEmailActivity.this, "",
                                                    LanguageProvider.getLanguage("UI000480C022"),
                                                    LanguageProvider.getLanguage("UI000480C023"),
                                                    (dialogs, a) -> {
                                                        LogUserAction.sendKickLog(userService, "kick_user_cancel", "UI000480");
                                                        dialogs.dismiss();
                                                    },
                                                    LanguageProvider.getLanguage("UI000480C024"),
                                                    (dialogs, a) -> {
                                                        LogUserAction.sendKickLog(userService, "kick_user_confirm", "UI000480");
                                                        dialogs.dismiss();
                                                        clearExistingUser();
                                                        new Handler().postDelayed(() -> proceedLogin(response, loginPasssword), 100);
                                                    });
                                        }else {
                                            proceedLogin(response, loginPasssword);
                                        }
                                    } else {
                                        String messageId = response.getMessage();
                                        String errorCode = response.getErrorCode();
                                        if (errorCode.equals("USR01-C009") || errorCode.equals("USR01-C014")) {
                                            //TODO:ChangeFlowLogin
                                            DialogUtil.createCustomYesNo(LoginEmailActivity.this, "",
                                                    LanguageProvider.getLanguage(messageId),
                                                    LanguageProvider.getLanguage("UI000480C018"),
                                                    (dialogInterface, i) -> dialogInterface.dismiss(),
                                                    LanguageProvider.getLanguage("UI000480C017"),
                                                    (dialogInterface, i) -> new Handler().postDelayed(() -> phoneNumberVerification(isClearing), 100));
                                        } else if (errorCode.equals("USR01-C006")) {
                                            String msg = LanguageProvider.getLanguage(messageId);
                                            String msgReplace = msg.replace("%TIMES_REMAINING%", " " + response.getData().getMessage_value() + " ")
                                                    .replace("%TIMES_ALLOWED%", " " + response.getData().getMessage_value2() + " ");
                                            DialogUtil.createSimpleOkDialog(LoginEmailActivity.this, "", msgReplace);
                                        } else {
                                            DialogUtil.createSimpleOkDialog(LoginEmailActivity.this, "", LanguageProvider.getLanguage(messageId));
                                        }
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    hideLoading();
                                    if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                        DialogUtil.offlineDialog(LoginEmailActivity.this, getApplicationContext());
                                    } else {
                                        DialogUtil.serverFailed(LoginEmailActivity.this, "UI000802C093", "UI000802C094", "UI000802C095", "UI000802C096");
                                    }
                                }
                            });

        }
    }

    private void phoneNumberVerification(boolean isClearing) {
        if (isClearing) {
            DialogUtil.createCustomYesNo(LoginEmailActivity.this, "",
                    LanguageProvider.getLanguage("UI000480C022"),
                    LanguageProvider.getLanguage("UI000480C023"),
                    (dialogs, a) -> {
                        LogUserAction.sendKickLog(userService, "kick_user_cancel", "UI000480");
                        dialogs.dismiss();
                    },
                    LanguageProvider.getLanguage("UI000480C024"),
                    (dialogs, a) -> {
                        LogUserAction.sendKickLog(userService, "kick_user_confirm", "UI000480");
                        dialogs.dismiss();
                        new Handler().postDelayed(() -> goChangePhone(isClearing), 100);
                    });
            return;
        }
        goChangePhone(isClearing);
    }

    public void goChangePhone(boolean isClearing) {
        Intent intent = new Intent(LoginEmailActivity.this, ChangePhoneInputActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(IntentUtil.User.EMAIL, etEmail.getText().toString());
        intent.putExtra(IntentUtil.User.PASSWORD, etPassword.getText().toString());
        intent.putExtra(IntentUtil.User.SNS_PROVIDER, 0);
        intent.putExtra(IntentUtil.User.IS_KICK_USER, isClearing);
        startActivity(intent);
    }

    public void clearExistingUser() {
        LogUserAction.sendKickLog(userService, "kick_user_execute", "UI000480");
        UserLogin.clear();
        UserRegistrationModel.clear();
        DataBaseUtil.wipeData(LoginEmailActivity.this,true);
    }

    private void proceedLogin(BaseResponse<LoginResponse> response, String loginPasssword){
        //clearing user data
        UserLogin.clear();
        ValidationPhoneModel.clear();
        ValidationEmailModel.clear();
        AnswerResult.clear();

        UserLogin userLogin = new UserLogin();
        userLogin.setId(response.getData().getId());
        userLogin.setPasswordOffline(EncryptUtils.encryptData(loginPasssword));
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
        BedApplication.token = response.getData().getApi_token();
        userLogin.setApiToken(response.getData().getApi_token());
        ApiClient.LogData.setLogData(LoginEmailActivity.this, userLogin);
        userLogin.insert();

        Intent intent = new Intent(LoginEmailActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
