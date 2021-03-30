package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.MHSModel;
import com.paramount.bed.data.model.MattressHardnessSettingModel;
import com.paramount.bed.data.model.MattressSettingModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.MattressSettingResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.login.ForgotIDInputActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.NetworkUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;


public class MattressSettingProvider {
    public static void fetchMattressSetting(Context ctx, MattressSettingProviderListener listener, int retryCount) {
        UserService userService = ApiClient.getClient(ctx).create(UserService.class);
        UserLogin userData = UserLogin.getUserLogin();
        if(userData != null){
            userService.getMattressSetting((userData.getId())).enqueue(new Callback<BaseResponse<MattressSettingModel>>() {
                @Override
                public void onResponse(Call<BaseResponse<MattressSettingModel>> call, Response<BaseResponse<MattressSettingModel>> response) {
                    if(response.body() != null){
                        MattressSettingModel settingModel = response.body().getData();
                        if (settingModel != null && response.body().isSucces()) {
                            MattressSettingProvider.setSetting(settingModel);
                            listener.onMattressSettingFetched(true,settingModel);
                        }else{
                            listener.onMattressSettingFetched(false,null);
                        }
                    }else{
                        if(retryCount< BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Logger.d("FETCH MATTRESS SETTING: " + MattressSettingProvider.class.getSimpleName());
                                    fetchMattressSetting(ctx, listener, retryCount+1);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            listener.onMattressSettingFetched(false,null);
                        }
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<MattressSettingModel>> call, Throwable t) {
                    if(retryCount< BuildConfig.MAX_RETRY){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Logger.d("FETCH MATTRESS SETTING: " + MattressSettingProvider.class.getSimpleName());
                                fetchMattressSetting(ctx, listener, retryCount+1);
                            }
                        },BuildConfig.REQUEST_TIME_OUT);
                    }else {
                        if(listener != null){
                            listener.onMattressSettingFetched(false,null);
                        }
                    }
                }
            });
        }else{
            if(listener != null){
                listener.onMattressSettingFetched(false,null);
            }
        }
    }
    public static void setSetting(MattressSettingModel setting){
        MattressSettingModel.clear();
        setting.insert();
    }
    public static void setMattressSetting(Context ctx, MattressHardnessSettingModel setting, MattressHardnessSettingListener listener, int retryCount) {
        UserService userService = ApiClient.getClient(ctx).create(UserService.class);
        UserLogin userData = UserLogin.getUserLogin();
        if(userData != null){
            userService.setMattressSetting(userData.getId(),setting.getId()).enqueue(new Callback<BaseResponse<MattressSettingModel>>() {
                @Override
                public void onResponse(Call<BaseResponse<MattressSettingModel>> call, Response<BaseResponse<MattressSettingModel>> response) {
                     if(response.body() != null){
                        MattressSettingModel settingModel = response.body().getData();
                        if (settingModel != null && response.body().isSucces()) {
                            listener.onSetMattressSetting(true,settingModel,"");
                        }else{
                            listener.onSetMattressSetting(false,settingModel,response.body().getMessage());
                        }
                    }else{
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setMattressSetting(ctx, setting, listener, retryCount+1);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            listener.onSetMattressSetting(false,null,"UI000802C001");
                        }
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<MattressSettingModel>> call, Throwable t) {
                    if (!NetworkUtil.isNetworkConnected(ctx)) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setMattressSetting(ctx, setting, listener, retryCount+1);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            listener.onSetMattressSetting(false,null,"UI000802C002");
                        }
                    } else {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setMattressSetting(ctx, setting, listener, retryCount+1);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            listener.onSetMattressSetting(false,null,"UI000802C001");
                        }
                    }
                }
            });
        }else{
            if(listener != null){
                listener.onSetMattressSetting(false,null,"");
            }
        }
    }
    public static void applyMattressSetting(Context ctx, MHSModel mhsModel, MattressApplyMHSListener listener, int retryCount) {
        UserService userService = ApiClient.getClient(ctx).create(UserService.class);
        UserLogin userData = UserLogin.getUserLogin();
        SettingModel setting = SettingModel.getSetting();

        if(userData != null && mhsModel != null){
            RealmList<Integer> hardness = mhsModel.getMattressHardness();
            Integer head = hardness.get(0);
            Integer shoulder = hardness.get(1);
            Integer hip = hardness.get(2);
            Integer thigh = hardness.get(3);
            Integer calf = hardness.get(4);
            Integer feet = hardness.get(5);
            if(head != null && shoulder != null && hip != null && thigh != null && calf != null && feet != null){

                userService.applyMattressSetting(userData.getId(),head,shoulder,hip,thigh,calf,feet,setting.getUser_desired_hardness()).enqueue(new Callback<BaseResponse<MattressSettingModel>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<MattressSettingModel>> call, Response<BaseResponse<MattressSettingModel>> response) {
                        if(response.body() != null){
                            MattressSettingModel settingModel = response.body().getData();
                            if (settingModel != null && response.body().isSucces()) {
                                listener.onMHSApplied(true,settingModel,"");
                            }else{
                                listener.onMHSApplied(false,settingModel,response.body().getMessage());
                            }
                        }else{
                            if(retryCount<BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Logger.d("APPLY MATTRESS SETTING: " + MattressSettingProvider.class.getSimpleName());
                                        applyMattressSetting(ctx,mhsModel,listener,retryCount+1);
                                    }
                                }, BuildConfig.REQUEST_TIME_OUT);
                            }else {
                                listener.onMHSApplied(false,null,"UI000802C001");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<MattressSettingModel>> call, Throwable t) {
                        if (!NetworkUtil.isNetworkConnected(ctx)) {
                            if(retryCount<BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        applyMattressSetting(ctx,mhsModel,listener,retryCount+1);
                                    }
                                }, BuildConfig.REQUEST_TIME_OUT);
                            }else {
                                listener.onMHSApplied(false,null,"UI000802C002");
                            }
                        } else {
                            if(retryCount<BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        applyMattressSetting(ctx,mhsModel,listener,retryCount+1);
                                    }
                                }, BuildConfig.REQUEST_TIME_OUT);
                            }else {
                                listener.onMHSApplied(false,null,"UI000802C001");
                            }
                        }
                    }
                });
            }else{
                listener.onMHSApplied(false,null,"");
            }
        }else{
            listener.onMHSApplied(false,null,"");
        }
    }

    public static MattressSettingModel getSetting(){
        return MattressSettingModel.get();
    }

    public interface MattressSettingProviderListener{
        void onMattressSettingFetched(boolean isSuccess, MattressSettingModel result);
    }

    public interface MattressHardnessSettingListener{
        void onSetMattressSetting(boolean isSuccess, MattressSettingModel result,String errTag);
    }

    public interface MattressApplyMHSListener{
        void onMHSApplied(boolean isSuccess, MattressSettingModel result,String errTag);
    }
}


