package com.paramount.bed.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paramount.bed.data.provider.FormPolicyProvider;
import com.paramount.bed.data.provider.MattressSettingProvider;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class FormPolicyModel extends RealmObject {
    public int ssidMinLength;
    public int ssidMaxLength;
    public int emailMinLength;
    public int emailMaxLength;
    public int nicknameMinLength;
    public int nicknameMaxLength;
    public int phoneNumberMinLength;
    public int phoneNumberMaxLength;
    public int pinLength;
    public int companyCodeLength;
    public int inquiryMinLength;
    public int inquiryMaxLength;
    public int ssidPassMinLength;
    public int ssidPassMaxLength;
    public int zipCodeLength;
    public RealmList<Integer> autodriveDegreeSetting;
    public RealmList<Integer> timeSleepResetSetting;
    public int asaOldVersionMajor;
    public int asaOldVersionMinor;
    public int asaOldVersionRevision;
    public RealmList<MattressHardnessSettingModel> mattressHardnessSetting;
    public double snoringRecordingDelay;
    public double snoringMinDiskSpace;
    public double snoringMaxRecordTime;
    public int snoreAnalysisParamSnoreTime;
    public int snoreAnalysisParamSnoreTh;
    public int snoreAnalysisParamSnoreInterval;
    public int snoreAnalysisParamSnoreFileTime;
    public int snoreAnalysisParamSnoreOutCount;
    public int snoreAnalysisMaxStorage;
    public double snoringMinDiskSpaceOnRecord;
    public double snoringMinDiskSpaceMargin;

    public int getSsidMinLength() {
        return ssidMinLength;
    }

    public void setSsidMinLength(int ssidMinLength) {
        this.ssidMinLength = ssidMinLength;
    }

    public int getSsidMaxLength() {
        return ssidMaxLength;
    }

    public void setSsidMaxLength(int ssidMaxLength) {
        this.ssidMaxLength = ssidMaxLength;
    }

    public int getEmailMinLength() {
        return emailMinLength;
    }

    public void setEmailMinLength(int emailMinLength) {
        this.emailMinLength = emailMinLength;
    }

    public int getEmailMaxLength() {
        return emailMaxLength;
    }

    public void setEmailMaxLength(int emailMaxLength) {
        this.emailMaxLength = emailMaxLength;
    }

    public int getNicknameMinLength() {
        return nicknameMinLength;
    }

    public void setNicknameMinLength(int nicknameMinLength) {
        this.nicknameMinLength = nicknameMinLength;
    }

    public int getNicknameMaxLength() {
        return nicknameMaxLength;
    }

    public void setNicknameMaxLength(int nicknameMaxLength) {
        this.nicknameMaxLength = nicknameMaxLength;
    }

    public int getPhoneNumberMinLength() {
        return phoneNumberMinLength;
    }

    public void setPhoneNumberMinLength(int phoneNumberMinLength) {
        this.phoneNumberMinLength = phoneNumberMinLength;
    }

    public int getPhoneNumberMaxLength() {
        return phoneNumberMaxLength;
    }

    public void setPhoneNumberMaxLength(int phoneNumberMaxLength) {
        this.phoneNumberMaxLength = phoneNumberMaxLength;
    }

    public int getPinLength() {
        return pinLength;
    }

    public void setPinLength(int pinLength) {
        this.pinLength = pinLength;
    }

    public int getCompanyCodeLength() {
        return companyCodeLength;
    }

    public void setCompanyCodeLength(int companyCodeLength) {
        this.companyCodeLength = companyCodeLength;
    }

    public int getInquiryMinLength() {
        return inquiryMinLength;
    }

    public void setInquiryMinLength(int inquiryMinLength) {
        this.inquiryMinLength = inquiryMinLength;
    }

    public int getInquiryMaxLength() {
        return inquiryMaxLength;
    }

    public void setInquiryMaxLength(int inquiryMaxLength) {
        this.inquiryMaxLength = inquiryMaxLength;
    }

    public int getSsidPassMinLength() {
        return ssidPassMinLength;
    }

    public void setSsidPassMinLength(int ssidPassMinLength) {
        this.ssidPassMinLength = ssidPassMinLength;
    }

    public int getSsidPassMaxLength() {
        return ssidPassMaxLength;
    }

    public void setSsidPassMaxLength(int ssidPassMaxLength) {
        this.ssidPassMaxLength = ssidPassMaxLength;
    }

    public RealmList<Integer> getAutodriveDegreeSetting() {
        return autodriveDegreeSetting;
    }

    public RealmList<Integer> getTimeSleepResetSetting() {
        return timeSleepResetSetting;
    }

    public void setTimeSleepResetSetting(RealmList<Integer> timeSleepResetSetting) {
        this.timeSleepResetSetting = timeSleepResetSetting;
    }

    public Integer[] getTimeSleepSettingPrimitives() {
        return timeSleepResetSetting.toArray(new Integer[0]);
    }

    public Integer[] getAutodriveDegreeSettingPrimitives() {
        return autodriveDegreeSetting.toArray(new Integer[0]);
    }

    public int getAsaOldVersionMajor() {
        return asaOldVersionMajor;
    }

    public void setAsaOldVersionMajor(int asaOldVersionMajor) {
        this.asaOldVersionMajor = asaOldVersionMajor;
    }

    public int getAsaOldVersionMinor() {
        return asaOldVersionMinor;
    }

    public void setAsaOldVersionMinor(int asaOldVersionMinor) {
        this.asaOldVersionMinor = asaOldVersionMinor;
    }

    public int getAsaOldVersionRevision() {
        return asaOldVersionRevision;
    }

    public void setAsaOldVersionRevision(int asaOldVersionRevision) {
        this.asaOldVersionRevision = asaOldVersionRevision;
    }

    public void setAutodriveDegreeSetting(RealmList<Integer> autodriveDegreeSetting) {
        this.autodriveDegreeSetting = autodriveDegreeSetting;
    }

    public RealmList<MattressHardnessSettingModel> getMattressHardnessSetting() {
        return mattressHardnessSetting;
    }

    public void setMattressHardnessSetting(RealmList<MattressHardnessSettingModel> mattressHardnessSetting) {
        this.mattressHardnessSetting = mattressHardnessSetting;
    }

    public int getZipCodeLength() {
        return zipCodeLength;
    }

    public void setZipCodeLength(int zipCodeLength) {
        this.zipCodeLength = zipCodeLength;
    }

    public double getSnoringRecordingDelay() {
        return snoringRecordingDelay;
    }

    public void setSnoringRecordingDelay(double snoringRecordingDelay) {
        this.snoringRecordingDelay = snoringRecordingDelay;
    }

    public double getSnoringMinDiskSpace() {
        return snoringMinDiskSpace;
    }

    public void setSnoringMinDiskSpace(double snoringMinDiskSpace) {
        this.snoringMinDiskSpace = snoringMinDiskSpace;
    }

    public double getSnoringMaxRecordTime() {
        return snoringMaxRecordTime;
    }

    public void setSnoringMaxRecordTime(double snoringMaxRecordTime) {
        this.snoringMaxRecordTime = snoringMaxRecordTime;
    }

    public int getSnoreAnalysisParamSnoreTime() {
        return snoreAnalysisParamSnoreTime;
    }

    public void setSnoreAnalysisParamSnoreTime(int snoreAnalysisParamSnoreTime) {
        this.snoreAnalysisParamSnoreTime = snoreAnalysisParamSnoreTime;
    }

    public int getSnoreAnalysisParamSnoreTh() {
        return snoreAnalysisParamSnoreTh;
    }

    public void setSnoreAnalysisParamSnoreTh(int snoreAnalysisParamSnoreTh) {
        this.snoreAnalysisParamSnoreTh = snoreAnalysisParamSnoreTh;
    }

    public int getSnoreAnalysisParamSnoreInterval() {
        return snoreAnalysisParamSnoreInterval;
    }

    public void setSnoreAnalysisParamSnoreInterval(int snoreAnalysisParamSnoreInterval) {
        this.snoreAnalysisParamSnoreInterval = snoreAnalysisParamSnoreInterval;
    }

    public int getSnoreAnalysisParamSnoreFileTime() {
        return snoreAnalysisParamSnoreFileTime;
    }

    public void setSnoreAnalysisParamSnoreFileTime(int snoreAnalysisParamSnoreFileTime) {
        this.snoreAnalysisParamSnoreFileTime = snoreAnalysisParamSnoreFileTime;
    }

    public int getSnoreAnalysisParamSnoreOutCount() {
        return snoreAnalysisParamSnoreOutCount;
    }

    public void setSnoreAnalysisParamSnoreOutCount(int snoreAnalysisParamSnoreOutCount) {
        this.snoreAnalysisParamSnoreOutCount = snoreAnalysisParamSnoreOutCount;
    }

    public int getSnoreAnalysisMaxStorage() {
        return snoreAnalysisMaxStorage;
    }

    public void setSnoreAnalysisMaxStorage(int snoreAnalysisMaxStorage) {
        this.snoreAnalysisMaxStorage = snoreAnalysisMaxStorage;
    }

    public double getSnoringMinDiskSpaceOnRecord() {
        return snoringMinDiskSpaceOnRecord;
    }

    public void setSnoringMinDiskSpaceOnRecord(double snoringMinDiskSpaceOnRecord) {
        this.snoringMinDiskSpaceOnRecord = snoringMinDiskSpaceOnRecord;
    }

    public double getSnoringMinDiskSpaceMargin() {
        return snoringMinDiskSpaceMargin;
    }

    public void setSnoringMinDiskSpaceMargin(double snoringMinDiskSpaceMargin) {
        this.snoringMinDiskSpaceMargin = snoringMinDiskSpaceMargin;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FormPolicyModel.class);
        realm.commitTransaction();
    }

    public static FormPolicyModel getPolicy() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<FormPolicyModel> query = realm.where(FormPolicyModel.class);
        FormPolicyModel result = query.findFirst();
        if (result == null) {
            FormPolicyModel formPolicyModel = new FormPolicyModel();
            formPolicyModel.setSsidMinLength(1);
            formPolicyModel.setSsidMaxLength(32);
            formPolicyModel.setEmailMinLength(5);
            formPolicyModel.setEmailMaxLength(128);
            formPolicyModel.setNicknameMinLength(1);
            formPolicyModel.setNicknameMaxLength(25);
            formPolicyModel.setPhoneNumberMinLength(11);
            formPolicyModel.setPhoneNumberMaxLength(11);
            formPolicyModel.setPinLength(4);
            formPolicyModel.setCompanyCodeLength(8);
            formPolicyModel.setInquiryMinLength(1);
            formPolicyModel.setInquiryMaxLength(1000);
            formPolicyModel.setSsidPassMinLength(0);
            formPolicyModel.setSsidPassMaxLength(64);
            formPolicyModel.setZipCodeLength(7);
            formPolicyModel.setSnoringRecordingDelay(0.05);
            formPolicyModel.setSnoringMaxRecordTime(480);
            formPolicyModel.setSnoringMinDiskSpace(800);
            formPolicyModel.setSnoreAnalysisParamSnoreTime(10);
            formPolicyModel.setSnoreAnalysisParamSnoreTh(15);
            formPolicyModel.setSnoreAnalysisParamSnoreInterval(1);
            formPolicyModel.setSnoreAnalysisParamSnoreFileTime(30);
            formPolicyModel.setSnoreAnalysisParamSnoreOutCount(15);
            formPolicyModel.setSnoreAnalysisMaxStorage(15);
            formPolicyModel.setSnoringMinDiskSpaceOnRecord(200);
            formPolicyModel.setSnoringMinDiskSpaceMargin(400);

            RealmList<Integer> defaultDegreeSetting = new RealmList<>();
            defaultDegreeSetting.add(0);
            defaultDegreeSetting.add(5);
            defaultDegreeSetting.add(10);
            defaultDegreeSetting.add(15);
            formPolicyModel.setAutodriveDegreeSetting(defaultDegreeSetting);

            int min = 0;
            int max = 50;
            int interval = 10;

            RealmList<Integer> defaultTimeResetSetting = new RealmList<>();
            for (int i = min; i<=max; i+=interval){
                defaultTimeResetSetting.add(i);
            }

            formPolicyModel.setTimeSleepResetSetting(defaultTimeResetSetting);

            //TODO: Change to real numbers
            formPolicyModel.setAsaOldVersionMajor(1);
            formPolicyModel.setAsaOldVersionMinor(99);
            formPolicyModel.setAsaOldVersionRevision(99);

            formPolicyModel.mattressHardnessSetting = new RealmList<>();
            MattressHardnessSettingModel opt1 = new MattressHardnessSettingModel();
            opt1.id = 1;
            opt1.value = "硬い";
            formPolicyModel.mattressHardnessSetting.add(opt1);

            MattressHardnessSettingModel opt2 = new MattressHardnessSettingModel();
            opt2.id = 2;
            opt2.value = "やや硬い";
            formPolicyModel.mattressHardnessSetting.add(opt2);

            MattressHardnessSettingModel opt3 = new MattressHardnessSettingModel();
            opt3.id = 3;
            opt3.value = "ふつう";
            formPolicyModel.mattressHardnessSetting.add(opt3);

            MattressHardnessSettingModel opt4 = new MattressHardnessSettingModel();
            opt4.id = 4;
            opt4.value = "やや柔らかい";
            formPolicyModel.mattressHardnessSetting.add(opt4);

            MattressHardnessSettingModel opt5 = new MattressHardnessSettingModel();
            opt5.id = 5;
            opt5.value = "柔らかい";
            formPolicyModel.mattressHardnessSetting.add(opt5);
            return formPolicyModel;
        }
        return result;
    }
    public  MattressHardnessSettingModel getMattressHardnessSettingById(int id){
        for (MattressHardnessSettingModel mattressHardnessSetting:mattressHardnessSetting
             ) {
            if(mattressHardnessSetting.id == id){
                return mattressHardnessSetting;
            }
        }
        //not found return default value
        return FormPolicyProvider.getDefaultMattressHardnessSetting();
    }
    public int getMattressHardnessSettingIndexById(int id){
        int index = 0;
        for (MattressHardnessSettingModel mattressHardnessSetting:mattressHardnessSetting
        ) {
            if(mattressHardnessSetting.id == id){
                return index;
            }
            index += 1;
        }
        return 0;
    }
    public static ArrayList<FormPolicyModel> getAll() {
        ArrayList<FormPolicyModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<FormPolicyModel> query = realm.where(FormPolicyModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
