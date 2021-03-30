package com.paramount.bed.ui.registration.step;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.RecyclerItemClickListener;
import com.paramount.bed.util.WifiUtil;

import java.util.ArrayList;

public class WifiListFragment extends BLEFragment {
    private ArrayList<String> lists = new ArrayList<>();
    RecyclerView wifiList;
    private WifiManager wifi;
    WifiListAdapter wifiScanAdapter;
    public static boolean isScanning;
    BaseActivity parentActivity;
    RegistrationStepActivity registrationStepActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_wifilist, container, false);
        BluetoothListFragment.isFromWifiList = true;
        wifiList = view.findViewById(R.id.wifiList);
        Button btnRescan = view.findViewById(R.id.btnRescan);
        TextView btnManualConfig = view.findViewById(R.id.btnManualConfig);
        btnRescan.setOnClickListener((v) -> findWifi(true));
        registrationStepActivity = (RegistrationStepActivity) getActivity();
        btnManualConfig.setOnClickListener((v) -> {
            if (registrationStepActivity != null) {
                registrationStepActivity.selectedWifi = "";
                registrationStepActivity.go(registrationStepActivity.FRAGMENT_MANUAL_WIFI);
            }
        });
        isScanning = false;
        applyLocalization(view);
        parentActivity = (BaseActivity) getActivity();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        wifiList.setLayoutManager(layoutManager);
        wifiScanAdapter = new WifiListAdapter(lists);
        wifiList.setAdapter(wifiScanAdapter);
        wifiList.addOnItemTouchListener(selectWifi());
        return view;
    }

    public void showProgress(Boolean isShow) {
        if (isShow) {
            showLoading();
        } else {
            hideLoading();
        }
    }

    private void registerBroadcast() {
        Activity activity = getActivity();
        if (activity != null) {
            wifi = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            getActivity().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    showProgress(false);
                    String action = intent.getAction();
                    if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                        SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                        if (SupplicantState.isValidState(state)
                                && state == SupplicantState.COMPLETED) {
                            WifiInfo wifiInfo = wifi.getConnectionInfo();
                            if (wifiInfo != null) {
                                String ssid = wifiInfo.getSSID();
                                lists.clear();
                                lists.add(ssid.replace("\"", ""));
                                wifiScanAdapter.notifyDataSetChanged();
                            }
                        }
                    }


                }
            }, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
        }
    }

    private void findWifi(Boolean isLoadingShow) {
        if (!PermissionUtil.hasLocationPermissions(getActivity())) {
            showLocationPermissionDialogAlert();
            return;
        }
        if (!PermissionUtil.isLocationServiceEnable(getActivity())) {
            showLocationServiceDialogAlert();
            return;
        }
        if (!WifiUtil.isWifiEnable(getContext())) {
            showWifiEnableDialogAert();
            return;
        }
        try {
            showProgress(isLoadingShow);
            registerBroadcast();
            isScanning = false;
        } catch (Exception e) {
            isScanning = false;
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        findWifi(false);
    }

    private RecyclerItemClickListener selectWifi() {
        return new RecyclerItemClickListener(getActivity(), wifiList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!PermissionUtil.hasLocationPermissions(getActivity())) {
                    showLocationPermissionDialogAlert();
                    return;
                }
                if (!PermissionUtil.isLocationServiceEnable(getActivity())) {
                    showLocationServiceDialogAlert();
                    return;
                }
                try {
                    RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                    if (activity != null) {
                        activity.selectedWifi = lists.get(position);
                        activity.go(activity.FRAGMENT_MANUAL_WIFI);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    findWifi(true);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void onNSDisconnectCancelled() {
        RegistrationStepActivity parentActivity = (RegistrationStepActivity) getActivity();
        if (parentActivity != null) {
            new Handler().postDelayed(() -> {
                parentActivity.poptoFragmentTag(RegistrationStepActivity.FRAGMENT_BLUETOOTH_LIST);
            }, 100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            showLocationPermissionDialogAlert();
        } else if (requestCode == 201) {
            showLocationServiceDialogAlert();
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
                    findWifi(true);
                    return;
                }
                if (PermissionUtil.isLocationServiceEnable(getActivity())) {
                    IOSDialogRight.Dismiss();
                    findWifi(true);
                    return;
                }
            }
        }
    };

    private void showWifiEnableDialogAert() {
        clearWifiAdapter();
        DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000320C008"),
                LanguageProvider.getLanguage("UI000802C003"), (dialog, which) -> dialog.dismiss());
    }

    public void showLocationPermissionDialogAlert() {
        clearWifiAdapter();
        PermissionUtil.showLocationPermissionDialogAlert(getActivity(), new PermissionUtil.PermissionDialogueListener() {
            @Override
            public void onPermissionCanceled(DialogInterface dialogInterface) {
            }

            @Override
            public void onPermissionGranted() {
                findWifi(true);
            }
        });
    }

    public void showLocationServiceDialogAlert() {
        clearWifiAdapter();
        PermissionUtil.showLocationServiceDialogAlert(getActivity(), new PermissionUtil.LocationServiceDialogueListener() {
            @Override
            public void onDisabled(DialogInterface dialogInterface) {

            }

            @Override
            public void onEnabled() {
                findWifi(true);
            }
        });
    }

    private void clearWifiAdapter() {
        if (wifiScanAdapter != null) {
            lists.clear();
            wifiScanAdapter.notifyDataSetChanged();
        }
    }
}
