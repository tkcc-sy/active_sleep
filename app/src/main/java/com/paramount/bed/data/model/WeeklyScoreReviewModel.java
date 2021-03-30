package com.paramount.bed.data.model;

import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.alarms.WeeklyScoreReviewDialog;

import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class WeeklyScoreReviewModel extends RealmObject {
    @PrimaryKey
    public String datePrimary;
    public String advice;
    public Date lastUpdate;

    public String getDatePrimary() {
        return datePrimary;
    }

    public void setDatePrimary(String datePrimary) {
        this.datePrimary = datePrimary;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public String getAdvice() {
        return advice;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
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

    public static ArrayList<WeeklyScoreReviewModel> getAll() {
        ArrayList<WeeklyScoreReviewModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<WeeklyScoreReviewModel> query = realm.where(WeeklyScoreReviewModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(WeeklyScoreReviewModel.class);
        realm.commitTransaction();
    }

    public static WeeklyScoreReviewModel getByDate(String selectedDate) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<WeeklyScoreReviewModel> query = realm.where(WeeklyScoreReviewModel.class).equalTo("datePrimary", selectedDate);
        WeeklyScoreReviewModel weeklyAdvice = query.findFirst();
        return weeklyAdvice;
    }
}
