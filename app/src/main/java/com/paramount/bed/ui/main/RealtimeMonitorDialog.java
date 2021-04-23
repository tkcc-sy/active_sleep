package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSRealtimeDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.interfaces.NSSettingDelegate;
import com.paramount.bed.ble.pojo.NSRealtimeFeed;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.ble.pojo.NSWifiSetting;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.AnimateUtils;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.RoundedWebView;
import com.paramount.bed.util.WebViewUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RealtimeMonitorDialog extends BaseActivity implements NSScanDelegate, NSConnectionDelegate, NSSettingDelegate, NSRealtimeDelegate {
    //MARK : View binding
    @BindView(R.id.main_webview)
    RoundedWebView mainWebView;
    @BindView(R.id.main_webview_container)
    RelativeLayout mainWebViewContainer;

    @OnClick(R.id.btnClose)
    void onCloseTap() {
        if (isNemuriScanInitiated) {
            purgeBLE();
            isIntentionalDC = true;
        }
        finish();
    }
    //MARK END: View binding

    //MARK : Logic vars
    public MediaPlayer pulseAudioPlayer;
    public Timer realTimeDataFetchTimer;
    public Timer graphRendererTimer;
    public Timer bioRendererTimer;
    //MARK END: Logic vars

    //MARK : BLE Vars
    NSRealtimeFeed lastData = new NSRealtimeFeed();
    private boolean isNemuriScanInitiated = false;
    private boolean isIntentionalDC = false;
    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            //LOG HERE NS_REALTIME_CONNECTION_FAILED
            LogUserAction.sendNewLog(userService, "NS_REALTIME_CONNECTION_FAILED", "1", "", "UI000550");
            runOnUiThread(() -> {
                hideProgress();
                btnClose.setEnabled(true);
            });
            purgeTimer();
            isIntentionalDC = true;
            //show alert
            DialogUtil.createCustomYesNo(RealtimeMonitorDialog.this, "", LanguageProvider.getLanguage("UI000610C022"),
                    LanguageProvider.getLanguage("UI000610C026"), (dialogInterface, i) -> finish(),
                    LanguageProvider.getLanguage("UI000610C025"), (dialogInterface, i) -> tryToConnectBLE());
        }
    };
    private int lastGraphSequence = -1;
    private int lastBioSequence = -1;
    private NSManager nsManager;


    private int reconnectNSRetryCount;

    private Handler reconnectNSWaitHandler = new Handler();
    private Runnable reconnectNSWaitTimer;
    private NemuriConstantsModel nemuriConstantsModel = new NemuriConstantsModel();
    private NemuriScanModel currentNemuriscan = null;
    private boolean isLocationPermissionRejected = false;

    //MARK END: BLE Vars
    ImageView btnClose;

    //MARK : Fragment Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setTheme(R.style.ThemeRealtimeDialogue);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            restartActivity();
        }
        //android O fix bug orientation

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        ButterKnife.bind(this);
        btnClose = findViewById(R.id.btnClose);
        btnClose.setEnabled(false);

        NemuriScanModel localStorageNemuriscan = NemuriScanModel.get();
        if(localStorageNemuriscan != null){
            currentNemuriscan = localStorageNemuriscan.getUnmanaged();
        }

        mainWebView.setVisibility(View.GONE);
        mainWebViewContainer.setAnimation(AnimateUtils.explode((() -> runOnUiThread(() -> {

        }))));
        //Audio setup
        pulseAudioPlayer = MediaPlayer.create(this, Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.realtime));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            new Handler().postDelayed(() -> runOnUiThread(() -> PermissionUtil.showLocationPermissionDialogAlert(RealtimeMonitorDialog.this, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    finish();
                    hideProgress();
                    purgeBLE();
                }

                @Override
                public void onPermissionGranted() {
                    isLocationPermissionRejected = false;
                    initBLE();
                }
            })), 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LOG HERE NS_REALTIME_CLOSED
        LogUserAction.sendNewLog(userService, "NS_REALTIME_CLOSED", "1", "", "UI000550");
        setStartRealTimeFetch(false);
        purgeBLE();
    }

    public void restartActivity() {
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        new Handler().postDelayed(() -> {
            overridePendingTransition(0, 0);
            startActivity(intent);
        }, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isNemuriScanInitiated) {
            setStartRealTimeFetch(false);
        }
        purgeBLE();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IOSDialogRight.Dismiss();
        if(!isLocationPermissionRejected) {
            runOnUiThread(() -> initBLE());
        }
        AlarmsQuizModule.run(this);

        if(nsManager != null){
            nsManager.setDelegate(this);
        }
    }

    public void initBLE() {
        //BLE setup
        showProgress();

        Integer bedType = currentNemuriscan == null ? null : currentNemuriscan.getInfoType();
        nsManager = NSManager.getInstance(this, this);
        DeviceTemplateProvider.getDeviceTemplate(this, (mattressModels, bedModels, mattressModelDefaults, bedModelDefaults, newNemuriConstantsModel) -> {
            nemuriConstantsModel.copyValue(newNemuriConstantsModel);
            //Webview setup
            String htmlContent = "";
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey("html_content")) {
                htmlContent = extras.getString("html_content");
            }
            initWebView(htmlContent);
        }, UserLogin.getUserLogin().getId(), bedType);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_realtime_monitor_dialog;
    }

    //MARK END : Fragment Lifecycle

    private void setStartRealTimeFetch(boolean shouldStart) {
        try {
            //LOG HERE NS_REALTIME_CONNECTION_SUCCESS
            LogUserAction.sendNewLog(userService, "NS_REALTIME_CONNECTION_SUCCESS", "1", "", "UI000550");
            if (realTimeDataFetchTimer != null) {
                realTimeDataFetchTimer.purge();
                realTimeDataFetchTimer.cancel();
                realTimeDataFetchTimer = null;
            }
            if (graphRendererTimer != null) {
                graphRendererTimer.purge();
                graphRendererTimer.cancel();
                graphRendererTimer = null;
            }
            if (bioRendererTimer != null) {
                bioRendererTimer.purge();
                bioRendererTimer.cancel();
                bioRendererTimer = null;
            }
            if (shouldStart) {
                realTimeDataFetchTimer = new Timer();
                realTimeDataFetchTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (isNemuriScanInitiated) {
                            nsManager.getRealtimeFeed();
                        }
                    }
                }, 0, 100);
                graphRendererTimer = new Timer();
                graphRendererTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (isNemuriScanInitiated) {
                            if (lastGraphSequence != lastData.sequence) {
                                lastGraphSequence = lastData.sequence;
                                String javascript = "javascript:(function(){" +
                                        "setGraphData(" + lastData.getData1() + "," + lastData.getData2() + "," + lastData.getData3() + "," +
                                        lastData.getData4() + "," + lastData.getData5() + "," + lastData.getData6() + "," +
                                        lastData.getData7() + "," + lastData.getData8() + ");" + "})()";

                                runOnUiThread(() -> mainWebView.loadUrl(javascript));
                            }
                        }
                    }
                }, 0, 200);
                bioRendererTimer = new Timer();
                bioRendererTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        Logger.d("DEBUG START BIORENDERER1");
                        if (isNemuriScanInitiated) {
                            if (lastBioSequence != lastData.sequence) {
                                lastBioSequence = lastData.sequence;
                                String adjustedRespirationRate = "-";
                                if (lastData.respRate <= 251 && lastData.respRate > 0) {
                                    adjustedRespirationRate = String.valueOf((int) (lastData.respRate * 0.2));
                                }
                                String adjustedHeartRate = "-";
                                if (lastData.heartRate <= 251 && lastData.heartRate > 0) {
                                    adjustedHeartRate = String.valueOf(lastData.heartRate);
                                }
                                String javascript = "javascript:(function(){" +
                                        "setBioData('" + adjustedHeartRate + "','" + adjustedRespirationRate + "');" +
                                        "})()";
                                Logger.d("DEBUG START BIORENDERER2");
                                Logger.d("Respiration Rate : " + lastData.respRate + " Heart Rate : " + lastData.heartRate);
                                runOnUiThread(() -> {
                                    mainWebView.loadUrl(javascript);
                                    pulseAudioPlayer.start();
                                });
                            }
                        }
                    }
                }, 0, 500);
            } else {
                purgeBLE();
                if (pulseAudioPlayer != null && pulseAudioPlayer.isPlaying()) {
                    pulseAudioPlayer.stop();
                    try {
                        pulseAudioPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(String content) {
        mainWebView.setScrollbarFadingEnabled(true);
        mainWebView.setVisibility(View.VISIBLE);
        mainWebView.setBackgroundColor(Color.TRANSPARENT);

        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        WebViewUtil.fixWebViewFonts(mainWebView);
        mainWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (!PermissionUtil.hasLocationPermissions(RealtimeMonitorDialog.this)) {
                    nsManager.requestLocationPermission(RealtimeMonitorDialog.this);
                    return;
                }
                tryToConnectBLE();
                DisplayUtils.FONTS.applyFontScale(RealtimeMonitorDialog.this,mainWebView);
            }
        });
        mainWebView.loadDataWithBaseURL("file:///android_asset/", content, "text/html", "utf-8", "");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //MARK : NSBaseDelegate
    @Override
    public void onCommandWritten(NSOperation command) {
        //TODO : HANDLE ILLEGAL BLE OP
        if (command.getCommandCode() == NSOperation.FREE_DECREASE_COMBI.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_MATTRESS_POSITION.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_BED_SETTING.getCommandCode()) {
            //DISCONNECT & SHOW ILLEGAL OPERATION ALERT
            purgeBLE();
            DialogUtil.createCustomYesNo(RealtimeMonitorDialog.this,
                    "",
                    LanguageProvider.getLanguage("UI000802C191"),
                    LanguageProvider.getLanguage("UI000802C193"),
                    (dialogInterface, i) -> {
                        finish();
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

    //MARK START : NSConnectionDelegate
    @Override
    public void onConnectionEstablished() {
        runOnUiThread(() -> {
            nsManager.getSerialNumber();
        });
    }

    @Override
    public void onDisconnect() {
        if (isIntentionalDC) {
            isIntentionalDC = false;
            return;
        }
        setStartRealTimeFetch(false);
        reconnectNSRetryCount = 0;
        reconnectNSWaitHandler.removeCallbacks(reconnectNSWaitTimer);
        postDataReconnect();
    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {
        if (currentNemuriscan != null) {
            nsManager.getNSSpec();
        }
    }

    @Override
    public void onAuthenticationFinished(int result) {
        Logger.d("NSManager onAuthenticationFinished "+result);
        if (result == NSConstants.NS_AUTH_SUCCESS || result == NSConstants.NS_AUTH_REG_SUCCESS) {
            //LOG HERE NS_SET_SERVERID_SUCCESS
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_SUCCESS", "1", "", "UI000550");
            isNemuriScanInitiated = true;
            connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
            setStartRealTimeFetch(true);
            runOnUiThread(() -> {
                hideProgress();
                btnClose.setEnabled(true);
            });
        } else {
            //LOG HERE NS_SET_SERVERID_FAILED
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_FAILED", "1", "", "UI000550");
            runOnUiThread(() -> DialogUtil.createSimpleOkDialog(RealtimeMonitorDialog.this, "", LanguageProvider.getLanguage("UI000610C034"),
                    LanguageProvider.getLanguage("UI000610C029"), (dialogInterface, i) -> onBackPressed()));
        }
    }

    @Override
    public void onNSStatusReceived(int systemStatus, int bleStatus, int wifiStatus) {

    }

    @Override
    public void onNSSpecReceived(NSSpec spec) {
        if (currentNemuriscan != null) {
            currentNemuriscan.updateSpec(spec);
        }
        LogUserAction.sendNewLog(userService, "REALTIME_FW_MODE", String.valueOf(spec.isFWMode()), "", "UI000550");

        nsManager.requestAuthentication(currentNemuriscan.getServerGeneratedId());
    }

    @Override
    public void onConnectionStalled(int status) {

    }
    //MARK END : NSConnectionDelegate

    //MARK : NSScanDelegate Implementation
    @Override
    public void onStartScan() {

    }

    @Override
    public void onLocationPermissionDenied() {
        isLocationPermissionRejected = true;
        new Handler().postDelayed(() -> runOnUiThread(() -> showLocationPermissionDialogAlert()),1000);
    }

    @Override
    public void onLocationServiceDisabled() {
        runOnUiThread(() -> showLocationServiceDialogAlert());
    }

    public void showLocationPermissionDialogAlert() {
        if (currentNemuriscan != null) {
            purgeTimer();
            PermissionUtil.showLocationPermissionDialogAlert(RealtimeMonitorDialog.this, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    finish();
                    hideProgress();
                    purgeBLE();
                }

                @Override
                public void onPermissionGranted() {
                    initBLE();
                }
            });
        } else {
            purgeBLE();
            PermissionUtil.showLocationPermissionDialogAlert(RealtimeMonitorDialog.this, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    finish();
                    hideProgress();
                    purgeBLE();
                }

                @Override
                public void onPermissionGranted() {
                    isLocationPermissionRejected = false;
                    DialogUtil.createSimpleOkDialogLink(RealtimeMonitorDialog.this, "", LanguageProvider.getLanguage("UI000610C030"),
                            LanguageProvider.getLanguage("UI000610C043"), (dialogInterface, i) -> {
                                Intent faqIntent = new Intent(RealtimeMonitorDialog.this, FaqActivity.class);
                                faqIntent.putExtra("ID_FAQ", "UI000610C043");
                                faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(faqIntent);
                                dialogInterface.dismiss();
                            }, LanguageProvider.getLanguage("UI000610C031"), (dialogInterface, i) -> {
                                finish();
                            });
                    return;
                }
            });

        }
    }

    public void showLocationServiceDialogAlert() {
        if (currentNemuriscan != null) {
            PermissionUtil.showLocationServiceDialogAlert(RealtimeMonitorDialog.this, new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                    finish();
                    hideProgress();
                    purgeBLE();
                }

                @Override
                public void onEnabled() {
                    initBLE();
                }
            });
        } else {
            PermissionUtil.showLocationServiceDialogAlert(RealtimeMonitorDialog.this, new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                    finish();
                    hideProgress();
                    purgeBLE();
                }

                @Override
                public void onEnabled() {
                    DialogUtil.createCustomYesNo(RealtimeMonitorDialog.this, "", LanguageProvider.getLanguage("UI000610C022"),
                            LanguageProvider.getLanguage("UI000610C026"), (dialogInterface, i) -> finish(),
                            LanguageProvider.getLanguage("UI000610C025"), (dialogInterface, i) -> initBLE());
                }
            });

        }
    }

    @Override
    public void onCancelScan() {

    }

    @Override
    public void onStopScan() {

    }

    @Override
    public void onScanResult(ScanResult scanResult) {
        if (currentNemuriscan != null) {
            String savedMac = currentNemuriscan.getMacAddress();
            String targetMac = scanResult.getDevice().getAddress();
            if (scanResult.getDevice().getName() != null) {
                Logger.v("RealtimeMonitor : Scanning BLE, looking for " + savedMac + " trying " + targetMac + " " + scanResult.getDevice().getName());
            }
            if (savedMac.equalsIgnoreCase(targetMac)) {
                nsManager.connectToDevice(scanResult.getDevice(), this);
                nsManager.stopScan();
            }
        }
    }
    //MARK END: NSScanDelegate Implementation

    //MARK : NSSettingDelegate Implementation
    @Override
    public void onSetNSURLFinished(boolean isSuccess) {

    }

    @Override
    public void onGetWifiReceived(NSWifiSetting data) {

    }

    @Override
    public void onSetWifiFinished(boolean isSuccess) {

    }
    //MARK END : NSSettingDelegate Implementation

    //MARK : ble related functions
    private void tryToConnectBLE() {
        if(currentNemuriscan == null){
            DialogUtil.createSimpleOkDialogLink(RealtimeMonitorDialog.this, "", LanguageProvider.getLanguage("UI000610C030"),
                    LanguageProvider.getLanguage("UI000610C043"), (dialogInterface, i) -> {
                        Intent faqIntent = new Intent(RealtimeMonitorDialog.this, FaqActivity.class);
                        faqIntent.putExtra("ID_FAQ", "UI000610C043");
                        faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(faqIntent);
                        dialogInterface.dismiss();
                    }, LanguageProvider.getLanguage("UI000610C031"), (dialogInterface, i) -> {
                        finish();
                    });
            return;
        }
        showProgress();
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        Logger.d("postDelayed " + nemuriConstantsModel.nsConnectionTimeout);
        connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, nemuriConstantsModel.nsConnectionTimeout * 1000);
        nsManager.startScan(RealtimeMonitorDialog.this);
    }

    private void postDataReconnect() {
        runOnUiThread(() -> {
            Logger.d("afk isIntentionalDC showProgress isloading " + isLoading);
            showProgress();
        });


        isNemuriScanInitiated = false;
        isIntentionalDC = true;
        Logger.w("afk trueing isIntentionalDC");

        reconnectNSWaitHandler.removeCallbacks(reconnectNSWaitTimer);
        reconnectNSWaitTimer = () -> {
            if (reconnectNSRetryCount > nemuriConstantsModel.nsPostDataMaxWaitRetry) {
                Logger.d("afk nemuriScanningTimeoutTimer " + reconnectNSRetryCount + "-" + nemuriConstantsModel.nsPostDataMaxWaitRetry);
                runOnUiThread(() ->
                        DialogUtil.createCustomYesNo(RealtimeMonitorDialog.this, "", LanguageProvider.getLanguage("UI000610C022"),
                                LanguageProvider.getLanguage("UI000610C026"), (dialogInterface, i) -> finish(),
                                LanguageProvider.getLanguage("UI000610C025"), (dialogInterface, i) -> tryToConnectBLE())
                );
                runOnUiThread(this::hideProgress);
            } else {
                postDataReconnect();
            }
            Logger.d("afk showProgress nemuriScanningTimeoutTimer cb");
        };
        reconnectNSWaitHandler.postDelayed(reconnectNSWaitTimer, (long) (nemuriConstantsModel.nsPostDataMaxWaitDuration * 1000));
        nsManager.startScan(this);
        reconnectNSRetryCount += 1;
    }

    private void purgeBLE() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        reconnectNSWaitHandler.removeCallbacks(reconnectNSWaitTimer);
        if (realTimeDataFetchTimer != null) {
            realTimeDataFetchTimer.purge();
            realTimeDataFetchTimer.cancel();
        }
        if (graphRendererTimer != null) {
            graphRendererTimer.purge();
            graphRendererTimer.cancel();
        }
        if (bioRendererTimer != null) {
            bioRendererTimer.purge();
            bioRendererTimer.cancel();
        }

        isIntentionalDC = true;
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }

    private void purgeTimer() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        reconnectNSWaitHandler.removeCallbacks(reconnectNSWaitTimer);
        if (realTimeDataFetchTimer != null) {
            realTimeDataFetchTimer.purge();
            realTimeDataFetchTimer.cancel();
        }
        if (graphRendererTimer != null) {
            graphRendererTimer.purge();
            graphRendererTimer.cancel();
        }
        if (bioRendererTimer != null) {
            bioRendererTimer.purge();
            bioRendererTimer.cancel();
        }

        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }
    //MARK END: ble related functions

    //MARK : NSRealTimeDelegate Implementation
    @Override
    public void onRealTimeFeedReceived(NSRealtimeFeed data) {
        lastData = data;
    }
    //MARK END : NSRealTimeDelegate Implementation

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
                if (PermissionUtil.hasLocationPermissions(RealtimeMonitorDialog.this)) {
                    IOSDialogRight.Dismiss();
                    showLocationPermissionDialogAlert();
                    return;
                }
                if (PermissionUtil.isLocationServiceEnable(RealtimeMonitorDialog.this)) {
                    IOSDialogRight.Dismiss();
                    showLocationServiceDialogAlert();
                    return;
                }
            }
        }
    };
}
