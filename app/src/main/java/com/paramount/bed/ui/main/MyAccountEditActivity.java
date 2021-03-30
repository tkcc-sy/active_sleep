package com.paramount.bed.ui.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MattressHardnessSettingModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.PasswordPolicyModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.ValidationEmailModel;
import com.paramount.bed.data.provider.FormPolicyProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.PasswordPolicyProvider;
import com.paramount.bed.data.provider.VerificationProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.ZipResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.datepicker.DateWheelBuilder;
import com.paramount.bed.ui.datepicker.DateWheelPicker;
import com.paramount.bed.ui.datepicker.OnDateWheelSelectListener;
import com.paramount.bed.util.AutoSizeTextUtil;
import com.paramount.bed.util.DialogOTP;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.StringUtil;
import com.paramount.bed.util.ValidationUtils;
import com.paramount.bed.util.ViewUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;
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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;
import static com.paramount.bed.util.LogUtil.Logx;
import static com.paramount.bed.util.NetworkUtil.isNetworkConnected;

public class MyAccountEditActivity extends BaseActivity implements VerificationProvider.PhoneUpdateReqOTPListener, VerificationProvider.EmailValidationListener, VerificationProvider.EmailActivationListener {

    @BindView(R.id.facebookLoginButton)
    LoginButton facebookLoginButton;
    @BindView(R.id.twitterLoginButton)
    TwitterLoginButton twitterLoginButton;
    CallbackManager callbackManager;
    String SNSToken;

    @BindView(R.id.tbEmail)
    ToggleButton tbEmail;

    @BindView(R.id.tbFacebook)
    ToggleButton tbFacebook;

    @BindView(R.id.tbTwitter)
    ToggleButton tbTwitter;

    @BindView(R.id.rgGender)
    RadioGroup rgGender;

    @BindView(R.id.etName)
    EditText etName;

    @BindView(R.id.etEmail)
    EditText etEmail;

    @BindView(R.id.etPassword)
    EditText etPassword;

    @BindView(R.id.etConfirmPassword)
    EditText etConfirmPassword;

    @BindView(R.id.etZip)
    EditText etZip;

    @BindView(R.id.etPhoneConfirm)
    EditText etPhoneConfirm;

    @BindView(R.id.btnPhoneConfirm)
    Button btnPhoneConfirm;

    @BindView(R.id.etAddress)
    TextView etAddress;

    @BindView(R.id.captionFacebook)
    TextView captionFacebook;

    @BindView(R.id.captionTwitter)
    TextView captionTwitter;

    @BindView(R.id.btnZipReq)
    Button btnZipReq;


    @BindView(R.id.etBirthday)
    EditText etBirthday;

    @BindView(R.id.etWeight)
    EditText etWeight;

    @BindView(R.id.etHeight)
    EditText etHeight;

    @BindView(R.id.etWeightClick)
    LinearLayout etWeightClick;

    @BindView(R.id.etHeightClick)
    LinearLayout etHeightClick;

    @BindView(R.id.etHardness)
    EditText etHardness;

    @BindView(R.id.etHardnessClick)
    LinearLayout etHardnessClick;

    @BindView(R.id.mattressSettingContainer)
    LinearLayout mattressSettingContainer;

    @BindView(R.id.btnNext)
    Button btnNext;
    public static Boolean isValidateAddress = true;

    @OnClick(R.id.btnNext)
    void next() {
        nextToPreview();
    }

    @OnClick(R.id.btnZipReq)
    void reqZip() {
        if (zipValidation()) {
            reqZipDialog();
        }
    }

    public static Activity activity;
    private VerificationProvider verificationProvider;
    private PasswordPolicyProvider passwordPolicyProvider;
    DateWheelPicker birthdayPicker;
    List<String> weightOptions;
    List<String> heightOptions;
    List<MattressHardnessSettingModel> hardnesOptions;
    OptionsPickerView weightPicker;
    OptionsPickerView heightPicker;
    OptionsPickerView hardnessPicker;
    String valName, valEmail, valPhone, valBirthDay, valPassword, valConfirmPassword, valZip, valHeight, valWeight, valAddress;
    MattressHardnessSettingModel selectedHardness = FormPolicyProvider.getDefaultMattressHardnessSetting();
    public Boolean ZipChecked;
    public boolean isValidPhone = true;
    public boolean isValidEmail = true;
    public String dataSNSToken;
    public String dataSNSEmail;
    public String companyCode;

