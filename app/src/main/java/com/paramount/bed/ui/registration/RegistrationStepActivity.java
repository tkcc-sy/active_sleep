package com.paramount.bed.ui.registration;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.Question;
import com.paramount.bed.data.model.QuestionAnswer;
import com.paramount.bed.data.model.QuestionnaireAnswerModel;
import com.paramount.bed.data.model.QuestionnaireModel;
import com.paramount.bed.data.model.QuestionnaireQuestionModel;
import com.paramount.bed.data.model.RegisterData;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.NemuriScanCheckResponse;
import com.paramount.bed.data.remote.response.QuestionAnswerResponse;
import com.paramount.bed.data.remote.response.QuestionResponse;
import com.paramount.bed.data.remote.response.QuestionnaireResponse;
import com.paramount.bed.data.remote.service.NemuriScanService;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.step.AccountAddressFragment;
import com.paramount.bed.ui.registration.step.AccountBioFragment;
import com.paramount.bed.ui.registration.step.AccountRegistrationFragment;
import com.paramount.bed.ui.registration.step.BluetoothListFragment;
import com.paramount.bed.ui.registration.step.ConnectionOptionFragment;
import com.paramount.bed.ui.registration.step.EmailInputFragment;
import com.paramount.bed.ui.registration.step.ManualWifiFragment;
import com.paramount.bed.ui.registration.step.PreviewFragment;
import com.paramount.bed.ui.registration.step.QuizFragment;
import com.paramount.bed.ui.registration.step.StartFragment;
import com.paramount.bed.ui.registration.step.TelephoneInputFragment;
import com.paramount.bed.ui.registration.step.TelephoneVerificationFragment;
import com.paramount.bed.ui.registration.step.WifiConnectFragment;
import com.paramount.bed.ui.registration.step.WifiListFragment;
import com.paramount.bed.ui.registration.step.WifiPasswordFragment;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.NemuriScanUtil;
import com.paramount.bed.util.ProgressDrawable;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.paramount.bed.ui.registration.step.StartFragment.isFromBluetoothList;

public class RegistrationStepActivity extends BaseActivity {
    public static int CURRENT_FRAGMENT = -1;
    public static final int FRAGMENT_START = 310;
    public static final int FRAGMENT_BLUETOOTH_LIST = 311;
    public static final int FRAGMENT_CONNECTION_OPTION = 323;
    public static final int FRAGMENT_WIFI_LIST = 320;
    public static final int FRAGMENT_MANUAL_WIFI = 321;
    public static final int FRAGMENT_WIFI_PASSWORD = 330;
    public static final int FRAGMENT_WIFI_CONNECT = 340;
    public static final int FRAGMENT_ACCOUNT_REGISTRATION = 400;
    public static final int FRAGMENT_EMAIL_INPUT = 410;
    public static final int FRAGMENT_TELEPHONE_INPUT = 420;
    public static final int FRAGMENT_TELEPHONE_VERIFICATION = 430;
    public static final int FRAGMENT_ACCOUNT_BIO = 440;
    public static final int FRAGMENT_ACCOUNT_ADDRESS = 450;
    public static final int FRAGMENT_QUIZ = 460;
    public static final int FRAGMENT_PREVIEW = 465;
    public static final int FINISH_FLOW = -1;

    public static RegisterData registerData;
    public static NemuriScanCheckResponse NEMURI_SCAN_CHECK;

    public int activeQuiz = 0;

    public String selectedBluetooth = "";
    public String selectedWifi = "";
    public String inputPhone = "";
    public int selectedGender = 0;
    public static int PROGRESSBAR_SEGMENT_SUM = 15;
    public static int INITIAL_PROGRESSBAR_SEGMENT = 3;
    public boolean isRegistration;
    public boolean isWifiOnly;

    public static int TYPE = 0;
    public static String EMAIL = "";
    public static String PASSWORD = "";
    public static String ACCESS_TOKEN = "";
    public static String PHONE_NUMBER = "";
    public static String NICK_NAME = "";
    public static String BIRTH_DAY = "BIRTH_DAY";
    public static int GENDER = 1;
    public static String ZIP_CODE = "";
    public static String CITY = "";
    public static String PREFECTURE = "";
    public static String ADDRESS = "";
    public static String COMPANY_CODE = "-";
    public static String HEIGHT = "";
    public static String WEIGHT = "";
    public static int USER_TYPE = 1;
    public static String QUESTIONNAIRE_RESULT = "";
    public static String SERIAL_NUMBER = "";

