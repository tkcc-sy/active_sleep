package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.orhanobut.logger.Logger;
import com.paramount.bed.BedApplication;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSAutomaticOperationDelegate;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.data.model.ActivityModel;
import com.paramount.bed.data.model.AdvertiseModel;
import com.paramount.bed.data.model.DailyScoreModel;
import com.paramount.bed.data.model.DashboardModel;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.ForestModel;
import com.paramount.bed.data.model.LanguageModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.QuestionGeneralModel;
import com.paramount.bed.data.model.SenderBirdieModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.TutorialShowModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.VersionModel;
import com.paramount.bed.data.model.WeeklyScoreModel;
import com.paramount.bed.data.model.WeeklyScoreReviewModel;
import com.paramount.bed.data.provider.AdsQSProvider;
import com.paramount.bed.data.provider.ContentProvider;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.ForestProvider;
import com.paramount.bed.data.provider.FormPolicyProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.LogProvider;
import com.paramount.bed.data.provider.MattressSettingProvider;
import com.paramount.bed.data.provider.MaxRowProvider;
import com.paramount.bed.data.provider.ScoreProvider;
import com.paramount.bed.data.provider.SettingProvider;
import com.paramount.bed.data.provider.SnoringProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.DashboardResponse;
import com.paramount.bed.data.remote.response.FirmwareVersionResponse;
import com.paramount.bed.data.remote.response.SettingResponse;
import com.paramount.bed.data.remote.response.UserDetailResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.dialog.CalendarWebviewDialog;
import com.paramount.bed.ui.login.LoginEmailActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.AnimateUtils;
import com.paramount.bed.util.AppUpdaterUtil;
import com.paramount.bed.util.BluetoothUtil;
import com.paramount.bed.util.CloseUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MediaPlayerUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NemuriScanUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.RecyclerItemClickListener;
import com.paramount.bed.util.RxUtil;
import com.paramount.bed.util.SnoreFileUtil;
import com.paramount.bed.util.SystemSettingUtil;
import com.paramount.bed.util.TimerUtils;
import com.paramount.bed.util.TokenExpiredReceiver;
import com.paramount.bed.util.ViewUtil;
import com.paramount.bed.util.WebViewUtil;
import com.paramount.bed.util.alarms.AlarmsAutoScheduler;
import com.paramount.bed.util.alarms.AlarmsQuizModule;
import com.paramount.bed.util.alarms.AlarmsSleepQuestionnaire;
import com.paramount.bed.util.alarms.WeeklyScoreReviewDialog;
import com.paramount.bed.util.homesequence.AppUpdateSequence;
import com.paramount.bed.util.homesequence.ForestSequence;
import com.paramount.bed.util.homesequence.HomeSequenceManager;
import com.paramount.bed.util.homesequence.NewsSequence;
import com.paramount.bed.util.homesequence.QuestionnaireSequence;
import com.paramount.bed.util.homesequence.SequenceDelegate;
import com.paramount.bed.util.homesequence.SleepResetSequence;
import com.paramount.bed.util.homesequence.TutorialSequence;
import com.paramount.bed.util.homesequence.WeeklyScoreReviewSequence;
import com.paramount.bed.viewmodel.TimerViewModel;
import com.suke.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.paramount.bed.util.AppUpdaterUtil.ANDROID_APPLICATION_TYPE_BED;
import static com.paramount.bed.util.LogUtil.Logx;
import static com.paramount.bed.util.alarms.AlarmsScheduler.setAllAlarms;

public class HomeActivity extends BaseActivity implements CalendarWebviewDialog.CalendarWebviewDialogDelegate, NSScanDelegate, NSConnectionDelegate, NemuriScanUtil.NemuriScanDetailFetchListener, NSAutomaticOperationDelegate {
    //MARK : BLE Vars
    private boolean isNemuriScanInitiated = true;
    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            purgeBLE();
        }
    };
    private static boolean isActivityVisible;
    private Handler handler;
    public static Runnable runnable, runnable2 = null;
    private TimerUtils timerUtils;
    private NSManager nsManager;
    NemuriConstantsModel nsConstants;
    private SettingProvider settingProvider;
    //MARK END: BLE Vars
    TokenExpiredReceiver tokenExpiredReceiver = new TokenExpiredReceiver();
    NemuriScanModel nemuriScanDetail = new NemuriScanModel();
    public static DrawerLayout drawerLayout;
    ConstraintLayout content;
    final int MODE_DAILY = 111;
    final int MODE_WEEKLY = 222;
    public static Boolean isCalendarOrDetailVisible = false;
    boolean statusTimer = true;
    public static Boolean REMOTEACTIVE;
    // Webview Content -> 111 = for day, 222 = for week
    private int activeHome = MODE_DAILY;
    public SVProgressHUD progressDialog1, progressDialog2, progressDialog3;
    Disposable mDisposable;
    HomeService homeService;
    private AdsQSProvider adsQSProvider;
    private ScoreProvider scoreProvider;

    String currentStartDailyDate, currentEndDailyDate, currentStartWeeklyDate, currentEndWeeklyDate;

    @BindView(R.id.wvHome)
    WebView wvHome;

    @BindView(R.id.overlay)
    View overlay;

    @BindView(R.id.btnTimer)
    ImageButton btnTimer;

    @BindView(R.id.wvDetail)
    WebView wvDetail;

    @BindView(R.id.btnHamburger)
    ToggleButton btnHamburger;

    @BindView(R.id.tbDay)
    ToggleButton tbDay;

    @BindView(R.id.tbWeek)
    ToggleButton tbWeek;

    @BindView(R.id.listMenu)
    RecyclerView listMenu;

    @BindView(R.id.version_app)
    TextView textAppVersion;

    @BindView(R.id.dialogSettingAuto)
    ConstraintLayout dialogSettingAuto;

    @BindView(R.id.autoDialogContainer)
    ConstraintLayout autoDialogContainer;

    @BindView(R.id.btnAuto)
    Button btnAuto;

    @BindView(R.id.btnAutoWhite)
    Button btnAutoWhite;

    @BindView(R.id.btnHomeMain)
    ToggleButton btnHomeMain;
    
    @BindView(R.id.contentMainMenu)
    LinearLayout contentMainMenu;

    @BindView(R.id.content)
    ConstraintLayout contentLayout;

    @BindView(R.id.contentBirdie)
    LinearLayout contentBirdie;

    @BindView(R.id.txtMessageBirdie)
    TextView txtMessageBirdie;

    //region bed Only
    public Boolean alarmOpen = false;
    private Boolean tutorialHome = false;
    private Boolean tutorialRemote = false;
    private static int statusdialog = 1;
    //endregion

    private SwitchButton sbSetWake, sbSetSleep;
    private TextView tvTime;
    private ArrayList Hours, Minutes;
    private OptionsPickerView timePicker;
    private TextView textView19, textView20, textView21, textView22, textView23, tvTimer;
    private TextView textTittle, textSubtitle;
    private ImageView imgPull;
    private Button btnSave;
    private LinearLayout btnStopTimer;
    private TextView tvStartTimer;
    private long totalManual;
    private ImageView btnClose, btnCloseTimer;
    private RecyclerView recyclerView;
    private boolean didNavigateWebviewFaq = false;
    public static boolean ISFAQRUN;
    public static boolean ISHOMEFINISHED;
    public static boolean ISDETAILFINISHED;
    public static int HOME_TUTORIAL_SEQUENCE_REQ_CODE = 412;
    public static int HOME_QUESTIONNAIRE_SEQUENCE_REQ_CODE = 413;
    public static int HOME_FOREST_SEQUENCE_REQ_CODE = 414;
    public static int HOME_WEEKLY_SEQUENCE_REQ_CODE = 415;
    public static int HOME_SLEEP_RESET_SEQUENCE_REQ_CODE = 416;

    WebViewClient homeWebClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            ISHOMEFINISHED = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (activeHome == MODE_DAILY) {
                ViewUtil.injectJS(getApplicationContext(), wvHome, "day.js");
            } else {
                ViewUtil.injectJS(getApplicationContext(), wvHome, "weekly.js");
            }
            ISHOMEFINISHED = true;
            loadDetailContent();
        }
    };

    WebViewClient detailWebClient = new WebViewClient() {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            ISDETAILFINISHED = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            ViewUtil.injectJS(getApplicationContext(), wvDetail, "detail.js");
            ISDETAILFINISHED = true;
            hideProgress1();
            showBirdie();
        }
    };
    public static int drawerPosition = 0;
    public static Boolean selectmenuClick = false;

    @OnClick(R.id.btnHamburger)
    void openDrawer() {
        drawerLayout.openDrawer(Gravity.LEFT, true);
    }

    @OnClick(R.id.tbDay)
    void menuDay() {
        if (tbDay.isChecked()) {
            activeHome = MODE_DAILY;
            getHomeContent();
            tbWeek.setChecked(false);
        } else {
            tbDay.setChecked(true);
        }
    }

    @OnClick(R.id.tbWeek)
    void menuWeek() {
        if (tbWeek.isChecked()) {
            activeHome = MODE_WEEKLY;
            getHomeContent();
            tbDay.setChecked(false);
        } else {
            tbWeek.setChecked(true);
        }
    }

    @OnClick(R.id.btn_snore)
    void openSnore() {
        Intent intent = new Intent(this, SnoreActivity.class);
        startActivity(intent);
    }

    public void startTimingResetSleep(){
        if (alarmOpen)
            return;
        alarmOpen = true;

        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);

        setView();
    }
    
    @SuppressLint("CheckResult")
    @OnClick(R.id.btnTimer)
    void openTimerDialog() {
        btnHomeMain.setChecked(false);
        contentMainMenu.setVisibility(View.GONE);

        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
            LogUserAction.sendNewLog(userService, "INTERNET_CONNECTION_FAILED", "", "", "UI000500");
            DialogUtil.offlineDialog(HomeActivity.this,HomeActivity.this );
        }else{
            NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
            if(nemuriScanModel == null){
                LogUserAction.sendNewLog(userService, "STOP_SLEEP_FAILED", "", UserLogin.getUserLogin().getScanSerialNumber(), "UI000500");
                DialogUtil.createSimpleOkDialogLink(HomeActivity.this, "", LanguageProvider.getLanguage("UI000610C030"),
                        LanguageProvider.getLanguage("UI000610C043"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(HomeActivity.this, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000610C043");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        }, LanguageProvider.getLanguage("UI000610C031"), (dialogInterface, i) -> dialogInterface.dismiss());


            }else if(!isSleepResetActivityActive(this)){
                Intent intent = new Intent(this, SleepResetActivity.class);
                startActivity(intent);
            }
        }
    }

    @OnClick(R.id.btnRemote)
    void openRemoteDialog() {
        REMOTEACTIVE = false;
        if (!isCalendarOrDetailVisible) {
            isCalendarOrDetailVisible = true;
            if (statusdialog == 1) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
                dialog = new Dialog(this, android.R.style.Theme_Dialog);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_before_remote);
                dialog.getWindow().getAttributes().windowAnimations = R.style.popupDialogueAnimation;
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setOnKeyListener(((dialogInterface, i, keyEvent) -> {
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        statusdialog = 0;
                        isCalendarOrDetailVisible = false;
                        dialog.dismiss();
                    }
                    return false;
                }));
                TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                txtTitle.setText(LanguageProvider.getLanguage("UI000610C027"));

                TextView txtMessage = dialog.findViewById(R.id.txtMessage);
                txtMessage.setText(LanguageProvider.getLanguage("UI000610C006"));
                if (DisplayUtils.FONTS.bigFontStatus(HomeActivity.this)) {
                    txtMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                }
                TextView txtMessage2 = dialog.findViewById(R.id.txtMessage2);
                txtMessage2.setText(LanguageProvider.getLanguage("UI000610C048"));

                TextView txtMessage3 = dialog.findViewById(R.id.txtMessage3);
                txtMessage3.setText(LanguageProvider.getLanguage("UI000610C049"));

                Button btnRemote = dialog.findViewById(R.id.btnGoRemoteAct);
                btnRemote.setText(LanguageProvider.getLanguage("UI000610C007"));

                btnRemote.setOnClickListener(v -> {
                    statusdialog = 0;
                    initTutorial();
                    tutorialRemote = false;
                    dialog.dismiss();
                });

                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog.show();
            } else {
                initTutorial();
                tutorialRemote = false;
            }

        }
    }

    private boolean isFirstRun = true;
    private TimerViewModel mViewModel;
    public static HomeActivity activity;
    private FormPolicyProvider formPolicyProvider;
    private MaxRowProvider maxRowProvider;
    public boolean isOfflineLaunchFired = false;
    View viewDialogScore;
    BedApplication bedApplication;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bedApplication = (BedApplication) (getApplicationContext());
        if (DisplayUtils.FONTS.needRestart(this)) {
            DisplayUtils.FONTS.needRestart(this, false);
        }
        timerUtils = new TimerUtils(this);
        isOfflineLaunchFired = false;
        isActivityVisible = true;
        ButterKnife.bind(this);
        StatusLogin.clear();
        StatusLogin statusLogin = new StatusLogin();
        statusLogin.statusLogin = true;
        statusLogin.insert();
        ApiClient.LogData.setLogData(HomeActivity.this, UserLogin.getUserLogin());

        Logx("ApiClientLog " + "OnCreate",
                "lg_company_id : " + UserLogin.getUserLogin().getCompanyId() +
                        " | lg_user_id : " + UserLogin.getUserLogin().getId() +
                        " | lg_nickname : " + UserLogin.getUserLogin().getNickname() +
                        " | lg_email : " + UserLogin.getUserLogin().getEmail() +
                        " | lg_ns_serial_number : " + "" +
                        " | lg_device_type : " + new AndroidSystemUtil().getDeviceType() +
                        " | lg_os_version : " + new AndroidSystemUtil().getOsVersion() +
                        " | lg_app_type : " + "2"
        );
        statusdialog = 1;
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        activity = this;
        homeService = ApiClient.getClient(getApplicationContext()).create(HomeService.class);
        mViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);

        adsQSProvider = new AdsQSProvider(this);
        scoreProvider = new ScoreProvider(this);
        formPolicyProvider = new FormPolicyProvider(this);
        maxRowProvider = new MaxRowProvider(this);
        settingProvider = new SettingProvider(this);

        drawerPosition = 0;
        selectmenuClick = false;
        isCalendarOrDetailVisible = false;

        drawerLayout = findViewById(R.id.drawerLayout);
        content = findViewById(R.id.content);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.setDrawerElevation(0f);
        ISFAQRUN = false;
        getfcmtoken();
        getInitialSetting(UserLogin.getUserLogin().getId());

        initDialogAlarmOff();

        String[] menuText = new String[]{
                LanguageProvider.getLanguage("UI000800C001"),
                LanguageProvider.getLanguage("UI000800C002"),
                LanguageProvider.getLanguage("UI000800C009"),
                LanguageProvider.getLanguage("UI000800C008"),
                LanguageProvider.getLanguage("UI000800C003"),
                LanguageProvider.getLanguage("UI000800C004"),
                LanguageProvider.getLanguage("UI000800C005"),
                LanguageProvider.getLanguage("UI000800C006"),
                LanguageProvider.getLanguage("UI000800C007")
        };

        int[] menuIcon = new int[]{
                R.drawable.icon1,
                R.drawable.icon2,
                R.drawable.icon9,
                R.drawable.icon8,
                R.drawable.icon3,
                R.drawable.icon4,
                R.drawable.icon5,
                R.drawable.icon6,
                R.drawable.icon7
        };


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listMenu.setLayoutManager(layoutManager);

        HomeMenuAdapter adapter = new HomeMenuAdapter(getApplicationContext(), menuText, menuIcon);
        listMenu.setAdapter(adapter);

        //listMenu.addOnItemTouchListener(selectMenu());
        listMenu.addOnItemTouchListener(selectMenu());

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX);
            }

            @Override
            public void onDrawerOpened(View view) {
                btnHamburger.setChecked(false);
            }

            @Override
            public void onDrawerClosed(View view) {
                btnHamburger.setChecked(true);
            }


        };

        initTimePicker();

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //region Tutorial : Last Update Angga Fachri Hamdani @ 2018-11-24
//        Boolean openTutorialHome = TutorialShowModel.get().getBedShowed();
        Boolean openTutorialRemote = TutorialShowModel.get().getRemoteShowed();