    TextWatcher emailWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            isValidEmail = false;
            tbEmail.setChecked(true);
            tbFacebook.setChecked(false);
            tbTwitter.setChecked(false);
            if (etEmail.getText().toString().equals(UserLogin.getUserLogin().getEmail()) && UserLogin.getUserLogin().getSnsProvider() == 0) {
                isValidEmail = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        verificationProvider = new VerificationProvider(this);
        passwordPolicyProvider = new PasswordPolicyProvider(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000710C001"));
        isValidPhone = true;
        isValidEmail = true;
        dataSNSToken = UserLogin.getUserLogin().getSnsToken();
        dataSNSEmail = UserLogin.getUserLogin().getEmail();

        activity = this;
        etBirthday.setFocusable(false);
        etBirthday.setClickable(true);
        etBirthday.setOnClickListener(onBirthdayClick());

        etWeight.setFocusable(false);
        etWeight.setClickable(true);
        etWeight.setOnClickListener(onWeightClick());
        etWeightClick.setOnClickListener(onWeightClick());

        etHeight.setFocusable(false);
        etHeight.setClickable(true);
        etHeight.setOnClickListener(onHeightClick());
        etHeightClick.setOnClickListener(onHeightClick());

        etHardness.setFocusable(false);
        etHardness.setClickable(true);
        etHardness.setOnClickListener(onHardnessClick());
        etHardnessClick.setOnClickListener(onHardnessClick());

        etPhoneConfirm.setText(UserLogin.getUserLogin().getPhoneNumber());

        etPhoneConfirm.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidPhone = false;
                if (etPhoneConfirm.getText().toString().equals(UserLogin.getUserLogin().getPhoneNumber())) {
                    isValidPhone = true;
                }
            }
        });

        etEmail.addTextChangedListener(emailWatcher);

        String userBirhday = UserLogin.getUserLogin().getBirthDate() == null ? "" : UserLogin.getUserLogin().getBirthDate();
        etBirthday.setText(userBirhday.isEmpty() ? userBirhday : userBirhday.trim().replace("-", "/"));

        Calendar selectedDate = Calendar.getInstance();
        String userBirthDay = UserLogin.getUserLogin().getBirthDate();
        if (userBirthDay == null || userBirthDay.isEmpty()) {
            selectedDate.set(1975, 1, 1);
        } else {
            DateTime date = DateTime.parse(UserLogin.getUserLogin().getBirthDate().trim().replace("-", "/"),
                    DateTimeFormat.forPattern("yyyy/MM/dd"));
            selectedDate.set(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
        }
        birthdayPicker = new DateWheelBuilder(this, onBirthdaySelect())
                .setDate(selectedDate)
                .setCancelText(LanguageProvider.getLanguage("UI000710C049"))
                .setConfirmText(LanguageProvider.getLanguage("UI000710C050"))
                .build();
        weightOptions = new ArrayList<>();
        heightOptions = new ArrayList<>();
        hardnesOptions = new ArrayList<>();
        // set options
        int selectedHeight = 26;
        for (int i = 139; i <= 210; i++) {
            if (i == 139) {
                heightOptions.add(LanguageProvider.getLanguage("UI000710C055"));
            } else {
                heightOptions.add(i + " cm");
            }
            if (UserLogin.getUserLogin().getHeight() == i) {
                selectedHeight = heightOptions.size() - 1;
            }
        }
        int selectedWeight = 36;
        for (int i = 19; i <= 140; i++) {
            if (i == 19) {
                weightOptions.add(LanguageProvider.getLanguage("UI000710C056"));
            } else {
                weightOptions.add(i + " kg");
            }
            if (UserLogin.getUserLogin().getWeight() == i) {
                selectedWeight = weightOptions.size() - 1;
            }
        }

        //mattress hardness
        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        ArrayList<String> hardnessOptionString = new ArrayList<>();
        int hardnessIndex;
        SettingModel settingModel = SettingModel.getSetting().getUnmanaged();
        FormPolicyModel formPolicyModel = FormPolicyModel.getPolicy();
        selectedHardness = formPolicyModel.getMattressHardnessSettingById(settingModel.user_desired_hardness);
        hardnessIndex = formPolicyModel.getMattressHardnessSettingIndexById(settingModel.user_desired_hardness);
        hardnesOptions.addAll(formPolicyModel.getMattressHardnessSetting());

        for (MattressHardnessSettingModel mattressHardnessSettingModel:hardnesOptions
             ) {
            hardnessOptionString.add(mattressHardnessSettingModel.getValue());
        }

        if(nemuriScanModel != null){
            mattressSettingContainer.setVisibility(nemuriScanModel.isMattressExist() ? View.VISIBLE : GONE);
            etHardness.setText(selectedHardness.getValue());
        }

        weightPicker = new OptionsPickerBuilder(this, onWeightSelect())
                .setSelectOptions(selectedWeight)
                .setBackgroundId(0)
                .setCyclic(false, false, false)
                .setCancelText(LanguageProvider.getLanguage("UI000710C051"))
                .setSubmitText(LanguageProvider.getLanguage("UI000710C052"))
                .build();

        weightPicker.setPicker(weightOptions);

        heightPicker = new OptionsPickerBuilder(this, onHeightSelect())
                .setSelectOptions(selectedHeight)
                .setBackgroundId(0)
                .setCyclic(false, false, false)
                .setCancelText(LanguageProvider.getLanguage("UI000710C053"))
                .setSubmitText(LanguageProvider.getLanguage("UI000710C054"))
                .build();

        heightPicker.setPicker(heightOptions);

        hardnessPicker = new OptionsPickerBuilder(this, onHardnessSelect())
                .setBackgroundId(0)
                .setCyclic(false, false, false)
                .setCancelText(LanguageProvider.getLanguage("UI000710C092"))
                .setSubmitText(LanguageProvider.getLanguage("UI000710C094"))
                .setSelectOptions(hardnessIndex)
                .build();

        hardnessPicker.setPicker(hardnessOptionString);
        setTextfield();
        etZip.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (etZip.getText().length() == 0) {
                    etAddress.setText("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //XXX do something

            }

            String tempString;
            char[] stringArray;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidateAddress = false;
                if (etZip.getText().length() == 4) {
                    tempString = etZip.getText().toString() + "-";
                    char c = tempString.charAt(tempString.length() - 2);

                    if (c != '-') {
                        stringArray = tempString.toCharArray();
                        stringArray[tempString.length() - 2] = stringArray[tempString.length() - 1];
                        stringArray[tempString.length() - 1] = c;

                        //code to convert charArray back to String..
                        tempString = new String(stringArray);
                        etZip.setText(tempString);
                        etZip.setSelection(tempString.length());
                        tempString = null;
                    }

                }


            }
        });
        switch (UserLogin.getUserLogin().getGender()) {
            case 1:
                rgGender.check(R.id.radioButton);
                break;
            case 2:
                rgGender.check(R.id.radioButton2);
                break;
            case 3:
                rgGender.check(R.id.radioButton3);
                break;
            default:
                rgGender.check(R.id.radioButton);
                break;
        }
        SNSInitialize();
        applyUI();
        applyHint(PasswordPolicyModel.getFirst());

        companyCode = UserLogin.getUserLogin() != null && UserLogin.getUserLogin().getCompanyCode() != null ? UserLogin.getUserLogin().getCompanyCode() : "";
        showLoading();
        passwordPolicyProvider.getPasswordPolicy(companyCode, (passwordPolicyModel, cc) -> {
            runOnUiThread(() -> {
                hideLoading();
                applyHint(passwordPolicyModel);
            });
        });
    }

    private void SNSInitialize() {
        TwitterConfig config = new TwitterConfig.Builder(getApplicationContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);

        callbackManager = CallbackManager.Factory.create();

        facebookLoginButton.setReadPermissions(Arrays.asList("email"));
        // Callback registration
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                getFacebookUser(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {

            }
        });

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                getTwitterUser(result.data);
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.paramount.bed",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void initialSocialButtonUI() {
        UserLogin userLogin = UserLogin.getUserLogin();
        etEmail.setEnabled(userLogin.getSnsProvider() == 0);
        tbEmail.setActivated(userLogin.getSnsProvider() == 0);
        tbFacebook.setActivated(userLogin.getSnsProvider() == 1);
        tbTwitter.setActivated(userLogin.getSnsProvider() == 2);
    }

    private void applyUI() {
        initialSocialButtonUI();
        tbEmail.setOnClickListener((view -> {
            etEmail.setEnabled(true);
            tbFacebook.setChecked(false);
            tbFacebook.setActivated(false);
            tbTwitter.setChecked(false);
            tbTwitter.setActivated(false);

            if (UserLogin.getUserLogin().getSnsProvider() == 0) {
                tbEmail.setChecked(tbEmail.isChecked());
                etEmail.removeTextChangedListener(emailWatcher);
                etEmail.setText(UserLogin.getUserLogin().getEmail());
                etEmail.addTextChangedListener(emailWatcher);
                tbEmail.setActivated(true);
                return;
            }
            tbEmail.setChecked(true);
        }));
        tbFacebook.setOnClickListener((view -> {
            etEmail.setEnabled(false);
            etEmail.removeTextChangedListener(emailWatcher);
            etEmail.setText("");
            etEmail.addTextChangedListener(emailWatcher);
            tbEmail.setChecked(false);
            tbEmail.setActivated(false);
            tbTwitter.setChecked(false);
            tbTwitter.setActivated(false);

            if (UserLogin.getUserLogin().getSnsProvider() == 1) {
                tbFacebook.setChecked(tbFacebook.isChecked());
                tbFacebook.setActivated(true);
                return;
            }
            tbFacebook.setChecked(true);
        }));
        tbTwitter.setOnClickListener((view -> {
            etEmail.setEnabled(false);
            etEmail.removeTextChangedListener(emailWatcher);
            etEmail.setText("");
            etEmail.addTextChangedListener(emailWatcher);
            tbFacebook.setChecked(false);
            tbFacebook.setActivated(false);
            tbEmail.setChecked(false);
            tbEmail.setActivated(false);

            if (UserLogin.getUserLogin().getSnsProvider() == 2) {
                tbTwitter.setChecked(tbTwitter.isChecked());
                tbTwitter.setActivated(true);
                return;
            }
            tbTwitter.setChecked(true);
        }));

        if(DisplayUtils.FONTS.bigFontStatus(MyAccountEditActivity.this)){
            captionFacebook.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            captionTwitter.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            etEmail.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            isValidPhone = resultCode == 1;
            if (isValidPhone) sendIntentData();
        }
    }

    private View.OnClickListener onBirthdayClick() {
        return view -> {
            ViewUtil.hideKeyboardFrom(view.getContext(), view);
            birthdayPicker.show();
        };
    }

    private OnDateWheelSelectListener onBirthdaySelect() {
        return (date, v) -> {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            String outputDate = df.format(date);
            etBirthday.setText(outputDate);
        };
    }

    private OnOptionsSelectListener onWeightSelect() {
        return (options1, options2, options3, v) -> {
            etWeight.setText(weightOptions.get(options1));
            if (options1 == 0) {
                etWeight.setText("");
            }
        };
    }

    private OnOptionsSelectListener onHeightSelect() {
        return (options1, options2, options3, v) -> {
            etHeight.setText(heightOptions.get(options1));
            if (options1 == 0) {
                etHeight.setText("");
            }
        };
    }

    private OnOptionsSelectListener onHardnessSelect() {
        return (options1, options2, options3, v) -> {
            selectedHardness = hardnesOptions.get(options1);
            etHardness.setText(selectedHardness.getValue());
        };
    }

    private View.OnClickListener onWeightClick() {
        return view -> {
            ViewUtil.hideKeyboardFrom(getApplicationContext(), view);
            for (int i = 0; i < weightOptions.size(); i++) {
                if (etWeight.getText().toString().equals(weightOptions.get(i))) {
                    weightPicker.setSelectOptions(i);
                }
            }
            weightPicker.show();
        };
    }

    private View.OnClickListener onHeightClick() {
        return view -> {
            ViewUtil.hideKeyboardFrom(getApplicationContext(), view);
            for (int i = 0; i < heightOptions.size(); i++) {
                if (etHeight.getText().toString().equals(heightOptions.get(i))) {
                    heightPicker.setSelectOptions(i);
                }
            }
            heightPicker.show();
        };
    }

    private View.OnClickListener onHardnessClick(){
        return view -> {
            ViewUtil.hideKeyboardFrom(getApplicationContext(), view);
            hardnessPicker.show();
        };
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_my_account_edit;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }


    public void nextToPreview() {
        //Initializing Values
        valName = etName.getText().toString();
        valEmail = etEmail.getText().toString();
        valPhone = etPhoneConfirm.getText().toString();
        valBirthDay = etBirthday.getText().toString();
        valPassword = etPassword.getText().toString();
        valConfirmPassword = etConfirmPassword.getText().toString();
        valHeight = etHeight.getText().toString();
        valWeight = etWeight.getText().toString();
        valAddress = etAddress.getText().toString();

        //Refactoring Values
        valName = StringUtil.nickName(valName);
        valHeight = !valHeight.contains(" cm") ? "0" : valHeight;
        valWeight = !valWeight.contains(" kg") ? "0" : valWeight;

        //Local Validation
        if (isBadName(valName)) return;
        if (isBadBirthDay(valBirthDay)) return;
        if (isBadPassword(valPassword, valConfirmPassword)) return;
        if (isBadEmail(valEmail)) return;
        if (isBadPhoneNumber(valPhone)) return;
        if (isBadZIP(valPhone)) return;


        valZip = etZip.getText().toString().replace("-", "");
        if (valZip.trim().length() != 0) {
            if (!zipValidation()) {
                return;
            }
            if (!pressSearchValidation()) {
                return;
            }
        } else {
            etAddress.setText("");
        }

        nextOnlinePasswordCheck(valPassword.trim().length() == 0 && valConfirmPassword.trim().length() == 0);
    }

    public void nextOnlinePasswordCheck(boolean skip) {
        if (skip) {
            nextOnlineEmailCheck(!isMarkForCheck());
            return;
        }
        showLoading();
        String passwordType = etPassword.getText().toString();
        passwordPolicyProvider.checkPasswordPolicy(companyCode, passwordType, (isResponse, passwordPolicyModel, baseResponse, e) -> {
            runOnUiThread(() -> {
                hideLoading();
                applyHint(passwordPolicyModel);
                if (!isResponse) {
                    if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                        DialogUtil.offlineDialog(MyAccountEditActivity.this, getApplicationContext());
                        return;
                    }
                    if (MultipleDeviceUtil.isTokenExpired(e)) {
                        DialogUtil.tokenExpireDialog(MyAccountEditActivity.this);
                        return;
                    }
                    DialogUtil.serverFailed(activity, "UI000802C037", "UI000802C038", "UI000802C039", "UI000802C040");
                    return;
                }

                if (!baseResponse.isSucces()) {
                    String Lang = LanguageProvider.getLanguage(baseResponse.getMessage());
                    String strReplaceDialogue = Lang.replace("%MIN_LEN%", passwordPolicyModel.minLength)
                            .replace("%MAX_LEN%", passwordPolicyModel.maxLength)
                            .replace("%ALLOWED_SYMBOLS%", passwordPolicyModel.allowedSymbols);
                    DialogUtil.createSimpleOkDialog(activity, "", strReplaceDialogue);
                    return;
                }
                nextOnlineEmailCheck(!isMarkForCheck());
            });
        });
    }

    public void nextOnlineEmailCheck(boolean skip) {
        if (skip) {
            nextOnlinePhoneCheck(isValidPhone);
            return;
        }

        if (getSNSProvider() == 0) {
            if (valEmail.equalsIgnoreCase(UserLogin.getUserLogin().getEmail()) && UserLogin.getUserLogin().getSnsProvider() == 0) {
                nextOnlinePhoneCheck(isValidPhone);
                return;
            }
            doEmailValidation(valEmail);
            return;
        }
        if (getSNSProvider() == 1) {
            loginWithFacebook();
            return;
        }
        if (getSNSProvider() == 2) {
            loginWithTwitter();
            return;
        }
    }

    public void nextOnlinePhoneCheck(boolean skip) {
        if (skip) {
            sendIntentData();
            return;
        }
        isValidEmail = true;
        String phoneNumber = etPhoneConfirm.getText().toString();
        showLoading();
        verificationProvider.phoneUpdateReqOTP(phoneNumber, this);
    }

    public void applyHint(PasswordPolicyModel passwordPolicyModel) {
        String strReplace = LanguageProvider.getLanguage("UI000710C037").replace("%MIN_LEN%", passwordPolicyModel.minLength)
                .replace("%MAX_LEN%", passwordPolicyModel.maxLength)
                .replace("%ALLOWED_SYMBOLS%", passwordPolicyModel.allowedSymbols);
        etPassword.setHint(strReplace);
        String strReplaceConfirm = LanguageProvider.getLanguage("UI000710C038").replace("%MIN_LEN%", passwordPolicyModel.minLength)
                .replace("%MAX_LEN%", passwordPolicyModel.maxLength)
                .replace("%ALLOWED_SYMBOLS%", passwordPolicyModel.allowedSymbols);
        etConfirmPassword.setHint(strReplaceConfirm);
        AutoSizeTextUtil.setAutoSizeHint(etPassword);
        AutoSizeTextUtil.setAutoSizeHint(etConfirmPassword);
        AutoSizeTextUtil.setAutoSizeHintEditEmail(etEmail);
    }

    private String getEmailData() {
        if (isMarkForCheck()) return isSNS() ? dataSNSEmail : valEmail;
        return UserLogin.getUserLogin().getEmail();
    }

    private String getPassword() {
        return valPassword.trim().length() == 0 && valConfirmPassword.trim().length() == 0 ? "" : valPassword;
    }

    private int getSNSProvider() {
        if (tbEmail.isChecked()) return 0;
        if (tbFacebook.isChecked()) return 1;
        if (tbTwitter.isChecked()) return 2;
        return UserLogin.getUserLogin().getSnsProvider();
    }

    private String getSNSToken() {
        if (isMarkForCheck()) return isSNS() ? dataSNSToken : "";
        return UserLogin.getUserLogin().getSnsToken();
    }

    @Override
    public void onPhoneUpdateReqOTPSuccess(BaseResponse response) {
        hideLoading();
        if (response.isSucces()) {
            if (response.getMessage().equals("USR32-C000") || response.getMessage().equals("USR32-C099")) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage(response.getMessage()), LanguageProvider.getLanguage("UI000710C084"), ((dialogInterface, i) -> {
                    Intent intent = new Intent(this, DialogOTP.class);
                    intent.putExtra(IntentUtil.User.PHONE, etPhoneConfirm.getText().toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, 100);
                }));
                return;
            }
            DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage(response.getMessage()));
            return;
        }
        DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage(response.getMessage()));
        btnPhoneConfirm.setEnabled(true);
    }

    @Override
    public void onPhoneUpdateReqOTPError(Throwable e) {
        runOnUiThread(() -> {
            hideLoading();
            btnPhoneConfirm.setEnabled(true);
            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                DialogUtil.offlineDialog(MyAccountEditActivity.this, getApplicationContext());
            } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                DialogUtil.tokenExpireDialog(MyAccountEditActivity.this);
            }
        });
    }

    //SNS
    @OnClick(R.id.btnFacebook)
    void btnLoginWithFacebook() {
        tbFacebook.performClick();
    }

    public void loginWithFacebook() {
        if (NetworkUtil.isNetworkConnected(this)) {
            deleteAccessToken(this);
            deleteTwitterSession(this);
            facebookLoginButton.performClick();
        } else {
            DialogUtil.offlineDialog(MyAccountEditActivity.this, getApplicationContext());
        }
    }

    @OnClick(R.id.btnTwitter)
    void btnLoginWithTwitter() {
        tbTwitter.performClick();
    }

    public void loginWithTwitter() {
        if (NetworkUtil.isNetworkConnected(this)) {
            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
            deleteAccessToken(this);
            deleteTwitterSession(this);
            twitterLoginButton.performClick();
        } else {
            DialogUtil.offlineDialog(MyAccountEditActivity.this, getApplicationContext());
        }
    }

    public void getTwitterUser(TwitterSession session) {
        TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                String email = result.data;
                if (email == null || email.isEmpty()) {
                    DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("USR01-C008"));
                } else {
                    loginSocial(2, result.data, session.getAuthToken().token);
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
                            DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("USR01-C008"));
                        } else {
                            loginSocial(1, email, accessToken.getToken().toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("USR01-C008"));
                    }
                }));
        Bundle parameters = new Bundle();
        parameters.putString("fields", "email");
