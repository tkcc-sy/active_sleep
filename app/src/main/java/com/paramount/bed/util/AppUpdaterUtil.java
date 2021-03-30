package com.paramount.bed.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.VersionModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.VersionResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.ui.front.SliderActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.util.homesequence.SequenceDelegate;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class AppUpdaterUtil {
    public static final int ANDROID_APPLICATION_TYPE_BED = 1;
    public static final int ANDROID_APPLICATION_TYPE_MONITORING = 2;

    @SuppressLint("CheckResult")
    public static void checkVersion(int appType, HomeService homeService, Activity activity) {
        homeService.getVersionApp(appType, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<VersionResponse>>() {
                    public void onSuccess(BaseResponse<VersionResponse> response) {
                        if (response.getData() != null) {
                            try {
                                VersionResponse versionResponse = response.getData();
                                boolean needsUpdate = false;
                                if(BuildConfig.VERSION_MAJOR < versionResponse.major){
                                    needsUpdate = true;
                                }else if(BuildConfig.VERSION_MINOR < versionResponse.minor &&
                                        BuildConfig.VERSION_MAJOR <= versionResponse.major){
                                    needsUpdate = true;
                                }else if(BuildConfig.VERSION_REVISION < versionResponse.revision &&
                                        BuildConfig.VERSION_MINOR <= versionResponse.minor &&
                                        BuildConfig.VERSION_MAJOR <= versionResponse.major){
                                    needsUpdate = true;
                                }

                                if (needsUpdate) {
                                    DialogUtil.createCustomYesNo(activity,
                                            "",
                                            LanguageProvider.getLanguage("UI000802C015").replace("%APP_VER%", versionResponse.major + "." + versionResponse.minor + "." + versionResponse.revision),
                                            LanguageProvider.getLanguage("UI000802C017"), (dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                                EventBus.getDefault().post(new HomeActivity.AppVerCheckFinishedEvent());
                                            },
                                            LanguageProvider.getLanguage("UI000802C016"),
                                            (dialogInterface, i) -> {
                                                String url = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                                                Intent in = new Intent(Intent.ACTION_VIEW);
                                                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                in.setData(Uri.parse(url));
                                                activity.startActivity(in);
                                                dialogInterface.dismiss();
                                                EventBus.getDefault().post(new HomeActivity.AppVerCheckFinishedEvent());
                                            }
                                    );
                                }else{
                                    EventBus.getDefault().post(new HomeActivity.AppVerCheckFinishedEvent());
                                }
                            } catch (Exception e) {
                                EventBus.getDefault().post(new HomeActivity.AppVerCheckFinishedEvent());
                            }

                        }else{
                            EventBus.getDefault().post(new HomeActivity.AppVerCheckFinishedEvent());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(new HomeActivity.AppVerCheckFinishedEvent());
                    }
                });
    }

    @SuppressLint("CheckResult")
    public static void checkVersionSequence(int appType, HomeService homeService, Activity activity, AppListener listener) {
        homeService.getVersionApp(appType, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<VersionResponse>>() {
                    public void onSuccess(BaseResponse<VersionResponse> response) {
                        if (response.getData() != null) {
                            try {
                                VersionResponse versionResponse = response.getData();
                                boolean needsUpdate = false;
                                if(BuildConfig.VERSION_MAJOR < versionResponse.major){
                                    needsUpdate = true;
                                }else if(BuildConfig.VERSION_MINOR < versionResponse.minor &&
                                        BuildConfig.VERSION_MAJOR <= versionResponse.major){
                                    needsUpdate = true;
                                }else if(BuildConfig.VERSION_REVISION < versionResponse.revision &&
                                        BuildConfig.VERSION_MINOR <= versionResponse.minor &&
                                        BuildConfig.VERSION_MAJOR <= versionResponse.major){
                                    needsUpdate = true;
                                }

                                if (needsUpdate) {
                                    DialogUtil.createCustomYesNo(activity,
                                            "",
                                            LanguageProvider.getLanguage("UI000802C015").replace("%APP_VER%", versionResponse.major + "." + versionResponse.minor + "." + versionResponse.revision),
                                            LanguageProvider.getLanguage("UI000802C017"), (dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                                EventBus.getDefault().post(new HomeActivity.AppVerCheckFinishedEvent());
                                            },
                                            LanguageProvider.getLanguage("UI000802C016"),
                                            (dialogInterface, i) -> {
                                                String url = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
                                                Intent in = new Intent(Intent.ACTION_VIEW);
                                                in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                in.setData(Uri.parse(url));
                                                activity.startActivity(in);
                                                dialogInterface.dismiss();
                                                listener.finish();
                                            }
                                    );
                                }else{
                                    listener.finish();
                                }
                            } catch (Exception e) {
                                listener.finish();
                            }

                        }else{
                            listener.finish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.finish();
                    }
                });
    }

    public interface AppListener {
        void start();
        void finish();
    }
}
