package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ligl.android.widget.iosdialog.IOSDialog;
import com.orhanobut.logger.Logger;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSAutomaticOperationDelegate;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSOperationDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.SleepResetModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.SleepResetProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.TimerUtils;
import com.paramount.bed.util.TokenExpiredReceiver;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SleepResetActivity extends BaseActivity implements NSScanDelegate, NSConnectionDelegate, NSAutomaticOperationDelegate, NSOperationDelegate {

    @BindView(R.id.parent_container)
    RelativeLayout parentContainer;
    @BindView(R.id.tvTimer)
    TextView tvTimer;
    @BindView(R.id.btnStopTimer)
    LinearLayout btnStopTimer;
    @OnClick(R.id.btnStopTimer)
    public void onStopTap(){
        LogUserAction.sendNewLog(userService, "TERMINATE_STOP_SLEEP", "", UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
        IOSDialogRight.Dismiss();//dismiss other alerts
        DialogUtil.createCustomYesNo(this, "",LanguageProvider.getLanguage("UI000506C004"), LanguageProvider.getLanguage("UI000506C006"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LogUserAction.sendNewLog(userService, "TERMINATE_STOP_SLEEP_CANCEL", "", UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
            }
        }, LanguageProvider.getLanguage("UI000506C005"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                stopSleepReset(true);
            }
        });
    }
    //MARK : BLE Vars
    private boolean activityInitialized = false;
    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            isNotifyingStart = false;
            purgeBLE();
            showASAFailedAlert();
        }
    };
    private NSManager nsManager;
    NemuriConstantsModel nsConstants;
    NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
    //MARK END: BLE Vars

    boolean isNotifyingStop;
    boolean isNotifyingStart;
    boolean isASANotified;
    SettingModel settingModel;
    Handler operationTimeoutTimer = new Handler();
    Runnable operationTimeoutRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };

    Handler secondTimer = new Handler();
    Runnable secondTimerRunnable = new Runnable() {
        @Override
        public void run() {
            tickCountdown();
            secondTimer.postDelayed(secondTimerRunnable,1000);
        }
    };
    TokenExpiredReceiver tokenExpiredReceiver = new TokenExpiredReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        //android O fix bug orientation
        Intent intent = getIntent();
        if(intent != null){
            isASANotified = intent.getBooleanExtra("FROM_HOME",false);
        }
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        ButterKnife.bind(this);
        settingModel = SettingModel.getSetting().getUnmanaged();
        int sleepReset = settingModel.getSleep_reset_timing();
        if(sleepReset == 0){
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        }else {
            LogUserAction.sendNewLog(userService, "STOP_SLEEP_SHOW", "", UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
            tvTimer.setText(TimerUtils.formatTime(sleepReset * 60));
            nsConstants = NemuriConstantsModel.get().getUnmanaged();
            nsManager = NSManager.getInstance(this, this);
        }
        adjustEndDateByBackgroundDate();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_sleep_reset;
    }
    @Override
    protected void onResume() {
        super.onResume();

        TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        AlarmsQuizModule.run(this);

        if(!activityInitialized) {
            parentContainer.setVisibility(View.VISIBLE);
            activityInitialized = true;
            if (SleepResetProvider.getSleepReset() != null) {
                startCountdown();
            } else {
                startSleepReset();
            }
        }else if(isNotifyingStop || isNotifyingStart){
            parentContainer.setVisibility(View.VISIBLE);
            tryToConnectBLE(false);
        }else{
            if(!isASANotified){
                parentContainer.setVisibility(View.VISIBLE);
                showASAFailedAlert();
            }else{
                //calculate time diffrence and restart timer
                Date foregroundDate = new Date();
                SleepResetModel sleepResetModel = SleepResetProvider.getSleepReset();
                if(sleepResetModel != null) {
                    long lastBackgroundDateSecond = 0;
                    if(sleepResetModel.getBackgroundDate() != null){
                        lastBackgroundDateSecond = sleepResetModel.getBackgroundDate().getTime();
                    }
                    long secondDiff = ((int)((foregroundDate.getTime() - lastBackgroundDateSecond) * -1)/1000)*1000;


                    Date startDate = sleepResetModel.getStartDate();
                    Date endDate = sleepResetModel.getEndDate();
                    if(endDate == null){
                        endDate = new Date();
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(endDate);
                    cal.add(Calendar.MILLISECOND, (int) secondDiff);
                    Date newEndDate = cal.getTime();
                    if(newEndDate.getTime() <= startDate.getTime()){
                        LogUserAction.sendNewLog(userService, "TERMINATE_STOP_SLEEP", "", UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
                        runOnUiThread(() -> tvTimer.setText("00:00"));
                        stopCountdown();
                        stopSleepReset(false);

                    }else{
                        parentContainer.setVisibility(View.VISIBLE);
                        sleepResetModel.updateEndDate(newEndDate);
                        startCountdown();
                    }
                }else{
                    stopSleepReset(false);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgress();
        purgeBLE();
        System.out.println("Setting background date");
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
        purgeBLE();
        SleepResetProvider.setBackgroundDate();
        stopCountdown();
        parentContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
        purgeBLE();
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.zoom_in, 0);
    }
    @SuppressLint("CheckResult")
    private void startSleepReset(){
        showProgress();
        SleepResetProvider.startSleepReset(this, (result, isSuccess, errTag) -> {
            if(isSuccess){
                isNotifyingStart = true;
                tryToConnectBLE(false);
            }else{
                hideProgress();
                if (errTag.equalsIgnoreCase("UI000802C002")) {
                    DialogUtil.showOfflineDialog(SleepResetActivity.this, (dialogInterface, i) -> {
                        setResult(1);//arbitrary value just to notify HomeActivity
                        finish();
                    });
                } else if(!errTag.isEmpty()) {
                    DialogUtil.serverFailed(SleepResetActivity.this, LanguageProvider.getLanguage(errTag), "UI000802C177", "UI000802C003", "UI000802C177", new DialogUtil.DialogUtilListener() {
                        @Override
                        public void onDismiss() {
                            setResult(1);//arbitrary value just to notify HomeActivity
                            finish();
                        }
                    });
                }else {
                    setResult(1);//arbitrary value just to notify HomeActivity
                    finish();
                }
            }
        },0);
    }

    @SuppressLint("CheckResult")
    private void stopSleepReset(boolean shouldCheckResult){
        if(!shouldCheckResult){
            parentContainer.setVisibility(View.INVISIBLE);
        }
        showProgress();
        SleepResetProvider.stopSleepReset(this, new SleepResetProvider.StopSleepResetListener() {
            @Override
            public void onFinish(BaseResponse result, boolean isSuccess, String errTag) {
                if(isSuccess || !shouldCheckResult){
                    stopCountdown();
                    LogUserAction.sendNewLog(userService, "STOP_SLEEP_END", "", UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
                    isNotifyingStop = true;
                    SleepResetProvider.deleteSleepReset();
                    tryToConnectBLE(false);

                }else {
                    hideProgress();
                    LogUserAction.sendNewLog(userService, "TERMINATE_STOP_SLEEP_FAILED", "", UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
                    if (errTag.equalsIgnoreCase("UI000802C002")) {
                        DialogUtil.offlineDialog(SleepResetActivity.this,SleepResetActivity.this );
                    } else if(!errTag.isEmpty()) {
                        DialogUtil.serverFailed(SleepResetActivity.this, LanguageProvider.getLanguage(errTag), "UI000802C177", "UI000802C003", "UI000802C177");
                    }
                }
            }
        },0);
    }
    private void showASAFailedAlert(){
        isNotifyingStart = false;
        if (isNotifyingStop) {
            hideProgress();
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        }else{
            hideProgress();
            isASANotified = true;
            operationTimeoutTimer.removeCallbacks(operationTimeoutRunnable);
            initCountdown();
        }
    }
    private void adjustEndDateByBackgroundDate(){
        SleepResetModel sleepResetModel = SleepResetProvider.getSleepReset();
        if(sleepResetModel != null) {
            Date endDate = sleepResetModel.getEndDate();
            if (endDate == null) {
                endDate = new Date();
            }

            Date dateBackground = sleepResetModel.getBackgroundDate();
            if (dateBackground != null) {
                //account for backgrounded/killed time
                long lastBackgroundDateSecond = sleepResetModel.getBackgroundDate().getTime();
                long secondDiff = (new Date().getTime() - lastBackgroundDateSecond) * -1;

                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.MILLISECOND, (int) secondDiff);
                Date newEndDate = cal.getTime();
                sleepResetModel.updateEndDate(newEndDate);
                sleepResetModel.updateBackgroundDate(null);
            }
        }
    }
    @SuppressLint({"SetTextI18n","DefaultLocale"})
    private void tickCountdown(){
        SleepResetModel sleepResetModel = SleepResetProvider.getSleepReset();
        if(sleepResetModel != null) {
            Date startDate = sleepResetModel.getStartDate();
            if(startDate == null){
                startDate = new Date();
            }
            Date endDate = sleepResetModel.getEndDate();
            if(endDate == null){
                endDate = new Date();
            }

            long secondDiff = (endDate.getTime() - startDate.getTime()) ;
            if(secondDiff <= 0){
                runOnUiThread(() -> tvTimer.setText("00:00"));
                stopCountdown();
                stopSleepReset(false);
            }else{
                String mmSS = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(secondDiff),
                        TimeUnit.MILLISECONDS.toSeconds(secondDiff) % TimeUnit.MINUTES.toSeconds(1));
                runOnUiThread(() -> tvTimer.setText(mmSS));

                Calendar cal = Calendar.getInstance();
                cal.setTime(endDate);
                cal.add(Calendar.SECOND, -1);
                sleepResetModel.updateEndDate(cal.getTime());
            }
            System.out.println("Setting background date update tick  "+tvTimer.getText().toString());
        }
    }

    private void stopCountdown(){
        secondTimer.removeCallbacks(secondTimerRunnable);
    }

    private void startCountdown(){
        secondTimer.removeCallbacks(secondTimerRunnable);
        if(settingModel.getSleep_reset_timing() != 0){
            tickCountdown();
            secondTimer.postDelayed(secondTimerRunnable,1000);
        }
    }

    private void initCountdown(){
        Date currentDate = new Date();
        int sleepReset = settingModel.getSleep_reset_timing();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.add(Calendar.MINUTE, sleepReset);
        Date endDate = cal.getTime();


        SleepResetModel sleepResetModel = SleepResetProvider.getSleepReset();
        if(sleepResetModel == null || sleepResetModel.isValid()){
            sleepResetModel = SleepResetModel.create();
            sleepResetModel.updateStartDate(currentDate);
            sleepResetModel.updateEndDate(endDate);
            sleepResetModel.insert();
        }else{
            sleepResetModel.updateStartDate(currentDate);
            sleepResetModel.updateEndDate(endDate);
            sleepResetModel.updateBackgroundDate(null);
        }

        startCountdown();
    }
    //MARK: ble methods
    private void tryToConnectBLE(boolean shouldShowLoading) {
        if (nemuriScanModel == null) {
            showASAFailedAlert();
            return;
        }
        if(shouldShowLoading) {
            runOnUiThread(this::showProgress);
        }
        //setup connection timeout
        int connectionTimeout = nsConstants.nsConnectionTimeout;

        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, connectionTimeout * 1000);

        boolean scanResult = nsManager.startSilentScan(this);
        if (!scanResult) {
            showASAFailedAlert();
        }
    }

    private void purgeBLE() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
        if(isNotifyingStop) {
            isNotifyingStop = false;
            hideProgress();
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        }
        if(isNotifyingStart && !isASANotified) {
            isNotifyingStart = false;
            runOnUiThread(this::showASAFailedAlert);
        }
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
            DialogUtil.createCustomYesNo(SleepResetActivity.this,
                    "",
                    LanguageProvider.getLanguage("UI000802C191"),
                    LanguageProvider.getLanguage("UI000802C193"),
                    (dialogInterface, i) -> {
                        SleepResetActivity.this.runOnUiThread(this::showASAFailedAlert);
                    },
                    LanguageProvider.getLanguage("UI000802C192"),
                    (dialogInterface, i) -> {
                        //retry
                        tryToConnectBLE(true);
                    });
        }
    }
    //MARK END : NSBaseDelegate

    //MARK : NSConnectionDelegate Implementation
    @Override
    public void onConnectionEstablished() {
        isNotifyingStart = false;
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        runOnUiThread(() -> {
            nsManager.requestAuthentication(nemuriScanModel.getServerGeneratedId());
        });

    }

    @Override
    public void onDisconnect() {
        isNotifyingStart = false;
        if(!isASANotified){
            runOnUiThread(this::showASAFailedAlert);
        }else if(isNotifyingStop) {
            hideProgress();
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        }
    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {
    }

    @Override
    public void onAuthenticationFinished(int result) {
        runOnUiThread(() -> {
            if (result == NSConstants.NS_AUTH_SUCCESS || result == NSConstants.NS_AUTH_REG_SUCCESS) {
                LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_SUCCESS", "1", "", "UI000506");
                nsManager.notifyAutomaticOperationChange();
            } else {
                //LOG HERE NS_SET_SERVERID_FAILED
                LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_FAILED", "1", "", "UI000506");
                runOnUiThread(this::showASAFailedAlert);
            }
        });
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
        runOnUiThread(() -> showASAFailedAlert());
    }

    @Override
    public void onLocationServiceDisabled() {
        runOnUiThread(() -> showASAFailedAlert());
    }

    public void showLocationPermissionDialogAlert() {
        hideProgress();
        purgeBLE();
        if (NemuriScanModel.get() != null) {
            PermissionUtil.showLocationPermissionDialogAlert(this, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    hideProgress();
                    purgeBLE();
                    showASAFailedAlert();
                }

                @Override
                public void onPermissionGranted() {
                    tryToConnectBLE(true);
                }
            });
        }
    }

    public void showLocationServiceDialogAlert() {
        hideProgress();
        purgeBLE();
        if (NemuriScanModel.get() != null) {
            PermissionUtil.showLocationServiceDialogAlert(this, new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                    hideProgress();
                    purgeBLE();
                    showASAFailedAlert();
                }

                @Override
                public void onEnabled() {
                    tryToConnectBLE(true);
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
        runOnUiThread(() -> {
            if (nemuriScanModel != null) {
                String savedMac = nemuriScanModel.getMacAddress();
                String targetMac = scanResult.getDevice().getAddress();
                if (scanResult.getDevice().getName() != null) {
                    Logger.v("TimerActivity : Scanning BLE, looking for " + savedMac + " trying " + targetMac + " " + scanResult.getDevice().getName());
                }
                if (savedMac.equalsIgnoreCase(targetMac)) {
                    nsManager.connectToDevice(scanResult.getDevice(), this);
                    nsManager.stopScan();
                }
            } else {
                runOnUiThread(this::showASAFailedAlert);
            }
        });
    }
    //MARK END: NSScanDelegate Implementation

    //MARK : NSAutomaticOperationDelegate Implementation
    @Override
    public void onNotifyAutomaticOperationFinished() {
        hideProgress();
        if(isNotifyingStop){
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        }else{
            isASANotified = true;
            operationTimeoutTimer.removeCallbacks(operationTimeoutRunnable);
            initCountdown();
        }
        //init timer
    }
    //MARK END: NSAutomaticOperationDelegate Implementation

    //MARK : NSOperationDelegate Implementation
    @Override
    public void onOperationTimeout() {
        runOnUiThread(this::showASAFailedAlert);
    }
    //MARK END: NSOperationDelegate Implementation
    //MARK END ble methods

    interface SleepStopObserver{
        void onSleepStopFinished(boolean isSuccess);
    }

    @Override
    public void onBackPressed() {
        //override to prevent back
    }
}