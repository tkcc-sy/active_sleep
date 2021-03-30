package com.paramount.bed.ui.registration.step;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orhanobut.logger.Logger;
import com.paramount.bed.BedApplication;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSOperationDelegate;
import com.paramount.bed.ble.interfaces.NSSettingDelegate;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.ble.pojo.NSWifiSetting;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.NemuriScanTemporaryModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.main.FaqActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.NemuriScanUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.paramount.bed.ui.registration.RegistrationStepActivity.mInstance;

public class WifiConnectFragment extends BLEFragment implements NSConnectionDelegate, NSSettingDelegate, DeviceTemplateProvider.DeviceTemplateFetchListener, NSOperationDelegate {
    private int WIFI_STATUS_POLLING_INTERVAL = NemuriConstantsModel.get().wifiStatusPollingInterval;
    private int WIFI_SETTING_TIMEOUT = NemuriConstantsModel.get().wifiSettingTimeout;

    private NSSpec.BED_MODEL currentBedModel;
    private boolean isBedExist;
    private boolean isMattressExist;
    private int majorVer;
    private int minorVer;
    private int revisionVer;
    private CircularProgressView progressView;
    private Handler pollConnectionStatusHandler = new Handler();
    private Runnable pollConnectionStatusTimer = new Runnable() {
        public void run() {
            nsManager.getNSStatus();
            pollConnectionStatusHandler.postDelayed(this, WIFI_STATUS_POLLING_INTERVAL * 1000);
        }
    };
    private boolean isFinished = false;
    private Handler wifiSettingTimeoutHandler = new Handler();
    private Runnable wifiSettingTimeoutTimer = () -> {
        nsManager.stopScan();
        Logger.e("WIFI SETTING TIMEOUT TRIGGERED");
        purgeBLE();
        Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(this::stopLoadingIndicator);
                activity.runOnUiThread(() ->
                        {
                            LogUserAction.sendNewLog(mInstance.userService, "NS_SET_WIFI_FAILED", "1", "", "UI000340");
                            DialogUtil.createYesNoDialogLink(getActivity(), "", LanguageProvider.getLanguage("UI000311C011"),
                                    LanguageProvider.getLanguage("UI000802C161"), (dialogInterface, i) -> {
                                        Intent faqIntent = new Intent(RegistrationStepActivity.mInstance, FaqActivity.class);
                                        faqIntent.putExtra("ID_FAQ", "UI000802C161");
                                        faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(faqIntent);
                                        dialogInterface.dismiss();
                                    }, LanguageProvider.getLanguage("UI000311C012"), (dialogInterface, i) -> {
                                        nsManager.disconnectCurrentDevice();
                                        initiateWifiProcess();
                                    },
                                    LanguageProvider.getLanguage("UI000311C013"), (dialogInterface, i) -> {
                                        //cancel/back
                                        new Handler().postDelayed(() -> {
                                            if (getActivity() != null) {
                                                getActivity().onBackPressed();
                                            }
                                        }, 100);
                                    }
                            );
                        }
                );
        }
    };

    private boolean isConnectionStalled = false;
    private boolean isWaitingForConnection = false;
    private boolean isReconnectingAfterSetWifi = false;
    private boolean isAuthFromSetUrl = false;
    public boolean isWifiOnly = false;
    private int reconnectPollMaxDuration = NemuriConstantsModel.get().reconnectPollMaxDuration;
    private int reconnectPollInterval = NemuriConstantsModel.get().reconnectPollInterval;
    private int reconnectPollSpentDuration = 0;

    private Handler reconnectTimerHandler = new Handler();
    private Runnable reconnectTimer = new Runnable() {
        @Override
        public void run() {
            Logger.e("CONNECT AFTER SET WIFI TIMEOUT TRIGGERED");
            if (!isWaitingForConnection) {
                return;
            }
            reconnectPollSpentDuration += reconnectPollInterval;
            if (reconnectPollSpentDuration > reconnectPollMaxDuration) {
                reconnectTimerHandler.removeCallbacks(this);
                LogUserAction.sendNewLog(mInstance.userService, "NS_SERVER_CONNECTION_FAILED", "1", "","UI000340");
                DialogUtil.createYesNoDialogLink(getActivity(), "", LanguageProvider.getLanguage("UI000311C010"),
                        LanguageProvider.getLanguage("UI000310C009"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(RegistrationStepActivity.mInstance, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000310C009");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        }, LanguageProvider.getLanguage("UI000311C008"), (dialogInterface, i) -> {
                            //retry
                            tryToConnectSilently();

                        }, LanguageProvider.getLanguage("UI000311C009"), (dialogInterface, i) -> {
                            //cancel/back
                            new Handler().postDelayed(() -> {
                                if (getActivity() != null) {
                                    getActivity().onBackPressed();
                                }
                            }, 100);
                        });
            } else {
                tryToConnectWithoutTimeout();
                isReconnectingAfterSetWifi = true;
            }
        }
    };
    BaseActivity parentActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_wificonnect, container, false);
        progressView = view.findViewById(R.id.progress_view);
        applyLocalization(view);
        parentActivity = (BaseActivity) getActivity();
        if (UserLogin.isUserExist()) {
            DeviceTemplateProvider.getDeviceTemplate(getContext(), this, UserLogin.getUserLogin().getId());
        } else {

            DeviceTemplateProvider.getDeviceTemplate(getContext(), this, 0);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        purgeBLE();
    }

    @Override
    public void onConnectionStalled(int status) {
        Logger.e("connection stalled " + status);
        isConnectionStalled = true;
        Logger.e("reconnecting on stalled conn" + status);
        this.tryToConnectSilently();
        parentActivity.runOnUiThread(() -> new Handler( Looper.getMainLooper()).postDelayed(this::tryToConnectSilently, 200));
        super.onConnectionStalled(status);
    }

    @Override
    public void onDisconnect() {
        super.onDisconnect();
        if(isConnectionStalled){
            Logger.e("disconnect cancelled due to stalled connection ");
            isConnectionStalled = false;
            return;
        }

        if (isIntentionalDC && isWaitingForConnection) {
            //WiFi Disconnect
            isIntentionalDC = false;
            reconnectTimerHandler.postDelayed(reconnectTimer, reconnectPollInterval * 1000);
        } else if(!isIntentionalDC){
            Logger.e("DISCONNECT TRIGGERED");
            showBLEReconnectAlert();
        }
    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {
        RegistrationStepActivity.SERIAL_NUMBER = serialNumber;
        nsManager.getNSSpec();
    }

    @Override
    public void onAuthenticationFinished(int result) {
        parentActivity.runOnUiThread(() -> {
            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
            if (activity != null) {
                if (result == NSConstants.NS_AUTH_REG_SUCCESS || result == NSConstants.NS_AUTH_SUCCESS) {
                    if (isWaitingForConnection) {
                        //LOG HERE NS_SET_SERVERID_SUCCESS
                        LogUserAction.sendNewLog(activity.userService, "NS_SET_SERVERID_SUCCESS", "1", "","UI000340");
                        isWaitingForConnection = false;
                        isIntentionalDC = false;
                        reconnectTimerHandler.removeCallbacks(reconnectTimer);
                        pollConnectionStatusHandler.post(pollConnectionStatusTimer);
                    } else {
                        if (isAuthFromSetUrl) {
                            isAuthFromSetUrl = false;
                            NSWifiSetting wifiSetting = new NSWifiSetting(ManualWifiFragment.chosenSSID, ManualWifiFragment.chosenEncryption, ManualWifiFragment.chosenPassword);
                            nsManager.setWifiSetting(wifiSetting);
                            return;
                        }

                        //LOG HERE NS_SET_SERVERID_SUCCESS
                        LogUserAction.sendNewLog(activity.userService, "NS_SET_SERVERID_SUCCESS", "1", "","UI000340");
                        String url = BluetoothListFragment.selectedNemuriScan.isIntranet() ? NemuriConstantsModel.get().getNsUrlIp() : NemuriConstantsModel.get().getNsUrl();


                        parentActivity.runOnUiThread(() -> new Handler( Looper.getMainLooper()).postDelayed(()->nsManager.setServerURL(url), 1000));
                    }
                } else {
                    Logger.e("AUTH FAILED TRIGGERED");
                    //LOG HERE NS_SET_SERVERID_FAILED
                    disableWifi("AUTH_FAILED");
                    LogUserAction.sendNewLog(activity.userService, "NS_SET_SERVERID_FAILED", "1", "","UI000340");
                    //TODO : proper faq id
                    DialogUtil.createYesNoDialogLink(getActivity(), "", LanguageProvider.getLanguage("UI000311C010"),
                            LanguageProvider.getLanguage("UI000310C009"), (dialogInterface, i) -> {
                                Intent faqIntent = new Intent(getActivity(), FaqActivity.class);
                                faqIntent.putExtra("ID_FAQ", "UI000310C009");
                                faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(faqIntent);
                            },
                            LanguageProvider.getLanguage("UI000311C008"), (dialogInterface, i) -> nsManager.requestAuthentication(BluetoothListFragment.selectedNemuriScan.getServerGeneratedId()),
                            LanguageProvider.getLanguage("UI000311C009"), (dialogInterface, i) -> {
                                //cancel/back
                                new Handler().postDelayed(() -> {
                                    if (getActivity() != null) {
                                        getActivity().onBackPressed();
                                    }
                                }, 100);
                            }
                    );
                }
            }
        });
    }

    @Override
    public void onSetNSURLFinished(boolean isSuccess) {
        if (isSuccess) {
            if (isWaitingForConnection) {
                isWaitingForConnection = false;
                isIntentionalDC = false;
                reconnectTimerHandler.removeCallbacks(reconnectTimer);
                pollConnectionStatusHandler.post(pollConnectionStatusTimer);
            } else {
                mInstance.runOnUiThread(() -> {
                    isAuthFromSetUrl = true;
                    Logger.d("setWifiAuthDelay " + NemuriConstantsModel.get().setWifiAuthDelay);
                    new Handler().postDelayed(() -> {
                        nsManager.requestAuthentication(BluetoothListFragment.selectedNemuriScan.getServerGeneratedId());
                    }, (long) (0.6 * 1000)); //todo remove hc
                });

            }
        } else {
            disableWifi("SET_URL_FAILED");
            //TODO : proper faq id
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtil.createYesNoDialogLink(getActivity(), "", LanguageProvider.getLanguage("UI000311C010"),
                            LanguageProvider.getLanguage("UI000310C009"), (dialogInterface, i) -> {
                                Intent faqIntent = new Intent(getActivity(), FaqActivity.class);
                                faqIntent.putExtra("ID_FAQ", "UI000310C009");
                                faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(faqIntent);
                            },
                            LanguageProvider.getLanguage("UI000311C008"), (dialogInterface, i) -> nsManager.requestAuthentication(BluetoothListFragment.selectedNemuriScan.getServerGeneratedId()),
                            LanguageProvider.getLanguage("UI000311C009"), (dialogInterface, i) -> {
                                //cancel/back
                                new Handler().postDelayed(() -> {
                                    if (getActivity() != null) {
                                        getActivity().onBackPressed();
                                    }
                                }, 100);
                            }
                    );
                }
            });
        }
    }

    @Override
    public void onGetWifiReceived(NSWifiSetting data) {
    }

    @Override
    public void onSetWifiFinished(boolean isSuccess) {
        if (isSuccess) {
            isWaitingForConnection = true;
            isIntentionalDC = true;
            try {
                //LOG HERE NS_SET_WIFI_SUCCESS
                LogUserAction.sendNewLog(mInstance.userService, "NS_SET_WIFI_SUCCESS", "1", "","UI000340");
            } catch (Exception ignored) {

            }
        } else {
            try {
                //LOG HERE NS_SET_WIFI_FAILED
                disableWifi("SET_WIFI_FAILED");
                LogUserAction.sendNewLog(mInstance.userService, "NS_SET_WIFI_FAILED", "1", "","UI000340");
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public void onNSStatusReceived(int systemStatus, int bleStatus, int wifiStatus) {
        Logger.d("NSManager system status " + systemStatus + " ble status " + bleStatus + " wifi status " + wifiStatus);
        wifiSettingTimeoutHandler.removeCallbacks(wifiSettingTimeoutTimer);
        pollConnectionStatusHandler.removeCallbacks(pollConnectionStatusTimer);
        mInstance.runOnUiThread(this::stopLoadingIndicator);
        BaseActivity.isLoading = false;

        if (wifiStatus == NSConstants.NS_WIFI_SERVER_CONNECTED) {

            LogUserAction.sendNewLog(mInstance.userService, "NS_SERVER_CONNECTION_SUCCESS", "1", "","UI000340");

            isFinished = true;
            mInstance.runOnUiThread(() -> DialogUtil.createSimpleOkDialog(mInstance, "", LanguageProvider.getLanguage("UI000340C003"),
                    LanguageProvider.getLanguage("UI000340C004"), (dialogInterface, i) -> {
                        RegistrationStepActivity activity = mInstance;
                        BluetoothListFragment.selectedNemuriScan.setInfoType(currentBedModel);
                        BluetoothListFragment.selectedNemuriScan.setMattressExist(isMattressExist);
                        BluetoothListFragment.selectedNemuriScan.setBedExist(isBedExist);
                        BluetoothListFragment.selectedNemuriScan.setLastUpdate(System.currentTimeMillis() / 1000);

                        BluetoothListFragment.selectedNemuriScan.setMajor(majorVer);
                        BluetoothListFragment.selectedNemuriScan.setMinor(minorVer);
                        BluetoothListFragment.selectedNemuriScan.setRevision(revisionVer);
                        BluetoothListFragment.selectedNemuriScan.setLastFWUpdate(System.currentTimeMillis() / 1000);

                        if (activity.isRegistration) {
                            activity.go(RegistrationStepActivity.FRAGMENT_ACCOUNT_REGISTRATION);
                            //NemuriScanModel.clear();
                            //BluetoothListFragment.selectedNemuriScan.insert();
                            //Insert To Temporary NemuriScan Registration
                            insertToTemporaryNS(BluetoothListFragment.selectedNemuriScan);
                            setSerialNumber(BluetoothListFragment.selectedNemuriScan.getSerialNumber());
                        } else if(isWifiOnly) {
                            purgeBLE();
                            activity.go(RegistrationStepActivity.FINISH_FLOW);
                        }else{
                            showProgress();
                            NemuriScanUtil.register(getActivity(), getActivity(), UserLogin.getUserLogin().getId(), BluetoothListFragment.selectedNemuriScan.getSerialNumber(),majorVer,minorVer,revisionVer,
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
                    }));

            isIntentionalDC = true;
            purgeBLE();
        } else {
            nsManager.stopScan();
            disableWifi("SERVER_CONN_FAILED");
            Logger.e("WIFI FAILED TRIGGERED");
            LogUserAction.sendNewLog(mInstance.userService, "NS_SERVER_CONNECTION_FAILED", "1", "","UI000340");
            mInstance.runOnUiThread(() -> DialogUtil.createYesNoDialogLink(parentActivity, "", LanguageProvider.getLanguage("UI000311C010"),
                    LanguageProvider.getLanguage("UI000802C171"), (dialogInterface, i) -> {
                        Intent faqIntent = new Intent(parentActivity, FaqActivity.class);
                        faqIntent.putExtra("ID_FAQ", "UI000802C171");
                        faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(faqIntent);
                    },
                    LanguageProvider.getLanguage("UI000311C008"), (dialogInterface, i) -> {nsManager.disconnectCurrentDevice();initiateWifiProcess();},
                    LanguageProvider.getLanguage("UI000311C009"), (dialogInterface, i) -> new Handler().postDelayed(parentActivity::onBackPressed, 100)
            ));
        }
    }

    @Override
    public void onNSSpecReceived(NSSpec spec) {
        currentBedModel = spec.getBedType();
        isBedExist = spec.isBedExist();
        isMattressExist = spec.isMattressExist();
        majorVer = spec.getMajor();
        minorVer = spec.getMinor();
        revisionVer = spec.getRevision();

        BluetoothListFragment.selectedNemuriScan.setMajor(majorVer);
        BluetoothListFragment.selectedNemuriScan.setMinor(minorVer);
        BluetoothListFragment.selectedNemuriScan.setRevision(revisionVer);

        nsManager.requestAuthentication(BluetoothListFragment.selectedNemuriScan.getServerGeneratedId());
    }

    private void initiateWifiProcess() {
        initiateWifiProcess(false);
    }

    private void initiateWifiProcess(boolean shouldSplitPacket) {
        wifiSettingTimeoutHandler.postDelayed(wifiSettingTimeoutTimer, WIFI_SETTING_TIMEOUT * 1000);
        mInstance.runOnUiThread(this::startLoadingIndicator);
        isIntentionalDC = true;
        nsManager = NSManager.getInstance(getContext(), this);
        nsManager.setShouldSplitPacket(shouldSplitPacket);
        nsManager.disconnectCurrentDevice();
        nsManager.startScan(parentActivity);
    }

    public void disableWifi(String reason){
        LogUserAction.sendNewLog(mInstance.userService, "NS_WIFI_DISABLING", reason, "", "UI000340");
        NSWifiSetting wifiSetting = new NSWifiSetting("", 0, "");
        wifiSetting.setIsWifiEnabled(0);
        nsManager.setWifiSetting(wifiSetting);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public static void response(RegistrationStepActivity activity) {
        BluetoothListFragment.selectedNemuriScan.insert();
        setSerialNumber(BluetoothListFragment.selectedNemuriScan.getSerialNumber());
        BluetoothListFragment.selectedNemuriScan = null;
        activity.go(RegistrationStepActivity.FINISH_FLOW);
    }

    public static void setSerialNumber(String sn_nemuri_scan) {
        //#region Register SN
        SharedPreferences sn = BedApplication.getsApplication().getSharedPreferences("SN_NEMURI_SCAN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sn.edit();
        editor.putString("SERIAL_NUMBER", sn_nemuri_scan);
        editor.apply();
        //#endregion
    }

    @Override
    protected void onNSDisconnectCancelled() {
        RegistrationStepActivity parentActivity = (RegistrationStepActivity) getActivity();
        if (parentActivity != null) {
            new Handler().postDelayed(() -> parentActivity.poptoFragmentTag(RegistrationStepActivity.FRAGMENT_BLUETOOTH_LIST), 100);
        }
    }

    @Override
    public void onDeviceTemplateFetched(List<DeviceTemplateMattressModel> mattressModels, List<DeviceTemplateBedModel> bedModels,
                                        List<DeviceTemplateMattressModel> mattressModelDefaults, List<DeviceTemplateBedModel> bedModelDefaults,
                                        NemuriConstantsModel nemuriConstantsModel) {

    }

    private void startLoadingIndicator() {
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
    }

    private void stopLoadingIndicator() {
        progressView.setVisibility(View.GONE);
        BaseActivity.isLoading = false;
        progressView.stopAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!isFinished)
            initiateWifiProcess();
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
            Logger.e("ILLEGAL OP TRIGGERED");
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

    @Override
    public void onOperationTimeout() {
        nsManager.stopScan();
        purgeBLE();
        initiateWifiProcess(true);
    }

    //MARK END : NSBaseDelegate
    @Override
    public void onPause() {
        super.onPause();
        if(!isFinished)
            purgeBLE();
    }

    private void purgeBLE() {
        wifiSettingTimeoutHandler.removeCallbacks(wifiSettingTimeoutTimer);
        pollConnectionStatusHandler.removeCallbacks(pollConnectionStatusTimer);
        reconnectTimerHandler.removeCallbacks(reconnectTimer);
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }

    @Override
    public void onConnectionEstablished() {
        parentActivity.runOnUiThread(() -> {
            nsManager.getSerialNumber();
        });
    }

    @Override
    public void onScanResult(ScanResult scanResult) {
        NemuriScanModel nemuriScanModel = BluetoothListFragment.selectedNemuriScan;
        if (nemuriScanModel != null) {
            String savedMac = nemuriScanModel.getMacAddress();
            String targetMac = scanResult.getDevice().getAddress();
//            Logger.v("BLEFragment : Scanning BLE, looking for "+savedMac +" trying "+targetMac+" "+scanResult.getDevice().getName());
            if (savedMac.equalsIgnoreCase(targetMac)) {
                Logger.v("BLEFragment : Scanning BLE, match found");
                nsManager.connectToDevice(scanResult.getDevice(), getContext());
                nsManager.stopScan();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        hideProgress();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideProgress();
        nsManager.setDelegate(null);
        if(nsManager.isBLECurrentlyScanning()){
            nsManager.stopScan();
        }
    }

    public void insertToTemporaryNS(NemuriScanModel nemuriScanModel) {
        NemuriScanTemporaryModel.clear();
        NemuriScanTemporaryModel nSTM = new NemuriScanTemporaryModel();
        nSTM.setSerialNumber(nemuriScanModel.getSerialNumber());
        nSTM.setMacAddress(nemuriScanModel.getMacAddress());
        nSTM.setServerGeneratedId(nemuriScanModel.getServerGeneratedId());
        nSTM.setServerURL(nemuriScanModel.getServerURL());
        nSTM.setInfoType(nemuriScanModel.getInfoType());
        nSTM.setIntranet(nemuriScanModel.isIntranet());
        nSTM.setMattressExist(nemuriScanModel.isMattressExist());
        nSTM.setBedExist(nemuriScanModel.isBedExist());
        nSTM.setLastConnectionTime(nemuriScanModel.getLastConnectionTime());
        nSTM.setLastUpdate(nemuriScanModel.getLastUpdate());
        nSTM.setHeightSupported(nemuriScanModel.isHeightSupported());
        nSTM.setMajor(nemuriScanModel.getMajor());
        nSTM.setMinor(nemuriScanModel.getMinor());
        nSTM.setRevision(nemuriScanModel.getRevision());
        nSTM.insert();
    }

    //MARK : NSScanDelegate

    @Override
    public void onStopScan() {
        super.onStopScan();
        if(isReconnectingAfterSetWifi) {
            reconnectTimerHandler.postDelayed(reconnectTimer, reconnectPollInterval * 1000);
            isReconnectingAfterSetWifi = false;
        }
    }

    //END MARK : NSScanDelegate

}


