package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;

import static com.paramount.bed.util.LogUtil.Logx;

public class LogUserModel extends RealmObject {
    String id;
    String userId;
    String key;
    String value;
    String deviceType;
    String osVersion;
    String nemuriScanSN;
    boolean status;
    String screenId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getNemuriScanSN() {
        return nemuriScanSN;
    }

    public void setNemuriScanSN(String nemuriScanSN) {
        this.nemuriScanSN = nemuriScanSN;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public static LogUserModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<LogUserModel> query = realm.where(LogUserModel.class);
        LogUserModel result = query.findFirst();
        return result;
    }

    public static ArrayList<LogUserModel> getAll() {
        ArrayList<LogUserModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<LogUserModel> query = realm.where(LogUserModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();

        Logx("LogUserModel",
                "id->" + id +
                        " | userId->" + userId +
                        " | key->" + key +
                        " | value->" + value +
                        " | deviceType->" + deviceType +
                        " | osVersion->" + osVersion +
                        " | nemuriScanSN->" + nemuriScanSN +
                        " | status->" + status +
                        " | screenId->" + screenId
        );
        Logx("LogUserModel",
                "Total Row->" + getAll().size()
        );
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if(this.isValid()) {
            deleteFromRealm();
        }
        realm.commitTransaction();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(LogUserModel.class);
        realm.commitTransaction();
    }
    public static LogUserModel getOldest() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<LogUserModel> query = realm.where(LogUserModel.class).sort("id", Sort.ASCENDING);
        LogUserModel result = query.findFirst();
        return result;
    }
}