//        tutorialHome = openTutorialHome;
        tutorialRemote = openTutorialRemote;
//        if (tutorialHome) {
//            Intent i = new Intent(HomeActivity.this, TutorialActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            i.putExtra("type", 0);
//            i.putExtra("isOtherShowed", true);
//            startActivityForResult(i, HOME_TUTORIAL_REQ_CODE);
//            tutorialHome = false;
//        } else {
//            AppUpdaterUtil.checkVersion(ANDROID_APPLICATION_TYPE_BED, homeService, activity);
//        }
        //endregion Tutorial : Last Update Angga Fachri Hamdani @ 2018-11-24
        initWebView();
        showTnCAfterUpdate();
        textAppVersion.setText("v " + BuildConfig.VERSION_NAME);
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Boolean isFromAlarm = Boolean.parseBoolean(intent.getExtras().getString("isFromAlarm") == null ? "false" : intent.getExtras().getString("isFromAlarm"));
            Boolean isAutoDrive = Boolean.parseBoolean(intent.getExtras().getString("isAutoDrive") == null ? "false" : intent.getExtras().getString("isAutoDrive"));

            Boolean isFromNotifService = intent.getAction() != null && intent.getAction().equals("onNotif");
            Boolean isApprovalReminder = intent.getAction() != null && intent.getAction().equals("APPROVAL_REMINDER");
            Boolean isMonitoringRequest = intent.getAction() != null && intent.getAction().equals("MONITORING_REQUEST");
            Boolean isBirdie = intent.getAction() != null && intent.getAction().equals("BIRDIE_BUTTON");
            Boolean isSleepAlarm = intent.getAction() != null && intent.getAction().equals("SLEEP_ALARM");

            if (isFromNotifService) {
                if (allowAlertNotif) {
                    String title = LanguageProvider.getLanguage("UI000802C010");
                    String msg = LanguageProvider.getLanguage("UI000802C011");
                    showNotifAlert(title, msg);
                }
            } else if (isApprovalReminder) {
                if (UserLogin.isLogin()) {
                    String title = LanguageProvider.getLanguage("UI000802C010");
                    String msg = LanguageProvider.getLanguage("UI000802C011");
                    showNotifAlert(title, msg);
                } else {
                    Intent i = new Intent(HomeActivity.this, LoginEmailActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();
                }
            } else if (isMonitoringRequest) {
                if (UserLogin.isLogin()) {
                    String title = LanguageProvider.getLanguage("UI000802C010");
                    String msg = LanguageProvider.getLanguage("UI000802C011");
                    showNotifAlert(title, msg);
                } else {
                    Intent i = new Intent(HomeActivity.this, LoginEmailActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();
                }
            } else if (isBirdie) {
                if (UserLogin.isLogin()) {
                    String sender = intent.getExtras().getString("sender");
                    if (sender != null && !sender.isEmpty()) {
                        SenderBirdieModel.updateByName(sender);
                        showBirdie();
                    }
                } else {
                    Intent i = new Intent(HomeActivity.this, LoginEmailActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();
                }
            } else if (isSleepAlarm) {
                if (UserLogin.isLogin()) {
                    DialogUtil.createCustomYesNo(activity, "", LanguageProvider.getLanguage(""), LanguageProvider.getLanguage("UI000802C019"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }, LanguageProvider.getLanguage("UI000802C020"), (dialogInterface, i) -> dialogInterface.dismiss());
                } else {
                    Intent i = new Intent(HomeActivity.this, LoginEmailActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();
                }
            }
        }

        checkSbState();
        ContentProvider.refreshContent(this, homeService, new ContentProvider.ContentRefreshListener() {
            @Override
            public void onContentSliderRefreshSuccess() {

            }

            @Override
            public void onContentLanguageRefreshSuccess() {

            }

            @Override
            public void onContentRefreshFailed(String message) {

            }

            @Override
            public void onContentRefreshNetworkFailure(Throwable t) {

            }

            @Override
            public void onContentHomeRefreshSuccess() {

            }
        });
        
        //make sure forest && weekly score model empty before questionnaire is answered
        LogUserAction.sendNewLog(userService,"WEEKLY_SCORE_TRACK","onCreare clear","","UI000502");
        WeeklyScoreReviewModel.clear();
        ForestModel.clear();

        initHomeSequence();

        btnHomeMain.setChecked(false);
        contentMainMenu.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnHomeMain)
    void toggleBtnHomeMain(){
        Boolean checked = btnHomeMain.isChecked()?true:false;

        if(checked){
            contentMainMenu.setVisibility(View.VISIBLE);
        }else {
            contentMainMenu.setVisibility(View.GONE);
        }

    }

    HomeSequenceManager homeSequenceManager;
    Boolean newsShowed = false;
    private void initHomeSequence(){
        homeSequenceManager = new HomeSequenceManager(this);

        //1.tutorial
        TutorialSequence tutorialSequence = new TutorialSequence(homeSequenceManager, new SequenceDelegate() {
            @Override
            public void onExecute() {

            }

            @Override
            public void onEnd() {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                    //fix bug chart not loaded in android 8
                    getHomeContent();
                }
            }
        });
        homeSequenceManager.enque(tutorialSequence);

        //2.news
        NewsSequence newsSequence = new NewsSequence(homeSequenceManager, new SequenceDelegate() {
            @Override
            public void onExecute() {
                enableHamburgerMenu(true);
            }

            @Override
            public void onEnd() {
                enableHamburgerMenu(false);
            }
        });
        homeSequenceManager.enque(newsSequence);

        //3.questionnaire
        QuestionnaireSequence questionnaireSequence = new QuestionnaireSequence(homeSequenceManager,null);
        homeSequenceManager.enque(questionnaireSequence);

        //4.forest sequence
        ForestSequence forestSequence = new ForestSequence(homeSequenceManager, null);
        homeSequenceManager.enque(forestSequence);

        //5.weekly score advice
        WeeklyScoreReviewSequence weeklySequence = new WeeklyScoreReviewSequence(homeSequenceManager, null);
        homeSequenceManager.enque(weeklySequence);

        //6.sleep reset sequence
        SleepResetSequence sleepResetSequence = new SleepResetSequence(homeSequenceManager, null);
        homeSequenceManager.enque(sleepResetSequence);

        //7.app update
        AppUpdateSequence appUpdateSequence = new AppUpdateSequence(homeSequenceManager, null);
        homeSequenceManager.enque(appUpdateSequence);

        homeSequenceManager.next();
    }
    private void initHomeSequenceFromBg(){
        //make sure forest && weekly score model empty before questionnaire is answered
        LogUserAction.sendNewLog(userService,"WEEKLY_SCORE_TRACK","initHomeSequenceFromBg clear","","UI000502");
        WeeklyScoreReviewModel.clear();
        ForestModel.clear();

        homeSequenceManager = new HomeSequenceManager(this);

        NewsSequence newsSequence = new NewsSequence(homeSequenceManager, new SequenceDelegate() {
            @Override
            public void onExecute() {
                enableHamburgerMenu(true);
            }

            @Override
            public void onEnd() {
                enableHamburgerMenu(false);
            }
        });
        homeSequenceManager.enque(newsSequence);
        
        QuestionnaireSequence questionnaireSequence = new QuestionnaireSequence(homeSequenceManager,null);
        homeSequenceManager.enque(questionnaireSequence);

        ForestSequence forestSequence = new ForestSequence(homeSequenceManager, null);
        homeSequenceManager.enque(forestSequence);

        WeeklyScoreReviewSequence weeklySequence = new WeeklyScoreReviewSequence(homeSequenceManager, null);
        homeSequenceManager.enque(weeklySequence);

        SleepResetSequence sleepResetSequence = new SleepResetSequence(homeSequenceManager, null);
        homeSequenceManager.enque(sleepResetSequence);

        homeSequenceManager.next();
    }
    private void initDialogScore() {

        viewDialogScore = getLayoutInflater().inflate(R.layout.dialog_score, null);
        viewDialogScore.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        content.addView(viewDialogScore);

        CardView contentScore = viewDialogScore.findViewById(R.id.contentScore);
        contentScore.setAnimation(AnimateUtils.explode((() -> runOnUiThread(() -> {
            ImageView imgClose = viewDialogScore.findViewById(R.id.img_close);
            ImageView imgScore = viewDialogScore.findViewById(R.id.img_score);
            Button btnOpneScore = viewDialogScore.findViewById(R.id.btn_open_score);

            btnOpneScore.setOnClickListener(view -> {
                viewDialogScore.setVisibility(View.GONE);
            });

            imgClose.setOnClickListener(view -> {
                viewDialogScore.setVisibility(View.GONE);
            });
        }))));

//        viewDialogScore.setVisibility(View.GONE);
    }


    private void sendPendingLog() {
        LogProvider.logBedTemplateChange(HomeActivity.this, null, null, null);
        LogProvider.logMattressTemplateChange(HomeActivity.this, null, null);
        LogProvider.logBedSettingChange(HomeActivity.this, 0, 0, 0, 0, 0, true);
        LogUserAction.sendPendingLogAction(HomeActivity.this);
        RemoteActivity.sendPendingApplyRemoteSetting(HomeActivity.this);
        SnoringProvider.sendPendingSnoringAnalysis(HomeActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 850) {
            getHomeFromAPI();
        } else if (requestCode == HOME_TUTORIAL_SEQUENCE_REQ_CODE) {
            if(homeSequenceManager != null && homeSequenceManager.getCurrentSequence() != null && homeSequenceManager.getCurrentSequence().getClass() == TutorialSequence.class){
                homeSequenceManager.getCurrentSequence().end();
            }
        }else if (requestCode == HOME_QUESTIONNAIRE_SEQUENCE_REQ_CODE) {
            if(homeSequenceManager != null && homeSequenceManager.getCurrentSequence() != null && homeSequenceManager.getCurrentSequence().getClass() == QuestionnaireSequence.class){
                homeSequenceManager.getCurrentSequence().end();
            }
        }else if (requestCode == HOME_FOREST_SEQUENCE_REQ_CODE) {
            if(homeSequenceManager != null && homeSequenceManager.getCurrentSequence() != null && homeSequenceManager.getCurrentSequence().getClass() == ForestSequence.class){
                homeSequenceManager.getCurrentSequence().end();
            }
        }else if (requestCode == HOME_WEEKLY_SEQUENCE_REQ_CODE) {
            if(homeSequenceManager != null && homeSequenceManager.getCurrentSequence() != null && homeSequenceManager.getCurrentSequence().getClass() == WeeklyScoreReviewSequence.class){
                homeSequenceManager.getCurrentSequence().end();
            }
        }
        else if (requestCode == HOME_SLEEP_RESET_SEQUENCE_REQ_CODE) {
            if(homeSequenceManager != null && homeSequenceManager.getCurrentSequence() != null && homeSequenceManager.getCurrentSequence().getClass() == SleepResetSequence.class){
                homeSequenceManager.getCurrentSequence().end();
            }
        }
    }

    private void showTnCAfterUpdate() {
        if (VersionModel.getAll().size() == 1) {
            VersionModel oldVersion = VersionModel.getAll().get(0);
            boolean isAfterUpdate = false;
            if (oldVersion.getMajor() < BuildConfig.VERSION_MAJOR) {
                isAfterUpdate = true;
            } else if (oldVersion.getMinor() < BuildConfig.VERSION_MINOR &&
                    oldVersion.getMajor() <= BuildConfig.VERSION_MAJOR) {
                isAfterUpdate = true;
            } else if (oldVersion.getRevision() < BuildConfig.VERSION_REVISION &&
                    oldVersion.getMinor() <= BuildConfig.VERSION_MINOR &&
                    oldVersion.getMajor() <= BuildConfig.VERSION_MAJOR) {
                isAfterUpdate = true;
            }
            if (isAfterUpdate) {
                Intent intent = new Intent(HomeActivity.this, TncAppUpdateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("isHome", true);
                startActivityForResult(intent, 850);
            } else {
                if (getIntent() != null && getIntent().getAction() != null) {
                    Boolean isFromNotifService = getIntent().getAction() != null && getIntent().getAction().equals("onNotif");
                    Boolean isApprovalReminder = getIntent().getAction() != null && getIntent().getAction().equals("APPROVAL_REMINDER");
                    Boolean isMonitoringRequest = getIntent().getAction() != null && getIntent().getAction().equals("MONITORING_REQUEST");
                    Boolean isSleepAlarm = getIntent().getAction() != null && getIntent().getAction().equals("SLEEP_ALARM");
                    if (isFromNotifService || isApprovalReminder || isMonitoringRequest || isSleepAlarm) {
                        getHomeContent();
                    } else {
                        getHomeFromAPI();
                    }
                } else {
                    getHomeContent();
                }
            }

        } else {
            //Insert OldVersion
            VersionModel.clear();
            VersionModel versionModel = new VersionModel();
            versionModel.setMajor(BuildConfig.VERSION_MAJOR);
            versionModel.setMinor(BuildConfig.VERSION_MINOR);
            versionModel.setRevision(BuildConfig.VERSION_REVISION);
            versionModel.setTNCRead(false);
            versionModel.insert();
            getHomeFromAPI();

        }
    }

    private void initDialogAlarmOff() {
        sbSetWake = dialogSettingAuto.findViewById(R.id.sbSetWake);
        sbSetSleep = dialogSettingAuto.findViewById(R.id.sbSetSleep);

        textView21 = dialogSettingAuto.findViewById(R.id.textView21);
        textView21.setText(LanguageProvider.getLanguage("UI000505C006"));
        textView21.setOnClickListener(view -> {
            Intent intent4 = new Intent(HomeActivity.this, AutomaticOperationActivity.class);
            intent4.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent4.putExtra("FROM_DRIVER", true);
            startActivity(intent4);

            if (isNemuriScanInitiated && nsManager != null) {
                purgeBLE();
            }
        });
        textView22 = dialogSettingAuto.findViewById(R.id.textView22);
        textView22.setText(LanguageProvider.getLanguage("UI000505C001"));

        textView23 = dialogSettingAuto.findViewById(R.id.textView23);
        textView23.setText(LanguageProvider.getLanguage("UI000505C002"));

        textView19 = dialogSettingAuto.findViewById(R.id.textView19);
        textView19.setText(LanguageProvider.getLanguage("UI000505C003"));

        textView20 = dialogSettingAuto.findViewById(R.id.textView20);
        textView20.setText(LanguageProvider.getLanguage("UI000505C004"));

        btnSave = dialogSettingAuto.findViewById(R.id.btnSave);
        btnSave.setText(LanguageProvider.getLanguage("UI000505C005"));
        btnSave.setOnClickListener(view -> updateSetting());

        tvTime = dialogSettingAuto.findViewById(R.id.tvTime);
        imgPull = dialogSettingAuto.findViewById(R.id.imgPull);

        tvTime.setTextColor(this.getColor(R.color.slight_blue));
        tvTime.setOnClickListener(onTvTimeClicked());
        imgPull.setOnClickListener(onTvTimeClicked());

        btnClose = dialogSettingAuto.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(v -> {
            getInitialSetting(UserLogin.getUserLogin().getId());
            alarmOpen = false;
            dialogSettingAuto.setVisibility(View.GONE);
            btnHamburger.setEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            if (isNemuriScanInitiated && nsManager != null) {
                purgeBLE();
            }
            hideProgress1();
        });
    }

    private View.OnClickListener onTvTimeClicked() {
        return view -> {
            ViewUtil.hideKeyboardFrom(getApplicationContext(), view);
            if (sbSetWake.isChecked()) {
                timePicker.show();
            }
        };
    }

    private void initTimePicker() {
        timePicker = new OptionsPickerBuilder(HomeActivity.this, onTimeSelected())
                .setBackgroundId(0)
                .setCyclic(true, true, false)
                .setOutSideCancelable(false)
                .setCancelText(LanguageProvider.getLanguage("UI000505C007"))
                .setSubmitText(LanguageProvider.getLanguage("UI000505C008"))
                .build();

        Hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                Hours.add("0" + i);
            } else {
                Hours.add("" + i);
            }
        }

        Minutes = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            if (i < 10) {
                Minutes.add("0" + i);
            } else {
                Minutes.add("" + i);
            }
        }

        timePicker.setNPicker(Hours, Minutes, null);

    }

    private OnOptionsSelectListener onTimeSelected() {
        return (options1, options2, options3, v) -> {
            String text = Hours.get(options1).toString() + ":" + Minutes.get(options2).toString();
            tvTime.setText(text);
        };
    }


    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings webSettings = wvHome.getSettings();
        wvHome.setScrollbarFadingEnabled(true);
        wvHome.getSettings().setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);

        WebViewController webViewController = new WebViewController();
        wvHome.addJavascriptInterface(webViewController, "controller");
        wvHome.setWebViewClient(homeWebClient);

        webSettings = wvDetail.getSettings();
        wvHome.getSettings().setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        wvDetail.addJavascriptInterface(webViewController, "controller");

        wvDetail.setWebViewClient(detailWebClient);
        wvDetail.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        wvHome.setWebContentsDebuggingEnabled(true);
        wvDetail.setWebContentsDebuggingEnabled(true);
        WebViewUtil.enableHardwareAcceleration(wvHome);
        WebViewUtil.enableHardwareAcceleration(wvDetail);

        WebViewUtil.fixWebViewFonts(wvHome);
        WebViewUtil.fixWebViewFonts(wvDetail);
    }

    @Override
    public void onResume() {
        ISFAQRUN = false;
        super.onResume();
        if(isSleepResetActivityActive(this)){
            return;
        }
        bedApplication.setCurrentActivity(this);
        isActivityVisible = true;
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        ActivityModel.setHomeResume();
        alarmOpen = false;
        showBirdie();
        //Close Detail If Show
        if(!didNavigateWebviewFaq) {
            enableNavigationUI();
        }else{
            didNavigateWebviewFaq = false;
        }
        //end
        drawerLayout.closeDrawer(GravityCompat.START, false);
        selectmenuClick = false;
        setView();
        if (UserLogin.getUserLogin() != null) {
            try {
                LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), "open_screen", "UI000500", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber(), "UI000500");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        AlarmsQuizModule.run(this);
        checkScoreStatus();
        sendPendingLog();
        if (!isFirstRun) {
            //disable for feb release
//            getNSFWVersion();

            btnHomeMain.setChecked(false);
            contentMainMenu.setVisibility(View.GONE);

            if(homeSequenceManager != null && homeSequenceManager.ended()){
                initHomeSequenceFromBg();
            }
        } else {
            isFirstRun = false;
        }

        if (nsManager != null) {
            nsManager.setDelegate(this);
        }

        //get mattress setting
        MattressSettingProvider.fetchMattressSetting(this, (isSuccess, result) -> {}, 0);
        //refresh ASA connectivity
        NemuriScanUtil.fetchSpec(this, nemuriScanDetailModel -> {});

        //refresh form policy,device template & home setting
        maxRowProvider.getMaxRow(maxRowModel -> { });
        formPolicyProvider.getFormPolicy(formPolicyModel -> {});
        DeviceTemplateProvider.getDeviceTemplate(this, new DeviceTemplateProvider.DeviceTemplateFetchListener() {
            @Override
            public void onDeviceTemplateFetched(List<DeviceTemplateMattressModel> mattressModels, List<DeviceTemplateBedModel> bedModels, List<DeviceTemplateMattressModel> mattressModelDefaults, List<DeviceTemplateBedModel> bedModelDefaults, NemuriConstantsModel nemuriConstantsModel) {
                nsConstants = nemuriConstantsModel;
            }
        },UserLogin.getUserLogin().getId(), nemuriScanDetail.getInfoType());
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearReferences();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
        ActivityModel.clear();
        purgeBLE();
        isActivityVisible = false;
    }

    Dialog dialog;

    @OnClick(R.id.btnAutoWhite)
    void showAutoDialog0() {
        if (sbSetWake.isChecked()) {
            imgPull.setVisibility(View.VISIBLE);
            tvTime.setTextColor(HomeActivity.this.getColor(R.color.colorPrimaryDark));
            tvTime.setEnabled(true);
        } else {
            imgPull.setVisibility(View.INVISIBLE);
            tvTime.setTextColor(HomeActivity.this.getColor(R.color.slight_blue));
            tvTime.setEnabled(false);
        }

        if (alarmOpen)
            return;

        alarmOpen = true;

        autoDialogContainer.setAnimation(AnimateUtils.explode((() -> runOnUiThread(() -> {
            sbSetSleep.setOnCheckedChangeListener((view, isChecked) -> {
                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                    DialogUtil.offlineDialog(HomeActivity.this, getApplicationContext());
                    btnSave.setEnabled(false);
                    tvTime.setEnabled(false);
                    tvTime.setTextColor(getColor(R.color.slight_blue));

                    imgPull.setVisibility(View.INVISIBLE);
                    sbSetWake.setEnabled(false);
                    sbSetSleep.setEnabled(false);
                }
            });

            sbSetWake.setOnCheckedChangeListener((view, isChecked) -> {
                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                    DialogUtil.offlineDialog(HomeActivity.this, getApplicationContext());

                    btnSave.setEnabled(false);
                    tvTime.setEnabled(false);
                    tvTime.setTextColor(getColor(R.color.slight_blue));

                    imgPull.setVisibility(View.INVISIBLE);
                    sbSetWake.setEnabled(false);
                    sbSetSleep.setEnabled(false);
                } else {
                    if (isChecked) {
                        imgPull.setVisibility(View.VISIBLE);
                        tvTime.setTextColor(HomeActivity.this.getColor(R.color.colorPrimaryDark));
                        tvTime.setEnabled(true);
                    } else {
                        imgPull.setVisibility(View.INVISIBLE);
                        tvTime.setTextColor(HomeActivity.this.getColor(R.color.slight_blue));
                        tvTime.setEnabled(false);
                    }
                }
            });

            btnHamburger.setEnabled(false);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


            if (!NetworkUtil.isNetworkConnected(this)) {

                DialogUtil.offlineDialog(this, getApplicationContext());

                btnSave.setEnabled(false);
                tvTime.setEnabled(false);
                tvTime.setTextColor(this.getColor(R.color.slight_blue));

                imgPull.setVisibility(View.INVISIBLE);
                sbSetWake.setEnabled(false);
                sbSetSleep.setEnabled(false);
            } else {
                btnSave.setEnabled(true);
                sbSetWake.setEnabled(true);
                sbSetSleep.setEnabled(true);

                nsManager = NSManager.getInstance(this, this);
                checkNSDetail();
            }
            setView();
        }))));
        dialogSettingAuto.setVisibility(View.VISIBLE);


    }

    @OnClick(R.id.btnAuto)
    void showAutoDialog() {
        if (sbSetWake.isChecked()) {
            imgPull.setVisibility(View.VISIBLE);
            tvTime.setTextColor(HomeActivity.this.getColor(R.color.colorPrimaryDark));
            tvTime.setEnabled(true);
        } else {
            imgPull.setVisibility(View.INVISIBLE);
            tvTime.setTextColor(HomeActivity.this.getColor(R.color.slight_blue));
            tvTime.setEnabled(false);
        }

        if (alarmOpen)
            return;
        alarmOpen = true;

        autoDialogContainer.setAnimation(AnimateUtils.explode((() -> runOnUiThread(() -> {
            sbSetSleep.setOnCheckedChangeListener((view, isChecked) -> {
                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                    DialogUtil.offlineDialog(HomeActivity.this, getApplicationContext());

                    btnSave.setEnabled(false);
                    tvTime.setEnabled(false);
                    tvTime.setTextColor(getColor(R.color.slight_blue));

                    imgPull.setVisibility(View.INVISIBLE);
                    sbSetWake.setEnabled(false);
                    sbSetSleep.setEnabled(false);
                }
            });

            sbSetWake.setOnCheckedChangeListener((view, isChecked) -> {
                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                    DialogUtil.offlineDialog(HomeActivity.this, getApplicationContext());

                    btnSave.setEnabled(false);
                    tvTime.setEnabled(false);
                    tvTime.setTextColor(getColor(R.color.slight_blue));

                    imgPull.setVisibility(View.INVISIBLE);
                    sbSetWake.setEnabled(false);
                    sbSetSleep.setEnabled(false);
                } else {
                    if (isChecked) {
                        imgPull.setVisibility(View.VISIBLE);
                        tvTime.setTextColor(HomeActivity.this.getColor(R.color.colorPrimaryDark));
                        tvTime.setEnabled(true);
                    } else {
                        imgPull.setVisibility(View.INVISIBLE);
                        tvTime.setTextColor(HomeActivity.this.getColor(R.color.slight_blue));
                        tvTime.setEnabled(false);
                    }
                }
            });

            btnHamburger.setEnabled(false);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            if (!NetworkUtil.isNetworkConnected(this)) {
                DialogUtil.offlineDialog(this, getApplicationContext());
                btnSave.setEnabled(false);
                tvTime.setEnabled(false);
                tvTime.setTextColor(this.getColor(R.color.slight_blue));
                imgPull.setVisibility(View.INVISIBLE);
                sbSetWake.setEnabled(false);
                sbSetSleep.setEnabled(false);
            } else {
                btnSave.setEnabled(true);
                sbSetWake.setEnabled(true);
                sbSetSleep.setEnabled(true);
                nsManager = NSManager.getInstance(this, this);
                checkNSDetail();
            }
            setView();
        }))));
        dialogSettingAuto.setVisibility(View.VISIBLE);
    }

    private void updateSetting() {
        try {
            boolean isSetSleep = sbSetSleep.isChecked();
            boolean isSetWake = sbSetWake.isChecked();
            String alarmTime = tvTime.getText().toString();
            JSONArray settings = new JSONArray();
            JSONObject jobj = new JSONObject();
            jobj.put("key", "automatic_operation_sleep_active");
            jobj.put("value", isSetSleep);
            SettingModel.saveSetting("automatic_operation_sleep_active", String.valueOf(isSetSleep));
            settings.put(jobj);
            Calendar calendar = Calendar.getInstance();
            switch (AlarmsAutoScheduler.nextAlarm(String.valueOf(alarmTime))) {
                case Calendar.SUNDAY:
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_sunday_active");
                    jobj.put("value", isSetWake);
                    settings.put(jobj);
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_sunday_time");
                    jobj.put("value", alarmTime);
                    settings.put(jobj);
                    SettingModel.saveSetting("automatic_operation_wakeup_sunday_active", String.valueOf(isSetWake));
                    SettingModel.saveSetting("automatic_operation_wakeup_sunday_time", String.valueOf(alarmTime));
                    break;
                case Calendar.MONDAY:
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_monday_active");
                    jobj.put("value", isSetWake);
                    settings.put(jobj);
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_monday_time");
                    jobj.put("value", alarmTime);
                    settings.put(jobj);
                    SettingModel.saveSetting("automatic_operation_wakeup_monday_active", String.valueOf(isSetWake));
                    SettingModel.saveSetting("automatic_operation_wakeup_monday_time", String.valueOf(alarmTime));
                    break;
                case Calendar.TUESDAY:
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_tuesday_active");
                    jobj.put("value", isSetWake);
                    settings.put(jobj);
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_tuesday_time");
                    jobj.put("value", alarmTime);
                    settings.put(jobj);
                    SettingModel.saveSetting("automatic_operation_wakeup_tuesday_active", String.valueOf(isSetWake));
                    SettingModel.saveSetting("automatic_operation_wakeup_tuesday_time", String.valueOf(alarmTime));
                    break;
                case Calendar.WEDNESDAY:
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_wednesday_active");
                    jobj.put("value", isSetWake);
                    settings.put(jobj);
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_wednesday_time");
                    jobj.put("value", alarmTime);
                    settings.put(jobj);
                    SettingModel.saveSetting("automatic_operation_wakeup_wednesday_active", String.valueOf(isSetWake));
                    SettingModel.saveSetting("automatic_operation_wakeup_wednesday_time", String.valueOf(alarmTime));
                    break;
                case Calendar.THURSDAY:
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_thursday_active");
                    jobj.put("value", isSetWake);
                    settings.put(jobj);
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_thursday_time");
                    jobj.put("value", alarmTime);
                    settings.put(jobj);
                    SettingModel.saveSetting("automatic_operation_wakeup_thursday_active", String.valueOf(isSetWake));
                    SettingModel.saveSetting("automatic_operation_wakeup_thursday_time", String.valueOf(alarmTime));
                    break;
                case Calendar.FRIDAY:
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_friday_active");
                    jobj.put("value", isSetWake);
                    settings.put(jobj);
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_friday_time");
                    jobj.put("value", alarmTime);
                    settings.put(jobj);
                    SettingModel.saveSetting("automatic_operation_wakeup_friday_active", String.valueOf(isSetWake));
                    SettingModel.saveSetting("automatic_operation_wakeup_friday_time", String.valueOf(alarmTime));
                    break;
                case Calendar.SATURDAY:
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_saturday_active");
                    jobj.put("value", isSetWake);
                    settings.put(jobj);
                    jobj = new JSONObject();
                    jobj.put("key", "automatic_operation_wakeup_saturday_time");
                    jobj.put("value", alarmTime);
                    settings.put(jobj);
                    SettingModel.saveSetting("automatic_operation_wakeup_saturday_active", String.valueOf(isSetWake));
                    SettingModel.saveSetting("automatic_operation_wakeup_saturday_time", String.valueOf(alarmTime));
                    break;
            }
            showProgress1();
            mDisposable = userService.saveSetting(UserLogin.getUserLogin().getId(), settings.toString(), 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                        @Override
                        public void onSuccess(BaseResponse response) {

                            if (response.isSucces()) {
                                hideProgress1();
                                alarmOpen = false;
                                dialogSettingAuto.setVisibility(View.GONE);

                                if (!isSetSleep && !isSetWake) {
                                    btnAutoWhite.setVisibility(View.VISIBLE);
                                    btnAuto.setVisibility(View.GONE);
                                } else {
                                    btnAutoWhite.setVisibility(View.GONE);
                                    btnAuto.setVisibility(View.VISIBLE);
                                }
                                if (BluetoothUtil.isBluetoothEnable()  && PermissionUtil.hasLocationPermissions(HomeActivity.this) && PermissionUtil.isLocationServiceEnable(HomeActivity.this)) {
                                    tryToConnectBLE();
                                }
                                setAllAlarms(HomeActivity.this);
                            } else {
                                hideProgress1();
                            }

                            btnHamburger.setEnabled(true);
                            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideProgress1();
                            if (MultipleDeviceUtil.checkForceLogout(e)) {
                                MultipleDeviceUtil.sendBroadCast(HomeActivity.this);
                            } else {
                                DialogUtil.serverFailed(HomeActivity.this, "UI000802C077", "UI000802C078", "UI000802C079", "UI000802C080");
                            }

                            btnHamburger.setEnabled(true);
                            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private RecyclerItemClickListener selectMenu() {
        return new RecyclerItemClickListener(HomeActivity.this, listMenu, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                runOnUiThread(() -> drawerLayout.closeDrawer(GravityCompat.START));
                setPage(view, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    private void setPage(View view, int position) {
        drawerLayout.closeDrawer(GravityCompat.START, false);
        if (!alarmOpen) {
            Intent intent = null;
            alarmOpen = true;
            switch (position) {
                case 0:
                    intent = new Intent(HomeActivity.this, AutomaticOperationActivity.class);
                    break;
                case 1:
                    intent = new Intent(HomeActivity.this, RealtimeMonitorDialog.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intent.putExtra("html_content", DashboardModel.getByKey(0).getContent());
                    break;
                case 2:
                    showLoading();
                    if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                        alarmOpen = false;
                        DialogUtil.offlineDialog(HomeActivity.this, getApplicationContext());
                        hideLoading();
                    }else{
                        ForestProvider.getForestCalculation(new ForestProvider.ForestListener() {
                            @Override
                            public void onCalculateForestScoreSuccess() {
                                hideLoading();
                                Intent forestIntent = new Intent(HomeActivity.this, ForestDialog.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                forestIntent.putExtra("from_menu",true);
                                startActivity(forestIntent);
                            }

                            @Override
                            public void onCalculateForestScoreError(boolean error, Throwable e) {
                                alarmOpen = false;
                                hideLoading();
                                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                    DialogUtil.offlineDialog(HomeActivity.this, getApplicationContext());
                                } else {
                                    DialogUtil.serverFailed(HomeActivity.this, "UI000802C145", "UI000802C146", "UI000802C147", "UI000802C148");
                                }
                            }
                        },1,0);
                    }
                    break;
                case 3:
                    intent = new Intent(HomeActivity.this, TopNewsListActivity.class);
                    break;
                case 4:
                    intent = new Intent(HomeActivity.this, MyAccountActivity.class);
                    break;
                case 5:
                    intent = new Intent(HomeActivity.this, DeviceListActivity.class);
                    break;
                case 6:
                    intent = new Intent(HomeActivity.this, SettingActivity.class);
                    break;
                case 7:
                    intent = new Intent(HomeActivity.this, FaqActivity.class);
                    break;
                case 8:
                    intent = new Intent(HomeActivity.this, HomeTNCActivity.class);
                    break;
                default:

            }
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }

        }
    }

    private void getHomeContent() {
        loadHomeContent();
    }

    private void loadHomeContent() {
        try {
            String homeHTMLContent;
            // not sure why but need to add space to different content T_T
            switch (activeHome) {
                case MODE_DAILY:
                    homeHTMLContent = "" + DashboardModel.getByKey(4).getContent();
                    break;
                case MODE_WEEKLY:
                    homeHTMLContent = "" + DashboardModel.getByKey(2).getContent();
                    break;
                default:
                    homeHTMLContent = "  " + DashboardModel.getByKey(4).getContent();
            }
            WebViewUtil.enableHardwareAcceleration(wvHome);
            WebViewUtil.enableHardwareAcceleration(wvDetail);
            wvHome.loadDataWithBaseURL("file:///android_asset/", homeHTMLContent, "text/html", "utf-8", "");
            wvHome.setWebViewClient(homeWebClient);

        } catch (Exception e) {
            e.printStackTrace();
            hideProgress1();
        }
    }


    private QuestionGeneralModel getQuestionnaireData() {
        return QuestionGeneralModel.getQuestionData();

    }

    private AdvertiseModel getAdvertiseData() {
        return AdvertiseModel.getAdvData();

    }

    private void loadDetailContent() {
        try {
            String detailHTMLContent;
            // not sure why but need to add space to different content T_T
            switch (activeHome) {
                case MODE_DAILY:
                    detailHTMLContent = "" + DashboardModel.getByKey(5).getContent();
                    break;
                case MODE_WEEKLY:
                    detailHTMLContent = " " + DashboardModel.getByKey(3).getContent();
                    break;
                default:
                    detailHTMLContent = "" + DashboardModel.getByKey(5).getContent();
            }
            wvDetail.loadDataWithBaseURL("file:///android_asset/", detailHTMLContent, "text/html", "utf-8", "");
            wvDetail.setWebViewClient(detailWebClient);
        } catch (Exception e) {
            e.printStackTrace();
            hideProgress1();
        }
    }

    private void getInitialSetting(int userid) {
        //showProgress1();
        mDisposable = userService.getSetting1(userid, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse<SettingResponse>>() {

                    @Override
                    public void onNext(BaseResponse<SettingResponse> response) {
                        Log.d("Test", "retrieve success");
                        if (response.getData() != null && response.isSucces()) {
                            setupDataSetting(response.getData());
                            setView();
                        } else {
                            DialogUtil.createSimpleOkDialog(HomeActivity.this, "",
                                    LanguageProvider.getLanguage(response.getMessage()));
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Test", "retrieve error");
                        if (MultipleDeviceUtil.checkForceLogout(e)) {
                            MultipleDeviceUtil.sendBroadCast(HomeActivity.this);
                        }
                        setView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void setView() {
        boolean tomorrowAlarm = false;
        String tomorrowAlarmTime = "07:00";
        SettingModel setting = SettingModel.getSetting();
        sbSetSleep.setChecked(NemuriScanModel.getBedActive() ? setting.isAutomatic_operation_wake_active() : false);
        switch (AlarmsAutoScheduler.nextAlarm("")) {
            case Calendar.SUNDAY:
                tomorrowAlarm = setting.automatic_operation_wakeup_sunday_active;
                tomorrowAlarmTime = setting.getAutomatic_operation_sleep_sunday_time();
                break;
            case Calendar.MONDAY:
                tomorrowAlarm = setting.automatic_operation_wakeup_monday_active;
                tomorrowAlarmTime = setting.getAutomatic_operation_sleep_monday_time();
                break;
            case Calendar.TUESDAY:
                tomorrowAlarm = setting.automatic_operation_wakeup_tuesday_active;
                tomorrowAlarmTime = setting.getAutomatic_operation_sleep_tuesday_time();
                break;
            case Calendar.WEDNESDAY:
                tomorrowAlarm = setting.automatic_operation_wakeup_wednesday_active;
                tomorrowAlarmTime = setting.getAutomatic_operation_sleep_wednesday_time();
                break;
            case Calendar.THURSDAY:
                tomorrowAlarm = setting.automatic_operation_wakeup_thursday_active;
                tomorrowAlarmTime = setting.getAutomatic_operation_sleep_thursday_time();
                break;
            case Calendar.FRIDAY:
                tomorrowAlarm = setting.automatic_operation_wakeup_friday_active;
                tomorrowAlarmTime = setting.getAutomatic_operation_sleep_friday_time();
                break;
            case Calendar.SATURDAY:
                tomorrowAlarm = setting.automatic_operation_wakeup_saturday_active;
                tomorrowAlarmTime = setting.getAutomatic_operation_sleep_saturday_time();
                break;
        }

        sbSetWake.setChecked(NemuriScanModel.getBedActive() ? tomorrowAlarm : false);
        if (tomorrowAlarmTime == null || tomorrowAlarmTime.isEmpty()) {
            tomorrowAlarmTime = "07:00";
        }
        tvTime.setText(tomorrowAlarmTime);
        if (tomorrowAlarm) {
            tvTime.setEnabled(false);
        } else {
            tvTime.setEnabled(true);
        }
        String[] currentAlarm = tomorrowAlarmTime.split(":");
        if (currentAlarm.length == 0) {
            currentAlarm = new String[]{"07", "00"};
        }
        timePicker.setSelectOptions(Integer.valueOf(currentAlarm[0]), Integer.valueOf(currentAlarm[1]));

        if ((!setting.isAutomatic_operation_wake_active() && !tomorrowAlarm) || !NemuriScanModel.getBedActive()) {
            btnAutoWhite.setVisibility(View.VISIBLE);
            btnAuto.setVisibility(View.GONE);
        } else {
            btnAutoWhite.setVisibility(View.GONE);
            btnAuto.setVisibility(View.VISIBLE);
        }

        if (!NemuriScanModel.getBedActive())
            tvTime.setText(LanguageProvider.getLanguage("UI000802C179"));

        if (setting.getSleep_reset_timing() == 0) {
            btnTimer.setVisibility(View.GONE);
        } else {
            btnTimer.setVisibility(View.VISIBLE);
        }
    }

    private void checkSbState() {
        SettingModel setting = SettingModel.getSetting();
        if (setting != null) {
            if (setting.isAutomatic_operation_wake_active()) {
                btnAutoWhite.setVisibility(View.GONE);
                btnAuto.setVisibility(View.VISIBLE);
            } else {
                btnAutoWhite.setVisibility(View.VISIBLE);
                btnAuto.setVisibility(View.GONE);
            }
        }

    }

    private void setupDataSetting(SettingResponse settingResponse) {
        SettingModel setting = new SettingModel();
        setting.truncate();
        setting.ads_allowed = settingResponse.ads_allowed;
        setting.automatic_operation_alarm_id = settingResponse.automatic_operation_alarm_id;
        setting.automatic_operation_sleep_active = settingResponse.automatic_operation_sleep_active;
        setting.monitoring_allowed = settingResponse.monitoring_allowed;
        setting.automatic_operation_bed_pattern_id = settingResponse.automatic_operation_bed_pattern_id;
        setting.automatic_operation_reminder_allowed = settingResponse.automatic_operation_reminder_allowed;
        setting.automatic_operation_wakeup_monday_active = settingResponse.automatic_operation_wakeup_monday_active;
        setting.automatic_operation_wakeup_monday_time = settingResponse.automatic_operation_wakeup_monday_time;
        setting.automatic_operation_wakeup_tuesday_active = settingResponse.automatic_operation_wakeup_tuesday_active;
        setting.automatic_operation_wakeup_tuesday_time = settingResponse.automatic_operation_wakeup_tuesday_time;
        setting.automatic_operation_wakeup_wednesday_active = settingResponse.automatic_operation_wakeup_wednesday_active;
        setting.automatic_operation_wakeup_wednesday_time = settingResponse.automatic_operation_wakeup_wednesday_time;
        setting.automatic_operation_wakeup_thursday_active = settingResponse.automatic_operation_wakeup_thursday_active;
        setting.automatic_operation_wakeup_thursday_time = settingResponse.automatic_operation_wakeup_thursday_time;
        setting.automatic_operation_wakeup_friday_active = settingResponse.automatic_operation_wakeup_friday_active;
        setting.automatic_operation_wakeup_friday_time = settingResponse.automatic_operation_wakeup_friday_time;
        setting.automatic_operation_wakeup_saturday_active = settingResponse.automatic_operation_wakeup_saturday_active;
        setting.automatic_operation_wakeup_saturday_time = settingResponse.automatic_operation_wakeup_saturday_time;
        setting.automatic_operation_wakeup_sunday_active = settingResponse.automatic_operation_wakeup_sunday_active;
        setting.automatic_operation_wakeup_sunday_time = settingResponse.automatic_operation_wakeup_sunday_time;
        setting.monitoring_questionnaire_allowed = settingResponse.monitoring_questionnaire_allowed;
        setting.monitoring_weekly_report_allowed = settingResponse.monitoring_weekly_report_allowed;
        setting.monitoring_error_report_allowed = settingResponse.monitoring_error_report_allowed;
        setting.bed_fast_mode = settingResponse.bed_fast_mode;
        setting.bed_combi_locked = settingResponse.bed_combi_locked;
        setting.bed_head_locked = settingResponse.bed_head_locked;
        setting.bed_leg_locked = settingResponse.bed_leg_locked;
        setting.bed_height_locked = settingResponse.bed_height_locked;
        setting.autodriveDegreeSetting = settingResponse.autodriveDegreeSetting;
        setting.user_desired_hardness = settingResponse.user_desired_hardness;
        setting.timer_setting = settingResponse.timer_setting;
        setting.forest_report_allowed = settingResponse.forest_report_allowed;
        setting.sleep_reset_timing = settingResponse.sleep_reset_timing;
        setting.snoring_storage_enable = settingResponse.snoring_storage_enable;
        setting.insert();
        setAllAlarms(HomeActivity.this);
        AlarmsAutoScheduler.setAllAlarms(HomeActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearReferences();
        ActivityModel.clear();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
        purgeBLE();
        RxUtil.dispose(mDisposable);
    }

//    public static Intent intent;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    public void onDateChosen(String dateText) {
        runOnUiThread(() -> wvHome.loadUrl("javascript:(function(){showSelectedData('" + dateText + "');})()"));
    }

    final class WebViewController {
        @JavascriptInterface
        public void closeNotifBirdie(String notifName) {
            SenderBirdieModel.deleteByName(notifName);
            Handler handler = new Handler();
            handler.postDelayed(() -> runOnUiThread(HomeActivity.this::showBirdie), 1000);

        }

        @JavascriptInterface
        public void openCalendarActivity() {
            if (ISHOMEFINISHED && ISDETAILFINISHED && !isCalendarOrDetailVisible) {
                isCalendarOrDetailVisible = true;
                runOnUiThread(() -> {
                    Dialog calendarViewDialog = CalendarWebviewDialog.create(HomeActivity.this, DashboardModel.getByKey(1).getContent(), HomeActivity.this);
                    calendarViewDialog.setCanceledOnTouchOutside(false);
                    calendarViewDialog.show();
                });
            }
        }

        @JavascriptInterface
        public void navigateSingleDetail(int attr, String date) {
            if (ISHOMEFINISHED && ISDETAILFINISHED && date != null && date.length() == 10 && !isCalendarOrDetailVisible) {
                isCalendarOrDetailVisible = true;
                runOnUiThread(() -> {
                    showProgress1();
                    scoreProvider.refreshDailyScore(new ScoreProvider.DailyScoreListener() {
                        @Override
                        public void onDailyScoreDone(String startDate, String endDate, boolean refreshSuccess, Throwable err) {
                            if (!refreshSuccess && MultipleDeviceUtil.checkForceLogout(err)) {
                                MultipleDeviceUtil.sendBroadCast(HomeActivity.this);
                            } else {
                                adsQSProvider.getAdsQS(((isParse, e, isSuccess, message) -> runOnUiThread(() -> {
                                    if (!isParse && MultipleDeviceUtil.checkForceLogout(e)) {
                                        MultipleDeviceUtil.sendBroadCast(HomeActivity.this);
                                        return;
                                    }

                                    if (isParse && !isSuccess) {
                                        DialogUtil.createSimpleOkDialog(HomeActivity.this, "",
                                                LanguageProvider.getLanguage(message));
                                    }
                                    if (!NetworkUtil.isNetworkConnected(HomeActivity.this)) {
                                        AdvertiseModel.clear();
                                        QuestionGeneralModel.clear();
                                    }

                                    disableNavigationUI();

                                    SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                                    Date start_date_format = null;
                                    Date end_date_format = null;
                                    try {
                                        start_date_format = format.parse(date);
                                        end_date_format = format.parse(date);
                                    } catch (ParseException parseEx) {
                                        parseEx.printStackTrace();
                                    }

                                    ArrayList<DailyScoreModel> dailyScoreModels = DailyScoreModel.getBetween(start_date_format, end_date_format);
                                    String params = "var chardata = '';";
                                    if (dailyScoreModels.size() > 0) {
                                        for (int i = 0; i < dailyScoreModels.size(); i++) {
                                            params += "chardata = '" + dailyScoreModels.get(i).data + "';";
                                        }
                                    }

                                    //#region Quiz & Ads
                                    QuestionGeneralModel questionGeneralModel = getQuestionnaireData();
                                    AdvertiseModel advertiseModel = getAdvertiseData();

                                    String quizData = "";
                                    String adsData = "";

                                    if (questionGeneralModel != null && questionGeneralModel.getData() != null) {
                                        quizData = questionGeneralModel.getData();
                                    }
                                    if (advertiseModel != null && advertiseModel.getData() != null) {
                                        adsData = advertiseModel.getData();
                                    }

                                    //Clear Data If Offline
                                    if (!NetworkUtil.isNetworkConnected(HomeActivity.this)) {
                                        AdvertiseModel.clear();
                                        QuestionGeneralModel.clear();
                                        adsData = "";
                                        quizData = "";
                                    }

                                    String qsAdsParam = "initializeQsAdv('" + quizData + "','" + adsData + "');";
                                    //#endregion

                                    String FAQ = "var helpBtns = document.getElementsByClassName('link-faq');for (var i = 0; i < helpBtns.length; i++) {helpBtns[i].addEventListener('click',function(e){var selectedAttr = this.getAttribute('data-id');window.controller.showFAQ(selectedAttr);},false);}";
                                    String javascript = "javascript:(function(){" +
                                            DisplayUtils.FONTS.injectSetFontValue(HomeActivity.this) +
                                            "" + params +
                                            qsAdsParam +
                                            "initChartStatus(['0','3','2','1','4']);" +
                                            FAQ +
                                            "scrollToChart(" + attr + ",chardata);" +
                                            "})()";

                                    loadDetailContent();
                                    wvDetail.setWebViewClient(new WebViewClient() {
                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            super.onPageFinished(view, url);
                                            ViewUtil.injectJS(getApplicationContext(), wvDetail, "detail.js");
                                            ISDETAILFINISHED = true;
                                            wvDetail.loadUrl(javascript);
                                            hideProgress1();
                                            showBirdie();
                                            wvDetail.setVisibility(View.VISIBLE);
                                        }
                                    });
                                })));
                            }
                        }
                    });
                });
            }
        }

        @JavascriptInterface
        public void showFAQ(String id) {
            if (ISHOMEFINISHED && ISDETAILFINISHED) {
                if (!ISFAQRUN) {
                    HomeActivity.ISFAQRUN = true;
                    didNavigateWebviewFaq = true;
                    Intent faqIntent = new Intent(HomeActivity.this, FaqActivity.class);
                    faqIntent.putExtra("ID_FAQ", id);
                    faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(faqIntent);
                }
            }
        }

        @JavascriptInterface
        public void closeDetail() {
            if (ISHOMEFINISHED && ISDETAILFINISHED) {
                isCalendarOrDetailVisible = false;
                runOnUiThread(() -> {
                    overlay.setVisibility(View.INVISIBLE);
                    wvDetail.setVisibility(View.INVISIBLE);
                    tbDay.setEnabled(true);
                    tbWeek.setEnabled(true);
                    btnHamburger.setEnabled(true);
                    btnAutoWhite.setEnabled(true);
                    btnAuto.setEnabled(true);
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

                });
            }
        }

        @JavascriptInterface
        public void openCalendarWeeklyActivity() {
            if (ISHOMEFINISHED && ISDETAILFINISHED && !isCalendarOrDetailVisible) {
                isCalendarOrDetailVisible = true;
                CalendarWebviewDialog.create(HomeActivity.this, DashboardModel.getByKey(1).getContent(), HomeActivity.this).show();
            }
        }

        @JavascriptInterface
        public void navigateSingleDetailWeekly(String attr, String start_date, String end_date) {
            if (ISHOMEFINISHED && ISDETAILFINISHED && start_date != null && end_date != null && start_date.length() == 10 && end_date.length() == 10 && !isCalendarOrDetailVisible) {
                isCalendarOrDetailVisible = true;
                runOnUiThread(() -> {
                    showProgress1();
                    adsQSProvider.getAdsQS(((isParse, e, isSuccess, message) -> runOnUiThread(() -> {
                        if (!isParse && MultipleDeviceUtil.checkForceLogout(e)) {
                            MultipleDeviceUtil.sendBroadCast(HomeActivity.this);
                        }

                        if (isParse && !isSuccess) {
                            DialogUtil.createSimpleOkDialog(HomeActivity.this, "",
                                    LanguageProvider.getLanguage(message));
                        }

                        if (!NetworkUtil.isNetworkConnected(HomeActivity.this)) {
                            AdvertiseModel.clear();
                            QuestionGeneralModel.clear();
                        }

                        disableNavigationUI();

                        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                        Date start_date_format = null;
                        Date end_date_format = null;
                        try {
                            start_date_format = format.parse(start_date);
                            end_date_format = format.parse(end_date);
                        } catch (ParseException parseEx) {
                            parseEx.printStackTrace();
                        }

                        ArrayList<WeeklyScoreModel> dailyScoreModels = WeeklyScoreModel.getBetween(start_date_format, end_date_format);
                        String params = "var chardata = '';";
                        for (int i = 0; i < dailyScoreModels.size(); i++) {
                            params += "chardata = '" + dailyScoreModels.get(i).data + "';";
                        }

                        //#region Quiz & Ads
                        QuestionGeneralModel questionGeneralModel = getQuestionnaireData();
                        AdvertiseModel advertiseModel = getAdvertiseData();

                        String quizData = "";
                        String adsData = "";

                        if (questionGeneralModel != null && questionGeneralModel.getData() != null) {
                            quizData = questionGeneralModel.getData();
                        }
                        if (advertiseModel != null && advertiseModel.getData() != null) {
                            adsData = advertiseModel.getData();
                        }

                        //Clear Data If Offline
                        if (!NetworkUtil.isNetworkConnected(HomeActivity.this)) {
                            AdvertiseModel.clear();
                            QuestionGeneralModel.clear();
                            adsData = "";
                            quizData = "";
                        }

                        String qsAdsParam = "initializeQsAdv('" + quizData + "','" + adsData + "');";
                        //#endregion

                        String FAQ = "var helpBtns = document.getElementsByClassName('link-faq');for (var i = 0; i < helpBtns.length; i++) {helpBtns[i].addEventListener('click',function(e){var selectedAttr = this.getAttribute('data-id');window.controller.showFAQ(selectedAttr);},false);}";
                        String javascript = "javascript:(function(){" +
                                DisplayUtils.FONTS.injectSetFontValue(HomeActivity.this) +
                                "" + params +
                                qsAdsParam +
                                "initChartStatus(['0','3','2','1','4']);" +
                                FAQ +
                                "scrollToChart(" + attr + ",chardata);" +
                                "})()";

                        loadDetailContent();
                        wvDetail.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                super.onPageFinished(view, url);
                                ViewUtil.injectJS(getApplicationContext(), wvDetail, "detail.js");
                                ISDETAILFINISHED = true;
                                wvDetail.loadUrl(javascript);
                                hideProgress1();
                                showBirdie();
                                wvDetail.setVisibility(View.VISIBLE);
                            }
                        });
                    })));
                });

            }
        }

        @JavascriptInterface
        public void closeDetailWeekly() {
            if (ISHOMEFINISHED && ISDETAILFINISHED) {
                isCalendarOrDetailVisible = false;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        overlay.setVisibility(View.INVISIBLE);
                        wvDetail.setVisibility(View.INVISIBLE);
                        tbDay.setEnabled(true);
                        tbWeek.setEnabled(true);
                        btnHamburger.setEnabled(true);
                        btnAutoWhite.setEnabled(true);
                        btnAuto.setEnabled(true);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                });
            }
        }

        @JavascriptInterface
        public void get_daily_data(String date) {
            runOnUiThread(() -> {
                String[] arr_date = date.split(";");
                currentStartDailyDate = arr_date[0];
                currentEndDailyDate = arr_date[1];

                if (activeHome == MODE_DAILY && wvHome.isEnabled()) {
                    setWebViewEnabled(wvHome, false);
                    showProgress3();
                }
                scoreProvider.getDailyScore(UserLogin.getUserLogin().getId(), arr_date[0], arr_date[1], ((startDate, endDate, isParse, e) -> runOnUiThread(() -> {
                    checkInToday(HomeActivity.this);
                    if (activeHome == MODE_DAILY) {
                        setWebViewEnabled(wvHome, true);
                        hideProgress3();
                        if (!isParse && MultipleDeviceUtil.checkForceLogout(e)) {
                            MultipleDeviceUtil.sendBroadCast(HomeActivity.this);
                        }
                        loadDailyChart(startDate, endDate);
                        if (!isOfflineLaunchFired) {
                            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                isOfflineLaunchFired = true;
                                DialogUtil.offlineDialog(HomeActivity.this, getApplicationContext());
                            }
                        }
                    }
                })));
            });
        }

        @JavascriptInterface
        public void get_weekly_data(String date) {
            runOnUiThread(() -> {
                String[] arr_date = date.split(";");
                currentStartWeeklyDate = arr_date[0];
                currentEndWeeklyDate = arr_date[1];

                if (activeHome == MODE_WEEKLY && wvHome.isEnabled()) {
                    setWebViewEnabled(wvHome, false);
                    showProgress3();
                    scoreProvider.getWeeklyScore(UserLogin.getUserLogin().getId(), arr_date[0], arr_date[1], ((startDate, endDate, isParse, e) -> runOnUiThread(() -> {
                        checkInToday(HomeActivity.this);
                        if (activeHome == MODE_WEEKLY) {
                            setWebViewEnabled(wvHome, true);
                            hideProgress3();
                            loadWeeklyChart(startDate, endDate);
                        }
                    })));
                }
            });
        }

        @JavascriptInterface
        public void finish_loading() {
            ISHOMEFINISHED = true;
        }

        @JavascriptInterface
        public void open_external_link(String url) {
            if (ISHOMEFINISHED && ISDETAILFINISHED) {
                if (AdvertiseModel.getAdvData() != null) {
                    LogUserAction.sendNewLog(userService, "ADVERTISE_LINK_CLICK", String.valueOf(AdvertiseModel.getAdvData().getId()), "", "UI000500");
                }
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        }

        @JavascriptInterface
        public void close_qsadv(String param) {
            AdvertiseModel advertiseModel = getAdvertiseData();
            QuestionGeneralModel questionGeneralModel = getQuestionnaireData();
            switch (param) {
                case "1":
                    if (advertiseModel != null) adsQSProvider.seeAds(advertiseModel.getId());
                    break;
                case "2":
                    if (questionGeneralModel != null)
                        adsQSProvider.seeQS(questionGeneralModel.getId());
                    break;
                default:
            }
        }

        @SuppressLint("CheckResult")
        @JavascriptInterface
        public void send_questionnaire_result(String param) {
            adsQSProvider.sendQSResult(3, param, ((isParse, e, isSuccess, message) -> runOnUiThread(() -> {
                if (!isParse && MultipleDeviceUtil.checkForceLogout(e))
                    MultipleDeviceUtil.sendBroadCast(HomeActivity.this);
            })));
        }

        @JavascriptInterface
        public void snore_availability(String[] snoreFiles) {
            ArrayList<String> fileAvailabilityStatus = new ArrayList<>();
            boolean allFileExist = true;
            for (String snoreFile :
                    snoreFiles) {
                boolean isExist = SnoreFileUtil.isSnoreResultExist(snoreFile,HomeActivity.this);
                LogUserAction.sendNewLog(userService,"SNORE_AVAILABILITY","Single file "+snoreFile+" "+isExist,"","");
                fileAvailabilityStatus.add(isExist ? "true" : "false");
                allFileExist = allFileExist && isExist;
            }

            if(!allFileExist){
                LogUserAction.sendNewLog(userService,"SNORE_AVAILABILITY","Content "+SnoreFileUtil.getAnalysisFolderResultContent(HomeActivity.this),"","");
            }

            wvDetail.post(() -> {
                String injectedScript = "snore_availability_status(["+fileAvailabilityStatus.stream().collect(Collectors.joining(","))+"]);";
                injectedScript = "javascript:(function(){" + injectedScript  + "})()";
                if(wvDetail != null && wvDetail.getVisibility() == View.VISIBLE){
                    wvDetail.loadUrl(injectedScript);
                }
            });
        }

        @JavascriptInterface
        public void snore_play(String snoreFile) {
            LogUserAction.sendNewLog(userService, "SNORING_PLAY", "", "", "UI000500");
            if(snoreFile != null && !snoreFile.isEmpty()){
                boolean isExist = SnoreFileUtil.isSnoreResultExist(snoreFile,HomeActivity.this);
                if(isExist){
                    boolean playbackStatus = MediaPlayerUtil.playAudioFromPath(HomeActivity.this, SnoreFileUtil.getAnalysisResultFolderPath(HomeActivity.this) + "/" + snoreFile, AudioManager.STREAM_MUSIC, false, new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            notifyStopSnorePlayback();
                        }
                    });
                    if(!playbackStatus){
                        notifyStopSnorePlayback();
                        LogUserAction.sendNewLog(userService, "SNORING_PLAY_FAILED", "PLAYBACK_FAILED : "+snoreFile, "", "UI000500");
                    }
                }else{
                    LogUserAction.sendNewLog(userService, "SNORING_PLAY_FAILED", "FILE_MISSING : "+snoreFile, "", "UI000500");
                }
            }else{
                LogUserAction.sendNewLog(userService, "SNORING_PLAY_FAILED", "PARAM_INVALID", "", "UI000500");
            }
        }

        @JavascriptInterface
        public void snore_stop(String param) {
            Logger.d("test ae stop");
            MediaPlayerUtil.stopAudio();
        }

        @JavascriptInterface
        public void snore_show(String param) {
            LogUserAction.sendNewLog(userService, "SNORING_DETAIL_SHOW", "", "", "UI000500");
        }
        private void notifyStopSnorePlayback(){
            if(wvDetail.getVisibility() != View.VISIBLE){
                return;
            }
            wvDetail.post(() -> {
                String injectedScript = "snore_finish();";
                injectedScript = "javascript:(function(){" + injectedScript  + "})()";
                if(wvDetail != null && wvDetail.getVisibility() == View.VISIBLE){
                    wvDetail.loadUrl(injectedScript);
                }
            });
        }
    }


    private void loadWeeklyChart(String start_date, String end_date) {
        if (activeHome == MODE_WEEKLY) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date start_date_format = null;
            Date end_date_format = null;
            try {
                start_date_format = format.parse(start_date);
                end_date_format = format.parse(end_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ArrayList<WeeklyScoreModel> weeklyScoreModels = WeeklyScoreModel.getBetween(start_date_format, end_date_format);
            String params = "var chardata = [];";
            for (int i = 0; i < weeklyScoreModels.size(); i++) {
                params += "chardata.push('" + weeklyScoreModels.get(i).data + "');";
            }
            String javascript = "javascript:(function(){" +
                    DisplayUtils.FONTS.injectSetFontValue(HomeActivity.this) +
                    "" + params +
                    "initializeWeeklyHome(chardata)" +
                    "})()";

            wvHome.loadUrl(javascript);
        }
    }

    private void loadDailyChart(String start_date, String end_date) {
        if (activeHome == MODE_DAILY) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            Date start_date_format = null;
            Date end_date_format = null;
            try {
                start_date_format = format.parse(start_date);
                end_date_format = format.parse(end_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ArrayList<DailyScoreModel> dailyScoreModels = DailyScoreModel.getBetween(start_date_format, end_date_format);
            String params = "var chardata = [];";
            for (int i = 0; i < dailyScoreModels.size(); i++) {
                params += "chardata.push('" + dailyScoreModels.get(i).data + "');";
            }
            String javascript = "javascript:(function(){" +
                    DisplayUtils.FONTS.injectSetFontValue(HomeActivity.this) +
                    "" + params +
                    "initializeDailyHome(chardata);" +
                    "})()";

            wvHome.loadUrl(javascript);
        }
    }

    public void getHomeFromAPI() {
        showProgress1();
        maxRowProvider.getMaxRow((maxRowModel) -> runOnUiThread(() -> {
            formPolicyProvider.getFormPolicy((formPolicyModel) -> runOnUiThread(() -> {
                DeviceTemplateProvider.getDeviceTemplate(this,
                        (mattressModels, bedModels, mattressModelDefaults, bedModelDefaults, nemuriConstantsModel) -> {
                            nsConstants = nemuriConstantsModel;
                            runOnUiThread(() -> {
                                homeService.getHomeContent(0, 1).enqueue(new Callback<BaseResponse<DashboardResponse>>() {
                                    @Override
                                    public void onResponse(Call<BaseResponse<DashboardResponse>> call, Response<BaseResponse<DashboardResponse>> response) {
                                        BaseResponse<DashboardResponse> HomeContentBaseResponse = response.body();
                                        if (HomeContentBaseResponse != null) {
                                            if (HomeContentBaseResponse.isSucces()) {
                                                DashboardResponse data = HomeContentBaseResponse.getData();
                                                DashboardModel.updateByKey(0, data.getRealtimeBed().getContent());
                                                DashboardModel.updateByKey(1, data.getCalendar().getContent());
                                                DashboardModel.updateByKey(2, data.getHomeWeekly().getContent());
                                                DashboardModel.updateByKey(3, data.getDetailWeekly().getContent());
                                                DashboardModel.updateByKey(4, data.getHome().getContent());
                                                DashboardModel.updateByKey(5, data.getDetail().getContent());
                                            }
                                        }
                                        getHomeContent();
                                    }


                                    @Override
                                    public void onFailure(Call<BaseResponse<DashboardResponse>> call, Throwable t) {
                                        setHomeDefaultContent();
                                        getHomeContent();
                                    }
                                });
                            });
                        }, UserLogin.getUserLogin().getId(), nemuriScanDetail.getInfoType());
            }));
        }));
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (wvDetail.getVisibility() == View.VISIBLE) {
            enableNavigationUI();
            return;
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(getApplicationContext(), CloseUtil.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("EXIT", true);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        drawerLayout.closeDrawer(GravityCompat.START, true);
        Toast.makeText(this, LanguageProvider.getLanguage("UI000500C007").equals("UI000500C007") ? "Please click Back again to exit" : LanguageProvider.getLanguage("UI000500C007"), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void showNotifBirdie(String sender) {
        SenderBirdieModel.updateByName(sender);
        showBirdie();
    }

    @OnClick(R.id.btnCloseBridie)
    void closeMessageBirdie(){
        SenderBirdieModel.clear();
        contentBirdie.animate().alpha(0.0f).translationX(-contentBirdie.getWidth());
        contentBirdie.setVisibility(View.VISIBLE);

        btnHomeMain.setVisibility(View.VISIBLE);
    }

    public void showBirdie() {
        try {
            if (SenderBirdieModel.getFirst() != null) {

                contentBirdie.setVisibility(View.VISIBLE);
                contentBirdie.animate().alpha(1.0f).translationX(0);
                btnHomeMain.setChecked(false);
                btnHomeMain.setVisibility(View.GONE);
                contentMainMenu.setVisibility(View.GONE);

                Spanned txtMessage = null;
                String lang = LanguageProvider.getLanguage("UI000500C020");
                String sourceString = lang.replace("%NAME%",SenderBirdieModel.getFirst().getName());

                contentBirdie.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    txtMessage = Html.fromHtml(sourceString, Html.FROM_HTML_MODE_COMPACT);
                } else {
                    txtMessage = Html.fromHtml(sourceString);
                }

                txtMessageBirdie.setText(txtMessage);

//                String javascript = "javascript:(function(){" +
//                        "showNotif('" + SenderBirdieModel.getFirst().getName() + "');" +
//                        "})()";
//
//                wvHome.loadUrl(javascript);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLangFromServer() {
        try {
            String[] languageString = {"en-US", "jp-JP", "id-ID"};
            homeService.getLanguage(languageString[1], 1).enqueue(new Callback<BaseResponse<LanguageModel[]>>() {
                @Override
                public void onResponse(@NonNull Call<BaseResponse<LanguageModel[]>> call, @NonNull Response<BaseResponse<LanguageModel[]>> response) {
                    BaseResponse<LanguageModel[]> languageModelBaseResponse = response.body();
                    if (languageModelBaseResponse != null) {
                        if (languageModelBaseResponse.isSucces()) {
                            LanguageModel[] data = languageModelBaseResponse.getData();
                            if (data == null) {
                                return;
                            }
                            ArrayList<LanguageModel> arr = new ArrayList<>();

                            LanguageModel.clear(languageString[1]);

                            for (LanguageModel languageModel : data
                            ) {
                                languageModel.setLanguageCode(languageString[1]);
                                arr.add(languageModel);
                            }
                            LanguageModel.batchInsert(arr);
                            applyLocalization();

                            homeService.getLanguage(languageString[0], 1).enqueue(new Callback<BaseResponse<LanguageModel[]>>() {
                                @Override
                                public void onResponse(@NonNull Call<BaseResponse<LanguageModel[]>> call, @NonNull Response<BaseResponse<LanguageModel[]>> response) {
                                    BaseResponse<LanguageModel[]> languageModelBaseResponse = response.body();
                                    if (languageModelBaseResponse != null) {
                                        if (languageModelBaseResponse.isSucces()) {
                                            LanguageModel[] data = languageModelBaseResponse.getData();
                                            if (data == null) {
                                                return;
                                            }
                                            ArrayList<LanguageModel> arr = new ArrayList<>();

                                            LanguageModel.clear(languageString[0]);

                                            for (LanguageModel languageModel : data
                                            ) {
                                                languageModel.setLanguageCode(languageString[0]);
                                                arr.add(languageModel);
                                            }
                                            LanguageModel.batchInsert(arr);
                                            applyLocalization();
                                            homeService.getLanguage(languageString[2], 1).enqueue(new Callback<BaseResponse<LanguageModel[]>>() {
                                                @Override
                                                public void onResponse(Call<BaseResponse<LanguageModel[]>> call, Response<BaseResponse<LanguageModel[]>> response) {
                                                    BaseResponse<LanguageModel[]> languageModelBaseResponse = response.body();
                                                    if (languageModelBaseResponse != null) {
                                                        if (languageModelBaseResponse.isSucces()) {
                                                            LanguageModel[] data = languageModelBaseResponse.getData();
                                                            if (data == null) {
                                                                return;
                                                            }
                                                            ArrayList<LanguageModel> arr = new ArrayList<>();

                                                            LanguageModel.clear(languageString[2]);

                                                            for (LanguageModel languageModel : data
                                                            ) {
                                                                languageModel.setLanguageCode(languageString[2]);
                                                                arr.add(languageModel);
                                                            }
                                                            LanguageModel.batchInsert(arr);
                                                            applyLocalization();
                                                        }
                                                    }
                                                }


                                                @Override
                                                public void onFailure(Call<BaseResponse<LanguageModel[]>> call, Throwable t) {

                                                }
                                            });

                                        }
                                    }
                                }


                                @Override
                                public void onFailure(Call<BaseResponse<LanguageModel[]>> call, Throwable t) {

                                }
                            });
                        }
                    }
                }


                @Override
                public void onFailure(@NonNull Call<BaseResponse<LanguageModel[]>> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setHomeDefaultContent() {
        try {
            InputStream isRealtime = getAssets().open("default/realtimebed/default_realtimebed.html");
            int size = isRealtime.available();

            byte[] buffer = new byte[size];
            isRealtime.read(buffer);
            isRealtime.close();

            String strisRealtime = new String(buffer);
            if (DashboardModel.getByKey(0) == null) {
                DashboardModel.updateByKey(0, strisRealtime);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(0) == null) {
                DashboardModel.updateByKey(0, "");
            }
        }
        try {
            InputStream is = getAssets().open("default/calendar/default_calendar.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            if (DashboardModel.getByKey(1) == null) {
                DashboardModel.updateByKey(1, str);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(1) == null) {
                DashboardModel.updateByKey(1, "");
            }
        }
        try {
            InputStream is = getAssets().open("default/home/default_homeweekly.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            if (DashboardModel.getByKey(2) == null) {
                DashboardModel.updateByKey(2, str);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(2) == null) {
                DashboardModel.updateByKey(2, "");
            }
        }
        try {
            InputStream is = getAssets().open("default/home/default_detailweekly.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            if (DashboardModel.getByKey(3) == null) {
                DashboardModel.updateByKey(3, str);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(3) == null) {
                DashboardModel.updateByKey(3, "");
            }
        }
        try {
            InputStream is = getAssets().open("default/home/default_home.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            if (DashboardModel.getByKey(4) == null) {
                DashboardModel.updateByKey(4, str);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(4) == null) {
                DashboardModel.updateByKey(4, "");
            }
        }
        try {
            InputStream is = getAssets().open("default/home/default_detail.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            if (DashboardModel.getByKey(5) == null) {
                DashboardModel.updateByKey(5, str);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(5) == null) {
                DashboardModel.updateByKey(5, "");
            }
        }
    }

    //MARK : BLE related functions
    private void tryToConnectBLE() {
        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        if (nemuriScanModel == null) {
            return;
        }
        runOnUiThread(this::showProgress1);
        //setup connection timeout
        nsConstants = NemuriConstantsModel.get();
        int connectionTimeout = nsConstants.nsConnectionTimeout;

        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, connectionTimeout * 1000);

        nsManager.startScan(this);
    }

    //MARK END : ble related functions


    //MARK : NSBaseDelegate
    @Override
    public void onCommandWritten(NSOperation command) {
        //TODO : HANDLE ILLEGAL BLE OP
        if (command.getCommandCode() == NSOperation.FREE_DECREASE_COMBI.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_MATTRESS_POSITION.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_BED_SETTING.getCommandCode()) {
            //DISCONNECT & SHOW ILLEGAL OPERATION ALERT
            purgeBLE();
            DialogUtil.createCustomYesNo(HomeActivity.this,
                    "",
                    LanguageProvider.getLanguage("UI000802C191"),
                    LanguageProvider.getLanguage("UI000802C193"),
                    (dialogInterface, i) -> {

                    },
                    LanguageProvider.getLanguage("UI000802C192"),
                    (dialogInterface, i) -> {
                        //retry
                        tryToConnectBLE();
                    }
            );
        }
    }
    //MARK END : NSBaseDelegate

    //MARK : NSConnectionDelegate Implementation
    @Override
    public void onConnectionEstablished() {
        runOnUiThread(() -> {
            NemuriScanModel nemuriScanModel = NemuriScanModel.get();
            if (nemuriScanModel != null) {
                nsManager.requestAuthentication(nemuriScanModel.getServerGeneratedId());
            }
        });
    }

    @Override
    public void onDisconnect() {
    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {
    }

    @Override
    public void onAuthenticationFinished(int result) {
        if (result == NSConstants.NS_AUTH_SUCCESS || result == NSConstants.NS_AUTH_REG_SUCCESS) {
            //LOG HERE NS_SET_SERVERID_SUCCESS
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_SUCCESS", "1", "", "UI000500");
            runOnUiThread(() -> {
                NemuriScanModel nemuriScanModel = NemuriScanModel.get();
                if (nemuriScanModel != null) {
                    nsManager.notifyAutomaticOperationChange();
                }
            });
        } else {
            //LOG HERE NS_SET_SERVERID_FAILED
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_FAILED", "1", "", "UI000500");
        }
    }

    @Override
    public void onNSStatusReceived(int systemStatus, int bleStatus, int wifiStatus) {

    }

    @Override
    public void onNSSpecReceived(NSSpec spec) {
    }

    @Override
    public void onConnectionStalled(int status) {

    }

    //MARK END : NSConnectionDelegate Implementation
    //MARK : NSScanDelegate Implementation
    @Override
    public void onStartScan() {

    }

    @Override
    public void onLocationPermissionDenied() {

    }

    @Override
    public void onLocationServiceDisabled() {

    }

    @Override
    public void onCancelScan() {

    }

    @Override
    public void onStopScan() {

    }

    @Override
    public void onScanResult(ScanResult scanResult) {
        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        if (nemuriScanModel != null) {
            if (scanResult.getDevice().getName() != null) {
                Logger.v("HomeActivity : Scanning BLE, looking for " + nemuriScanModel.getMacAddress() + " trying " + scanResult.getDevice().getAddress() + " " + scanResult.getDevice().getName());
            }
            if (scanResult.getDevice().getAddress().equalsIgnoreCase(nemuriScanModel.getMacAddress())) {
                Logger.v("HomeActivity : Scanning BLE, match found");
                nsManager.connectToDevice(scanResult.getDevice(), this);
                nsManager.stopScan();
            }
        }

    }
    //MARK END: NSScanDelegate Implementation

    //MARK : NSAutomaticOperationDelegate Implementation
    @Override
    public void onNotifyAutomaticOperationFinished() {
    }
    //MARK END: NSAutomaticOperationDelegate Implementation

    //MARK : NemuriScanDetailFetchListener Implementation
    @Override
    public void onNemuriScanDetailFetched(NemuriScanModel nemuriScanDetailModel) {
        nemuriScanDetail = nemuriScanDetailModel;
        runOnUiThread(() -> {
            hideProgress2();
            if (!NemuriScanModel.getBedActive()) {
                DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000802C030"), LanguageProvider.getLanguage("UI000802C031"), null);
            }
        });
        updateUIState();
    }
    //MARK END : NemuriScanDetailFetchListener Implementation

    private void updateUIState() {
        runOnUiThread(() -> {
            setView();
            btnSave.setEnabled(nemuriScanDetail.isBedExist());
            tvTime.setEnabled(nemuriScanDetail.isBedExist());
            if (!nemuriScanDetail.isBedExist()) {
                tvTime.setTextColor(HomeActivity.this.getColor(R.color.slight_blue));
                tvTime.setText(LanguageProvider.getLanguage("UI000802C179"));
                imgPull.setVisibility(View.INVISIBLE);
            }
            sbSetWake.setEnabled(nemuriScanDetail.isBedExist());
            sbSetSleep.setEnabled(nemuriScanDetail.isBedExist());
        });
    }

    private void checkNSDetail() {
        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        if (nemuriScanModel == null) {
            runOnUiThread(() -> {
                applyNoNSUI();
                SettingModel.resetNSRelatedSettings();
            });
            settingProvider.noNSSetting((isSuccess) -> runOnUiThread(() -> runOnUiThread(() -> applyNoNSUI())));
            DialogUtil.createSimpleOkDialogLink(HomeActivity.this, "", LanguageProvider.getLanguage("UI000610C030"),
                    LanguageProvider.getLanguage("UI000610C043"), (dialogInterface, i) -> {
                        Intent faqIntent = new Intent(HomeActivity.this, FaqActivity.class);
                        faqIntent.putExtra("ID_FAQ", "UI000610C043");
                        faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(faqIntent);
                        dialogInterface.dismiss();
                    }, LanguageProvider.getLanguage("UI000610C031"), (dialogInterface, i) -> dialogInterface.dismiss());
            return;
        }
        showProgress2();
        NemuriScanUtil.fetchSpec(HomeActivity.this, HomeActivity.this);
    }

    private void applyNoNSUI() {
        runOnUiThread(() -> {
            btnAuto.setVisibility(View.GONE);
            btnAutoWhite.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
            sbSetSleep.setChecked(false);
            sbSetWake.setChecked(false);
            sbSetSleep.setEnabled(false);
            sbSetWake.setEnabled(false);
            tvTime.setTextColor(HomeActivity.this.getColor(R.color.slight_blue));
            tvTime.setText(LanguageProvider.getLanguage("UI000802C179"));
            imgPull.setVisibility(View.INVISIBLE);
        });
    }

    public void showProgress1() {
        runOnUiThread(() -> {
            if (!this.isFinishing()) {
                if (progressDialog1 == null || !progressDialog1.isShowing()) {
                    progressDialog1 = new SVProgressHUD(HomeActivity.this);
                    progressDialog1.show();
                }
            }
        });
    }

    public void hideProgress1() {
        runOnUiThread(() -> {
            if (progressDialog1 != null && progressDialog1.isShowing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog1 != null && progressDialog1.isShowing()) {
                            progressDialog1.dismissImmediately();
                        }
                    }
                }, 1500);
            }
        });
    }

    public void showProgress2() {
        runOnUiThread(() -> {
            if (!this.isFinishing()) {
                if (progressDialog2 == null || !progressDialog2.isShowing()) {
                    progressDialog2 = new SVProgressHUD(HomeActivity.this);
                    progressDialog2.show();
                }
            }
        });
    }

    public void hideProgress2() {
        runOnUiThread(() -> {
            if (progressDialog2 != null && progressDialog2.isShowing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog2 != null && progressDialog2.isShowing()) {
                            progressDialog2.dismissImmediately();
                        }
                    }
                }, 1500);
            }
        });
    }

    public void showProgress3() {
        runOnUiThread(() -> {
            if (!this.isFinishing()) {
                if (progressDialog3 == null || !progressDialog3.isShowing()) {
                    progressDialog3 = new SVProgressHUD(HomeActivity.this);
                    progressDialog3.show();
                }
            }
        });
    }

    public void hideProgress3() {
        runOnUiThread(() -> {
            if (progressDialog3 != null && progressDialog3.isShowing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog3 != null && progressDialog3.isShowing()) {
                            progressDialog3.dismissImmediately();
                        }
                    }
                }, 1500);
            }
        });
    }

    private void purgeBLE() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }

    private void initTutorial() {
        //For Dummy Show Tutorial
        NemuriScanModel nemuriScanModelLocal = NemuriScanModel.get();
        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        if (nemuriScanModelLocal != null && nemuriScanModelLocal.getSerialNumber().startsWith("F")) {
            nemuriScanModel = NemuriScanModel.get();
        }
        if (nemuriScanModel != null && (nemuriScanModel.getInfoType() != null || nemuriScanModel.onlyMattress()) && (nemuriScanModel.isMattressExist() || nemuriScanModel.isBedExist())) {
            TutorialShowModel tSM = TutorialShowModel.get();
            if (tSM.getRemoteShowed() != null && tSM.getRemoteShowed()){
                Intent i = new Intent(HomeActivity.this, TutorialActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                //1. ASB, 2. Intime
                int bedType = nemuriScanModel.getInfoType();

                if (bedType == NSSpec.BED_MODEL.INTIME_COMFORT.ordinal()) {
                    // 「INTIME Comfort」の場合、「INTIME」と同じチュートリアルを表示するため2にする
                    bedType = 2;
                }

                if(nemuriScanModel.onlyMattress()){
                    //3.mattress only
                    bedType = 3;
                }

                //just in case
                if(bedType == 0){
                    openRemote();
                    return;
                }

                i.putExtra("type", bedType);
                i.putExtra("isTutorialRemote", true);
                startActivity(i);
                return;
            }
            openRemote();
        } else {
            openRemote();
        }
    }

    public void openRemote() {
        Intent intent = new Intent(HomeActivity.this, RemoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void enableNavigationUI() {
        isCalendarOrDetailVisible = false;
        overlay.setVisibility(View.INVISIBLE);
        wvDetail.setVisibility(View.INVISIBLE);
        tbDay.setEnabled(true);
        tbWeek.setEnabled(true);
        btnAutoWhite.setEnabled(true);
        btnAuto.setEnabled(true);
        enableHamburgerMenu(false);
    }

    private void enableHamburgerMenu(Boolean params){
        if(params){
            btnHamburger.setEnabled(false);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }else {
            btnHamburger.setEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    public void disableNavigationUI() {
        overlay.setVisibility(View.VISIBLE);
        tbDay.setEnabled(false);
        tbWeek.setEnabled(false);
        btnAuto.setEnabled(false);
        btnAutoWhite.setEnabled(false);
        btnHamburger.setEnabled(false);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void checkScoreStatus() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");

        if (activeHome == MODE_DAILY) {
            String today, yesterday;
            today = getTodayString(df);
            yesterday = getYesterdayString(df);
            scoreProvider.getDailyScoreStatus(UserLogin.getUserLogin().getId(), yesterday, today, ((startDate, endDate, isParse, e, isPull) -> runOnUiThread(() -> {
                if (isPull || isDateChanged()) {
                    if (wvHome.isEnabled()) {
                        runOnUiThread(() -> wvHome.loadUrl("javascript:(function(){showSelectedData('" + yesterday + "');})()"));
                    }
                }
            })));
        } else if (activeHome == MODE_WEEKLY) {
            String currentWeek, lastWeek;
            currentWeek = getCurrentWeek(df);
            lastWeek = getLastWeek(df);
            scoreProvider.getWeeklyScoreStatus(UserLogin.getUserLogin().getId(), lastWeek, currentWeek, ((startDate, endDate, isParse, e, isPull) -> runOnUiThread(() -> {
                if (isPull || isDateChanged()) {
                    if (wvHome.isEnabled()) {
                        runOnUiThread(() -> wvHome.loadUrl("javascript:(function(){showSelectedData('" + lastWeek + "');})()"));
                    }
                }
            })));
        }
    }

    public boolean isDateChanged() {
        SimpleDateFormat dfToday = new SimpleDateFormat("yyyyMMdd");
        if (latestDateCheck(HomeActivity.this) != Integer.parseInt(getTodayString(dfToday))) {
            return true;
        }
        return false;
    }

    public void checkInToday(Activity activity) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        SharedPreferences mSettings = activity.getSharedPreferences("BED_PULL_SCORE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("BED_PULL_SCORE_STATUS", Integer.parseInt(getTodayString(df)));
        editor.apply();
    }

    public int latestDateCheck(Activity activity) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        SharedPreferences monitoringUserLog = activity.getSharedPreferences("BED_PULL_SCORE", Context.MODE_PRIVATE);
        return monitoringUserLog.getInt("BED_PULL_SCORE_STATUS", Integer.parseInt(getYesterdayString(df)));
    }

    private String getTodayString(DateFormat df) {
        Date nowDate = new Date();
        return df.format(nowDate);
    }

    private String getYesterdayString(DateFormat df) {
        return df.format(yesterday());
    }

    private String getCurrentWeek(DateFormat df) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return df.format(calendar.getTime());
    }

    private String getLastWeek(DateFormat df) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.add(Calendar.DATE, -7);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return df.format(calendar.getTime());
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public float getFontScale() {
        return SystemSettingUtil.getFontScale(getContentResolver(), getResources().getConfiguration());
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setWebViewEnabled(WebView webView, boolean isEnabled) {
        webView.setEnabled(isEnabled);
        if (isEnabled) {
            webView.setOnTouchListener((v, event) -> false);
        } else {
            webView.setOnTouchListener((v, event) -> true);
        }
    }

    @SuppressLint("CheckResult")
    private void getNSFWVersion() {
        userService.getFirmwareVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<FirmwareVersionResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<FirmwareVersionResponse> response) {
                        if (response != null) {
                            if (response.isSucces()) {
                                FirmwareVersionResponse data = response.getData();
                                if (data != null) {
                                    NemuriScanModel nemuriScanModel = NemuriScanModel.get();
                                    if (nemuriScanModel != null) {
                                        if (data.lastUpdate > nemuriScanModel.getLastFWUpdate()) {
                                            nemuriScanModel.updateVersion(data.revision, data.minor, data.major, data.lastUpdate);
                                        }
                                    }
                                }
                            }
                        }
                        checkNSFWUpdate();
                    }

                    @Override
                    public void onError(Throwable e) {
                        checkNSFWUpdate();
                    }
                });
    }

    private void checkNSFWUpdate() {
        NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
        if (nemuriScanModel != null && nemuriScanModel.needsFWUpdate()) {
            //show update alert
            LogUserAction.sendNewLog(userService, "HOME_FW_UPDATE_FAILED", String.valueOf(nemuriScanModel.isFWUpdateFailed()), "", "UI000500");
            if (nemuriScanModel.isFWUpdateFailed()) {
                DialogUtil.createYesNoDialogLink(this, "", LanguageProvider.getLanguage("UI000500C009"),
                        LanguageProvider.getLanguage("UI000500C008"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(HomeActivity.this, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000500C008");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        }, LanguageProvider.getLanguage("UI000500C006"), (dialogInterface, i) -> {
                            Intent intent = new Intent(HomeActivity.this, UpdateFirmwareIntroActivity.class);
                            startActivityForResult(intent, 101);
                            dialogInterface.dismiss();
                        },
                        LanguageProvider.getLanguage("UI000500C005"), (dialogInterface, i) -> {

                        });
            } else {
                DialogUtil.createCustomYesNo(activity, "", LanguageProvider.getLanguage("UI000500C004"),
                        LanguageProvider.getLanguage("UI000500C005"),
                        (dialogInterface, i) -> dialogInterface.dismiss(), LanguageProvider.getLanguage("UI000500C006"),
                        (dialog, which) -> {
                            Intent intent = new Intent(HomeActivity.this, UpdateFirmwareIntroActivity.class);
                            startActivityForResult(intent, 101);
                            dialog.dismiss();
                        });
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TutorialFinsihedEvent event) {
        if (event != null) {
            if (event instanceof AppVerCheckFinishedEvent) {
                //disable for feb release
//                getNSFWVersion();
            } else {
                //tutorial finshed, check for app update
                AppUpdaterUtil.checkVersion(ANDROID_APPLICATION_TYPE_BED, homeService, activity);
            }
        }
    }

    public static class WeeklyScoreEvent {
        String message;

        public WeeklyScoreEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @Subscribe
    public void onWeeklyScoreEvent(WeeklyScoreEvent weeklyScoreEvent) {
        if (weeklyScoreEvent.getMessage().equals(WeeklyScoreReviewDialog.SET_DAILY)) {
            activeHome = MODE_DAILY;
            tbWeek.setChecked(false);
            tbDay.setChecked(true);
            getHomeContent();
        } else if (weeklyScoreEvent.getMessage().equals(WeeklyScoreReviewDialog.SET_WEEKLY)) {
            activeHome = MODE_WEEKLY;
            tbDay.setChecked(false);
            tbWeek.setChecked(true);
            getHomeContent();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    public static class TutorialFinsihedEvent {
    }

    public static class AppVerCheckFinishedEvent extends TutorialFinsihedEvent {
    }

    public static interface LoadingListener{
        void onShow();
        void onHide();
    }

    public static class TimerEvent {
        String message;

        public TimerEvent(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @Subscribe
    public void onTimerEvent(TimerEvent timerEvent) {
        if (timerEvent.getMessage().equals(TimerActivity.APPLY_NO_NS_UI)) {
            applyNoNSUI();
        }
        else if (timerEvent.getMessage().equals(TimerActivity.STOP_TIMER_ACTION)) {
            getInitialSetting(UserLogin.getUserLogin().getId());
            alarmOpen = false;

            btnHamburger.setEnabled(true);
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            if (isNemuriScanInitiated && nsManager != null) {
                purgeBLE();
            }

            hideProgress1();
        }
        else if (timerEvent.getMessage().equals(TimerActivity.UPDATE_UI_STATE)) {
            updateUIState();
        }
    }

    private void clearReferences(){
        Activity currActivity = bedApplication.getCurrentActivity();
        if (this.equals(currActivity))
            bedApplication.setCurrentActivity(null);
    }
    public static boolean isHomeActivity(String className){
        boolean classNameCheck = Arrays.asList(HomeActivity.class.getSimpleName(),
                            TutorialActivity.class.getSimpleName(),
                            AlarmsSleepQuestionnaire.class.getSimpleName(),
                            WeeklyScoreReviewDialog.class.getSimpleName(),
                            SleepResetActivity.class.getSimpleName()
                            ).contains(className);

       if(className.equalsIgnoreCase(ForestDialog.class.getSimpleName())){
            return !ForestDialog.fromMenu;
        }else{
            return classNameCheck;
        }
    }

    public static boolean isSleepResetActivityActive(Context ctx){
        ActivityManager mgr = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        String className = "";
        if (mgr != null) {
            List<ActivityManager.AppTask> tasks = mgr.getAppTasks();
             if (tasks != null && !tasks.isEmpty()) {
                className = tasks.get(0).getTaskInfo().topActivity.getClassName();
            }
        }
        Logger.d("getTopActivity "+className);
        return "com.paramount.bed.ui.main.SleepResetActivity".equalsIgnoreCase(className);
    }
}
