package com.paramount.bed.data.model;


import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class QSDailyModel extends RealmObject {
    public int type;
    public int day;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static boolean isQSShowed(int day) {
        QSDailyModel data = getFirst();
        if (data != null) {
            if (data.getDay() == day) {
                return true;
            } else {
                clear();
                return false;
            }
        }
        return false;
    }


    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static void qsShowed(int day) {
        clear();
        QSDailyModel qsAdsDailyModel = new QSDailyModel();
        qsAdsDailyModel.setType(2);
        qsAdsDailyModel.setDay(day);
        qsAdsDailyModel.insert();
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static QSDailyModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<QSDailyModel> query = realm.where(QSDailyModel.class);
        QSDailyModel result = query.findFirst();
        return result;
    }

    public static ArrayList<QSDailyModel> getAll() {
        ArrayList<QSDailyModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<QSDailyModel> query = realm.where(QSDailyModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(QSDailyModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(QSDailyModel.class);
        realm.commitTransaction();
    }

}
