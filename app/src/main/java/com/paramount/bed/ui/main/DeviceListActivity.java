package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.SleepResetModel;
import com.paramount.bed.data.model.TutorialShowModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.SettingProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.NemuriScanService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NemuriScanUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.RxUtil;
import com.paramount.bed.util.TokenExpiredReceiver;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.paramount.bed.util.alarms.AlarmsScheduler.setAllAlarms;

public class DeviceListActivity extends BaseActivity implements NemuriScanUtil.NemuriScanDetailFetchListener {
    @BindView(R.id.textViewNSSerialnumber)
    TextView nsSerialNumberTextView;
    @BindView(R.id.textLastConnection)
    TextView textLastConnection;
    @BindView(R.id.nsContainer)
    ConstraintLayout containerConstraintLayout;
    @BindView(R.id.nsContainerBed)
    ConstraintLayout containerConstraintLayoutBed;
    @BindView(R.id.nsContainerMat)
    ConstraintLayout containerConstraintLayoutMat;
    @BindView(R.id.imageView12)
    ImageView imageView12;
    @BindView(R.id.imageView13)
    ImageView imageView13;
    @BindView(R.id.imageView14)
    ImageView imageView14;
    @BindView(R.id.textView35)
    TextView bedTitle;

    Disposable mDisposable;
    NemuriScanModel nemuriScanModel = NemuriScanModel.get();
    TokenExpiredReceiver tokenExpiredReceiver = new TokenExpiredReceiver();
    private SettingProvider settingProvider;

    @OnClick(R.id.add_device)
    void onAddDeviceTap() {
        if (nemuriScanModel != null) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000730C008"), LanguageProvider.getLanguage("UI000730C009"), null);
        } else {
            Intent intent = new Intent(this, RegistrationStepActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("is_registration", false);
            startActivityForResult(intent, 101);
        }
    }

