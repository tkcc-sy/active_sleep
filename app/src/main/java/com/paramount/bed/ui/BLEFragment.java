package com.paramount.bed.ui;

import android.app.Activity;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.ble.NSOperation;
import com.paramount.bed.ble.interfaces.NSConnectionDelegate;
import com.paramount.bed.ble.interfaces.NSScanDelegate;
import com.paramount.bed.ble.pojo.NSSpec;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.main.FaqActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.ui.registration.step.BluetoothListFragment;
import com.paramount.bed.ui.registration.step.ManualWifiFragment;
import com.paramount.bed.ui.registration.step.WifiConnectFragment;
import com.paramount.bed.ui.registration.step.WifiListFragment;
import com.paramount.bed.util.BluetoothUtil;
import com.paramount.bed.util.DialogUtil;

import static com.paramount.bed.ui.registration.RegistrationStepActivity.FRAGMENT_START;

public class BLEFragment extends BaseFragment implements NSScanDelegate, NSConnectionDelegate {
    protected NSManager nsManager;
    //TODO : get the reference in a better way
    public Activity activityRef;
    private Handler connectionTimeoutHandler = new Handler();
    private Runnable connectionTimeoutTimer = () -> {
//        hideProgress();
        purgeBLE();
        showBLEReconnectAlert();
    };

    private void purgeBLE() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        if (nsManager != null) {
            nsManager.disconnectCurrentDevice();
        }
    }

    protected boolean isIntentionalDC = false;

    //MARK : NSBaseDelegate
    @Override
    public void onCommandWritten(NSOperation command) {
        //TODO : HANDLE ILLEGAL BLE OP
        if (command.getCommandCode() == NSOperation.FREE_DECREASE_COMBI.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_MATTRESS_POSITION.getCommandCode() ||
                command.getCommandCode() == NSOperation.SET_BED_SETTING.getCommandCode()) {
            //DISCONNECT & SHOW ILLEGAL OPERATION ALERT
            purgeBLE();
            Activity activity = getActivity();
            if(activity == null){
                activity = activityRef;
            }
            if(activity == null){
                return;
            }
            DialogUtil.createCustomYesNo(activity,
                    "",
                    LanguageProvider.getLanguage("UI000802C191"),
                    LanguageProvider.getLanguage("UI000802C193"),
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        onNSDisconnectCancelled();
                    },
                    LanguageProvider.getLanguage("UI000802C192"),
                    (dialogInterface, i) -> {
                        //retry
                        tryToConnect();
                    }
            );
        }
    }
    //MARK END : NSBaseDelegate

    //MARK : NSScanDelegate
    @Override
    public void onStartScan() {

    }

    @Override
    public void onLocationPermissionDenied() {

    }

    @Override
    public void onLocationServiceDisabled() {

    }

    @Override
    public void onCancelScan() {

    }

    @Override
    public void onStopScan() {

    }

    @Override
    public void onScanResult(ScanResult scanResult) {

    }
    //MARK END : NSScanDelegate

    //MARK : NSConnectionDelegate
    @Override
    public void onConnectionEstablished() {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onSerialNumberReceived(String serialNumber) {

    }

    @Override
    public void onAuthenticationFinished(int result) {

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
    //MARK END: NSConnectionDelegate

    //MARK : Fragment lifecycle

    @Override
    public void onStart() {
        super.onStart();
//        Logger.i("BLE FRAGMENT onStart " + this.getClass().getName());
//        Logx("BLE FRAGMENT", "BLE FRAGMENT onStart " + this.getClass().getName());
//        nsManager = NSManager.getInstance(getContext(), this);
//        if (!nsManager.isBLEReady()) {
//            showBLEReconnectAlert();
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Logger.i("BLE FRAGMENT onCreateView " + this.getClass().getName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    //MARK END : Fragment lifecycle

    //MARK : Logic
    protected void showBluetoothSettingAlert() {
        Fragment topFragment = getTopFragment();
        if(topFragment == null){
            return;
        }
        if (topFragment.getClass() != WifiConnectFragment.class
                && topFragment.getClass() != BluetoothListFragment.class
                && topFragment.getClass() != WifiListFragment.class
                && topFragment.getClass() != ManualWifiFragment.class) {
            connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
            if (nsManager != null) {
                nsManager.disconnectCurrentDevice();
            }
            return;
        }

        Activity activity = getActivity();
        if(activity == null){
            activity = activityRef;
        }
        final Activity finalActivity = activity;
        if(activity == null){
            return;
        }
        activity.runOnUiThread(() -> DialogUtil.createCustomYesNo(finalActivity, "", LanguageProvider.getLanguage("UI000802C009"),
                LanguageProvider.getLanguage("UI000311C009"),
                (dialogInterface, i) -> dialogInterface.dismiss(),
                LanguageProvider.getLanguage("UI000802C007"), (dialogInterface, i) -> {
                    startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS), 100);
                    dialogInterface.dismiss();
                }));
    }

    protected void showBLEReconnectAlert() {
        if (BluetoothListFragment.selectedNemuriScan != null &&
                !BluetoothListFragment.selectedNemuriScan.getSerialNumber().startsWith("F")) {
            if (BluetoothUtil.isBluetoothEnable()) {
                Fragment topFragment = getTopFragment();
                if(topFragment == null){
                    return;
                }
                if (topFragment.getClass() != WifiConnectFragment.class) {
                    connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
                    if (nsManager != null) {
                        nsManager.disconnectCurrentDevice();
                    }
                    return;
                }
                Activity activity = getActivity();
                if(activity == null){
                    activity = activityRef;
                }
                final Activity finalActivity = activity;
                if(activity == null){
                    return;
                }
                activityRef.runOnUiThread(() -> DialogUtil.createYesNoDialogLink(finalActivity, "", LanguageProvider.getLanguage("UI000802C025"),
                        LanguageProvider.getLanguage("UI000802C178"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(RegistrationStepActivity.mInstance, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000802C178");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        },
                        LanguageProvider.getLanguage("UI000802C026"), (dialogInterface, i) -> tryToConnect(),
                        LanguageProvider.getLanguage("UI000802C166"), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            onNSDisconnectCancelled();
                        }));


            } else {
                showBluetoothSettingAlert();
            }
        }
    }

    public Fragment getTopFragment() {
        FragmentActivity activity = getActivity();
        if(activity == null){
            activity = (FragmentActivity) activityRef;
        }
        if(activity != null) {
            if (activity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                return activity.getSupportFragmentManager().findFragmentByTag(String.valueOf(FRAGMENT_START));
            }
            String fragmentTag = activity.getSupportFragmentManager().getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
            return activity.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        }
        return null;
    }

    protected void onNSDisconnectCancelled() {

    }

    protected void tryToConnect() {
//        showProgress();
        tryToConnectSilently();
    }

    protected void tryToConnectSilently() {
        connectionTimeoutHandler.removeCallbacks(connectionTimeoutTimer);
        connectionTimeoutHandler.postDelayed(connectionTimeoutTimer, (NemuriConstantsModel.get().nsConnectionTimeout) * 1000);
        tryToConnectWithoutTimeout();
    }

    protected void tryToConnectWithoutTimeout() {
        Activity activity = getActivity();
        if(activity == null){
            activity = activityRef;
        }
        if (activity != null) {
            nsManager.startScan(activity);
        } else {
            Logger.e("tryToConnectSilently activity null");
        }
    }
    //MARK END : Logic


}
