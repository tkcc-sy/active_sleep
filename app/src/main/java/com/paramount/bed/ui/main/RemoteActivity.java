package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSFailCode;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSBedDelegate;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSMattressDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.pojo.NSBedPosition;
import com.paramount.bed.ble.pojo.NSBedSetting;
import com.paramount.bed.ble.pojo.NSBedSpec;
import com.paramount.bed.ble.pojo.NSMattressPosition;
import com.paramount.bed.ble.pojo.NSMattressStatus;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MHSModel;
import com.paramount.bed.data.model.MattressHardnessSettingModel;
import com.paramount.bed.data.model.MattressSettingModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.PendingMHSModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.FormPolicyProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.LogProvider;
import com.paramount.bed.data.provider.MattressSettingProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.AnimateUtils;
import com.paramount.bed.util.CustomViewPager;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.RemoteAnimationUtil;
import com.paramount.bed.util.RemoteSettingUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.ViewCollections;
import io.reactivex.disposables.Disposable;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.paramount.bed.util.LogUtil.Logx;

//REMOTE CONTROL BLE CLASS
public class RemoteActivity extends BaseActivity implements BedManualFragment.BedManualListener, NSScanDelegate,
        NSConnectionDelegate, NSBedDelegate, NSMattressDelegate, BedPresetFragment.BedPresetEventListener, DeviceTemplateProvider.DeviceTemplateFetchListener,
        MatressPresetFragment.MattressPresetEventListener, MatressManualFragment.MattressFreeEventListener, MatresRecommendFragment.MattressRecommendEventListener {

    //Check pair bed or mattress
    private final BedMattressCheck bedMattressCheck = BedMattressCheck.ACTIVE; //change to BedMattressCheck.IGNORE to ignoring pair with bed or mattress

    //MARK : Main Layout Binding
    @BindView(R.id.mainLayout)
    ConstraintLayout mainLayout;
    @BindView(R.id.bedCardPager)
    CustomViewPager bedCardPager;
    @BindView(R.id.matressCardPager)
    CustomViewPager mattressCardPager;
    @BindView(R.id.bedContainer)
    ConstraintLayout bedContainer;
    @BindView(R.id.matressContainer)
    ConstraintLayout matressContainer;
    @BindView(R.id.btnStartMatress)
    LinearLayout btnStartMatress;
    @BindView(R.id.txtStartMatress)
    TextView txtStartMatress;
    @BindView(R.id.btnStartBed)
    LinearLayout btnStartBed;
    @BindView(R.id.txtStartBed)
    TextView txtStartBed;
    @BindView(R.id.btnBed)
    ToggleButton btnBed;
    @BindView(R.id.btnMatress)
    ToggleButton btnMattress;
    @BindView(R.id.btnHelp)
    Button btnHelp;
    @BindView(R.id.btnSetting)
    Button btnSetting;
    @BindView(R.id.btnCloseMatress)
    Button btnCloseMattress;
    @BindView(R.id.btnCloseBed)
    View btnCloseBad;
    boolean lastStatusTvFootVisibility = false;

    View viewDialogHardness;
    private boolean isLocationPermissionRejected = false;
    @OnClick(R.id.btnBed)
    void toggleBed() {
        if(currentTabType == RemoteTabType.BED){
            btnBed.setChecked(true);
            return;
        }
        if (mattressPresetFragment.isFukattoActive()) {
            setMattressFukattoUI(true);
        }
        if(mattressRecomendFragment != null && mattressRecomendFragment.isHistoryShowing()){
            mattressRecomendFragment.onMattresRecommendHistoryCloseTapped();
        }
        stopAllAnimation();
        if (btnBed.isChecked()) {
            btnMattress.setChecked(false);
            mainLayout.setBackground(getDrawable(R.drawable.bed_background));

            //hide matress
            matressContainer.setVisibility(View.INVISIBLE);
            //show bed
            bedContainer.setVisibility(View.VISIBLE);
        } else {
            btnBed.setChecked(true);
        }
        currentTabType = RemoteTabType.BED;
        currentPagerType = RemotePagerType.PRESET;
        bedCardPager.setCurrentItem(0);
        userWarnedHeight = false;
        isFirstOperated = false;
        mattressPresetSelectAdjusted = false;
        applyNSSpec();
        showMissingPartAlert();
        disableUIByLocation();
        if (btnBed.isChecked() && NemuriScanModel.get() == null) {
            btnStartBed.setVisibility(View.VISIBLE);
            btnStartBed.animate().alpha(1.0f).translationY(0);
        }
    }

    @OnClick(R.id.btnMatress)
    void toggleMatress() {
        if(currentTabType == RemoteTabType.MATTRESS){
            btnMattress.setChecked(true);
            return;
        }
        if (btnMattress.isChecked()) {
            btnBed.setChecked(false);
            mainLayout.setBackground(getDrawable(R.drawable.matress_background));
            //hide bed
            bedContainer.setVisibility(View.INVISIBLE);
            //show bed
            matressContainer.setVisibility(View.VISIBLE);
        } else {
            btnMattress.setChecked(true);
        }
        currentTabType = RemoteTabType.MATTRESS;
        currentPagerType = RemotePagerType.PRESET;
        lastStatusTvFootVisibility = tvFootIndicator.getVisibility() == View.VISIBLE;
        mattressCardPager.setCurrentItem(0);
        setMattressNumericIndicator(currentMattressPosition);
        userWarnedHeight = false;
        isFirstOperated = false;
        mattressPresetSelectAdjusted = false;
        applyNSSpec();
        showMissingPartAlert();

        if (mattressPresetFragment.isFukattoActive()) {
            setMattressFukattoUI(false);
        }
        disableUIByLocation();
    }

    @OnClick(R.id.btnCloseBed)
    void closeBed() {
        HomeActivity.REMOTEACTIVE = false;
        bedPresetFragment.setIsBedOperationRunning(false);
        finish();
    }

    @OnClick(R.id.btnCloseMatress)
    void closeMatress() {
        mattressPresetFragment.setIsFukattoOperationRunning(false);
        finish();
    }

    @OnClick(R.id.btnHelp)
    public void onBtnHelpClick() {
        Intent faqIntent = new Intent(RemoteActivity.this, FaqActivity.class);
        faqIntent.putExtra("ID_FAQ", btnBed.isChecked() ? "UI000610C048" : "UI000610C049");
        faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(faqIntent);
        userWarnedHeight = false;
        isFirstOperated = false;
    }
    //MARK END : Main Layout Binding

    //MARK : Bed Layout Binding
    @BindView(R.id.tiltIconImage)
    ImageView tiltIconImage;

    @BindView(R.id.bed_head_segment)
    View bedHeadSegment;
    @BindView(R.id.bed_shad_head_segment)
    View bedShadHeadSegment;
    @BindView(R.id.bed_thigh_segment)
    View bedThighSegment;
    @BindView(R.id.bed_shad_thigh_segment)
    View bedShadThighSegment;
    @BindView(R.id.bed_leg_segment)
    View bedLegSegment;
    @BindView(R.id.bed_shad_leg_segment)
    View bedShadLegSegment;

    @BindView(R.id.headIndicator)
    TextView tvHeadIndicator;
    @BindView(R.id.heightIndicator)
    TextView tvHeightIndicator;
    @BindView(R.id.footIndicator)
    TextView tvFootIndicator;

    @BindViews({R.id.heightArrowUp1, R.id.heightArrowUp2, R.id.heightArrowUp3})
    List<View> heightArrowsUp;
    @BindViews({R.id.heightArrowDown1, R.id.heightArrowDown2, R.id.heightArrowDown3})
    List<View> heightArrowsDown;
    //MARK END: Bed Layout Binding

    //MARK : Mattress Layout Binding
    @BindView(R.id.matressWrap)
    LinearLayout mattressWrap;
    @BindViews({R.id.txtSegment1, R.id.txtSegment2, R.id.txtSegment3,
            R.id.txtSegment4, R.id.txtSegment5, R.id.txtSegment6})
    List<TextView> mattressNumericIndicators;
    @BindViews({R.id.mWrapOn1, R.id.mWrapOn2, R.id.mWrapOn3,
            R.id.mWrapOn4, R.id.mWrapOn5, R.id.mWrapOn6})
    List<View> mattressVisualIndicators;
    //MARK END : Mattress Layout Binding

    //MARK : General vars
    private RemoteTabType currentTabType = RemoteTabType.BED;
    private RemotePagerType currentPagerType = RemotePagerType.PRESET;

    public Boolean userWarnedHeight = false;

    private boolean isStart;
    private Boolean isBack = true;

    private int counterDim = 0;
    private ArrayList<float[]> dimmingPatternUp = new ArrayList<>();
    private ArrayList<float[]> dimmingPatternDown = new ArrayList<>();

    private ArrayList<DeviceTemplateBedModel> bedPresetValues;
    private ArrayList<DeviceTemplateMattressModel> mattressPresetValues;

    private BedPresetFragment bedPresetFragment;
    private BedManualFragment bedManualFragment;
    private MatressPresetFragment mattressPresetFragment;
    private MatressManualFragment mattressManualFragment;
    private MatresRecommendFragment mattressRecomendFragment;

    private boolean shouldNotifyBedMissing;
    private boolean shouldNotifyMattressMissing;
    private boolean shouldNotifyBothMissing;

    HomeService homeService;
    Disposable mDisposable;

    private int reconnectNSRetryCount;

    private Handler reconnectNSWaitHandler = new Handler();
    private Runnable reconnectNSWaitTimer;
    private boolean isInBackground = false;
    //MARK END : General vars

    //MARK : Bed Setting vars
    private BedSettingDialogController bedSettingController = new BedSettingDialogController();
    private MattressSettingDialogController mattressSettingController = new MattressSettingDialogController();

    @BindView(R.id.dialogSettingBed)
    ConstraintLayout dialogSettingBed;
    @BindView(R.id.bedSettingContainer)
    ConstraintLayout bedSettingContainer;
    @BindViews({R.id.toogle1, R.id.toogle2, R.id.toogle3, R.id.toogle4})
    List<ToggleButton> lockToggles;
    @BindViews({R.id.lockToogle1, R.id.lockToogle2, R.id.lockToogle3, R.id.lockToogle4})
    List<ImageView> lockToggleIcons;
    @BindView(R.id.btnBedConfirm)
    Button btnBedConfirm;
    @BindView(R.id.textViewBedReset)
    TextView textViewBedReset;
    @BindView(R.id.btn_fast)
    ToggleButton tglBtnFast;
    @BindView(R.id.btn_usually)
    ToggleButton tglBtnUsually;
    @BindView(R.id.btnBedPositionAdjustment)
    Button btnBedPositionAdjustment;

    @OnClick(R.id.btnSetting)
    void showSetting() {
        if (currentTabType == RemoteTabType.BED) {
            //LOG HERE NS_REMOTE_OPEN_BED_SETTING
            LogUserAction.sendNewLog(userService, "NS_REMOTE_OPEN_BED_SETTING", "1", "", "UI000610");
            bedSettingController.openSettingDialog(currentSetting);
            clearBedPresetUI();
        } else {
            //LOG HERE NS_REMOTE_OPEN_BED_SETTING
            LogUserAction.sendNewLog(userService, "NS_REMOTE_OPEN_MATTRESS_SETTING", "1", "", "UI000610");
            mattressSettingController.openSettingDialog();
            clearMattressUI();
        }
        userWarnedHeight = false;
        isFirstOperated = false;
        mattressPresetSelectAdjusted = false;
    }

    @OnClick({R.id.toogle1, R.id.toogle2, R.id.toogle3, R.id.toogle4})
    void lockToggleTap(ToggleButton tappedButton) {
        bedSettingController.onLockTap(tappedButton);
    }

    @OnClick(R.id.btnBedConfirm)
    void saveSettingBed() {
        bedSettingController.onApplyBedTemplateTap();
    }

    @OnClick(R.id.textViewBedReset)
    void resetSettingBed() {
        bedSettingController.onResetBedTemplateTap();
    }

    @OnClick(R.id.btnCloseSettingBed)
    void closeSettingBed() {
        bedSettingController.onCloseTap();
    }

    @OnClick(R.id.btn_fast)
    void fastBtn() {
        bedSettingController.onFastModeTap();
    }

    @OnClick(R.id.btn_usually)
    void fastUsually() {
        bedSettingController.onNormalModeTap();
    }
    //MARK END : Bed Setting vars

    //MARK : Mattress Setting vars
    @BindView(R.id.btnMatressConfirm)
    Button btnMatressConfirm;
    @BindView(R.id.textViewMatressReset)
    TextView textViewMatressReset;
    @BindView(R.id.dialogSettingMatress)
    ConstraintLayout dialogSettingMattress;

    @BindView(R.id.matressSettingContainer)
    ConstraintLayout matressSettingContainer;
    @BindView(R.id.btnMatressPositionAdjustment)
    Button btnMatressPositionAdjustment;

    @OnClick(R.id.btnCloseSettingMatress)
    void closeSettingMatress() {
        mattressSettingController.onCloseTap();
    }

    @OnClick(R.id.btnMatressConfirm)
    void saveSettingMatress() {
        mattressSettingController.onApplyMattressTemplateTap();
    }

    @OnClick(R.id.textViewMatressReset)
    void resetSettingMatressToDefault() {
        mattressSettingController.onResetMattressTemplateTap();
    }

    @OnClick(R.id.btnGoRemoteAct)
    void onDehumidifierTap() {
        mattressSettingController.onDehumidifierTap();
    }

    @BindView(R.id.btnGoRemoteAct)
    Button btnDehumidifier;
    //MARK END : Mattress Setting vars

    //MARK : BLE Vars
    private NemuriScanModel nemuriScanModel;
    private boolean isNemuriScanMissing = false;
    private boolean isNemuriScanInitiated = false;
    private boolean isIntentionalDC = false;

    private NSManager nsManager;
    private NSBedSpec currentBedSpec = new NSBedSpec(0, 70, 0, 30, 0, 37, 0, 0);
    private NSBedPosition currentBedPosition = new NSBedPosition(0, 0, 27, 0);
    private NSBedPosition currentShadowPosition = new NSBedPosition();
    private NSMattressPosition dehumidifierTemp = new NSMattressPosition();
    private int dehumidifierTempCounter = 0;
    private int dehumidifierDelayCount = 3;
    private NSMattressPosition currentMattressPosition = new NSMattressPosition();
    private NSMattressStatus currentMattressStatus = new NSMattressStatus();
    private NSSpec currentNSSpec = new NSSpec();
    private NSBedSetting currentSetting = new NSBedSetting();
    private NSBedSetting previousSetting = new NSBedSetting();
    private NemuriConstantsModel nemuriConstantsModel = new NemuriConstantsModel();

    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            Logger.w("RemoteActivity : Disconnect by timeout");
            hideProgress();
            isNemuriScanInitiated = false;
            isIntentionalDC = true;
            if (nsManager != null) {
                nsManager.disconnectCurrentDevice();
            }
            new Handler().postDelayed(() -> {
                DialogUtil.createCustomYesNo(RemoteActivity.this,
                        "",
                        LanguageProvider.getLanguage("UI000610C022"),
                        LanguageProvider.getLanguage("UI000610C026"),
                        (dialogInterface, i) -> {
                            currentNSSpec.setMattressExist(false);
                            currentNSSpec.setBedExist(false);
                            disableBedUI();
                            disableMattressUI();
                        },
                        LanguageProvider.getLanguage("UI000610C025"),
                        (dialogInterface, i) -> {
                            //retry
                            tryToConnectBLE();
                        }
                );
            }, 1);

        }
    };

    private Handler getPositionHandler = new Handler();
    private Runnable getPositionTimer = new Runnable() {
        public void run() {
            if (!isInBackground && !isArrowPressedDown && !isPresetPressedDown && currentBedOperationStatus == NSOperation.BedOperationType.NONE
                    && mattressPendingOperationType == MattressPendingOperationType.NONE && isNemuriScanInitiated) {
                if (currentTabType == RemoteTabType.BED) {
                    nsManager.getBedPosition();
                } else if (currentTabType == RemoteTabType.MATTRESS) {
                    nsManager.getMattressPosition();
                }
            }
            getPositionHandler.postDelayed(getPositionTimer, nemuriConstantsModel.statusPollingInterval * 1000);
        }
    };
    private Handler sameBedPosHandler = new Handler();
    private Runnable sameBedPosTimer;
    private int mismatchButtonCodeCounter = 0;
    private boolean mattressOnlyChecked = false;
    private MHSModel lastMHSUsed;
    //MARK END : BLE VARS

    //MARK : Set Mattress vars
    private MattressPendingOperationType mattressPendingOperationType = MattressPendingOperationType.NONE;
    private float MATTRESS_OPERATION_TIMEOUT = NemuriConstantsModel.get().mattressOperationTimeout;
    private int mattressRetryCount;
    private Handler setMattressRetryHandler = new Handler();
    private Runnable setMattressRetryTimer;
    private boolean mattressPresetSelectAdjusted;
    //MARK END : Set Mattress vars

    //MARK :  BLE bed freemode vars
    private Timer nsArrowAnimator;
    private int commandRequestCount = 0;
    private int commandResponseCount = 0;
    private int fingerPressCount = 0;
    private boolean isSendingMultiButton = false;
    private boolean isArrowPressedDown = false;
    private NSOperation currentFreeOperation;

    private Handler commandTimeoutHandler = new Handler();
    private Runnable commandTimeoutTimer = () -> {
//        Logger.w("(catchball) commandResponseTimeoutTimer triggered commandRequestCount : " + (commandRequestCount) + " commandResponseCount : " + (commandResponseCount));

        //LOG HERE NS_REMOTE_CONNECTION_FAILED
        LogUserAction.sendNewLog(userService, "NS_REMOTE_CONNECTION_FAILED", "1", "", "UI000610");
        if (commandRequestCount - commandResponseCount > 2) {
            purgeBLE();
            Logger.e("(catchball) no responding alert triggered with count: " + (commandRequestCount - commandResponseCount) + " commandRequestCount : " + (commandRequestCount) + " commandResponseCount : " + (commandResponseCount));
            runOnUiThread(() -> DialogUtil.createSimpleOkDialog(RemoteActivity.this, "",
                    LanguageProvider.getLanguage("UI000610C032"),
                    LanguageProvider.getLanguage("UI000610C033"), (dialogInterface, i) -> {
                        //TODO:On Bluetooth Off
                        //retry
                        tryToConnectBLE();
                    }));
//                onFreeTouchEnd(); MITEANEKI
            stopPresetOperation();
            stopBedFreeMode();
            fingerPressCount = 0;
        }
    };

    private Handler freeCommandHandler = new Handler();
    private Runnable freeCommandTimer = () -> {
        if (isArrowPressedDown) {
            scheduleArrowCommand(false);
        }
    };
    private Handler presetCommandHandler = new Handler();
    private Runnable presetCommandTimer = new Runnable() {
        @Override
        public void run() {
            if (currentBedOperationStatus == NSOperation.BedOperationType.PRESET) {
                schedulePresetCommand(false);
            }
        }
    };
    //MARK END :  BLE bed freemode vars

    //MARK : BLE bed preset vars
    private NSBedPosition currentPresetTarget;
    private NSOperation.BedOperationType currentBedOperationStatus = NSOperation.BedOperationType.NONE;
    private boolean isPresetPressedDown = false;
    //MARK END :  BLE Vars End

    //MARK : Bed setting vars
    private Handler setBedSettingRetryHandler = new Handler();
    private Runnable setBedSettingRetryTimer;
    //TODO : set proper contants
    private int BED_SETTING_MAX_RETRY = NemuriConstantsModel.get().mattressOperationMaxRetry;
    private float BED_SETTING_OPERATION_TIMEOUT = NemuriConstantsModel.get().mattressOperationTimeout;
    private int bedSettingRetryCount;
    private MattressSettingModel mattressSettingModel = MattressSettingProvider.getSetting().getUnmanaged();
    private MattressHardnessSettingModel selectedHardness = FormPolicyProvider.getDefaultMattressHardnessSetting();
    private int selectedHardnessIndex = 0;
    //MARK END :  Bed setting vars

    // 現在の高さアニメーション
    private enum HeightAnimation {
        None,   // アニメーションなし
        Up,     // 上昇アニメーション
        Down,   // 下降アニメーション
    }
    private HeightAnimation heightAnimation = HeightAnimation.None;

    private final int showTiltIconThreshold = 2;    // 傾斜マーク表示閾値
    private final int tiltIconFadeStopTime = 2000;  // この時間（ms）の間、傾斜が変化しなかったら、傾斜マークの点滅を終了する
    private Handler tiltIconFadeHandler = new Handler();
    private Runnable tiltIconFadeRunnable = new Runnable() {
        @Override
        public void run() {
            tiltIconImage.clearAnimation();
            tiltIconImage.setAlpha(1.0f);
        }
    };

    private boolean isFirstOperated = false; // 初回操作済みか否か

    // 高さ閾値と比較して、どの高さからの操作開始かを表す
    private enum StartOperation_HeightPosition {
        Higher, // 高さ閾値より高い
        Equal,  // 高さ閾値と等しい
        Lower,  // 高さ閾値より低い
    }
    private StartOperation_HeightPosition startOperation_heightPosition = StartOperation_HeightPosition.Higher;

    private final float legHeightCalcValue = 70.8f;
    private final int tiltThreshold = 2;    // 傾斜角度がこの数値以上の場合は、傾斜有りと判断

    private final int legHeightAddValue = 4;    // 足先高さと比較する場合に、高さ閾値に加算する数値
    private final int minimumPopupHeightAddValue = -3;    // ポップアップ表示の最低高さを求めるため高さ閾値に加算する数値
    private final int heightAnimationStopTime = 1;  // この時間（秒）の間、高さが変化しなかったら、高さアニメーションを停止する
    private final int checkHeightWarningTime = 2;   // この時間（秒）の間、ベッド位置情報が変化しなかったら、高さ警告ポップアップ表示の判定を行う

    private Date headLastUpdateTime = new Date();
    private Date legLastUpdateTime = new Date();
    private Date heightLastUpdateTime = new Date();
    private Date tiltLastUpdateTime = new Date();

    @BindView(R.id.debugTiltIndicator)
    TextView debugTiltIndicator;

    @BindView(R.id.debugLegHeightIndicator)
    TextView debugLegHeightIndicator;

    @BindView(R.id.debugTargetHead)
    TextView debugTargetHead;

    @BindView(R.id.debugTargetLeg)
    TextView debugTargetLeg;

    @BindView(R.id.debugTargetHeight)
    TextView debugTargetHeight;

    @BindView(R.id.debugTargetTilt)
    TextView debugTargetTilt;

    @BindView(R.id.debugTargetTitle)
    TextView debugTargetTitle;

    //MARK : Activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        lastStatusTvFootVisibility = false;
        if (savedInstanceState != null) {
            restartActivity();
            return;
        }
        nemuriScanModel = NemuriScanModel.get();
        btnStartBed.setVisibility(View.INVISIBLE);
        btnStartMatress.setVisibility(View.INVISIBLE);
        tvHeightIndicator.setVisibility(View.INVISIBLE);
        tvHeadIndicator.setVisibility(View.INVISIBLE);
        tvFootIndicator.setVisibility(View.INVISIBLE);
        homeService = ApiClient.getClient(getApplicationContext()).create(HomeService.class);
        showProgress();
        initUI();
        initPager();
        if(nemuriScanModel != null && nemuriScanModel.onlyMattress()){
            btnMattress.setChecked(true);
            toggleMatress();
        }

        Integer bedType = nemuriScanModel == null ? null : nemuriScanModel.getInfoType();
        DeviceTemplateProvider.getDeviceTemplate(this, this, UserLogin.getUserLogin().getId(), bedType);

        if (UserLogin.getUserLogin() != null) {
            try {
                LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), "open_screen", "UI000610", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber(), "UI000610");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            debugTargetTitle.setVisibility(View.VISIBLE);
        }

        if (DisplayUtils.FONTS.bigFontStatus(RemoteActivity.this)) {
            tvHeadIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvHeightIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvFootIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            DisplayUtils.SCREEN.isSupportedScreen(this, new DisplayUtils.SCREEN.SupportScreenListener() {
                @Override
                public void isHD(DisplayUtils.DisplayProperty displayProperty) {
                    tvHeadIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tvHeightIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tvFootIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    if (displayProperty.densityDPI == 240) {

                    } else if (displayProperty.densityDPI == 320) {

                    } else {

                    }
                }

                @Override
                public void isHD2(DisplayUtils.DisplayProperty displayProperty) {
                    tvHeadIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tvHeightIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    tvFootIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                }

                @Override
                public void isHD2Plus(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 240) {

                    }
                }

                @Override
                public void isWXGA(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 320) {

                    }
                }

                @Override
                public void isHDPlus(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 320) {

                    }
                }

                @Override
                public void isFHD(DisplayUtils.DisplayProperty displayProperty) {
                    tvHeadIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tvHeightIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    tvFootIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    if (displayProperty.densityDPI == 320) {

                    }
                    if (displayProperty.densityDPI == 480) {

                    }
                }

                @Override
                public void isFHD2(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 480) {
                    }
                }

                @Override
                public void isFHDPlus(DisplayUtils.DisplayProperty displayProperty) {

                }

                @Override
                public void isFHD3(DisplayUtils.DisplayProperty displayProperty) {

                }

                @Override
                public void isFHD4(DisplayUtils.DisplayProperty displayProperty) {

                }

                @Override
                public void isFHD5(DisplayUtils.DisplayProperty displayProperty) {

                }

                @Override
                public void isFHD6(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 440) {

                    }
                }

                @Override
                public void isQHD(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 480) {

                    }
                    if (displayProperty.densityDPI == 640) {

                    }
                }

                @Override
                public void isQHD2(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 480) {

                    }
                }

                @Override
                public void isWQHDPlus(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 480) {

                    }
                }

                @Override
                public void isWQHD2Plus(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 480) {

                    }
                    if (displayProperty.densityDPI == 640) {

                    }
                }

                @Override
                public void isWQHD3(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 480) {

                    }
                }

                @Override
                public void isWQHD4(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 640) {

                    }
                }

                @Override
                public void isUHDMin(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 640) {
                        tvHeadIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        tvHeightIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        tvFootIndicator.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        btnBed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        btnMattress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                        btnMatressConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
                    }
                }

                @Override
                public void isQHDPlus(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 640) {

                    }
                }

                @Override
                public void isUHD(DisplayUtils.DisplayProperty displayProperty) {
                    if (displayProperty.densityDPI == 640) {

                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            PermissionUtil.showLocationPermissionDialogAlert(RemoteActivity.this, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    hideProgress();
                    purgeBLE();
                    disableBedUI();
                    disableMattressUI();
                    dialogInterface.dismiss();
                }

                @Override
                public void onPermissionGranted() {
                    isLocationPermissionRejected = false;
                    tryToConnectBLE();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LOG HERE NS_REMOTE_CLOSED
        LogUserAction.sendNewLog(userService, "NS_REMOTE_CLOSED", "1", "", "UI000610");
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);
        freeCommandHandler.removeCallbacks(freeCommandTimer);
        presetCommandHandler.removeCallbacks(presetCommandTimer);
        getPositionHandler.removeCallbacks(getPositionTimer);
        if (isNemuriScanInitiated) {

            Logger.d("isIntentionalDC showProgress ondestroy");
            isIntentionalDC = true;
            if (nsManager != null) {
                nsManager.disconnectCurrentDevice();
            }
        }
        EventBus.getDefault().unregister(this);
        HomeActivity.isCalendarOrDetailVisible = false;
    }

    public void restartActivity() {
        finish();
        Intent intent = new Intent(RemoteActivity.this, RemoteActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInBackground = true;
        purgeBLE();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInBackground && !isLocationPermissionRejected) {
            runOnUiThread(() -> showLocationPermissionDialogAlert());
        }
        isInBackground = false;
        AlarmsQuizModule.run(this);

        if (nsManager != null) {
            nsManager.setDelegate(this);
        }
    }


    @Override
    protected int getStatusBarTheme() {
        return STATUS_BAR_DARK;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_remote;
    }

    @Override
    public void onBackPressed() {
        if (dialogSettingBed.getVisibility() == View.VISIBLE) {
            bedSettingController.onCloseTap();
            return;
        }
        if (dialogSettingMattress.getVisibility() == View.VISIBLE) {
            mattressSettingController.onCloseTap();
            return;
        }
        if (progressDialogs.isShowing()) {
            return;
        }
        if (isBack) {
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.explode, R.anim.godown);
    }
    //MARK END: Activity lifecycle

    //MARK : Initialization
    private void initUI() {
        dimmingPatternUp.add(new float[]{0.5f, 0.5f, 1});
        dimmingPatternUp.add(new float[]{0.5f, 1, 0.75f});
        dimmingPatternUp.add(new float[]{1, 0.75f, 0.5f});

        dimmingPatternDown.add(new float[]{1, 0.5f, 0.5f});
        dimmingPatternDown.add(new float[]{0.75f, 1, 0.5f});
        dimmingPatternDown.add(new float[]{0.5f, 0.75f, 1});

        overridePendingTransition(R.anim.goup, R.anim.explode);
    }

    private void initPager() {
        bedPresetFragment = new BedPresetFragment();
        bedPresetFragment.listener = this;

        Fragment[] bedFragments = new Fragment[2];
        bedFragments[0] = bedPresetFragment;

        bedManualFragment = new BedManualFragment();
        bedManualFragment.listener = this;

        bedFragments[1] = bedManualFragment;

        FragmentPagerAdapter bedAdapterViewPager = new CardPagerAdapter(getSupportFragmentManager(), bedFragments);
        bedCardPager.setAdapter(bedAdapterViewPager);
        bedCardPager.setClipToPadding(false);
        bedCardPager.setPageMargin(-15);
        bedCardPager.setPadding(40, 3, 40, 5);
        bedCardPager.setOffscreenPageLimit(2);
        bedCardPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPagerType = RemotePagerType.values()[position];
                txtStartBed.clearAnimation();

                if (currentPagerType == RemotePagerType.FREE || currentNSSpec.getBedType() == NSSpec.BED_MODEL.INTIME || currentNSSpec.getBedType() == NSSpec.BED_MODEL.INTIME_COMFORT) {
                    btnStartBed.animate().alpha(0.0f).translationY(btnStartBed.getHeight());
//                    btnStartBed.setVisibility(View.INVISIBLE);
                } else if (currentPagerType == RemotePagerType.PRESET) {
                    btnStartBed.setVisibility(View.VISIBLE);
                    btnStartBed.animate().alpha(1.0f).translationY(0);
                }
                clearBedPresetUI();
                applyLockWhenBedExist();
                if (!PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) {
                    disableBedUI();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Fragment[] matressFragments = new Fragment[3];
        mattressPresetFragment = new MatressPresetFragment();
        mattressPresetFragment.listener = this;
        matressFragments[0] = mattressPresetFragment;

        mattressManualFragment = new MatressManualFragment();
        mattressManualFragment.listener = this;
        matressFragments[1] = mattressManualFragment;

        mattressRecomendFragment = new MatresRecommendFragment();
        mattressRecomendFragment.setMattressSettingModel(mattressSettingModel);
        mattressRecomendFragment.listener = this;
        matressFragments[2] = mattressRecomendFragment;

        FragmentPagerAdapter matressAdapterViewPager = new CardPagerAdapter(getSupportFragmentManager(), matressFragments);
        mattressCardPager.setAdapter(matressAdapterViewPager);
        mattressCardPager.setClipToPadding(false);
        mattressCardPager.setPageMargin(-15);
        mattressCardPager.setPadding(40, 3, 40, 5);
        mattressCardPager.setOffscreenPageLimit(3);
        mattressCardPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //reset view
                currentPagerType = RemotePagerType.values()[position];
                Log.d("CURRENTPAGE", "onPageSelected: "+currentPagerType);
                if (currentPagerType == RemotePagerType.PRESET) {
                    mattressPresetFragment.clearSelection();
                    setHighlightAllMattressSegments(currentNSSpec.isMattressExist());
                    btnCloseMattress.setBackgroundResource(R.drawable.selector_img_remote);
                } else if (currentPagerType == RemotePagerType.FREE) {
                    setMattressNumericIndicator(currentMattressPosition);
                    mattressManualFragment.clearSelection();
                    setHighlightAllMattressSegments(false);
                    btnCloseMattress.setBackgroundResource(R.drawable.selector_img_remote);
                }else if (currentPagerType == RemotePagerType.RECOMMEND) {
                    LogUserAction.sendNewLog(userService, "CHALENGE_MATTRESS_SHOW", "", "", "UI000670");
                    mattressRecomendFragment.clearSelection();
                    setHighlightAllMattressSegments(currentNSSpec.isMattressExist());
                    btnCloseMattress.setBackgroundResource(R.drawable.selector_img_remote);
                }
                clearMattressUI();
                setMattressStartBlinking(false);
                userWarnedHeight = false;
                isFirstOperated = false;
                mattressPresetSelectAdjusted = false;
                if (!PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) {
                    disableMattressUI();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                txtStartMatress.clearAnimation();
                isStart = false;
            }
        });
    }

    private void initBLE() {
        //BLE initialization
        if (nemuriScanModel != null) {
            if (!nemuriScanModel.getSerialNumber().startsWith("F")) { //dummy
//                nsManager = NSManager.getInstance(this, this);
                runOnUiThread(() -> showLocationPermissionDialogAlert());
            } else {
                new Handler().postDelayed(() -> runOnUiThread(() -> {
                    if (!RemoteActivity.this.isFinishing()) {
                        hideProgress();
                        applyNSSpec();
                        checkingNSSpec();
                    }
                }), 3000);
            }
        } else {
            //Waiting for Fragment Child Created
            btnStartBed.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> runOnUiThread(() -> {
                if (!RemoteActivity.this.isFinishing()) {
                    isNemuriScanMissing = true;
                    hideProgress();
                    applyNoNSUI();
                    DialogUtil.createSimpleOkDialogLink(RemoteActivity.this, "",
                            LanguageProvider.getLanguage("UI000610C030"),
                            LanguageProvider.getLanguage("UI000610C043"), (dialogInterface, i) -> {
                                Intent faqIntent = new Intent(RemoteActivity.this, FaqActivity.class);
                                faqIntent.putExtra("ID_FAQ", "UI000610C043");
                                faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(faqIntent);
                                dialogInterface.dismiss();
                            }, LanguageProvider.getLanguage("UI000610C031"), (dialogInterface, i) -> dialogInterface.dismiss());
                }
            }), 3000);

        }
    }
    //MARK END : Initialization

    private void checkingNSSpec() {
        shouldNotifyBedMissing = !currentNSSpec.isBedExist();
        shouldNotifyMattressMissing = !currentNSSpec.isMattressExist();
        if (currentNSSpec.isBedExist() && currentNSSpec.isMattressExist()) {
            btnBed.performClick();
        } else if (currentNSSpec.isBedExist() && !currentNSSpec.isMattressExist()) {
            btnBed.performClick();
        } else if (!currentNSSpec.isBedExist() && currentNSSpec.isMattressExist()) {
            btnMattress.performClick();
        }
    }

    //MARK : UI functions
    private void applyNSSpec() {
        if (!currentNSSpec.isBedExist()) {
            disableBedUI();
        } else {
            enableBedUI();
        }
        if (!currentNSSpec.isMattressExist()) {
            disableMattressUI();
        } else {
            enableMattressUI();
        }
        if (currentBedSpec.isHeightLockSupported()) {
//            btnStartBed.animate().alpha(0.0f).translationY(btnStartBed.getHeight());
        }
        runOnUiThread(() -> {
            if (NemuriScanModel.get() != null && PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) {
                btnStartBed.setVisibility(View.VISIBLE);
                btnStartMatress.setVisibility(View.VISIBLE);
                if (!currentBedSpec.isHeightLockSupported()) {
                    tvHeightIndicator.setVisibility(View.GONE);
                } else {
                    tvHeightIndicator.setVisibility(View.VISIBLE);
                }
                tvHeadIndicator.setVisibility(View.VISIBLE);
                tvFootIndicator.setVisibility(View.VISIBLE);
            } else {
                tvHeightIndicator.setVisibility(View.INVISIBLE);
                tvHeadIndicator.setVisibility(View.INVISIBLE);
                tvFootIndicator.setVisibility(View.INVISIBLE);
            }

            if (!lastStatusTvFootVisibility) {
                tvHeadIndicator.setVisibility(View.INVISIBLE);
                tvFootIndicator.setVisibility(View.INVISIBLE);
            }
            //Keep Button Start : Issue 909
            btnStartBed.setVisibility(View.VISIBLE);
            btnStartMatress.setVisibility(View.VISIBLE);

            if (currentPagerType == RemotePagerType.FREE || (currentTabType == RemoteTabType.BED && currentNSSpec.getBedType() == NSSpec.BED_MODEL.INTIME || currentNSSpec.getBedType() == NSSpec.BED_MODEL.INTIME_COMFORT)) {
                btnStartBed.setVisibility(View.INVISIBLE);
            }
            //TO Set Default By Last Persistence Data
            if (!lastStatusTvFootVisibility) {
                if (NemuriScanModel.get() != null) {
                    Integer lastBedModel = NemuriScanModel.get().getInfoType() != null && NemuriScanModel.get().getInfoType() == 2 ? 1 : 2;
                    currentNSSpec.setBedModel(lastBedModel);
                    currentBedSpec.setHeightLockSupported(NemuriScanModel.get().isHeightSupported());
                }
            }
        });
        bedPresetFragment.setIsTouchHoldMode(currentNSSpec.getBedType() == NSSpec.BED_MODEL.INTIME || currentNSSpec.getBedType() == NSSpec.BED_MODEL.INTIME_COMFORT);
        bedManualFragment.setHeightAvailable(currentBedSpec.isHeightLockSupported());
        applyLockWhenBedExist();
        hideProgress();
    }

    private void applyBedLock() {
        if (currentSetting.isLockSettingDiffrent(previousSetting)) {
            applyLockWhenBedExist();
        }
    }

    private void startBedPresetUI() {
        if (bedPresetFragment != null) bedPresetFragment.showStopUI();
        disableNavigationUI();
        setBedStartBlinking(false);
    }

    private void stopBedPresetUI() {
        clearBedPresetUI();
        if (bedPresetFragment != null) bedPresetFragment.showStartUI();
        enableNavigationUI();
    }

    private void startBedPresetHoldUI() {
        disableNavigationUI();
        setBedStartBlinking(false);
    }

    private void clearBedPresetUI() {
        if (bedPresetFragment != null) bedPresetFragment.clearSelection();
        animateBedShadow(currentBedPosition);
        setBedStartBlinking(false);
    }

    private void enableBedUI() {
        runOnUiThread(() -> {
            if (!PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) {
                disableBedUI();
                return;
            }
            bedCardPager.setSwipeLocked(false);
            if (bedPresetFragment != null) bedPresetFragment.enableUI();
            if (bedManualFragment != null) bedManualFragment.enableUI();
            bedSettingController.enableUI();
            btnStartBed.setEnabled(true);
            if (currentTabType == RemoteTabType.BED) {
                //only mutate common buttons when the tab is relevant
                btnSetting.setEnabled(true);
            }
        });
    }

    private void disableBedUI() {
        runOnUiThread(() -> {
            if (!lastStatusTvFootVisibility) {
                if (NemuriScanModel.get() != null) {
                    Integer lastBedModel = NemuriScanModel.get().getInfoType() != null && NemuriScanModel.get().getInfoType() == 2 ? 1 : 2;
                    currentNSSpec.setBedModel(lastBedModel);
                    currentBedSpec.setHeightLockSupported(NemuriScanModel.get().isHeightSupported());
                }
            }
            btnStartBed.setVisibility(currentNSSpec.getBedType() == NSSpec.BED_MODEL.INTIME || currentNSSpec.getBedType() == NSSpec.BED_MODEL.INTIME_COMFORT ? View.INVISIBLE : View.VISIBLE);
            bedCardPager.setSwipeLocked(false);
            if (bedPresetFragment != null) bedPresetFragment.disableUI();
            if (bedManualFragment != null) bedManualFragment.disableUI();
            bedSettingController.disableUI();
            btnStartBed.setEnabled(false);
            if (currentTabType == RemoteTabType.BED) {
                //only mutate common buttons when the tab is relevant
                btnSetting.setEnabled(false);
            }
        });
    }

    private void enableMattressUI() {
        runOnUiThread(() -> {
            if (bedMattressCheck==BedMattressCheck.ACTIVE &
                    !PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) {
                disableMattressUI();
                return;
            }
            mattressCardPager.setSwipeLocked(false);
            if (mattressPresetFragment != null) mattressPresetFragment.enableUI();
            if (mattressManualFragment != null) mattressManualFragment.enableUI();
            if (mattressRecomendFragment != null) mattressRecomendFragment.enableUI();
            mattressSettingController.enableUI();
            btnStartMatress.setEnabled(true);
            if (currentTabType == RemoteTabType.MATTRESS) {
                //only mutate common buttons when the tab is relevant
                btnSetting.setEnabled(true);
            }
            ViewCollections.run(mattressVisualIndicators, (view, index) -> setHighlightMattressSegment(index, true));
            if(mattressRecomendFragment != null && mattressRecomendFragment.isHistoryShowing()){
                setMattressHistoryUI(false);
            }
        });
    }

    private void disableMattressUI() {
        if(bedMattressCheck==BedMattressCheck.ACTIVE) {
            runOnUiThread(() -> {
                mattressCardPager.setSwipeLocked(false);
                if (mattressPresetFragment != null) mattressPresetFragment.disableUI();
                if (mattressManualFragment != null) mattressManualFragment.disableUI();
                if (mattressRecomendFragment != null) mattressRecomendFragment.disableUI();
                mattressSettingController.disableUI();
                btnStartMatress.setEnabled(false);
                setMattressNumericIndicatorDisable();
                if (currentTabType == RemoteTabType.MATTRESS) {
                    //only mutate common buttons when the tab is relevant
                    btnSetting.setEnabled(false);
                }
                ViewCollections.run(mattressVisualIndicators, (view, index) -> {
                    setHighlightMattressSegment(index, false);
                    setMattressSegmentBlinking(index, false);
                });
            });
        } else enableMattressUI();
    }

    private void startMattressRecommendUI() {
        if (mattressRecomendFragment != null) mattressRecomendFragment.disableAllButton();
        disableNavigationUI();
        setMattressStartBlinking(false);
    }

    private void stopMattressRecommendUI() {
        if (mattressRecomendFragment != null) mattressRecomendFragment.enableAllButton();
        clearMattressUI();
        enableNavigationUI();
    }

    private void startMattressPresetUI() {
        if (mattressPresetFragment != null) mattressPresetFragment.disableAllButton();
        disableNavigationUI();
        setMattressStartBlinking(false);
    }

    private void startMattressFukattoUI() {
        setMattressStartBlinking(false);
        if (mattressCardPager.getCurrentItem() != 0) {
            mattressCardPager.setCurrentItem(0);
        }
        mattressPresetFragment.clearSelection();//clear selection upon fukkato start
        setMattressFukattoUI(false);
    }

    private void stopMattressFukattoUI() {
        clearMattressUI();
        setMattressFukattoUI(true);
    }

    private void stopMattressPresetUI() {
        if (mattressPresetFragment != null) mattressPresetFragment.enableAllButton();
        clearMattressUI();
        enableNavigationUI();
    }

    private void startMattressFreeUI() {
        if (mattressManualFragment != null) mattressManualFragment.disableAllButton();
        disableNavigationUI();
        setMattressStartBlinking(false);
    }

    private void stopMattressFreeUI() {
        if (mattressManualFragment != null) mattressManualFragment.enableAllButton();
        clearMattressUI();
        enableNavigationUI();
    }

    private void clearMattressUI() {
        if (mattressManualFragment != null) mattressManualFragment.clearSelection();
        if (mattressPresetFragment != null) mattressPresetFragment.clearSelection();
        if (mattressRecomendFragment != null) mattressRecomendFragment.clearSelection();
        setMattressStartBlinking(false);
    }

    private void setHighlightAllMattressSegments(boolean shouldTurnOn) {
        for (int i = 0; i < mattressVisualIndicators.size(); i++) {
            setHighlightMattressSegment(i, shouldTurnOn);
        }
        //
    }

    private void highlightSingletMattressSegment(int index) {
        for (int i = 0; i < mattressVisualIndicators.size(); i++) {
            if (i == index)
                setHighlightMattressSegment(i, true);
            else
                setHighlightMattressSegment(i, false);
        }
    }

    private void setHighlightMattressSegment(int index, boolean shouldTurnOn) {
        View targetSegment = mattressVisualIndicators.get(index);

        Logger.w("setMattressSegmentBlinking 3/" + index + "/" + shouldTurnOn);
        switch (index) {
            case 0:
                if (shouldTurnOn)
                    targetSegment.setBackground(getDrawable(R.drawable.mattress_segment_right_flat));
                else
                    targetSegment.setBackground(getDrawable(R.drawable.mattress_segment_right_flat_off));
                break;
            case 1:
                if (shouldTurnOn)
                    targetSegment.setBackground(getDrawable(R.drawable.mattress_segment_both_flat));
                else
                    targetSegment.setBackground(getDrawable(R.drawable.mattress_segment_both_flat_off));
                break;
            case 2:
                if (shouldTurnOn)
                    targetSegment.setBackground(getDrawable(R.drawable.mattress_segment_left_flat));
                else
                    targetSegment.setBackground(getDrawable(R.drawable.mattress_segment_left_flat_off));
                break;
            default:
                if (shouldTurnOn)
                    targetSegment.setBackground(getDrawable(R.drawable.bed_segment));
                else
                    targetSegment.setBackground(getDrawable(R.drawable.bed_segment_off));
                break;
        }
    }

    private String prepareMattressDisplayValue(int value) {
        if (value >= 1 && value <= 10) {
            return String.valueOf(value);
        } else {
            return "-";
        }
    }

    private void setMattressNumericIndicator(NSMattressPosition target) {
        if (!currentNSSpec.isMattressExist()) {
            setMattressNumericIndicatorDisable();
            return;
        }
        mattressNumericIndicators.get(0).setText(prepareMattressDisplayValue(target.getHead()));
        mattressNumericIndicators.get(1).setText(prepareMattressDisplayValue(target.getShoulder()));
        mattressNumericIndicators.get(2).setText(prepareMattressDisplayValue(target.getHip()));
        mattressNumericIndicators.get(3).setText(prepareMattressDisplayValue(target.getThigh()));
        mattressNumericIndicators.get(4).setText(prepareMattressDisplayValue(target.getCalf()));
        mattressNumericIndicators.get(5).setText(prepareMattressDisplayValue(target.getFeet()));
    }

    private void setMattressNumericIndicatorDisable() {
        mattressNumericIndicators.get(0).setText("-");
        mattressNumericIndicators.get(1).setText("-");
        mattressNumericIndicators.get(2).setText("-");
        mattressNumericIndicators.get(3).setText("-");
        mattressNumericIndicators.get(4).setText("-");
        mattressNumericIndicators.get(5).setText("-");
    }

    public void enableNavigationUI() {
        bedCardPager.setSwipeLocked(false);
        mattressCardPager.setSwipeLocked(false);
        btnStartBed.setEnabled(true);
        btnStartMatress.setEnabled(true);

        btnCloseBad.setEnabled(true);
        btnCloseBad.setAlpha(1f);

        btnCloseMattress.setEnabled(true);
        btnCloseMattress.setAlpha(1f);

        btnBed.setEnabled(true);
        btnBed.setAlpha(1f);

        btnMattress.setEnabled(true);
        btnMattress.setAlpha(1f);

        btnHelp.setEnabled(true);
        btnHelp.setAlpha(1f);

        btnSetting.setEnabled(true);
        btnSetting.setAlpha(1f);
        new Handler().postDelayed(() -> {
            HomeActivity.REMOTEACTIVE = false;
            if (bedPresetFragment != null) bedPresetFragment.setIsBedOperationRunning(false);
            if (mattressPresetFragment != null)
                mattressPresetFragment.setIsFukattoOperationRunning(false);
            isBack = true;
        }, 500);
    }

    public void disableNavigationUI() {
        bedCardPager.setSwipeLocked(true);
        mattressCardPager.setSwipeLocked(true);
        txtStartBed.clearAnimation();

        btnStartMatress.setEnabled(false);
        btnStartBed.setEnabled(false);

        btnCloseBad.setEnabled(false);
        btnCloseBad.setAlpha(0.5f);

        btnCloseMattress.setEnabled(false);
        btnCloseMattress.setAlpha(0.5f);

        btnBed.setEnabled(false);
        btnBed.setAlpha(0.5f);

        btnMattress.setEnabled(false);
        btnMattress.setAlpha(0.5f);

        btnHelp.setEnabled(false);
        btnHelp.setAlpha(0.5f);

        btnSetting.setEnabled(false);
        btnSetting.setAlpha(0.5f);

        isBack = false;
    }

    public void setMattressFukattoUI(boolean isEnabled) {
        if(isEnabled && mattressRecomendFragment != null && mattressRecomendFragment.isHistoryShowing()){
            return;
        }
        btnStartBed.setEnabled(isEnabled);
        btnHelp.setAlpha(isEnabled ? 1f : 0.5f);

        btnStartMatress.setEnabled(isEnabled);
        btnStartMatress.setAlpha(isEnabled ? 1f : 0.5f);


        btnHelp.setEnabled(isEnabled);
        btnHelp.setAlpha(isEnabled ? 1f : 0.5f);

        btnSetting.setEnabled(isEnabled);
        btnSetting.setAlpha(isEnabled ? 1f : 0.5f);

        mattressCardPager.setSwipeLocked(!isEnabled);
    }

    public void setMattressHistoryUI(boolean isEnabled) {
        if (isEnabled && mattressPresetFragment.isFukattoActive()) {
            return;
        }
        btnStartBed.setEnabled(isEnabled);
        btnHelp.setAlpha(isEnabled ? 1f : 0.5f);

        btnStartMatress.setEnabled(isEnabled);
        btnStartMatress.setAlpha(isEnabled ? 1f : 0.5f);


        btnHelp.setEnabled(isEnabled);
        btnHelp.setAlpha(isEnabled ? 1f : 0.5f);

        btnSetting.setEnabled(isEnabled);
        btnSetting.setAlpha(isEnabled ? 1f : 0.5f);

        mattressCardPager.setSwipeLocked(!isEnabled);
    }

    private void showMissingPartAlert() {
        if (currentTabType == RemoteTabType.BED && shouldNotifyBedMissing) {
            shouldNotifyBedMissing = false;
            disableBedUI();
            runOnUiThread(() ->{
                String msgTag = "UI000610C044";
                String btnTag = "UI000610C045";

                if(nemuriScanModel != null){
                    boolean onlyMattress = nemuriScanModel.onlyMattress();
                    if(onlyMattress){
                        msgTag = "UI000610C060";
                        btnTag = "UI000610C061";
                    }
                }
                DialogUtil.createSimpleOkDialog(RemoteActivity.this, "", LanguageProvider.getLanguage(msgTag), LanguageProvider.getLanguage(btnTag), (dialogInterface, i) -> disableBedUI());
            });
        } else if (currentTabType == RemoteTabType.MATTRESS && shouldNotifyMattressMissing) {
            shouldNotifyMattressMissing = false;
            disableMattressUI();
            runOnUiThread(() -> DialogUtil.createSimpleOkDialog(RemoteActivity.this, "", LanguageProvider.getLanguage("UI000610C046"), LanguageProvider.getLanguage("UI000610C047"), (dialogInterface, i) -> disableMattressUI()));
        }
    }

    private void showHeightThresholdAlert() {
        //Change With Dialog Icon
        runOnUiThread(() -> DialogUtil.createSimpleOkDialogWithIcon(this, LanguageProvider.getLanguage("UI000610C027"),
                LanguageProvider.getLanguage("UI000610C028"), LanguageProvider.getLanguage("UI000610C029"),
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    userWarnedHeight = true;
                }));
    }

    private void showMismatchButtonCodeAlert() {
        //Change With Dialog Icon
        runOnUiThread(() -> DialogUtil.createSimpleOkDialogWithIcon(this, LanguageProvider.getLanguage("UI000610C053"),
                LanguageProvider.getLanguage("UI000610C051"), LanguageProvider.getLanguage("UI000610C052"),
                (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    mismatchButtonCodeCounter = 0;
                }));
    }

    private void showFailCodeAlert(int failCode) {
        purgeBLE();
        runOnUiThread(() ->
                DialogUtil.createSimpleOkDialogLink(RemoteActivity.this, "",
                        LanguageProvider.getLanguage("UI000610C036").replace("%ERROR_CODE%", NSFailCode.bedHumanReadableError(failCode)),
                        LanguageProvider.getLanguage("UI000610C041"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(RemoteActivity.this, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000610C041");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        }, LanguageProvider.getLanguage("UI000610C037"), (dialogInterface, i) -> {
                            disableBedUI();
                            tryToConnectBLE();
                            dialogInterface.dismiss();
                        }));

    }

    private void showFailCodeAlertMattressH(int failCode) {
        runOnUiThread(() ->
                DialogUtil.createSimpleOkDialogLink(RemoteActivity.this, "",
                        LanguageProvider.getLanguage("UI000630C013").replace("%ERROR_CODE%", NSFailCode.mattressHumanReadableError(failCode)),
                        LanguageProvider.getLanguage("UI000630C018"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(RemoteActivity.this, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000630C018");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        }, LanguageProvider.getLanguage("UI000630C014"), (dialogInterface, i) -> {
                            disableMattressUI();
                            dialogInterface.dismiss();
                        }));

    }

    private void showFailCodeAlertMattressNonH(int failCode) {
        runOnUiThread(() ->
                DialogUtil.createYesNoDialogLink(RemoteActivity.this, "", LanguageProvider.getLanguage("UI000630C015").replace("%ERROR_CODE%", NSFailCode.mattressHumanReadableError(failCode)),
                        LanguageProvider.getLanguage("UI000630C019"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(RemoteActivity.this, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000630C019");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        }, LanguageProvider.getLanguage("UI000630C016"), (dialogInterface, i) -> tryToConnectBLE(),
                        LanguageProvider.getLanguage("UI000630C017"), (dialogInterface, i) -> {
                            disableMattressUI();
                            dialogInterface.dismiss();
                        }
                ));

    }

    private void applyNoNSUI() {
        disableBedUI();
        disableMattressUI();
        runOnUiThread(() -> {
            if (nemuriScanModel == null) {
                nemuriScanModel = new NemuriScanModel();
                nemuriScanModel.setInfoType(NSSpec.BED_MODEL.ACTIVE_SLEEP);
                nemuriScanModel.setHeightSupported(false);
            }
            if (nemuriScanModel.getInfoType() == 1) {
                currentNSSpec.setBedModel(NSSpec.BED_MODEL.ACTIVE_SLEEP.ordinal());
            } else if (nemuriScanModel.getInfoType() == 3) {
                currentNSSpec.setBedModel(NSSpec.BED_MODEL.INTIME_COMFORT.ordinal());
            } else {
                currentNSSpec.setBedModel(NSSpec.BED_MODEL.INTIME.ordinal());
            }
            currentBedSpec.setHeightLockSupported(nemuriScanModel.isHeightSupported());
            applyNSSpec();
        });

    }
    //MARK END :  UI functions

    //MARK : animation methods
    private void setBedStartBlinking(boolean shouldStart) {
        if (shouldStart) {
            txtStartBed.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink));
        } else {
            txtStartBed.clearAnimation();
        }
    }

    private void animateBed(NSBedPosition targetPosition) {
        // 現在のベッド情報とレスポンスのベッド情報を比較して、差分があったら各データごとに最終更新時間を更新する
        if (targetPosition.getHead() != currentBedPosition.getHead()) headLastUpdateTime = new Date();
        if (targetPosition.getLeg() != currentBedPosition.getLeg()) legLastUpdateTime = new Date();
        if (targetPosition.getHeight() != currentBedPosition.getHeight()) heightLastUpdateTime = new Date();
        if (targetPosition.getTilt() != currentBedPosition.getTilt()) tiltLastUpdateTime = new Date();

        setHeadBed(targetPosition);
        setHeightBed(targetPosition);
        setFootBed(targetPosition);
        setTiltBed(targetPosition);
    }

    private void animateBedShadow(NSBedPosition targetPosition) {
        setHeadShadBed(targetPosition);
        setFootBedShad(targetPosition);
    }

    @SuppressLint("SetTextI18n")
    private void setHeadBed(NSBedPosition target) {
        runOnUiThread(() -> {
            int adjustedOrigin = RemoteAnimationUtil.getAdjustedHead(currentBedPosition.getHead());
            int adjustedTarget = RemoteAnimationUtil.getAdjustedHead(target.getHead());

            RotateAnimation rotateAnimation = new RotateAnimation(adjustedOrigin, adjustedTarget, RotateAnimation.RELATIVE_TO_SELF, 1f, RotateAnimation.RELATIVE_TO_SELF, 0.3f);
            rotateAnimation.setDuration(1000);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setFillEnabled(true);
            bedHeadSegment.startAnimation(rotateAnimation);
            tvHeadIndicator.setText(String.valueOf(target.getHead()) + "°");
            currentBedPosition.setHead(target.getHead());
        });
    }

    @SuppressLint("SetTextI18n")
    private void setHeightBed(NSBedPosition target) {
        int diff = target.getHeight() - currentBedPosition.getHeight();
        if ((isArrowPressedDown || isPresetPressedDown) && diff != 0) {
            animateNSHeightArrow(diff > 0 ? HeightAnimation.Up : HeightAnimation.Down);
        }

        //no animation,only info display
        runOnUiThread(() -> tvHeightIndicator.setText(target.getHeight() + "cm"));
        currentBedPosition.setHeight(target.getHeight());
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            calcLegHeight(currentBedPosition.getHeight(), currentBedPosition.getTilt());
        }

        // 高さが閾値より大きくなったら高さ警告ポップアップの表示フラグをリセット
        if ((currentBedPosition.getTilt() < tiltThreshold && currentBedPosition.getHeight() > nemuriConstantsModel.heightWarningThreshold) ||
            (currentBedPosition.getTilt() >= tiltThreshold && calcLegHeight(currentBedPosition.getHeight(), currentBedPosition.getTilt()) > calcLegHeightWarningThreshold())) {
            userWarnedHeight = false;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setFootBed(NSBedPosition target) {
        runOnUiThread(() -> {
            RotateAnimation rotateAnimation = new RotateAnimation(-1 * currentBedPosition.getLeg(), -1 * target.getLeg(), RotateAnimation.RELATIVE_TO_SELF, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.3f);
            rotateAnimation.setDuration(1000);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setFillEnabled(true);

            float currentDegressLeg = RemoteAnimationUtil.getValueFromDegree(currentBedPosition.getLeg());
            float currentXValue = -RemoteAnimationUtil.getDeltaX(currentDegressLeg);
            float currentYValue = -RemoteAnimationUtil.getDeltaY(currentDegressLeg);

            float targetDegressLeg = RemoteAnimationUtil.getValueFromDegree(target.getLeg());
            float toXValue = -RemoteAnimationUtil.getDeltaX(targetDegressLeg);
            float toYValue = -RemoteAnimationUtil.getDeltaY(targetDegressLeg);

            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, currentXValue,
                    Animation.RELATIVE_TO_SELF, toXValue,
                    Animation.RELATIVE_TO_SELF, currentYValue,
                    Animation.RELATIVE_TO_SELF, toYValue);
            animation.setDuration(500);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            bedThighSegment.startAnimation(rotateAnimation);
            bedLegSegment.startAnimation(animation);
            currentBedPosition.setLeg(target.getLeg());
            tvFootIndicator.setText(Math.abs(Math.round(target.getLeg())) + "°");
        });
    }

    @SuppressLint("SetTextI18n")
    private void setTiltBed(NSBedPosition target) {
        if (target.getTilt() >= showTiltIconThreshold) {
            tiltIconImage.setVisibility(View.VISIBLE);
            if ((isArrowPressedDown || isPresetPressedDown) && target.getTilt() != currentBedPosition.getTilt()) {
                if (tiltIconImage.getAnimation() == null) fadeOutTiltIcon();    // アニメーション設定がnullなら、アニメーション中ではないと判断してフェードを開始
                tiltIconFadeHandler.removeCallbacks(tiltIconFadeRunnable);
                tiltIconFadeHandler.postDelayed(tiltIconFadeRunnable, tiltIconFadeStopTime);
            }
        } else {
            tiltIconImage.setVisibility(View.INVISIBLE);
        }

        currentBedPosition.setTilt(target.getTilt());
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            runOnUiThread(() -> debugTiltIndicator.setText("傾斜 " + target.getTilt() + "°"));
        }
    }

    private void setHeadShadBed(NSBedPosition target) {
        runOnUiThread(() -> {
            int adjustedOrigin = RemoteAnimationUtil.getAdjustedHead(currentBedPosition.getHead());
            int adjustedTarget = RemoteAnimationUtil.getAdjustedHead(target.getHead());

            RotateAnimation rotateAnimation = new RotateAnimation(adjustedOrigin, adjustedTarget, RotateAnimation.RELATIVE_TO_SELF, 1f, RotateAnimation.RELATIVE_TO_SELF, 0.3f);
            rotateAnimation.setDuration(0);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setFillEnabled(true);
            bedShadHeadSegment.startAnimation(rotateAnimation);
            currentShadowPosition.setHead(target.getHead());
        });
    }

    private void setFootBedShad(NSBedPosition target) {
        runOnUiThread(() -> {
            RotateAnimation rotateAnimation = new RotateAnimation(-1 * currentShadowPosition.getLeg(), -1 * target.getLeg(), RotateAnimation.RELATIVE_TO_SELF, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.3f);
            rotateAnimation.setDuration(0);
            rotateAnimation.setFillAfter(true);
            rotateAnimation.setFillEnabled(true);
            bedShadThighSegment.startAnimation(rotateAnimation);

            float currentDegressLeg = RemoteAnimationUtil.getValueFromDegree(currentShadowPosition.getLeg());
            float currentXValue = -RemoteAnimationUtil.getDeltaX(currentDegressLeg);
            float currentYValue = -RemoteAnimationUtil.getDeltaY(currentDegressLeg);

            float targetDegressLeg = RemoteAnimationUtil.getValueFromDegree(target.getLeg());
            float toXValue = -RemoteAnimationUtil.getDeltaX(targetDegressLeg);
            float toYValue = -RemoteAnimationUtil.getDeltaY(targetDegressLeg);

            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, currentXValue,
                    Animation.RELATIVE_TO_SELF, toXValue,
                    Animation.RELATIVE_TO_SELF, currentYValue,
                    Animation.RELATIVE_TO_SELF, toYValue);
            animation.setDuration(0);
            animation.setFillEnabled(true);
            animation.setFillAfter(true);
            bedShadLegSegment.startAnimation(animation);
            currentShadowPosition.setLeg(target.getLeg());
        });
    }

    private void animateNSHeightArrow(HeightAnimation animation) {
        if (nsArrowAnimator != null && animation != heightAnimation) {
            stopHeightArrowAnimation();
        }

        heightAnimation = animation;
        if (nsArrowAnimator == null) {
            nsArrowAnimator = new Timer();
            nsArrowAnimator.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.SECOND, -heightAnimationStopTime);
                    // 高さが1秒以内に変化している場合はアニメーション継続、変化していない場合アニメーション停止
                    if ((isArrowPressedDown || isPresetPressedDown) && heightLastUpdateTime.after(calendar.getTime())) {
                        runOnUiThread(() -> startHeightArrowAnimation());
                    } else {
                        stopHeightArrowAnimation();
                    }
                }
            }, 0, 300);
        }
    }

    private void startHeightArrowAnimation() {
        ArrayList<float[]> patterns;
        List<View> arrows;
        switch (heightAnimation) {
            case Up:
                patterns = dimmingPatternUp;
                arrows = heightArrowsUp;
                break;
            case Down:
                patterns = dimmingPatternDown;
                arrows = heightArrowsDown;
                break;
            default:
                return;
        }

        float[] value = patterns.get(counterDim);
        for (int i = 0; i < arrows.size(); i++) {
            arrows.get(i).setAlpha(value[i]);
        }
        counterDim = counterDim + 1;
        if (counterDim == patterns.size()) counterDim = 0;
    }

    private void stopHeightArrowAnimation() {
        heightAnimation = HeightAnimation.None;
        if (nsArrowAnimator != null) {
            nsArrowAnimator.cancel();
            nsArrowAnimator.purge();
            nsArrowAnimator = null;
        }
        ViewCollections.run(heightArrowsUp, (view, index) -> view.setAlpha(0));
        ViewCollections.run(heightArrowsDown, (view, index) -> view.setAlpha(0));
    }

    private void setMattressSegmentBlinking(int index, boolean shouldStart) {
        View mattressSegment = mattressVisualIndicators.get(index);
        Animation currentAnimation = mattressSegment.getAnimation();

        Logger.w("setMattressSegmentBlinking 0/" + index + "/" + shouldStart);
        if (shouldStart && (currentAnimation == null || currentAnimation.hasEnded())) {//avoid duplicate animations
            Logger.w("setMattressSegmentBlinking 1");
            mattressSegment.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.mattress_segment_blink));
        } else if (!shouldStart) {
            Logger.w("setMattressSegmentBlinking 2");
            mattressSegment.clearAnimation();
        }
        if (currentPagerType != RemotePagerType.PRESET && currentPagerType != RemotePagerType.RECOMMEND) {
            setHighlightMattressSegment(index, shouldStart);
        }

    }

    private void setMattressStartBlinking(boolean shouldStart) {
        if (shouldStart) {
            txtStartMatress.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink));
        } else {
            txtStartMatress.clearAnimation();
        }
    }

    private void fadeOutTiltIcon() {
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.tilt_icon_fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (tiltIconImage.getAnimation() == null) return;
                fadeInTiltIcon();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        tiltIconImage.startAnimation(fadeOut);
    }

    private void fadeInTiltIcon() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.tilt_icon_fade_in);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (tiltIconImage.getAnimation() == null) return;
                fadeOutTiltIcon();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        tiltIconImage.startAnimation(fadeIn);
    }
    //MARK END : animation methods

    //MARK : BLE related functions
    private void tryToConnectBLE() {
        IOSDialogRight.Dismiss();
        nsManager = NSManager.getInstance(this, this);
        disableBedUI();
        disableMattressUI();
        showProgress();

        //setup connection timeout
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) {
            connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, (nemuriConstantsModel.nsConnectionTimeout) * 1000);
        }

        if (nsManager != null) {
            nsManager.startScan(this);
        }
    }

    private void postDataReconnect() {
        runOnUiThread(() -> {
            Logger.d("afk isIntentionalDC showProgress progressDialogs.isShowing() " + progressDialogs.isShowing());
            if (!progressDialogs.isShowing()) {
                showProgress();
            }
        });


        isNemuriScanInitiated = false;
        isIntentionalDC = true;
        Logger.w("afk trueing isIntentionalDC");

        reconnectNSWaitHandler.removeCallbacks(reconnectNSWaitTimer);
        reconnectNSWaitTimer = () -> {
            if (reconnectNSRetryCount > nemuriConstantsModel.nsPostDataMaxWaitRetry) {
                purgeBLE();
                if (!PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) return;
                Logger.d("afk nemuriScanningTimeoutTimer " + reconnectNSRetryCount + "-" + nemuriConstantsModel.nsPostDataMaxWaitRetry);
                runOnUiThread(() -> DialogUtil.createCustomYesNo(RemoteActivity.this,
                        "",
                        LanguageProvider.getLanguage("UI000610C022"),
                        LanguageProvider.getLanguage("UI000610C026"),
                        (dialogInterface, i) -> {
                            currentNSSpec.setMattressExist(false);
                            currentNSSpec.setBedExist(false);
                            disableBedUI();
                            disableMattressUI();
                        },
                        LanguageProvider.getLanguage("UI000610C025"),
                        (dialogInterface, i) -> {
                            //retry
                            tryToConnectBLE();
                        }
                ));
                runOnUiThread(this::hideProgress);
            } else {
                if (PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) postDataReconnect();
            }
            Logger.d("afk showProgress nemuriScanningTimeoutTimer cb");
        };
        reconnectNSWaitHandler.postDelayed(reconnectNSWaitTimer, (long) (nemuriConstantsModel.nsPostDataMaxWaitDuration * 1000));
        if (nsManager != null) {
            nsManager.startScan(this);
        }
        reconnectNSRetryCount += 1;
    }

    private void purgeBLE() {
        disableBedUI();
        disableMattressUI();
        stopAllCommands();
        stopAllTimers();
        stopAllAnimation();
        isIntentionalDC = true;
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }

    private void stopAllAnimation() {
        runOnUiThread(() -> {
            stopDummyBedPresetAnimation();
            stopDummyBedFreeAnimation();
            stopDummyMattressPresetAnimation();
            stopDummyMattressFreeAnimation();
        });
    }

    private void stopAllCommands() {
        stopPresetOperation();
        stopBedFreeMode(true);
        fingerPressCount = 0;
        if (mattressPresetFragment != null) mattressPresetFragment.abortFukatto();
    }

    private void stopAllTimers() {
        getPositionHandler.removeCallbacks(getPositionTimer);
        sameBedPosHandler.removeCallbacks(sameBedPosTimer);
        setMattressRetryHandler.removeCallbacks(setMattressRetryTimer);
        commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);
        freeCommandHandler.removeCallbacks(freeCommandTimer);
        presetCommandHandler.removeCallbacks(presetCommandTimer);
        setBedSettingRetryHandler.removeCallbacks(setBedSettingRetryTimer);
    }

    //MARK END : ble related functions

    //MARK : BLE free mode functions
    private void scheduleArrowCommand(boolean isFirst) {
        if (currentFreeOperation == NSOperation.FREE_DECREASE_HEIGHT) {
            if (isFirst) {
                updateStartOperationHeightPosition();

                if (!isFirstOperated) {
                    isFirstOperated = true;

                    // 一旦停止判定（初回操作）
                    if (checkHeightWarning_firstOperation(null, null)) {
                        showHeightThresholdAlert(); // 警告ポップアップ表示
                        stopBedFreeMode();  // FREE操作終了
                        return;
                    }
                }
            }
        }

        sendArrowCommand();
        commandRequestCount += 1;
        Logger.v("scheduleArrowCommand commandRequestCount " + commandRequestCount + " commandResponseCount " + commandResponseCount + " waiting for " + nemuriConstantsModel.bedResponseTimeout);
        if (!isFirst && commandRequestCount != commandResponseCount) {
            commandTimeoutHandler.postDelayed(commandTimeoutTimer, (long) ((nemuriConstantsModel.bedResponseTimeout) * 1000));
        }
        freeCommandHandler.removeCallbacks(freeCommandTimer);
        freeCommandHandler.postDelayed(freeCommandTimer, (long) ((nemuriConstantsModel.commandInterval) * 1000));
    }

    private void sendArrowCommand() {
        if (fingerPressCount > 1 || isSendingMultiButton) {
            isSendingMultiButton = true;
            currentFreeOperation = NSOperation.FREE_MULTI_BUTTON;
        }

        if (currentFreeOperation == NSOperation.FREE_DECREASE_HEIGHT) {
            if (checkNoChangeBedPosition()) {
                // 一旦停止判定（ベッド情報が2秒間変化なし）
                if (checkHeightWarning_noChangeBedPosition(null, null)) {
                    showHeightThresholdAlert();
                    stopBedFreeMode();
                    return;
                }
            }
        }

        if (nsManager != null) {
            nsManager.sendArrowCommand(currentFreeOperation);
        }

    }

    private void stopBedFreeMode() {
        stopBedFreeMode(false);
    }

    private void stopBedFreeMode(boolean shouldTerminate) {
        bedCardPager.setSwipeLocked(false);
        if (isNemuriScanInitiated) {
            fingerPressCount -= 1;
            if (fingerPressCount <= 0) {
                fingerPressCount = 0;
            }
            isSendingMultiButton = isSendingMultiButton && fingerPressCount > 0;
            if (!isSendingMultiButton) {
                commandResponseCount = 0;
                commandRequestCount = 0;
                mismatchButtonCodeCounter = 0;
                isArrowPressedDown = false;

                tiltIconImage.clearAnimation();
                tiltIconImage.setAlpha(1.0f);

                freeCommandHandler.removeCallbacks(freeCommandTimer);
                if (!shouldTerminate) {
                    new Handler().postDelayed(() -> nsManager.sendArrowCommand(NSOperation.FREE_TERMINATE),
                            (long) ((nemuriConstantsModel.commandInterval) * 1000));
                }
                animateBedShadow(currentBedPosition);
            }
        }
    }
    //MARK END: BLE free mode functions

    //MARK : BLE preset mode functions
    @SuppressLint("SetTextI18n")
    private void schedulePresetCommand(boolean isFirst) {
        if (isFirst) {
            if (BuildConfig.BUILD_TYPE.equals("debug")) {
                debugTargetHead.setText("背角度 " + currentPresetTarget.getHead() + "°");
                debugTargetLeg.setText("膝角度 " + currentPresetTarget.getLeg() + "°");
                debugTargetHeight.setText("高さ " + currentPresetTarget.getHeight() + "cm");
                debugTargetTilt.setText("傾斜 " + currentPresetTarget.getTilt() + "°");
            }

            updateStartOperationHeightPosition();

            if (!isFirstOperated) {
                isFirstOperated = true;

                if (!reachTargetHeadAndLeg() || !reachTargetHeightAndTilt()) {
                    // 一旦停止判定（初回操作）
                    if (checkHeightWarning_firstOperation(currentPresetTarget.getHeight(), currentPresetTarget.getTilt())) {
                        showHeightThresholdAlert(); // 警告ポップアップ表示
                        startTimer_stopPresetOperation();   // POSITION操作終了
                        return;
                    }
                }
            }
        }

        if (checkNoChangeBedPosition()) {
            // 「背角度と膝角度が目標値に達している」場合
            if (reachTargetHeadAndLeg()) {
                // 「ベッドタイプが「INTIME_COMFORT」ではない」場合
                if (nemuriScanModel.getInfoType() != NSSpec.BED_MODEL.INTIME_COMFORT.ordinal()) {
                    startTimer_stopPresetOperation();   // POSITION操作終了
                    return;
                } else {
                    // 「高さと傾斜が目標値に達している」場合
                    if (reachTargetHeightAndTilt()) {
                        startTimer_stopPresetOperation();   // POSITION操作終了
                        return;
                    }
                }
            }

            // 一旦停止判定（ベッド情報が2秒間変化なし）
            if (checkHeightWarning_noChangeBedPosition(currentPresetTarget.getHeight(), currentPresetTarget.getTilt())) {
                showHeightThresholdAlert(); // 警告ポップアップ表示
                startTimer_stopPresetOperation();   // POSITION操作終了
                return;
            }
        }

        stopTimer_stopPresetOperation();

        if (isSendingMultiButton) {
            nsManager.sendArrowCommand(NSOperation.FREE_MULTI_BUTTON);
            isArrowPressedDown = true;
        } else {
            nsManager.sendPresetCommand(currentPresetTarget, currentBedPosition.getHeight());
        }

        commandRequestCount += 1;
        if (!isFirst && commandRequestCount != commandResponseCount) {
            commandTimeoutHandler.postDelayed(commandTimeoutTimer, (long) (nemuriConstantsModel.bedResponseTimeout * 1000));
        }

        presetCommandHandler.removeCallbacks(presetCommandTimer);
        presetCommandHandler.postDelayed(presetCommandTimer, (long) ((nemuriConstantsModel.commandInterval) * 1000));
    }

    private void startTimer_stopPresetOperation() {
        if (sameBedPosTimer == null) {
            sameBedPosTimer = () -> {
                stopPresetOperation();
                sameBedPosTimer = null;
            };
            sameBedPosHandler.postDelayed(sameBedPosTimer, nemuriConstantsModel.nsBedSameResultTimeout * 1000);
        }
    }

    private void stopTimer_stopPresetOperation() {
        if (sameBedPosTimer != null) {
            sameBedPosHandler.removeCallbacks(sameBedPosTimer);
            sameBedPosTimer = null;
        }
    }

    private void startPresetOperation() {
        DeviceTemplateBedModel preset = bedPresetValues.get(bedPresetFragment.selectedPresetIndex);
        currentPresetTarget = new NSBedPosition(preset);
        currentBedOperationStatus = NSOperation.BedOperationType.PRESET;
        startBedPresetUI();
        schedulePresetCommand(true);
    }

    private void stopPresetOperation() {
        runOnUiThread(() -> {
            if (currentBedOperationStatus != NSOperation.BedOperationType.PRESET) {
                return;
            }
            new Handler().postDelayed(() -> nsManager.sendArrowCommand(NSOperation.FREE_TERMINATE),
                    (long) ((nemuriConstantsModel.commandInterval) * 1000));
            currentBedOperationStatus = NSOperation.BedOperationType.NONE;
            commandRequestCount = commandResponseCount = 0;
            isPresetPressedDown = false;
            mismatchButtonCodeCounter = 0;
            stopBedPresetUI();
        });
    }

    /**
     * 背角度と膝角度が目標値に達しているか？
     * @return true=達している、false=達していない
     */
    private boolean reachTargetHeadAndLeg() {
        if (currentPresetTarget == null) return false;
        return (currentPresetTarget.getHead() > (currentBedPosition.getHead() - nemuriConstantsModel.lowerBedThreshold) && currentPresetTarget.getHead() < (currentBedPosition.getHead() + nemuriConstantsModel.upperBedThreshold)) &&
               (currentPresetTarget.getLeg() > (currentBedPosition.getLeg() - nemuriConstantsModel.lowerBedThreshold) && currentPresetTarget.getLeg() < (currentBedPosition.getLeg() + nemuriConstantsModel.upperBedThreshold));
    }

    /**
     * 高さと傾斜角度が目標値に達しているか？
     * @return true=達している、false=達していない
     */
    private boolean reachTargetHeightAndTilt() {
        if (currentPresetTarget == null) return false;
        return (currentPresetTarget.getHeight() > (currentBedPosition.getHeight() - nemuriConstantsModel.lowerBedThreshold) && currentPresetTarget.getHeight() < (currentBedPosition.getHeight() + nemuriConstantsModel.upperBedThreshold)) &&
               (currentPresetTarget.getTilt() > (currentBedPosition.getTilt() - nemuriConstantsModel.lowerBedThreshold) && currentPresetTarget.getTilt() < (currentBedPosition.getTilt() + nemuriConstantsModel.upperBedThreshold));
    }

    private void updateMattressTemplateIfChanged(int templateId, NSMattressPosition mattressPosition) {

        DeviceTemplateMattressModel deviceTemplateMattressModel = DeviceTemplateMattressModel.getById(templateId, false);
        boolean contentChanged = false;

        if (deviceTemplateMattressModel.getHead() != mattressPosition.getHead()) {
            deviceTemplateMattressModel.setHead(mattressPosition.getHead());
            contentChanged = true;
        }
        if (deviceTemplateMattressModel.getShoulder() != mattressPosition.getShoulder()) {
            deviceTemplateMattressModel.setShoulder(mattressPosition.getShoulder());
            contentChanged = true;
        }
        if (deviceTemplateMattressModel.getHip() != mattressPosition.getHip()) {
            deviceTemplateMattressModel.setHip(mattressPosition.getHip());
            contentChanged = true;
        }
        if (deviceTemplateMattressModel.getThigh() != mattressPosition.getThigh()) {
            deviceTemplateMattressModel.setThigh(mattressPosition.getThigh());
            contentChanged = true;
        }
        if (deviceTemplateMattressModel.getCalf() != mattressPosition.getCalf()) {
            deviceTemplateMattressModel.setCalf(mattressPosition.getCalf());
            contentChanged = true;
        }
        if (deviceTemplateMattressModel.getFeet() != mattressPosition.getFeet()) {
            deviceTemplateMattressModel.setFeet(mattressPosition.getFeet());
            contentChanged = true;
        }
        if (contentChanged) {
            LogProvider.logMattressTemplateChange(RemoteActivity.this, deviceTemplateMattressModel, null);
            mattressPresetValues.set(templateId - 1, deviceTemplateMattressModel);
        }
        if (templateId == mattressPresetFragment.selectedPresetIndex + 1) {
            currentMattressPosition = new NSMattressPosition(mattressPresetValues.get(mattressPresetFragment.selectedPresetIndex));
            setMattressNumericIndicator(currentMattressPosition);
        }
    }
    //MARK END: ble preset mode functions

    /**
     * 高さと傾斜角度から、足先高さを計算して返す
     * @return 足先高さ
     */
    @SuppressLint("SetTextI18n")
    private int calcLegHeight(int height, int tilt) {
        int legHeight = (int)(height - (legHeightCalcValue * Math.sin(Math.toRadians(tilt))));
        if (BuildConfig.BUILD_TYPE.equals("debug")) {
            runOnUiThread(() -> debugLegHeightIndicator.setText("足先高さ " + legHeight + "cm"));
        }
        return legHeight;
    }

    /**
     * 足先高さ用の高さ閾値を計算して返す
     * @return 足先高さ用の高さ閾値
     */
    private int calcLegHeightWarningThreshold() {
        return nemuriConstantsModel.heightWarningThreshold + legHeightAddValue;
    }

    /**
     * 高さ一旦停止ポップアップの表示最低高さを計算して返す
     * @return 表示最低高さ
     */
    private int calcMinimumPopupHeight() {
        return nemuriConstantsModel.heightWarningThreshold + minimumPopupHeightAddValue;
    }

    /**
     * 高さ閾値フラグの更新
     */
    private void updateStartOperationHeightPosition() {
        Integer bedType = nemuriScanModel == null ? null : nemuriScanModel.getInfoType();
        if (bedType == null) return;

        final int height = currentBedPosition.getTilt() < tiltThreshold ? currentBedPosition.getHeight() : calcLegHeight(currentBedPosition.getHeight(), currentBedPosition.getTilt());
        final int threshold = currentBedPosition.getTilt() < tiltThreshold ? nemuriConstantsModel.heightWarningThreshold : calcLegHeightWarningThreshold();
        
        // 「現在高さ（傾斜角度が2°以上の場合は、足先高さ）が閾値より大きい」場合は、Higher
        // 「現在高さ（傾斜角度が2°以上の場合は、足先高さ）が閾値より小さい」場合は、Lower
        // 「現在高さ（傾斜角度が2°以上の場合は、足先高さ）と閾値が等しい」場合は、Equal
        if (height > threshold) startOperation_heightPosition = StartOperation_HeightPosition.Higher;
        else if (height < threshold) startOperation_heightPosition = StartOperation_HeightPosition.Lower;
        else startOperation_heightPosition = StartOperation_HeightPosition.Equal;
    }

    /**
     * ベッド情報（頭、膝、高さ、傾斜）が2秒間変化なしかどうかのチェック
     * @return true=変化なし、false=変化あり
     */
    private boolean checkNoChangeBedPosition() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, -checkHeightWarningTime);
        // 「ベッド情報が2秒間変化していない」場合はtrue
        return !(headLastUpdateTime.after(calendar.getTime()) || legLastUpdateTime.after(calendar.getTime()) || heightLastUpdateTime.after(calendar.getTime()) || tiltLastUpdateTime.after(calendar.getTime()));
    }

    /**
     * 高さ一旦停止ポップアップの表示チェック（初回操作の場合）
     * @return true=表示する、false=表示しない
     */
    private boolean checkHeightWarning_firstOperation(Integer targetHeight, Integer targetTilt) {
        // 「すでにポップアップ表示済み」の場合はfalse
        if (userWarnedHeight) return false;

        // 「高さをサポートしていないベッド」の場合はfalse
        Integer bedType = nemuriScanModel == null ? null : nemuriScanModel.getInfoType();
        if (bedType != NSSpec.BED_MODEL.INTIME.ordinal() && bedType != NSSpec.BED_MODEL.INTIME_COMFORT.ordinal() && !currentBedSpec.isHeightLockSupported()) return false;

        // 「ベッドタイプがINTIME」かつ「目標値が存在する（POSITION操作）」場合はfalse
        if (bedType == NSSpec.BED_MODEL.INTIME.ordinal() && targetHeight != null && targetTilt != null) return false;

        if (targetHeight != null && targetTilt != null) {
            final int currentHeight = currentBedPosition.getTilt() < tiltThreshold ? currentBedPosition.getHeight() : calcLegHeight(currentBedPosition.getHeight(), currentBedPosition.getTilt());
            final int _targetHeight = currentBedPosition.getTilt() < tiltThreshold ? targetHeight : calcLegHeight(targetHeight, targetTilt);
            // 「高さ閾値より大きい高さからの操作開始ではない」かつ「現在高さが高さ目標値より高い」場合はtrue、それ以外はfalse
            return startOperation_heightPosition != StartOperation_HeightPosition.Higher && currentHeight > _targetHeight;
        } else {
            // 「高さ閾値より大きい高さからの操作開始ではない」場合はtrue、それ以外はfalse
            return startOperation_heightPosition != StartOperation_HeightPosition.Higher;
        }
    }

    /**
     * 高さ一旦停止ポップアップの表示チェック（ベッド情報が2秒間変化なしの場合）
     * @return true=表示する、false=表示しない
     */
    private boolean checkHeightWarning_noChangeBedPosition(Integer targetHeight, Integer targetTilt) {
        // 「すでにポップアップ表示済み」の場合はfalse
        if (userWarnedHeight) return false;

        // 「高さをサポートしていないベッド」の場合はfalse
        Integer bedType = nemuriScanModel == null ? null : nemuriScanModel.getInfoType();
        if (bedType != NSSpec.BED_MODEL.INTIME.ordinal() && bedType != NSSpec.BED_MODEL.INTIME_COMFORT.ordinal() && !currentBedSpec.isHeightLockSupported()) return false;

        // 「ベッドタイプがINTIME」かつ「目標値が存在する（POSITION操作）」場合はfalse
        if (bedType == NSSpec.BED_MODEL.INTIME.ordinal() && targetHeight != null && targetTilt != null) return false;

        // 「高さ閾値より大きい高さからの操作開始ではない」場合はfalse
        if (startOperation_heightPosition != StartOperation_HeightPosition.Higher) return false;

        final int height = currentBedPosition.getTilt() < tiltThreshold ? currentBedPosition.getHeight() : calcLegHeight(currentBedPosition.getHeight(), currentBedPosition.getTilt());
        final int threshold = currentBedPosition.getTilt() < tiltThreshold ? nemuriConstantsModel.heightWarningThreshold : calcLegHeightWarningThreshold();
        if (targetHeight != null && targetTilt != null) {
            // 「目標値が存在する（POSITION動作）」場合
            // 「現在高さ（傾斜角度2°以上の時は足先高さ）が表示最低高さ以上」かつ「現在高さ（傾斜角度2°以上の時は足先高さ）が閾値以下」かつ「現在高さが目標値に達していない」かつ「傾斜角度2°未満の時に傾斜角度が目標値に達していない」の場合はtrue
            return calcMinimumPopupHeight() <= height && height <= threshold && (currentBedPosition.getHeight() != targetHeight || (currentBedPosition.getTilt() != targetTilt && currentBedPosition.getTilt() < tiltThreshold));
        } else {
            // 「目標値が存在しない（FREE動作）」場合
            // 「現在高さ（傾斜角度2°以上の時は足先高さ）が表示最低高さ以上」かつ「現在高さ（傾斜角度2°以上の時は足先高さ）が閾値以下」の場合はtrue
            return calcMinimumPopupHeight() <= height && height <= threshold;
        }
    }

    //MARK : dummy ns exclusive functions
    private void startDummyBedPresetAnimation(NSBedPosition target) {
        bedCardPager.setSwipeLocked(true);
        dummyBedPresetAnimator = new Timer();
        dummyBedPresetAnimator.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    boolean isHeadReached = false;
                    boolean isLegReached = false;
                    NSBedPosition targetPosition = currentBedPosition.clone();
                    if (currentBedPosition.getHead() != target.getHead()) {
                        if (currentBedPosition.getHead() < target.getHead()) {
                            targetPosition.setHead(targetPosition.getHead() + dummyBedAnimIncrement.getHead());
                            //clip values
                            targetPosition.setHead(targetPosition.getHead() > target.getHead() ? target.getHead() : targetPosition.getHead());
                        } else {
                            targetPosition.setHead(targetPosition.getHead() - dummyBedAnimIncrement.getHead());
                        }
                        setHeadBed(targetPosition);
                    } else {
                        isHeadReached = true;
                    }
                    if (currentBedPosition.getLeg() != target.getLeg()) {
                        if (currentBedPosition.getLeg() < target.getLeg()) {
                            targetPosition.setLeg(targetPosition.getLeg() + dummyBedAnimIncrement.getLeg());
                            //clip values
                            targetPosition.setLeg(targetPosition.getLeg() > target.getLeg() ? target.getLeg() : targetPosition.getLeg());
                        } else {
                            targetPosition.setLeg(targetPosition.getLeg() - dummyBedAnimIncrement.getLeg());
                        }
                        setFootBed(targetPosition);
                    } else {
                        isLegReached = true;
                    }

                    if (isHeadReached && isLegReached) {
                        stopDummyBedPresetAnimation();
                    }

                });
            }
        }, 1000, 1000);
    }

    private void stopDummyBedPresetAnimation() {
        if (dummyBedPresetAnimator != null) {
            dummyBedPresetAnimator.cancel();
            dummyBedPresetAnimator.purge();
        }
        stopBedPresetUI();
    }

    @SuppressLint("SetTextI18n")
    private void startDummyBedFreeAnimation(NSOperation operation) {
        bedCardPager.setSwipeLocked(true);
        if (dummyBedFreeAnimator != null) {
            dummyBedFreeAnimator.purge();
            dummyBedFreeAnimator.cancel();
            dummyBedFreeAnimator = null;
        }
        dummyBedFreeAnimator = new Timer();
        dummyBedFreeAnimator.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                disableNavigationUI();
                NSBedPosition targetPosition = currentBedPosition.clone();
                switch (operation) {
                    case FREE_INCREASE_HEAD:
                        if (targetPosition.getHead() + dummyBedAnimIncrement.getHead() <= currentBedSpec.getHeadUpperRange())
                            targetPosition.setHead(targetPosition.getHead() + dummyBedAnimIncrement.getHead());
                        else
                            targetPosition.setHead(currentBedSpec.getHeadUpperRange());
                        setHeadBed(targetPosition);
                        setHeadShadBed(targetPosition);
                        break;
                    case FREE_DECREASE_HEAD:
                        if (targetPosition.getHead() - dummyBedAnimIncrement.getHead() >= currentBedSpec.getHeadLowerRange())
                            targetPosition.setHead(targetPosition.getHead() - dummyBedAnimIncrement.getHead());
                        else
                            targetPosition.setHead(currentBedSpec.getHeadLowerRange());
                        setHeadBed(targetPosition);
                        setHeadShadBed(targetPosition);
                        break;
                    case FREE_INCREASE_LEG:
                        if (targetPosition.getLeg() + dummyBedAnimIncrement.getLeg() <= currentBedSpec.getLegUpperRange())
                            targetPosition.setLeg(targetPosition.getLeg() + dummyBedAnimIncrement.getLeg());
                        else
                            targetPosition.setLeg(currentBedSpec.getLegUpperRange());
                        setFootBed(targetPosition);
                        setFootBedShad(targetPosition);
                        break;
                    case FREE_DECREASE_LEG:
                        if (targetPosition.getLeg() - dummyBedAnimIncrement.getLeg() >= currentBedSpec.getLegLowerRange())
                            targetPosition.setLeg(targetPosition.getLeg() - dummyBedAnimIncrement.getLeg());
                        else
                            targetPosition.setLeg(currentBedSpec.getLegLowerRange());
                        setFootBed(targetPosition);
                        setFootBedShad(targetPosition);
                        break;
                    case FREE_INCREASE_COMBI:
                        if (targetPosition.getHead() + dummyBedAnimIncrement.getHead() <= currentBedSpec.getHeadUpperRange())
                            targetPosition.setHead(targetPosition.getHead() + dummyBedAnimIncrement.getHead());
                        else
                            targetPosition.setHead(currentBedSpec.getHeadUpperRange());

                        if (targetPosition.getLeg() + dummyBedAnimIncrement.getLeg() <= currentBedSpec.getLegUpperRange())
                            targetPosition.setLeg(targetPosition.getLeg() + dummyBedAnimIncrement.getLeg());
                        else
                            targetPosition.setLeg(currentBedSpec.getLegUpperRange());
                        animateBed(targetPosition);
                        animateBedShadow(targetPosition);
                        break;
                    case FREE_DECREASE_COMBI:
                        if (targetPosition.getHead() - dummyBedAnimIncrement.getHead() >= currentBedSpec.getHeadLowerRange())
                            targetPosition.setHead(targetPosition.getHead() - dummyBedAnimIncrement.getHead());
                        else
                            targetPosition.setHead(currentBedSpec.getHeadLowerRange());

                        if (targetPosition.getLeg() - dummyBedAnimIncrement.getLeg() >= currentBedSpec.getLegLowerRange())
                            targetPosition.setLeg(targetPosition.getLeg() - dummyBedAnimIncrement.getLeg());
                        else
                            targetPosition.setLeg(currentBedSpec.getLegLowerRange());
                        animateBed(targetPosition);
                        animateBedShadow(targetPosition);
                        break;
                    case FREE_DECREASE_HEIGHT:
                        if (targetPosition.getHeight() - dummyBedAnimIncrement.getHeight() >= currentBedSpec.getHeightLowerRange())
                            targetPosition.setHeight(targetPosition.getHeight() - dummyBedAnimIncrement.getHeight());
                        else
                            targetPosition.setHeight(currentBedSpec.getHeightLowerRange());

                        if (decreaseHeightThreshold(targetPosition.getHeight())) {
                            showHeightThresholdAlert();
                        } else {
                            setHeightBed(targetPosition);
                        }
                        break;

                    case FREE_INCREASE_HEIGHT:
                        if (targetPosition.getHeight() + dummyBedAnimIncrement.getHeight() <= currentBedSpec.getHeightUpperRange())
                            targetPosition.setHeight(targetPosition.getHeight() + dummyBedAnimIncrement.getHeight());
                        else
                            targetPosition.setHeight(currentBedSpec.getHeightUpperRange());
                        runOnUiThread(() -> {
                            increaseHeightThreshold(targetPosition.getHeight());
                            setHeightBed(targetPosition);
                        });
                        break;
                }
            }
        }, 0, 1000);
    }

    public Boolean decreaseHeightThreshold(int heightPosition) {
        return !userWarnedHeight && heightPosition < nemuriConstantsModel.heightWarningThreshold;
    }

    public void increaseHeightThreshold(int heightPosition) {
        if (heightPosition > nemuriConstantsModel.heightWarningThreshold) {
            userWarnedHeight = false;
        }
    }

    private void stopDummyBedFreeAnimation() {
        bedCardPager.setSwipeLocked(false);
        if (dummyBedFreeAnimator != null) {
            dummyBedFreeAnimator.cancel();
            dummyBedFreeAnimator.purge();
            dummyBedFreeAnimator = null;
        }
    }

    private void startDummyMattressPresetAnimation(NSMattressPosition target) {
        dummyMattressPresetAnimator = new Timer();
        dummyMattressPresetAnimator.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    boolean isHeadReached = false;
                    boolean isShoulderReached = false;
                    boolean isHipReached = false;
                    boolean isThighReached = false;
                    boolean isCalfReached = false;
                    boolean isLegReached = false;
                    NSMattressPosition targetPosition = currentMattressPosition.clone();
                    if (currentMattressPosition.getHead() != target.getHead()) {
                        if (currentMattressPosition.getHead() < target.getHead()) {
                            targetPosition.setHead(targetPosition.getHead() + dummyMattressAnimIncrement.getHead());
                            //clip values
                            targetPosition.setHead(targetPosition.getHead() > target.getHead() ? target.getHead() : targetPosition.getHead());
                        } else {
                            targetPosition.setHead(targetPosition.getHead() - dummyMattressAnimIncrement.getHead());
                        }
                        setMattressSegmentBlinking(0, true);
                    } else {
                        isHeadReached = true;
                        setMattressSegmentBlinking(0, false);
                    }
                    if (currentMattressPosition.getShoulder() != target.getShoulder()) {
                        if (currentMattressPosition.getShoulder() < target.getShoulder()) {
                            targetPosition.setShoulder(targetPosition.getShoulder() + dummyMattressAnimIncrement.getShoulder());
                            //clip values
                            targetPosition.setShoulder(targetPosition.getShoulder() > target.getShoulder() ? target.getShoulder() : targetPosition.getShoulder());
                        } else {
                            targetPosition.setShoulder(targetPosition.getShoulder() - dummyMattressAnimIncrement.getShoulder());
                        }
                        setMattressSegmentBlinking(1, true);
                    } else {
                        isShoulderReached = true;
                        setMattressSegmentBlinking(1, false);
                    }
                    if (currentMattressPosition.getHip() != target.getHip()) {
                        if (currentMattressPosition.getHip() < target.getHip()) {
                            targetPosition.setHip(targetPosition.getHip() + dummyMattressAnimIncrement.getHip());
                            //clip values
                            targetPosition.setShoulder(targetPosition.getShoulder() > target.getShoulder() ? target.getShoulder() : targetPosition.getShoulder());
                        } else {
                            targetPosition.setHip(targetPosition.getHip() - dummyMattressAnimIncrement.getHip());
                        }
                        setMattressSegmentBlinking(2, true);
                    } else {
                        isHipReached = true;
                        setMattressSegmentBlinking(2, false);
                    }
                    if (currentMattressPosition.getThigh() != target.getThigh()) {
                        if (currentMattressPosition.getThigh() < target.getThigh()) {
                            targetPosition.setThigh(targetPosition.getThigh() + dummyMattressAnimIncrement.getThigh());
                            //clip values
                            targetPosition.setThigh(targetPosition.getThigh() > target.getThigh() ? target.getThigh() : targetPosition.getThigh());
                        } else {
                            targetPosition.setThigh(targetPosition.getThigh() - dummyMattressAnimIncrement.getThigh());
                        }
                        setMattressSegmentBlinking(3, true);
                    } else {
                        isThighReached = true;
                        setMattressSegmentBlinking(3, false);
                    }

                    if (currentMattressPosition.getCalf() != target.getCalf()) {
                        if (currentMattressPosition.getCalf() < target.getCalf()) {
                            targetPosition.setCalf(targetPosition.getCalf() + dummyMattressAnimIncrement.getCalf());
                            //clip values
                            targetPosition.setCalf(targetPosition.getCalf() > target.getCalf() ? target.getCalf() : targetPosition.getCalf());
                        } else {
                            targetPosition.setCalf(targetPosition.getCalf() - dummyMattressAnimIncrement.getCalf());
                        }
                        setMattressSegmentBlinking(4, true);
                    } else {
                        isCalfReached = true;
                        setMattressSegmentBlinking(4, false);
                    }
                    if (currentMattressPosition.getFeet() != target.getFeet()) {
                        if (currentMattressPosition.getFeet() < target.getFeet()) {
                            targetPosition.setFeet(targetPosition.getFeet() + dummyMattressAnimIncrement.getFeet());
                            //clip values
                            targetPosition.setFeet(targetPosition.getFeet() > target.getFeet() ? target.getFeet() : targetPosition.getFeet());
                        } else {
                            targetPosition.setFeet(targetPosition.getFeet() - dummyMattressAnimIncrement.getFeet());
                        }
                        setMattressSegmentBlinking(5, true);
                    } else {
                        isLegReached = true;
                        setMattressSegmentBlinking(5, false);
                    }

                    if (isHeadReached && isShoulderReached && isHipReached &&
                            isThighReached && isCalfReached && isLegReached) {
                        stopDummyMattressPresetAnimation();
                    } else {
                        currentMattressPosition = targetPosition;
                        setMattressNumericIndicator(currentMattressPosition);
                    }

                });
            }
        }, 1000, 1000);
    }

    private void stopDummyMattressPresetAnimation() {
        if (dummyMattressPresetAnimator != null) {
            dummyMattressPresetAnimator.cancel();
            dummyMattressPresetAnimator.purge();
        }
        stopMattressPresetUI();
    }

    private void startDummyMattressFreeAnimation(int segmentIndex, int newValue) {
        dummyMattressFreeAnimator = new Timer();
        dummyMattressFreeAnimator.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    int currentValue = currentMattressPosition.getValueByIndex(segmentIndex);
                    int incrementValue = dummyMattressAnimIncrement.getValueByIndex(segmentIndex);
                    if (currentValue != newValue) {
                        if (currentValue < newValue) {
                            currentValue = currentValue + incrementValue;
                        } else {
                            currentValue = currentValue - incrementValue;
                        }
                        setMattressSegmentBlinking(segmentIndex, true);
                        currentMattressPosition.setValueByIndex(segmentIndex, currentValue);
                        setMattressNumericIndicator(currentMattressPosition);
                    } else {
                        setMattressSegmentBlinking(segmentIndex, false);
                        stopDummyMattressFreeAnimation();
                    }

                });
            }
        }, 1000, 1000);
    }

    private void stopDummyMattressFreeAnimation() {
        if (dummyMattressFreeAnimator != null) {
            dummyMattressFreeAnimator.cancel();
            dummyMattressFreeAnimator.purge();
        }
        setHighlightAllMattressSegments(false);
        stopMattressFreeUI();
    }
    //MARK END : dummy ns exclusive functions

    //MARK : DeviceTemplateFetchListener implementation
    @Override
    public void onDeviceTemplateFetched(List<DeviceTemplateMattressModel> mattressModels, List<DeviceTemplateBedModel> bedModels,
                                        List<DeviceTemplateMattressModel> mattressModelDefaults, List<DeviceTemplateBedModel> bedModelDefaults,
                                        NemuriConstantsModel nemuriConstantsModel) {
        this.nemuriConstantsModel.copyValue(nemuriConstantsModel);
        bedPresetValues = new ArrayList<>();
        bedPresetValues.addAll(bedModels);
        //sort ascending by id
        bedPresetValues.sort(Comparator.comparingInt(DeviceTemplateBedModel::getId));

        mattressPresetValues = new ArrayList<>();
        mattressPresetValues.addAll(mattressModels);
        //sort ascending by id
        mattressPresetValues.sort(Comparator.comparingInt(DeviceTemplateMattressModel::getId));

        btnStartBed.setOnClickListener(v -> {
            if (isNemuriScanInitiated && bedPresetFragment.selectedPresetIndex >= 0) {
                startPresetOperation();
                sendRemoteLog(isStart);
                return;
            }
            if (isStart && bedPresetFragment.selectedPresetIndex >= 0) {
                HomeActivity.REMOTEACTIVE = true;
                bedPresetFragment.setIsBedOperationRunning(true);
                startBedPresetUI();
                sendRemoteLog(isStart);
                currentBedOperationStatus = NSOperation.BedOperationType.PRESET;
                startDummyBedPresetAnimation(new NSBedPosition(bedPresetValues.get(bedPresetFragment.selectedPresetIndex)));
            }
        });
        btnStartMatress.setOnClickListener(v -> {
            if (currentPagerType == RemotePagerType.PRESET) {
                if (mattressPresetFragment.selectedPresetIndex >= 0) {
                    if (mattressPresetFragment.selectedPresetIndex == 5) {
                        startMattressFukatto();
                        return;
                    }
                    mattressPresetFragment.setIsFukattoOperationRunning(true);
                    startMattressPresetUI();
                    if (isNemuriScanInitiated) {
                        mattressPendingOperationType = MattressPendingOperationType.POSITION;
                        nsManager.getMattressPosition();
                    } else {
                        startDummyMattressPresetAnimation(new NSMattressPosition(mattressPresetValues.get(mattressPresetFragment.selectedPresetIndex)));
                        mattressPresetFragment.clearSelection();
                    }
                    sendRemoteLog(isStart);
                }
            } else if (currentPagerType == RemotePagerType.FREE) {
                if (mattressManualFragment.selectedSegmentIndex >= 0) {
                    startMattressFreeUI();

                    //LOG HERE NS_REMOTE_MATTRESS_FREE_START
                    LogUserAction.sendNewLog(userService, "NS_REMOTE_MATTRESS_FREE_START", "1", "", "UI000610");
                    if (isNemuriScanInitiated) {
                        mattressPendingOperationType = MattressPendingOperationType.FREE;
                        nsManager.getMattressPosition();
                    } else {
                        startDummyMattressFreeAnimation(mattressManualFragment.selectedSegmentIndex, mattressManualFragment.newSegmentValue);
                        mattressManualFragment.clearSelection();
                    }

                }
            }else if (currentPagerType == RemotePagerType.RECOMMEND) {
                if (mattressRecomendFragment.selectedMHSModel != null) {
                    lastMHSUsed = mattressRecomendFragment.selectedMHSModel;
                    startMattressRecommendUI();
                    if (isNemuriScanInitiated) {
                        mattressPendingOperationType = MattressPendingOperationType.RECOMMEND;
                        nsManager.getMattressPosition();
                    } else {
                        startDummyMattressPresetAnimation(new NSMattressPosition(mattressPresetValues.get(mattressPresetFragment.selectedPresetIndex)));
                        mattressRecomendFragment.clearSelection();
                    }
                    sendRemoteLog(isStart);

                }
            }
        });
        initBLE();
    }
    //MARK END: DeviceTemplateFetchListener implementation

    //MARK : BedManualListener implementation
    @Override
    public void onIncreaseCombinationTouchStart() {
        Logger.w("ARROW TOUCHSTART + COMBI " + fingerPressCount);
        disableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_START
        bedCardPager.setSwipeLocked(true);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_START", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);

            currentFreeOperation = NSOperation.FREE_INCREASE_COMBI;
            fingerPressCount += 1;
            commandResponseCount = 0;
            commandRequestCount = 0;
            isArrowPressedDown = true;

            scheduleArrowCommand(true);

            return;
        }
        startDummyBedFreeAnimation(NSOperation.FREE_INCREASE_COMBI);

    }

    @Override
    public void onIncreaseCombinationTouchEnd() {
        Logger.e("ARROW TOUCHEND + COMBI " + fingerPressCount);
        enableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_STOP
        bedCardPager.setSwipeLocked(false);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_STOP", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            stopBedFreeMode();
            return;
        }
        stopDummyBedFreeAnimation();
    }

    @Override
    public void onDecreaseCombinationTouchStart() {
        Logger.w("ARROW TOUCHSTART - COMBI " + fingerPressCount);
        disableNavigationUI();
        bedCardPager.setSwipeLocked(true);
        //LOG HERE NS_REMOTE_FREE_START
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_START", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);

            currentFreeOperation = NSOperation.FREE_DECREASE_COMBI;
            fingerPressCount += 1;
            commandResponseCount = 0;
            commandRequestCount = 0;
            isArrowPressedDown = true;

            Logger.e("Finger " + fingerPressCount);
            scheduleArrowCommand(true);

            return;
        }
        startDummyBedFreeAnimation(NSOperation.FREE_DECREASE_COMBI);
    }

    @Override
    public void onDecreaseCombinationTouchEnd() {
        Logger.e("ARROW TOUCHEND - COMBI " + fingerPressCount);
        enableNavigationUI();
        bedCardPager.setSwipeLocked(false);
        //LOG HERE NS_REMOTE_FREE_STOP
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_STOP", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            stopBedFreeMode();
            return;
        }
        stopDummyBedFreeAnimation();
    }

    @Override
    public void onIncreaseHeadTouchStart() {
        Logger.w("ARROW TOUCHSTART + HEAD " + fingerPressCount);
        disableNavigationUI();
        bedCardPager.setSwipeLocked(true);
        //LOG HERE NS_REMOTE_FREE_START
        if (isNemuriScanInitiated) {
            commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);

            currentFreeOperation = NSOperation.FREE_INCREASE_HEAD;
            fingerPressCount += 1;
            commandResponseCount = 0;
            commandRequestCount = 0;
            isArrowPressedDown = true;

            scheduleArrowCommand(true);

            return;
        }
        startDummyBedFreeAnimation(NSOperation.FREE_INCREASE_HEAD);
    }

    @Override
    public void onIncreaseHeadTouchEnd() {
        Logger.e("ARROW TOUCHEND + HEAD " + fingerPressCount);
        enableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_STOP
        bedCardPager.setSwipeLocked(false);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_STOP", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            stopBedFreeMode();
            return;
        }
        stopDummyBedFreeAnimation();
    }

    @Override
    public void onDecreaseHeadTouchStart() {
        Logger.w("ARROW TOUCHSTART - HEAD " + fingerPressCount);
        disableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_START
        bedCardPager.setSwipeLocked(true);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_START", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);

            currentFreeOperation = NSOperation.FREE_DECREASE_HEAD;
            fingerPressCount += 1;
            commandResponseCount = 0;
            commandRequestCount = 0;
            isArrowPressedDown = true;

            scheduleArrowCommand(true);

            return;
        }
        startDummyBedFreeAnimation(NSOperation.FREE_DECREASE_HEAD);
    }

    @Override
    public void onDecreaseHeadTouchEnd() {
        Logger.e("ARROW TOUCHEND - HEAD " + fingerPressCount);
        enableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_STOP
        bedCardPager.setSwipeLocked(false);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_STOP", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            stopBedFreeMode();
            return;
        }
        stopDummyBedFreeAnimation();
    }

    @Override
    public void onIncreaseLegTouchStart() {
        Logger.w("ARROW TOUCHSTART + LEG " + fingerPressCount);
        disableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_START
        bedCardPager.setSwipeLocked(true);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_START", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);
            currentFreeOperation = NSOperation.FREE_INCREASE_LEG;
            fingerPressCount += 1;
            commandResponseCount = 0;
            commandRequestCount = 0;
            isArrowPressedDown = true;

            scheduleArrowCommand(true);

            return;
        }
        startDummyBedFreeAnimation(NSOperation.FREE_INCREASE_LEG);
    }

    @Override
    public void onIncreaseLegTouchEnd() {
        Logger.e("ARROW TOUCHEND + LEG " + fingerPressCount);
        enableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_STOP
        bedCardPager.setSwipeLocked(false);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_STOP", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            stopBedFreeMode();
            return;
        }
        stopDummyBedFreeAnimation();
    }

    @Override
    public void onDecreaseLegTouchStart() {
        Logger.w("ARROW TOUCHSTART - LEG " + fingerPressCount);
        disableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_START
        bedCardPager.setSwipeLocked(true);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_START", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);

            currentFreeOperation = NSOperation.FREE_DECREASE_LEG;
            fingerPressCount += 1;
            commandResponseCount = 0;
            commandRequestCount = 0;
            isArrowPressedDown = true;

            scheduleArrowCommand(true);
            return;
        }
        startDummyBedFreeAnimation(NSOperation.FREE_DECREASE_LEG);

    }

    @Override
    public void onDecreaseLegTouchEnd() {
        Logger.e("ARROW TOUCHEND - LEG " + fingerPressCount);
        enableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_STOP
        bedCardPager.setSwipeLocked(false);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_STOP", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            stopBedFreeMode();
            return;
        }
        stopDummyBedFreeAnimation();
    }

    @Override
    public void onIncreaseHeightTouchStart() {
        Logger.w("ARROW TOUCHSTART + HEIGHT " + fingerPressCount);
        disableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_START
        bedCardPager.setSwipeLocked(true);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_START", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);

            currentFreeOperation = NSOperation.FREE_INCREASE_HEIGHT;
            fingerPressCount += 1;
            commandResponseCount = 0;
            commandRequestCount = 0;
            isArrowPressedDown = true;

            scheduleArrowCommand(true);

            return;
        }
        startDummyBedFreeAnimation(NSOperation.FREE_INCREASE_HEIGHT);
    }

    @Override
    public void onIncreaseHeightTouchEnd() {
        Logger.e("ARROW TOUCHEND + HEIGHT " + fingerPressCount);
        enableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_STOP
        bedCardPager.setSwipeLocked(false);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_STOP", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            stopBedFreeMode();
        }
        stopDummyBedFreeAnimation();
    }

    @Override
    public void onDecreaseHeightTouchStart() {
        Logger.w("ARROW TOUCHSTART - HEIGHT " + fingerPressCount);
        disableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_START
        bedCardPager.setSwipeLocked(true);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_START", "1", "", "UI000610");

        if (isNemuriScanInitiated) {
            commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);

            currentFreeOperation = NSOperation.FREE_DECREASE_HEIGHT;
            fingerPressCount += 1;
            commandResponseCount = 0;
            commandRequestCount = 0;
            isArrowPressedDown = true;

            scheduleArrowCommand(true);

            return;
        }
        startDummyBedFreeAnimation(NSOperation.FREE_DECREASE_HEIGHT);

    }

    @Override
    public void onDecreaseHeightTouchEnd() {
        Logger.e("ARROW TOUCHEND - HEIGHT " + fingerPressCount);
        enableNavigationUI();
        //LOG HERE NS_REMOTE_FREE_STOP
        bedCardPager.setSwipeLocked(false);
        LogUserAction.sendNewLog(userService, "NS_REMOTE_FREE_STOP", "1", "", "UI000610");
        if (isNemuriScanInitiated) {
            stopBedFreeMode();
        }
        stopDummyBedFreeAnimation();
    }
    //MARK END : BedManualListener implementation

    //MARK : NSBaseDelegate
    @Override
    public void onCommandWritten(NSOperation command) {
        //TODO : HANDLE ILLEGAL BLE OP
        if (currentTabType == RemoteTabType.BED) {
            if (command.getCommandCode() == NSOperation.SET_MATTRESS_POSITION.getCommandCode()) {
                //DISCONNECT & SHOW ILLEGAL OPERATION ALERT
                purgeBLE();
                Logger.v("RemoteActivity : ILLEGAL COMMAND SET_MATTRESS_POSITION");
                runOnUiThread(() -> DialogUtil.createCustomYesNo(RemoteActivity.this,
                        "",
                        LanguageProvider.getLanguage("UI000802C191"),
                        LanguageProvider.getLanguage("UI000802C193"),
                        (dialogInterface, i) -> {
                            currentNSSpec.setMattressExist(false);
                            currentNSSpec.setBedExist(false);
                            disableBedUI();
                            disableMattressUI();
                        },
                        LanguageProvider.getLanguage("UI000802C192"),
                        (dialogInterface, i) -> {
                            //retry
                            tryToConnectBLE();
                        }
                ));
            }
        } else if (currentTabType == RemoteTabType.MATTRESS) {
            if (command.getCommandCode() == NSOperation.SET_BED_SETTING.getCommandCode() ||
                    command.getCommandCode() == NSOperation.FREE_DECREASE_COMBI.getCommandCode()) {
                //DISCONNECT & SHOW ILLEGAL OPERATION ALERT
                purgeBLE();
                DialogUtil.createCustomYesNo(RemoteActivity.this,
                        "",
                        LanguageProvider.getLanguage("UI000802C191"),
                        LanguageProvider.getLanguage("UI000802C193"),
                        (dialogInterface, i) -> {
                            currentNSSpec.setMattressExist(false);
                            currentNSSpec.setBedExist(false);
                            disableBedUI();
                            disableMattressUI();
                        },
                        LanguageProvider.getLanguage("UI000802C192"),
                        (dialogInterface, i) -> {
                            //retry
                            tryToConnectBLE();
                        }
                );
            }
        }
    }
    //MARK END : NSBaseDelegate


    //MARK : NSConnectionDelegate Implementation
    @Override
    public void onConnectionEstablished() {
        reconnectNSRetryCount = 0;
        reconnectNSWaitHandler.removeCallbacks(reconnectNSWaitTimer);

        runOnUiThread(() -> {
            isIntentionalDC = false;
            if (nsManager != null) {
                nsManager.getSerialNumber();
            }
        });
    }

    @Override
    public void onDisconnect() {
        Logger.w("disconnect triggered isIntentionalDC " + isIntentionalDC);
        if (isIntentionalDC) {
            isIntentionalDC = false;
            return;
        }
        stopPresetOperation();
        stopBedFreeMode(true);

        disableBedUI();
        disableMattressUI();
        fingerPressCount = 0;
        reconnectNSRetryCount = 0;
        if (PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) postDataReconnect();
    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {
        runOnUiThread(() -> {
            if (nemuriScanModel != null) {
                nsManager.getNSSpec();
            } else {
                Logger.e("onSerialNumberReceived ns model null");
            }

        });
    }

    @Override
    public void onAuthenticationFinished(int result) {
        Logger.d("NSManager onAuthenticationFinished " + result);
        if (result == NSConstants.NS_AUTH_SUCCESS || result == NSConstants.NS_AUTH_REG_SUCCESS) {
            //LOG HERE NS_SET_SERVERID_SUCCESS
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_SUCCESS", "1", "", "UI000610");
            nsManager.getBedSpec();
        } else {
            //LOG HERE NS_SET_SERVERID_FAILED
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_FAILED", "1", "", "UI000610");
            //TODO : not back pressed but disabling
            runOnUiThread(() -> DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000610C034"),
                    LanguageProvider.getLanguage("UI000610C035"), (dialogInterface, i) -> onBackPressed()));
        }

    }

    @Override
    public void onNSStatusReceived(int systemStatus, int bleStatus, int wifiStatus) {

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
        isLocationPermissionRejected = true;
        PermissionUtil.showLocationPermissionDialogAlert(RemoteActivity.this, new PermissionUtil.PermissionDialogueListener() {
            @Override
            public void onPermissionCanceled(DialogInterface dialogInterface) {
                hideProgress();
                purgeBLE();
                disableBedUI();
                disableMattressUI();
                dialogInterface.dismiss();
            }

            @Override
            public void onPermissionGranted() {
                isLocationPermissionRejected = false;
                tryToConnectBLE();
            }
        });
    }

    @Override
    public void onLocationServiceDisabled() {
        runOnUiThread(() -> showLocationServiceDialogAlert());
    }

    public void showLocationPermissionDialogAlert() {
        hideProgress();
        purgeBLE();
        disableBedUI();
        disableMattressUI();
        if (NemuriScanModel.get() != null) {
            tryToConnectBLE();
        }
    }

    public void showLocationServiceDialogAlert() {
        hideProgress();
        purgeBLE();
        disableBedUI();
        disableMattressUI();
        if (NemuriScanModel.get() != null) {
            PermissionUtil.showLocationServiceDialogAlert(RemoteActivity.this, new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                    hideProgress();
                    purgeBLE();
                    disableBedUI();
                    disableMattressUI();
                }

                @Override
                public void onEnabled() {
                    tryToConnectBLE();
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Logx("RemoteActivityLifeCycle", "onRestart");
    }

    @Override
    public void onCancelScan() {

    }

    @Override
    public void onStopScan() {

    }

    @Override
    public void onScanResult(ScanResult scanResult) {
        runOnUiThread(() -> {
            if (nemuriScanModel != null) {
                String savedMac = nemuriScanModel.getMacAddress();
                String targetMac = scanResult.getDevice().getAddress();
                Logger.v("RemoteActivity : Scanning BLE, looking for " + savedMac + " trying " + targetMac + " " + scanResult.getDevice().getName());
                if (savedMac.equalsIgnoreCase(targetMac)) {
                    Logger.v("RemoteActivity : Scanning BLE, match found");
                    runOnUiThread(() -> {
                        nsManager.connectToDevice(scanResult.getDevice(), RemoteActivity.this);
                        nsManager.stopScan();
                    });
                }
            }

        });
    }

    @Override
    public void onNSSpecReceived(NSSpec spec) {
//        spec.setMattressExist(true);
        Logger.v("RemoteActivity onNSSpecReceived isNSExist : " + spec.isNSExist() + " isBedExist " + spec.isBedExist() + " isMattressExist " + spec.isMattressExist());
        LogUserAction.sendNewLog(userService, "REMOTE_FW_MODE", String.valueOf(spec.isFWMode()), "", "UI000610");

        runOnUiThread(() -> {
            if (nemuriScanModel != null) {
                nemuriScanModel.updateSpec(spec);
            }
        });
        currentNSSpec = spec;
        shouldNotifyBedMissing = !currentNSSpec.isBedExist();
        shouldNotifyMattressMissing = !currentNSSpec.isMattressExist();
        shouldNotifyBothMissing = shouldNotifyBedMissing && shouldNotifyMattressMissing;

        NemuriScanModel nsModel = NemuriScanModel.getUnmanagedModel();
        if(nsModel != null ){
            nsManager.requestAuthentication(nsModel.getServerGeneratedId());

        }else{
            runOnUiThread(() -> {
                nsManager.requestAuthentication(nemuriScanModel.getServerGeneratedId());
            });
        }

        runOnUiThread(() -> nemuriScanModel.updateInfoType(spec.getBedType()));
    }
    //MARK END : NSScanDelegate Implementation

    //MARK : NSBedDelegate Implementation
    @Override
    public void onBedSpecReceived(NSBedSpec bedSpec) {
        Logger.d("傾斜上限値 %d", bedSpec.getTiltUpperRange());
        Logger.d("傾斜下限値 %d", bedSpec.getTiltLowerRange());

        //LOG HERE NS_REMOTE_CONNECTION_SUCCESS
        LogUserAction.sendNewLog(userService, "NS_REMOTE_CONNECTION_SUCCESS", "1", "", "UI000610");
        currentBedSpec = bedSpec;
        runOnUiThread(() -> nemuriScanModel.updateHeightSuppored(currentBedSpec.isHeightLockSupported()));

        //invalidate connection timeout
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        lastStatusTvFootVisibility = true;
        applyNSSpec();

        //poll for position
        getPositionHandler.removeCallbacks(getPositionTimer);
        nsManager.getBedPosition();
        getPositionHandler.postDelayed(getPositionTimer, nemuriConstantsModel.statusPollingInterval * 1000);
        if(!mattressOnlyChecked) {
            runOnUiThread(() -> {
                if (nemuriScanModel.onlyMattress()) {
                    btnMattress.setChecked(true);
                    toggleMatress();
                } else {
                    btnBed.setChecked(true);
                    toggleBed();
                }
                if(shouldNotifyBothMissing){
                    shouldNotifyBothMissing = false;
                    disableBedUI();
                    runOnUiThread(() ->{
                        String msgTag = "UI000610C044";
                        String btnTag = "UI000610C045";
                        DialogUtil.createSimpleOkDialog(RemoteActivity.this, "", LanguageProvider.getLanguage(msgTag), LanguageProvider.getLanguage(btnTag), (dialogInterface, i) -> disableBedUI());
                    });
                }
            });
            mattressOnlyChecked = true;
        }
    }

    @Override
    public void onBedFreePositionReceived(NSBedPosition bedPosition, int failCode, int buttonCode, boolean isButtonCodeMatched) {
        if (!isButtonCodeMatched) {
            mismatchButtonCodeCounter += 1;
            Logger.e("mismatchButtonCodeCounter " + mismatchButtonCodeCounter);
        } else {
            mismatchButtonCodeCounter = 0;
        }
        if (mismatchButtonCodeCounter >= 3) {
            runOnUiThread(this::stopBedFreeMode);
            runOnUiThread(this::showMismatchButtonCodeAlert);
            return;
        }
        animateBed(bedPosition);
        animateBedShadow(bedPosition);

        if (failCode != 0) {
            runOnUiThread(() -> {
                showFailCodeAlert(failCode);
                stopBedFreeMode();
            });
            return;
        }
        if (isArrowPressedDown) {
            commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);
            commandResponseCount = commandRequestCount;
        }
    }

    @Override
    public void onBedPresetPositionReceived(NSBedPosition bedPosition, int failCode, int buttonCode, boolean isButtonCodeMatched) {
        if (!isButtonCodeMatched) {
            mismatchButtonCodeCounter += 1;
            Logger.e("mismatchButtonCodeCounter " + mismatchButtonCodeCounter);
        } else {
            mismatchButtonCodeCounter = 0;
        }
        if (mismatchButtonCodeCounter >= 3) {
            runOnUiThread(this::stopPresetOperation);
            runOnUiThread(this::showMismatchButtonCodeAlert);
            return;
        }
        animateBed(bedPosition);
        if (failCode != 0) {
            runOnUiThread(() -> {
                showFailCodeAlert(failCode);
                stopPresetOperation();
            });
            return;
        }
        commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);
        commandResponseCount = commandRequestCount;
    }

    @Override
    public void onBedPositionReceived(NSBedPosition bedPosition, int failCode) {
        if (!isNemuriScanInitiated) {
            isNemuriScanInitiated = true;
            runOnUiThread(this::hideProgress);
            if (progressDialogs.isShowing()) {
                runOnUiThread(this::hideProgress);
            }
        }

        animateBed(bedPosition);
        if (failCode != 0) {
            runOnUiThread(() -> {
                showFailCodeAlert(failCode);
                stopPresetOperation();
            });
            return;
        }
        if (!bedPresetFragment.isPresetSelected()) {
            animateBedShadow(bedPosition);
        }
    }

    @Override
    public void onBedSettingReceived(NSBedSetting bedSetting) {
        previousSetting = currentSetting;
        currentSetting = bedSetting;
        applyBedLock();
    }

    @Override
    public void onBedSettingResult() {
        setBedSettingRetryHandler.removeCallbacks(setBedSettingRetryTimer);
        bedSettingRetryCount = 0;
    }

    //MARK END : NSBedDelegate Implementation

    //MARK : NSMattressDelegate Implementation
    boolean fukattoSim = false;

    @Override
    public void onMattressPositionReceived(NSMattressPosition mattressPosition, NSMattressStatus mattressStatus, int failCode) {
        currentMattressStatus = mattressStatus;
        Logger.d("Mattress incoming " + mattressPosition.toString() + " - " + mattressStatus.toString());
        runOnUiThread(() -> {
            //TODO:MATRESS FAIL CODE
            if (failCode != 0) {
                purgeBLE();
                if (currentMattressStatus.isFailCodeH()) {
                    runOnUiThread(() -> showFailCodeAlertMattressH(failCode));
                    return;
                }
                runOnUiThread(() -> showFailCodeAlertMattressNonH(failCode));
                return;
            }


            mattressPresetFragment.applyFukattoCounter(mattressPosition.isFukattoOn());


            Logger.w("setMattressSegmentBlinking A " + mattressPresetFragment.selectedPresetIndex + "/" + mattressManualFragment.selectedSegmentIndex);
            if (mattressPresetFragment.selectedPresetIndex == -1 && mattressManualFragment.selectedSegmentIndex == -1 && mattressRecomendFragment.selectedMHSModel == null) {
                Logger.w("setMattressSegmentBlinking B " + mattressStatus.toString());
                setMattressSegmentBlinking(0, mattressStatus.isHeadBusy());
                setMattressSegmentBlinking(1, mattressStatus.isShoulderBusy());
                setMattressSegmentBlinking(2, mattressStatus.isHipBusy());
                setMattressSegmentBlinking(3, mattressStatus.isThighBusy());
                setMattressSegmentBlinking(4, mattressStatus.isCalfBusy());
                setMattressSegmentBlinking(5, mattressStatus.isFeetBusy());
            }

            //mirror values from mattress remote
            if (mattressPosition.getOperationMode() == 11) {
                updateMattressTemplateIfChanged(4, mattressPosition);

            } else if (mattressPosition.getOperationMode() == 12) {
                updateMattressTemplateIfChanged(5, mattressPosition);

            }
            if (!mattressPresetSelectAdjusted && currentTabType == RemoteTabType.MATTRESS && currentPagerType == RemotePagerType.PRESET && !mattressPresetFragment.isFukattoActive()) {
                switch (mattressPosition.getOperationMode()) {
                    case 1:
                        mattressPresetFragment.setSelectedItem(2);
                        break;
                    case 3:
                        mattressPresetFragment.setSelectedItem(0);
                        break;
                    case 5:
                        mattressPresetFragment.setSelectedItem(1);
                        break;
                    case 11:
                        mattressPresetFragment.setSelectedItem(3);
                        break;
                    case 12:
                        mattressPresetFragment.setSelectedItem(4);
                        break;
                }
                mattressPresetSelectAdjusted = true;
            }
            currentMattressPosition = mattressPosition;
            switch (mattressPendingOperationType) {
                case NONE:
                    //LOG HERE NS_REMOTE_MATTRESS_UPDATE, if values are diffrent than before
                    LogUserAction.sendNewLog(userService, "NS_REMOTE_MATTRESS_UPDATE", "1", "", "UI000610");
                    if (mattressPresetFragment.selectedPresetIndex == -1) {
                        setMattressNumericIndicator(currentMattressPosition);
                    }
                    break;
                case POSITION:
                    if (mattressPresetFragment.selectedPresetIndex == 5) {
                        return;
                    }
                    NSMattressPosition target = new NSMattressPosition(mattressPresetValues.get(mattressPresetFragment.selectedPresetIndex));
                    mattressPosition.copyPositionValueOf(target);

                    mattressPosition.setDehumidifierOperation(0);
                    if (mattressPresetFragment.selectedPresetIndex == 0) {
                        mattressPosition.setOperationMode(3);
                    } else if (mattressPresetFragment.selectedPresetIndex == 1) {
                        mattressPosition.setOperationMode(5);
                    } else if (mattressPresetFragment.selectedPresetIndex == 2) {
                        mattressPosition.setOperationMode(1);
                    } else if (mattressPresetFragment.selectedPresetIndex == 3) {
                        mattressPosition.setOperationMode(11);
                    } else if (mattressPresetFragment.selectedPresetIndex == 4) {
                        mattressPosition.setOperationMode(12);
                    }

                    setMattressRetryTimer = () -> {
                        mattressRetryCount += 1;
                        nsManager.setMattressPosition(mattressPosition);
                        if (mattressRetryCount <= nemuriConstantsModel.mattressOperationMaxRetry) {
                            setMattressRetryHandler.postDelayed(setMattressRetryTimer, (long) (MATTRESS_OPERATION_TIMEOUT * 1000));
                        } else {
                            setMattressRetryHandler.removeCallbacks(setMattressRetryTimer);
                            mattressRetryCount = 0;
                        }
                    };
                    setMattressRetryHandler.postDelayed(setMattressRetryTimer, (long) (MATTRESS_OPERATION_TIMEOUT * 1000));

                    nsManager.setMattressPosition(mattressPosition);
                    break;
                case FREE:
                    mattressPosition.setValueByIndex(mattressManualFragment.selectedSegmentIndex, mattressManualFragment.newSegmentValue);
                    mattressPosition.setDehumidifierOperation(0);
                    mattressPosition.setOperationMode(15);
                    setMattressRetryTimer = () -> {
                        mattressRetryCount += 1;
                        nsManager.setMattressPosition(mattressPosition);
                        if (mattressRetryCount <= nemuriConstantsModel.mattressOperationMaxRetry) {
                            setMattressRetryHandler.postDelayed(setMattressRetryTimer, (long) (MATTRESS_OPERATION_TIMEOUT * 1000));
                        } else {
                            setMattressRetryHandler.removeCallbacks(setMattressRetryTimer);
                            mattressRetryCount = 0;
                        }
                    };
                    setMattressRetryHandler.postDelayed(setMattressRetryTimer, (long) (MATTRESS_OPERATION_TIMEOUT * 1000));

                    nsManager.setMattressPosition(mattressPosition);
                    break;
                case DEHUMIDIFIER:
                    mattressPosition.setDehumidifierOperation(mattressPosition.getDehumidifierOperation() == 0 ? 1 : 0); //flip integer
                    setMattressRetryTimer = () -> {
                        mattressRetryCount += 1;
                        nsManager.setMattressPosition(mattressPosition);
                        if (mattressRetryCount <= nemuriConstantsModel.mattressOperationMaxRetry) {
                            setMattressRetryHandler.postDelayed(setMattressRetryTimer, (long) (MATTRESS_OPERATION_TIMEOUT * 1000));
                        } else {
                            setMattressRetryHandler.removeCallbacks(setMattressRetryTimer);
                            mattressRetryCount = 0;
                        }
                    };
                    setMattressRetryHandler.postDelayed(setMattressRetryTimer, (long) (MATTRESS_OPERATION_TIMEOUT * 1000));

                    nsManager.setMattressPosition(mattressPosition);
                    mattressPendingOperationType = MattressPendingOperationType.NONE;
                    break;
                case FUKATTO_ON:
                    mattressPosition.setFukatto(true);
                    nsManager.setMattressPosition(mattressPosition);
                    mattressPendingOperationType = MattressPendingOperationType.NONE;
                    mattressPresetFragment.requestFukattoChanged(mattressPosition.isFukattoOn());
                    fukattoSim = true;
                    break;
                case FUKATTO_OFF:
                    mattressPosition.setFukatto(false);
                    mattressPresetFragment.requestFukattoChanged(mattressPosition.isFukattoOn());
                    nsManager.setMattressPosition(mattressPosition);
                    mattressPendingOperationType = MattressPendingOperationType.NONE;
                    fukattoSim = false;
                    break;
                case RECOMMEND:
                    if (mattressRecomendFragment.selectedMHSModel == null) {
                        return;
                    }
                    mattressPosition.copyPositionValueOf(new NSMattressPosition(mattressRecomendFragment.selectedMHSModel));

                    mattressPosition.setDehumidifierOperation(0);
                    mattressPosition.setOperationMode(15);

                    setMattressRetryTimer = () -> {
                        mattressRetryCount += 1;
                        nsManager.setMattressPosition(mattressPosition);
                        if (mattressRetryCount <= nemuriConstantsModel.mattressOperationMaxRetry) {
                            setMattressRetryHandler.postDelayed(setMattressRetryTimer, (long) (MATTRESS_OPERATION_TIMEOUT * 1000));
                        } else {
                            setMattressRetryHandler.removeCallbacks(setMattressRetryTimer);
                            mattressRetryCount = 0;
                        }
                    };
                    setMattressRetryHandler.postDelayed(setMattressRetryTimer, (long) (MATTRESS_OPERATION_TIMEOUT * 1000));

                    nsManager.setMattressPosition(mattressPosition);
                    break;
            }
            Logger.w("MATTRESS DEHUMIDIFER UPDATE UI MAT POS " + mattressPosition.toString());
            if (dehumidifierTemp.getDehumidifierOperation() == mattressPosition.getDehumidifierOperation()) {
                dehumidifierTempCounter++;
                if (dehumidifierTempCounter >= dehumidifierDelayCount) {
                    dehumidifierTempCounter = 0;
                    mattressSettingController.updateUI(mattressPosition);
                }
            }
            dehumidifierTemp = mattressPosition;
        });
    }

    @Override
    public void onMattressResultReceived(boolean isSuccess) {
        if (mattressPresetFragment.isStopUIShowing()) return;
        if (currentPagerType == RemotePagerType.PRESET) {
            runOnUiThread(() -> {
                mattressPresetFragment.clearSelection();
                stopMattressPresetUI();
            });
        } else if (currentPagerType == RemotePagerType.FREE) {
            runOnUiThread(() -> {
                mattressManualFragment.clearSelection();
                stopMattressFreeUI();
            });
        }else if (currentPagerType == RemotePagerType.RECOMMEND) {
            runOnUiThread(() -> {
                mattressRecomendFragment.clearSelection();
                stopMattressRecommendUI();
            });
            if(isSuccess && lastMHSUsed != null){
                final MHSModel tempLastUsed = lastMHSUsed;
                lastMHSUsed = null;
                showProgress();
                MattressSettingProvider.applyMattressSetting(this, tempLastUsed, new MattressSettingProvider.MattressApplyMHSListener() {
                    @Override
                    public void onMHSApplied(boolean isSuccess, MattressSettingModel result, String errTag) {
                        hideProgress();
                        SettingModel settingModel = SettingModel.getSetting().getUnmanaged();
                        tempLastUsed.setDesiredHardness(settingModel.user_desired_hardness);
                        if(isSuccess && result != null && result.getHistoryMHS() != null){
                            mattressSettingModel.setHistoryMHS(result.getHistoryMHS());
                        }else{
                            tempLastUsed.setScore(-1);
                            tempLastUsed.setDate(new Date());

                            PendingMHSModel pendingMHS = new PendingMHSModel();
                            pendingMHS.setEpoch(new DateTime().getMillis());
                            pendingMHS.setScore(-1);
                            pendingMHS.setDate(tempLastUsed.getDate());
                            pendingMHS.setMattressHardness(tempLastUsed.getMattressHardness());
                            pendingMHS.setDesiredHardness(settingModel.user_desired_hardness);
                            pendingMHS.setSent(false);
                            pendingMHS.insert();
                        }
                        MattressSettingProvider.setSetting(mattressSettingModel);
                        mattressSettingModel = MattressSettingProvider.getSetting().getUnmanaged();
                        mattressRecomendFragment.setMattressSettingModel(mattressSettingModel);
                    }
                },0);
                LogUserAction.sendNewLog(userService, "CHALENGE_MATTRESS_SETTING_SUCCESS", "", "", "");

            }
        }
        mattressPendingOperationType = MattressPendingOperationType.NONE;
        mattressRetryCount = 0;
        setMattressRetryHandler.removeCallbacks(setMattressRetryTimer);
    }
    //MARK END : NSMattressDelegate Implementation

    public static void sendPendingApplyRemoteSetting(Activity activity) {
        Logx("Mattress Setting Init", "Send Pending Log->" + PendingMHSModel.getUnsentMattressSetting().size());

        PendingMHSModel.deleteSentMattressSetting();

        if (!activity.isFinishing()) {
            if (!NetworkUtil.isNetworkConnected(activity.getApplicationContext())) {
                return;
            }

            ArrayList<PendingMHSModel> listPendingMHS = PendingMHSModel.getUnsentMattressSetting();
            Log.d("Mattress Setting before send pending", "saved MHS Model: " + listPendingMHS.size());
            for (int i=0; i<listPendingMHS.size(); i++) {
                PendingMHSModel mhsModel = listPendingMHS.get(i);
                UserService userService = ApiClient.getClient(activity.getApplicationContext()).create(UserService.class);
                UserLogin userData = UserLogin.getUserLogin();
                SettingModel setting = SettingModel.getSetting();

                if (userData != null && mhsModel != null){
                    RealmList<Integer> hardness = mhsModel.getMattressHardness();
                    Integer head = hardness.get(0);
                    Integer shoulder = hardness.get(1);
                    Integer hip = hardness.get(2);
                    Integer thigh = hardness.get(3);
                    Integer calf = hardness.get(4);
                    Integer feet = hardness.get(5);

                    if (head != null && shoulder != null && hip != null && thigh != null && calf != null && feet != null) {
                        int finalI = i;
                        userService.applyMattressSettingOffline(userData.getId(),mhsModel.getDate(),head,shoulder,hip,thigh,calf,feet,setting.getUser_desired_hardness()).enqueue(new Callback<BaseResponse<MattressSettingModel>>() {
                            @Override
                            public void onResponse(Call<BaseResponse<MattressSettingModel>> call, Response<BaseResponse<MattressSettingModel>> response) {
                                Log.d("Mattress Setting Init", "onResponse: " + response.body().isSucces());
                                Log.d("Mattress Setting Init", "onResponse: " + response.code());

                                if (response.body().isSucces()) {
                                    PendingMHSModel.updateSentSetting(listPendingMHS.get(finalI));
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseResponse<MattressSettingModel>> call, Throwable t) {

                            }
                        });
                    }
                }
            }


        }
    }

    //MARK : Dummy vars
    private Timer dummyBedFreeAnimator;
    private Timer dummyBedPresetAnimator;
    private Timer dummyMattressFreeAnimator;
    private Timer dummyMattressPresetAnimator;
    private NSBedPosition dummyBedAnimIncrement = new NSBedPosition(5, 5, 3, 5);
    private NSMattressPosition dummyMattressAnimIncrement = new NSMattressPosition();
    //MARK END: Dummy vars


    //MARK : BedPresetEventListener implementation
    @Override
    public void onPresetStopTapped() {
        sendRemoteLog(false);
        if (!isNemuriScanInitiated) {
            stopDummyBedPresetAnimation();
        } else {
            stopPresetOperation();
        }
    }

    @Override
    public void onPresetSelected(int index) {
        //this operation only available for active sleep beds
        if (currentNSSpec.getBedType() != NSSpec.BED_MODEL.ACTIVE_SLEEP) return;
        if (index >= 0) {
            //preset selected
            NSBedPosition target = new NSBedPosition(bedPresetValues.get(index));
            animateBedShadow(target);
            setBedStartBlinking(true);
            isStart = true;
        } else {
            //preset unselected
            animateBedShadow(currentBedPosition);
            setBedStartBlinking(false);
            isStart = false;
        }
    }

    @Override
    public void onPresetTouchStart(int index) {
        //this operation only available for intime beds
        Logger.d("touch onPresetTouchStart");
        if (currentNSSpec.getBedType() != NSSpec.BED_MODEL.INTIME && currentNSSpec.getBedType() != NSSpec.BED_MODEL.INTIME_COMFORT) return;
        if (isNemuriScanInitiated) {
            fingerPressCount += 1;
            isSendingMultiButton = fingerPressCount > 1;

            if (fingerPressCount == 1) {
                DeviceTemplateBedModel preset = bedPresetValues.get(index);
                currentPresetTarget = new NSBedPosition(preset);
                Logger.d("presetTarget head:" + currentPresetTarget.getHead() + " leg:" + currentPresetTarget.getLeg() + " height:" + currentPresetTarget.getHeight() + " tilt:" + currentPresetTarget.getTilt());
                currentBedOperationStatus = NSOperation.BedOperationType.PRESET;
                animateBedShadow(currentPresetTarget);

                commandTimeoutHandler.removeCallbacks(commandTimeoutTimer);
                commandResponseCount = 0;
                commandRequestCount = 0;
                isPresetPressedDown = true;

                startBedPresetHoldUI();
                schedulePresetCommand(true);
            }
        }
    }

    @Override
    public void onPresetTouchEnd(int index) {
        Logger.d("touch onPresetTouchEnd");
        //this operation only available for in time beds
        if (currentNSSpec.getBedType() != NSSpec.BED_MODEL.INTIME && currentNSSpec.getBedType() != NSSpec.BED_MODEL.INTIME_COMFORT) return;

        fingerPressCount -= 1;
        if (fingerPressCount <= 0) {
            fingerPressCount = 0;
        }
        isSendingMultiButton = isSendingMultiButton && fingerPressCount > 0;
        if (fingerPressCount == 0) {
            animateBedShadow(currentBedPosition);
        }
        if (isNemuriScanInitiated) {
            if (!isSendingMultiButton) {
                commandResponseCount = 0;
                commandRequestCount = 0;
                isPresetPressedDown = false;
                isArrowPressedDown = false;

                tiltIconImage.clearAnimation();
                tiltIconImage.setAlpha(1.0f);

                stopPresetOperation();
            }
            return;
        }
        stopDummyBedFreeAnimation();

    }

    //MARK END : BedPresetEventListener implementation

    //MARK : MattressPresetEventListener implementation

    @Override
    public void onMattresFukattoStopTapped() {
        //TODO:MATTRESS FUKATTO
        sendRemoteLog(false);
        mattressPendingOperationType = MattressPendingOperationType.FUKATTO_OFF;
        nsManager.getMattressPosition();
    }

    @Override
    public void onMattressFukattoStartUI() {
        startMattressFukattoUI();
    }

    @Override
    public void onMattressFukattoStopUI() {
        stopMattressFukattoUI();
    }

    @Override
    public void onMattressPresetSelected(int index) {
        if (index >= 0 && index < 5) {
            //preset selected
            NSMattressPosition target = new NSMattressPosition(mattressPresetValues.get(index));
            setMattressNumericIndicator(target);
            setMattressSegmentBlinking(0, false);
            setMattressSegmentBlinking(1, false);
            setMattressSegmentBlinking(2, false);
            setMattressSegmentBlinking(3, false);
            setMattressSegmentBlinking(4, false);
            setMattressSegmentBlinking(5, false);
            setMattressStartBlinking(true);
            return;
        }
        if (index == 5) {
            setMattressStartBlinking(true);
            return;
        }
        setMattressNumericIndicator(currentMattressPosition);
        setMattressStartBlinking(false);

    }
    //MARK END : MattressPresetEventListener implementation

    //MARK : MattressFreeEventListener implementation
    @Override
    public void onMattressSegmentSelected(int index) {
        if (index >= 0 && index < 5) {
            //segment selected
            setMattressSegmentBlinking(0, false);
            setMattressSegmentBlinking(1, false);
            setMattressSegmentBlinking(2, false);
            setMattressSegmentBlinking(3, false);
            setMattressSegmentBlinking(4, false);
            setMattressSegmentBlinking(5, false);
            setMattressStartBlinking(true);
        } else {
            setMattressStartBlinking(false);
        }
        highlightSingletMattressSegment(index);
    }

    @Override
    public int getMattressValueFor(int index) {
        switch (index) {
            case 0:
                return currentMattressPosition.getHead();
            case 1:
                return currentMattressPosition.getShoulder();
            case 2:
                return currentMattressPosition.getHip();
            case 3:
                return currentMattressPosition.getThigh();
            case 4:
                return currentMattressPosition.getCalf();
            case 5:
                return currentMattressPosition.getFeet();
            default:
                return 1;
        }
    }

    @Override
    public void onMattressMHSSelected(MHSModel selectedMHS) {
        if (selectedMHS != null) {
            setMattressSegmentBlinking(0, false);
            setMattressSegmentBlinking(1, false);
            setMattressSegmentBlinking(2, false);
            setMattressSegmentBlinking(3, false);
            setMattressSegmentBlinking(4, false);
            setMattressSegmentBlinking(5, false);
        }
        setMattressStartBlinking(selectedMHS != null);
    }

    @Override
    public void onHistoryShow() {
        setMattressHistoryUI(false);
    }

    @Override
    public void onHistoryHide() {
        setMattressHistoryUI(true);
    }

    //MARK END : MattressFreeEventListener implementation

    //MARK : Setting Dialog Wrappers
    //Wrapper class for Setting dialog logic to make the code more organized
    private class BedSettingDialogController {
        SettingModel oldSetting = SettingModel.getSetting();
        SettingModel newSetting;

        boolean isInitialized = false;
        int selectedBedSettingTemplate = 0;

        void init() {
            //setup bed template picker
            final List<String> bedPosition = new ArrayList<>();

            bedPosition.add(LanguageProvider.getLanguage("UI000650C015"));
            bedPosition.add(LanguageProvider.getLanguage("UI000650C016"));
            bedPosition.add(LanguageProvider.getLanguage("UI000650C017"));
            bedPosition.add(LanguageProvider.getLanguage("UI000650C018"));
            bedPosition.add(LanguageProvider.getLanguage("UI000650C019"));
            bedPosition.add(LanguageProvider.getLanguage("UI000650C020"));
            bedPosition.add(LanguageProvider.getLanguage("UI000650C021"));
            bedPosition.add(LanguageProvider.getLanguage("UI000610C018"));


            btnBedPositionAdjustment.setText(bedPosition.get(RemoteSettingUtil.lastBedTemplate(RemoteActivity.this)));
            OptionsPickerView bedPositionPicker = new OptionsPickerBuilder(RemoteActivity.this,
                    (options1, options2, options3, v) -> {
                        btnBedPositionAdjustment.setText(bedPosition.get(options1));
                        selectedBedSettingTemplate = options1;
                        RemoteSettingUtil.lastBedTemplate(RemoteActivity.this, options1);
                    })
                    .setCyclic(false, false, false)
                    .setBackgroundId(0)
                    .setCancelText(LanguageProvider.getLanguage("UI000610C038"))
                    .setSubmitText(LanguageProvider.getLanguage("UI000610C039"))
                    .build();

            bedPositionPicker.setPicker(bedPosition);

            btnBedPositionAdjustment.setOnClickListener(v -> {
                bedPositionPicker.setSelectOptions(RemoteSettingUtil.lastBedTemplate(RemoteActivity.this));
                bedPositionPicker.show();
            });
        }

        //MARK : External Events
        void openSettingDialog(NSBedSetting setting) {
            if (!isInitialized) {
                init();
                isInitialized = true;
            }

            oldSetting = SettingModel.getSetting();
            if (isNemuriScanInitiated) {
                oldSetting.setBed_height_locked(setting.isHeightLocked() ? 1 : 0);
                oldSetting.setBed_head_locked(setting.isHeadLocked() ? 1 : 0);
                oldSetting.setBed_leg_locked(setting.isLegLocked() ? 1 : 0);
                oldSetting.setBed_combi_locked(setting.isCombiLocked() ? 1 : 0);
                oldSetting.setBed_fast_mode(setting.isFastMode() ? 1 : 0);
            }

            newSetting = new SettingModel();
            newSetting.copyValuesFrom(oldSetting);
            bedSettingContainer.setAnimation(AnimateUtils.explode(null));
            dialogSettingBed.setVisibility(View.VISIBLE);
            mainLayout.setClickable(true);

            updateUI();
        }

        void closeSettingDialog() {
            dialogSettingBed.setVisibility(View.INVISIBLE);
            mainLayout.setClickable(true);
        }

        void onCloseTap() {
            if (isSpeedSettingChanged() || isLockSettingChanged()) {
                DialogUtil.createCustomYesNo(RemoteActivity.this, "", LanguageProvider.getLanguage("UI000650C026")
                        , LanguageProvider.getLanguage("UI000650C025"), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            closeSettingDialog();
                        }, LanguageProvider.getLanguage("UI000650C024"), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            saveSetting();
                            closeSettingDialog();
                        });
            } else {
                closeSettingDialog();
            }
        }

        void onFastModeTap() {
            newSetting.bed_fast_mode = tglBtnFast.isChecked() ? 1 : 0;
            updateUI();
        }

        void onNormalModeTap() {
            newSetting.bed_fast_mode = !tglBtnUsually.isChecked() ? 1 : 0;
            updateUI();
        }

        void onLockTap(ToggleButton toggleButton) {
            int lockIndex = lockToggles.indexOf(toggleButton);
            switch (lockIndex) {
                case 0:
                    newSetting.bed_combi_locked = toggleButton.isChecked() ? 1 : 0;
                    break;
                case 1:
                    newSetting.bed_head_locked = toggleButton.isChecked() ? 1 : 0;
                    break;
                case 2:
                    newSetting.bed_leg_locked = toggleButton.isChecked() ? 1 : 0;
                    break;
                case 3:
                    newSetting.bed_height_locked = toggleButton.isChecked() ? 1 : 0;
                    break;

            }
            updateUI();
        }

        void onApplyBedTemplateTap() {
            DialogUtil.createCustomYesNo(RemoteActivity.this, "", LanguageProvider.getLanguage("UI000650C034")
                    , LanguageProvider.getLanguage("UI000650C033"), (dialogInterface, i) -> dialogInterface.dismiss(),
                    LanguageProvider.getLanguage("UI000650C032"), (dialogInterface, i) -> {
                        if (isSpeedSettingChanged() || isLockSettingChanged()) {
                            saveSetting();
                        }
                        saveBedTemplate();
                        dialogInterface.dismiss();
                        closeSettingDialog();
                    });
        }

        void onResetBedTemplateTap() {
            DialogUtil.createCustomYesNo(RemoteActivity.this, "", LanguageProvider.getLanguage("UI000650C038")
                    , LanguageProvider.getLanguage("UI000650C037"), (dialogInterface, i) -> dialogInterface.dismiss(),
                    LanguageProvider.getLanguage("UI000650C036"), (dialogInterface, i) -> {
                        showProgress();
                        Integer bedType = nemuriScanModel == null ? null : nemuriScanModel.getInfoType();
                        DeviceTemplateProvider.getDeviceTemplate(RemoteActivity.this, (mattressModels, bedModels, mattressModelDefaults, bedModelDefaults, nemuriConstantsModel) -> resetBedTemplate(), UserLogin.getUserLogin().getId(), bedType);
                        dialogInterface.dismiss();
                        closeSettingDialog();
                    });
        }

        void enableUI() {
            tglBtnFast.setEnabled(true);
            tglBtnUsually.setEnabled(true);
            ViewCollections.run(lockToggles, (view, index) -> view.setEnabled(true));
            btnBedPositionAdjustment.setEnabled(true);
            btnBedConfirm.setEnabled(true);
            textViewBedReset.setEnabled(true);
        }

        void disableUI() {
            tglBtnFast.setEnabled(false);
            tglBtnUsually.setEnabled(false);
            ViewCollections.run(lockToggles, (view, index) -> view.setEnabled(false));
            btnBedPositionAdjustment.setEnabled(false);
            btnBedConfirm.setEnabled(false);
            textViewBedReset.setEnabled(false);
        }
        //MARK END : External Events

        //MARK : Logic
        void updateUI() {
            tglBtnFast.setChecked(newSetting.bed_fast_mode == 1);
            tglBtnUsually.setChecked(newSetting.bed_fast_mode == 0);
            if (newSetting.bed_combi_locked == 1) {
                lockToggles.get(0).setChecked(true);
                lockToggleIcons.get(0).setVisibility(View.VISIBLE);
            } else {
                lockToggles.get(0).setChecked(false);
                lockToggleIcons.get(0).setVisibility(View.GONE);
            }
            lockToggles.get(0).setEnabled(currentBedSpec.isCombiLockSupported());
            if (newSetting.bed_head_locked == 1) {
                lockToggles.get(1).setChecked(true);
                lockToggleIcons.get(1).setVisibility(View.VISIBLE);
            } else {
                lockToggles.get(1).setChecked(false);
                lockToggleIcons.get(1).setVisibility(View.GONE);
            }
            lockToggles.get(1).setEnabled(currentBedSpec.isHeadLockSupported());
            if (newSetting.bed_leg_locked == 1) {
                lockToggles.get(2).setChecked(true);
                lockToggleIcons.get(2).setVisibility(View.VISIBLE);
            } else {
                lockToggles.get(2).setChecked(false);
                lockToggleIcons.get(2).setVisibility(View.GONE);
            }
            lockToggles.get(2).setEnabled(currentBedSpec.isLegLockSupported());
            if (newSetting.bed_height_locked == 1) {
                lockToggles.get(3).setChecked(true);
                lockToggleIcons.get(3).setVisibility(View.VISIBLE);
            } else {
                lockToggles.get(3).setChecked(false);
                lockToggleIcons.get(3).setVisibility(View.GONE);
            }
            lockToggles.get(3).setEnabled(currentBedSpec.isHeightLockSupported());
            if (currentNSSpec.getBedType() == NSSpec.BED_MODEL.ACTIVE_SLEEP) {
                lockToggles.get(3).setVisibility(View.GONE);
                lockToggleIcons.get(3).setVisibility(View.GONE);
            }
        }

        void saveSetting() {
            oldSetting.setBed_fast_mode(newSetting.bed_fast_mode);
            oldSetting.setBed_combi_locked(newSetting.bed_combi_locked);
            oldSetting.setBed_head_locked(newSetting.bed_head_locked);
            oldSetting.setBed_leg_locked(newSetting.bed_leg_locked);
            oldSetting.setBed_height_locked(newSetting.bed_height_locked);

            LogProvider.logBedSettingChange(RemoteActivity.this, newSetting.bed_fast_mode, newSetting.bed_combi_locked,
                    newSetting.bed_head_locked, newSetting.bed_leg_locked, newSetting.bed_height_locked, false);
            if (isNemuriScanInitiated) {
                nsManager.setBedSetting(new NSBedSetting(newSetting));

                setBedSettingRetryTimer = () -> {
                    bedSettingRetryCount += 1;
                    nsManager.setBedSetting(new NSBedSetting(newSetting));
                    if (bedSettingRetryCount <= BED_SETTING_MAX_RETRY) {
                        setBedSettingRetryHandler.postDelayed(setBedSettingRetryTimer, (long) (BED_SETTING_OPERATION_TIMEOUT * 1000 * 4));
                    } else {
                        setBedSettingRetryHandler.removeCallbacks(setBedSettingRetryTimer);
                        bedSettingRetryCount = 0;
                    }
                };
                setBedSettingRetryHandler.postDelayed(setBedSettingRetryTimer, (long) (BED_SETTING_OPERATION_TIMEOUT * 1000 * 4));
            }
        }

        void saveBedTemplate() {
            int selectedTemplate = shiftUIIndex();
            DeviceTemplateBedModel target = DeviceTemplateBedModel.getById(selectedTemplate, false);
            target.setHead(currentBedPosition.getHead());
            target.setLeg(currentBedPosition.getLeg());

            // 更新禁止値ではない場合、高さと傾斜も更新する
            DeviceTemplateBedModel preset = bedPresetValues.get(selectedTemplate - 1);
            if (preset.getHeight() != DeviceTemplateBedModel.heightAndTiltDefaultValue_Other) {
                target.setHeight(currentBedPosition.getHeight());
            }
            if (preset.getTilt() != DeviceTemplateBedModel.heightAndTiltDefaultValue_Other) {
                target.setTilt(currentBedPosition.getTilt());
            }

            //update data used by remote
            bedPresetValues.set(selectedTemplate - 1, target);

            //send log
            showProgress();
            Integer bedType = nemuriScanModel == null ? null : nemuriScanModel.getInfoType();
            LogProvider.logBedTemplateChange(RemoteActivity.this, target, bedType, () -> runOnUiThread(() -> hideProgress()));
        }

        void resetBedTemplate() {
            int selectedTemplate = shiftUIIndex();
            DeviceTemplateBedModel target = DeviceTemplateBedModel.getById(selectedTemplate, false);
            DeviceTemplateBedModel defaultTemplate = DeviceTemplateBedModel.getById(selectedTemplate, true);
            target.setHead(defaultTemplate.getHead());
            target.setLeg(defaultTemplate.getLeg());
            target.setHeight(defaultTemplate.getHeight());
            target.setTilt(defaultTemplate.getTilt());

            //update data used by remote
            bedPresetValues.set(selectedTemplate - 1, target);

            //send log
            Integer bedType = nemuriScanModel == null ? null : nemuriScanModel.getInfoType();
            LogProvider.logBedTemplateChange(RemoteActivity.this, target, bedType, () -> runOnUiThread(() -> hideProgress()));
        }

        int shiftUIIndex() {
            //+1 to shift 0 based index to 1 based index
            int selectedTemplate = RemoteSettingUtil.lastBedTemplate(RemoteActivity.this) + 1;
            if (selectedTemplate == 8) {
                //shift one index after "relax", relax can't be changed
                selectedTemplate = 9;
            }
            return selectedTemplate;
        }

        boolean isSpeedSettingChanged() {
            return newSetting.bed_fast_mode != oldSetting.bed_fast_mode;
        }

        boolean isLockSettingChanged() {
            return newSetting.bed_combi_locked != oldSetting.bed_combi_locked ||
                    newSetting.bed_head_locked != oldSetting.bed_head_locked ||
                    newSetting.bed_leg_locked != oldSetting.bed_leg_locked ||
                    newSetting.bed_height_locked != oldSetting.bed_height_locked;
        }
        //MARK END : Logic
    }

    private class MattressSettingDialogController {

        boolean isInitialized = false;
        int selectedMattressSettingTemplate = 0;

        void init() {
            //setup mattress template picker
            final List<String> matressPosition = new ArrayList<>();
            matressPosition.add(LanguageProvider.getLanguage("UI000630C006"));
            matressPosition.add(LanguageProvider.getLanguage("UI000630C007"));

            btnMatressPositionAdjustment.setText(matressPosition.get(RemoteSettingUtil.lastMattressTemplate(RemoteActivity.this)));
            OptionsPickerView matressPositionPicker = new OptionsPickerBuilder(RemoteActivity.this,
                    (options1, options2, options3, v) -> {
                        btnMatressPositionAdjustment.setText(matressPosition.get(options1));
                        selectedMattressSettingTemplate = options1;
                        RemoteSettingUtil.lastMattressTemplate(RemoteActivity.this, options1);
                    })
                    .setCyclic(false, false, false)
                    .setBackgroundId(0)
                    .setCancelText(LanguageProvider.getLanguage("UI000610C040"))
                    .setSubmitText(LanguageProvider.getLanguage("UI000610C050"))
                    .build();

            matressPositionPicker.setPicker(matressPosition);

            btnMatressPositionAdjustment.setOnClickListener(v -> {
                matressPositionPicker.setSelectOptions(RemoteSettingUtil.lastMattressTemplate(RemoteActivity.this));
                matressPositionPicker.show();
            });
        }

        //MARK : External Events
        void openSettingDialog() {
            if (!isInitialized) {
                init();
                isInitialized = true;
            }
            matressSettingContainer.setAnimation(AnimateUtils.explode(null));
            dialogSettingMattress.setVisibility(View.VISIBLE);
            mainLayout.setClickable(true);
        }

        void closeSettingDialog() {
            dialogSettingMattress.setVisibility(View.INVISIBLE);
            mainLayout.setClickable(true);
        }

        void onCloseTap() {
            closeSettingDialog();
        }

        void onApplyMattressTemplateTap() {
            DialogUtil.createCustomYesNo(RemoteActivity.this, "", LanguageProvider.getLanguage("UI000660C021")
                    , LanguageProvider.getLanguage("UI000660C020"), (dialogInterface, i) -> dialogInterface.dismiss(),
                    LanguageProvider.getLanguage("UI000660C019"), (dialogInterface, i) -> {
                        saveMattressTemplate();
                        dialogInterface.dismiss();
                        closeSettingDialog();
                    });
        }

        void onResetMattressTemplateTap() {
            DialogUtil.createCustomYesNo(RemoteActivity.this, "", LanguageProvider.getLanguage("UI000660C025")
                    , LanguageProvider.getLanguage("UI000660C024"), (dialogInterface, i) -> dialogInterface.dismiss(), LanguageProvider.getLanguage("UI000660C023"), (dialogInterface, i) -> {
                        resetMattressTemplate();
                        dialogInterface.dismiss();
                        closeSettingDialog();
                    });
        }

        void onDehumidifierTap() {
            if (isNemuriScanInitiated) {
                mattressPendingOperationType = MattressPendingOperationType.DEHUMIDIFIER;
                nsManager.getMattressPosition();
                runOnUiThread(RemoteActivity.this::showProgress);
                new Handler().postDelayed(() -> runOnUiThread(RemoteActivity.this::hideProgress), dehumidifierDelayCount * 1000);
            }
        }

        void enableUI() {
            btnDehumidifier.setEnabled(true);
            btnMatressPositionAdjustment.setEnabled(true);
            btnMatressConfirm.setEnabled(true);
            textViewMatressReset.setEnabled(true);
        }

        void disableUI() {
            btnDehumidifier.setEnabled(false);
            btnMatressPositionAdjustment.setEnabled(false);
            btnMatressConfirm.setEnabled(false);
            textViewMatressReset.setEnabled(false);
        }
        //MARK END : External Events

        //MARK : Logic
        void updateUI(NSMattressPosition mattressPosition) {
            if (mattressPosition.getDehumidifierOperation() == 1) {
                Logger.w("MATTRESS DEHUMIDIFER UPDATE UI BUTTON ON");
                btnDehumidifier.setBackground(getDrawable(R.drawable.dehumidifier_on));
                btnDehumidifier.setTextColor(getColor(R.color.dehumidifier_on_text_color));
                String title = LanguageProvider.getLanguage("UI000660C026").replace("%MINUTE_LEFT%", String.valueOf(mattressPosition.getDehumidifierTime()));
                btnDehumidifier.setText(title);
            } else {
                Logger.w("MATTRESS DEHUMIDIFER UPDATE UI BUTTON OF");
                btnDehumidifier.setBackground(getDrawable(R.drawable.dehumidifier_off));
                btnDehumidifier.setTextColor(getColor(R.color.dehumidifier_off_text_color));
                btnDehumidifier.setText(LanguageProvider.getLanguage("UI000660C004"));
            }
        }

        void saveMattressTemplate() {
            int selectedTemplate = shiftUIIndex();
            DeviceTemplateMattressModel target = DeviceTemplateMattressModel.getById(selectedTemplate, false);
            target.setHead(currentMattressPosition.getHead());
            target.setShoulder(currentMattressPosition.getShoulder());
            target.setHip(currentMattressPosition.getHip());
            target.setThigh(currentMattressPosition.getThigh());
            target.setCalf(currentMattressPosition.getCalf());
            target.setFeet(currentMattressPosition.getFeet());

            //update data used by remote
            mattressPresetValues.set(selectedTemplate - 1, target);

            //send log
            showProgress();
            LogProvider.logMattressTemplateChange(RemoteActivity.this, target, () -> runOnUiThread(() -> hideProgress()));

        }

        void resetMattressTemplate() {
            int selectedTemplate = shiftUIIndex();
            DeviceTemplateMattressModel defaultValue = DeviceTemplateMattressModel.getById(selectedTemplate, true);
            DeviceTemplateMattressModel target = DeviceTemplateMattressModel.getById(selectedTemplate, false);
            target.setHead(defaultValue.getHead());
            target.setShoulder(defaultValue.getShoulder());
            target.setHip(defaultValue.getHip());
            target.setThigh(defaultValue.getThigh());
            target.setCalf(defaultValue.getCalf());
            target.setFeet(defaultValue.getFeet());

            //update data used by remote
            mattressPresetValues.set(selectedTemplate - 1, target);

            //send log
            showProgress();
            LogProvider.logMattressTemplateChange(RemoteActivity.this, target, () -> runOnUiThread(() -> hideProgress()));

        }

        int shiftUIIndex() {
            //only memory 1 and 2 can be changed
            int selectedTemplate = RemoteSettingUtil.lastMattressTemplate(RemoteActivity.this);
            if (selectedTemplate == 0)
                return 4;
            else
                return 5;
        }
        //MARK END : Logic
    }
    //MARK END : Setting Dialog Wrappers

    //MARK : Helper enums
    private enum RemoteTabType {
        BED, MATTRESS
    }

    private enum RemotePagerType {
        PRESET, FREE, RECOMMEND
    }

    private enum MattressPendingOperationType {
        NONE, POSITION, FREE, DEHUMIDIFIER, FUKATTO_ON, FUKATTO_OFF, RECOMMEND
    }
    //MARK END : Helper enums

    public void startMattressFukatto() {
        if (isNemuriScanInitiated) {
            mattressPendingOperationType = MattressPendingOperationType.FUKATTO_ON;
            nsManager.getMattressPosition();
        }
    }

    public void sendRemoteLog(Boolean isStart) {
        String jsonLogs = "";
        switch (currentTabType) {
            case BED:
                if (isStart) {
                    LogUserAction.sendNewLog(userService, "NS_REMOTE_BED_PRESET_START", jsonLogs, "", "UI000610");
                } else {
                    LogUserAction.sendNewLog(userService, "NS_REMOTE_BED_PRESET_STOP", jsonLogs, "", "UI000610");
                }
                break;
            case MATTRESS:
                if (isStart) {
                    LogUserAction.sendNewLog(userService, "NS_REMOTE_MATTRESS_PRESET_START", jsonLogs, "", "UI000610");
                } else {
                    LogUserAction.sendNewLog(userService,
                            "NS_REMOTE_MATTRESS_PRESET_STOP", jsonLogs, "", "UI000610");
                }
                break;
        }
    }

    public void applyLockWhenBedExist() {
        if (currentNSSpec.isBedExist()) {
            bedManualFragment.applyLock(currentSetting);
            runOnUiThread(() -> {
                Integer bedType = nemuriScanModel == null ? null : nemuriScanModel.getInfoType();
                bedPresetFragment.applyLock(bedType, currentSetting);
            });
        }
    }

    static int randomBoleanValue = 0;

    private boolean getRandomBooleanValue() {
        randomBoleanValue++;
        Logger.d("randomBoleanValue % 5 " + (randomBoleanValue % 5));
//        fukattoSim = randomBoleanValue > 4;
        return false;
    }

    static int busyValue = 0;

    private NSMattressStatus generateBusyStatus(NSMattressStatus mattressStatus) {
        int mod = busyValue % 6;
        switch (mod) {
            case 0:
                mattressStatus.setHeadBusy(true);
                break;
            case 1:
                mattressStatus.setShoulderBusy(true);
                break;
            case 2:
                mattressStatus.setHipBusy(true);
                break;
            case 3:
                mattressStatus.setThighBusy(true);
                break;
            case 4:
                mattressStatus.setCalfBusy(true);
                break;
            case 5:
                mattressStatus.setFeetBusy(true);
                break;
        }
        busyValue++;
        return mattressStatus;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(gpsReceiver);
    }

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if (PermissionUtil.hasLocationPermissions(RemoteActivity.this)) {
                    IOSDialogRight.Dismiss();
                    showLocationPermissionDialogAlert();
                    return;
                }
                if (PermissionUtil.isLocationServiceEnable(RemoteActivity.this)) {
                    IOSDialogRight.Dismiss();
                    showLocationServiceDialogAlert();
                    return;
                }
            }
        }
    };

    private void disableUIByLocation() {
        if (!PermissionUtil.locationFeatureEnabled(RemoteActivity.this)) {
            disableBedUI();
            disableMattressUI();
        }
    }

    private enum BedMattressCheck{
        ACTIVE,IGNORE
    }

    public void initDialogSettingHardness(){
        LogUserAction.sendNewLog(userService, "CHALENGE_MATTRESS_SETTING_SHOW", "", "","");
        SettingModel settingModel = SettingModel.getSetting();
        FormPolicyModel formPolicyModel = FormPolicyModel.getPolicy();

        selectedHardness = formPolicyModel.getMattressHardnessSettingById(settingModel.user_desired_hardness);
        final MattressHardnessSettingModel oldHardness = selectedHardness;
        selectedHardnessIndex = formPolicyModel.getMattressHardnessSettingIndexById(settingModel.user_desired_hardness);

        List<MattressHardnessSettingModel> hardnesOptions = new ArrayList<>();
        hardnesOptions.addAll(formPolicyModel.getMattressHardnessSetting());

        final List<String> listHardness = new ArrayList<>();
        for (MattressHardnessSettingModel mattressHardnessSettingModel:hardnesOptions
        ) {
            listHardness.add(mattressHardnessSettingModel.getValue());
        }

        viewDialogHardness = getLayoutInflater().inflate(R.layout.dialog_hardness,null);
        viewDialogHardness.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mainLayout.addView(viewDialogHardness);
        applyLocalization();

        CardView cardHardness = viewDialogHardness.findViewById(R.id.content_set_hardness);
        cardHardness.setAnimation(AnimateUtils.explode((() -> runOnUiThread(() -> {
        }))));

        LinearLayout closeHardness = viewDialogHardness.findViewById(R.id.close_hardness);
        LinearLayout spinHardness = viewDialogHardness.findViewById(R.id.spin_hardness);
        TextView txtHardness = viewDialogHardness.findViewById(R.id.txt_hardness);
        Button btnSaveHardness = viewDialogHardness.findViewById(R.id.btn_save_hardness);
        btnSaveHardness.setEnabled(false);
        closeHardness.setOnClickListener(v -> {
            if(selectedHardness.getId() != oldHardness.getId()) {
                DialogUtil.createCustomYesNo(RemoteActivity.this, "",
                        LanguageProvider.getLanguage("UI000672C014"),
                        LanguageProvider.getLanguage("UI000672C015"), (dialogInterface, i) -> {

                        }, LanguageProvider.getLanguage("UI000672C016"), (dialogInterface, i) -> {
                            LogUserAction.sendNewLog(userService, "CHALENGE_MATTRESS_SHOW", "", "", "");
                            viewDialogHardness.setVisibility(View.GONE);
                        });
            }else{
                LogUserAction.sendNewLog(userService, "CHALENGE_MATTRESS_SHOW", "", "", "");
                viewDialogHardness.setVisibility(View.GONE);
            }
        });
        txtHardness.setText(selectedHardness.getValue());
        spinHardness.setOnClickListener(view->{
            OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                    String tx = listHardness.get(options1);
                    txtHardness.setText(tx);
                    selectedHardness = hardnesOptions.get(options1);
                    btnSaveHardness.setEnabled(selectedHardness.getId() != oldHardness.getId());
                    selectedHardnessIndex = options1;
                }
            }).setBackgroundId(0)
                    .setCancelText(LanguageProvider.getLanguage("UI000672C007"))
                    .setSubmitText(LanguageProvider.getLanguage("UI000672C008"))
                    .setCyclic(false, false, false)
                    .setOutSideCancelable(false)
                    .setSelectOptions(selectedHardnessIndex)
                    .build();

            pvOptions.setPicker(listHardness);
            pvOptions.show();
        });

        btnSaveHardness.setOnClickListener(v -> {
            showProgress();
            MattressSettingProvider.setMattressSetting(this, selectedHardness, (isSuccess, result, errTag) -> {
                hideProgress();
                if(isSuccess){
                    SettingModel.saveSetting("user_desired_hardness",String.valueOf(selectedHardness.getId()));
                    LogUserAction.sendNewLog(userService, "CHALENGE_MATTRESS_HARDNESS_SETTING_SUCCESS", "", "","");

                    mattressSettingModel.setHighestMHS(result.getHighestMHS());
                    MattressSettingProvider.setSetting(mattressSettingModel);
                    mattressSettingModel = MattressSettingProvider.getSetting().getUnmanaged();
                    if(mattressRecomendFragment != null){
                        mattressRecomendFragment.setMattressSettingModel(mattressSettingModel);
                    }
                }else{
                    LogUserAction.sendNewLog(userService, "CHALENGE_MATTRESS_HARDNESS_SETTING_FAILED", "", "","");
                    if(errTag.equalsIgnoreCase("UI000802C002")){
                        DialogUtil.createSimpleOkDialog(RemoteActivity.this, "", LanguageProvider.getLanguage(errTag),
                                LanguageProvider.getLanguage("UI000802C003"), (dialogInterface, i) -> dialogInterface.dismiss());
                    }else {
                        DialogUtil.createSimpleOkDialogLink(RemoteActivity.this, "",
                                LanguageProvider.getLanguage(errTag), //message
                                LanguageProvider.getLanguage("UI000802C177"), (dialogInterface, i) -> { //faq
                                    Intent faqIntent = new Intent(RemoteActivity.this, FaqActivity.class);
                                    faqIntent.putExtra("ID_FAQ", "UI000802C177");
                                    faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(faqIntent);
                                    dialogInterface.dismiss();
                                }, LanguageProvider.getLanguage("UI000802C003"), (dialogInterface, i) -> dialogInterface.dismiss()); //ok string
                    }
                }
                viewDialogHardness.setVisibility(View.GONE);
            },0);
        });
    }
}

