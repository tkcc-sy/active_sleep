package com.paramount.bed.data.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

import static com.paramount.bed.util.LogUtil.Logx;

public class DeviceSettingBedModelLog extends RealmObject {
    @PrimaryKey
    private String logID;
    private long timestamp;
    private String jsonString;

    public DeviceSettingBedModelLog() {
    }

    public DeviceSettingBedModelLog(String logID, long timestamp, String jsonString) {
        this.logID = logID;
        this.timestamp = timestamp;
        this.jsonString = jsonString;
    }

    public String getLogID() {
        return logID;
    }

    public void setLogID(String logID) {
        this.logID = logID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
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
        if (this.isValid()) {
            deleteFromRealm();
        }
        realm.commitTransaction();
    }


    public static DeviceSettingBedModelLog getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceSettingBedModelLog> query = realm.where(DeviceSettingBedModelLog.class).sort("timestamp", Sort.ASCENDING);
        DeviceSettingBedModelLog result = query.findFirst();
        return result;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(DeviceSettingBedModelLog.class);
        realm.commitTransaction();
    }

    public static void limitLogData() {
        if (DeviceSettingBedModelLog.getAll().size() >= MaxRowModel.getMaxRow().getMaxRowLog()) {
            DeviceSettingBedModelLog deviceSettingBedModelLog = DeviceSettingBedModelLog.getOldest();
            if(deviceSettingBedModelLog != null) {
                deviceSettingBedModelLog.delete();
                Logx("DeviceSettingBedModelLog:->DeletedByOverLimit", String.valueOf(DeviceSettingBedModelLog.getAll().size()));
                limitLogData();
            }
        }
    }

    public static void insertLogBedSettingChangeOffline(String jsonString) {
        limitLogData();
        DeviceSettingBedModelLog offlineLog = new DeviceSettingBedModelLog();
        offlineLog.setLogID(UUID.randomUUID().toString());
        offlineLog.setTimestamp(Calendar.getInstance().getTimeInMillis());
        offlineLog.setJsonString(jsonString);
        offlineLog.insert();
    }

    public static ArrayList<DeviceSettingBedModelLog> getAll() {
        ArrayList<DeviceSettingBedModelLog> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceSettingBedModelLog> query = realm.where(DeviceSettingBedModelLog.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static DeviceSettingBedModelLog getOldest() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceSettingBedModelLog> query = realm.where(DeviceSettingBedModelLog.class).sort("timestamp", Sort.ASCENDING);
        DeviceSettingBedModelLog result = query.findFirst();
        return result;
    }
}
