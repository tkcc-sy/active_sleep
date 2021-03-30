package com.paramount.bed.data.model;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;

public class SleepResetModel  extends RealmObject {
    private Date startDate;
    private Date endDate;
    private Date backgroundDate;

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getBackgroundDate() {
        return backgroundDate;
    }

    public void setBackgroundDate(Date backgroundDate) {
        this.backgroundDate = backgroundDate;
    }

    public void updateStartDate(Date startDate) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setStartDate(startDate);
        realm.commitTransaction();
    }

    public void updateEndDate(Date endDate) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setEndDate(endDate);
        realm.commitTransaction();
    }

    public void updateBackgroundDate(Date backgroundDate) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setBackgroundDate(backgroundDate);
        realm.commitTransaction();
    }

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static SleepResetModel create(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        SleepResetModel result = realm.createObject(SleepResetModel.class);
        realm.commitTransaction();
        return result;
    }

    public void delete(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SleepResetModel.class);
        realm.commitTransaction();
    }
}
