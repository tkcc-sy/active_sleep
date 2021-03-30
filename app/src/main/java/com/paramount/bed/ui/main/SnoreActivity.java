package com.paramount.bed.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StatFs;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.orhanobut.logger.Logger;
import com.paramount.bed.R;
import com.paramount.bed.data.model.AlarmStopModel;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.IsForegroundModel;
import com.paramount.bed.data.model.PendingSnoringModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.SnoringProvider;
import com.paramount.bed.nativewrapper.SnoreDetectiveLibrary;
import com.paramount.bed.recorder.RecordingService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.ActivityUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.SnoreFileUtil;
import com.paramount.bed.util.alarms.AlarmsPopup;
import com.paramount.bed.util.alarms.AlarmsQuizModule;
import com.paramount.bed.util.alarms.AlarmsScheduler;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SnoreActivity extends BaseActivity implements SnoreRecordFragment.SnoreRecordListener{

    private static final int REQUEST_CODE_PERMISSION = 200;
    private static final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};
    private boolean isGranted = false;


    @BindView(R.id.containerButton)
    ConstraintLayout containerButton;

    @BindView(R.id.snoreFragment)
    FrameLayout fragmentContainer;

    @BindView(R.id.btnStartSnore)
    LinearLayout btnStartSnore;

    @BindView(R.id.btnSetting)
    Button btnSetting;

    @BindView(R.id.btnHelp)
    Button btnHelp;

    @BindView(R.id.btnCloseSnore)
    Button btnCloseSnore;

    @BindView(R.id.btnChartCloseSnore)
    Button btnChartCloseSnore;

    @BindView(R.id.overlay)
    View overlay;

    SnoreManualFragment snoreManualFragment;
    SnoreRecordFragment snoreRecordFragment;
    boolean timerDelayRunning = false;
    public static boolean isRecording, isAnalyzing;
    boolean isInBackground, isDeferringAction, isAnalysisCancelled, isRunningOutOfDisk, isInterrupted, isRecordingCancelledByAlarmBG, isDelayingRecording, shouldResumeAnalyzing;
    DeferredAction deferredAction;

    private IntentFilter  intentFilterShutDown, intentFilterReboot;

    SnoreDetectiveLibrary snoreDetectiveLibrary = new SnoreDetectiveLibrary();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        isRecording = false;
        isAnalyzing = false;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        System.loadLibrary("native-lib");

        ButterKnife.bind(this);
        overridePendingTransition(R.anim.goup, R.anim.explode);
        applyLocalization();

        snoreManualFragment = new SnoreManualFragment();
        snoreRecordFragment = new SnoreRecordFragment();

        initFragment();

        intentFilterShutDown = new IntentFilter(Intent.ACTION_SHUTDOWN);
        intentFilterReboot = new IntentFilter(Intent.ACTION_REBOOT);

        LogUserAction.sendNewLog(userService,"SNORING_RECORDER_SHOW","","","");

        //uncomment for testing
