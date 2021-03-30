package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import com.google.gson.Gson;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.AdvertiseModel;
import com.paramount.bed.data.model.QuestionGeneralModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.AdvertisementResponse;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.SettingResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.RetryWithDelay;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.paramount.bed.util.LogUtil.Logx;

public class AdsQSProvider {

    private UserService adsService;
    private Context ctx;

    public AdsQSProvider(Context ctx) {
        this.ctx = ctx;
        this.adsService = ApiClient.getClient(ctx).create(UserService.class);
    }

    @SuppressLint("CheckResult")
    public void getAdsQS(AdsQSListener listener) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.getAdvertisement(UserLogin.getUserLogin().getId(), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<AdvertisementResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<AdvertisementResponse> response) {
                        if (response.isSucces()) {
                            if (response.getData() != null)
                                updateAdsQSData(response.getData());
                            listener.onAdsQSDone(true, null, true, response.getMessage());
                            return;
                        }

                        listener.onAdsQSDone(true, null, false, response.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onAdsQSDone(false, e, false, "");
                    }
                });
    }

    public interface AdsQSListener {
        void onAdsQSDone(boolean isParse, Throwable e, boolean isSuccess, String message);
    }

    private void updateAdsQSData(AdvertisementResponse data) {
        QuestionGeneralModel.clear();
        AdvertiseModel.clear();
        if (data.getQuestionnaire() != null) {
            QuestionGeneralModel questionnaire = new QuestionGeneralModel();
            questionnaire.setId(data.getQuestionnaire().getId());
            questionnaire.setData(data.getQuestionnaire().getData());
            questionnaire.setUpdated_date(data.getQuestionnaire().getUpdated_date());
            questionnaire.insert();
            Logx("GET ADVERTISEMENT", "INSERT DB QS :" + data.getQuestionnaire().getData());
        }
        if (data.getAdvertisement() != null) {
            AdvertiseModel advertise = new AdvertiseModel();
            advertise.setId(data.getAdvertisement().getId());
            advertise.setData(data.getAdvertisement().getData());
            advertise.setUpdated_date(data.getAdvertisement().getUpdated_date());
            advertise.insert();
            Logx("GET ADVERTISEMENT", "INSERT DB ADV :" + data.getAdvertisement().getData());
        }
    }

    @SuppressLint("CheckResult")
    public void seeAds(int adsId) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        Logx("ApiClientLog : User/see_ads", "advertise_id : " + adsId + " | end_user_id : " + UserLogin.getUserLogin().getId());
        sService.seeAds(adsId, UserLogin.getUserLogin().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void seeQS(int questionnaireId) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        Logx("ApiClientLog : User/see_questionnaire", "questionnaire_id : " + questionnaireId + " | user_id : " + UserLogin.getUserLogin().getId());
        sService.seeQS(questionnaireId, UserLogin.getUserLogin().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void sendQSResult(int QSType, String result, SendQSResultListener listener) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.sendQuestionnaireHome(UserLogin.getUserLogin().getId(), QSType, result, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse<String>>() {
                    @Override
                    public void onNext(BaseResponse<String> response) {
                        listener.onQSSent(true, null, response.isSucces(), response.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onQSSent(false, e, false, "");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface SendQSResultListener {
        void onQSSent(boolean isParse, Throwable e, boolean isSuccess, String message);
    }
}
