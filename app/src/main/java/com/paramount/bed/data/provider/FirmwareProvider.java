package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;

import com.paramount.bed.data.model.FirmwareIntroContentModel;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.HomeService;

import java.io.IOException;
import java.io.InputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class FirmwareProvider {

    public static String getLocalContent(Context context){
        String ret = "";
        try {
            InputStream isRealtime = context.getAssets().open("default/firmware/default_intro.html");
            int size = isRealtime.available();

            byte[] buffer = new byte[size];
            isRealtime.read(buffer);
            isRealtime.close();

            ret = new String(buffer);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return ret;
    }

    @SuppressLint("CheckResult")
    public static void getIntroContent(Context context,FirmwareIntroCallback callback){
        HomeService homeService = ApiClient.getClient(context).create(HomeService.class);
        homeService.getFWUpdateIntro()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                    public void onSuccess(BaseResponse<String> response) {
                        String html = response.getData();
                        FirmwareIntroContentModel firmwareIntroContentModel = FirmwareIntroContentModel.get(context);
                        firmwareIntroContentModel.updateContent(html);
                        if(callback != null){
                            callback.onGetIntro(firmwareIntroContentModel.getUnmanaged());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(callback != null){
                            callback.onGetIntro(FirmwareIntroContentModel.get((context)).getUnmanaged());
                        }
                    }
                });
    }

    public interface FirmwareIntroCallback{
        public void onGetIntro(FirmwareIntroContentModel intro);
    }
}