//        initializeAnalyzerFolders(new TempDirectoryCallback() {
//            @Override
//            public void creationFinished(boolean isSuccess) {
//                analyze(new AnalyzeCallback() {
//                    @Override
//                    public void analyzeFinished(boolean isSuccess, String result) {
//
//                    }
//                });
//
//            }
//        });
//        copyAnalysisResultFileTest(new String[]{"AudioSnore05.wav","AudioSnore06.wav","AudioSnore07.wav","AudioSnore08.wav"}, new String[]{"20201220050404","20201220050405","20201220050406","20201220050407"}, new CopyAnalysisCallback() {
//            @Override
//            public void copyFinished(boolean isSuccess) {
//
//            }
//        },0);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_snore;
    }

    private void initFragment(){
        setFragment(snoreManualFragment);
    }

    @OnClick(R.id.btnStartSnore)
    void startButtonSelected(){
        LogUserAction.sendNewLog(userService,"SNORING_RECORDING","","","");
        if(isGranted){
            validateRecordingCondition();
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION);
        }
    }

    @OnClick(R.id.btnSetting)
    void settingSnore(){
        startActivity(new Intent(this, SnoreSettingActivity.class));
    }

    @OnClick(R.id.btnHelp)
    void faq(){
        Intent faqIntent = new Intent(this, FaqActivity.class);
        faqIntent.putExtra("ID_FAQ", "UI000560C029");
        faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(faqIntent);
    }

    @OnClick(R.id.btnCloseSnore)
    void closeSnore() {
        DialogUtil.createCustomYesNo(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C023"), LanguageProvider.getLanguage("UI000560C025"), (dialogInterface, i) -> dialogInterface.dismiss(),
                LanguageProvider.getLanguage("UI000560C024"), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    SnoreRecordFragment.START_RECORD = false;
                    HomeActivity.REMOTEACTIVE = false;
                    stopRecord();
                    finish();
                });
    }

    @OnClick(R.id.btnChartCloseSnore)
    void chartCloseSnore(){
        finish();
    }

    private void setFragment(Fragment fragment){
        ActivityUtil.navigationFragmentAllowStateLoss(this, R.id.snoreFragment, fragment);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!checkAllGrant(grantResults)) {
            LogUserAction.sendNewLog(userService,"SNORING_RECORDING_FAILED","PERMISSION_FAILED","","");
            DialogUtil.createSimpleOkDialog(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C026"),
                    LanguageProvider.getLanguage("UI000560C027"), (dialogInterface, i) -> dialogInterface.dismiss());
            enabledUI();
        }else{
            isGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            validateRecordingCondition();
        }
    }

    private boolean checkAllGrant(int[] grantResults){
        boolean grant = true;
        if(grantResults.length>0){
            for (int grantResult:grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    grant = false;
                    break;
                }
            }
        }else {
            grant = false;
        }

        return grant;
    }

    @Override
    protected int getStatusBarTheme() {
        return STATUS_BAR_SNORE;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.explode, R.anim.godown);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Logger.d("SNORE : ON PAUSE");
        Logger.d("SNORE : TIMER DELAY: " + timerDelayRunning);
        isInBackground = true;
        if(isAnalyzing && !isAlarmActive() && !AlarmsPopup.isAboutToTriggerQuestionnaire){
            isAnalysisCancelled = true;
            LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_STOP","BACKGROUND","","UI000560");

            long cancelResult =  snoreDetectiveLibrary.SDL_SnoreAnalysisCancel();
            if (cancelResult != 0) {
                LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_FAILED","BACKGROUND_CANCEL_FAILED","","UI000560");
            }
        }else if(isRecording && (SnoreFileUtil.getRecordingSize(getApplicationContext()) <= 0 || isDelayingRecording)){
            //still on delay phase
            hideLoading();
            stopRecord();
            refreshUIState();
            isDelayingRecording = false;
            isAnalysisCancelled = false;
            deferredAction = () -> DialogUtil.createSimpleOkDialog(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C067"), LanguageProvider.getLanguage("UI000560C068"), (dialogInterface, i) -> { dialogInterface.dismiss();});
            if(!isInBackground) {
                deferredAction.execute();
                deferredAction = null;
            }else{
                isDeferringAction = true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("SNORE : ON RESUME");
        isInBackground = false;
        LocalBroadcastManager.getInstance(this).registerReceiver(errorReceiver, new IntentFilter("snore-activity"));

        Logger.d("SNORE : ON RESUME " + isAnalyzing + " " + isAnalysisCancelled);
        if(isAnalysisCancelled){
            snoreRecordFragment.hideSubLoading();
            isAnalyzing = false;
            runOnUiThread(() -> getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON));
            DialogUtil.createYesNoDialogLink(this, "", LanguageProvider.getLanguage("UI000560C052"), LanguageProvider.getLanguage("UI000560C055")
                    , (dialogInterface, i) -> {
                        openFAQ("UI000560C055");
                    }, LanguageProvider.getLanguage("UI000560C053"), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        isAnalysisCancelled = false;
                        stop();
                    }, LanguageProvider.getLanguage("UI000560C054"), (dialogInterface, i) -> {
                        isRecording = false;
                        refreshUIState();
                        isAnalysisCancelled = false;
                        dialogInterface.dismiss();
                    });
        }else if(isDeferringAction){
            isDeferringAction = false;
            if(deferredAction != null){
                deferredAction.execute();
                deferredAction = null;
            }
        }else if(shouldResumeAnalyzing){
            LogUserAction.sendNewLog(userService,"SNORING_RECORDING_ANALYZE_BACKGROUND_START","","","");
            shouldResumeAnalyzing = false;
            stop();
        }
        //prevent ui disabled after went to faq
        enabledUI();
        AlarmsQuizModule.run(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(isRecording) {
            IsForegroundModel.clear();
            IsForegroundModel fm = new IsForegroundModel();
            fm.setStatus(false);
            fm.insert();
        }
        Logger.d("SNORE : ON STOP");
    }

    private void stopRecord(){
        stopRecordUI();
        DelayTimer.getInstance().stop();
        if(isRecording){
            isRecording = false;
            Intent intentRecordService = new Intent(this, RecordingService.class);
            stopService(intentRecordService);
            LogUserAction.sendNewLog(userService,"SNORING_RECORDING_STOP","","","UI000560");
        }
    }


    private void disabledUI(){
        btnStartSnore.setEnabled(false);
        btnCloseSnore.setEnabled(false);
        btnChartCloseSnore.setEnabled(false);
    }

    private void enabledUI(){
        btnStartSnore.setEnabled(true);
        btnCloseSnore.setEnabled(true);
        btnChartCloseSnore.setEnabled(true);
    }

    private void startRecordUI(){
        btnChartCloseSnore.setVisibility(View.INVISIBLE);
        btnCloseSnore.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        btnHelp.setEnabled(false);
        btnHelp.setAlpha(0.5f);
        btnSetting.setEnabled(false);
    }

    private void stopRecordUI(){
        btnCloseSnore.setVisibility(View.INVISIBLE);
        btnChartCloseSnore.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.INVISIBLE);
        btnHelp.setEnabled(true);
        btnHelp.setAlpha(1f);
        btnSetting.setEnabled(true);
    }

    @Override
    public void onStopRecord() {
        LogUserAction.sendNewLog(userService,"SNORING_RECORDING_STOP","","","UI000560");
        stop();
    }

    @Override
    public void onCallback(boolean startRecord) {
    }

    @Override
    public void onStartFlip() {
        disabledUI();
    }

    @Override
    public void onStopFlip() {
        enabledUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IOSDialogRight.Dismiss();
        try {
            //Prevent crash if errorReceiver not registered
            LocalBroadcastManager.getInstance(this).unregisterReceiver(errorReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverDeviceOff);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverDeviceReboot);
            this.unregisterReceiver(receiverDeviceOff);
            this.unregisterReceiver(receiverDeviceReboot);
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        DelayTimer.getInstance().stop();
        if(isRecording){
            Intent intentRecordService = new Intent(this, RecordingService.class);
            stopService(intentRecordService);
        }
    }

    public interface TimerCallback{
        void timerFinished();
        void timerTick();
    }

    public interface AnalyzeCallback{
        void analyzeFinished(boolean isSuccess, String result);
    }


    public interface CopyAnalysisCallback{
        void copyFinished(boolean isSuccess);
    }

    public interface TempDirectoryCallback{
        void creationFinished(boolean isSuccess);
    }

    public interface DeferredAction{
        void execute();
    }


    private BroadcastReceiver receiverDeviceOff = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d("SNORE : DEVICE DETECT OFF");
            stopRecordingService();
        }
    };

    private BroadcastReceiver receiverDeviceReboot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.d("SNORE : DEVICE DETECT REBOOT");
            stopRecordingService();
        }
    };

    private BroadcastReceiver errorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            boolean isError = intent.getBooleanExtra("isError",false);

            if(isError){
                DelayTimer.getInstance().stop();
                deferredAction = () -> {
                    LogUserAction.sendNewLog(userService,"SNORING_RECORDING_FAILED","OS_RECORDING_FAILED","","");
                    DialogUtil.createYesNoDialogLink(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C031"), LanguageProvider.getLanguage("UI000560C034"), (dialogInterface, i) -> {
                        openFAQ("UI000560C034");
                    }, LanguageProvider.getLanguage("UI000560C032"), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        startAudioRecording();
                    }, LanguageProvider.getLanguage("UI000560C033"), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        isRecording = false;
                        refreshUIState();
                    });
                };

                if(isInBackground){
                    isDeferringAction = true;
                }else{
                    deferredAction.execute();
                    deferredAction = null;
                }
            }
        }
    };

    private void stopRecordingService(){
        SnoreRecordFragment.START_RECORD = false;
        Intent intentRecordService = new Intent(SnoreActivity.this, RecordingService.class);
        stopService(intentRecordService);
        isRecording = false;
        LogUserAction.sendNewLog(userService,"SNORING_RECORDING_STOP","","","");

        this.unregisterReceiver(receiverDeviceOff);
        this.unregisterReceiver(receiverDeviceReboot);
    }

    private void validateRecordingCondition(){
        if(!isRecording){
            if(isTelephonyBusy()){
                LogUserAction.sendNewLog(userService,"SNORING_RECORDING_FAILED","OS_RECORDING_FAILED","","");
                DialogUtil.createYesNoDialogLink(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C031"), LanguageProvider.getLanguage("UI000560C034"), (dialogInterface, i) -> {
                    openFAQ("UI000560C034");
                }, LanguageProvider.getLanguage("UI000560C032"), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    new Handler().postDelayed(() -> runOnUiThread(this::validateRecordingCondition),250);
                }, LanguageProvider.getLanguage("UI000560C033"), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
            }else {
                if (isMusicBusy()) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    int result = am.requestAudioFocus(i -> {},
                            AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        Logger.d("SNORE : Audio focus received");
                    } else {
                        Logger.d("SNORE : Audio focus NOT received");
                        LogUserAction.sendNewLog(userService,"SNORING_RECORDING_FAILED","OS_RECORDING_FAILED_AUDIO_FOCUS","","");
                        DialogUtil.createYesNoDialogLink(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C031"), LanguageProvider.getLanguage("UI000560C034"), (dialogInterface, i) -> {
                            openFAQ("UI000560C034");
                        }, LanguageProvider.getLanguage("UI000560C032"), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            new Handler().postDelayed(() -> runOnUiThread(this::validateRecordingCondition),250);
                        }, LanguageProvider.getLanguage("UI000560C033"), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });
                        return;
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    if(activityManager.isBackgroundRestricted()){
                        DialogUtil.createSimpleOkDialogLink(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C069"), LanguageProvider.getLanguage("UI000560C071"), (dialogInterface, i) -> {
                            openFAQ("UI000560C071");
                            dialogInterface.dismiss();
                            isRecording = false;
                            refreshUIState();
                        }, LanguageProvider.getLanguage("UI000560C070"), (dialogInterface, i) -> {;
                            dialogInterface.dismiss();
                            isRecording = false;
                            refreshUIState();
                        });
                        return;
                    }
                }


                FormPolicyModel policyModel = FormPolicyModel.getPolicy();

                StatFs stat = new StatFs(SnoreFileUtil.getPath(getApplicationContext()));
                long bytesAvailable = stat.getAvailableBytes();
                long freeSpace = (bytesAvailable / 1024 / 1024);
                freeSpace += policyModel.getSnoringMinDiskSpaceMargin();

                double minimumFreeSpace = policyModel.getSnoringMinDiskSpace();

                if (freeSpace > minimumFreeSpace) {
                    isRunningOutOfDisk = false;
                    isInterrupted = false;
                    isRecordingCancelledByAlarmBG = false;
                    start();
                } else {
                    LogUserAction.sendNewLog(userService, "SNORING_RECORDING_FAILED", "INSUFFICIENT_SPACE", "", "UI000504");
                    String message = LanguageProvider.getLanguage("UI000560C017").replace("%MIN_STORAGE_MB%", String.valueOf((int) minimumFreeSpace));
                    DialogUtil.createSimpleOkDialogLink(SnoreActivity.this, "", message,
                            LanguageProvider.getLanguage("UI000560C019"), (dialogInterface, i) -> {
                                openFAQ("UI000560C019");
                                dialogInterface.dismiss();
                            }, LanguageProvider.getLanguage("UI000560C018"), (dialogInterface, i) -> dialogInterface.dismiss());
                }
            }
        }
    }

    private void start(){
        isRecording = true;
        refreshUIState();
        startAudioRecording();
    }

    private void startAudioRecording(){
        FormPolicyModel policyModel = FormPolicyModel.getPolicy();
        double maxRecordTime = policyModel.getSnoringMaxRecordTime();
        double delayTime = (policyModel.getSnoringRecordingDelay() * 60) - 2;
        shouldResumeAnalyzing = false;
        initializeAnalyzerFolders(isSuccess -> {
            if(isSuccess){
                Logger.d("SNORE :  START DELAY");
                showLoading();
                isDelayingRecording = true;
                DelayTimer.getInstance().startTimerDelay(delayTime, new TimerCallback() {
                    @Override
                    public void timerFinished() {
                        Logger.d("SNORE :  END DELAY");
                        new Handler().postDelayed(() -> runOnUiThread(() -> {
                            isDelayingRecording = false;
                            hideLoading();
                        }),2*1000);
                        Intent intentRecordService = new Intent(SnoreActivity.this, RecordingService.class);

                        intentRecordService.putExtra("EXTRA_TEMP_PATH", SnoreFileUtil.getAnalysisAudioInputPath(getApplicationContext()));

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intentRecordService);
                        } else {
                            startService(intentRecordService);
                        }

                        registerReceiver(receiverDeviceOff, intentFilterShutDown);
                        registerReceiver(receiverDeviceReboot, intentFilterReboot);

                        DelayTimer.getInstance().startTimerDelay(maxRecordTime * 60, new TimerCallback() {
                            @Override
                            public void timerFinished() {
                                stop();
                            }

                            @Override
                            public void timerTick() {
                                FormPolicyModel policyModel = FormPolicyModel.getPolicy();

                                StatFs stat = new StatFs(SnoreFileUtil.getPath(getApplicationContext()));
                                long bytesAvailable = stat.getAvailableBlocksLong()*stat.getBlockSizeLong();
                                long freeSpace   = bytesAvailable / 1024 / 1024;
                                freeSpace += policyModel.getSnoringMinDiskSpaceMargin();

                                double minimumFreeSpace = policyModel.getSnoringMinDiskSpaceOnRecord();

                                Logger.d("SNORE : Disk usage watchdog : Recording free space "+freeSpace+" minimum "+minimumFreeSpace+ " is alarm active "+isAlarmActive());
                                if(freeSpace < minimumFreeSpace){
                                    Logger.d("SNORE : Disk usage watchdog : Recording exceeding minimum");
                                    isRunningOutOfDisk = true;
                                    stop();
                                    return;
                                }else{
                                    isRunningOutOfDisk = false;
                                    Logger.d("SNORE : Disk usage watchdog : Recording still going");
                                }

                                if(isAudioBusy()) {
                                    Logger.d("SNORE : Recording Interrupted");
                                    isInterrupted = true;
                                    stop();
                                }else{
                                    isInterrupted = false;
                                    if(isAlarmActive()){
                                        Logger.d("SNORE : Recording Interrupted by alarm");
                                        isRecordingCancelledByAlarmBG = true;
                                        stop();
                                    }else{
                                        isRecordingCancelledByAlarmBG = false;
                                    }
                                    Logger.d("SNORE : Recording not interrupted");
                                }

                            }
                        }, "Record Limit");
                    }

                    @Override
                    public void timerTick() {

                    }
                },"Record Delay");
            }else{
                LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_FAILED","FOLDER_MISSING","","");
                DialogUtil.createYesNoDialogLink(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C039"), LanguageProvider.getLanguage("UI000560C042"), (dialogInterface, i) -> {
                    openFAQ("UI000560C041");
                }, LanguageProvider.getLanguage("UI000560C040"), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    new Handler().postDelayed(() -> runOnUiThread(this::startAudioRecording),500);
                }, LanguageProvider.getLanguage("UI000560C041"), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    isRecording = false;
                    refreshUIState();
                });
            }
        });

    }

    private void showGenericAnalysisError(String msgTag, String okTag, String cancelTag, String faqTag, AnalyzeCallback callback){
        isAnalyzing = false;
        runOnUiThread(() -> getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON));
        deferredAction = new DeferredAction() {
            @Override
            public void execute() {
                runOnUiThread(() -> DialogUtil.createYesNoDialogLink(SnoreActivity.this, "", LanguageProvider.getLanguage(msgTag), LanguageProvider.getLanguage(faqTag), (dialogInterface, i) -> {
                    openFAQ(faqTag);
                }, LanguageProvider.getLanguage(okTag), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Runnable analysisRunnable = () -> analyze(callback);
                    AsyncTask.execute(analysisRunnable);
                }, LanguageProvider.getLanguage(cancelTag), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    callback.analyzeFinished(false,"");
                }));
            }
        };
        if(isInBackground){
            isDeferringAction = true;
        }else{
            deferredAction.execute();
            deferredAction = null;
        }
    }

    private void showGenericAnalysisErrorOkOnly(String msgTag, String okTag, AnalyzeCallback callback){
        isAnalyzing = false;
        runOnUiThread(() -> getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON));
        deferredAction = new DeferredAction() {
            @Override
            public void execute() {
                runOnUiThread(() -> DialogUtil.createSimpleOkDialog(SnoreActivity.this, "", LanguageProvider.getLanguage(msgTag), LanguageProvider.getLanguage(okTag), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        callback.analyzeFinished(false,"");
                    }
                }));
            }
        };
        if(isInBackground){
            isDeferringAction = true;
        }else{
            deferredAction.execute();
            deferredAction = null;
        }
    }

    private void stop(){
        shouldResumeAnalyzing = false;
        if(SnoreFileUtil.getRecordingSize(getApplicationContext()) <= 0){
            LogUserAction.sendNewLog(userService,"SNORING_RECORDING_CANCELLED_DELAY_PHASE","","","");
            isRecording = false;
            refreshUIState();
            return;
        }
        LogUserAction.sendNewLog(userService,"SNORING_RECORDING_FINISH","","","");
        Logger.d("SNORE : STOPPING REOCORDING");
        DelayTimer.getInstance().stop();
        Intent intentRecordService = new Intent(this, RecordingService.class);
        stopService(intentRecordService);

        if(isRunningOutOfDisk){
            isRunningOutOfDisk = false;
            LogUserAction.sendNewLog(userService,"SNORING_RECORDING_DISK_FULL","","","");

            deferredAction = () -> DialogUtil.createCustomYesNo(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C060"), LanguageProvider.getLanguage("UI000560C062"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    LogUserAction.sendNewLog(userService,"SNORING_RECORDING_DISK_FULL_CANCELLED","","","");
                    cleanAnalyzerTempFolders(null);
                    isRecording = false;
                    refreshUIState();

                }
            }, LanguageProvider.getLanguage("UI000560C061"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    LogUserAction.sendNewLog(userService,"SNORING_RECORDING_DISK_FULL_ANALYZING","","","");
                    stop();
                }
            });

            if(!isInBackground) {
                deferredAction.execute();
                deferredAction = null;
            }else{
                isDeferringAction = true;
            }
            return;
        }else if(isInterrupted || isRecordingCancelledByAlarmBG){
            LogUserAction.sendNewLog(userService,"SNORING_RECORDING_INTERRUPTED","interruption "+isInterrupted+", Alarm "+isRecordingCancelledByAlarmBG,"","");
            isInterrupted = false;
            isRecordingCancelledByAlarmBG = false;
            Logger.d("SNORE :  INTERRUPTED");

            deferredAction = () -> {
                LogUserAction.sendNewLog(userService,"SNORING_RECORDING_INTERRUPTED","deferred action executed","","");
                DialogUtil.createCustomYesNo(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C057"), LanguageProvider.getLanguage("UI000560C059"), (dialogInterface, i) -> {
                    LogUserAction.sendNewLog(userService,"SNORING_RECORDING_INTERRUPTED_CANCELLED","","","");
                    cleanAnalyzerTempFolders(null);
                    isRecording = false;
                    refreshUIState();
                    Logger.d("SNORE :  INTERRUPTED CANCEL");
                }, LanguageProvider.getLanguage("UI000560C058"), (dialogInterface, i) -> {
                    LogUserAction.sendNewLog(userService,"SNORING_RECORDING_INTERRUPTED_ANALYZING","","","");
                    stop();
                    Logger.d("SNORE :  INTERRUPTED OK");
                });
            };

            if(!isInBackground) {
                LogUserAction.sendNewLog(userService,"SNORING_RECORDING_INTERRUPTED","deferred action executed directly","","");
                deferredAction.execute();
                deferredAction = null;
            }else{
                LogUserAction.sendNewLog(userService,"SNORING_RECORDING_INTERRUPTED","deferred action queued","","");
                isDeferringAction = true;
            }
            return;
        }else if(isInBackground){
            shouldResumeAnalyzing = true;
            return;
        }
        runOnUiThread(() ->{
            snoreRecordFragment.showSubLoading();
        });
        isAnalyzing = true;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Runnable analysisRunnable = () -> analyze((isSuccess, result) -> {
            cleanAnalyzerTempFolders(null);
            if(isSuccess && result != null){
                SnoringProvider.sendSnoringAnalysis(0, result, (isAPISendSuccess, message) -> {
                    snoreRecordFragment.hideSubLoading();
                    isRecording = false;
                    refreshUIState();
                    if(!isAPISendSuccess) {

                        LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_SEND_FAILED","","","");
                        PendingSnoringModel pendingSnoringModel = new PendingSnoringModel();
                        pendingSnoringModel.setEpoch(new DateTime().getMillis());
                        pendingSnoringModel.setSnoringResult(result);
                        pendingSnoringModel.setSent(false);
                        pendingSnoringModel.insert();
                    }
                    deferredAction = () -> DialogUtil.createSimpleOkDialog(SnoreActivity.this, "", LanguageProvider.getLanguage("UI000560C056"), LanguageProvider.getLanguage("UI000560C051"), (dialogInterface, i) -> { });
                    if(!isInBackground) {
                        deferredAction.execute();
                        deferredAction = null;
                    }else{
                        isDeferringAction = true;
                    }
                });
            }else{
                snoreRecordFragment.hideSubLoading();
                isRecording = false;
                refreshUIState();
            }
        });
        new Handler().postDelayed(() -> AsyncTask.execute(analysisRunnable), 500);