    @OnClick(R.id.btnDeleteNS)
    void onButtonDeleteTap() {
        NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
        if(nemuriScanModel == null){
            return;
        }
        LogUserAction.sendNewLog(userService, "DELETE_DEVICE_UPDATE_FAILED", String.valueOf(nemuriScanModel.isFWUpdateFailed()), "", "UI000730");
        DialogUtil.createCustomYesNo(this, "", LanguageProvider.getLanguage("UI000730C005"), LanguageProvider.getLanguage("UI000730C007"),
                (dialogInterface, i) -> dialogInterface.dismiss(),
                LanguageProvider.getLanguage("UI000730C006"), (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                        DialogUtil.offlineDialog(DeviceListActivity.this, getApplicationContext());
                    } else {
                        deleteNemuriScan();
                    }
                });
    }

    @OnClick(R.id.btnWifiSetting)
    void onButtonWifiTap(){
        NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
        if(nemuriScanModel == null){
            return;
        }
        LogUserAction.sendNewLog(userService, "EDIT_WIFI_UPDATE_FAILED", String.valueOf(nemuriScanModel.isFWUpdateFailed()), "", "UI000730");
        Intent intent = new Intent(this, RegistrationStepActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("is_registration", false);
        intent.putExtra("is_wifi_only", true);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);

        settingProvider = new SettingProvider(this);
        imageView12.setImageResource(R.drawable.edit_01);
        imageView13.setImageResource(R.drawable.bed_sample);
        imageView14.setImageResource(R.drawable.mat_sample);
        updateUIState();
        checkNSDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        AlarmsQuizModule.run(this);
        updateUIState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_device_list;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    private void deleteNemuriScan() {
        showLoading();
        nemuriScanService = ApiClient.getClient(this).create(NemuriScanService.class);
        mDisposable = nemuriScanService.deleteNemuriScan(UserLogin.getUserLogin().getId(), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                        hideLoading();
                        boolean isSuccess = response.getSuccess();
                        if (isSuccess) {
                            NemuriScanModel.clear();
                            TutorialShowModel.resetRemoteTutorial();
                            UserLogin.clearRegisteredNS();
                            SleepResetModel.clear();
                        }
                        DialogUtil.createSimpleOkDialog(DeviceListActivity.this, "",
                                LanguageProvider.getLanguage(response.getMessage()), LanguageProvider.getLanguage("UI000802C003"), ((dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    checkNSDetail();
                                }));
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        if (!NetworkUtil.isNetworkConnected(DeviceListActivity.this)) {
                            DialogUtil.offlineDialog(DeviceListActivity.this, DeviceListActivity.this);
                        } else if (MultipleDeviceUtil.checkForceLogout(e)) {
                            MultipleDeviceUtil.sendBroadCast(DeviceListActivity.this);
                        } else {
                            DialogUtil.serverFailed(DeviceListActivity.this, "UI000802C069", "UI000802C070", "UI000802C071", "UI000802C072");
                        }
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        if (HomeActivity.drawerLayout != null) {
            HomeActivity.drawerLayout.closeDrawer(GravityCompat.START, false);
        }
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtil.dispose(mDisposable);
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
        HomeActivity.drawerLayout.closeDrawer(GravityCompat.START, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            checkNSDetail();
        }
    }

    @Override
    public void onNemuriScanDetailFetched(NemuriScanModel nemuriScanDetailModel) {
        settingProvider.getSetting((settingModel, isSuccess, e) -> {
            runOnUiThread(() -> {
                hideLoading();
                updateUIState();
                if (MultipleDeviceUtil.checkForceLogout(e)) {
                    MultipleDeviceUtil.sendBroadCast(DeviceListActivity.this);
                }
            });
        });

    }

    private void updateUIState() {
        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        if (nemuriScanModel != null && nemuriScanModel.getInfoType() != null && nemuriScanModel.getInfoType() == 2) {
            switch (nemuriScanModel.getInfoType()) {
                case 2:
                    imageView13.setImageResource(R.drawable.intime_sample);
                    bedTitle.setText(LanguageProvider.getLanguage("UI000730C013"));
                    break;
                case 3:
                    imageView13.setImageResource(R.drawable.intime_sample); // あとで変更：正式画像を受領したら修正
                    bedTitle.setText(LanguageProvider.getLanguage("UI000730C014")); // あとで変更：正式機器名を受領したら修正
                    break;
                default:
                    imageView13.setImageResource(R.drawable.bed_sample);
                    bedTitle.setText(LanguageProvider.getLanguage("UI000730C010"));
            }
        } else {
            imageView13.setImageResource(R.drawable.bed_sample);
            bedTitle.setText(LanguageProvider.getLanguage("UI000730C010"));
        }
        containerConstraintLayoutBed.setVisibility(NemuriScanModel.getBedActive() ? View.VISIBLE : View.GONE);
        containerConstraintLayoutMat.setVisibility(NemuriScanModel.getMattressActive() ? View.VISIBLE : View.GONE);
        textLastConnection.setText(LanguageProvider.getLanguage("UI000730C012").replace("%LAST_CONNECTION_TIME%", getLastConnection()));
    }

    @SuppressLint("SetTextI18n")
    private void checkNSDetail() {
        nemuriScanModel = NemuriScanModel.get();
        if (nemuriScanModel == null) {
            applyNoNSUI();
            settingProvider.noNSSetting((isSuccess) -> {
                runOnUiThread(() -> {
                    SettingModel.resetNSRelatedSettings();
                });
            });
            return;
        }
        runOnUiThread(() -> {
            containerConstraintLayout.setVisibility(View.VISIBLE);
            NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
            if(nemuriScanModel != null) {
                if (nemuriScanModel.isDefaultFWVersion()) {
                    nsSerialNumberTextView.setText(nemuriScanModel.getSerialNumber());
                } else {
                    nsSerialNumberTextView.setText(nemuriScanModel.getSerialNumber() + " - " + LanguageProvider.getLanguage("UI000730C017") + nemuriScanModel.getMajor() + "." + nemuriScanModel.getMinor() + "." + nemuriScanModel.getRevision());
                }
                textLastConnection.setText(LanguageProvider.getLanguage("UI000730C012").replace("%LAST_CONNECTION_TIME%", getLastConnection()));
            }
        });
        NemuriScanUtil.fetchSpec(DeviceListActivity.this, DeviceListActivity.this);
    }

    private void applyNoNSUI() {
        runOnUiThread(() -> {
            containerConstraintLayout.setVisibility(View.GONE);
            containerConstraintLayoutBed.setVisibility(View.GONE);
            containerConstraintLayoutMat.setVisibility(View.GONE);
            setAllAlarms(getApplicationContext());
        });
    }

    private String getLastConnection() {
        String lastConnectionTime = "-";
        if (!NemuriScanModel.getLastConnect().isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date lastConnection;
            try {
                lastConnection = dateFormat.parse(NemuriScanModel.getLastConnect());
                DateFormat df = new SimpleDateFormat("M/d H:mm");
                String strLastConnection = df.format(lastConnection);
                lastConnectionTime = strLastConnection;
            } catch (ParseException e) {

            }
        }
        return lastConnectionTime;
    }
}
