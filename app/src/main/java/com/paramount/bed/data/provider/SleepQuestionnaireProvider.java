package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.ForestModel;
import com.paramount.bed.data.model.SleepQuestionnaireAnswerModel;
import com.paramount.bed.data.model.SleepQuestionnaireModel;
import com.paramount.bed.data.model.SleepQuestionnaireQuestionModel;
import com.paramount.bed.data.model.SleepQuestionnaireResult;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.util.alarms.AlarmsSleepQuestionnaire;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SleepQuestionnaireProvider {

    public interface SendSleepQuestionnaireListener{
        void onSuccessSend(BaseResponse<String> result);
        void onErrorSend(String errTag);
    }

    @SuppressLint("CheckResult")
    public static void sendSleepQuestionnareToServer(Context context, SleepQuestionnaireResult questionnaireResult, SendSleepQuestionnaireListener listener, int retryCount){
        UserService questionnareService = ApiClient.getClient(context).create(UserService.class);
        Gson gson = new Gson();
        questionnareService.sendQuestionnaireHome(UserLogin.getUserLogin().getId(), 2, gson.toJson(questionnaireResult), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse<String>>() {
                    @Override
                    public void onNext(BaseResponse<String> stringBaseResponse) {
                        if(stringBaseResponse!=null){
                            listener.onSuccessSend(stringBaseResponse);
                        }else {
                            if(retryCount< BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Logger.d("SEND_ANSWER_QUESTIONNAIRE: " + AlarmsSleepQuestionnaire.class.getSimpleName());
                                        sendSleepQuestionnareToServer(context, questionnaireResult, listener, retryCount+1);
                                    }
                                },BuildConfig.REQUEST_TIME_OUT);
                            }else {
                                listener.onErrorSend(null);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Logger.d("SEND_ANSWER_QUESTIONNAIRE: " + AlarmsSleepQuestionnaire.class.getSimpleName());
                                    sendSleepQuestionnareToServer(context, questionnaireResult, listener, retryCount+1);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            listener.onErrorSend(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
