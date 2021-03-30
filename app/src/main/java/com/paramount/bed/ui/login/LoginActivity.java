package com.paramount.bed.ui.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.paramount.bed.BedApplication;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.AnswerResult;
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
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.registration.TncActivity;
import com.paramount.bed.util.DataBaseUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.EncryptUtils;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.RxUtil;
import com.paramount.bed.util.ServerUtil;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;

import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity {
    @BindView(R.id.btnBack)
    ImageView btnBack;

    @OnClick(R.id.btnEmail)
    void loginWithEmail() {
        Intent intent = new Intent(LoginActivity.this, LoginEmailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        LoginActivity.this.startActivity(intent);
    }

    @OnClick(R.id.btnBack)
    void back() {
        finish();
    }

    @BindView(R.id.facebookLoginButton)
    LoginButton facebookLoginButton;
    @BindView(R.id.twitterLoginButton)
    TwitterLoginButton twitterLoginButton;
    CallbackManager callbackManager;

    @OnClick(R.id.btnGoogle)
    void loginWithGoogle() {
        if (NetworkUtil.isNetworkConnected(this)) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            DialogUtil.offlineDialog(LoginActivity.this, getApplicationContext());
        }
    }


    @OnClick(R.id.btnFacebook)
    void loginWithFacebook() {
        if (NetworkUtil.isNetworkConnected(this)) {
            if (BuildConfig.DEMO_MODE) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                LoginActivity.this.startActivity(intent);
                return;
            }
            deleteAccessToken(this);
            deleteTwitterSession(this);
            facebookLoginButton.performClick();
        } else {
            DialogUtil.offlineDialog(LoginActivity.this, getApplicationContext());
        }
    }

    @OnClick(R.id.btnTwitter)
    void loginWithTwitter() {
        if (NetworkUtil.isNetworkConnected(this)) {
            if (BuildConfig.DEMO_MODE) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                LoginActivity.this.startActivity(intent);
                return;
            }
            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

            deleteAccessToken(this);
            deleteTwitterSession(this);
            twitterLoginButton.performClick();
        } else {
            DialogUtil.offlineDialog(LoginActivity.this, getApplicationContext());
        }
    }

    public boolean checkIfUserExist(String email) {
        if (StatusLogin.getUserLogin() != null && UserLogin.getUserLogin() != null && UserLogin.getUserLogin().getEmail() != null && !email.equalsIgnoreCase(UserLogin.getUserLogin().getEmail())) {
            return true;
        }
        return false;
    }

    private void loginSocial(String type, String email, String accessToken) {
        doLogin(checkIfUserExist(email), type, email, accessToken);
    }
    private void proceedLogin(BaseResponse<LoginResponse> response){
        //clear existing user
        UserLogin.clear();
        ValidationPhoneModel.clear();
        ValidationEmailModel.clear();
        AnswerResult.clear();

        setSession(response);
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);

    }
    private void doLogin(boolean isClearing, String type, String email, String accessToken) {
        showLoading();
        String loginType = type;
        String loginEmail = email;
        String loginPasssword = "";
        String loginPhone = "";
        String loginNS = "";
        String loginToken = accessToken;
        if (UserLogin.getUserLogin() != null && UserLogin.getUserLogin().getEmail() != null && loginEmail.equalsIgnoreCase(UserLogin.getUserLogin().getEmail())) {
            loginPhone = UserLogin.getUserLogin().getPhoneNumber();
            loginNS = UserLogin.getUserLogin().getScanSerialNumber();
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
                                        DialogUtil.createCustomYesNo(LoginActivity.this, "",
                                                LanguageProvider.getLanguage("UI000470C010"),
                                                LanguageProvider.getLanguage("UI000470C011"),
                                                (dialogs, a) -> {
                                                    LogUserAction.sendKickLog(userService, "kick_user_cancel", "UI000470");
                                                    dialogs.dismiss();
                                                },
                                                LanguageProvider.getLanguage("UI000470C012"),
                                                (dialogs, a) -> {
                                                    LogUserAction.sendKickLog(userService, "kick_user_confirm", "UI000470");
                                                    dialogs.dismiss();
                                                    clearExistingUser();
                                                    new Handler().postDelayed(() -> proceedLogin(response), 100);
                                                });

                                    }else{
                                        proceedLogin(response);
                                    }
                                } else {
                                    String messageId = response.getMessage();
                                    String errorCode = response.getErrorCode();
                                    if (errorCode.equals("USR01-C009") || errorCode.equals("USR01-C014")) {
                                        //TODO:ChangeFlowLogin
                                        LoginResponse loginResponse = response.getData();
                                        DialogUtil.createCustomYesNo(LoginActivity.this, "",
                                                LanguageProvider.getLanguage(messageId),
                                                LanguageProvider.getLanguage("UI000470C009"),
                                                (dialogInterface, i) -> dialogInterface.dismiss(),
                                                LanguageProvider.getLanguage("UI000470C008"),
                                                (dialogInterface, i) -> new Handler().postDelayed(() -> phoneNumberVerification(isClearing, email, loginResponse.getSns_token(), type.equals("facebook") ? 1 : type.equals("twitter") ? 2 : 0), 100));
                                    }else if(errorCode.equals("USR01-C005") ) {
                                        DialogUtil.createSimpleOkDialog(LoginActivity.this, "", LanguageProvider.getLanguage(messageId), LanguageProvider.getLanguage("UI000802C003"), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                goToRegister(isClearing);
                                            }
                                        });
                                    } else {
                                        DialogUtil.createSimpleOkDialog(LoginActivity.this, "", LanguageProvider.getLanguage(messageId));
                                    }

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoading();
                                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                    DialogUtil.offlineDialog(LoginActivity.this, getApplicationContext());
                                } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                    DialogUtil.tokenExpireDialog(LoginActivity.this);
                                } else {
                                    DialogUtil.serverFailed(LoginActivity.this, "UI000802C137", "UI000802C138", "UI000802C139", "UI000802C140");
                                }
                            }
                        });
    }

    public static int RC_SIGN_IN = 506;

    GoogleSignInClient mGoogleSignInClient;

    private static final String EMAIL = "email";
    private int count = 0;
    private long startMillis = 0;

    public void onFiveClick() {
        //get system current milliseconds
        long time = System.currentTimeMillis();


        //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything
        if (startMillis == 0 || (time - startMillis > 3000)) {
            startMillis = time;
            count = 1;
        }
        //it is not the first, and it has been  less than 3 seconds since the first
        else { //  time-startMillis< 3000
            count++;
        }

        if (count == 5) {
            Intent a = new Intent(LoginActivity.this, ServerUtil.class);
            a.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(a);
        }

    }

    public boolean statusFromLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        setToolbarTitle(LanguageProvider.getLanguage("UI000470C001"));
        TextView textView10 = findViewById(R.id.textView10);
        textView10.setOnTouchListener(
                new View.OnTouchListener() {
                    private boolean moved;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (BaseActivity.IS_DEVELOPMENT) {
                            onFiveClick();
                        }
                        return false;
                    }
                }
        );
        Intent intentFromLogout = getIntent();
        statusFromLogin = intentFromLogout.getBooleanExtra("fromLogin", true);

        if (StatusLogin.getUserLogin() != null) {
            btnBack.setVisibility(View.INVISIBLE);
        } else {
            btnBack.setVisibility(View.VISIBLE);
        }
        if (getIntent().getBooleanExtra("isFromSlider", false)) {
            btnBack.setVisibility(View.VISIBLE);
        }

        TwitterConfig config = new TwitterConfig.Builder(getApplicationContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestServerAuthCode(getString(R.string.client_id))
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        callbackManager = CallbackManager.Factory.create();

        facebookLoginButton.setPermissions(Collections.singletonList(EMAIL));

        // Callback registration
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                getFacebookUser(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                DialogUtil.createSimpleOkDialog(LoginActivity.this,"",LanguageProvider.getLanguage("UI000470C019"));
            }
        });

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                getTwitterUser(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });
    }

    public void getTwitterUser(TwitterSession session) {
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                String email = result.data;
                if (email == null || email.isEmpty()) {
                    DialogUtil.createSimpleOkDialog(LoginActivity.this, "", LanguageProvider.getLanguage("USR01-C008"));
                } else {
                    loginSocial("twitter", result.data, session.getAuthToken().token);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.d("abx", exception.getCause().toString());
            }
        });

    }


    public void getFacebookUser(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken, ((object, response) -> {
                    try {
                        String email = object.getString("email");
                        if (email == null || email.isEmpty()) {
                            DialogUtil.createSimpleOkDialog(LoginActivity.this, "", LanguageProvider.getLanguage("USR01-C008"));
                        } else {
                            loginSocial("facebook", email, accessToken.getToken().toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        DialogUtil.createSimpleOkDialog(LoginActivity.this, "", LanguageProvider.getLanguage("USR01-C008"));
                    }
                }));
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
//        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (UserLogin.getUserLogin() != null && UserLogin.getUserLogin().getEmail() != null && !account.getEmail().equals(UserLogin.getUserLogin().getEmail())) {
                DialogUtil.createSimpleOkDialog(LoginActivity.this, "", LanguageProvider.getLanguage("USR01-C008"));
            } else {
                loginSocial("google", account.getEmail(), account.getId());;
            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            //updateUI(null);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @Override
    public void onBackPressed() {
        // If user has login, cant back to slider activity (Iphone case)
        // If user want to go to slider, he need to delete account in MyAccountActivity (Iphone case)
        // Remove comment
        if (getIntent().getBooleanExtra("isFromSlider", false)) {
            super.onBackPressed();
            return;
        }
        if (StatusLogin.getUserLogin() != null) {
            if (!StatusLogin.getUserLogin().statusLogin) {
                this.finishAffinity();
                finishAndRemoveTask();
            } else {
                this.finishAffinity();
                finishAndRemoveTask();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtil.dispose(mDisposable);
    }

    @Override
    public void onPause() {
        super.onPause();
//        btnBack.setVisibility(View.VISIBLE);
    }

    public void setSession(BaseResponse<LoginResponse> response) {
        UserLogin.clear();
        UserLogin userLogin = new UserLogin();
        userLogin.setId(response.getData().getId());
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
        ApiClient.LogData.setLogData(LoginActivity.this, userLogin);
        userLogin.insert();
    }

    public static void deleteTwitterSession(Context context) {
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
            TwitterCore.getInstance().getSessionManager().clearActiveSession();
        } catch (Exception e) {

        }
    }

    public static void deleteAccessToken(Context context) {
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeSessionCookie();
            LoginManager.getInstance().logOut();
        } catch (Exception e) {

        }
    }

    private void phoneNumberVerification(boolean isClearing, String email, String password, int SNSProvider) {
        if (isClearing) {
            DialogUtil.createCustomYesNo(LoginActivity.this, "",
                    LanguageProvider.getLanguage("UI000470C010"),
                    LanguageProvider.getLanguage("UI000470C011"),
                    (dialogs, a) -> {
                        LogUserAction.sendKickLog(userService, "kick_user_cancel", "UI000470");
                        dialogs.dismiss();
                    },
                    LanguageProvider.getLanguage("UI000470C012"),
                    (dialogs, a) -> {
                        LogUserAction.sendKickLog(userService, "kick_user_confirm", "UI000470");
                        dialogs.dismiss();
                        new Handler().postDelayed(() -> goChangePhone(email, password, SNSProvider, isClearing), 100);
                    });
            return;
        }
        goChangePhone(email, password, SNSProvider, isClearing);
    }

    public void goChangePhone(String email, String password, int SNSProvider, boolean isClearing) {
        Intent intent = new Intent(LoginActivity.this, ChangePhoneInputActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(IntentUtil.User.EMAIL, email);
        intent.putExtra(IntentUtil.User.PASSWORD, password);
        intent.putExtra(IntentUtil.User.SNS_PROVIDER, SNSProvider);
        intent.putExtra(IntentUtil.User.IS_KICK_USER, isClearing);
        startActivity(intent);
    }

    public void goToRegister(boolean isClearing) {
        Intent registrationIntent = new Intent(this, TncActivity.class);
        registrationIntent.putExtra("companyId", 0);
        registrationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        registrationIntent.putExtra(IntentUtil.User.IS_KICK_USER, isClearing);
        startActivity(registrationIntent);
    }

    public void clearExistingUser() {
        LogUserAction.sendKickLog(userService, "kick_user_execute", "UI000470");
        UserLogin.clear();
        UserRegistrationModel.clear();
        DataBaseUtil.wipeData(LoginActivity.this,true);
    }
}
