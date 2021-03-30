package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDetailResponse {
    @JsonProperty("email")
    String email;
    @JsonProperty("nickname")
    String nickname;
    @JsonProperty("birth_date")
    String birthDate;
    @JsonProperty("phone_number")
    String phoneNumber;
    @JsonProperty("gender")
    int gender;
    @JsonProperty("company_id")
    int companyId;
    @JsonProperty("group_id")
    int groupId;
    @JsonProperty("ns_last_updated")
    String nsLastUpdate;
    @JsonProperty("max_user_monitored")
    int maxUserMonitored;
    @JsonProperty("ns_serial_number")
    String nsSerialNumber;
    @JsonProperty("sleep_questionaire_min_hour")
    int sleepQuestionnaireMinHour = 5;
    @JsonProperty("sleep_questionaire_min_minute")
    int sleepQuestionnaireMinMinute = 0;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getNsLastUpdate() {
        return nsLastUpdate;
    }

    public void setNsLastUpdate(String nsLastUpdate) {
        this.nsLastUpdate = nsLastUpdate;
    }

    public int getMaxUserMonitored() {
        return maxUserMonitored;
    }

    public void setMaxUserMonitored(int maxUserMonitored) {
        this.maxUserMonitored = maxUserMonitored;
    }

    public String getNsSerialNumber() {
        return nsSerialNumber;
    }

    public void setNsSerialNumber(String nsSerialNumber) {
        this.nsSerialNumber = nsSerialNumber;
    }

    public int getSleepQuestionnaireMinHour() {
        return sleepQuestionnaireMinHour;
    }

    public void setSleepQuestionnaireMinHour(int sleepQuestionnaireMinHour) {
        this.sleepQuestionnaireMinHour = sleepQuestionnaireMinHour;
    }

    public int getSleepQuestionnaireMinMinute() {
        return sleepQuestionnaireMinMinute;
    }

    public void setSleepQuestionnaireMinMinute(int sleepQuestionnaireMinMinute) {
        this.sleepQuestionnaireMinMinute = sleepQuestionnaireMinMinute;
    }
}
