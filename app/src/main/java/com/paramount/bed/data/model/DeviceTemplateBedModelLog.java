package com.paramount.bed.data.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

import static com.paramount.bed.util.LogUtil.Logx;

public class DeviceTemplateBedModelLog extends RealmObject {
    @PrimaryKey
    private String logID;
    private long timestamp;
    private int id;
    private int head;
    private int leg;
    private int tilt;
    private int height;
    private int tiltDefault;
    private int heightDefault;
    private boolean isDefault = false;

    public DeviceTemplateBedModelLog() {
    }

    public DeviceTemplateBedModelLog(String logID, long timestamp, int id, int head, int leg, int tilt, int height, int tiltDefault, int heightDefault, boolean isDefault) {
        this.logID = logID;
        this.timestamp = timestamp;
        this.id = id;
        this.head = head;
        this.leg = leg;
        this.tilt = tilt;
        this.height = height;
        this.tiltDefault = tiltDefault;
        this.heightDefault = heightDefault;
        this.isDefault = isDefault;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getLogID() {
        return logID;
    }

    public void setLogID(String logID) {
        this.logID = logID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getLeg() {
        return leg;
    }

    public void setLeg(int leg) {
        this.leg = leg;
    }

    public int getTilt() {
        return tilt;
    }

    public void setTilt(int tilt) {
        this.tilt = tilt;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getTiltDefault() {
        return tiltDefault;
    }

    public void setTiltDefault(int tiltDefault) {
        this.tiltDefault = tiltDefault;
    }

    public int getHeightDefault() {
        return heightDefault;
    }

    public void setHeightDefault(int heightDefault) {
        this.heightDefault = heightDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
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

    public static ArrayList<DeviceTemplateBedModelLog> getAll(boolean isDefault) {
        ArrayList<DeviceTemplateBedModelLog> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateBedModelLog> query = realm.where(DeviceTemplateBedModelLog.class).equalTo("isDefault", isDefault);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public static DeviceTemplateBedModelLog getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateBedModelLog> query = realm.where(DeviceTemplateBedModelLog.class).sort("timestamp", Sort.ASCENDING);
        DeviceTemplateBedModelLog result = query.findFirst();
        return result;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(DeviceTemplateBedModelLog.class);
        realm.commitTransaction();
    }

    public static void limitLogData() {
        if (DeviceTemplateBedModelLog.getAll().size() >= MaxRowModel.getMaxRow().getMaxRowLog()) {
            DeviceTemplateBedModelLog deviceTemplateBedModelLog = DeviceTemplateBedModelLog.getOldest();
            if(deviceTemplateBedModelLog != null) {
                Logx("DeviceTemplateBedModelLog:->ID Deleted", deviceTemplateBedModelLog.getLogID());
                deviceTemplateBedModelLog.delete();
                Logx("DeviceTemplateBedModelLog:->DeletedByOverLimit", String.valueOf(DeviceTemplateBedModelLog.getAll().size()));
                limitLogData();
            }
        }
    }

    public static void insertLogBedTemplateChangeOffline(DeviceTemplateBedModel target) {
        limitLogData();
        DeviceTemplateBedModelLog offlineLog = new DeviceTemplateBedModelLog();
        offlineLog.setLogID(UUID.randomUUID().toString());
        offlineLog.setTimestamp(Calendar.getInstance().getTimeInMillis());
        offlineLog.setId(target.getId());
        offlineLog.setHead(target.getHead());
        offlineLog.setLeg(target.getLeg());
        offlineLog.setTilt(target.getTilt());
        offlineLog.setHeight(target.getHeight());
        offlineLog.insert();
        Logx("DeviceTemplateBedModelLog:->ID Inserted", offlineLog.getLogID());
    }

    public static ArrayList<DeviceTemplateBedModelLog> getAll() {
        ArrayList<DeviceTemplateBedModelLog> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateBedModelLog> query = realm.where(DeviceTemplateBedModelLog.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static DeviceTemplateBedModelLog getOldest() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateBedModelLog> query = realm.where(DeviceTemplateBedModelLog.class).sort("timestamp", Sort.ASCENDING);
        DeviceTemplateBedModelLog result = query.findFirst();
        return result;
    }
}
