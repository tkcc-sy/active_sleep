package com.paramount.bed.data.model;


import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class SenderBirdieListModel extends RealmObject {
    @PrimaryKey
    public String PID;
    public String monitoredUserId;
    public String monitoredNickname;
    public String createdDate;

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getMonitoredUserId() {
        return monitoredUserId;
    }

    public void setMonitoredUserId(String monitoredUserId) {
        this.monitoredUserId = monitoredUserId;
    }

    public String getMonitoredNickname() {
        return monitoredNickname;
    }

    public void setMonitoredNickname(String monitoredNickname) {
        this.monitoredNickname = monitoredNickname;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public static SenderBirdieListModel getByPID(String Name) {
        Realm realm = Realm.getDefaultInstance();

        SenderBirdieListModel validationNameModel = realm.where(SenderBirdieListModel.class).equalTo("PID", Name.trim()).findFirst();
        if (validationNameModel == null) {
            SenderBirdieListModel result = new SenderBirdieListModel();
            result.setPID(UUID.randomUUID().toString());
            result.setMonitoredUserId("");
            result.setMonitoredNickname("");
            result.setCreatedDate("");
            validationNameModel = result;
        }
        return validationNameModel;

    }

    public static void deleteByPID(String pid) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(SenderBirdieListModel.class).equalTo("PID", pid).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static SenderBirdieListModel updateByPID(String pid) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SenderBirdieListModel> query = realm.where(SenderBirdieListModel.class).equalTo("PID", pid.trim());
        SenderBirdieListModel result = query.findFirst();
        if (result == null) {
            SenderBirdieListModel data = new SenderBirdieListModel();
            result.setMonitoredUserId(data.getMonitoredUserId());
            result.setMonitoredNickname(data.getMonitoredNickname());
            result.setCreatedDate(data.getCreatedDate());
            data.insert();
        }
        return result;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(this);
        realm.commitTransaction();
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static SenderBirdieListModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SenderBirdieListModel> query = realm.where(SenderBirdieListModel.class);
        SenderBirdieListModel result = query.findFirst();
        return result;
    }

    public static ArrayList<SenderBirdieListModel> getAll() {
        ArrayList<SenderBirdieListModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SenderBirdieListModel> query = realm.where(SenderBirdieListModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SenderBirdieListModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SenderBirdieListModel.class);
        realm.commitTransaction();
    }

}
