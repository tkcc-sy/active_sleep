package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import com.google.gson.Gson;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.SettingResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.util.MultipleDeviceUtil;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SettingProvider {

    private UserService settingService;
    private Context ctx;

    public SettingProvider(Context ctx) {
        this.ctx = ctx;
        this.settingService = ApiClient.getClient(ctx).create(UserService.class);
    }

    @SuppressLint("CheckResult")
    public void getSetting(SettingFetchListener listener) {
        settingService.getSetting(UserLogin.getUserLogin().getId(), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<SettingResponse>>() {
                    public void onSuccess(BaseResponse<SettingResponse> response) {
                        if (response != null && response.isSucces() && response.getData() != null) {
                            setupDataSetting(response.getData());
                        }
                        if (listener != null) {
                            listener.onSettingFetched(SettingModel.getFirst(), true, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            listener.onSettingFetched(SettingModel.getFirst(), false, e);
                        }
                    }
                });
    }

    private void setupDataSetting(SettingResponse settingResponse) {
        SettingModel.truncate();
        SettingModel setting = new SettingModel();
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
        setting.autodriveDegreeSetting = settingResponse.autodriveDegreeSetting;
        setting.user_desired_hardness = settingResponse.user_desired_hardness;
        setting.sleep_reset_timing = settingResponse.sleep_reset_timing;
        setting.forest_report_allowed = settingResponse.forest_report_allowed;
        setting.snoring_storage_enable = settingResponse.snoring_storage_enable;
        setting.insert();
    }

    @SuppressLint("CheckResult")
    public void saveSetting(String key, String value, SettingSaveListener listener) {
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
                        if (listener != null) {
                            listener.onSettingSaved(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            MultipleDeviceUtil.checkForceLogout(e);
                            listener.onSettingSaved(false);
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void noNSSetting(SettingSaveListener listener) {
        if(UserLogin.haveRegisteredNS()){
            //do not reset if NS regstered in server
            listener.onSettingSaved(false);
            return;
        }
        ArrayList<VMSaveSetting> isave = new ArrayList<>();
        isave.add(new VMSaveSetting("automatic_operation_sleep_active", "false"));

        isave.add(new VMSaveSetting("automatic_operation_wakeup_sunday_active", "false"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_monday_active", "false"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_tuesday_active", "false"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_wednesday_active", "false"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_thursday_active", "false"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_friday_active", "false"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_saturday_active", "false"));

        isave.add(new VMSaveSetting("automatic_operation_wakeup_sunday_time", "07:00"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_monday_time", "07:00"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_tuesday_time", "07:00"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_wednesday_time", "07:00"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_thursday_time", "07:00"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_friday_time", "07:00"));
        isave.add(new VMSaveSetting("automatic_operation_wakeup_saturday_time", "07:00"));
        Gson gs = new Gson();
        String t = gs.toJson(isave);
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.saveSetting(UserLogin.getUserLogin().getId(), t, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                        if (listener != null) {
                            listener.onSettingSaved(true);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (listener != null) {
                            listener.onSettingSaved(false);
                        }
                    }
                });
    }

    private static Activity getActivity(Context context) {
        if (context == null) return null;
        if (context instanceof Activity) return (Activity) context;
        if (context instanceof ContextWrapper)
            return getActivity(((ContextWrapper) context).getBaseContext());
        return null;
    }

    public interface SettingFetchListener {
        void onSettingFetched(SettingModel settingModel, boolean isSuccess, Throwable e);
    }

    public interface SettingSaveListener {
        void onSettingSaved(boolean isSuccess);
    }

    public class VMSaveSetting {
        String key;
        String value;

        public VMSaveSetting() {
        }

        public VMSaveSetting(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