    public static String COMPANY_NAME = "-";
    public static int COMPANY_ID = 0;
    public static int IS_COMPANY_REGISTER = 0;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    public static ArrayList<Question> questions;
    public static Disposable mDisposables;
    public static UserService questionnareService;
    public static NemuriScanService nemuriScanService;

    public static int RC_SIGN_IN = 506;

    GoogleSignInClient mGoogleSignInClient;

    public static RegistrationStepActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000310C001"));
        setActionBar(toolbar);
        questionnareService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        nemuriScanService = ApiClient.getClient(getApplicationContext()).create(NemuriScanService.class);
        getQuestionnare(false);
        mInstance = this;
        if (IS_COMPANY_REGISTER == 1) {
            if (getIntent().getStringExtra("companyCode") != null) {
                COMPANY_CODE = getIntent().getStringExtra("companyCode");
            }
        } else {
            COMPANY_CODE = "-";
        }
        isRegistration = getIntent().getBooleanExtra("is_registration", true);
        isWifiOnly = getIntent().getBooleanExtra("is_wifi_only", false);
        if (StatusLogin.getUserLogin() == null) {
            PROGRESSBAR_SEGMENT_SUM = 15;
            INITIAL_PROGRESSBAR_SEGMENT = 3;
        } else {
            if (!StatusLogin.getUserLogin().statusLogin) {
                PROGRESSBAR_SEGMENT_SUM = 15;
                INITIAL_PROGRESSBAR_SEGMENT = 3;
            } else {
                PROGRESSBAR_SEGMENT_SUM = 5;
                INITIAL_PROGRESSBAR_SEGMENT = 1;
            }
        }
        if(isRegistration){
            PROGRESSBAR_SEGMENT_SUM = 15;
            INITIAL_PROGRESSBAR_SEGMENT = 3;
        }

