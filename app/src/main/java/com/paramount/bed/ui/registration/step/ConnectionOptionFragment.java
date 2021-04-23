package com.paramount.bed.ui.registration.step;

import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.orhanobut.logger.Logger;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSConstants;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DeviceTemplateProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.PermissionUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectionOptionFragment extends BLEFragment implements DeviceTemplateProvider.DeviceTemplateFetchListener {
    @BindView(R.id.btnChoose)
    Button btnChoose;
    @BindView(R.id.rgConnection)
    RadioGroup rgConnection;
    @BindView(R.id.rbConnectionInternet)
    RadioButton rbConnectionInternet;
    @BindView(R.id.rbConnectionIntranet)
    RadioButton rbConnectionIntranet;

    BaseActivity parentActivity;
    public UserService userService;

    //ble vars
    private NemuriScanModel nemuriScanModel;
    public boolean isWifiOnly;
    private NemuriConstantsModel nemuriConstantsModel = new NemuriConstantsModel();

    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = new Runnable() {
        public void run() {
            Logger.w("RemoteActivity : Disconnect by timeout");
            hideProgress();
            isIntentionalDC = true;
            if (nsManager != null) {
                nsManager.disconnectCurrentDevice();
            }
            new Handler().postDelayed(() -> {
                DialogUtil.createCustomYesNo(parentActivity,
                        "",
                        LanguageProvider.getLanguage("UI000610C022"),
                        LanguageProvider.getLanguage("UI000610C026"),
                        (dialogInterface, i) -> {
                            parentActivity.onBackPressed();
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

    private boolean isInBackground = false;
    private boolean isLocationPermissionRejected = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_connection_option, container, false);
        ButterKnife.bind(this, view);
        applyLocalization(view);
        parentActivity = (BaseActivity) getActivity();
        btnChoose.setOnClickListener((v) -> choseOption());
        rbConnectionInternet.setChecked(true);
        BluetoothListFragment.isFromWifiList = true;

        if(isWifiOnly){
            showProgress();
            userService = ApiClient.getClient(getContext()).create(UserService.class);
            nemuriScanModel = NemuriScanModel.getUnmanagedModel();
            BluetoothListFragment.selectedNemuriScan = nemuriScanModel;
            DeviceTemplateProvider.getDeviceTemplate(getContext(),this, UserLogin.getUserLogin().getId(), nemuriScanModel.getInfoType());
        }
        return view;
    }

    private void choseOption() {
        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        if (!activity.isFinishing()) {
            switch (rgConnection.getCheckedRadioButtonId()) {
                case R.id.rbConnectionInternet:
                    BluetoothListFragment.selectedNemuriScan.setIntranet(false);
                    activity.go(activity.FRAGMENT_WIFI_LIST);
                    break;
                case R.id.rbConnectionIntranet:
                    BluetoothListFragment.selectedNemuriScan.setIntranet(true);
                    activity.selectedWifi = "";
                    activity.go(activity.FRAGMENT_MANUAL_WIFI);
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            PermissionUtil.showLocationPermissionDialogAlert(parentActivity, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    hideProgress();
                    parentActivity.onBackPressed();
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
    public void onPause() {
        super.onPause();
        isInBackground = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInBackground && nsManager != null && !nsManager.isBLEReady() && !isLocationPermissionRejected) {
            parentActivity.runOnUiThread(this::tryToConnectBLE);
        }
        isInBackground = false;
    }

    //ble related func†ions
    @Override
    public void onScanResult(ScanResult scanResult) {
        parentActivity.runOnUiThread(() -> {
            if (nemuriScanModel != null) {
                String savedMac = nemuriScanModel.getMacAddress();
                String targetMac = scanResult.getDevice().getAddress();
                Logger.v("RemoteActivity : Scanning BLE, looking for " + savedMac + " trying " + targetMac + " " + scanResult.getDevice().getName());
                if (savedMac.equalsIgnoreCase(targetMac)) {
                    Logger.v("RemoteActivity : Scanning BLE, match found");
                    parentActivity.runOnUiThread(() -> {
                        nsManager.connectToDevice(scanResult.getDevice(), parentActivity);
                        nsManager.stopScan();
                    });
                }
            }

        });
    }
    @Override
    public void onConnectionEstablished() {
        parentActivity.runOnUiThread(() -> {
            isIntentionalDC = false;
            if (nsManager != null) {
                nsManager.getSerialNumber();
            }
        });
    }
    @Override
    public void onSerialNumberReceived(String serialNumber) {
        parentActivity.runOnUiThread(() -> {
            connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
            if (nemuriScanModel != null) {
                nsManager.requestAuthentication(nemuriScanModel.getServerGeneratedId());
            } else {
                Logger.e("onSerialNumberReceived ns model null");
            }

        });
    }

    @Override
    public void onAuthenticationFinished(int result) {
        hideProgress();
        if (result == NSConstants.NS_AUTH_SUCCESS || result == NSConstants.NS_AUTH_REG_SUCCESS) {
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_SUCCESS", "1", "", "UI000610");
        } else {
            LogUserAction.sendNewLog(userService, "NS_SET_SERVERID_FAILED", "1", "", "UI000610");
            parentActivity.runOnUiThread(() -> DialogUtil.createSimpleOkDialog(getContext(), "", LanguageProvider.getLanguage("UI000610C034"),
                    LanguageProvider.getLanguage("UI000610C035"), (dialogInterface, i) -> parentActivity.onBackPressed()));
        }

    }

    @Override
    public void onNSSpecReceived(NSSpec spec) {
    }
    @Override
    public void onDisconnect() {
        //TODO
        Logger.w("disconnect triggered isIntentionalDC " + isIntentionalDC);
        if (isIntentionalDC) {
            isIntentionalDC = false;
            return;
        }
        parentActivity.runOnUiThread(this::showLocationPermissionDialogAlert);
    }
    @Override
    public void onDeviceTemplateFetched(List<DeviceTemplateMattressModel> mattressModels, List<DeviceTemplateBedModel> bedModels, List<DeviceTemplateMattressModel> mattressModelDefaults, List<DeviceTemplateBedModel> bedModelDefaults, NemuriConstantsModel nemuriConstantsModel) {
        this.nemuriConstantsModel.copyValue(nemuriConstantsModel);
        initBLE();
    }

    @Override
    public void onLocationPermissionDenied() {
        isLocationPermissionRejected = true;
        parentActivity.runOnUiThread(this::showLocationPermissionDialogAlert);
    }

    @Override
    public void onLocationServiceDisabled() {
        parentActivity.runOnUiThread(this::showLocationServiceDialogAlert);
    }
    private void initBLE() {
        //BLE initialization
        if (nemuriScanModel != null) {
            if (!nemuriScanModel.getSerialNumber().startsWith("F")) {
                parentActivity.runOnUiThread(this::tryToConnectBLE);
            }
        }
    }

    public void showLocationPermissionDialogAlert() {
        hideProgress();
        if (NemuriScanModel.get() != null) {
            PermissionUtil.showLocationPermissionDialogAlert(parentActivity, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                    hideProgress();
                    parentActivity.onBackPressed();
                }

                @Override
                public void onPermissionGranted() {
                    isLocationPermissionRejected = false;
                    tryToConnectBLE();
                }
            });
        }
    }

    public void showLocationServiceDialogAlert() {
        hideProgress();
        if (NemuriScanModel.get() != null) {
            PermissionUtil.showLocationServiceDialogAlert(parentActivity, new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                    hideProgress();
                    parentActivity.onBackPressed();
                }

                @Override
                public void onEnabled() {
                    tryToConnectBLE();
                }
            });
        }
    }

    private void tryToConnectBLE() {
        IOSDialogRight.Dismiss();
        nsManager = NSManager.getInstance(getContext(), this);
        if(nsManager.isBLEReady()){
            return;
        }
        showProgress();

        //setup connection timeout
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (PermissionUtil.locationFeatureEnabled(parentActivity)) {
            connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, (nemuriConstantsModel.nsConnectionTimeout) * 1000);
        }

        if (nsManager != null) {
            nsManager.startScan(parentActivity);
        }
    }
}
