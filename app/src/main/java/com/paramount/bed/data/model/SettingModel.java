package com.paramount.bed.data.model;


import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class SettingModel extends RealmObject {
    //TODO : RENAME TO CAMELCASE
    public boolean ads_allowed;
    public int automatic_operation_alarm_id;
    public boolean automatic_operation_sleep_active;
    public boolean monitoring_allowed;
    public int automatic_operation_bed_pattern_id;
    public boolean automatic_operation_reminder_allowed;
    public boolean automatic_operation_wakeup_monday_active;
    public String automatic_operation_wakeup_monday_time;
    public boolean automatic_operation_wakeup_tuesday_active;
    public String automatic_operation_wakeup_tuesday_time;
    public boolean automatic_operation_wakeup_wednesday_active;
    public String automatic_operation_wakeup_wednesday_time;
    public boolean automatic_operation_wakeup_thursday_active;
    public String automatic_operation_wakeup_thursday_time;
    public boolean automatic_operation_wakeup_friday_active;
    public String automatic_operation_wakeup_friday_time;
    public boolean automatic_operation_wakeup_saturday_active;
    public String automatic_operation_wakeup_saturday_time;
    public boolean automatic_operation_wakeup_sunday_active;
    public String automatic_operation_wakeup_sunday_time;
    public boolean monitoring_questionnaire_allowed;
    public boolean monitoring_weekly_report_allowed;
    public boolean monitoring_error_report_allowed;
    public int bed_fast_mode;

    public int bed_combi_locked;
    public int bed_head_locked;
    public int bed_leg_locked;
    public int bed_height_locked;
    public int autodriveDegreeSetting;
    public int timer_setting;
    public int mori_feature_active;
    public int user_desired_hardness=3;
    public int sleep_reset_timing;
    public boolean forest_report_allowed;
    public int snoring_storage_enable;

    public SettingModel() {
    }

    public SettingModel(boolean ads_allowed, int automatic_operation_alarm_id, boolean automatic_operation_sleep_active, boolean monitoring_allowed, int automatic_operation_bed_pattern_id, boolean automatic_operation_reminder_allowed, boolean automatic_operation_wakeup_monday_active, String automatic_operation_wakeup_monday_time, boolean automatic_operation_wakeup_tuesday_active, String automatic_operation_wakeup_tuesday_time, boolean automatic_operation_wakeup_wednesday_active, String automatic_operation_wakeup_wednesday_time, boolean automatic_operation_wakeup_thursday_active, String automatic_operation_wakeup_thursday_time, boolean automatic_operation_wakeup_friday_active, String automatic_operation_wakeup_friday_time, boolean automatic_operation_wakeup_saturday_active, String automatic_operation_wakeup_saturday_time, boolean automatic_operation_wakeup_sunday_active, String automatic_operation_wakeup_sunday_time, boolean monitoring_questionnaire_allowed, boolean monitoring_weekly_report_allowed, boolean monitoring_error_report_allowed, int bed_fast_mode, int bed_combi_locked, int bed_head_locked, int bed_leg_locked, int bed_height_locked, int autodriveDegreeSetting, int timer_setting, int mori_feature_active, int user_desired_hardness, int sleep_reset_timing, boolean forest_report_allowed, int snoring_storage_enable) {
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

    public int isMori_feature_active() {
        return mori_feature_active;
    }

    public void setMori_feature_active(int mori_feature_active) {
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

    public int getUser_desired_hardness() {
        return user_desired_hardness;
    }

    public void setUser_desired_hardness(int user_desired_hardness) {
        this.user_desired_hardness = user_desired_hardness;
    }

    public int getSleep_reset_timing() {
        return sleep_reset_timing;
    }

    public void setSleep_reset_timing(int sleep_reset_timing) {
        this.sleep_reset_timing = sleep_reset_timing;
    }

    public int getBed_fast_mode() {
        return bed_fast_mode;
    }

    public void setBed_fast_mode(int bed_fast_mode) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            this.bed_fast_mode = bed_fast_mode;
            realm.commitTransaction();
        }
    }

    public int getBed_combi_locked() {
        return bed_combi_locked;
    }

    public void setBed_combi_locked(int bed_combi_locked) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            this.bed_combi_locked = bed_combi_locked;
            realm.commitTransaction();
        }
    }

    public int getBed_head_locked() {
        return bed_head_locked;
    }

    public void setBed_head_locked(int bed_head_locked) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            this.bed_head_locked = bed_head_locked;
            realm.commitTransaction();
        }
    }

    public int getBed_leg_locked() {
        return bed_leg_locked;
    }

    public void setBed_leg_locked(int bed_leg_locked) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            this.bed_leg_locked = bed_leg_locked;
            realm.commitTransaction();
        }
    }

    public int getBed_height_locked() {
        return bed_height_locked;
    }

    public int getSnoring_storage_enable() {
        return snoring_storage_enable;
    }

    public void setSnoring_storage_enable(int snoring_storage_enable) {
        this.snoring_storage_enable = snoring_storage_enable;
    }

    public void setBed_height_locked(int bed_height_locked) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            this.bed_height_locked = bed_height_locked;
            realm.commitTransaction();
        }
    }

    public static void saveSetting(String key, String value) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingModel> query = realm.where(SettingModel.class);
        SettingModel setting = query.findFirst();
        SettingModel InsertChanges = new SettingModel();
        InsertChanges.ads_allowed = key == "ads_allowed" ? Boolean.parseBoolean(value) : setting.ads_allowed;
        InsertChanges.automatic_operation_alarm_id = key == "automatic_operation_alarm_id" ? Integer.parseInt(value) : setting.automatic_operation_alarm_id;
        InsertChanges.automatic_operation_sleep_active = key == "automatic_operation_sleep_active" ? Boolean.parseBoolean(value) : setting.automatic_operation_sleep_active;
        InsertChanges.monitoring_allowed = key == "monitoring_allowed" ? Boolean.parseBoolean(value) : setting.monitoring_allowed;
        InsertChanges.automatic_operation_bed_pattern_id = key == "automatic_operation_bed_pattern_id" ? Integer.parseInt(value) : setting.automatic_operation_bed_pattern_id;
        InsertChanges.automatic_operation_reminder_allowed = key == "automatic_operation_reminder_allowed" ? Boolean.parseBoolean(value) : setting.automatic_operation_reminder_allowed;
        InsertChanges.automatic_operation_wakeup_monday_active = key == "automatic_operation_wakeup_monday_active" ? Boolean.parseBoolean(value) : setting.automatic_operation_wakeup_monday_active;
        InsertChanges.automatic_operation_wakeup_monday_time = key == "automatic_operation_wakeup_monday_time" ? value : setting.automatic_operation_wakeup_monday_time;
        InsertChanges.automatic_operation_wakeup_tuesday_active = key == "automatic_operation_wakeup_tuesday_active" ? Boolean.parseBoolean(value) : setting.automatic_operation_wakeup_tuesday_active;
        InsertChanges.automatic_operation_wakeup_tuesday_time = key == "automatic_operation_wakeup_tuesday_time" ? value : setting.automatic_operation_wakeup_tuesday_time;
        InsertChanges.automatic_operation_wakeup_wednesday_active = key == "automatic_operation_wakeup_wednesday_active" ? Boolean.parseBoolean(value) : setting.automatic_operation_wakeup_wednesday_active;
        InsertChanges.automatic_operation_wakeup_wednesday_time = key == "automatic_operation_wakeup_wednesday_time" ? value : setting.automatic_operation_wakeup_wednesday_time;
        InsertChanges.automatic_operation_wakeup_thursday_active = key == "automatic_operation_wakeup_thursday_active" ? Boolean.parseBoolean(value) : setting.automatic_operation_wakeup_thursday_active;
        InsertChanges.automatic_operation_wakeup_thursday_time = key == "automatic_operation_wakeup_thursday_time" ? value : setting.automatic_operation_wakeup_thursday_time;
        InsertChanges.automatic_operation_wakeup_friday_active = key == "automatic_operation_wakeup_friday_active" ? Boolean.parseBoolean(value) : setting.automatic_operation_wakeup_friday_active;
        InsertChanges.automatic_operation_wakeup_friday_time = key == "automatic_operation_wakeup_friday_time" ? value : setting.automatic_operation_wakeup_friday_time;
        InsertChanges.automatic_operation_wakeup_saturday_active = key == "automatic_operation_wakeup_saturday_active" ? Boolean.parseBoolean(value) : setting.automatic_operation_wakeup_saturday_active;
        InsertChanges.automatic_operation_wakeup_saturday_time = key == "automatic_operation_wakeup_saturday_time" ? value : setting.automatic_operation_wakeup_saturday_time;
        InsertChanges.automatic_operation_wakeup_sunday_active = key == "automatic_operation_wakeup_sunday_active" ? Boolean.parseBoolean(value) : setting.automatic_operation_wakeup_sunday_active;
        InsertChanges.automatic_operation_wakeup_sunday_time = key == "automatic_operation_wakeup_sunday_time" ? value : setting.automatic_operation_wakeup_sunday_time;
        InsertChanges.monitoring_questionnaire_allowed = key == "monitoring_questionnaire_allowed" ? Boolean.parseBoolean(value) : setting.monitoring_questionnaire_allowed;
        InsertChanges.monitoring_weekly_report_allowed = key == "monitoring_weekly_report_allowed" ? Boolean.parseBoolean(value) : setting.monitoring_weekly_report_allowed;
        InsertChanges.monitoring_error_report_allowed = key == "monitoring_error_report_allowed" ? Boolean.parseBoolean(value) : setting.monitoring_error_report_allowed;

        InsertChanges.bed_fast_mode = key == "bed_fast_mode" ? Integer.parseInt(value) : setting.bed_fast_mode;
        InsertChanges.bed_combi_locked = key == "bed_combi_locked" ? Integer.parseInt(value) : setting.bed_combi_locked;
        InsertChanges.bed_head_locked = key == "bed_head_locked" ? Integer.parseInt(value) : setting.bed_head_locked;
        InsertChanges.bed_leg_locked = key == "bed_leg_locked" ? Integer.parseInt(value) : setting.bed_leg_locked;
        InsertChanges.bed_height_locked = key == "bed_height_locked" ? Integer.parseInt(value) : setting.bed_height_locked;
        InsertChanges.autodriveDegreeSetting = key == "automatic_operation_bed_degree" ? Integer.valueOf(value) : setting.autodriveDegreeSetting;
        InsertChanges.timer_setting = key == "timer_setting" ? Integer.valueOf(value) : setting.timer_setting;
        InsertChanges.mori_feature_active = key == "mori_feature_active" ? Integer.valueOf(value) : setting.mori_feature_active;
        InsertChanges.user_desired_hardness = key == "user_desired_hardness" ? Integer.valueOf(value) : setting.user_desired_hardness;
        InsertChanges.sleep_reset_timing = key == "sleep_reset_timing" ? Integer.valueOf(value) : setting.sleep_reset_timing;
        InsertChanges.forest_report_allowed = key == "forest_report_allowed" ? Boolean.valueOf(value) : setting.forest_report_allowed;
        InsertChanges.snoring_storage_enable = key == "snoring_storage_enable" ? Integer.parseInt(value) : setting.snoring_storage_enable;

        SettingModel.truncate();
        InsertChanges.insert();
    }

    public int getAutodriveDegreeSetting() {
        return autodriveDegreeSetting;
    }

    public void setAutodriveDegreeSetting(int autodriveDegreeSetting) {
        this.autodriveDegreeSetting = autodriveDegreeSetting;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static SettingModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingModel> query = realm.where(SettingModel.class);
        SettingModel result = query.findFirst();
        return result;
    }

    public static ArrayList<SettingModel> getAll() {
        ArrayList<SettingModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingModel> query = realm.where(SettingModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingModel.class);
        realm.commitTransaction();
    }

    public static SettingModel getSetting() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingModel> query = realm.where(SettingModel.class);
        SettingModel result = query.findFirst();
        if (result == null) {
            result = InitialSetting();
        }
        return result;
    }

    public static String getAlarmTime(int dayOfWeek) {
        String alarmTime = null;
        SettingModel currentSetting = SettingModel.getSetting();
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                alarmTime = currentSetting.getAutomatic_operation_sleep_sunday_time();
                break;
            case Calendar.MONDAY:
                alarmTime = currentSetting.getAutomatic_operation_sleep_monday_time();
                break;
            case Calendar.TUESDAY:
                alarmTime = currentSetting.getAutomatic_operation_sleep_tuesday_time();
                break;
            case Calendar.WEDNESDAY:
                alarmTime = currentSetting.getAutomatic_operation_sleep_wednesday_time();
                break;
            case Calendar.THURSDAY:
                alarmTime = currentSetting.getAutomatic_operation_sleep_thursday_time();
                break;
            case Calendar.FRIDAY:
                alarmTime = currentSetting.getAutomatic_operation_sleep_friday_time();
                break;
            case Calendar.SATURDAY:
                alarmTime = currentSetting.getAutomatic_operation_sleep_saturday_time();
                break;
        }
        return alarmTime;
    }

    public static boolean getAlarmActive(int dayOfWeek) {
        boolean alarmActive = false;
        SettingModel currentSetting = SettingModel.getSetting();
        switch (dayOfWeek) {
            case Calendar.SUNDAY:
                alarmActive = currentSetting.isAutomatic_operation_sleep_sunday_active();
                break;
            case Calendar.MONDAY:
                alarmActive = currentSetting.isAutomatic_operation_sleep_monday_active();
                break;
            case Calendar.TUESDAY:
                alarmActive = currentSetting.isAutomatic_operation_sleep_tuesday_active();
                break;
            case Calendar.WEDNESDAY:
                alarmActive = currentSetting.isAutomatic_operation_sleep_wednesday_active();
                break;
            case Calendar.THURSDAY:
                alarmActive = currentSetting.isAutomatic_operation_sleep_thursday_active();
                break;
            case Calendar.FRIDAY:
                alarmActive = currentSetting.isAutomatic_operation_sleep_friday_active();
                break;
            case Calendar.SATURDAY:
                alarmActive = currentSetting.isAutomatic_operation_sleep_saturday_active();
                break;
        }
        return alarmActive;
    }

    public static SettingModel getUnmanagedModel(){
        SettingModel settingModel = SettingModel.getSetting();
        if(settingModel != null){
            settingModel = settingModel.getUnmanaged();
        }
        return settingModel;
    }


    public SettingModel getUnmanaged() {
        if(!isManaged()){
            return this;
        }
        Realm realm = Realm.getDefaultInstance();
        SettingModel unmanagedObject =  realm.copyFromRealm(this);
        realm.close();
        return unmanagedObject;
    }

    public static SettingModel InitialSetting() {
        SettingModel.truncate();
        SettingModel setting = new SettingModel();
        setting.ads_allowed = true;
        setting.automatic_operation_alarm_id = 1;
        setting.automatic_operation_sleep_active = false;
        setting.monitoring_allowed = true;
        setting.automatic_operation_bed_pattern_id = 1;
        setting.automatic_operation_reminder_allowed = true;
        setting.automatic_operation_wakeup_monday_active = false;
        setting.automatic_operation_wakeup_monday_time = "07:00";
        setting.automatic_operation_wakeup_tuesday_active = false;
        setting.automatic_operation_wakeup_tuesday_time = "07:00";
        setting.automatic_operation_wakeup_wednesday_active = false;
        setting.automatic_operation_wakeup_wednesday_time = "07:00";
        setting.automatic_operation_wakeup_thursday_active = false;
        setting.automatic_operation_wakeup_thursday_time = "07:00";
        setting.automatic_operation_wakeup_friday_active = false;
        setting.automatic_operation_wakeup_friday_time = "07:00";
        setting.automatic_operation_wakeup_saturday_active = false;
        setting.automatic_operation_wakeup_saturday_time = "07:00";
        setting.automatic_operation_wakeup_sunday_active = false;
        setting.automatic_operation_wakeup_sunday_time = "07:00";
        setting.monitoring_questionnaire_allowed = true;
        setting.monitoring_weekly_report_allowed = true;
        setting.monitoring_error_report_allowed = true;
        setting.bed_fast_mode = 1;
        setting.bed_combi_locked = 1;
        setting.bed_head_locked = 1;
        setting.bed_leg_locked = 1;
        setting.bed_height_locked = 1;
        setting.autodriveDegreeSetting = 0;
        setting.timer_setting = 0;
        setting.mori_feature_active = 1;
        setting.user_desired_hardness = 3;
        setting.sleep_reset_timing = 0;
        setting.forest_report_allowed = true;
        setting.snoring_storage_enable = 1;
        setting.insert();
        return setting;
    }

    public static SettingModel resetNSRelatedSettings() {
        SettingModel currentSetting = SettingModel.getSetting();
        if (UserLogin.haveRegisteredNS()) {
            //do not reset if NS regstered in server
            return currentSetting;
        }
        SettingModel setting = new SettingModel();
        setting.copyValuesFrom(currentSetting);

        SettingModel.truncate();
        setting.automatic_operation_alarm_id = 1;
        setting.automatic_operation_sleep_active = false;
        setting.automatic_operation_bed_pattern_id = 1;
        setting.automatic_operation_wakeup_monday_active = false;
        setting.automatic_operation_wakeup_monday_time = "07:00";
        setting.automatic_operation_wakeup_tuesday_active = false;
        setting.automatic_operation_wakeup_tuesday_time = "07:00";
        setting.automatic_operation_wakeup_wednesday_active = false;
        setting.automatic_operation_wakeup_wednesday_time = "07:00";
        setting.automatic_operation_wakeup_thursday_active = false;
        setting.automatic_operation_wakeup_thursday_time = "07:00";
        setting.automatic_operation_wakeup_friday_active = false;
        setting.automatic_operation_wakeup_friday_time = "07:00";
        setting.automatic_operation_wakeup_saturday_active = false;
        setting.automatic_operation_wakeup_saturday_time = "07:00";
        setting.automatic_operation_wakeup_sunday_active = false;
        setting.automatic_operation_wakeup_sunday_time = "07:00";
        setting.bed_fast_mode = 1;
        setting.bed_combi_locked = 1;
        setting.bed_head_locked = 1;
        setting.bed_leg_locked = 1;
        setting.bed_height_locked = 1;
        setting.timer_setting = 0;
        setting.sleep_reset_timing = 0;
        setting.insert();
        return setting;
    }

    public void copyValuesFrom(SettingModel settingModel) {
        this.ads_allowed = settingModel.ads_allowed;
        this.automatic_operation_alarm_id = settingModel.automatic_operation_alarm_id;
        this.automatic_operation_sleep_active = settingModel.automatic_operation_sleep_active;
        this.monitoring_allowed = settingModel.monitoring_allowed;
        this.automatic_operation_bed_pattern_id = settingModel.automatic_operation_bed_pattern_id;
        this.automatic_operation_reminder_allowed = settingModel.automatic_operation_reminder_allowed;
        this.automatic_operation_wakeup_monday_active = settingModel.automatic_operation_wakeup_monday_active;
        this.automatic_operation_wakeup_monday_time = settingModel.automatic_operation_wakeup_monday_time;
        this.automatic_operation_wakeup_tuesday_active = settingModel.automatic_operation_wakeup_tuesday_active;
        this.automatic_operation_wakeup_tuesday_time = settingModel.automatic_operation_wakeup_tuesday_time;
        this.automatic_operation_wakeup_wednesday_active = settingModel.automatic_operation_wakeup_wednesday_active;
        this.automatic_operation_wakeup_wednesday_time = settingModel.automatic_operation_wakeup_wednesday_time;
        this.automatic_operation_wakeup_thursday_active = settingModel.automatic_operation_wakeup_thursday_active;
        this.automatic_operation_wakeup_thursday_time = settingModel.automatic_operation_wakeup_thursday_time;
        this.automatic_operation_wakeup_friday_active = settingModel.automatic_operation_wakeup_friday_active;
        this.automatic_operation_wakeup_friday_time = settingModel.automatic_operation_wakeup_friday_time;
        this.automatic_operation_wakeup_saturday_active = settingModel.automatic_operation_wakeup_saturday_active;
        this.automatic_operation_wakeup_saturday_time = settingModel.automatic_operation_wakeup_saturday_time;
        this.automatic_operation_wakeup_sunday_active = settingModel.automatic_operation_wakeup_sunday_active;
        this.automatic_operation_wakeup_sunday_time = settingModel.automatic_operation_wakeup_sunday_time;
        this.monitoring_questionnaire_allowed = settingModel.monitoring_questionnaire_allowed;
        this.monitoring_weekly_report_allowed = settingModel.monitoring_weekly_report_allowed;
        this.monitoring_error_report_allowed = settingModel.monitoring_error_report_allowed;

        this.bed_fast_mode = settingModel.bed_fast_mode;
        this.bed_combi_locked = settingModel.bed_combi_locked;
        this.bed_head_locked = settingModel.bed_head_locked;
        this.bed_leg_locked = settingModel.bed_leg_locked;
        this.bed_height_locked = settingModel.bed_height_locked;
        this.autodriveDegreeSetting = settingModel.autodriveDegreeSetting;
        this.timer_setting = settingModel.timer_setting;
        this.mori_feature_active = settingModel.mori_feature_active;
        this.user_desired_hardness = settingModel.user_desired_hardness;
        this.sleep_reset_timing = settingModel.sleep_reset_timing;
        this.forest_report_allowed = settingModel.forest_report_allowed;
        this.snoring_storage_enable = settingModel.snoring_storage_enable;
    }

    public boolean isAlarmSettingDifferentFrom(SettingModel otherSetting) {
        return this.automatic_operation_wakeup_monday_active != otherSetting.automatic_operation_wakeup_monday_active ||
                this.automatic_operation_wakeup_tuesday_active != otherSetting.automatic_operation_wakeup_tuesday_active ||
                this.automatic_operation_wakeup_wednesday_active != otherSetting.automatic_operation_wakeup_wednesday_active ||
                this.automatic_operation_wakeup_thursday_active != otherSetting.automatic_operation_wakeup_thursday_active ||
                this.automatic_operation_wakeup_friday_active != otherSetting.automatic_operation_wakeup_friday_active ||
                this.automatic_operation_wakeup_saturday_active != otherSetting.automatic_operation_wakeup_saturday_active ||
                this.automatic_operation_wakeup_sunday_active != otherSetting.automatic_operation_wakeup_sunday_active ||

                !this.automatic_operation_wakeup_monday_time.equals(otherSetting.automatic_operation_wakeup_monday_time) ||
                !this.automatic_operation_wakeup_tuesday_time.equals(otherSetting.automatic_operation_wakeup_tuesday_time) ||
                !this.automatic_operation_wakeup_wednesday_time.equals(otherSetting.automatic_operation_wakeup_wednesday_time) ||
                !this.automatic_operation_wakeup_thursday_time.equals(otherSetting.automatic_operation_wakeup_thursday_time) ||
                !this.automatic_operation_wakeup_friday_time.equals(otherSetting.automatic_operation_wakeup_friday_time) ||
                !this.automatic_operation_wakeup_saturday_time.equals(otherSetting.automatic_operation_wakeup_saturday_time) ||
                !this.automatic_operation_wakeup_sunday_time.equals(otherSetting.automatic_operation_wakeup_sunday_time)
                ;

    }

    public boolean isAlarmSoundSettingDifferentFrom(SettingModel otherSetting) {
        return this.automatic_operation_alarm_id != otherSetting.automatic_operation_alarm_id;
    }

    public boolean isBedPatternSettingDifferentFrom(SettingModel otherSetting) {
        return this.automatic_operation_bed_pattern_id != otherSetting.automatic_operation_bed_pattern_id;
    }

    public boolean isAutomaticWakeSettingDifferentFrom(SettingModel otherSetting) {
        if (this.isAlarmSettingDifferentFrom(otherSetting)) return true;
        if (this.isBedPatternSettingDifferentFrom(otherSetting)) return true;
        if (this.isAlarmSoundSettingDifferentFrom(otherSetting)) return true;
        return false;
    }
}
