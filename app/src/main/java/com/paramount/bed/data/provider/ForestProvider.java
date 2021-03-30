package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.os.Handler;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.ForestModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.ForrestScoreAdviceResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.RetryWithDelay;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ForestProvider {
    public interface ForestListener {
        void onCalculateForestScoreSuccess();
        void onCalculateForestScoreError(boolean error, Throwable e);
    }

    @SuppressLint("CheckResult")
    public static void getForestCalculation(ForestListener listener, int menuAccess, int retryCount){
        UserService userService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        userService.forestCalculation(UserLogin.getUserLogin().getId(),menuAccess)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse<ForrestScoreAdviceResponse>>() {
                    @Override
                    public void onNext(BaseResponse<ForrestScoreAdviceResponse> baseResponse) {
                        if (baseResponse != null && baseResponse.getData() != null ) {
                            ForrestScoreAdviceResponse data = baseResponse.getData();
                            ForestModel.clear();
                            if (data.getScore() != null  && data.getImg() != null && data.getDate() != null) {
                                ForestModel fm = new ForestModel();
                                fm.setScore(baseResponse.getData().getScore());
                                fm.setUserNickname(baseResponse.getData().getUser_nickname());
                                fm.setAdvice(baseResponse.getData().getAdvice());
                                fm.setImg(baseResponse.getData().getImg());
                                fm.setDate(baseResponse.getData().getDate());
                                fm.insert();

                                listener.onCalculateForestScoreSuccess();
                            }else {
                                listener.onCalculateForestScoreError(true,null);
                            }
                        }else {
                            if(retryCount<BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getForestCalculation(listener,menuAccess, retryCount+1);
                                    }
                                },BuildConfig.REQUEST_TIME_OUT);
                            }else {
                                listener.onCalculateForestScoreError(true,null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getForestCalculation(listener,menuAccess, retryCount+1);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            listener.onCalculateForestScoreError(true,e);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
