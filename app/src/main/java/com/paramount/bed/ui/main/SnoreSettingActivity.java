package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;
import com.paramount.bed.R;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SnoreSettingActivity extends BaseActivity {


    SettingModel setting;
    UserService settingService;

    @BindView(R.id.sbEnableStorage)
    SwitchButton sbEnableStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        settingService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        getSetting();

        sbEnableStorage.setOnCheckedChangeListener(enable_storage_listener);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_snore_setting;
    }

    @OnClick(R.id.btnBack)
    void back(){
        onBackPressed();
    }

    SwitchButton.OnCheckedChangeListener enable_storage_listener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            saveSetting("snoring_storage_enable", String.valueOf(isChecked?1:0));
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    private void getSetting() {
        setupViewSetting();

        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
            setOfflineMode();
        }
    }

    public class VMSaveSetting {
        String key;
        String value;
    }
    
    @SuppressLint("CheckResult")
    private void saveSetting(String key, String value) {
        ArrayList<SnoreSettingActivity.VMSaveSetting> isave = new ArrayList<>();
        SnoreSettingActivity.VMSaveSetting a = new SnoreSettingActivity.VMSaveSetting();
        a.key = key;
        a.value = value;
        isave.add(a);
        Gson gs = new Gson();
        String t = gs.toJson(isave);
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.saveSetting(UserLogin.getUserLogin().getId(), t, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                        if(key.equals("snoring_storage_enable")){
                            SettingModel.saveSetting(key, value);
                            LogUserAction.sendNewLog(userService,"SNORING_SETTING","","","UI000561");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            setOfflineMode();
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(SnoreSettingActivity.this);
                        } else {
                            DialogUtil.serverFailed(SnoreSettingActivity.this, "UI000802C045", "UI000802C046", "UI000802C047", "UI000802C048");
                        }
                    }
                });
    }

    public void setOfflineMode() {
        LogUserAction.sendNewLog(userService,"INTERNET_CONNECTION_FAILED","","","UI000561");
        DialogUtil.offlineDialog(SnoreSettingActivity.this, getApplicationContext());
        sbEnableStorage.setEnabled(false);
    }

    public void setupViewSetting() {
        setting = SettingModel.getSetting();
        sbEnableStorage.setChecked(setting.snoring_storage_enable==1);
    }
}
