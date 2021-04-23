package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSFWUpdateDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.interfaces.NSSettingDelegate;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.ble.pojo.NSWifiSetting;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.FirmwareFileModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.step.BluetoothListFragment;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class UpdateFirmwareActivity extends BaseActivity implements NSScanDelegate, NSConnectionDelegate, NSSettingDelegate, DeviceTemplateProvider.DeviceTemplateFetchListener, NSFWUpdateDelegate {
    @BindView(R.id.fwUpdateProgressBar)
    CircularProgressBar fwUpdateProgressBar;

    @BindView(R.id.percentage_tv)
    TextView percentageTV;

    @BindView(R.id.status_text)
    TextView statusTV;

    @BindView(R.id.btnRetry)
    Button retryButton;


    @OnClick(R.id.btnRetry)
    protected void onRetryTapped(){
        retryButton.setVisibility(View.GONE);
        runOnUiThread(this::showLocationPermissionDialogAlert);
    }


    //ble vars
    private NSManager nsManager;
    private NemuriScanModel nemuriScanModel;
    private NemuriConstantsModel nemuriConstantsModel;
    private boolean isIntentionalDC = false;
    FirmwareFileModel.FirmwareBLEPacket currentlySentPacket;
    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            hideProgress();
            isIntentionalDC = true;
            if (nsManager != null) {
                nsManager.disconnectCurrentDevice();
            }
            LogUserAction.sendNewLog(userService, "FW_UPDATE_CONNECTION_TIMEOUT", "", "", "UI000732");
            updateStatusTV(LanguageProvider.getLanguage("UI000732C012"),true);
            purgeBLE();

        }
    };

    private Handler fwWriteTimeoutHandler = new Handler();
    private Runnable fwWriteTimeoutTimer = new Runnable() {
        public void run() {
            if (nsManager != null) {
                nsManager.disconnectCurrentDevice();
            }
            LogUserAction.sendNewLog(userService, "FW_UPDATE_F2_TIMEOUT", "", "", "UI000732");
            updateStatusTV(LanguageProvider.getLanguage("UI000732C025"),true);
            purgeBLE();

        }
    };

    //firmware vars
    private FirmwareFileModel firmwareFileModel;
    private ArrayList<FirmwareFileModel.FirmwareBLEPacket> fwBLEPackets = new ArrayList<>();
    private int fwWriteFailCount;
    private int totalPackets = 0;
    private boolean dueToFail = false;
    private boolean updateFinished = false;
    private boolean updateCancelled = false;
    private boolean isInBackground = false;
    private boolean shouldAutoReconnect = false;
    private boolean isFinishingUpdate = false;
    private boolean isTemporaryNS = false;
    private String currentSN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shouldHideBackButton = true;
        super.onCreate(savedInstanceState);
        setToolbarTitle(LanguageProvider.getLanguage("UI000732C001"));
        ButterKnife.bind(this);
        nemuriConstantsModel = NemuriConstantsModel.get().getUnmanaged();
        nemuriScanModel = NemuriScanModel.getUnmanagedModel();
        if(nemuriScanModel == null){
            isTemporaryNS = true;
            nemuriScanModel = BluetoothListFragment.selectedNemuriScan;
            if(nemuriScanModel == null){
                nemuriScanModel = UpdateFirmwareScanActivity.selectedNemuriScan;
            }
            currentSN = nemuriScanModel.getSerialNumber();
        }else{
            currentSN = nemuriScanModel.getSerialNumber();
        }
        setupProgressBar();
        setupPercentageTV();
        loadFirmwareFile();
    }

    //initialization functions
    private void loadFirmwareFile(){
        firmwareFileModel = new FirmwareFileModel(BuildConfig.FIRMWARE_NAME,this);
        fwBLEPackets = firmwareFileModel.getBLEPackets();
        totalPackets = fwBLEPackets.size();

        LogUserAction.sendNewLog(userService, "FW_UPDATE_PARSE_FILE", BuildConfig.FIRMWARE_NAME+"-"+
                        BuildConfig.FIRMWARE_CHECKSUM+" "+BuildConfig.FIRMWARE_MAJOR+"."+BuildConfig.FIRMWARE_MINOR+"."+BuildConfig.FIRMWARE_REVISION
                , "", "UI000732");
        getDeviceTemplate();
    }

    private void setupProgressBar(){
        fwUpdateProgressBar.setProgressBarColorStart(Color.argb(255,0,171,228));
        fwUpdateProgressBar.setProgressBarColorEnd(Color.argb(255,0,217,225));
        fwUpdateProgressBar.setProgressBarColorDirection(CircularProgressBar.GradientDirection.TOP_TO_BOTTOM);

        fwUpdateProgressBar.setBackgroundProgressBarColor(Color.GRAY);
        fwUpdateProgressBar.setProgressBarWidth(5f);
        fwUpdateProgressBar.setBackgroundProgressBarWidth(5f);

    }

    private void setupPercentageTV(){
        String valueText = "0%";

        Spannable spannable = new SpannableString(valueText);
        spannable.setSpan(new ForegroundColorSpan(Color.argb(255,0,171,228)),0,1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(3),0,1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        percentageTV.setText(spannable);
    }

    //ui related functions
    private void updateStatusTV(String value, boolean shouldShowRetry){
        runOnUiThread(() -> {
            statusTV.setText(value);
            retryButton.setVisibility(shouldShowRetry ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onBackPressed() {
        //prevent back
    }

    private void updateProgress(){
        runOnUiThread(() -> {
            float progressInFloat = (float) fwBLEPackets.size() / (float) totalPackets;
            int currentProgress = (int) ((1.0 - progressInFloat) * 95);
            fwUpdateProgressBar.setProgress(currentProgress);

            updatePercentageTV(currentProgress);
        });
    }

    private void updatePercentageTV(int currentProgress){
        int  numEndIndex;
        //calculate spannable index
        if(currentProgress >= 100){
            numEndIndex = 3;
        }else if(currentProgress >= 10){
            numEndIndex = 2;
        }else{
            numEndIndex = 1;
        }
        Spannable spannable = new SpannableString(currentProgress +"%")
                ;
        spannable.setSpan(new ForegroundColorSpan(Color.argb(255,0,171,228)),0,numEndIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(3),0,numEndIndex,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        percentageTV.setText(spannable);

    }
    @Override
    protected void onPause() {
        super.onPause();
        if(retryButton.getVisibility() == View.GONE && !updateFinished && nsManager != null){
            nsManager.disconnectCurrentDevice();
        }
        isInBackground = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInBackground) {
            runOnUiThread(this::showLocationPermissionDialogAlert);
        }
        isInBackground = false;
    }

    //ble related functions
    private void initBLE() {
        //BLE initialization
        if (nemuriScanModel != null) {
            runOnUiThread(this::showLocationPermissionDialogAlert);
        }
    }

    private void startUpdate(){
        if(firmwareFileModel != null){
            fwBLEPackets = firmwareFileModel.getBLEPackets();
            totalPackets = fwBLEPackets.size();
            LogUserAction.sendNewLog(userService, "FW_UPDATE_REQ_81",currentSN, "", "UI000732");
            nsManager.getSerialNumber();
        }
    }

    private void tryToConnectBLE() {
        if(updateFinished){
            return;
        }
        IOSDialogRight.Dismiss();
        nsManager = NSManager.getInstance(this, this);
        updateStatusTV(LanguageProvider.getLanguage("UI000732C002"),false);

        //setup connection timeout
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (PermissionUtil.locationFeatureEnabled(UpdateFirmwareActivity.this)) {
            connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, nemuriConstantsModel.nsConnectionTimeout * 1000);
        }

        if (nsManager != null) {
            nsManager.startScan(this);
        }
    }

    public void showLocationPermissionDialogAlert() {
        hideProgress();
        if (nemuriScanModel != null) {
            PermissionUtil.showLocationPermissionDialogAlert(UpdateFirmwareActivity.this, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    hideProgress();
                    purgeBLE();
                    finish();
                }

                @Override
                public void onPermissionGranted() {
                    tryToConnectBLE();
                }
            });
        }
    }

    public void showLocationServiceDialogAlert() {
        hideProgress();
        if (nemuriScanModel != null) {
            PermissionUtil.showLocationServiceDialogAlert(UpdateFirmwareActivity.this, new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                    hideProgress();
                    purgeBLE();
                    finish();
                }

                @Override
                public void onEnabled() {
                    tryToConnectBLE();
                }
            });
        }
    }

    private void purgeBLE() {
        isIntentionalDC = true;
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }

    private void getDeviceTemplate() {
        Integer bedType = nemuriScanModel == null ? null : nemuriScanModel.getInfoType();
        if (UserLogin.isUserExist()) {
            DeviceTemplateProvider.getDeviceTemplate(this, this, UserLogin.getUserLogin().getId(), bedType);
        } else {
            DeviceTemplateProvider.getDeviceTemplate(this, this, 0, bedType);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_update_firmware;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @Override
    public void onConnectionEstablished() {
        startUpdate();
    }

    @Override
    public void onDisconnect() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        Logger.v("NSManager onDisconnect Auto Reconnecting "+nemuriConstantsModel.switchFWReconnectTime);
        if(shouldAutoReconnect){
            shouldAutoReconnect = false;
            Logger.v("NSManager Auto Reconnecting "+nemuriConstantsModel.switchFWReconnectTime);
            runOnUiThread(() -> new Handler().postDelayed(this::showLocationPermissionDialogAlert, nemuriConstantsModel.switchFWReconnectTime * 1000));
        }
        if(isIntentionalDC){
            isIntentionalDC = false;
            return;
        }
        hideProgress();
        if(!updateFinished) {
            LogUserAction.sendNewLog(userService, "FW_UPDATE_DISCONNECTED", "", "", "UI000732");
            updateStatusTV(LanguageProvider.getLanguage("UI000732C022"), true);
        }
    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {
        LogUserAction.sendNewLog(userService, "FW_UPDATE_RES_81",serialNumber, "", "UI000732");
        currentSN = serialNumber;
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        runOnUiThread(() -> {
            if (nemuriScanModel != null) {
                LogUserAction.sendNewLog(userService, "FW_UPDATE_REQ_82",serialNumber, "", "UI000732");
                nsManager.getNSSpec();
            } else {
                Logger.e("onSerialNumberReceived ns model null");
            }
        });
    }

    @Override
    public void onAuthenticationFinished(int result) {
        Logger.d("NSManager onAuthenticationFinished "+result);
        LogUserAction.sendNewLog(userService, "FW_UPDATE_RES_84",String.valueOf(result), "", "UI000732");
        if (result == NSConstants.NS_AUTH_SUCCESS || result == NSConstants.NS_AUTH_REG_SUCCESS) {
            //LOG HERE NS_SET_SERVERID_SUCCESS
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_SUCCESS", "1", "", "UI000610");
            LogUserAction.sendNewLog(userService, "FW_UPDATE_REQ_94",String.valueOf(result), "", "UI000732");
            nsManager.switchFirmwareMode();
        } else {
            //LOG HERE NS_SET_SERVERID_FAILED
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_FAILED", "1", "", "UI000610");
            updateStatusTV(LanguageProvider.getLanguage("UI000732C013"),true);
        }
    }

    @Override
    public void onNSStatusReceived(int systemStatus, int bleStatus, int wifiStatus) {
    }

    private void showFinishAlert(){
        runOnUiThread(() -> DialogUtil.createSimpleOkDialog(UpdateFirmwareActivity.this, "", LanguageProvider.getLanguage("UI000732C019"),
                LanguageProvider.getLanguage("UI000802C003"), (dialog, which) -> {
                    setResult(UpdateFirmwareIntroActivity.FINISH_RESULT_CODE);
                    finish();
                }));
    }

    @SuppressLint("CheckResult")
    @Override
    public void onNSSpecReceived(NSSpec spec) {
        LogUserAction.sendNewLog(userService, "FW_UPDATE_RES_82",String.valueOf(spec.isFWMode()), "", "UI000732");
        Logger.v("RemoteActivity onNSSpecReceived isNSExist : " + spec.isNSExist() + " isBedExist " + spec.isBedExist() + " isMattressExist " + spec.isMattressExist());
        if(isFinishingUpdate){
            isFinishingUpdate = false;
            if(!spec.isFWMode()) {
                //send firmware version data
                userService.sendFirmwareVersion(BuildConfig.FIRMWARE_MAJOR, BuildConfig.FIRMWARE_MINOR, BuildConfig.FIRMWARE_REVISION, currentSN)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                            @Override
                            public void onSuccess(BaseResponse baseResponse) {
                                if(baseResponse.isSucces()) {
                                    LogUserAction.sendNewLog(userService, "FW_UPDATE_FINISHED",currentSN, "", "UI000732");
                                    updateFinished = true;
                                    if (!isTemporaryNS) {
                                        NemuriScanModel localNS = NemuriScanModel.get();
                                        if (localNS != null) {
                                            Logger.d("NSManager updating update firmware flag true ");
                                            runOnUiThread(() -> NemuriScanModel.get().updateIsFWUpdateFailed(false));
                                            localNS.updateVersion(BuildConfig.FIRMWARE_REVISION, BuildConfig.FIRMWARE_MINOR, BuildConfig.FIRMWARE_MAJOR, (System.currentTimeMillis() / 1000));
                                        } else {
                                            Logger.d("NSManager local ns not present ");
                                        }
                                    }

                                    runOnUiThread(() -> {
                                        updatePercentageTV(100);
                                        fwUpdateProgressBar.setProgress(100);
                                        updateStatusTV("",false);
                                    });
                                    showFinishAlert();
                                    nsManager.disconnectCurrentDevice();
                                }else{
                                    if (nsManager != null) {
                                        nsManager.disconnectCurrentDevice();
                                    }
                                    updateStatusTV(LanguageProvider.getLanguage("UI000732C026"),true);
                                    purgeBLE();
                                    LogUserAction.sendNewLog(userService, "FW_UPDATE_CONNECTION_FAILURE", currentSN, "", "UI000732");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (nsManager != null) {
                                    nsManager.disconnectCurrentDevice();
                                }
                                updateStatusTV(LanguageProvider.getLanguage("UI000732C026"),true);
                                purgeBLE();
                                LogUserAction.sendNewLog(userService, "FW_UPDATE_CONNECTION_FAILURE", currentSN, "", "UI000732");
                            }
                        });

            }else{
                LogUserAction.sendNewLog(userService, "FW_UPDATE_CHECKSUM_FAILED", currentSN, "", "UI000732");

                if (nsManager != null) {
                    nsManager.disconnectCurrentDevice();
                }
                updateStatusTV(LanguageProvider.getLanguage("UI000732C023"),true);
                purgeBLE();
            }
        }else {
            runOnUiThread(() -> {
                if (nemuriScanModel != null && !isTemporaryNS) {
                    nemuriScanModel.updateSpec(spec);
                }
            });
            dueToFail = false;

            if (!spec.isFWMode()) {
                //extra checking version
                if (!nemuriScanModel.needsFWUpdate() && !isTemporaryNS) {
                    LogUserAction.sendNewLog(userService, "FW_UPDATE_SAME_VERSION",nemuriScanModel.getVersionString(), "", "UI000732");
                    runOnUiThread(() -> DialogUtil.createSimpleOkDialog(UpdateFirmwareActivity.this, "", LanguageProvider.getLanguage("UI000732C024"), LanguageProvider.getLanguage("UI000802C003"), (dialog, which) -> {
                        setResult(UpdateFirmwareIntroActivity.FINISH_RESULT_CODE);
                        finish();
                    }));
                } else {
                    if (nemuriScanModel != null) {
                        LogUserAction.sendNewLog(userService, "FW_UPDATE_REQ_84",currentSN, "", "UI000732");
                        nsManager.requestAuthentication(nemuriScanModel.getServerGeneratedId());
                    }
                }
            } else {
                LogUserAction.sendNewLog(userService, "FW_UPDATE_REQ_F1",currentSN, "", "UI000732");
                nsManager.enterFirmwareMode();
            }
        }
    }


    @Override
    public void onEnterFirmwareMode(boolean result) {
        LogUserAction.sendNewLog(userService, "FW_UPDATE_RES_F1",String.valueOf(result), "", "UI000732");
        Logger.i("result of onEnterFirmwareMode  "+result);
        if(result){
            hideProgress();
            updateStatusTV(LanguageProvider.getLanguage("UI000732C002"),false);
            currentlySentPacket = fwBLEPackets.remove(0);
            fwWriteFailCount = 0;
            fwWriteTimeoutHandler.postDelayed(fwWriteTimeoutTimer,nemuriConstantsModel.operationTimeout*1000);
            nsManager.writeFirmware(currentlySentPacket);
        }else{
            updateStatusTV(LanguageProvider.getLanguage("UI000732C017"),true);
            dueToFail = true;
        }
    }

    @Override
    public void onWriteFirmware(boolean result) {
        Logger.i("result of onWriteFirmware  "+result);
        fwWriteTimeoutHandler.removeCallbacks(fwWriteTimeoutTimer);
        if(updateCancelled){
            return;
        }
        if(result){
            updateProgress();
            if(fwBLEPackets.size() > 0){
                runOnUiThread(() -> new Handler().postDelayed(() -> {
                    currentlySentPacket = fwBLEPackets.remove(0);
                    fwWriteTimeoutHandler.postDelayed(fwWriteTimeoutTimer,nemuriConstantsModel.operationTimeout*1000);
                    nsManager.writeFirmware(currentlySentPacket);
                    Logger.i("sisa "+fwBLEPackets.size()+" dari "+totalPackets);
                },0));

                Logger.i("sisa "+fwBLEPackets.size()+" dari "+totalPackets);

            }else{
                LogUserAction.sendNewLog(userService, "FW_UPDATE_REQ_F3", BuildConfig.FIRMWARE_CHECKSUM, "", "UI000732");
                nsManager.exitFirmwareMode(BuildConfig.FIRMWARE_CHECKSUM);
            }
            fwWriteFailCount = 0;
        }else{
            LogUserAction.sendNewLog(userService, "FW_UPDATE_RES_F2",String.valueOf(fwBLEPackets.size()), "", "UI000732");
            fwWriteFailCount++;
            //fail write 2 times
            if(fwWriteFailCount > 1){
                LogUserAction.sendNewLog(userService, "FW_UPDATE_F2_FAILED", fwBLEPackets.size() +" "+fwWriteFailCount, "", "UI000732");
                updateStatusTV(LanguageProvider.getLanguage("UI000732C018"),true);
                dueToFail = true;
            }else{
                //retry
                LogUserAction.sendNewLog(userService, "FW_UPDATE_F2_RETRY", fwBLEPackets.size() +" "+fwWriteFailCount, "", "UI000732");
                fwWriteTimeoutHandler.postDelayed(fwWriteTimeoutTimer,nemuriConstantsModel.operationTimeout*1000);
                nsManager.writeFirmware(currentlySentPacket);
            }

        }

    }

    @Override
    public void onExitFirmwareMode(boolean result) {
        Logger.i("result of onExitFirmwareMode  "+result);
        LogUserAction.sendNewLog(userService, "FW_UPDATE_RES_F3", String.valueOf(result), "", "UI000732");

        isIntentionalDC = true;
        if(result){
            if(dueToFail){
                String message = LanguageProvider.getLanguage("UI000732C020");
                updateStatusTV(message,dueToFail);
            }else{
                nsManager.disconnectCurrentDevice();
                shouldAutoReconnect = true;
                isFinishingUpdate = true;
            }
            dueToFail = false;
        }else{
            updateStatusTV(LanguageProvider.getLanguage("UI000732C021"),dueToFail);
        }

    }

    @Override
    public void onSwitchFirmwareMode(boolean result) {
        Logger.i("result of onSwitchFirmwareMode  "+result);
        LogUserAction.sendNewLog(userService, "FW_UPDATE_RES_94",String.valueOf(result), "", "UI000732");
        if(result){
            isIntentionalDC = true;
            nsManager.disconnectCurrentDevice();
            shouldAutoReconnect = true;
            if(!isTemporaryNS) {
                runOnUiThread(() -> NemuriScanModel.get().updateIsFWUpdateFailed(true));
            }
        }else{
            updateStatusTV(LanguageProvider.getLanguage("UI000732C016"),true);
        }

    }


    @Override
    public void onLocationPermissionDenied() {
        runOnUiThread(this::showLocationPermissionDialogAlert);
    }

    @Override
    public void onLocationServiceDisabled() {
        runOnUiThread(this::showLocationServiceDialogAlert);
    }

    @Override
    public void onDeviceTemplateFetched(List<DeviceTemplateMattressModel> mattressModels, List<DeviceTemplateBedModel> bedModels, List<DeviceTemplateMattressModel> mattressModelDefaults, List<DeviceTemplateBedModel> bedModelDefaults, NemuriConstantsModel nemuriConstantsModel) {
        this.nemuriConstantsModel = nemuriConstantsModel.getUnmanaged();
        hideLoading();
        initBLE();
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
                        nsManager.connectToDevice(scanResult.getDevice(), UpdateFirmwareActivity.this);
                        nsManager.stopScan();
                    });
                }
            }

        });
    }

    @Override
    public void onConnectionStalled(int status) {
        LogUserAction.sendNewLog(userService, "FW_UPDATE_CONN_STALLED", "", "", "UI000732");
        hideProgress();
        updateStatusTV(LanguageProvider.getLanguage( "UI000732C022"), true);
    }

    @Override
    public void onCommandWritten(NSOperation command) {
        if(command == NSOperation.SET_BED_SETTING  ||
                command == NSOperation.FREE_INCREASE_COMBI ||
                command == NSOperation.SET_MATTRESS_POSITION){
            LogUserAction.sendNewLog(userService, "FW_UPDATE_INVALID_COMMAND", String.valueOf(command.getCommandCode()), "", "UI000732");
            nsManager.disconnectCurrentDevice();
        }
    }

    @Override
    public void onSetNSURLFinished(boolean isSuccess) {

    }

    @Override
    public void onGetWifiReceived(NSWifiSetting data) {

    }

    @Override
    public void onSetWifiFinished(boolean isSuccess) {

    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onCancelScan() {

    }

    @Override
    public void onStopScan() {

    }


}
