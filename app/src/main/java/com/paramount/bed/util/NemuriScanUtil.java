package com.paramount.bed.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.QSSleepDailyModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.NemuriScanAddResponse;
import com.paramount.bed.data.remote.response.NemuriScanCheckResponse;
import com.paramount.bed.data.remote.response.NemuriScanDetailResponse;
import com.paramount.bed.data.remote.service.NemuriScanService;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.ui.registration.step.WifiConnectFragment;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class NemuriScanUtil {
    private static final int ANDROID_APPLICATION_TYPE_BED = 1;

    @SuppressLint("CheckResult")
    public static void fetchSpec(Context context, NemuriScanDetailFetchListener listener) {
        if (NemuriScanModel.get() == null || NemuriScanModel.get().getSerialNumber() == null) {
            listener.onNemuriScanDetailFetched(NemuriScanModel.get());
            return;
        }
        if (NemuriScanModel.get() != null
                && NemuriScanModel.get().getSerialNumber() != null
                && NemuriScanModel.get().getSerialNumber().trim().length() > 0
        ) {
            NemuriScanService nemuriScanService = ApiClient.getClient(context).create(NemuriScanService.class);
            nemuriScanService.getNemuriScanDetail(NemuriScanModel.get().getSerialNumber(), UserLogin.getUserLogin().getId())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse<NemuriScanDetailResponse>>() {
                        public void onSuccess(BaseResponse<NemuriScanDetailResponse> response) {
                            if (response.isSucces()) {
                                NemuriScanDetailResponse nsData = response.getData();
                                if(nsData != null) {
                                    listener.onNemuriScanDetailFetched(NemuriScanModel.updateDetail(nsData.bedActive == 1, nsData.mattressActive == 1, nsData.lastConnectionTime));
                                } else {
                                    listener.onNemuriScanDetailFetched(NemuriScanModel.get());
                                }
                            } else {
                                listener.onNemuriScanDetailFetched(NemuriScanModel.get());
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            listener.onNemuriScanDetailFetched(NemuriScanModel.get());
                        }
                    });
        } else {
            listener.onNemuriScanDetailFetched(NemuriScanModel.get());
        }
    }

    @SuppressLint("CheckResult")
    public static void register(Activity activity,Context context, int userId, String serialNumber,int major, int minor, int revision, NemuriScanRegisterListener listener) {
        if (userId <= 0 || serialNumber.isEmpty()) {
            listener.onNemuriScanRegisterFailed();
            return;
        }
        NemuriScanService nemuriScanService = ApiClient.getClient(context).create(NemuriScanService.class);
        nemuriScanService.addNemuriScan(serialNumber, userId, ANDROID_APPLICATION_TYPE_BED,major,minor,revision)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<NemuriScanAddResponse>() {
                    @Override
                    public void onSuccess(NemuriScanAddResponse response) {
                        if(QSSleepDailyModel.getFirst() == null) {
                            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                            QSSleepDailyModel.adsShowed(day);
                        }
                        UserLogin.setRegisterdNSSerialNumber(serialNumber);
                        listener.onNemuriScanRegistered(response.getData());
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (!NetworkUtil.isNetworkConnected(context)) {
                            DialogUtil.showOfflineDialog(activity, (dialogInterface, i) -> listener.onNemuriScanRegisterFailed());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(activity);
                        }
                    }
                });
    }

    @SuppressLint("CheckResult")
    public static void check(String nsSerialNumber, NemuriScanService nemuriScanService, Activity activity, int routes) {
        nemuriScanService.validateSerialNumber(nsSerialNumber, 0, ANDROID_APPLICATION_TYPE_BED)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<NemuriScanCheckResponse>() {
                    public void onSuccess(NemuriScanCheckResponse response) {
                        if (response.isSucces()) {
                            /*
                                0: Dummy BLE
                            */
                            switch (routes) {
                                case 0:
                                    RegistrationStepActivity registrationStepActivity = (RegistrationStepActivity) activity;
                                    registrationStepActivity.successCheck(nsSerialNumber, response);
                                    break;
                            }

                        } else {
                            switch (routes) {
                                case 0:
                                    RegistrationStepActivity registrationStepActivity = (RegistrationStepActivity) activity;
                                    registrationStepActivity.hideLoading();
                                    DialogUtil.createSimpleOkDialog(registrationStepActivity, "", LanguageProvider.getLanguage(response.getMessage()));
                                    break;
                            }

                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("abx load content 2");
                        Log.d("abx", e.getMessage());
                        switch (routes) {
                            case 0:
                                RegistrationStepActivity registrationStepActivity = (RegistrationStepActivity) activity;
                                registrationStepActivity.hideLoading();
                                if (!NetworkUtil.isNetworkConnected(registrationStepActivity)) {
                                    DialogUtil.offlineDialog(registrationStepActivity, registrationStepActivity);
                                } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                    DialogUtil.tokenExpireDialog(registrationStepActivity);
                                } else {
                                    DialogUtil.serverFailed(registrationStepActivity, "UI000802C157", "UI000802C158", "UI000802C159", "UI000802C160");
                                }
                                break;
                        }


                    }
                });
    }

    public interface NemuriScanDetailFetchListener {
        void onNemuriScanDetailFetched(NemuriScanModel nemuriScanDetailModel);
    }

    public interface NemuriScanRegisterListener {
        void onNemuriScanRegistered(String date);
        void onNemuriScanRegisterFailed();
    }
}
