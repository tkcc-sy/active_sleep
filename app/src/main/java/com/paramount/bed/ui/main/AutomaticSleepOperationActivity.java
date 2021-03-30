package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.FormPolicyProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.SettingProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.FirmwareVersionResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.login.ChangePhonePinActivity;
import com.paramount.bed.util.BluetoothUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NemuriScanUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.TokenExpiredReceiver;
import com.paramount.bed.util.alarms.AlarmsAutoScheduler;
import com.paramount.bed.util.alarms.AlarmsQuizModule;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.paramount.bed.util.alarms.AlarmsScheduler.setAllAlarms;

public class AutomaticSleepOperationActivity extends BaseActivity implements NSScanDelegate, NSConnectionDelegate, NemuriScanUtil.NemuriScanDetailFetchListener, NSAutomaticOperationDelegate {

    //MARK : BLE Vars
    private boolean isNemuriScanInitiated = true;
    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            purgeBLE();
            nsManager = null;
            finish();
        }
    };
    private NSManager nsManager;
    private boolean oldSetting;
    private int oldAngleSetting;
    private int selectedAngleSetting;
    NemuriConstantsModel nsConstants;
    //MARK END: BLE Vars
    TokenExpiredReceiver tokenExpiredReceiver = new TokenExpiredReceiver();
    NemuriScanModel nemuriScanDetail = new NemuriScanModel();
    private NemuriScanModel nemuriScanModel;

    @BindView(R.id.switch_onoff)
    SwitchButton switchOnOff;
    SettingProvider settingProvider;
    SwitchButton.OnCheckedChangeListener automaticOperationSleepActiveListener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            settingProvider.saveSetting("automatic_operation_sleep_active", String.valueOf(isChecked), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticSleepOperationActivity.this));
            AlarmsAutoScheduler.setAllAlarms(AutomaticSleepOperationActivity.this);
        }
    };

    @BindView(R.id.spin_degree)
    LinearLayout spinDegree;

    @BindView(R.id.txt_degree)
    TextView txtDegree;

    @BindView(R.id.angle_setting_container)
    LinearLayout angleSettingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        setToolbarTitle(LanguageProvider.getLanguage("UI000741C001"));
        switchOnOff.setOnCheckedChangeListener(automaticOperationSleepActiveListener);
        settingProvider = new SettingProvider(this);
        nemuriScanModel = NemuriScanModel.get();
        showProgress();
        DeviceTemplateProvider.getDeviceTemplate(this,
                (mattressModels, bedModels, mattressModelDefaults, bedModelDefaults, nemuriConstantsModel) -> {
                    nsConstants = nemuriConstantsModel;
                    hideProgress();
                    if (!NetworkUtil.isNetworkConnected(AutomaticSleepOperationActivity.this)) {
                        updateUIState();
                    } else {
                        settingProvider.getSetting((settingModel, isSuccess, e) -> {
                            oldSetting = settingModel.automatic_operation_sleep_active;
                            oldAngleSetting = settingModel.autodriveDegreeSetting;
                            selectedAngleSetting = oldAngleSetting;
                            if (MultipleDeviceUtil.checkForceLogout(e)) {
                                MultipleDeviceUtil.sendBroadCast(AutomaticSleepOperationActivity.this);
                            }
                        });

                        nsManager = NSManager.getInstance(this, this);
                        checkNSDetail();
                    }
                }, UserLogin.getUserLogin().getId());

        Integer[] listDegreePolicy =  FormPolicyModel.getPolicy().getAutodriveDegreeSettingPrimitives();
        final ArrayList<Integer> listDegree = new ArrayList<>(Arrays.asList(listDegreePolicy));
        if(listDegree.isEmpty()){
            listDegree.add(0);
            listDegree.add(5);
            listDegree.add(10);
            listDegree.add(15);
        }

        final ArrayList<String> listDegreeLabels = new ArrayList<>();
        for (Integer degree:listDegree
             ) {
            listDegreeLabels.add(degree+"°");
        }
        spinDegree.setOnClickListener(view->{
            int selectedIndex = listDegree.indexOf(selectedAngleSetting);
            if (selectedIndex < 0) {
                selectedIndex = 0;
            }
            OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, option2, options3, v) -> {
                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                    DialogUtil.offlineDialog(AutomaticSleepOperationActivity.this, getApplicationContext());
                    spinDegree.setEnabled(false);
                    return;
                }
                selectedAngleSetting = listDegree.get(options1);
                txtDegree.setText(listDegreeLabels.get(options1));
                settingProvider.saveSetting("automatic_operation_bed_degree", String.valueOf(selectedAngleSetting), (issuccess) -> MultipleDeviceUtil.sendBroadCast(AutomaticSleepOperationActivity.this));
            }).setCyclic(false, false, false)
                    .setBackgroundId(0)
                    .setCancelText(LanguageProvider.getLanguage("UI000741C006"))
                    .setSelectOptions(selectedIndex)
                    .setSubmitText(LanguageProvider.getLanguage("UI000741C007"))
                    .setOutSideCancelable(false)
                    .build();

            pvOptions.setPicker(listDegreeLabels);
            pvOptions.show();
        });

        updateFirmwareVersion();
        updateUIState();
    }

    @SuppressLint("SetTextI18n")
    void setAutodriveAngleText(int degree){
        txtDegree.setText(degree+"°");
    }

    @Override
    protected void onResume() {
        super.onResume();
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        AlarmsQuizModule.run(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
        purgeBLE();
    }

    @Override
    public void onBackPressed() {
        if (nsManager != null && isNemuriScanInitiated && (oldSetting != switchOnOff.isChecked() || oldAngleSetting != selectedAngleSetting) && BluetoothUtil.isBluetoothEnable() && PermissionUtil.hasLocationPermissions(this) && PermissionUtil.isLocationServiceEnable(this)) {
            tryToConnectBLE();
        } else {
            finish();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_automatic_sleep_operation;
    }

    @Override
    protected boolean useToolbar() {
        return true;
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
            DialogUtil.createCustomYesNo(AutomaticSleepOperationActivity.this,
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
        runOnUiThread(() -> {
            if (nemuriScanModel != null) {
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
                LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_SUCCESS", "1", "", "UI000741");
                nsManager.notifyAutomaticOperationChange();
            } else {
                //LOG HERE NS_SET_SERVERID_FAILED
                LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_FAILED", "1", "", "UI000741");
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
            PermissionUtil.showLocationPermissionDialogAlert(AutomaticSleepOperationActivity.this, new PermissionUtil.PermissionDialogueListener() {
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
            PermissionUtil.showLocationServiceDialogAlert(AutomaticSleepOperationActivity.this, new PermissionUtil.LocationServiceDialogueListener() {
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
            if (!nemuriScanDetail.isBedExist()) {
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
            SettingModel settingModel = SettingModel.getFirst();
            applySettingToView(settingModel);

            boolean shouldEnabled = nemuriScanDetail.isBedExist() && NetworkUtil.isNetworkConnected(getApplicationContext());
            switchOnOff.setEnabled(shouldEnabled);
            spinDegree.setEnabled(shouldEnabled);
            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                DialogUtil.showOfflineDialog(this);
            }
        });
    }

    public void applySettingToView(SettingModel setting) {
        switchOnOff.setChecked(setting.automatic_operation_sleep_active);
        setAutodriveAngleText(setting.autodriveDegreeSetting);
    }

    private void checkNSDetail() {
        if (nemuriScanModel == null) {
            runOnUiThread(this::showProgress);
            settingProvider.noNSSetting((isSuccess) -> {
                runOnUiThread(() -> {
                    hideProgress();
                    applyNoNSUI();
                    DialogUtil.createSimpleOkDialogLink(AutomaticSleepOperationActivity.this, "", LanguageProvider.getLanguage("UI000610C030"),
                            LanguageProvider.getLanguage("UI000610C043"), (dialogInterface, i) -> {
                                Intent faqIntent = new Intent(AutomaticSleepOperationActivity.this, FaqActivity.class);
                                faqIntent.putExtra("ID_FAQ", "UI000610C043");
                                faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(faqIntent);
                                dialogInterface.dismiss();
                            }, LanguageProvider.getLanguage("UI000610C031"), (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            });
                });
            });
            return;
        }
        runOnUiThread(this::showProgress);
        NemuriScanUtil.fetchSpec(AutomaticSleepOperationActivity.this, AutomaticSleepOperationActivity.this);
    }

    private void applyNoNSUI() {
        runOnUiThread(() -> {
            applySettingToView(SettingModel.resetNSRelatedSettings());
            switchOnOff.setEnabled(false);
            spinDegree.setEnabled(false);
            setAllAlarms(getApplicationContext());
        });
    }

    private void purgeBLE() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }

    @SuppressLint("CheckResult")
    private void updateFirmwareVersion(){
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
                        refreshShowAngleSetting();
                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshShowAngleSetting();
                    }
                });
    }

    private void refreshShowAngleSetting(){
        runOnUiThread(() -> {
            FormPolicyModel formPolicyModel = FormPolicyModel.getPolicy();
            if(nemuriScanModel != null) {
                boolean isOldFW = nemuriScanModel.isOldFWVersion(formPolicyModel.asaOldVersionMajor, formPolicyModel.asaOldVersionMinor, formPolicyModel.asaOldVersionRevision);
                angleSettingContainer.setVisibility(isOldFW ? View.GONE : View.VISIBLE);
            }
        });
    }

}
