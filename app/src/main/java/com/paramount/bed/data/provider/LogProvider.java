package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import com.google.gson.Gson;
import com.paramount.bed.data.model.DeviceSettingBedModelLog;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateBedModelLog;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModelLog;
import com.paramount.bed.data.model.MaxRowModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;

import java.util.ArrayList;

import javax.annotation.Nullable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.paramount.bed.util.LogUtil.Logx;

public class LogProvider {
    @SuppressLint("CheckResult")
    public static void logBedTemplateChange(Activity activity, @Nullable DeviceTemplateBedModel target, Integer bedType, BedTemplateChangeListener bedTemplateChangeListener) {
        if (target != null) {
            DeviceTemplateBedModelLog.insertLogBedTemplateChangeOffline(target);
        }
        if (!activity.isFinishing()) {
            if (!NetworkUtil.isNetworkConnected(activity.getApplicationContext())) {
                if (bedTemplateChangeListener != null) bedTemplateChangeListener.onDone();
                return;
            }
            DeviceTemplateBedModelLog logData = DeviceTemplateBedModelLog.getFirst();
            if (logData != null) {
                UserService sService = ApiClient.getClient(activity.getApplicationContext()).create(UserService.class);
                Single<BaseResponse> method;
                if (bedType == null) {
                    method = sService.sendBedTemplate(UserLogin.getUserLogin().getId(), logData.getId(), logData.getHead(), logData.getLeg(), logData.getTilt(), logData.getHeight(), 1);
                } else {
                    method = sService.sendBedTemplate(UserLogin.getUserLogin().getId(), logData.getId(), logData.getHead(), logData.getLeg(), logData.getTilt(), logData.getHeight(), bedType, 1);
                }
                method.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                        public void onSuccess(BaseResponse response) {
                            logData.delete();
                            if (!activity.isFinishing()) {
                                logBedTemplateChange(activity, null, bedType, bedTemplateChangeListener);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (bedTemplateChangeListener != null)
                                bedTemplateChangeListener.onDone();
                        }
                    });
            } else {
                if (bedTemplateChangeListener != null) bedTemplateChangeListener.onDone();
            }
        } else {
            if (bedTemplateChangeListener != null) bedTemplateChangeListener.onDone();
        }
    }

    public interface BedTemplateChangeListener {
        void onDone();
    }

    @SuppressLint("CheckResult")
    public static void logMattressTemplateChange(Activity activity, @Nullable DeviceTemplateMattressModel
            target, MattressTemplateChangeListener mattressTemplateChangeListener) {
        if (target != null) {
            DeviceTemplateMattressModelLog.insertLogMattressTemplateChangeOffline(target);
        }
        if (!activity.isFinishing()) {
            if (!NetworkUtil.isNetworkConnected(activity.getApplicationContext())) {
                if (mattressTemplateChangeListener != null) mattressTemplateChangeListener.onDone();
                return;
            }
            DeviceTemplateMattressModelLog logData = DeviceTemplateMattressModelLog.getFirst();
            if (logData != null) {
                UserService sService = ApiClient.getClient(activity.getApplicationContext()).create(UserService.class);
                sService.sendMatressTemplate(UserLogin.getUserLogin().getId(),
                        logData.getId(),
                        logData.getHead(),
                        logData.getShoulder(),
                        logData.getHip(),
                        logData.getThigh(),
                        logData.getCalf(),
                        logData.getFeet(),
                        1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                            public void onSuccess(BaseResponse response) {
                                logData.delete();
                                if (!activity.isFinishing()) {
                                    logMattressTemplateChange(activity, null, mattressTemplateChangeListener);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (mattressTemplateChangeListener != null)
                                    mattressTemplateChangeListener.onDone();
                            }
                        });
            } else {
                if (mattressTemplateChangeListener != null) mattressTemplateChangeListener.onDone();
            }
        } else {
            if (mattressTemplateChangeListener != null) mattressTemplateChangeListener.onDone();
        }
    }

    public interface MattressTemplateChangeListener {
        void onDone();
    }

    @SuppressLint("CheckResult")
    public static void logBedSettingChange(Activity activity, int fastMode, int combiLock,
                                           int headLock, int legLock, int heightLock, boolean isLooping) {

        if (!isLooping) {
            //prepare json
            ArrayList<LogSettingPair> settingPairs = new ArrayList<>();

            LogSettingPair fastModePair = new LogSettingPair();
            fastModePair.key = "bed_fast_mode";
            fastModePair.value = String.valueOf(fastMode);
            settingPairs.add(fastModePair);

            LogSettingPair combiLockPair = new LogSettingPair();
            combiLockPair.key = "bed_combi_locked";
            combiLockPair.value = String.valueOf(combiLock);
            settingPairs.add(combiLockPair);

            LogSettingPair headLockPair = new LogSettingPair();
            headLockPair.key = "bed_head_locked";
            headLockPair.value = String.valueOf(headLock);
            settingPairs.add(headLockPair);

            LogSettingPair legLockPair = new LogSettingPair();
            legLockPair.key = "bed_leg_locked";
            legLockPair.value = String.valueOf(legLock);
            settingPairs.add(legLockPair);

            LogSettingPair heightLockPair = new LogSettingPair();
            heightLockPair.key = "bed_height_locked";
            heightLockPair.value = String.valueOf(heightLock);
            settingPairs.add(heightLockPair);

            Gson gson = new Gson();
            String jsonString = gson.toJson(settingPairs);
            DeviceSettingBedModelLog.insertLogBedSettingChangeOffline(jsonString);
        }

        if (!activity.isFinishing()) {
            if (!NetworkUtil.isNetworkConnected(activity.getApplicationContext())) {
                return;
            }
            DeviceSettingBedModelLog logData = DeviceSettingBedModelLog.getFirst();
            if (logData != null) {
                UserService sService = ApiClient.getClient(activity.getApplicationContext()).create(UserService.class);
                sService.saveSetting(UserLogin.getUserLogin().getId(), logData.getJsonString(), 1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                            public void onSuccess(BaseResponse response) {
                                logData.delete();
                                if (!activity.isFinishing()) {
                                    logBedSettingChange(activity, 0, 0, 0, 0, 0, true);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        });
            }
        }
    }
}

class LogSettingPair {
    String key;
    String value;
}
