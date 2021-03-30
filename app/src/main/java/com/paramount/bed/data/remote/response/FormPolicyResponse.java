package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.paramount.bed.data.model.MattressHardnessSettingModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FormPolicyResponse {
    @JsonProperty("ssid_min_length")
    public int ssidMinLength;
    @JsonProperty("ssid_max_length")
    public int ssidMaxLength;
    @JsonProperty("email_min_length")
    public int emailMinLength;
    @JsonProperty("email_max_length")
    public int emailMaxLength;
    @JsonProperty("nickname_min_length")
    public int nicknameMinLength;
    @JsonProperty("nickname_max_length")
    public int nicknameMaxLength;
    @JsonProperty("phone_number_min_length")
    public int phoneNumberMinLength;
    @JsonProperty("phone_number_max_length")
    public int phoneNumberMaxLength;
    @JsonProperty("pin_length")
    public int pinLength;
    @JsonProperty("company_code_length")
    public int companyCodeLength;
    @JsonProperty("inquiry_min_length")
    public int inquiryMinLength;
    @JsonProperty("inquiry_max_length")
    public int inquiryMaxLength;
    @JsonProperty("ssid_pass_min_length")
    public int ssidPassMinLength;
    @JsonProperty("ssid_pass_max_length")
    public int ssidPassMaxLength;
    @JsonProperty("zipcode_length")
    public int zipCodeLength;
    @JsonProperty("autodrive_degree_setting")
    public int[] autodriveDegreeSetting;
    @JsonProperty("time_sleep_reset_setting")
    public int[] timeSleepResetSetting;
    @JsonProperty("asa_old_version_major")
    public int asaOldVersionMajor;
    @JsonProperty("asa_old_version_minor")
    private int asaOldVersionMinor;
    @JsonProperty("asa_old_version_revision")
    public int asaOldVersionRevision;
    @JsonProperty("desired_hardness_setting")
    public MattressHardnessSettingModel[] mattressHardnessSettings;
    @JsonProperty("snoring_recording_delay")
    public double snoringRecordingDelay;
    @JsonProperty("snoring_min_disk_space")
    public double snoringMinDiskSpace;
    @JsonProperty("snoring_min_disk_space_on_record")
    public double snoringMinDiskSpaceOnRecord;
    @JsonProperty("snoring_max_record_time")
    public double snoringMaxRecordTime;
    @JsonProperty("snore_analysis_param_snore_time")
    public int snoreAnalysisParamSnoreTime;
    @JsonProperty("snore_analysis_param_snore_th")
    public int snoreAnalysisParamSnoreTh;
    @JsonProperty("snore_analysis_param_snore_interval")
    public int snoreAnalysisParamSnoreInterval;
    @JsonProperty("snore_analysis_param_snore_file_time")
    public int snoreAnalysisParamSnoreFileTime;
    @JsonProperty("snore_analysis_param_snore_out_count")
    public int snoreAnalysisParamSnoreOutCount;
    @JsonProperty("snore_analysis_max_storage")
    public int snoreAnalysisMaxStorage;
    @JsonProperty("snoring_min_disk_space_margin")
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

    public int getZipCodeLength() {
        return zipCodeLength;
    }

    public void setZipCodeLength(int zipCodeLength) {
        this.zipCodeLength = zipCodeLength;
    }

    public int[] getAutodriveDegreeSetting() {
        return autodriveDegreeSetting;
    }

    public void setAutodriveDegreeSetting(int[] autodriveDegreeSetting) {
        this.autodriveDegreeSetting = autodriveDegreeSetting;
    }

    public int[] getTimeSleepResetSetting() {
        return timeSleepResetSetting;
    }

    public void setTimeSleepResetSetting(int[] timeSleepResetSetting) {
        this.timeSleepResetSetting = timeSleepResetSetting;
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

    public MattressHardnessSettingModel[] getMattressHardnessSettings() {
        return mattressHardnessSettings;
    }

    public void setMattressHardnessSettings(MattressHardnessSettingModel[] mattressHardnessSettings) {
        this.mattressHardnessSettings = mattressHardnessSettings;
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
}