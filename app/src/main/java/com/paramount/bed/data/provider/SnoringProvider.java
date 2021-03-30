package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.LogUserModel;
import com.paramount.bed.data.model.PendingMHSModel;
import com.paramount.bed.data.model.PendingSnoringModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.SleepQuestionnaireModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.paramount.bed.util.LogUtil.Logx;

public class SnoringProvider {

    public interface SnoringListener{
        void onFinished(boolean isSuccess, String message );
    }

    @SuppressLint("CheckResult")
    public static void sendSnoringAnalysis(int retryCount, String dataSnoring, SnoringListener listener){
        UserService userService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        userService.getSnoringAnalysis(UserLogin.getUserLogin().getId(),dataSnoring)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse>(){
                    @Override
                    public void onNext(BaseResponse baseResponse) {
                        if(baseResponse!=null){
                            listener.onFinished(baseResponse.isSucces(),baseResponse.getMessage());
                        }else {
                            if(retryCount<BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(() -> sendSnoringAnalysis(retryCount+1,dataSnoring,listener),BuildConfig.REQUEST_TIME_OUT);
                            }else {
                                listener.onFinished(false,"UI000802C001");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(() -> sendSnoringAnalysis(retryCount+1,dataSnoring,listener),BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            listener.onFinished(false,"UI000802C002");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public static void sendPendingSnoringAnalysis(Activity activity){

        PendingSnoringModel.deleteSentSnoringResult();

        if (!activity.isFinishing()) {
            if (!NetworkUtil.isNetworkConnected(activity.getApplicationContext())) {
                return;
            }

            ArrayList<PendingSnoringModel> pendingSnoringModels = PendingSnoringModel.getUnsentSnoringResult();
            Logger.d("Snoring before send pending", + pendingSnoringModels.size());

            for (int i=0; i<pendingSnoringModels.size(); i++) {
                PendingSnoringModel pendingSnoringModel = pendingSnoringModels.get(i);
                UserService userService = ApiClient.getClient(activity.getApplicationContext()).create(UserService.class);
                UserLogin userData = UserLogin.getUserLogin();

                if (userData != null && pendingSnoringModel != null) {
                    int finalI = i;

                    if(pendingSnoringModel.getSnoringResult()!=null){
                        userService.getSnoringAnalysisOffline(userData.getId(), pendingSnoringModel.getSnoringResult()).enqueue(new Callback<BaseResponse>() {
                            @Override
                            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                                Logger.d("TEST: "+ response);
                                if(response!=null) {
                                    if (response.hashCode() == 401) {
                                        Logger.d("Snoring Provider: " + response);
                                    } else {
                                        PendingSnoringModel.updateSentSnoringResult(pendingSnoringModels.get(finalI));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseResponse> call, Throwable t) {

                            }
                        });
                    }
                }
            }


        }

    }
}