        TwitterConfig config = new TwitterConfig.Builder(getApplicationContext())
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);

        registerData = new RegisterData();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        isFromBluetoothList = false;
        initProgressBarSegment();
        if(isWifiOnly) {
            go(FRAGMENT_CONNECTION_OPTION);
            renderToolbar(Objects.requireNonNull(getPageOptions(FRAGMENT_CONNECTION_OPTION)));
        }else{
            setFragment(new StartFragment(), false, FRAGMENT_START + "");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initProgressBarSegment() {
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");
        Drawable d = new ProgressDrawable(fgColor, bgColor, PROGRESSBAR_SEGMENT_SUM);
        progressBar.setProgressDrawable(d);
        setProgressBarSegment(INITIAL_PROGRESSBAR_SEGMENT);
    }

    public void setProgressBarSegment(int segment) {
        progressBar.setProgress(1000 * segment / RegistrationStepActivity.PROGRESSBAR_SEGMENT_SUM);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_registration_step;
    }

    private void setFragment(Fragment fragment, boolean shouldAddToBackStack, String tag) {
        // Get a reference to the fragment manager
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);


        // Start the transaction & specify the holder
        //String tag = fragment.getClass().getSimpleName();
        transaction.replace(R.id.fragment_holder, fragment, tag);

        // If desired, add to the backstack
        if (shouldAddToBackStack) {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    public void go(int id) {
        StepComponent comp = getPageOptions(id);
        if (comp == null) return;
        boolean shouldAdd = true;
        setFragment(comp.fragment, shouldAdd, id + "");
        renderToolbar(comp);

    }

    public void poptoFragmentTag(int tag) {
        FragmentManager fm = getSupportFragmentManager();
        for (int i = fm.getBackStackEntryCount() - 1; i > 0; i--) {
            if (!fm.getBackStackEntryAt(i).getName().equalsIgnoreCase(tag + "")) {
                fm.popBackStack();
            } else {
                break;
            }
        }
        renderToolbar(getPageOptions(tag));
    }

    public void renderToolbar(StepComponent comp) {
        if (comp.toolbar) {
            getActionBar().show();
            setToolbarTitle(comp.title);
            setProgressBarSegment(comp.progress);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            getActionBar().hide();
            progressBar.setVisibility(View.GONE);
        }
    }

    public void handleLoginGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Log.d("TAG", "handleSignInResult: " + account.getIdToken());
            // Signed in successfully, show authenticated UI.
            registerData = new RegisterData();
            registerData.setEmail(account.getEmail());
            registerData.setType(3);
            TYPE = 3;
            ACCESS_TOKEN = account.getIdToken();
            go(FRAGMENT_EMAIL_INPUT);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("abx", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }

    public class StepComponent {
        Fragment fragment;
        Boolean toolbar;
        int progress;
        String title;

        public StepComponent(Fragment fragment, Boolean toolbar, String title, int progress) {
            this.fragment = fragment;
            this.toolbar = toolbar;
            this.progress = progress;
            this.title = title;
        }
    }

    @BindView(R.id.btnBack)
    public ImageView btnBack;

    public boolean statusForStartFragment = true;

    private StepComponent getPageOptions(int id) {
        Fragment fragment = null;
        Boolean toolbarVisibilty = true;
        float page = 1;
        int progress = 0;
        String title = "";
        switch (id) {
            case FRAGMENT_START:
                btnBack.setEnabled(true);
                fragment = new StartFragment();
                page = 1;
                title = LanguageProvider.getLanguage("UI000310C001");
                break;
            case FRAGMENT_BLUETOOTH_LIST:
                btnBack.setEnabled(true);
                BluetoothListFragment.isFromWifiList = false;
                fragment = new BluetoothListFragment();
                page = 2;
                title = LanguageProvider.getLanguage("UI000311C001");
                break;
            case FRAGMENT_CONNECTION_OPTION:
                btnBack.setEnabled(true);
                BluetoothListFragment.isFromWifiList = false;
                ConnectionOptionFragment connectionOptionFragment = new ConnectionOptionFragment();
                connectionOptionFragment.isWifiOnly = isWifiOnly;
                fragment = connectionOptionFragment;
                page = 3;
                title = LanguageProvider.getLanguage("UI000321C006");
                break;
            case FRAGMENT_WIFI_LIST:
                btnBack.setEnabled(true);
                fragment = new WifiListFragment();
                page = 4;
                title = LanguageProvider.getLanguage("UI000320C001");
                break;
            case FRAGMENT_WIFI_PASSWORD:
                fragment = new WifiPasswordFragment();
                toolbarVisibilty = false;
                break;
            case FRAGMENT_WIFI_CONNECT:
                if (!BaseActivity.isLoading) {
                    btnBack.setEnabled(true);
                }
                WifiConnectFragment wifiConnectFragment = new WifiConnectFragment();
                wifiConnectFragment.isWifiOnly = isWifiOnly;
                fragment = wifiConnectFragment;
                toolbarVisibilty = true;
                title = LanguageProvider.getLanguage("UI000340C001");
                page = 5;
                break;
            case FRAGMENT_MANUAL_WIFI:
                btnBack.setEnabled(true);
                fragment = new ManualWifiFragment();
                toolbarVisibilty = true;
                title = LanguageProvider.getLanguage("UI000330C001");
                page = 5;
                break;
            case FRAGMENT_ACCOUNT_REGISTRATION:
                btnBack.setEnabled(true);
                page = 6;
                title = LanguageProvider.getLanguage("UI000400C001");
                fragment = new AccountRegistrationFragment();
                break;
            case FRAGMENT_EMAIL_INPUT:
                btnBack.setEnabled(true);
                page = 7;
                title = LanguageProvider.getLanguage("UI000410C001");
                fragment = (Fragment) new EmailInputFragment();
                break;
            case FRAGMENT_TELEPHONE_INPUT:
                btnBack.setEnabled(true);
                page = 8;
                toolbarVisibilty = true;
                title = LanguageProvider.getLanguage("UI000420C001");
                fragment = (Fragment) new TelephoneInputFragment();
                break;
            case FRAGMENT_TELEPHONE_VERIFICATION:
                btnBack.setEnabled(true);
                page = 9;
                toolbarVisibilty = true;
                title = LanguageProvider.getLanguage("UI000430C001");
                fragment = new TelephoneVerificationFragment();
                break;
            case FRAGMENT_ACCOUNT_BIO:
                btnBack.setEnabled(true);
                fragment = new AccountBioFragment();
                page = 10;
                toolbarVisibilty = true;
                title = LanguageProvider.getLanguage("UI000440C001");
                break;
            case FRAGMENT_ACCOUNT_ADDRESS:
                btnBack.setEnabled(true);
                fragment = new AccountAddressFragment();
                page = 11;
                title = LanguageProvider.getLanguage("UI000450C001");
                break;
            case FRAGMENT_QUIZ:
                btnBack.setEnabled(true);
                if (questions.size() == 0) {
                    page = 13;
                    fragment = new PreviewFragment();
                    toolbarVisibilty = true;
                    title = LanguageProvider.getLanguage("UI000465C001");
                } else {
                    Bundle b = new Bundle();
                    b.putInt("activeQuiz", activeQuiz);
                    fragment = new QuizFragment(activeQuiz, questions.get(activeQuiz));
                    fragment.setArguments(b);
                    page = 12;
                    title = LanguageProvider.getLanguage("UI000460C001");
                }
                break;
            case FRAGMENT_PREVIEW:
                btnBack.setEnabled(true);
                page = 13;
                fragment = new PreviewFragment();
                toolbarVisibilty = true;
                title = LanguageProvider.getLanguage("UI000465C001");
                break;
            case FINISH_FLOW:
                finish();
                return null;
            default:
                fragment = new StartFragment();
                page = 1;
        }
        //TODO : set the reference in a better way
        if (fragment instanceof BLEFragment) {
            ((BLEFragment) fragment).activityRef = this;
        }
        CURRENT_FRAGMENT = id;
        progress = (int) page + 2; //offset by two activities
        if (!isRegistration) {
            progress -= 2;
        }
        return new StepComponent(fragment, toolbarVisibilty, title, progress);
    }

    @Override
    public void onBackPressed() {
        if (!isLoading) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
            int tag = Integer.parseInt(currentFragment.getTag());
            if (tag == FRAGMENT_START) {
                finish();
            }
            if(tag == FRAGMENT_CONNECTION_OPTION && isWifiOnly){
                NSManager.getInstance(this,null).disconnectCurrentDevice();
                finish();
            }
            super.onBackPressed();

            if (tag == FRAGMENT_QUIZ) {
                try {
                    int active = currentFragment.getArguments().getInt("activeQuiz");
                    activeQuiz = active - 1;
                } catch (Exception e) {

                }

            }
            if (tag == FRAGMENT_ACCOUNT_REGISTRATION) {
                hideProgress();
                getSupportFragmentManager().popBackStackImmediate();
                renderToolbar(getPageOptions(FRAGMENT_MANUAL_WIFI));
            }
            if (tag == FRAGMENT_ACCOUNT_BIO && Integer.valueOf(getTopFragment().getTag()) == FRAGMENT_TELEPHONE_VERIFICATION) {
                getSupportFragmentManager().popBackStackImmediate();
                renderToolbar(getPageOptions(FRAGMENT_TELEPHONE_INPUT));
            }

            if (getTopFragment() != null) {
                StepComponent comp = getPageOptions(Integer.valueOf(getTopFragment().getTag()));
                renderToolbar(comp);
            }
        }
    }

    public Fragment getTopFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return getSupportFragmentManager().findFragmentByTag(String.valueOf(FRAGMENT_START));
        }
        String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        return getSupportFragmentManager().findFragmentByTag(fragmentTag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the fragment, which will then pass the result to the login
        // button.
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_holder);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    public static String paramCompanyCode;

    public void getQuestionnare(Boolean isFromNext) {
//        showLoading();
        String iCompanyCode;
        try {
            iCompanyCode = getIntent().getStringExtra("companyCode");
        } catch (Exception e) {
            iCompanyCode = "";
        }
        if (isFromNext) {
            iCompanyCode = paramCompanyCode == null ? "" : paramCompanyCode;
        } else {
            paramCompanyCode = iCompanyCode;
        }
        mDisposables = questionnareService.getQuestionnaire(iCompanyCode, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<QuestionnaireResponse<ArrayList<QuestionResponse<ArrayList<QuestionAnswerResponse>>>>>>() {
                    public void onSuccess(BaseResponse<QuestionnaireResponse<ArrayList<QuestionResponse<ArrayList<QuestionAnswerResponse>>>>> qResponse) {
//                        hideLoading();
                        InsertToDatabase(qResponse.getData(), isFromNext);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("abx load content 2");
                        loadQuestion(isFromNext);
                    }
                });
    }

    public void InsertToDatabase(QuestionnaireResponse<ArrayList<QuestionResponse<ArrayList<QuestionAnswerResponse>>>> data, Boolean isFromNext) {
        QuestionnaireModel qtrunc = new QuestionnaireModel();
        qtrunc.truncate();
        QuestionnaireQuestionModel qqmtrunc = new QuestionnaireQuestionModel();
        qqmtrunc.truncate();
        QuestionnaireAnswerModel qamtrunc = new QuestionnaireAnswerModel();
        qamtrunc.truncate();
//        AnswerResult.clear();
        QuestionnaireModel qm = new QuestionnaireModel();
        qm.setQuestionnaire_id(data.getQuestionnaireId());
        qm.setTitle(data.getTitle());
        qm.setDescription(data.getDescription());
        for (int i = 0; i < data.getQuestions().size(); i++) {
            QuestionnaireQuestionModel qqm = new QuestionnaireQuestionModel();
            qqm.setQuestionnaire_id(data.getQuestionnaireId());
            qqm.setQuestion_id(data.getQuestions().get(i).getQuestionId());
            qqm.setIs_multiple_choice(data.getQuestions().get(i).getisMultipleChoice() == null ? false : data.getQuestions().get(i).getisMultipleChoice());
            qqm.setContent(data.getQuestions().get(i).getContent());
            for (int j = 0; j < data.getQuestions().get(i).getAnswers().size(); j++) {
                QuestionnaireAnswerModel qam = new QuestionnaireAnswerModel();
                qam.setQuestion_id(data.getQuestions().get(i).getQuestionId());
                qam.setAnswer_id(data.getQuestions().get(i).getAnswers().get(j).getAnswerId());
                qam.setContent(data.getQuestions().get(i).getAnswers().get(j).getContent());
                qam.insert();
            }
            qqm.insert();
        }
        qm.insert();
        loadQuestion(isFromNext);
    }

    public void loadQuestion(Boolean isFromNext) {
        questions = new ArrayList<>();
        QuestionnaireQuestionModel qqmread = new QuestionnaireQuestionModel();
        for (int i = 0; i < qqmread.getAll().size(); i++) {
            QuestionnaireAnswerModel qqaread = new QuestionnaireAnswerModel();

            ArrayList<QuestionAnswer> answers = new ArrayList<>();
            //filter
            ArrayList<QuestionnaireAnswerModel> qqafilter = qqaread.getByQuestionID(qqmread.getAll().get(i).getQuestion_id());
            for (int j = 0; j < qqafilter.size(); j++) {
                answers.add(new QuestionAnswer(qqafilter.get(j).getAnswer_id(), qqafilter.get(j).getContent()));
            }
            Question question = new Question(qqmread.getAll().get(i).getIs_multiple_choice() ? Question.MULTIPLE_CHOICE : Question.ONE_CHOICE, qqmread.getAll().get(i).getQuestion_id(), qqmread.getAll().get(i).getContent(), answers);
            questions.add(question);
        }
        if (isFromNext) {
            activeQuiz = 0;
            mInstance.hideLoading();
            mInstance.go(RegistrationStepActivity.FRAGMENT_QUIZ);
            AccountAddressFragment.btnNext.setEnabled(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    public static void checkNemuri(String nemuriScanSN) {
        mInstance.showLoading();
        NemuriScanUtil.check(nemuriScanSN, nemuriScanService, mInstance, 0);
    }

    public void successCheck(String nsSerialNumber, NemuriScanCheckResponse response) {
        mInstance.hideLoading();
        RegistrationStepActivity.SERIAL_NUMBER = "F" + nsSerialNumber;
        RegistrationStepActivity.NEMURI_SCAN_CHECK = response;

        DialogUtil.createSimpleOkDialog(mInstance, "",
                LanguageProvider.getLanguage("UI000311C005"),
                LanguageProvider.getLanguage("UI000802C003"),
                ((dialogInterface, i) -> {
                    //dummy NS

                    BluetoothListFragment.selectedNemuriScan = new NemuriScanModel();
                    BluetoothListFragment.selectedNemuriScan.setServerGeneratedId(RegistrationStepActivity.NEMURI_SCAN_CHECK.getData().getServerId());
                    BluetoothListFragment.selectedNemuriScan.setServerURL(RegistrationStepActivity.NEMURI_SCAN_CHECK.getData().getNsUrl());
                    BluetoothListFragment.selectedNemuriScan.setMacAddress("1A:2A:3A:4A:5A:6A");
                    BluetoothListFragment.selectedNemuriScan.setSerialNumber(RegistrationStepActivity.SERIAL_NUMBER);
                    BluetoothListFragment.selectedNemuriScan.setInfoType(NSSpec.BED_MODEL.INTIME);
                    BluetoothListFragment.selectedNemuriScan.setMattressExist(true);
                    BluetoothListFragment.selectedNemuriScan.setBedExist(true);

                    mInstance.selectedBluetooth = "Dummy BLE";
                    mInstance.go(RegistrationStepActivity.FRAGMENT_CONNECTION_OPTION);
                })
        );

    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }
}
