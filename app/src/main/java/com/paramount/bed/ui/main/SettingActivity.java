package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MonitoringModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.MonitoringResponse;
import com.paramount.bed.data.remote.response.SettingResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.TimerUtils;
import com.paramount.bed.util.alarms.AlarmsAutoScheduler;
import com.paramount.bed.util.alarms.AlarmsQuizModule;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.paramount.bed.util.alarms.AlarmsScheduler.setAllAlarms;

public class SettingActivity extends BaseActivity implements MonitoringListAdapter.MonitoringUserRowListener {
    @BindView(R.id.sbAd)
    SwitchButton sbAd;
    @BindView(R.id.sbNotice)
    SwitchButton sbNotice;
    @BindView(R.id.sbAllowMonitoring)
    SwitchButton sbAllowMonitoring;
    @BindView(R.id.sbAllowForest)
    SwitchButton sbAllowForest;
    @BindView(R.id.sbBigFont)
    SwitchButton sbBigFont;
    @BindView(R.id.sbBigFontCaption)
    TextView sbBigFontCaption;

    @BindView(R.id.monitoringWrap)
    LinearLayout monitoringWrap;
    UserService settingService;
    @BindView(R.id.monitoringView)
    RecyclerView monitoringView;
    @BindView(R.id.menu_fw)
    LinearLayout menuFWRow;


