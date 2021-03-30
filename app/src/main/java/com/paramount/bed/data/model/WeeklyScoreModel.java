package com.paramount.bed.data.model;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class WeeklyScoreModel extends RealmObject {
    @PrimaryKey
    public String datePrimary;
    public Date start_date;
    public Date end_date;
    public String data;
    public String lastUpdate;

    public String getDatePrimary() {
        return datePrimary;
    }

    public void setDatePrimary(String datePrimary) {
        this.datePrimary = datePrimary;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
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

    public static ArrayList<WeeklyScoreModel> getAll() {
        ArrayList<WeeklyScoreModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<WeeklyScoreModel> query = realm.where(WeeklyScoreModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }


    public static ArrayList<WeeklyScoreModel> getBetween(Date start_date, Date end_date) {
        ArrayList<WeeklyScoreModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<WeeklyScoreModel> query = realm.where(WeeklyScoreModel.class).
                greaterThanOrEqualTo("start_date", start_date).
                lessThanOrEqualTo("end_date", end_date);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(DailyScoreModel.class);
        realm.commitTransaction();
    }

    public static WeeklyScoreModel getOldest() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<WeeklyScoreModel> query = realm.where(WeeklyScoreModel.class).sort("lastUpdate", Sort.ASCENDING);
        WeeklyScoreModel result = query.findFirst();
        return result;
    }
}
