package com.paramount.bed.data.model;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class DailyScoreModel extends RealmObject {
    @PrimaryKey
    public String datePrimary;
    public Date date;
    public String data;
    public String lastUpdate;

    public String getDatePrimary() {
        return datePrimary;
    }

    public void setDatePrimary(String datePrimary) {
        this.datePrimary = datePrimary;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
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
        if (this.isValid()) {
            deleteFromRealm();
        }
        realm.commitTransaction();
    }

    public static ArrayList<DailyScoreModel> getAll() {
        ArrayList<DailyScoreModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DailyScoreModel> query = realm.where(DailyScoreModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }


    public static ArrayList<DailyScoreModel> getBetween(Date start_date, Date end_date) {
        ArrayList<DailyScoreModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DailyScoreModel> query = realm.where(DailyScoreModel.class).between("date", start_date, end_date);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(DailyScoreModel.class);
        realm.commitTransaction();
    }

    public static DailyScoreModel getOldest() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DailyScoreModel> query = realm.where(DailyScoreModel.class).sort("lastUpdate", Sort.ASCENDING);
        DailyScoreModel result = query.findFirst();
        return result;
    }
}