    SwitchButton.OnCheckedChangeListener ads_allowed_listener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            saveSetting("ads_allowed", String.valueOf(isChecked));
        }
    };
    SwitchButton.OnCheckedChangeListener sbNotice_listener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            saveSetting("automatic_operation_reminder_allowed", String.valueOf(isChecked));
            AlarmsAutoScheduler.setAllAlarms(SettingActivity.this);
        }
    };
    SwitchButton.OnCheckedChangeListener monitoring_allowed_listener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            saveSetting("monitoring_allowed", String.valueOf(isChecked));
            if (sbAllowMonitoring.isChecked()) {
                getMonitoring();
            } else {
                MonitoringModel.clear();
                setupViewMonitoring();
            }
        }
    };
    SwitchButton.OnCheckedChangeListener allowed_forest_listener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            saveSetting("forest_report_allowed", String.valueOf(isChecked));
        }
    };
    SwitchButton.OnCheckedChangeListener bigfont_listener = new SwitchButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(SwitchButton view, boolean isChecked) {
            view.setEnabled(false);
            DisplayUtils.FONTS.bigFontStatus(SettingActivity.this, sbBigFont.isChecked());
            restartActivity();
        }
    };
    private MonitoringListAdapter adapter;
    public static SettingActivity mInstance;
    private TimerUtils timerUtils;
    ArrayList<Integer> listTimeSleep;
    ArrayList<String> listTimeSleepLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        timerUtils = new TimerUtils(this);
        mInstance = this;
        setToolbarTitle(LanguageProvider.getLanguage("UI000750C001"));
        initOptionsTimer();
        setupViewSetting();
        settingService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        getSetting();

        sbAd.setOnCheckedChangeListener(ads_allowed_listener);
        sbNotice.setOnCheckedChangeListener(sbNotice_listener);
        sbAllowMonitoring.setOnCheckedChangeListener(monitoring_allowed_listener);
        sbAllowForest.setOnCheckedChangeListener(allowed_forest_listener);
        sbBigFont.setChecked(DisplayUtils.FONTS.bigFontStatus(SettingActivity.this));
        sbBigFont.setOnCheckedChangeListener(bigfont_listener);
        sbBigFontCaption.setText(LanguageProvider.getLanguage("UI000750C010").equals("UI000750C010") ? "Big Fonts" : LanguageProvider.getLanguage("UI000750C010"));

        if (sbAllowMonitoring.isChecked()) {
            getMonitoring();
        } else {
            MonitoringModel.clear();
            setupViewMonitoring();
        }

        isSettingActivity = true;

        menuFWRow.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, UpdateFirmwareScanActivity.class);
            startActivity(intent);
        });
    }

    private void initOptionsTimer() {
        int min = 10;
        int max = 60;
        int interval = 10;

        Integer[] listTimeSleepSetting =  FormPolicyModel.getPolicy().getTimeSleepSettingPrimitives();
        listTimeSleep = new ArrayList<>(Arrays.asList(listTimeSleepSetting));
        if(listTimeSleep.isEmpty()){
            for (int i = min; i<=max; i+=interval){
                listTimeSleep.add(i);
            }
        }

        listTimeSleepLabels = new ArrayList<>();
        for (Integer time:listTimeSleep) {
            if(time==0){
                listTimeSleepLabels.add(LanguageProvider.getLanguage("UI000750C015"));
            }else {
                listTimeSleepLabels.add(time+LanguageProvider.getLanguage("UI000750C016"));
            }
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_setting;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    Disposable mDisposable;

    private void getSetting() {
        showProgress();
        mDisposable =
                settingService.getSetting(UserLogin.getUserLogin().getId(), 1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse<SettingResponse>>() {
                            public void onSuccess(BaseResponse<SettingResponse> response) {
                                hideProgress();
                                if (response.isSucces()) {
                                    setupDataSetting(response.getData());
                                }
                                setupViewSetting();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.d("abx load content 2");
                                Log.d("abx", e.getMessage());
                                hideProgress();
                                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                    setOfflineMode();
                                } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                    DialogUtil.tokenExpireDialog(SettingActivity.this);
                                } else {
                                    DialogUtil.serverFailed(SettingActivity.this, "UI000802C045", "UI000802C046", "UI000802C047", "UI000802C048");
                                }
                                setupViewSetting();
                            }
                        });
    }

    SettingModel setting;

    private void setupDataSetting(SettingResponse settingResponse) {
        setting = new SettingModel();
        setting.truncate();
        setting.ads_allowed = settingResponse.ads_allowed;
        setting.automatic_operation_alarm_id = settingResponse.automatic_operation_alarm_id;
        setting.automatic_operation_sleep_active = settingResponse.automatic_operation_sleep_active;
        setting.monitoring_allowed = settingResponse.monitoring_allowed;
        setting.automatic_operation_bed_pattern_id = settingResponse.automatic_operation_bed_pattern_id;
        setting.automatic_operation_reminder_allowed = settingResponse.automatic_operation_reminder_allowed;
        setting.automatic_operation_wakeup_monday_active = settingResponse.automatic_operation_wakeup_monday_active;
        setting.automatic_operation_wakeup_monday_time = settingResponse.automatic_operation_wakeup_monday_time;
        setting.automatic_operation_wakeup_tuesday_active = settingResponse.automatic_operation_wakeup_tuesday_active;
        setting.automatic_operation_wakeup_tuesday_time = settingResponse.automatic_operation_wakeup_tuesday_time;
        setting.automatic_operation_wakeup_wednesday_active = settingResponse.automatic_operation_wakeup_wednesday_active;
        setting.automatic_operation_wakeup_wednesday_time = settingResponse.automatic_operation_wakeup_wednesday_time;
        setting.automatic_operation_wakeup_thursday_active = settingResponse.automatic_operation_wakeup_thursday_active;
        setting.automatic_operation_wakeup_thursday_time = settingResponse.automatic_operation_wakeup_thursday_time;
        setting.automatic_operation_wakeup_friday_active = settingResponse.automatic_operation_wakeup_friday_active;
        setting.automatic_operation_wakeup_friday_time = settingResponse.automatic_operation_wakeup_friday_time;
        setting.automatic_operation_wakeup_saturday_active = settingResponse.automatic_operation_wakeup_saturday_active;
        setting.automatic_operation_wakeup_saturday_time = settingResponse.automatic_operation_wakeup_saturday_time;
        setting.automatic_operation_wakeup_sunday_active = settingResponse.automatic_operation_wakeup_sunday_active;
        setting.automatic_operation_wakeup_sunday_time = settingResponse.automatic_operation_wakeup_sunday_time;
        setting.monitoring_questionnaire_allowed = settingResponse.monitoring_questionnaire_allowed;
        setting.monitoring_weekly_report_allowed = settingResponse.monitoring_weekly_report_allowed;
        setting.monitoring_error_report_allowed = settingResponse.monitoring_error_report_allowed;
        setting.bed_fast_mode = settingResponse.bed_fast_mode;
        setting.bed_combi_locked = settingResponse.bed_combi_locked;
        setting.bed_head_locked = settingResponse.bed_head_locked;
        setting.bed_leg_locked = settingResponse.bed_leg_locked;
        setting.bed_height_locked = settingResponse.bed_height_locked;
        setting.timer_setting = settingResponse.timer_setting;
        setting.forest_report_allowed = settingResponse.forest_report_allowed;
        setting.sleep_reset_timing = settingResponse.sleep_reset_timing;
        setting.snoring_storage_enable = settingResponse.snoring_storage_enable;
        setting.insert();
        setAllAlarms(SettingActivity.this);
    }

    public void setupViewSetting() {
        setting = SettingModel.getSetting();
        sbAd.setChecked(setting.ads_allowed);
        sbNotice.setChecked(setting.automatic_operation_reminder_allowed);
        sbAllowMonitoring.setChecked(setting.monitoring_allowed);
        timerUtils.setTimer(setting.sleep_reset_timing);
        sbAllowForest.setChecked(setting.forest_report_allowed);
    }


    Disposable mDisposableMonitoring;

    public void getMonitoring() {
        showProgress();
        mDisposableMonitoring =
                settingService.getMonitoring(UserLogin.getUserLogin().getId(), 1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<MonitoringResponse>>>() {
                            public void onSuccess(BaseResponse<ArrayList<MonitoringResponse>> response) {
                                hideProgress();
                                try {
                                    if (response.isSucces()) {
                                        setupDataMonitoring(response.getData());
                                    }
                                } catch (Exception e) {

                                }
                                setupViewMonitoring();

                            }

                            @Override
                            public void onError(Throwable e) {
                                hideProgress();
                                Timber.d("abx load content 2");
                                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                    setOfflineMode();
                                } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                    DialogUtil.tokenExpireDialog(SettingActivity.this);
                                } else {
                                    DialogUtil.serverFailed(SettingActivity.this, "UI000802C045", "UI000802C046", "UI000802C047", "UI000802C048");
                                }
                                setupViewMonitoring();
                            }
                        });
    }

    private void setupDataMonitoring(ArrayList<MonitoringResponse> monitoringResponse) {
        MonitoringModel.clear();
        for (int i = 0; i < monitoringResponse.size(); i++) {
            MonitoringModel monitoring = new MonitoringModel();
            monitoring.setId(monitoringResponse.get(i).getId());
            monitoring.setNick_name(monitoringResponse.get(i).getNick_name());
            monitoring.setStatus(monitoringResponse.get(i).getStatus());
            monitoring.insert();
        }
    }

    public void setupViewMonitoring() {
        List<MonitoringModel> monitoring = MonitoringModel.getAll();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        monitoringView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        Collections.sort(monitoring, new Comparator<MonitoringModel>() {
            @Override
            public int compare(MonitoringModel t1, MonitoringModel t2) {
                if (t1.getNick_name() == t2.getNick_name()) {
                    return 0;
                } else if (t1.getNick_name() == null) {
                    return -1;
                } else if (t2.getNick_name() == null) {
                    return 1;
                } else {
                    return t1.getNick_name().compareToIgnoreCase(t2.getNick_name());
                }
            }
        });
        adapter = new MonitoringListAdapter(SettingActivity.this, this, monitoring, this);
        monitoringView.setAdapter(adapter);

        final float scale = getResources().getDisplayMetrics().density;
        int paddingTop = (int) (4 * scale + 0.5f);

        if (monitoringView.getAdapter() != null && monitoring.size() > 0) {
            monitoringWrap.setVisibility(View.VISIBLE);
            monitoringWrap.setPadding(0,paddingTop,0,0);
        }else {
            monitoringWrap.setPadding(0,0,0,0);
        }
    }


    Disposable mDisposableSaveSetting;

    @Override
    public void onMonitoringUserRowEdit(MonitoringModel selectedUser) {
        setupViewMonitoring();
    }

    public class VMSaveSetting {
        String key;
        String value;
    }

    @SuppressLint("CheckResult")
    public void saveSetting(String key, String value) {
        SettingModel.saveSetting(key, value);
        ArrayList<VMSaveSetting> isave = new ArrayList<>();
        VMSaveSetting a = new VMSaveSetting();
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
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            setOfflineMode();
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(SettingActivity.this);
                        } else {
                            DialogUtil.serverFailed(SettingActivity.this, "UI000802C045", "UI000802C046", "UI000802C047", "UI000802C048");
                        }

                        if(key.equals("sleep_reset_timing")){
                            if (UserLogin.getUserLogin() != null) {
                                try {
                                    LogUserAction.sendNewLog(userService, "STOP_SLEEP_SETTING_FAILED", "", UserLogin.getUserLogin().getScanSerialNumber(), "UI000507");
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        isSettingActivity = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HomeActivity.drawerLayout.closeDrawer(GravityCompat.START, false);
        isSettingActivity = false;
    }

    public void setOfflineMode() {
        LogUserAction.sendNewLog(userService, "INTERNET_CONNECTION_FAILED", "", "", "UI000507");
        DialogUtil.offlineDialog(SettingActivity.this, getApplicationContext());
        setupViewSetting();
        setupViewMonitoring();
        sbAd.setEnabled(false);
        sbNotice.setEnabled(false);
        sbAllowForest.setEnabled(false);
        sbAllowMonitoring.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (DisplayUtils.FONTS.needRestart(this)) {
            finishAffinity();
            Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }
    }

    public void restartActivity() {
        Intent intent = getIntent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
