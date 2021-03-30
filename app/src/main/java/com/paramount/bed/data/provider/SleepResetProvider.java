package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.SleepResetModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.TimeSleepResetStatusResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.RetryWithDelay;
import com.paramount.bed.util.TimerUtils;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmQuery;

public class SleepResetProvider {

    public static SleepResetModel getSleepReset(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepResetModel> query = realm.where(SleepResetModel.class);
        SleepResetModel result = query.findFirst();
        return result;
    }
    public static void deleteSleepReset(){
        SleepResetModel sleepResetModel = SleepResetProvider.getSleepReset();
        if(sleepResetModel != null){
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            sleepResetModel.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    @SuppressLint("CheckResult")
    public static void startSleepReset(Context context,StartSleepResetListener listener, int retryCount){
        UserLogin userData = UserLogin.getUserLogin();
        UserService userService = ApiClient.getClient(context).create(UserService.class);
        if(userData != null){
            SettingModel userSetting = SettingModel.getSetting();
            LogUserAction.sendNewLog(userService, "STOP_SLEEP_START", "", "", "UI000506");

            userService.sendSleepResetStart(userData.getId(),userSetting.sleep_reset_timing)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .retryWhen(new RetryWithDelay(BuildConfig.MAX_RETRY, BuildConfig.REQUEST_TIME_OUT))
                    .subscribeWith(new DisposableObserver<BaseResponse<TimeSleepResetStatusResponse>>() {
                        @Override
                        public void onNext(BaseResponse<TimeSleepResetStatusResponse> timeSleepResetStatusResponseBaseResponse) {
                            if(timeSleepResetStatusResponseBaseResponse != null && timeSleepResetStatusResponseBaseResponse.isSucces()){
                                listener.onFinish(timeSleepResetStatusResponseBaseResponse,true,"");
                            }else if(timeSleepResetStatusResponseBaseResponse != null){
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    String message = LanguageProvider.getLanguage(timeSleepResetStatusResponseBaseResponse.getMessage());
                                    listener.onFinish(timeSleepResetStatusResponseBaseResponse,false,message);
                                }
                            } else {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    listener.onFinish(null,false,"UI000802C001");
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (!NetworkUtil.isNetworkConnected(context)) {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    listener.onFinish(null,false,"UI000802C002");
                                }
                            } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    DialogUtil.tokenExpireDialog(context);
                                    listener.onFinish(null,false,"UI000802C001");
                                }
                            } else {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    listener.onFinish(null,false,"UI000802C001");
                                }
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else{
            LogUserAction.sendNewLog(userService, "STOP_SLEEP_FAILED", "Invalid user data", UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
            listener.onFinish(null,false,"");
        }
    }

    @SuppressLint("CheckResult")
    public static void stopSleepReset(Context context,StopSleepResetListener listener, int retryCount){
        UserLogin userData = UserLogin.getUserLogin();
        UserService userService = ApiClient.getClient(context).create(UserService.class);
        if(userData != null){
            SettingModel userSetting = SettingModel.getSetting();

            userService.sendSleepResetStop(userData.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<BaseResponse>() {
                        @Override
                        public void onNext(BaseResponse baseResponse) {
                            if(baseResponse != null && !baseResponse.isSucces()){
                                String message = LanguageProvider.getLanguage(baseResponse.getMessage());
                                listener.onFinish(baseResponse,false,message);
                            }else if(baseResponse == null){
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Logger.d("STOP SLEEP RESET: " + SleepResetProvider.class.getSimpleName());
                                            stopSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    listener.onFinish(null,false,"UI000802C001");
                                }
                            }else{
                                listener.onFinish(baseResponse,true,"");
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (!NetworkUtil.isNetworkConnected(context)) {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Logger.d("STOP SLEEP RESET: " + SleepResetProvider.class.getSimpleName());
                                            stopSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    listener.onFinish(null,false,"UI000802C002");
                                }
                            } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Logger.d("STOP SLEEP RESET: " + SleepResetProvider.class.getSimpleName());
                                            stopSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    DialogUtil.tokenExpireDialog(context);
                                    listener.onFinish(null,false,"UI000802C001");
                                }
                            } else {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Logger.d("STOP SLEEP RESET: " + SleepResetProvider.class.getSimpleName());
                                            stopSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    listener.onFinish(null,false,"UI000802C001");
                                }
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else{
            listener.onFinish(null,false,"");
        }
    }

    @SuppressLint("CheckResult")
    public static void fetchSleepReset(Context context,StartSleepResetListener listener, int retryCount){
        UserLogin userData = UserLogin.getUserLogin();
        UserService userService = ApiClient.getClient(context).create(UserService.class);
        if(userData != null){
            userService.getSleepResetStatus(userData.getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<BaseResponse<TimeSleepResetStatusResponse>>() {
                        @Override
                        public void onNext(BaseResponse<TimeSleepResetStatusResponse> timeSleepResetStatusResponseBaseResponse) {
                            if(timeSleepResetStatusResponseBaseResponse != null && timeSleepResetStatusResponseBaseResponse.isSucces()){
                                listener.onFinish(timeSleepResetStatusResponseBaseResponse,true,"");
                            }else if(timeSleepResetStatusResponseBaseResponse != null){
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Logger.d("FETCH SLEEP RESET: " + SleepResetProvider.class.getSimpleName());
                                            fetchSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else{
                                    String message = LanguageProvider.getLanguage(timeSleepResetStatusResponseBaseResponse.getMessage());
                                    listener.onFinish(timeSleepResetStatusResponseBaseResponse,false,message);
                                }
                            } else {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Logger.d("FETCH SLEEP RESET: " + SleepResetProvider.class.getSimpleName());
                                            fetchSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    listener.onFinish(null, false, "UI000802C001");
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            if (!NetworkUtil.isNetworkConnected(context)) {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Logger.d("FETCH SLEEP RESET: " + SleepResetProvider.class.getSimpleName());
                                            fetchSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    listener.onFinish(null,false,"UI000802C002");
                                }
                            } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Logger.d("FETCH SLEEP RESET: " + SleepResetProvider.class.getSimpleName());
                                            fetchSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    DialogUtil.tokenExpireDialog(context);
                                    listener.onFinish(null,false,"UI000802C001");
                                }
                            } else {
                                if(retryCount<BuildConfig.MAX_RETRY){
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Logger.d("FETCH SLEEP RESET: " + SleepResetProvider.class.getSimpleName());
                                            fetchSleepReset(context,listener,retryCount+1);
                                        }
                                    },BuildConfig.REQUEST_TIME_OUT);
                                }else {
                                    listener.onFinish(null,false,"UI000802C001");
                                }
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }else{
            LogUserAction.sendNewLog(userService, "STOP_SLEEP_FAILED", "Invalid user data", "", "");
            listener.onFinish(null,false,"");
        }
    }

    public static void updateSleepResetModel(TimeSleepResetStatusResponse data){

        if( data != null && data.getSleepResetDatetime() != null){
            Date convetedDate = TimerUtils.parseDateTime(data.getSleepResetDatetime());
            if(convetedDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(convetedDate);
                cal.add(Calendar.SECOND, data.getSleepResetRemains());
                Date endDate = cal.getTime();

                SleepResetModel sleepResetModel = SleepResetProvider.getSleepReset();
                if(sleepResetModel == null){
                    sleepResetModel = SleepResetModel.create();
                    sleepResetModel.updateStartDate(convetedDate);
                    sleepResetModel.updateEndDate(endDate);
                    sleepResetModel.insert();
                }else{
                    sleepResetModel.updateStartDate(convetedDate);
                    sleepResetModel.updateEndDate(endDate);
                    sleepResetModel.updateBackgroundDate(null);
                }
            }
        }
    }

    public static void setBackgroundDate(){
        SleepResetModel sleepResetModel = SleepResetProvider.getSleepReset();
        if(sleepResetModel != null){
            sleepResetModel.updateBackgroundDate(new Date());
        }
    }
    public interface StartSleepResetListener{
        void onFinish(BaseResponse<TimeSleepResetStatusResponse> result, boolean isSuccess, String errTag);
    }

    public interface StopSleepResetListener{
        void onFinish(BaseResponse result, boolean isSuccess, String errTag);
    }
}
