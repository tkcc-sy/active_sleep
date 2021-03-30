package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SettingResponse {
    //TODO : RENAME TO CAMELCASE
    @SerializedName("ads_allowed")
    public boolean ads_allowed;
    @SerializedName("automatic_operation_alarm_id")
    public int automatic_operation_alarm_id;
    @SerializedName("automatic_operation_sleep_active")
    public boolean automatic_operation_sleep_active;
    @SerializedName("monitoring_allowed")
    public boolean monitoring_allowed;
    @SerializedName("automatic_operation_bed_pattern_id")
    public int  automatic_operation_bed_pattern_id;
    @SerializedName("automatic_operation_reminder_allowed")
    public boolean automatic_operation_reminder_allowed;
    @SerializedName("automatic_operation_wakeup_monday_active")
    public boolean automatic_operation_wakeup_monday_active;
    @SerializedName("automatic_operation_wakeup_monday_time")
    public String automatic_operation_wakeup_monday_time;
    @SerializedName("automatic_operation_wakeup_tuesday_active")
    public boolean automatic_operation_wakeup_tuesday_active;
    @SerializedName("automatic_operation_wakeup_tuesday_time")
    public String automatic_operation_wakeup_tuesday_time;
    @SerializedName("automatic_operation_wakeup_wednesday_active")
    public boolean automatic_operation_wakeup_wednesday_active;
    @SerializedName("automatic_operation_wakeup_wednesday_time")
    public String automatic_operation_wakeup_wednesday_time;
    @SerializedName("automatic_operation_wakeup_thursday_active")
    public boolean automatic_operation_wakeup_thursday_active;
    @SerializedName("automatic_operation_wakeup_thursday_time")
    public String  automatic_operation_wakeup_thursday_time;
    @SerializedName("automatic_operation_wakeup_friday_active")
    public boolean automatic_operation_wakeup_friday_active;
    @SerializedName("automatic_operation_wakeup_friday_time")
    public String automatic_operation_wakeup_friday_time;
    @SerializedName("automatic_operation_wakeup_saturday_active")
    public boolean  automatic_operation_wakeup_saturday_active;
    @SerializedName("automatic_operation_wakeup_saturday_time")
    public String automatic_operation_wakeup_saturday_time;
    @SerializedName("automatic_operation_wakeup_sunday_active")
    public boolean automatic_operation_wakeup_sunday_active;
    @SerializedName("automatic_operation_wakeup_sunday_time")
    public String automatic_operation_wakeup_sunday_time;
    @SerializedName("monitoring_questionnaire_allowed")
    public boolean monitoring_questionnaire_allowed;
    @SerializedName("monitoring_weekly_report_allowed")
    public boolean monitoring_weekly_report_allowed;
    @SerializedName("monitoring_error_report_allowed")
    public boolean monitoring_error_report_allowed;
    @SerializedName("bed_fast_mode")
    public int bed_fast_mode;
    @SerializedName("bed_combi_locked")
    public int bed_combi_locked;
    @SerializedName("bed_head_locked")
    public int bed_head_locked;
    @SerializedName("bed_leg_locked")
    public int bed_leg_locked;
    @SerializedName("bed_height_locked")
    public int bed_height_locked;
    @JsonProperty("automatic_operation_bed_degree")
    public int autodriveDegreeSetting;
    @SerializedName("timer_setting")
    public int timer_setting;
    @SerializedName("mori_feature_active")
    public boolean mori_feature_active;
    @SerializedName("user_desired_hardness")
    public int user_desired_hardness;
    @JsonProperty("sleep_reset_timing")
    public int sleep_reset_timing;
    @JsonProperty("forest_report_allowed")
    public boolean forest_report_allowed;
    @SerializedName("snoring_storage_enable")
    public int snoring_storage_enable;
    public SettingResponse() {
    }

    public SettingResponse(boolean ads_allowed, int automatic_operation_alarm_id, boolean automatic_operation_sleep_active, boolean monitoring_allowed, int automatic_operation_bed_pattern_id, boolean automatic_operation_reminder_allowed, boolean automatic_operation_wakeup_monday_active, String automatic_operation_wakeup_monday_time, boolean automatic_operation_wakeup_tuesday_active, String automatic_operation_wakeup_tuesday_time, boolean automatic_operation_wakeup_wednesday_active, String automatic_operation_wakeup_wednesday_time, boolean automatic_operation_wakeup_thursday_active, String automatic_operation_wakeup_thursday_time, boolean automatic_operation_wakeup_friday_active, String automatic_operation_wakeup_friday_time, boolean automatic_operation_wakeup_saturday_active, String automatic_operation_wakeup_saturday_time, boolean automatic_operation_wakeup_sunday_active, String automatic_operation_wakeup_sunday_time, boolean monitoring_questionnaire_allowed, boolean monitoring_weekly_report_allowed, boolean monitoring_error_report_allowed, int bed_fast_mode, int bed_combi_locked, int bed_head_locked, int bed_leg_locked, int bed_height_locked, int autodriveDegreeSetting, int timer_setting, boolean mori_feature_active, int user_desired_hardness, int sleep_reset_timing, boolean forest_report_allowed, int snoring_storage_enable) {
        this.ads_allowed = ads_allowed;
        this.automatic_operation_alarm_id = automatic_operation_alarm_id;
        this.automatic_operation_sleep_active = automatic_operation_sleep_active;
        this.monitoring_allowed = monitoring_allowed;
        this.automatic_operation_bed_pattern_id = automatic_operation_bed_pattern_id;
        this.automatic_operation_reminder_allowed = automatic_operation_reminder_allowed;
        this.automatic_operation_wakeup_monday_active = automatic_operation_wakeup_monday_active;
        this.automatic_operation_wakeup_monday_time = automatic_operation_wakeup_monday_time;
        this.automatic_operation_wakeup_tuesday_active = automatic_operation_wakeup_tuesday_active;
        this.automatic_operation_wakeup_tuesday_time = automatic_operation_wakeup_tuesday_time;
        this.automatic_operation_wakeup_wednesday_active = automatic_operation_wakeup_wednesday_active;
        this.automatic_operation_wakeup_wednesday_time = automatic_operation_wakeup_wednesday_time;
        this.automatic_operation_wakeup_thursday_active = automatic_operation_wakeup_thursday_active;
        this.automatic_operation_wakeup_thursday_time = automatic_operation_wakeup_thursday_time;
        this.automatic_operation_wakeup_friday_active = automatic_operation_wakeup_friday_active;
        this.automatic_operation_wakeup_friday_time = automatic_operation_wakeup_friday_time;
        this.automatic_operation_wakeup_saturday_active = automatic_operation_wakeup_saturday_active;
        this.automatic_operation_wakeup_saturday_time = automatic_operation_wakeup_saturday_time;
        this.automatic_operation_wakeup_sunday_active = automatic_operation_wakeup_sunday_active;
        this.automatic_operation_wakeup_sunday_time = automatic_operation_wakeup_sunday_time;
        this.monitoring_questionnaire_allowed = monitoring_questionnaire_allowed;
        this.monitoring_weekly_report_allowed = monitoring_weekly_report_allowed;
        this.monitoring_error_report_allowed = monitoring_error_report_allowed;
        this.bed_fast_mode = bed_fast_mode;
        this.bed_combi_locked = bed_combi_locked;
        this.bed_head_locked = bed_head_locked;
        this.bed_leg_locked = bed_leg_locked;
        this.bed_height_locked = bed_height_locked;
        this.autodriveDegreeSetting = autodriveDegreeSetting;
        this.timer_setting = timer_setting;
        this.mori_feature_active = mori_feature_active;
        this.user_desired_hardness = user_desired_hardness;
        this.sleep_reset_timing = sleep_reset_timing;
        this.forest_report_allowed = forest_report_allowed;
        this.snoring_storage_enable = snoring_storage_enable;
    }

    public boolean getMori_feature_active() {
        return mori_feature_active;
    }

    public void setMori_feature_active(boolean mori_feature_active) {
        this.mori_feature_active = mori_feature_active;
    }

    public boolean getForest_report_allowed() {
        return forest_report_allowed;
    }

    public void setForest_report_allowed(boolean forest_report_allowed) {
        this.forest_report_allowed = forest_report_allowed;
    }

    public int getTimer_setting() {
        return timer_setting;
    }

    public void setTimer_setting(int timer_setting) {
        this.timer_setting = timer_setting;
    }

    public boolean isAds_allowed() {
        return ads_allowed;
    }

    public void setAds_allowed(boolean ads_allowed) {
        this.ads_allowed = ads_allowed;
    }

    public int getAutomatic_operation_alarm_id() {
        return automatic_operation_alarm_id;
    }

    public void setAutomatic_operation_alarm_id(int automatic_operation_alarm_id) {
        this.automatic_operation_alarm_id = automatic_operation_alarm_id;
    }

    public boolean isAutomatic_operation_wake_active() {
        return automatic_operation_sleep_active;
    }

    public void setAutomatic_operation_wake_active(boolean automatic_operation_sleep_active) {
        this.automatic_operation_sleep_active = automatic_operation_sleep_active;
    }

    public boolean isMonitoring_allowed() {
        return monitoring_allowed;
    }

    public void setMonitoring_allowed(boolean monitoring_allowed) {
        this.monitoring_allowed = monitoring_allowed;
    }

    public int getAutomatic_operation_bed_pattern_id() {
        return automatic_operation_bed_pattern_id;
    }

    public void setAutomatic_operation_bed_pattern_id(int automatic_operation_bed_pattern_id) {
        this.automatic_operation_bed_pattern_id = automatic_operation_bed_pattern_id;
    }

    public boolean isAutomatic_operation_reminder_allowed() {
        return automatic_operation_reminder_allowed;
    }

    public void setAutomatic_operation_reminder_allowed(boolean automatic_operation_reminder_allowed) {
        this.automatic_operation_reminder_allowed = automatic_operation_reminder_allowed;
    }

    public boolean isAutomatic_operation_sleep_monday_active() {
        return automatic_operation_wakeup_monday_active;
    }

    public void setAutomatic_operation_sleep_monday_active(boolean automatic_operation_wakeup_monday_active) {
        this.automatic_operation_wakeup_monday_active = automatic_operation_wakeup_monday_active;
    }

    public String getAutomatic_operation_sleep_monday_time() {
        return automatic_operation_wakeup_monday_time;
    }

    public void setAutomatic_operation_sleep_monday_time(String automatic_operation_wakeup_monday_time) {
        this.automatic_operation_wakeup_monday_time = automatic_operation_wakeup_monday_time;
    }

    public boolean isAutomatic_operation_sleep_tuesday_active() {
        return automatic_operation_wakeup_tuesday_active;
    }

    public void setAutomatic_operation_sleep_tuesday_active(boolean automatic_operation_wakeup_tuesday_active) {
        this.automatic_operation_wakeup_tuesday_active = automatic_operation_wakeup_tuesday_active;
    }

    public String getAutomatic_operation_sleep_tuesday_time() {
        return automatic_operation_wakeup_tuesday_time;
    }

    public void setAutomatic_operation_sleep_tuesday_time(String automatic_operation_wakeup_tuesday_time) {
        this.automatic_operation_wakeup_tuesday_time = automatic_operation_wakeup_tuesday_time;
    }

    public boolean isAutomatic_operation_sleep_wednesday_active() {
        return automatic_operation_wakeup_wednesday_active;
    }

    public void setAutomatic_operation_sleep_wednesday_active(boolean automatic_operation_wakeup_wednesday_active) {
        this.automatic_operation_wakeup_wednesday_active = automatic_operation_wakeup_wednesday_active;
    }

    public String getAutomatic_operation_sleep_wednesday_time() {
        return automatic_operation_wakeup_wednesday_time;
    }

    public void setAutomatic_operation_sleep_wednesday_time(String automatic_operation_wakeup_wednesday_time) {
        this.automatic_operation_wakeup_wednesday_time = automatic_operation_wakeup_wednesday_time;
    }

    public boolean isAutomatic_operation_sleep_thursday_active() {
        return automatic_operation_wakeup_thursday_active;
    }

    public void setAutomatic_operation_sleep_thursday_active(boolean automatic_operation_wakeup_thursday_active) {
        this.automatic_operation_wakeup_thursday_active = automatic_operation_wakeup_thursday_active;
    }

    public String getAutomatic_operation_sleep_thursday_time() {
        return automatic_operation_wakeup_thursday_time;
    }

    public void setAutomatic_operation_sleep_thursday_time(String automatic_operation_wakeup_thursday_time) {
        this.automatic_operation_wakeup_thursday_time = automatic_operation_wakeup_thursday_time;
    }

    public boolean isAutomatic_operation_sleep_friday_active() {
        return automatic_operation_wakeup_friday_active;
    }

    public void setAutomatic_operation_sleep_friday_active(boolean automatic_operation_wakeup_friday_active) {
        this.automatic_operation_wakeup_friday_active = automatic_operation_wakeup_friday_active;
    }

    public String getAutomatic_operation_sleep_friday_time() {
        return automatic_operation_wakeup_friday_time;
    }

    public void setAutomatic_operation_sleep_friday_time(String automatic_operation_wakeup_friday_time) {
        this.automatic_operation_wakeup_friday_time = automatic_operation_wakeup_friday_time;
    }

    public boolean isAutomatic_operation_sleep_saturday_active() {
        return automatic_operation_wakeup_saturday_active;
    }

    public void setAutomatic_operation_sleep_saturday_active(boolean automatic_operation_wakeup_saturday_active) {
        this.automatic_operation_wakeup_saturday_active = automatic_operation_wakeup_saturday_active;
    }

    public String getAutomatic_operation_sleep_saturday_time() {
        return automatic_operation_wakeup_saturday_time;
    }

    public void setAutomatic_operation_sleep_saturday_time(String automatic_operation_wakeup_saturday_time) {
        this.automatic_operation_wakeup_saturday_time = automatic_operation_wakeup_saturday_time;
    }

    public boolean isAutomatic_operation_sleep_sunday_active() {
        return automatic_operation_wakeup_sunday_active;
    }

    public void setAutomatic_operation_sleep_sunday_active(boolean automatic_operation_wakeup_sunday_active) {
        this.automatic_operation_wakeup_sunday_active = automatic_operation_wakeup_sunday_active;
    }

    public String getAutomatic_operation_sleep_sunday_time() {
        return automatic_operation_wakeup_sunday_time;
    }

    public void setAutomatic_operation_sleep_sunday_time(String automatic_operation_wakeup_sunday_time) {
        this.automatic_operation_wakeup_sunday_time = automatic_operation_wakeup_sunday_time;
    }

    public boolean isMonitoring_questionnaire_allowed() {
        return monitoring_questionnaire_allowed;
    }

    public void setMonitoring_questionnaire_allowed(boolean monitoring_questionnaire_allowed) {
        this.monitoring_questionnaire_allowed = monitoring_questionnaire_allowed;
    }

    public boolean isMonitoring_weekly_report_allowed() {
        return monitoring_weekly_report_allowed;
    }

    public void setMonitoring_weekly_report_allowed(boolean monitoring_weekly_report_allowed) {
        this.monitoring_weekly_report_allowed = monitoring_weekly_report_allowed;
    }

    public boolean isMonitoring_error_report_allowed() {
        return monitoring_error_report_allowed;
    }

    public void setMonitoring_error_report_allowed(boolean monitoring_error_report_allowed) {
        this.monitoring_error_report_allowed = monitoring_error_report_allowed;
    }

    public int getAutodriveDegreeSetting() {
        return autodriveDegreeSetting;
    }

    public void setAutodriveDegreeSetting(int autodriveDegreeSetting) {
        this.autodriveDegreeSetting = autodriveDegreeSetting;
    }

    public int getBed_fast_mode() {
        return bed_fast_mode;
    }

    public void setBed_fast_mode(int bed_fast_mode) {
        this.bed_fast_mode = bed_fast_mode;
    }

    public int getBed_combi_locked() {
        return bed_combi_locked;
    }

    public void setBed_combi_locked(int bed_combi_locked) {
        this.bed_combi_locked = bed_combi_locked;
    }

    public int getBed_head_locked() {
        return bed_head_locked;
    }

    public void setBed_head_locked(int bed_head_locked) {
        this.bed_head_locked = bed_head_locked;
    }

    public int getBed_leg_locked() {
        return bed_leg_locked;
    }

    public void setBed_leg_locked(int bed_leg_locked) {
        this.bed_leg_locked = bed_leg_locked;
    }

    public int getBed_height_locked() {
        return bed_height_locked;
    }

    public void setBed_height_locked(int bed_height_locked) {
        this.bed_height_locked = bed_height_locked;
    }

    public int getSleep_reset_timing() {
        return sleep_reset_timing;
    }

    public void setSleep_reset_timing(int sleep_reset_timing) {
        this.sleep_reset_timing = sleep_reset_timing;
    }

    public int getSnoring_storage_enable() {
        return snoring_storage_enable;
    }

    public void setSnoring_storage_enable(int snoring_storage_enable) {
        this.snoring_storage_enable = snoring_storage_enable;
    }
}


