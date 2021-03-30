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

public class DeviceTemplateMattressModelLog extends RealmObject {
    @PrimaryKey
    private String logID;
    private long timestamp;
    private int id;
    private int head;
    private int shoulder;
    private int hip;
    private int thigh;
    private int calf;
    private int feet;


    public DeviceTemplateMattressModelLog() {
    }

    public DeviceTemplateMattressModelLog(String logID, long timestamp, int id, int head, int shoulder, int hip, int thigh, int calf, int feet) {
        this.logID = logID;
        this.timestamp = timestamp;
        this.id = id;
        this.head = head;
        this.shoulder = shoulder;
        this.hip = hip;
        this.thigh = thigh;
        this.calf = calf;
        this.feet = feet;
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

    public int getShoulder() {
        return shoulder;
    }

    public void setShoulder(int shoulder) {
        this.shoulder = shoulder;
    }

    public int getHip() {
        return hip;
    }

    public void setHip(int hip) {
        this.hip = hip;
    }

    public int getThigh() {
        return thigh;
    }

    public void setThigh(int thigh) {
        this.thigh = thigh;
    }

    public int getCalf() {
        return calf;
    }

    public void setCalf(int calf) {
        this.calf = calf;
    }

    public int getFeet() {
        return feet;
    }

    public void setFeet(int feet) {
        this.feet = feet;
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
        if(this.isValid()) {
            deleteFromRealm();
        }
        realm.commitTransaction();
    }

    public static DeviceTemplateMattressModelLog getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateMattressModelLog> query = realm.where(DeviceTemplateMattressModelLog.class).sort("timestamp", Sort.ASCENDING);
        ;
        DeviceTemplateMattressModelLog result = query.findFirst();
        return result;
    }

    public static ArrayList<DeviceTemplateMattressModelLog> getAll(boolean isDefault) {
        ArrayList<DeviceTemplateMattressModelLog> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateMattressModelLog> query = realm.where(DeviceTemplateMattressModelLog.class).equalTo("isDefault", isDefault);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(DeviceTemplateMattressModelLog.class);
        realm.commitTransaction();
    }

    public static void limitLogData() {
        if (DeviceTemplateMattressModelLog.getAll().size() >= MaxRowModel.getMaxRow().getMaxRowLog()) {
            DeviceTemplateMattressModelLog deviceTemplateMattressModelLog = DeviceTemplateMattressModelLog.getOldest();
            if(deviceTemplateMattressModelLog != null) {
                deviceTemplateMattressModelLog.delete();
                Logx("DeviceTemplateMattressModelLog:->DeletedByOverLimit", String.valueOf(DeviceTemplateMattressModelLog.getAll().size()));
                limitLogData();
            }
        }
    }

    public static void insertLogMattressTemplateChangeOffline(DeviceTemplateMattressModel target) {
        limitLogData();
        DeviceTemplateMattressModelLog offlineLog = new DeviceTemplateMattressModelLog();
        offlineLog.setLogID(UUID.randomUUID().toString());
        offlineLog.setTimestamp(Calendar.getInstance().getTimeInMillis());
        offlineLog.setId(target.getId());
        offlineLog.setHead(target.getHead());
        offlineLog.setShoulder(target.getShoulder());
        offlineLog.setHip(target.getHip());
        offlineLog.setThigh(target.getThigh());
        offlineLog.setCalf(target.getCalf());
        offlineLog.setFeet(target.getFeet());
        offlineLog.insert();
    }

    public static ArrayList<DeviceTemplateMattressModelLog> getAll() {
        ArrayList<DeviceTemplateMattressModelLog> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateMattressModelLog> query = realm.where(DeviceTemplateMattressModelLog.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static DeviceTemplateMattressModelLog getOldest() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateMattressModelLog> query = realm.where(DeviceTemplateMattressModelLog.class).sort("timestamp", Sort.ASCENDING);
        DeviceTemplateMattressModelLog result = query.findFirst();
        return result;
    }
}
