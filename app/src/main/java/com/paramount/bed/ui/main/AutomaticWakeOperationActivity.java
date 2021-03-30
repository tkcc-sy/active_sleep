package com.paramount.bed.ui.main;

import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.orhanobut.logger.Logger;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSAutomaticOperationDelegate;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.SettingProvider;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.BluetoothUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MediaPlayerUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NemuriScanUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.TokenExpiredReceiver;
import com.paramount.bed.util.alarms.AlarmsQuizModule;
import com.paramount.bed.util.alarms.AlarmsReceiver;
import com.paramount.bed.util.alarms.AlarmsScheduler;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.ViewCollections;

import static com.paramount.bed.util.alarms.AlarmsScheduler.setAllAlarms;

public class AutomaticWakeOperationActivity extends BaseActivity implements NSScanDelegate, NSConnectionDelegate, NemuriScanUtil.NemuriScanDetailFetchListener, NSAutomaticOperationDelegate {
    //MARK : BLE Vars
    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            purgeBLE();
            nsManager = null;
            finish();
        }
    };
    private NSManager nsManager;
    private NemuriScanModel nemuriScanDetail = new NemuriScanModel();
    private NemuriScanModel nemuriScanModel;
    NemuriConstantsModel nsConstants;
    //MARK END: BLE Vars

    //Line For
    TokenExpiredReceiver tokenExpiredReceiver = new TokenExpiredReceiver();
    //Line

    private ArrayList<Integer> audioTrack;
    private List<Alarm> alarms;
    private AutomaticAlarmAdapter adapter;
    private SettingProvider settingProvider;

    @BindView(R.id.alarmList)
    RecyclerView alarmList;

    @BindView(R.id.descPattern)
    TextView descPattern;

    @BindView(R.id.labelPattern1)
    TextView labelPattern1;

    @BindView(R.id.labelPattern2)
    TextView labelPattern2;


    @BindView(R.id.imgPattern)
    ImageView imgPattern;

    @BindView(R.id.subtitle3)
    TextView subtitle3;

    @BindViews({R.id.btnPattern1, R.id.btnPattern2, R.id.btnPattern3, R.id.btnPattern4})
    List<ToggleButton> radioPatterns;

    @BindViews({R.id.btnAlarm0, R.id.btnAlarm1, R.id.btnAlarm2, R.id.btnAlarm3})
    List<ToggleButton> radioAlarms;

    @BindViews({R.id.btnPlayAlarm1, R.id.btnPlayAlarm2, R.id.btnPlayAlarm3})
    List<ToggleButton> playAlarmButtons;

    private boolean shouldSendPatternSetting = false;
    private View.OnClickListener radioPatternsListener = v -> {
        uncheckAllPatterns();

        ToggleButton view = (ToggleButton) v;
        view.setChecked(true);

        int selectedPatternIndex = -1;
        switch (v.getId()) {
            case R.id.btnPattern1:
                selectedPatternIndex = 0;
                break;
            case R.id.btnPattern2:
                selectedPatternIndex = 1;
                break;
            case R.id.btnPattern3:
                selectedPatternIndex = 2;
                break;
            case R.id.btnPattern4:
                selectedPatternIndex = 3;
                break;
        }

        hidePatternLabels();
        if (selectedPatternIndex != -1) {
            //update UI
            updatePatternLabels(selectedPatternIndex);
            //update data
            if (shouldSendPatternSetting) {
                setSettingPattern(selectedPatternIndex);
            }
        }

    };

    private boolean shouldSendAlarmSetting = false;
    private View.OnClickListener radioAlarmListener = v -> {
        uncheckAllAlarms();
        ToggleButton view = (ToggleButton) v;
        view.setChecked(true);
        if (shouldSendAlarmSetting) {
            switch (v.getId()) {
                case R.id.btnAlarm0:
                    setSettingAudio(0);
                    break;
                case R.id.btnAlarm1:
                    setSettingAudio(1);
                    break;
                case R.id.btnAlarm2:
                    setSettingAudio(2);
                    break;
                case R.id.btnAlarm3:
                    setSettingAudio(3);
                    break;
            }
        }
    };

    private View.OnClickListener playAlarmClickListener = v -> {
        ToggleButton view = (ToggleButton) v;
        if (!view.isChecked()) {
            uncheckAllPlayAlarms();
            switch (v.getId()) {
                case R.id.btnPlayAlarm1:
                    playAudio(false, 0);
                    break;
                case R.id.btnPlayAlarm2:
                    playAudio(false, 1);
                    break;
                case R.id.btnPlayAlarm3:
                    playAudio(false, 2);
                    break;
            }
        } else {
            uncheckAllPlayAlarms();
            view.setChecked(true);
            switch (v.getId()) {
                case R.id.btnPlayAlarm1:
                    playAudio(true, 0);
                    break;
                case R.id.btnPlayAlarm2:
                    playAudio(true, 1);
                    break;
                case R.id.btnPlayAlarm3:
                    playAudio(true, 2);
                    break;
            }
        }
    };

    SettingModel oldSetting = new SettingModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        setToolbarTitle(LanguageProvider.getLanguage("UI000742C001"));
        settingProvider = new SettingProvider(this);

        audioTrack = new ArrayList<>();
        audioTrack.add(R.raw.audio1short);
        audioTrack.add(R.raw.audio2short);
        audioTrack.add(R.raw.audio3short);

        ViewCollections.run(radioPatterns, (view, index) -> view.setOnClickListener(radioPatternsListener));
        ViewCollections.run(radioAlarms, (view, index) -> view.setOnClickListener(radioAlarmListener));
        ViewCollections.run(playAlarmButtons, (view, index) -> view.setOnClickListener(playAlarmClickListener));

        nemuriScanModel = NemuriScanModel.get();
        showProgress();
        DeviceTemplateProvider.getDeviceTemplate(this,
                (mattressModels, bedModels, mattressModelDefaults, bedModelDefaults, nemuriConstantsModel) -> {
                    nsConstants = nemuriConstantsModel;
                    hideProgress();
                    if (!NetworkUtil.isNetworkConnected(AutomaticWakeOperationActivity.this)) {
                        updateUIState();
                    } else {
                        settingProvider.getSetting((settingModel, isSuccess, e) -> {
                            oldSetting.copyValuesFrom(settingModel);
                            setAllAlarms(getApplicationContext());
                            runOnUiThread(() -> {
                                if (MultipleDeviceUtil.checkForceLogout(e)) {
                                    MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this);
                                }
                            });
                        });

                        nsManager = NSManager.getInstance(AutomaticWakeOperationActivity.this, AutomaticWakeOperationActivity.this);
                        checkNSDetail();
                    }
                }, UserLogin.getUserLogin().getId());
        if(DisplayUtils.FONTS.bigFontStatus(AutomaticWakeOperationActivity.this)) {
            labelPattern1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
            labelPattern2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
        } else {
            labelPattern1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
            labelPattern2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        }
        if(DisplayUtils.FONTS.bigFontStatus(AutomaticWakeOperationActivity.this)){
            int padding_in_dp = 40;  // 6 dps
            final float scale = getResources().getDisplayMetrics().density;
            int padding_in_px = (int) (padding_in_dp * scale + 0.5f);
            subtitle3.setPaddingRelative(0,0,padding_in_px,0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        AlarmsQuizModule.run(this);
    }

    @Override
    protected void onPause() {
        //clearReferences();
        super.onPause();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
        MediaPlayerUtil.stopAudio();
        uncheckAllPlayAlarms();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerUtil.stopAudio();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
        purgeBLE();
    }

    @Override
    public void onBackPressed() {
        SettingModel currentSetting = SettingModel.getSetting();
        if (nsManager != null &&
                (oldSetting.isAutomaticWakeSettingDifferentFrom(currentSetting)) && BluetoothUtil.isBluetoothEnable() && PermissionUtil.hasLocationPermissions(this) && PermissionUtil.isLocationServiceEnable(this)) {
            tryToConnectBLE();
        } else {
            finish();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_automatic_wake_operation;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    private void playAudio(boolean isPlay, int position) {
        MediaPlayerUtil.stopAudio();
        if (isPlay) {
            MediaPlayerUtil.playAudio(getApplicationContext(), audioTrack.get(position), AudioManager.STREAM_MUSIC, false, mediaPlayer -> uncheckAllPlayAlarms());
        } else {
            MediaPlayerUtil.stopAudio();
        }
    }

    private void checkPattern(int index) {
        shouldSendPatternSetting = false; //prevent sending to server
        radioPatterns.get(index).setChecked(true);
        shouldSendPatternSetting = true;
    }

    private void uncheckAllPatterns() {
        shouldSendPatternSetting = false; //prevent sending to server
        ViewCollections.run(radioPatterns, (view, index) -> view.setChecked(false));
        shouldSendPatternSetting = true;
    }

    private void checkAlarm(int index) {
        shouldSendAlarmSetting = false; //prevent sending to server
        radioAlarms.get(index).setChecked(true);
        shouldSendAlarmSetting = true;
    }

    private void uncheckAllAlarms() {
        shouldSendAlarmSetting = false; //prevent sending to server
        ViewCollections.run(radioAlarms, (view, index) -> view.setChecked(false));
        shouldSendAlarmSetting = true;
    }

    public void uncheckAllPlayAlarms() {
        ViewCollections.run(playAlarmButtons, (view, index) -> view.setChecked(false));
    }

    public void setAlarm(int index, boolean active) {
        alarms.get(index).active = active;
        AutomaticAlarmAdapter adapter = new AutomaticAlarmAdapter(this, alarms);
        alarmList.setAdapter(adapter);
    }

    public class Alarm {
        String day;
        boolean active;
        LocalTime time;

        public Alarm(String day, LocalTime time, boolean active) {
            this.day = day;
            this.time = time;
            this.active = active;
        }
    }

    public LocalTime stringToLocalTime(String value) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
        return fmt.parseLocalTime(value);
    }

    public void setSettingPattern(int position) {
        //defer to background thread to ease UI load
        AsyncTask.execute(() -> settingProvider.saveSetting("automatic_operation_bed_pattern_id", String.valueOf(position), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this)));
    }

    public void setSettingAudio(int position) {
        //defer to background thread to ease UI load
        AsyncTask.execute(() -> settingProvider.saveSetting("automatic_operation_alarm_id", String.valueOf(position), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this)));
    }

    public void setSettingAlarm(int position, boolean isChecked) {
        switch (position) {
            case 0:
                settingProvider.saveSetting("automatic_operation_wakeup_sunday_active", String.valueOf(isChecked), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                setAlarms(
                        Calendar.SUNDAY,
                        SettingModel.getSetting().getAutomatic_operation_sleep_sunday_time(),
                        SettingModel.getSetting().isAutomatic_operation_sleep_sunday_active()
                );
                break;
            case 1:
                settingProvider.saveSetting("automatic_operation_wakeup_monday_active", String.valueOf(isChecked), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                setAlarms(
                        Calendar.MONDAY,
                        SettingModel.getSetting().getAutomatic_operation_sleep_monday_time(),
                        SettingModel.getSetting().isAutomatic_operation_sleep_monday_active()
                );
                break;
            case 2:
                settingProvider.saveSetting("automatic_operation_wakeup_tuesday_active", String.valueOf(isChecked), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                setAlarms(
                        Calendar.TUESDAY,
                        SettingModel.getSetting().getAutomatic_operation_sleep_tuesday_time(),
                        SettingModel.getSetting().isAutomatic_operation_sleep_tuesday_active()
                );
                break;
            case 3:
                settingProvider.saveSetting("automatic_operation_wakeup_wednesday_active", String.valueOf(isChecked), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                setAlarms(
                        Calendar.WEDNESDAY,
                        SettingModel.getSetting().getAutomatic_operation_sleep_wednesday_time(),
                        SettingModel.getSetting().isAutomatic_operation_sleep_wednesday_active()
                );
                break;
            case 4:
                settingProvider.saveSetting("automatic_operation_wakeup_thursday_active", String.valueOf(isChecked), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                setAlarms(
                        Calendar.THURSDAY,
                        SettingModel.getSetting().getAutomatic_operation_sleep_thursday_time(),
                        SettingModel.getSetting().isAutomatic_operation_sleep_thursday_active()
                );
                break;
            case 5:
                settingProvider.saveSetting("automatic_operation_wakeup_friday_active", String.valueOf(isChecked), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                setAlarms(
                        Calendar.FRIDAY,
                        SettingModel.getSetting().getAutomatic_operation_sleep_friday_time(),
                        SettingModel.getSetting().isAutomatic_operation_sleep_friday_active()
                );
                break;
            case 6:
                settingProvider.saveSetting("automatic_operation_wakeup_saturday_active", String.valueOf(isChecked), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                setAlarms(
                        Calendar.SATURDAY,
                        SettingModel.getSetting().getAutomatic_operation_sleep_saturday_time(),
                        SettingModel.getSetting().isAutomatic_operation_sleep_saturday_active()
                );
                break;

        }

    }

    public void setTimeAlarm(int position) {
        int hourCurrent = alarms.get(position).time.getHourOfDay();
        int minuteCurrent = alarms.get(position).time.getMinuteOfHour();
        List<Integer> hourOptions = new ArrayList<>();
        List<Integer> minuteOptions = new ArrayList<>();
        // set options
        for (int i = 0; i <= 23; i++) {
            hourOptions.add(i);
        }

        for (int i = 0; i <= 59; i++) {
            minuteOptions.add(i);
        }

        OptionsPickerView timePicker = new OptionsPickerBuilder(this, onTimeSelect(position))
                .setSelectOptions(hourCurrent, minuteCurrent)
                .setBackgroundId(0)
                .setCyclic(true, true, false)
                .setCancelText(LanguageProvider.getLanguage("UI000742C030"))
                .setSubmitText(LanguageProvider.getLanguage("UI000742C031"))
                .build();

        timePicker.setNPicker(hourOptions, minuteOptions, null);
        timePicker.show();
    }

    private OnOptionsSelectListener onTimeSelect(int position) {
        return (options1, options2, options3, v) -> {
            String hour = String.valueOf(options1);
            String minute = String.valueOf(options2);

            String time = (hour.length() == 1 ? "0" + hour : hour) + ":" + (minute.length() == 1 ? "0" + minute : minute);

            switch (position) {
                case 0:
                    settingProvider.saveSetting("automatic_operation_wakeup_sunday_time", time, (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                    setAlarms(
                            Calendar.SUNDAY,
                            SettingModel.getSetting().getAutomatic_operation_sleep_sunday_time(),
                            SettingModel.getSetting().isAutomatic_operation_sleep_sunday_active()
                    );
                    break;
                case 1:
                    settingProvider.saveSetting("automatic_operation_wakeup_monday_time", time, (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                    setAlarms(
                            Calendar.MONDAY,
                            SettingModel.getSetting().getAutomatic_operation_sleep_monday_time(),
                            SettingModel.getSetting().isAutomatic_operation_sleep_monday_active()
                    );
                    break;
                case 2:
                    settingProvider.saveSetting("automatic_operation_wakeup_tuesday_time", time, (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                    setAlarms(
                            Calendar.TUESDAY,
                            SettingModel.getSetting().getAutomatic_operation_sleep_tuesday_time(),
                            SettingModel.getSetting().isAutomatic_operation_sleep_tuesday_active()
                    );
                    break;
                case 3:
                    settingProvider.saveSetting("automatic_operation_wakeup_wednesday_time", time, (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                    setAlarms(
                            Calendar.WEDNESDAY,
                            SettingModel.getSetting().getAutomatic_operation_sleep_wednesday_time(),
                            SettingModel.getSetting().isAutomatic_operation_sleep_wednesday_active()
                    );
                    break;
                case 4:
                    settingProvider.saveSetting("automatic_operation_wakeup_thursday_time", time, (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                    setAlarms(
                            Calendar.THURSDAY,
                            SettingModel.getSetting().getAutomatic_operation_sleep_thursday_time(),
                            SettingModel.getSetting().isAutomatic_operation_sleep_thursday_active()
                    );
                    break;
                case 5:
                    settingProvider.saveSetting("automatic_operation_wakeup_friday_time", time, (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                    setAlarms(
                            Calendar.FRIDAY,
                            SettingModel.getSetting().getAutomatic_operation_sleep_friday_time(),
                            SettingModel.getSetting().isAutomatic_operation_sleep_friday_active()
                    );
                    break;
                case 6:
                    settingProvider.saveSetting("automatic_operation_wakeup_saturday_time", time, (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticWakeOperationActivity.this));
                    setAlarms(
                            Calendar.SATURDAY,
                            SettingModel.getSetting().getAutomatic_operation_sleep_saturday_time(),
                            SettingModel.getSetting().isAutomatic_operation_sleep_saturday_active()
                    );
                    break;

            }
            updateUIState();
        };
    }

    public void setAlarms(int dayOfWeek, String time, boolean isActive) {
        String[] TIME = time.split(":");
//        if (isActive) {
        AlarmsScheduler.setReminder(dayOfWeek, this, AlarmsReceiver.class, Integer.parseInt(TIME[0]), Integer.parseInt(TIME[1]));
//        } else {
//            AlarmsScheduler.cancelReminder(dayOfWeek, this, AlarmsReceiver.class);
//        }
    }

    //MARK : BLE related functions
    private void tryToConnectBLE() {
        if (nemuriScanModel == null) {
            finish();
            return;
        }
        runOnUiThread(this::showProgress);
        //setup connection timeout

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
            DialogUtil.createCustomYesNo(AutomaticWakeOperationActivity.this,
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

    //MARK : NSConnectionDelegate Implementation
    @Override
    public void onConnectionEstablished() {
        runOnUiThread(() -> {
            nsManager.requestAuthentication(nemuriScanModel.getServerGeneratedId());
        });
    }

    @Override
    public void onDisconnect() {
        finish();
    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        runOnUiThread(() -> {
            if (nemuriScanModel != null) {
                nsManager.notifyAutomaticOperationChange();
                finish();
            } else {
                Logger.e("onSerialNumberReceived ns model null");
            }
        });
    }

    @Override
    public void onAuthenticationFinished(int result) {
        runOnUiThread(() -> {
            if (result == NSConstants.NS_AUTH_SUCCESS || result == NSConstants.NS_AUTH_REG_SUCCESS) {
                //LOG HERE NS_SET_SERVERID_SUCCESS
                LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_SUCCESS", "1", "", "UI000742");
                nsManager.notifyAutomaticOperationChange();
            } else {
                //LOG HERE NS_SET_SERVERID_FAILED
                LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_FAILED", "1", "", "UI000742");
                finish();
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
        runOnUiThread(() -> showLocationPermissionDialogAlert());
    }

    @Override
    public void onLocationServiceDisabled() {
        runOnUiThread(() -> showLocationServiceDialogAlert());
    }

    public void showLocationPermissionDialogAlert() {
        hideProgress();
        purgeBLE();
        if (NemuriScanModel.get() != null) {
            PermissionUtil.showLocationPermissionDialogAlert(AutomaticWakeOperationActivity.this, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    hideProgress();
                    purgeBLE();
                    finish();
                }

                @Override
                public void onPermissionGranted() {
                    onBackPressed();
                }
            });
        }
    }

    public void showLocationServiceDialogAlert() {
        hideProgress();
        purgeBLE();
        if (NemuriScanModel.get() != null) {
            PermissionUtil.showLocationServiceDialogAlert(AutomaticWakeOperationActivity.this, new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                    hideProgress();
                    purgeBLE();
                    finish();
                }

                @Override
                public void onEnabled() {
                    onBackPressed();
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
                    Logger.v("AutomaticSleepOperationActivity : Scanning BLE, looking for " + savedMac + " trying " + targetMac + " " + scanResult.getDevice().getName());
                }
                if (savedMac.equalsIgnoreCase(targetMac)) {
                    Logger.v("AutomaticSleepOperationActivity : Scanning BLE, match found");
                    nsManager.connectToDevice(scanResult.getDevice(), this);
                    nsManager.stopScan();
                }
            } else {
                Logger.e("onSerialNumberReceived ns model null");
            }
        });
    }
    //MARK END: NSScanDelegate Implementation

    //MARK : NemuriScanDetailFetchListener Implementation
    @Override
    public void onNemuriScanDetailFetched(NemuriScanModel nemuriScanDetailModel) {
        nemuriScanDetail = nemuriScanDetailModel;
        runOnUiThread(() -> {
            hideProgress();
            if (!nemuriScanDetail.getBedActive()) {
                DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000802C030"), LanguageProvider.getLanguage("UI000802C031"), null);
            }
        });
        updateUIState();
    }
    //MARK END : NemuriScanDetailFetchListener Implementation

    //MARK : NSAutomaticOperationDelegate Implementation
    @Override
    public void onNotifyAutomaticOperationFinished() {
        purgeBLE();
        finish();
    }
    //MARK END: NSAutomaticOperationDelegate Implementation

    private void updateUIState() {
        runOnUiThread(() -> {
            SettingModel setting = SettingModel.getFirst();
            applySettingToView(setting);
            boolean shouldEnabled = nemuriScanDetail.getBedActive() && NetworkUtil.isNetworkConnected(getApplicationContext());

            adapter.setActivated(shouldEnabled);
            adapter.notifyDataSetChanged();
            ViewCollections.run(radioPatterns, (view, index) -> view.setEnabled(shouldEnabled));
            ViewCollections.run(radioAlarms, (view, index) -> view.setEnabled(shouldEnabled));
            ViewCollections.run(playAlarmButtons, (view, index) -> view.setEnabled(shouldEnabled));

            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                DialogUtil.showOfflineDialog(this);
            }
        });
    }

    private void hidePatternLabels() {
        labelPattern1.setVisibility(View.GONE);
        labelPattern2.setVisibility(View.GONE);
        labelPattern1.setEnabled(false);
        labelPattern2.setEnabled(false);
        labelPattern1.setText("");
        labelPattern2.setText("");
    }

    private void updatePatternLabels(int selectedPattern) {
        switch (selectedPattern) {
            case 0:
                labelPattern1.setText(LanguageProvider.getLanguage("UI000742C019"));
                labelPattern1.setVisibility(View.VISIBLE);
                descPattern.setText(LanguageProvider.getLanguage("UI000742C020"));

                imgPattern.setImageDrawable(getDrawable(R.drawable.automatic_operation_dummy5));
                break;
            case 1:
                labelPattern1.setText(LanguageProvider.getLanguage("UI000742C021"));
                labelPattern1.setVisibility(View.VISIBLE);
                labelPattern2.setText(LanguageProvider.getLanguage("UI000742C022"));
                labelPattern2.setEnabled(true);
                labelPattern2.setVisibility(View.VISIBLE);
                descPattern.setText(LanguageProvider.getLanguage("UI000742C023"));

                imgPattern.setImageDrawable(getDrawable(R.drawable.automatic_operation_dummy5));
                break;
            case 2:
                labelPattern1.setText(LanguageProvider.getLanguage("UI000742C024"));
                labelPattern1.setEnabled(true);
                labelPattern1.setVisibility(View.VISIBLE);
                descPattern.setText(LanguageProvider.getLanguage("UI000742C025"));

                imgPattern.setImageDrawable(getDrawable(R.drawable.automatic_operation_dummy5));
                break;
            case 3:
                labelPattern1.setText(LanguageProvider.getLanguage("UI000742C027"));
                labelPattern1.setEnabled(true);
                labelPattern1.setVisibility(View.VISIBLE);
                labelPattern2.setText(LanguageProvider.getLanguage("UI000742C028"));
                labelPattern2.setEnabled(true);
                labelPattern2.setVisibility(View.VISIBLE);
                descPattern.setText(LanguageProvider.getLanguage("UI000742C029"));

                imgPattern.setImageDrawable(getDrawable(R.drawable.automatic_operation_dummy5));
                break;
        }
    }

    public void applySettingToView(SettingModel setting) {
        ViewCollections.run(radioPatterns, (view, index) -> view.setEnabled(false));
        ViewCollections.run(radioAlarms, (view, index) -> view.setEnabled(false));
        ViewCollections.run(playAlarmButtons, (view, index) -> view.setEnabled(false));

        //setup bed pattern
        uncheckAllPatterns();
        checkPattern(setting.getAutomatic_operation_bed_pattern_id());
        hidePatternLabels();
        updatePatternLabels(setting.getAutomatic_operation_bed_pattern_id());

        //setup alarm
        alarms = new ArrayList<>();
        alarms.add(new Alarm(LanguageProvider.getLanguage("UI000742C003"), stringToLocalTime(setting.automatic_operation_wakeup_sunday_time), setting.automatic_operation_wakeup_sunday_active));
        alarms.add(new Alarm(LanguageProvider.getLanguage("UI000742C004"), stringToLocalTime(setting.automatic_operation_wakeup_monday_time), setting.automatic_operation_wakeup_monday_active));
        alarms.add(new Alarm(LanguageProvider.getLanguage("UI000742C005"), stringToLocalTime(setting.automatic_operation_wakeup_tuesday_time), setting.automatic_operation_wakeup_tuesday_active));
        alarms.add(new Alarm(LanguageProvider.getLanguage("UI000742C006"), stringToLocalTime(setting.automatic_operation_wakeup_wednesday_time), setting.automatic_operation_wakeup_wednesday_active));
        alarms.add(new Alarm(LanguageProvider.getLanguage("UI000742C007"), stringToLocalTime(setting.automatic_operation_wakeup_thursday_time), setting.automatic_operation_wakeup_thursday_active));
        alarms.add(new Alarm(LanguageProvider.getLanguage("UI000742C008"), stringToLocalTime(setting.automatic_operation_wakeup_friday_time), setting.automatic_operation_wakeup_friday_active));
        alarms.add(new Alarm(LanguageProvider.getLanguage("UI000742C009"), stringToLocalTime(setting.automatic_operation_wakeup_saturday_time), setting.automatic_operation_wakeup_saturday_active));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        alarmList.setLayoutManager(layoutManager);
        adapter = new AutomaticAlarmAdapter(this, alarms);
        alarmList.setAdapter(adapter);

        //setup alarm type
        uncheckAllAlarms();
        checkAlarm(setting.getAutomatic_operation_alarm_id());

    }

    private void checkNSDetail() {
        if (nemuriScanModel == null) {
            runOnUiThread(this::showProgress);
            settingProvider.noNSSetting((isSuccess) -> runOnUiThread(() -> {
                hideProgress();
                applyNoNSUI();
                DialogUtil.createSimpleOkDialogLink(AutomaticWakeOperationActivity.this, "", LanguageProvider.getLanguage("UI000610C030"),
                        LanguageProvider.getLanguage("UI000610C043"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(AutomaticWakeOperationActivity.this, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000610C043");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        }, LanguageProvider.getLanguage("UI000610C031"), (dialogInterface, i) -> dialogInterface.dismiss());
            }));
            return;
        }
        runOnUiThread(this::showProgress);
        NemuriScanUtil.fetchSpec(AutomaticWakeOperationActivity.this, AutomaticWakeOperationActivity.this);
    }

    private void applyNoNSUI() {
        runOnUiThread(() -> {
            applySettingToView(SettingModel.resetNSRelatedSettings());
            adapter.setActivated(false);
            adapter.notifyDataSetChanged();
            ViewCollections.run(radioPatterns, (view, index) -> view.setEnabled(false));
            ViewCollections.run(radioAlarms, (view, index) -> view.setEnabled(false));
            ViewCollections.run(playAlarmButtons, (view, index) -> view.setEnabled(false));
            setAllAlarms(getApplicationContext());
        });
    }

    private void purgeBLE() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }
}