//        parameters.putString("fields", "id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void loginSocial(int snsProviderType, String email, String accessToken) {
        dataSNSToken = accessToken;
        dataSNSEmail = email;
        if (email.equalsIgnoreCase(UserLogin.getUserLogin().getEmail())) {
            nextOnlinePhoneCheck(isValidPhone);
            return;
        }
        doEmailValidation(email);
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


    public void doEmailValidation(String email) {
        showProgress();
        verificationProvider.emailValidation(email, isSNS() ? 2 : 1, ValidationEmailModel.getByEmail(email).getToken(), this);
    }

    public void doEmailActivation(String email) {
        showProgress();
        verificationProvider.emailActivation(email, ValidationEmailModel.getByEmail(email).getToken(), this);
    }

    @Override
    public void onEmailValidationSuccess(String email, int mailVerification, String token, BaseResponse response) {
        runOnUiThread(() -> {
            hideProgress();
            String responseMessage = LanguageProvider.getLanguage(response.getMessage());
            if (response.isSucces()) {
                ValidationEmailModel.updateByEmail(email, "", "");
                Logx("onEmailValidationSuccess", response.getMessage());
                //ByPass If SNS
                if (mailVerification == 2) {
                    nextOnlinePhoneCheck(isValidPhone);
                    return;
                }

                //ByPass If Email Already Request
                if (response.getMessage().equals("USR36-C099")) {
                    doEmailActivation(email);
                    return;
                }

                DialogUtil.createCustomYesNo(this, "", responseMessage,
                        LanguageProvider.getLanguage("UI000710C078"),
                        (dialogInterface, i) -> dialogInterface.dismiss(),
                        LanguageProvider.getLanguage("UI000710C077"),
                        (dialogInterface, i) -> doEmailActivation(email));
                return;
            }
            DialogUtil.createSimpleOkDialog(this, "", responseMessage);
        });
    }

    @Override
    public void onEmailValidationError(Throwable e) {
        hideProgress();
        if (!NetworkUtil.isNetworkConnected(MyAccountEditActivity.this)) {
            DialogUtil.offlineDialog(MyAccountEditActivity.this, MyAccountEditActivity.this);
            return;
        }
        if (MultipleDeviceUtil.isTokenExpired(e)) {
            DialogUtil.tokenExpireDialog(MyAccountEditActivity.this);
            return;
        }
        DialogUtil.serverFailed(MyAccountEditActivity.this, "UI000802C129", "UI000802C130", "UI000802C131", "UI000802C132");
    }

    @Override
    public void onEmailActivationSuccess(String email, String token, BaseResponse response) {
        runOnUiThread(() -> {
            hideProgress();
            String responseMessage = LanguageProvider.getLanguage(response.getMessage());
            if (response.isSucces()) {
                if (isSNS()) {
                    nextOnlinePhoneCheck(isValidPhone);
                    return;
                }
                DialogUtil.createSimpleOkDialog(this, "", responseMessage, LanguageProvider.getLanguage("UI000802C003"), (dialogInterface, i) -> {
                    nextOnlinePhoneCheck(isValidPhone);
                });
                return;
            }
            DialogUtil.createCustomYesNo(this, "",
                    responseMessage,
                    LanguageProvider.getLanguage("UI000710C078"),
                    (dialogInterface, i) -> dialogInterface.dismiss(),
                    LanguageProvider.getLanguage("UI000710C077"),
                    (dialogInterface, i) -> doEmailActivation(email));
        });
    }

    @Override
    public void onEmailActivationError(Throwable e) {
        hideProgress();
        if (!NetworkUtil.isNetworkConnected(MyAccountEditActivity.this)) {
            DialogUtil.offlineDialog(MyAccountEditActivity.this, MyAccountEditActivity.this);
            return;
        }
        if (MultipleDeviceUtil.isTokenExpired(e)) {
            DialogUtil.tokenExpireDialog(MyAccountEditActivity.this);
            return;
        }
        DialogUtil.serverFailed(MyAccountEditActivity.this, "UI000802C129", "UI000802C130", "UI000802C131", "UI000802C132");
    }


    //#region Local On Next Validator
    public boolean isBadName(String name) {
        return ValidationUtils.NAME.isBad(name, new ValidationUtils.NAME.BadNameListener() {
            @Override
            public void onNameEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C043")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onNameShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C086")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onNameLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C045")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onNameHasEmoji(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C031")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onNameHasSpecialChars(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C031")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }

    public boolean isBadBirthDay(String birthDay) {
        if (birthDay.trim().isEmpty()) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000710M012"));
            return true;
        }
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
        DateTime valBirthday = formatter.parseDateTime(birthDay);
        if (valBirthday.isAfterNow()) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000710C019"));
            return true;
        }
        return false;
    }

    public boolean isBadPassword(String password, String confirmPassword) {
        if (valPassword.trim().length() == 0 && valConfirmPassword.trim().length() == 0)
            return false;

        return ValidationUtils.PASSWORD.isBad(password, confirmPassword, new ValidationUtils.PASSWORD.BadPasswordListener() {
            @Override
            public void onPasswordEmpty() {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C023"));
            }

            @Override
            public void onPasswordNotMatch() {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C030"));
            }
        });
    }

    public boolean isBadEmail(String email) {
        if (!tbEmail.isChecked()) {
            return false;
        }
        return ValidationUtils.EMAIL.isBad(email, new ValidationUtils.EMAIL.BadEmailListener() {
            @Override
            public void onEmailEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C073")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onEmailShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C074")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onEmailLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C075")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onEmailCharsWrong(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C076")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }

    public boolean isBadPhoneNumber(String phoneNumber) {
        return ValidationUtils.PHONE.isBad(phoneNumber, new ValidationUtils.PHONE.BadPhoneListener() {
            @Override
            public void onPhoneEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C079")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C087")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C080")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneCharsWrong(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(MyAccountEditActivity.this, "", LanguageProvider.getLanguage("UI000710C081")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }

    public boolean isBadZIP(String zipCode) {
        return false;
    }

    public boolean isSNS() {
        return !tbEmail.isChecked();
    }

    public boolean isMarkForCheck() {
        return tbEmail.isChecked() || tbFacebook.isChecked() || tbTwitter.isChecked();
    }
    //#endregion

    private void sendIntentData() {
        Intent intent = new Intent(this, MyAccountSaveActivity.class);
        intent.putExtra(IntentUtil.User.NAME, StringUtil.nickName(valName));
        intent.putExtra(IntentUtil.User.BIRTHDAY, valBirthDay);
        //TODO:ChangePhoneNumber
        intent.putExtra(IntentUtil.User.EMAIL, getEmailData());
        intent.putExtra(IntentUtil.User.PHONE, etPhoneConfirm.getText().toString());
        intent.putExtra(IntentUtil.User.GENDER, getValGender());
        intent.putExtra(IntentUtil.User.SNS_PROVIDER, getSNSProvider());
        intent.putExtra(IntentUtil.User.SNS_TOKEN, getSNSToken());
        intent.putExtra(IntentUtil.User.MATTRESS_HARDNESS_ID, selectedHardness.getId());

        intent.putExtra(IntentUtil.User.ZIP, valZip);
        intent.putExtra(IntentUtil.User.PASSWORD, getPassword());
        intent.putExtra(IntentUtil.User.HEIGHT, valHeight.replace(" cm", ""));
        intent.putExtra(IntentUtil.User.WEIGHT, valWeight.replace(" kg", ""));
        intent.putExtra(IntentUtil.User.ADDRESS, !valZip.equals("") ? valAddress : "");
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    //TODO:Need Refactoring
    private int getValGender() {
        switch (rgGender.getCheckedRadioButtonId()) {
            case R.id.radioButton:
                return 1;
            case R.id.radioButton2:
                return 2;
            case R.id.radioButton3:
                return 3;
            default:
                return 1;
        }
    }

    private void reqZipDialog() {
        if (!isNetworkConnected(getApplicationContext())) {
            DialogUtil.offlineDialog(this, getApplicationContext());
            return;
        }
        btnZipReq.setEnabled(false);
        showLoading();

        Activity activity = this;
        String zipCode = etZip.getText().toString().replace("-", "");
        this.mDisposable = this.userService.requestZipLookup(zipCode, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<ZipResponse>>() {

                    @Override
                    public void onSuccess(BaseResponse<ZipResponse> zipResponseBaseResponse) {
                        hideLoading();
                        btnZipReq.setEnabled(true);
                        if (zipResponseBaseResponse.isSucces()) {
                            if (zipResponseBaseResponse.getData().getCity() != null) {
                                etAddress.setText(zipResponseBaseResponse.getData().getPrefecture() + " " + zipResponseBaseResponse.getData().getCity() + " " + zipResponseBaseResponse.getData().getTown());
                                isValidateAddress = true;
                            } else {
                                etAddress.setText("");
                                isValidateAddress = false;
                            }
                        } else {
                            etAddress.setText("");
                            isValidateAddress = false;
                            DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(zipResponseBaseResponse.getMessage())
                                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
                            );
                        }

                        etAddress.setVisibility(View.VISIBLE);
                    }


                    @Override
                    public void onError(Throwable e) {
                        Log.d("abx", e.getMessage());
                        hideLoading();
                        btnZipReq.setEnabled(true);
                        isValidateAddress = false;
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            DialogUtil.offlineDialog(MyAccountEditActivity.this, getApplicationContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(MyAccountEditActivity.this);
                        } else {
                            DialogUtil.serverFailed(MyAccountEditActivity.this, "UI000802C037", "UI000802C038", "UI000802C039", "UI000802C037");
                        }
                    }
                });


    }

    public void setTextfield() {
        etName.setText(StringUtil.nickName(UserLogin.getUserLogin().getNickname()));
        etEmail.removeTextChangedListener(emailWatcher);
        etEmail.setText(UserLogin.getUserLogin().getSnsProvider() == 0 ? UserLogin.getUserLogin().getEmail() : "");
        etEmail.addTextChangedListener(emailWatcher);
        String zipBegin, zipLast;
        if (!UserLogin.getUserLogin().getZipCode().equals("")) {
            zipBegin = UserLogin.getUserLogin().getZipCode().replace("-", "").substring(0, 3);
            zipLast = UserLogin.getUserLogin().getZipCode().replace("-", "").substring(3, 7);
            etZip.setText(zipBegin + "-" + zipLast);
        } else {
            etZip.setText("");
        }
        if (UserLogin.getUserLogin().getHeight() > 0) {
            etHeight.setText(" " + UserLogin.getUserLogin().getHeight() + " cm");
        }
        if (UserLogin.getUserLogin().getWeight() > 0) {
            etWeight.setText(" " + UserLogin.getUserLogin().getWeight() + " kg");
        }

        etAddress.setText(UserLogin.getUserLogin().getStreetAddress() != null ? UserLogin.getUserLogin().getStreetAddress() : "");
    }

    public boolean zipValidation() {
        valZip = etZip.getText().toString().replace("-", "");
        if (valZip.trim().length() == 0) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000710C028")
                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
            );
            return false;
        }
        if (valZip.length() != 7 && !valZip.isEmpty()) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000710C028")
                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
            );
            return false;
        }
        return true;
    }

    public boolean pressSearchValidation() {
        if (valZip.trim().length() == 0) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000710C028")
                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
            );
            return false;
        }
        if (!isValidateAddress && !etZip.getText().toString().replace("-", "").trim().equals("")) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000710C040")
                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
            );
            return false;
        } else {
            return true;
        }
    }
}
