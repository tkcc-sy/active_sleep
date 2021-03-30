package com.paramount.bed.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.PasswordPolicyResponse;
import com.paramount.bed.data.remote.response.VersionResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.data.remote.service.UserService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class PasswordPolicyUtil {
    public static final int ANDROID_APPLICATION_TYPE_BED = 1;
    public static final int ANDROID_APPLICATION_TYPE_MONITORING = 2;

    public static void getPasswordPolicy(String company_code, int appType, UserService userService, Activity activity) {
        userService.getPasswordPolicy(company_code, ANDROID_APPLICATION_TYPE_BED)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<PasswordPolicyResponse>>() {
                    public void onSuccess(BaseResponse<PasswordPolicyResponse> response) {
                        PasswordPolicyResponse versionResponse = response.getData();

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("abx load content 2");
                        Log.d("abx", e.getMessage());
                        //hideLoading();

                    }
                });
    }

    public static void validatePassword(String company_code, String password, int appType, UserService userService, Activity activity) {
        userService.validatePassword(company_code, password, appType)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                        if (response.isSucces()) {
                            //TODO
                            getPasswordPolicy(company_code, appType, userService, activity);
                        } else {
                            getPasswordPolicy(company_code, appType, userService, activity);
                            DialogUtil.createSimpleOkDialog(activity,"",LanguageProvider.getLanguage(response.getMessage()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("abx load content 2");
                        Log.d("abx", e.getMessage());
                        //hideLoading();

                    }
                });
    }
}
