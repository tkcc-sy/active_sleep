package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;

import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MattressHardnessSettingModel;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.FormPolicyResponse;
import com.paramount.bed.data.remote.service.HomeService;

import java.util.Arrays;
import java.util.Collections;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.paramount.bed.util.LogUtil.APITracker;

public class FormPolicyProvider {

    private HomeService homeService;
    private Context ctx;

    public FormPolicyProvider(Context ctx) {
        this.ctx = ctx;
        this.homeService = ApiClient.getClient(ctx).create(HomeService.class);
    }

    @SuppressLint("CheckResult")
    public void getFormPolicy(FormPolicyListener listener) {
        APITracker("Content/form_policy", 0, "Initializing");
        HomeService sService = ApiClient.getClient(getApplicationContext()).create(HomeService.class);
        sService.getFormPolicy()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<FormPolicyResponse>>() {
                    public void onSuccess(BaseResponse<FormPolicyResponse> response) {
                        APITracker("Content/form_policy", 1, "onSuccess");
                        if (response != null && response.isSucces() && response.getData() != null && response.getMessage().equals("CNT05-C000")) {
                            APITracker("Content/form_policy", 2, "Save To FormPolicyModel");
                            FormPolicyResponse fPR = response.getData();
                            FormPolicyModel.clear();
                            FormPolicyModel formPolicyModel = new FormPolicyModel();
                            formPolicyModel.setSsidMinLength(fPR.getSsidMinLength());
                            formPolicyModel.setSsidMaxLength(fPR.getSsidMaxLength());
                            formPolicyModel.setEmailMinLength(fPR.getEmailMinLength());
                            formPolicyModel.setEmailMaxLength(fPR.getEmailMaxLength());
                            formPolicyModel.setNicknameMinLength(fPR.getNicknameMinLength());
                            formPolicyModel.setNicknameMaxLength(fPR.getNicknameMaxLength());
                            formPolicyModel.setPhoneNumberMinLength(fPR.getPhoneNumberMinLength());
                            formPolicyModel.setPhoneNumberMaxLength(fPR.getPhoneNumberMaxLength());
                            formPolicyModel.setPinLength(fPR.getPinLength());
                            formPolicyModel.setCompanyCodeLength(fPR.getCompanyCodeLength());
                            formPolicyModel.setInquiryMinLength(fPR.getInquiryMinLength());
                            formPolicyModel.setInquiryMaxLength(fPR.getInquiryMaxLength());
                            formPolicyModel.setSsidPassMinLength(fPR.getSsidPassMinLength());
                            formPolicyModel.setSsidPassMaxLength(fPR.getSsidPassMaxLength());
                            formPolicyModel.setZipCodeLength(fPR.getZipCodeLength());

                            RealmList<Integer> defaultDegreeSetting = new RealmList<>();
                            if(fPR.autodriveDegreeSetting != null) {
                                for (int val : fPR.autodriveDegreeSetting
                                ) {
                                    defaultDegreeSetting.add(val);
                                }
                            }

                            RealmList<Integer> defaultTimeSleepSetting = new RealmList<>();
                            if(fPR.timeSleepResetSetting != null) {
                                for (int val : fPR.timeSleepResetSetting
                                ) {
                                    defaultTimeSleepSetting.add(val);
                                }
                            }

                            formPolicyModel.setAutodriveDegreeSetting(defaultDegreeSetting);
                            formPolicyModel.setTimeSleepResetSetting(defaultTimeSleepSetting);
                            formPolicyModel.setAsaOldVersionMajor(fPR.getAsaOldVersionMajor());
                            formPolicyModel.setAsaOldVersionMinor(fPR.getAsaOldVersionMinor());
                            formPolicyModel.setAsaOldVersionRevision(fPR.getAsaOldVersionRevision());

                            RealmList<MattressHardnessSettingModel> defaultMattressSetting = new RealmList<>();
                            if(fPR.mattressHardnessSettings != null) {
                                Collections.addAll(defaultMattressSetting, fPR.mattressHardnessSettings);
                            }
                            formPolicyModel.setMattressHardnessSetting(defaultMattressSetting);

                            formPolicyModel.setSnoringRecordingDelay(fPR.getSnoringRecordingDelay());
                            formPolicyModel.setSnoringMinDiskSpace(fPR.getSnoringMinDiskSpace());
                            formPolicyModel.setSnoringMaxRecordTime(fPR.getSnoringMaxRecordTime());

                            formPolicyModel.setSnoreAnalysisParamSnoreTime(fPR.getSnoreAnalysisParamSnoreTime());
                            formPolicyModel.setSnoreAnalysisParamSnoreTh(fPR.getSnoreAnalysisParamSnoreTh());
                            formPolicyModel.setSnoreAnalysisParamSnoreInterval(fPR.getSnoreAnalysisParamSnoreInterval());
                            formPolicyModel.setSnoreAnalysisParamSnoreFileTime(fPR.getSnoreAnalysisParamSnoreFileTime());
                            formPolicyModel.setSnoreAnalysisParamSnoreOutCount(fPR.getSnoreAnalysisParamSnoreOutCount());
                            formPolicyModel.setSnoreAnalysisMaxStorage(fPR.getSnoreAnalysisMaxStorage());
                            formPolicyModel.setSnoringMinDiskSpaceOnRecord(fPR.getSnoringMinDiskSpaceOnRecord());
                            formPolicyModel.setSnoringMinDiskSpaceMargin(fPR.getSnoringMinDiskSpaceMargin());

                            formPolicyModel.insert();
                            APITracker("Content/form_policy", 3, "Server Data : SSID_MIN_LENGTH -> " + fPR.getSsidMinLength());
                            APITracker("Content/form_policy", 4, "Local Data : SSID_MIN_LENGTH -> " + FormPolicyModel.getPolicy().getSsidMinLength());
                        }
                        listener.onFormPolicyDone(FormPolicyModel.getPolicy());
                        APITracker("Content/form_policy", 5, "onFormPolicyDone");
                    }

                    @Override
                    public void onError(Throwable e) {
                        APITracker("Content/form_policy", 5, "onError");
                        listener.onFormPolicyDone(FormPolicyModel.getPolicy());
                    }
                });
    }

    public interface FormPolicyListener {
        void onFormPolicyDone(FormPolicyModel formPolicyModel);
    }

    public static MattressHardnessSettingModel getDefaultMattressHardnessSetting(){
        MattressHardnessSettingModel defaultSetting = new MattressHardnessSettingModel();
        defaultSetting.setId(3);
        defaultSetting.setValue( "ふつう");
        return defaultSetting;
    }
}
