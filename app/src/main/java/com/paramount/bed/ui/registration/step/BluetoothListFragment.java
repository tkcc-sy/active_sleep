package com.paramount.bed.ui.registration.step;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.paramount.bed.BedApplication;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.interfaces.NSSettingDelegate;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.ble.pojo.NSWifiSetting;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.ServerModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.NemuriScanCheckResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.data.remote.service.NemuriScanService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.BaseFragment;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.BluetoothUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.FakeNemuriUtil;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NemuriScanUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.RecyclerItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class BluetoothListFragment extends BaseFragment implements NSScanDelegate, NSConnectionDelegate, NSSettingDelegate, DeviceTemplateProvider.DeviceTemplateFetchListener {
    //UI
    ArrayList<String> scannedDeviceName = new ArrayList<>();
    ArrayList<BluetoothDevice> scannedDeviceRefs = new ArrayList<>();
    @BindView(R.id.bluetoothList)
    RecyclerView bluetoothList;
    int connectionRetry = 0;
    BluetoothDevice selectedDevice;
    int MAXRETRY_CONN = 3;
    //BLE
    BluetoothListAdapter adapter;
    NSManager nsManager;
    private String currentServerId = "";
    public static NemuriScanModel selectedNemuriScan;
    private NemuriConstantsModel nemuriConstantsModel;
    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            nsManager.disconnectCurrentDevice();
            if(connectionRetry < MAXRETRY_CONN){
                new Handler().postDelayed(() -> {
                    connectionRetry+=1;
                    showProgress();
                    connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
                    nsManager.connectToDevice(selectedDevice, getContext());
                    connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, nemuriConstantsModel.nsConnectionTimeout  * 1000);
                },100);
            }else {
                connectionRetry = 0;
                parentActivity.runOnUiThread(() -> {
                    DialogUtil.createSimpleOkDialog(getActivity(), "",
                            LanguageProvider.getLanguage("UI000311C007"), LanguageProvider.getLanguage("UI000802C003"),
                            ((dialogInterface, i) -> {
                                isProgress = false;
                            }));
                    hideProgress();
                });
            }

        }
    };

    //Network
    NemuriScanService nemuriScanService;
    HomeService homeService;

    //Etc
    BaseActivity parentActivity;
    public static boolean isFromWifiList;
    public static Boolean isFromFakeNemuriUtil;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_bluetoothlist, container, false);
        ButterKnife.bind(this, view);
        isFromFakeNemuriUtil = false;
        nemuriScanService = ApiClient.getClient(getContext()).create(NemuriScanService.class);
        homeService = ApiClient.getClient(getContext()).create(HomeService.class);
        parentActivity = (BaseActivity) getActivity();
        StartFragment.isFromBluetoothList = true;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        bluetoothList.setLayoutManager(layoutManager);
        adapter = new BluetoothListAdapter(scannedDeviceName);
        bluetoothList.setAdapter(adapter);
        bluetoothList.addOnItemTouchListener(selectBLE());

        applyLocalization(view);
        getDeviceTemplate();
        isProgress = false;
        if(DisplayUtils.FONTS.bigFontStatus(getContext())) {
            TextView textview10=view.findViewById(R.id.textview10);
            textview10.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //just in case a NS is connected
        purgeBLE();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(nsManager != null){
            nsManager.setDelegate(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (!BluetoothUtil.isBluetoothEnable() && !isFromWifiList) {
                DialogUtil.createCustomYesNo(getActivity(), "", LanguageProvider.getLanguage("UI000802C009"),
                        LanguageProvider.getLanguage("UI000311C009"),
                        (dialogInterface, i) -> dialogInterface.dismiss(),
                        LanguageProvider.getLanguage("UI000802C007"), (dialogInterface, i) -> {
                            startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 100);
                            dialogInterface.dismiss();
                        });
            }
        } else if (requestCode == 200) {
            PermissionUtil.showLocationPermissionDialogAlert(getActivity(), new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    isProgress = false;
                }

                @Override
                public void onPermissionGranted() {
                    doScan();
                }
            });
        } else if (requestCode == 201) {
            PermissionUtil.showLocationServiceDialogAlert(getActivity(), new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                    isProgress = false;
                }

                @Override
                public void onEnabled() {
                    doScan();
                }
            });
        }

    }

    private void doScan() {
        if (isFromFakeNemuriUtil) {
            isFromFakeNemuriUtil = false;
            return;
        }
        if (BluetoothUtil.isBluetoothEnable()) {
            showProgress();
        }
        scannedDeviceName.clear();
        scannedDeviceRefs.clear();
        if (ServerModel.getHost().url.contains("assqcx")) {
            scannedDeviceName.add("Dummy BLE");
            scannedDeviceRefs.add(null);
        }
        adapter.notifyDataSetChanged();

        nsManager = NSManager.getInstance(getContext(), this);
        nsManager.startScan(getActivity());
    }

    @OnClick(R.id.btnRescan)
    public void doRescan() {
        doScan();
    }

    public boolean isProgress = false;
    public boolean isGettingSerialNumber = false;

    private RecyclerItemClickListener selectBLE() {
        return new RecyclerItemClickListener(getActivity(), bluetoothList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(BluetoothUtil.isBluetoothEnable()) {
                    if (isProgress) return;
                    isProgress = true;
                    if (position == 0 && ServerModel.getHost().url.contains("assqcx")) {
                        Intent intents = new Intent(view.getContext(), FakeNemuriUtil.class);
                        intents.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivityForResult(intents, 555);
                    } else {
                        selectedDevice = scannedDeviceRefs.get(position);
                        showProgress();
                        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
                        nsManager.connectToDevice(selectedDevice, getContext());
                        connectionRetry = 0;
                        connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, nemuriConstantsModel.nsConnectionTimeout * 1000);
                    }
                }else{
                    showBLESettingAlert();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @SuppressLint("CheckResult")
    private void verifyNemuriScanSN(String serialNumber) {
        showProgress();
        nemuriScanService.validateSerialNumber(serialNumber, ApiClient.LogData.getLogUserId(getActivity()), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<NemuriScanCheckResponse>() {
                    public void onSuccess(NemuriScanCheckResponse nemuriScanCheckResponse) {
                        boolean isSuccess = nemuriScanCheckResponse.getSuccess();
                        if (isSuccess) {
                            currentServerId = nemuriScanCheckResponse.getData().getServerId();
                            String serverUrl = NemuriConstantsModel.get().nsUrl;

                            BluetoothListFragment.selectedNemuriScan = new NemuriScanModel();
                            BluetoothListFragment.selectedNemuriScan.setServerGeneratedId(currentServerId);
                            BluetoothListFragment.selectedNemuriScan.setServerURL(serverUrl);
                            BluetoothListFragment.selectedNemuriScan.setSerialNumber(serialNumber);

                            if (nsManager.getCurrentDevice() != null) {
                                String macAddress = nsManager.getCurrentDevice().getAddress();
                                Logger.d("setting mac address looking for " + macAddress);
                                BluetoothListFragment.selectedNemuriScan.setMacAddress(macAddress);
                            } else {
                                BluetoothListFragment.selectedNemuriScan.setMacAddress("");
                            }


                            nsManager.getNSSpec();
                            connectionRetry = 0;
                            connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);

                        } else {
                            hideProgress();
                            DialogUtil.createSimpleOkDialog(parentActivity, "",
                                    LanguageProvider.getLanguage(nemuriScanCheckResponse.getMessage()));
                            nsManager.disconnectCurrentDevice();
                            isProgress = false;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        if (!NetworkUtil.isNetworkConnected(Objects.requireNonNull(getContext()))) {
                            DialogUtil.offlineDialog(parentActivity, getContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(parentActivity);
                        } else {
                            DialogUtil.serverFailed(parentActivity, "UI000802C125", "UI000802C126", "UI000802C127", "UI000802C128");
                        }
                        isProgress = false;
                    }
                });
    }

    private void getDeviceTemplate() {
//        showProgress();
        Integer bed_type = selectedNemuriScan == null ? null : selectedNemuriScan.getInfoType();
        if (UserLogin.isUserExist()) {
            DeviceTemplateProvider.getDeviceTemplate(getContext(), this, UserLogin.getUserLogin().getId(), bed_type);
        } else {
            DeviceTemplateProvider.getDeviceTemplate(getContext(), this, 0, bed_type);
        }
    }

    //DeviceTemplateProvider.DeviceTemplateFetchListener implementation
    @Override
    public void onDeviceTemplateFetched(List<DeviceTemplateMattressModel> mattressModels, List<DeviceTemplateBedModel> bedModels,
                                        List<DeviceTemplateMattressModel> mattressModelDefaults, List<DeviceTemplateBedModel> bedModelDefaults,
                                        NemuriConstantsModel nemuriConstantsModel) {
        hideProgress();
        this.nemuriConstantsModel = nemuriConstantsModel.getUnmanaged();
        this.MAXRETRY_CONN = nemuriConstantsModel.reconnectCount;
        if (!isRemoving() && getActivity() != null && !isDetached() && getView() != null) {
            if (!BluetoothUtil.isBluetoothEnable() && !isFromWifiList) {
                DialogUtil.createCustomYesNo(getActivity(), "", LanguageProvider.getLanguage("UI000802C009"),
                        LanguageProvider.getLanguage("UI000311C009"),
                        (dialogInterface, i) -> dialogInterface.dismiss(),
                        LanguageProvider.getLanguage("UI000802C007"), (dialogInterface, i) -> {
                            startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 100);
                            dialogInterface.dismiss();
                        });

            } else {
                doScan();
            }
        }
    }

    //NSScanDelegate Implementation
    @Override
    public void onStartScan() {
    }

    @Override
    public void onLocationPermissionDenied() {
        hideProgress();
        purgeBLE();
        PermissionUtil.showLocationPermissionDialogAlert(getActivity(), new PermissionUtil.PermissionDialogueListener() {
            @Override
            public void onPermissionCanceled(DialogInterface dialogInterface) {

            }

            @Override
            public void onPermissionGranted() {
                doScan();
            }
        });
    }

    @Override
    public void onLocationServiceDisabled() {
        hideProgress();
        purgeBLE();
        PermissionUtil.showLocationServiceDialogAlert(getActivity(), new PermissionUtil.LocationServiceDialogueListener() {
            @Override
            public void onDisabled(DialogInterface dialogInterface) {

            }

            @Override
            public void onEnabled() {
                doScan();
            }
        });
    }

    @Override
    public void onCancelScan() {
        hideProgress();
    }

    @Override
    public void onStopScan() {
        hideProgress();
    }

    @Override
    public void onConnectionEstablished() {
        parentActivity.runOnUiThread(() -> {
            showProgress();
            nsManager.getSerialNumber();
        });
    }

    @Override
    public void onDisconnect() {
        if(isProgress) {
            if (connectionRetry < MAXRETRY_CONN) {
                parentActivity.runOnUiThread(() -> new Handler().postDelayed(() -> {
                    connectionRetry += 1;
                    showProgress();
                    connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
                    nsManager.connectToDevice(selectedDevice, getContext());
                    connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, nemuriConstantsModel.nsConnectionTimeout * 1000);
                }, 100));
            } else {
                connectionRetry = 0;
                parentActivity.runOnUiThread(() -> {
                    DialogUtil.createSimpleOkDialog(getActivity(), "",
                            LanguageProvider.getLanguage("UI000311C007"), LanguageProvider.getLanguage("UI000802C003"),
                            ((dialogInterface, i) -> {
                                isProgress = false;
                            }));
                    hideProgress();
                });
            }
        }else{
            hideProgress();
        }
    }

    @Override
    public void onScanResult(ScanResult scanResult) {
        String deviceName = scanResult.getDevice().getName();
        if (deviceName != null && deviceName.startsWith("Active") && !scannedDeviceName.contains(deviceName)) {
            scannedDeviceName.add(deviceName);
            scannedDeviceRefs.add(scanResult.getDevice());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {
        isProgress = false;
        hideProgress();
        connectionRetry = 0;
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        parentActivity.runOnUiThread(() -> verifyNemuriScanSN(serialNumber));
    }

    @Override
    public void onNSSpecReceived(NSSpec spec) {

        BluetoothListFragment.selectedNemuriScan.setRevision(spec.getRevision());
        BluetoothListFragment.selectedNemuriScan.setMinor(spec.getMinor());
        BluetoothListFragment.selectedNemuriScan.setMajor(spec.getMajor());
        BluetoothListFragment.selectedNemuriScan.setLastFWUpdate(System.currentTimeMillis() / 1000);

        BluetoothListFragment.selectedNemuriScan.setBedExist(spec.isBedExist());
        BluetoothListFragment.selectedNemuriScan.setMattressExist(spec.isMattressExist());
        BluetoothListFragment.selectedNemuriScan.setInfoType(spec.getBedType());
        BluetoothListFragment.selectedNemuriScan.setLastUpdate(System.currentTimeMillis() / 1000);

        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        LogUserAction.sendNewLog(activity.userService, "DEVICE_REG_FW_MODE", String.valueOf(spec.isFWMode()), "", "UI000311");

        connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, nemuriConstantsModel.nsConnectionTimeout * 1000);
        nsManager.requestAuthentication(currentServerId);
    }

    @Override
    public void onAuthenticationFinished(int result) {
        hideProgress();
        connectionRetry = 0;
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        parentActivity.runOnUiThread(() -> {
            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
            if (activity != null) {
                if (result == NSConstants.NS_AUTH_REG_SUCCESS || result == NSConstants.NS_AUTH_SUCCESS) {
                    nsManager.getWifiSettings();
                } else {
                    //LOG HERE NS_SET_SERVERID_FAILED
                    LogUserAction.sendNewLog(activity.userService, "NS_SET_SERVERID_FAILED", "1", "", "UI000311");
                    DialogUtil.createSimpleOkDialog(getActivity(), "",
                            LanguageProvider.getLanguage("UI000311C007"), LanguageProvider.getLanguage("UI000802C003"),
                            ((dialogInterface, i) -> {
                                isProgress = false;
                            }));
                }
            }
        });
    }

    @Override
    public void onNSStatusReceived(int systemStatus, int bleStatus, int wifiStatus) {

    }

    @Override
    public void onConnectionStalled(int status) {

    }

    @Override
    public void onSetNSURLFinished(boolean isSuccess) {

    }

    @Override
    public void onGetWifiReceived(NSWifiSetting data) {
        //clean up garbage string from NS
        String tempSSID = data.getSsid();
        char[] tempSSIDCharArr = tempSSID.toCharArray();

        //remove invalid characters
        StringBuilder cleanSSID = new StringBuilder();
        for(char c:tempSSIDCharArr){
            if(c == '\u0000'){
                break;
            }
            cleanSSID.append(c);
        }
        tempSSID = cleanSSID.toString();

        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        if(activity != null) {
            if(!tempSSID.isEmpty() && !activity.isRegistration){
                String msg = LanguageProvider.getLanguage("UI000311C032").replace("%SSID_NAME%", tempSSID);
                if(data.getWifiCommStatus() == 4){
                    msg =  LanguageProvider.getLanguage("UI000311C026").replace("%SSID_NAME%", tempSSID);
                }
                String finalMsg = msg;
                parentActivity.runOnUiThread(() -> DialogUtil.createCustomYesNo(parentActivity, "", finalMsg, LanguageProvider.getLanguage("UI000311C028"), (dialog, which) -> {
                    LogUserAction.sendNewLog(activity.userService, "NS_WIFI_SKIPPING_CANCELLED", activity.isRegistration?"true":"false", "", "UI000311");
                    activity.go(RegistrationStepActivity.FRAGMENT_CONNECTION_OPTION);
                    isProgress = false;
                }, LanguageProvider.getLanguage("UI000311C027"), (dialog, which) -> {
                    LogUserAction.sendNewLog(activity.userService, "NS_WIFI_SKIPPING", activity.isRegistration?"true":"false", "", "UI000311");
                    new Handler().postDelayed(() ->
                            parentActivity.runOnUiThread(() -> DialogUtil.createSimpleOkDialog(parentActivity, "",
                                    LanguageProvider.getLanguage("UI000311C005"),
                                    LanguageProvider.getLanguage("UI000802C003"), (dialogInterface, i) -> {
                                        addNS(activity);
                                    })),1000); //delay for 1s to wait out dialog dismissed
                }));
            }else{
                LogUserAction.sendNewLog(activity.userService, "NS_WIFI_NOT_SKIPPING", activity.isRegistration?"true":"false", "", "UI000311");

                parentActivity.runOnUiThread(() -> DialogUtil.createSimpleOkDialog(parentActivity, "",
                        LanguageProvider.getLanguage("UI000311C005"),
                        LanguageProvider.getLanguage("UI000802C003"), (dialogInterface, i) -> {
                            activity.go(RegistrationStepActivity.FRAGMENT_CONNECTION_OPTION);
                            isProgress = false;
                        }));
            }
        }
    }

    private static void setSerialNumber(String sn_nemuri_scan) {
        //#region Register SN
        SharedPreferences sn = BedApplication.getsApplication().getSharedPreferences("SN_NEMURI_SCAN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sn.edit();
        editor.putString("SERIAL_NUMBER", sn_nemuri_scan);
        editor.apply();
        //#endregion
    }

    private void addNS(RegistrationStepActivity activity){
        showProgress();
        NemuriScanUtil.register(getActivity(), getActivity(), UserLogin.getUserLogin().getId(), BluetoothListFragment.selectedNemuriScan.getSerialNumber(),BluetoothListFragment.selectedNemuriScan.getMajor(),BluetoothListFragment.selectedNemuriScan.getMinor(),BluetoothListFragment.selectedNemuriScan.getRevision(),
                new NemuriScanUtil.NemuriScanRegisterListener() {
                    @Override
                    public void onNemuriScanRegistered(String date) {
                        if (!parentActivity.isFinishing()) {
                            parentActivity.runOnUiThread(() -> {
                                hideProgress();
                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date lastConnection;
                                try {
                                    lastConnection = dateFormat.parse(date);
                                    BluetoothListFragment.selectedNemuriScan.setLastUpdate(lastConnection.getTime() / 1000);
                                } catch (ParseException e) {
                                    BluetoothListFragment.selectedNemuriScan.setLastUpdate(System.currentTimeMillis() / 1000);
                                }

                                BluetoothListFragment.selectedNemuriScan.insert();
                                setSerialNumber(BluetoothListFragment.selectedNemuriScan.getSerialNumber());

                                BluetoothListFragment.selectedNemuriScan = null;
                                purgeBLE();
                                activity.go(RegistrationStepActivity.FINISH_FLOW);
                            });
                        }
                    }

                    @Override
                    public void onNemuriScanRegisterFailed() {
                        if (!parentActivity.isFinishing()) {
                            parentActivity.runOnUiThread(() -> {
                                hideProgress();
                                BaseActivity.isLoading = false;
                                parentActivity.onBackPressed();
                            });
                        }
                    }
                });
    }

    @Override
    public void onSetWifiFinished(boolean isSuccess) {

    }

    //NSScanDelege Implementation END

    //MARK : NSBaseDelegate
    @Override
    public void onCommandWritten(NSOperation command) {
        //TODO : HANDLE ILLEGAL BLE OP
        if (command.getCommandCode() == NSOperation.FREE_DECREASE_COMBI.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_MATTRESS_POSITION.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_BED_SETTING.getCommandCode()) {
            //DISCONNECT & SHOW ILLEGAL OPERATION ALERT
            purgeBLE();
            DialogUtil.createCustomYesNo(getActivity(),
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
    //MARK END : NSBaseDelegate

    @Override
    public void onPause() {
        super.onPause();
        purgeBLE();
    }

    private void purgeBLE() {
        isProgress = false;
        connectionRetry = 0;
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getContext().registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
    }

    @Override
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(gpsReceiver);
    }


    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                if (PermissionUtil.hasLocationPermissions(getActivity())) {
                    IOSDialogRight.Dismiss();
                    onLocationPermissionDenied();
                    return;
                }
                if (PermissionUtil.isLocationServiceEnable(getActivity())) {
                    IOSDialogRight.Dismiss();
                    onLocationServiceDisabled();
                    return;
                }
            }
        }
    };

    private void showBLESettingAlert(){
        parentActivity.runOnUiThread(() -> DialogUtil.createCustomYesNo(parentActivity, "", LanguageProvider.getLanguage("UI000802C009"),
                LanguageProvider.getLanguage("UI000311C009"),
                (dialogInterface, i) -> {dialogInterface.dismiss();hideProgress();},
                LanguageProvider.getLanguage("UI000802C007"), (dialogInterface, i) -> {
                    startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 100);
                    dialogInterface.dismiss();
                    hideProgress();
                }));
    }
}


