package com.paramount.bed.ui.front;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.data.model.FAQLinkModel;
import com.paramount.bed.data.model.MigrateLanguageModel;
import com.paramount.bed.data.model.ServerModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.DashboardProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.FAQLinkResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.login.LoginActivity;
import com.paramount.bed.ui.login.LoginEmailActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.main.RemoteActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.NetworkUtil;

import java.util.ArrayList;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2500;
    Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isLauchActivity = true;
        super.onCreate(savedInstanceState);
    }

    public void runInitial() {
        NSManager.getInstance(getApplicationContext(),null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window w = this.getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        migrateLanguage();
        if (NetworkUtil.isNetworkConnected(this)) {
            getFAQ(homeService);
        } else {
            initialFAQ();
        }
    }

    private void migrateLanguage() {
        if (MigrateLanguageModel.getAll().size() == 1) {
            MigrateLanguageModel updateApp = MigrateLanguageModel.getAll().get(0);
            String appVersion = "1" + String.valueOf(BuildConfig.VERSION_MAJOR) + String.valueOf(BuildConfig.VERSION_MINOR) + String.valueOf(BuildConfig.VERSION_REVISION);
            String dbVersion = "1" + String.valueOf(updateApp.getMajor()) + String.valueOf(updateApp.getMinor()) + String.valueOf(updateApp.getRevision());
            if (Integer.parseInt(appVersion) > Integer.parseInt(dbVersion)) {
                LanguageProvider.init(this);
                DashboardProvider.init(this);
                ServerModel.clear();
                MigrateLanguageModel.clear();
                MigrateLanguageModel updateData = new MigrateLanguageModel();
                updateData.setMajor(BuildConfig.VERSION_MAJOR);
                updateData.setMinor(BuildConfig.VERSION_MINOR);
                updateData.setRevision(BuildConfig.VERSION_REVISION);
                updateData.setTNCRead(false);
                updateData.insert();
            }

        } else {
            LanguageProvider.init(this);
            DashboardProvider.init(this);
            ServerModel.clear();
            MigrateLanguageModel.clear();
            MigrateLanguageModel firstInstall = new MigrateLanguageModel();
            firstInstall.setMajor(BuildConfig.VERSION_MAJOR);
            firstInstall.setMinor(BuildConfig.VERSION_MINOR);
            firstInstall.setRevision(BuildConfig.VERSION_REVISION);
            firstInstall.setTNCRead(false);
            firstInstall.insert();
        }
    }

    private void startNextIntent() {
        if(isFinishing()){
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (UserLogin.isLogin()) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                    return;
                } else {
                    if (StatusLogin.getUserLogin() == null) {
                        Intent mainIntent = new Intent(SplashActivity.this, SliderActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    } else {
                        Intent mainIntent = new Intent(SplashActivity.this, LoginEmailActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_splash;
    }

    public void getFAQ(HomeService service) {
        try {
            service.getFAQLink()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<FAQLinkResponse>>>() {
                        public void onSuccess(BaseResponse<ArrayList<FAQLinkResponse>> response) {
                            if (response != null) {
                                if (response.isSucces()) {
                                    ArrayList<FAQLinkResponse> data = response.getData();
                                    FAQLinkModel.clear();
                                    for (int i = 0; i < data.size(); i++) {
                                        FAQLinkModel faqLinkModel = new FAQLinkModel();
                                        faqLinkModel.setPID(UUID.randomUUID().toString());
                                        faqLinkModel.setLinkNo(data.get(i).getLinkNo());
                                        faqLinkModel.setAppliTag(data.get(i).getAppliTag());
                                        faqLinkModel.insert();
                                    }
                                    startNextIntent();


                                } else {
                                    initialFAQ();
                                }
                            } else {
                                initialFAQ();
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            initialFAQ();
                        }
                    });
        } catch (Exception e) {
            initialFAQ();
        }

    }

    public void initialFAQ() {
        if (FAQLinkModel.getAll().size() == 0) {
            ArrayList<FAQLinkModel> data = FAQLinkModel.initialFAQ();
            FAQLinkModel.clear();
            for (int i = 0; i < data.size(); i++) {
                FAQLinkModel faqLinkModel = new FAQLinkModel();
                faqLinkModel.setPID(UUID.randomUUID().toString());
                faqLinkModel.setLinkNo(data.get(i).getLinkNo());
                faqLinkModel.setAppliTag(data.get(i).getAppliTag());
                faqLinkModel.insert();
            }
        }
        startNextIntent();
    }

    @Override
    protected void onLaunchActivityPassedFilter() {
        runInitial();
    }
}
