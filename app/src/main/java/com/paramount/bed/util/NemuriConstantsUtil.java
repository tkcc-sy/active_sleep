package com.paramount.bed.util;

import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.remote.response.NemuriConstantsResponse;
import com.paramount.bed.data.remote.service.NemuriScanService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class NemuriConstantsUtil {
    public static final int ANDROID_APPLICATION_TYPE_BED = 1;
    public static final int ANDROID_APPLICATION_TYPE_MONITORING = 2;

    public static void Sync(NemuriScanService service) {
        service.getNemuriConstants()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<NemuriConstantsResponse>() {
                    public void onSuccess(NemuriConstantsResponse response) {
                        if (response != null) {
                            if (response.isSucces()) {
                                NemuriConstantsModel data = response.getData();
                                NemuriConstantsModel.clear();
                                NemuriConstantsModel nemuriConstantsModel = new NemuriConstantsModel();
                                nemuriConstantsModel.nsUrlIp = data.nsUrlIp;
                                nemuriConstantsModel.nsUrl = data.nsUrl;
                                nemuriConstantsModel.commandInterval = data.commandInterval;
                                nemuriConstantsModel.operationTimeout = data.operationTimeout;
                                nemuriConstantsModel.statusPollingInterval = data.statusPollingInterval;
                                nemuriConstantsModel.upperBedThreshold = data.upperBedThreshold;
                                nemuriConstantsModel.lowerBedThreshold = data.lowerBedThreshold;
                                nemuriConstantsModel.bedResponseTimeout = data.bedResponseTimeout;
                                nemuriConstantsModel.heightWarningThreshold = data.heightWarningThreshold;
                                nemuriConstantsModel.wifiSettingTimeout = data.wifiSettingTimeout;
                                nemuriConstantsModel.wifiStatusPollingInterval = data.wifiStatusPollingInterval;
                                nemuriConstantsModel.mattressOperationTimeout = data.mattressOperationTimeout;
                                nemuriConstantsModel.mattressDehumidifierTime = data.mattressDehumidifierTime;
                                nemuriConstantsModel.mattressOperationMaxRetry = data.mattressOperationMaxRetry;
                                nemuriConstantsModel.logResendInterval = data.logResendInterval;
                                nemuriConstantsModel.bedSettingMaxRetry = data.bedSettingMaxRetry;
                                nemuriConstantsModel.mattressBusyCheck = data.mattressBusyCheck;
//                        nemuriConstantsModel.mattressMemory1Default = data.mattressMemory1Default;
//                        nemuriConstantsModel.mattressMemory2Default = data.mattressMemory2Default;
                                nemuriConstantsModel.nsPostDataMaxWaitDuration = data.nsPostDataMaxWaitDuration;
                                nemuriConstantsModel.nsPostDataMaxWaitRetry = data.nsPostDataMaxWaitRetry;
                                nemuriConstantsModel.nsBedSameResultTimeout = data.nsBedSameResultTimeout;
                                nemuriConstantsModel.dummyBleActive = data.dummyBleActive;
                                nemuriConstantsModel.nsConnectionTimeout = data.nsConnectionTimeout;
                                nemuriConstantsModel.reconnectPollMaxDuration =data.reconnectPollMaxDuration;
                                nemuriConstantsModel.reconnectPollInterval = data.reconnectPollInterval;
                                nemuriConstantsModel.realtimBellInterval = data.realtimBellInterval;
                                nemuriConstantsModel.realtimeFetchInterval = data.realtimeFetchInterval;
                                nemuriConstantsModel.nsScanTime = data.nsScanTime;
                                nemuriConstantsModel.sleepAlarmTime = data.sleepAlarmTime;
                                nemuriConstantsModel.qsSleepTime = data.qsSleepTime;
                                nemuriConstantsModel.setWifiAuthDelay = data.setWifiAuthDelay;
                                nemuriConstantsModel.reconnectCount = data.reconnectCount;
                                nemuriConstantsModel.insert();
                            } else {
                            }
                        } else {
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.d("abx load content 2");

                    }
                });
    }
}
