package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toolbar;

import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.NemuriScanCheckResponse;
import com.paramount.bed.data.remote.service.NemuriScanService;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.step.BluetoothListAdapter;
import com.paramount.bed.util.BluetoothUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.RecyclerItemClickListener;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class UpdateFirmwareScanActivity extends BaseActivity implements NSScanDelegate, NSConnectionDelegate, DeviceTemplateProvider.DeviceTemplateFetchListener {

    //UI
    ArrayList<String> scannedDeviceName = new ArrayList<>();
    ArrayList<BluetoothDevice> scannedDeviceRefs = new ArrayList<>();

    @BindView(R.id.bluetoothList)
    RecyclerView bluetoothList;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @OnClick(R.id.btnRescan)
    public void doRescan() {
        if(isNSRegistered) {
            connectToKnownDevice();
        }else {
            doScan();
        }
    }

    BluetoothDevice selectedDevice;

    //BLE
    BluetoothListAdapter adapter;
    NSManager nsManager;
    private String currentServerId = "";
    public static NemuriScanModel selectedNemuriScan;
    private NemuriConstantsModel nemuriConstantsModel = NemuriConstantsModel.get().getUnmanaged();
    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            nsManager.disconnectCurrentDevice();

            runOnUiThread(() -> {
                DialogUtil.createYesNoDialogLink(UpdateFirmwareScanActivity.this, "", LanguageProvider.getLanguage("UI000802C025"),
                        LanguageProvider.getLanguage("UI000802C178"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(UpdateFirmwareScanActivity.this, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000802C178");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        },
                        LanguageProvider.getLanguage("UI000802C026"), (dialogInterface, i) -> showLocationPermissionDialogAlert(),
                        LanguageProvider.getLanguage("UI000802C166"), (dialogInterface, i) -> {
                            if(isNSRegistered){
                                onBackPressed();
                            }else {
                                dialogInterface.dismiss();
                                hideProgress();
                            }
                        });
            });
        }
    };

    NemuriScanService nemuriScanService;
    public UserService userService;
    private boolean isNSRegistered = false;
    private NemuriScanModel registeredNemuriScan = null;
    private boolean isFirstLaunch = true;

    private RecyclerItemClickListener selectBLE() {
        return new RecyclerItemClickListener(this, bluetoothList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(BluetoothUtil.isBluetoothEnable()) {
                    showProgress();
                    selectedDevice = scannedDeviceRefs.get(position);
                    connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
                    nsManager.connectToDevice(selectedDevice, UpdateFirmwareScanActivity.this);
                    connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, nemuriConstantsModel.nsConnectionTimeout * 1000);
                }else{
                    showBLESettingAlert();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000311C001"));
        setActionBar(toolbar);

        userService = ApiClient.getClient(this).create(UserService.class);
        nemuriScanService = ApiClient.getClient(this).create(NemuriScanService.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        bluetoothList.setLayoutManager(layoutManager);
        adapter = new BluetoothListAdapter(scannedDeviceName);
        bluetoothList.setAdapter(adapter);
        bluetoothList.addOnItemTouchListener(selectBLE());

        nsManager = NSManager.getInstance(this, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
        nsManager.setDelegate(this);

        NemuriScanModel currentNS = NemuriScanModel.get();
        isNSRegistered = currentNS != null;
        if(currentNS != null){
            registeredNemuriScan = currentNS.getUnmanaged();
        }

        isIntentionalDC = true;
        nsManager.disconnectCurrentDevice();
        nsManager.stopScan();

        getDeviceTemplate();

        IOSDialogRight.Dismiss();
    }


    @Override
    public void onBackPressed() {
        isIntentionalDC = true;
        nsManager.disconnectCurrentDevice();
        nsManager.stopScan();

        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(gpsReceiver);
    }

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if (PermissionUtil.hasLocationPermissions(UpdateFirmwareScanActivity.this)) {
                    IOSDialogRight.Dismiss();
                    showLocationPermissionDialogAlert();
                    return;
                }
                if (PermissionUtil.isLocationServiceEnable(UpdateFirmwareScanActivity.this)) {
                    IOSDialogRight.Dismiss();
                    showLocationServiceDialogAlert();
                    return;
                }
            }
        }
    };

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_update_firmware_scan;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //just in case a NS is connected
        purgeBLE();
    }

    @Override
    protected void onPause() {
        super.onPause();
        purgeBLE();
        overridePendingTransition(0, 0);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (!BluetoothUtil.isBluetoothEnable()) {
               showBLESettingAlert();
            }
        } else if (requestCode == 200) {
            PermissionUtil.showLocationPermissionDialogAlert(this, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                }

                @Override
                public void onPermissionGranted() {
                    if(isNSRegistered) {
                        connectToKnownDevice();
                    }else {
                        doScan();
                    }
                }
            });
        } else if (requestCode == 201) {
            PermissionUtil.showLocationServiceDialogAlert(this, new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                }

                @Override
                public void onEnabled() {
                    if(isNSRegistered) {
                        connectToKnownDevice();
                    }else {
                        doScan();
                    }
                }
            });
        }else if (requestCode == 301 && resultCode == 301) { ///from registered NS
            onBackPressed();
        }

    }

    @Override
    public void onConnectionEstablished() {
        runOnUiThread(() -> {
            showProgress();
            nsManager.getSerialNumber();
        });
    }

    @Override
    public void onDisconnect() {
        if(isIntentionalDC){
            isIntentionalDC = false;
            return;
        }
        NemuriScanModel currentNemuriscanModel;
        if(isNSRegistered){
            currentNemuriscanModel = registeredNemuriScan;
        }else{
            currentNemuriscanModel = selectedNemuriScan;
        }

        if(currentNemuriscanModel != null){
            showLocationPermissionDialogAlert();
        }else{
            //TODO : CHECK OPERATION
            hideProgress();
        }
    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if(isNSRegistered){
            nsManager.getNSSpec();
        }else {
            runOnUiThread(() -> verifyNemuriScanSN(serialNumber));
        }
    }

    @Override
    public void onAuthenticationFinished(int result) {
        runOnUiThread(() -> {
            if (result == NSConstants.NS_AUTH_SUCCESS || result == NSConstants.NS_AUTH_REG_SUCCESS) {
                //LOG HERE NS_SET_SERVERID_SUCCESS
                LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_SUCCESS", "1", "", "UI000741");
                nsManager.getNSSpec();
            } else {
                isIntentionalDC = true;
                nsManager.disconnectCurrentDevice();
                DialogUtil.createSimpleOkDialog(UpdateFirmwareScanActivity.this, "",
                        LanguageProvider.getLanguage("UI000311C007"), LanguageProvider.getLanguage("UI000802C003"),
                        ((dialogInterface, i) -> {
                        }));
                hideProgress();
            }
        });
    }

    @Override
    public void onNSStatusReceived(int systemStatus, int bleStatus, int wifiStatus) {

    }

    @Override
    public void onNSSpecReceived(NSSpec spec) {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);

        if(isNSRegistered && registeredNemuriScan.isFWUpdateFailed()){
            proceedUpdate();
            return;
        }

        if(spec.isFWMode()){
            proceedUpdate();
            return;
        }

        if(needsFWUpdate(spec)){
            proceedUpdate();
            return;
        }

        hideProgress();
        runOnUiThread(() -> DialogUtil.createSimpleOkDialog(UpdateFirmwareScanActivity.this, "", LanguageProvider.getLanguage("UI000311C030"),
                LanguageProvider.getLanguage("UI000311C031"), (dialog, which) -> onBackPressed()));
    }

    @Override
    public void onConnectionStalled(int status) {
        nsManager.disconnectCurrentDevice();
    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onLocationPermissionDenied() {
        hideProgress();
        purgeBLE();
        PermissionUtil.showLocationPermissionDialogAlert(this, new PermissionUtil.PermissionDialogueListener() {
            @Override
            public void onPermissionCanceled(DialogInterface dialogInterface) {

            }

            @Override
            public void onPermissionGranted() {
                if(isNSRegistered) {
                    connectToKnownDevice();
                }else {
                    doScan();
                }
            }
        });
    }

    @Override
    public void onLocationServiceDisabled() {
        hideProgress();
        purgeBLE();
        PermissionUtil.showLocationServiceDialogAlert(this, new PermissionUtil.LocationServiceDialogueListener() {
            @Override
            public void onDisabled(DialogInterface dialogInterface) {

            }

            @Override
            public void onEnabled() {
                if(isNSRegistered) {
                    connectToKnownDevice();
                }else {
                    doScan();
                }
            }
        });
    }

    @Override
    public void onCancelScan() {
        hideProgress();
    }

    @Override
    public void onStopScan() {
        if(!isNSRegistered) {
            hideProgress();
        }
    }

    @Override
    public void onScanResult(ScanResult scanResult) {
        if(isNSRegistered){
            String savedMac = registeredNemuriScan.getMacAddress();
            String targetMac = scanResult.getDevice().getAddress();
            if (scanResult.getDevice().getName() != null) {
                Logger.v("RealtimeMonitor : Scanning BLE, looking for " + savedMac + " trying " + targetMac + " " + scanResult.getDevice().getName());
            }
            if (savedMac.equalsIgnoreCase(targetMac)) {
                nsManager.connectToDevice(scanResult.getDevice(), this);
                nsManager.stopScan();
            }
        }else {
            String deviceName = scanResult.getDevice().getName();
            if (deviceName != null && deviceName.startsWith("Active") && !scannedDeviceName.contains(deviceName)) {
                scannedDeviceName.add(deviceName);
                scannedDeviceRefs.add(scanResult.getDevice());
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCommandWritten(NSOperation command) {
        //TODO : HANDLE ILLEGAL BLE OP
        if (command.getCommandCode() == NSOperation.FREE_DECREASE_COMBI.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_MATTRESS_POSITION.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_BED_SETTING.getCommandCode()) {
            //DISCONNECT & SHOW ILLEGAL OPERATION ALERT
            purgeBLE();
            DialogUtil.createCustomYesNo(this,
                    "",
                    LanguageProvider.getLanguage("UI000802C191"),
                    LanguageProvider.getLanguage("UI000802C193"),
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    },
                    LanguageProvider.getLanguage("UI000802C192"),
                    (dialogInterface, i) -> {
                        //retry
                        dialogInterface.dismiss();
                    }
            );
        }
    }

    //DeviceTemplateProvider.DeviceTemplateFetchListener implementation
    @Override
    public void onDeviceTemplateFetched(List<DeviceTemplateMattressModel> mattressModels, List<DeviceTemplateBedModel> bedModels,
                                        List<DeviceTemplateMattressModel> mattressModelDefaults, List<DeviceTemplateBedModel> bedModelDefaults,
                                        NemuriConstantsModel nemuriConstantsModel) {
        this.nemuriConstantsModel.copyValue(nemuriConstantsModel);
        runOnUiThread(() -> showLocationPermissionDialogAlert());
    }

    public void showLocationPermissionDialogAlert() {
        PermissionUtil.showLocationPermissionDialogAlert(this, new PermissionUtil.PermissionDialogueListener() {
            @Override
            public void onPermissionCanceled(DialogInterface dialogInterface) {
                finish();
                hideProgress();
                purgeBLE();
            }

            @Override
            public void onPermissionGranted() {
                if(isNSRegistered) {
                    connectToKnownDevice();
                }else {
                    doScan();
                }
            }
        });
    }

    public void showLocationServiceDialogAlert() {
        PermissionUtil.showLocationServiceDialogAlert(this, new PermissionUtil.LocationServiceDialogueListener() {
            @Override
            public void onDisabled(DialogInterface dialogInterface) {
                finish();
                hideProgress();
                purgeBLE();
            }

            @Override
            public void onEnabled() {
                if(isNSRegistered) {
                    connectToKnownDevice();
                }else {
                    doScan();
                }
            }
        });
    }

    public void connectToKnownDevice() {
        //BLE setup
        tryToConnectBLE();
    }
    private void tryToConnectBLE() {
        showProgress();
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, nemuriConstantsModel.nsConnectionTimeout * 1000);
        nsManager.startScan(this);
    }

    @SuppressLint("CheckResult")
    private void verifyNemuriScanSN(String serialNumber) {
        showProgress();
        nemuriScanService.validateSerialNumber(serialNumber, ApiClient.LogData.getLogUserId(this), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<NemuriScanCheckResponse>() {
                    public void onSuccess(NemuriScanCheckResponse nemuriScanCheckResponse) {
                        boolean isSuccess = nemuriScanCheckResponse.getSuccess();
                        if (isSuccess) {
                            currentServerId = nemuriScanCheckResponse.getData().getServerId();
                            String serverUrl = NemuriConstantsModel.get().nsUrl;

                            selectedNemuriScan = new NemuriScanModel();
                            selectedNemuriScan.setServerGeneratedId(currentServerId);
                            selectedNemuriScan.setServerURL(serverUrl);
                            selectedNemuriScan.setSerialNumber(serialNumber);

                            if (nsManager.getCurrentDevice() != null) {
                                String macAddress = nsManager.getCurrentDevice().getAddress();
                                Logger.d("setting mac address looking for " + macAddress);
                                selectedNemuriScan.setMacAddress(macAddress);
                            } else {
                                selectedNemuriScan.setMacAddress("");
                            }

                            nsManager.requestAuthentication(currentServerId);
                        } else {
                            hideProgress();
                            DialogUtil.createSimpleOkDialog(UpdateFirmwareScanActivity.this, "",
                                    LanguageProvider.getLanguage(nemuriScanCheckResponse.getMessage()));
                            isIntentionalDC = true;
                            nsManager.disconnectCurrentDevice();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        if (!NetworkUtil.isNetworkConnected(Objects.requireNonNull(UpdateFirmwareScanActivity.this))) {
                            DialogUtil.offlineDialog(UpdateFirmwareScanActivity.this, UpdateFirmwareScanActivity.this);
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(UpdateFirmwareScanActivity.this);
                        } else {
                            DialogUtil.serverFailed(UpdateFirmwareScanActivity.this, "UI000802C125", "UI000802C126", "UI000802C127", "UI000802C128");
                        }
                        isIntentionalDC = true;
                        nsManager.disconnectCurrentDevice();
                    }
                });
    }

    private void getDeviceTemplate() {
        showProgress();

        NemuriScanModel currentNemuriscanModel = isNSRegistered ? registeredNemuriScan : selectedNemuriScan;
        Integer bedType = currentNemuriscanModel == null ? null : currentNemuriscanModel.getInfoType();
        if (UserLogin.isUserExist()) {
            DeviceTemplateProvider.getDeviceTemplate(this, this, UserLogin.getUserLogin().getId(), bedType);
        } else {
            DeviceTemplateProvider.getDeviceTemplate(this, this, 0, bedType);
        }
    }

    private void doScan() {
        if (BluetoothUtil.isBluetoothEnable()) {
            showProgress();
            nsManager.startScan(this);
        }else{
            showBLESettingAlert();
        }
        scannedDeviceName.clear();
        scannedDeviceRefs.clear();
        adapter.notifyDataSetChanged();

    }

    private void purgeBLE() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }

    private void proceedUpdate(){
        isIntentionalDC = true;
        nsManager.disconnectCurrentDevice();

        Intent intent = new Intent(this, UpdateFirmwareIntroActivity.class);
        intent.putExtra("isManualUpdate",isNSRegistered);
        intent.putExtra("isFreedNS",!isNSRegistered);
        startActivityForResult(intent,301);
    }

    private boolean needsFWUpdate(NSSpec spec){
        int localRev = spec.getRevision();
        int localMin =  spec.getMinor();
        int localMaj =  spec.getMajor();
        boolean needsUpdate = false;

        if(localMaj < BuildConfig.FIRMWARE_MAJOR){
            needsUpdate = true;
        }else if(localMaj <= BuildConfig.FIRMWARE_MAJOR &&
                localMin < BuildConfig.FIRMWARE_MINOR){
            needsUpdate = true;
        }else if(localMaj <= BuildConfig.FIRMWARE_MAJOR &&
                localMin <= BuildConfig.FIRMWARE_MINOR &&
                localRev < BuildConfig.FIRMWARE_REVISION){
            needsUpdate = true;
        }
        return needsUpdate && !(localRev == 0 && localMin == 0 && localMaj == 0);
    }

    boolean isIntentionalDC = false;

    private void showBLESettingAlert(){
        runOnUiThread(() -> DialogUtil.createCustomYesNo(UpdateFirmwareScanActivity.this, "", LanguageProvider.getLanguage("UI000802C009"),
                LanguageProvider.getLanguage("UI000311C009"),
                (dialogInterface, i) -> {dialogInterface.dismiss();hideProgress();},
                LanguageProvider.getLanguage("UI000802C007"), (dialogInterface, i) -> {
                    startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 100);
                    dialogInterface.dismiss();
                    hideProgress();
                }));
    }
}