//        cleanAnalyzerTempFolders(new TempDirectoryCallback() {
//            @Override
//            public void creationFinished(boolean isSuccess) {
//                new Handler().postDelayed(() -> AsyncTask.execute(analysisRunnable), 500);
//
//            }
//        });


    }

    private void initializeAnalyzerFolders(TempDirectoryCallback callback){
        if(callback == null){
            callback = isSuccess -> {};
        }

        String extStoragePath = SnoreFileUtil.getAnalysisMainFolderPath(getApplicationContext());
        String tempPath = SnoreFileUtil.getAnalysisTempFolderPath(getApplicationContext());
        String highlightPath = SnoreFileUtil.getAnalysisResultFolderPath(getApplicationContext());

        try {
            //prepare temp path
            File tempDir = new File(tempPath);
            if (tempDir.exists() && tempDir.isDirectory() && tempDir.listFiles() != null) {
                //delete all files
                for (File tempDirContent: Objects.requireNonNull(tempDir.listFiles())
                ) {
                    boolean deleteOp = tempDirContent.delete();
                    if(!deleteOp){
                        callback.creationFinished(false);
                        return;
                    }
                }
                boolean deleteOp = tempDir.delete();
                if(!deleteOp){
                    callback.creationFinished(false);
                    return;
                }
            }

            boolean mkdirOp = tempDir.mkdirs();
            if(!mkdirOp){
                callback.creationFinished(false);
                return;
            }

            //prepare highlight path
            File highlightDir = new File(highlightPath);
            if (!highlightDir.exists()) {
                mkdirOp = highlightDir.mkdirs();
                if(!mkdirOp){
                    callback.creationFinished(false);
                    return;
                }
            }

            //prepare recording file
            OutputStream fOut = null;
            File file = new File(extStoragePath, SnoreFileUtil.SNORE_ANALYSIS_FILE_NAME);
            if (file.exists()) {
                boolean deleteOp = file.delete();
                if(!deleteOp){
                    callback.creationFinished(false);
                    return;
                }
            }
            boolean createOp = file.createNewFile();
            if(!createOp){
                callback.creationFinished(false);
                return;
            }
            fOut = new FileOutputStream(file);
            fOut.flush();
            fOut.close();
            callback.creationFinished(true);
        } catch (Exception e) {
            callback.creationFinished(false);
        }
    }

    private void cleanAnalyzerTempFolders(TempDirectoryCallback callback){
        if(callback == null){
            callback = isSuccess -> {};
        }

        String extStoragePath = SnoreFileUtil.getAnalysisMainFolderPath(getApplicationContext());
        String tempPath = SnoreFileUtil.getAnalysisTempFolderPath(getApplicationContext());

        try {
            //prepare temp path
            File tempDir = new File(tempPath);
            if (tempDir.exists() && tempDir.isDirectory() && tempDir.listFiles() != null) {
                //delete all files
                for (File tempDirContent: Objects.requireNonNull(tempDir.listFiles())
                ) {
                    boolean deleteOp = tempDirContent.delete();
                    if(!deleteOp){
                        callback.creationFinished(false);
                        return;
                    }
                }
                boolean deleteOp = tempDir.delete();
                if(!deleteOp){
                    callback.creationFinished(false);
                    return;
                }
            }
            File file = new File(extStoragePath, SnoreFileUtil.SNORE_ANALYSIS_FILE_NAME);
            if (file.exists()) {
                boolean deleteOp = file.delete();
                if(!deleteOp){
                    callback.creationFinished(false);
                    return;
                }
            }
            callback.creationFinished(true);
        } catch (Exception e) {
            callback.creationFinished(false);
        }
    }

    private void analyze(final AnalyzeCallback callback){
        Logger.d("SNORE :  START ANALYZE");
        LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS","","","");
        long initializeStatus = -1;
        for (int i = 0;i < 3; i++){
            initializeStatus = snoreDetectiveLibrary.SDL_SnoreInitialize();
            if(initializeStatus == 0){
                break;
            }
        }

        if(initializeStatus != 0){
            long errCode = snoreDetectiveLibrary.SDL_GetErrorCode();
            LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_FAILED","INITIALIZE : "+Long.toHexString(errCode),"","");
            showGenericAnalysisErrorOkOnly("UI000560C020","UI000560C021",callback);
//            showGenericAnalysisError(getSDLErrorMsgTag(Long.toHexString(errCode)),"UI000560C021","UI000560C030",getSDLErrorFaqTag(Long.toHexString(errCode)),callback);
        }else{
            SnoreFileUtil.wipeTempAnalyzerContent(getApplicationContext()); // make sure temp folder are clean
            FormPolicyModel formPolicyModel = FormPolicyModel.getPolicy();
            int snoreTIme = formPolicyModel.getSnoreAnalysisParamSnoreTime();
            int snoreTH = formPolicyModel.getSnoreAnalysisParamSnoreTh();
            int snoreInterval = formPolicyModel.getSnoreAnalysisParamSnoreInterval();
            int snoreFileTime = formPolicyModel.getSnoreAnalysisParamSnoreFileTime();
            int snoreOutCount = formPolicyModel.getSnoreAnalysisParamSnoreOutCount();
            long analysisResult = snoreDetectiveLibrary.SDL_SnoreAnalysis(SnoreFileUtil.getAnalysisAudioInputPathTest(getApplicationContext()),
                    SnoreFileUtil.getAnalysisTempFolderPath(getApplicationContext())+"/",
                    snoreTIme,
                    snoreTH,
                    snoreInterval,
                    snoreFileTime,
                    snoreOutCount);
            isAnalyzing = false;
            runOnUiThread(() -> getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON));

            Logger.d("SNORE :  FINISH ANALYZE");
            if(analysisResult == -2 || isAnalysisCancelled) {
                Logger.d("SNORE :  FINISH ANALYZE CANCELLED");
                LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_FAILED","ANALYSIS CANCELLED","","");
                //ignore if cancelled
            }else if(analysisResult != 0){
                Logger.d("SNORE :  FINISH ANALYZE FAIL");
                long errCode = snoreDetectiveLibrary.SDL_GetErrorCode();
                LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_FAILED","ANALYSIS : "+Long.toHexString(errCode),"","");
                showGenericAnalysisErrorOkOnly("UI000560C020","UI000560C021",callback);
//                showGenericAnalysisError(getSDLErrorMsgTag(Long.toHexString(errCode)) ,"UI000560C021","UI000560C030",getSDLErrorFaqTag(Long.toHexString(errCode)),callback);
            }else{
                Logger.d("SNORE :  FINISH ANALYZE SUCCESS");
                String analysisResultJSON = SnoreFileUtil.getAnalysisResult(getApplicationContext());
                if(!analysisResultJSON.isEmpty()){
                    SettingModel userSetting = SettingModel.getSetting();
                    if(userSetting.getSnoring_storage_enable() == 1){
                        Logger.d("SNORE :  COPYING ANALYZE");
                        copyAnalysisResult(analysisResultJSON, new CopyAnalysisCallback() {
                            @Override
                            public void copyFinished(boolean isSuccess) {
                                if(isSuccess){
                                    Logger.d("SNORE :  FINISH ANALYZE CALLBACK");
                                    LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_SUCCESS","FILE COPIED "+userSetting.getSnoring_storage_enable(),"","");
                                    callback.analyzeFinished(true,analysisResultJSON);
                                }else{
                                    long errCode = snoreDetectiveLibrary.SDL_GetErrorCode();
                                    LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_FAILED","COPY_FAILED : "+Long.toHexString(errCode),"","");
                                    showGenericAnalysisError("UI000560C047","UI000560C048","UI000560C049","UI000560C050",callback);
                                }
                            }
                        });
                    }else{
                        Logger.d("SNORE :  FINISH ANALYZE CALLBACK");
                        LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_SUCCESS","FILE NOT COPIED "+userSetting.getSnoring_storage_enable(),"","");
                        callback.analyzeFinished(true,analysisResultJSON);
                    }

                }else{
                    long errCode = snoreDetectiveLibrary.SDL_GetErrorCode();
                    LogUserAction.sendNewLog(userService,"SNORING_ANALYSIS_FAILED","JSON_MISSING : "+Long.toHexString(errCode),"","");
                    showGenericAnalysisError("UI000560C043","UI000560C044","UI000560C045","UI000560C046",callback);
                }
            }
        }
    }

    private void copyAnalysisResult(String jsonResult, CopyAnalysisCallback callback){
        if(callback == null){
            //prevent null pointer exception
            callback = (isSuccess) -> {};
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonResult);
            if(jsonObject.has("snore_audio_file")){
                JSONArray audioFileArray = jsonObject.getJSONArray("snore_audio_file");
                copyAnalysisResultFile(audioFileArray, callback, 0);
            }else{
                callback.copyFinished(false);
            }
        } catch (JSONException e) {
            callback.copyFinished(false);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private Date parseAudioTimestamp(File audioFile){
        String audioFileName = audioFile.getName();
        if(!audioFileName.isEmpty()){
            String[] splittedString = audioFileName.split("_");
            if(splittedString.length == 2){
                String timestampString = splittedString[0];
                try {
                    return new SimpleDateFormat("yyyyMMddHHmmss").parse(timestampString);
                }catch (Exception e){
                    return new Date();
                }
            }
        }
        return new Date();
    }

    private void copyAnalysisResultFile(JSONArray snoreAudioJsonArray,CopyAnalysisCallback callback,int counter){
        LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","init ","","");
        if(counter >= snoreAudioJsonArray.length()) {
            LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","Copy finished","","");
            callback.copyFinished(true);
        }else{
            try {
                JSONObject singleSnoreAudio = snoreAudioJsonArray.getJSONObject(counter);
                if(singleSnoreAudio.has("filename") && singleSnoreAudio.has("start_time")){
                    String fileName = singleSnoreAudio.getString("filename");
                    String timeStamp = singleSnoreAudio.getString("start_time");

                    LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 1 "+fileName+" "+timeStamp,"","");
                    if(!fileName.isEmpty() && !timeStamp.isEmpty()){
                        String origin = SnoreFileUtil.getAnalysisTempFolderPath(getApplicationContext())+"/"+fileName;
                        String destination = SnoreFileUtil.getAnalysisResultFolderPath(getApplicationContext())+"/"+timeStamp+"_"+fileName;
                        LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 2 "+origin+" "+destination,"","");
                        try {
                            //check for max
                            File resultFolder = new File(SnoreFileUtil.getAnalysisResultFolderPath(getApplicationContext()));
                            File[] resultFolderContent = resultFolder.listFiles();

                            FormPolicyModel formPolicyModel = FormPolicyModel.getPolicy();
                            if(resultFolderContent != null && resultFolderContent.length >= formPolicyModel.getSnoreAnalysisMaxStorage()){
                                LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 3 file max reached "+resultFolderContent.length+" "+formPolicyModel.getSnoreAnalysisMaxStorage(),"","");
                                File oldestFile = resultFolderContent[0];
                                for (File resultFile:resultFolderContent
                                     ) {
                                    Date oldestFileDate = parseAudioTimestamp(oldestFile);
                                    Date resultFileDate = parseAudioTimestamp(resultFile);
                                    if(oldestFileDate.after(resultFileDate)){
                                        oldestFile = resultFile;

                                    }
                                }
                                LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 3 deleting "+oldestFile.getAbsolutePath(),"","");
                                boolean deleteOp = oldestFile.delete();
                                if(!deleteOp){
                                    LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 3 deleting failed","","");
                                    callback.copyFinished(false);
                                    return;
                                }
                            }
                            SnoreFileUtil.copy(origin,destination);
                            copyAnalysisResultFile(snoreAudioJsonArray,callback,counter+1);
                        }catch (IOException ex){
                            LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 3 exception"+ex.getLocalizedMessage(),"","");
                            callback.copyFinished(false);
                        }
                    }else{
                        callback.copyFinished(false);
                    }
                }else{
                    callback.copyFinished(false);
                }
            } catch (JSONException e) {
                LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 1 exception"+e.getLocalizedMessage(),"","");
                callback.copyFinished(false);
            }
        }
    }
    private void copyAnalysisResultFileTest(String[] filename,String[] timestamp,CopyAnalysisCallback callback,int counter){
        LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","init ","","");
        if(counter >= filename.length) {
            LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","Copy finished","","");
            callback.copyFinished(true);
        }else{
            String fileName = filename[counter];
            String timeStamp = timestamp[counter];

            LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 1 "+fileName+" "+timeStamp,"","");
            if(!fileName.isEmpty() && !timeStamp.isEmpty()){
                String origin = SnoreFileUtil.getAnalysisTempFolderPath(getApplicationContext())+"/"+timeStamp+"_"+fileName;
                String destination = SnoreFileUtil.getAnalysisResultFolderPath(getApplicationContext())+"/"+timeStamp+"_"+fileName;
                LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 2 "+origin+" "+destination,"","");
                try {
                    //check for max
                    File resultFolder = new File(SnoreFileUtil.getAnalysisResultFolderPath(getApplicationContext()));
                    File[] resultFolderContent = resultFolder.listFiles();

                    FormPolicyModel formPolicyModel = FormPolicyModel.getPolicy();
                    if(resultFolderContent != null && resultFolderContent.length >= formPolicyModel.getSnoreAnalysisMaxStorage()){
                        LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 3 file max reached "+resultFolderContent.length+" "+formPolicyModel.getSnoreAnalysisMaxStorage(),"","");
                        File oldestFile = resultFolderContent[0];
                        for (File resultFile:resultFolderContent
                        ) {
                            Date oldestFileDate = parseAudioTimestamp(oldestFile);
                            Date resultFileDate = parseAudioTimestamp(resultFile);
                            if(oldestFileDate.after(resultFileDate)){
                                oldestFile = resultFile;

                            }
                        }
                        LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 3 deleting "+oldestFile.getAbsolutePath(),"","");
                        boolean deleteOp = oldestFile.delete();
                        if(!deleteOp){
                            LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 3 deleting failed","","");
                            callback.copyFinished(false);
                            return;
                        }
                    }
                    SnoreFileUtil.copy(origin,destination);
                    copyAnalysisResultFileTest(filename,timestamp,callback,counter+1);
                }catch (IOException ex){
                    LogUserAction.sendNewLog(userService,"SNORE_AUDIO_COPY","step 3 exception"+ex.getLocalizedMessage(),"","");
                    callback.copyFinished(false);
                }
            }else{
                callback.copyFinished(false);
            }
        }
    }
    private void refreshUIState(){
        if(isRecording){
            setFragment(snoreRecordFragment);
            startRecordUI();
        }else{
            DelayTimer.getInstance().stop();
            setFragment(snoreManualFragment);
            stopRecordUI();
        }
    }

    private void openFAQ(String faqId){
        Intent faqIntent = new Intent(SnoreActivity.this, FaqActivity.class);
        faqIntent.putExtra("ID_FAQ", faqId);
        faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(faqIntent);
    }

    private boolean isAudioBusy(){
        AudioManager audioManager =  (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        boolean isAudioBusy = audioManager.getMode() == AudioManager.MODE_RINGTONE ||  audioManager.getMode() == AudioManager.MODE_IN_CALL || audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION || audioManager.isMusicActive();
        if(isAudioBusy){
            LogUserAction.sendNewLog(userService,"SNORING_RECORDING_INTERRUPTED","reason :"+audioManager.getMode()+" isMusicActive "+audioManager.isMusicActive(),"","");
        }
        return isAudioBusy;
    }

    private boolean isTelephonyBusy(){
        AudioManager audioManager =  (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getMode() == AudioManager.MODE_RINGTONE ||  audioManager.getMode() == AudioManager.MODE_IN_CALL || audioManager.getMode() == AudioManager.MODE_IN_COMMUNICATION ;
    }

    private boolean isMusicBusy(){
        AudioManager audioManager =  (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        return audioManager.isMusicActive() ;
    }


    private boolean isAlarmActive(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return AlarmsQuizModule.isAlarmIsLessThanThreeMinute() && !AlarmStopModel.isStopPressed(day) && AlarmsScheduler.isAlarmActive(day);
    }

    private String getSDLErrorMsgTag(String errorCode){
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("80000000", "UI000560C073");
        msgMap.put("80001000", "UI000560C074");
        msgMap.put("80001001", "UI000560C075");
        msgMap.put("80001002", "UI000560C076");
        msgMap.put("80001003", "UI000560C077");
        msgMap.put("80001004", "UI000560C078");
        msgMap.put("80001005", "UI000560C079");
        msgMap.put("80001006", "UI000560C080");
        msgMap.put("80001008", "UI000560C081");
        msgMap.put("80001009", "UI000560C082");
        msgMap.put("80001010", "UI000560C083");
        msgMap.put("80001011", "UI000560C084");
        msgMap.put("80001100", "UI000560C085");
        msgMap.put("80002000", "UI000560C086");
        msgMap.put("80002100", "UI000560C087");

        String retVal = msgMap.get(errorCode);
        if(retVal == null || retVal.isEmpty()){
            retVal = "UI000560C020";//default value
        }
        return retVal;
    }

    private String getSDLErrorFaqTag(String errorCode){
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("80000000", "UI000560C088");
        msgMap.put("80001000", "UI000560C089");
        msgMap.put("80001001", "UI000560C090");
        msgMap.put("80001002", "UI000560C091");
        msgMap.put("80001003", "UI000560C092");
        msgMap.put("80001004", "UI000560C093");
        msgMap.put("80001005", "UI000560C094");
        msgMap.put("80001006", "UI000560C095");
        msgMap.put("80001008", "UI000560C096");
        msgMap.put("80001009", "UI000560C097");
        msgMap.put("80001010", "UI000560C098");
        msgMap.put("80001011", "UI000560C099");
        msgMap.put("80001100", "UI000560C100");
        msgMap.put("80002000", "UI000560C101");
        msgMap.put("80002100", "UI000560C102");

        String retVal = msgMap.get(errorCode);
        if(retVal == null || retVal.isEmpty()){
            retVal = "UI000560C022";//default value
        }
        return retVal;
    }
}

class DelayTimer{
    public boolean isRunning;
    private double timeLeft;
    private String type = "";
    private CountDownTimer timer;
    private DelayTimer(){

    }

    private static DelayTimer instance;
    public static DelayTimer getInstance(){
        if(instance == null){
            instance = new DelayTimer();
        }
        return instance;
    }
    public void startTimerDelay(double delayTime, SnoreActivity.TimerCallback callback,String type){
        this.type = type;
        if(callback == null){
            //prevent null pointer exception
            callback = new SnoreActivity.TimerCallback() {
                @Override
                public void timerFinished() {

                }

                @Override
                public void timerTick() {

                }
            };
        }

        SnoreActivity.TimerCallback finalCallback = callback;
        isRunning = true;
        timeLeft = delayTime;

        Logger.d("SNORE : TIMER DELAY FROM SERVER: "+timeLeft+" - "+type);
        timer = new CountDownTimer((long)timeLeft*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateCountDownTextDelay();
                finalCallback.timerTick();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                finalCallback.timerFinished();
            }
        }.start();
    }


    private void updateCountDownTextDelay() {
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        Logger.d("SNORE : RECORDING DELAY: " + timeLeftFormatted + " - " + type);
    }

    public  void stop(){
        if(timer != null){
            timer.cancel();
        }
    }
}
