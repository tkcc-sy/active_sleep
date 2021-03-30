package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class PendingAlarmModel extends RealmObject {
    @PrimaryKey
    public String pendingAlarmId;
    public int pendingAlarmType;
    public int pendingAlarmDay;

    public PendingAlarmModel() {
    }

    public PendingAlarmModel(String pendingAlarmId, int pendingAlarmType, int pendingAlarmDay) {
        this.pendingAlarmId = pendingAlarmId;
        this.pendingAlarmType = pendingAlarmType;
        this.pendingAlarmDay = pendingAlarmDay;
    }

    public String getPendingAlarmId() {
        return pendingAlarmId;
    }

    public void setPendingAlarmId(String pendingAlarmId) {
        this.pendingAlarmId = pendingAlarmId;
    }

    public int getPendingAlarmType() {
        return pendingAlarmType;
    }

    public void setPendingAlarmType(int pendingAlarmType) {
        this.pendingAlarmType = pendingAlarmType;
    }

    public int getPendingAlarmDay() {
        return pendingAlarmDay;
    }

    public void setPendingAlarmDay(int pendingAlarmDay) {
        this.pendingAlarmDay = pendingAlarmDay;
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

    public static PendingAlarmModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<PendingAlarmModel> query = realm.where(PendingAlarmModel.class);
        PendingAlarmModel result = query.findFirst();
        return result;
    }

    public static ArrayList<PendingAlarmModel> getAll() {
        ArrayList<PendingAlarmModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<PendingAlarmModel> query = realm.where(PendingAlarmModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PendingAlarmModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PendingAlarmModel.class);
        realm.commitTransaction();
    }

}
