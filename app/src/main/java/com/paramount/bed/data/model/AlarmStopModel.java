package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class AlarmStopModel extends RealmObject {
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

    public static boolean isStopPressed(int day) {
        AlarmStopModel data = getFirst();
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

    public static void stopStatusRegister(int day) {
        clear();
        AlarmStopModel qsAdsDailyModel = new AlarmStopModel();
        qsAdsDailyModel.setType(1);
        qsAdsDailyModel.setDay(day);
        qsAdsDailyModel.insert();
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static AlarmStopModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AlarmStopModel> query = realm.where(AlarmStopModel.class);
        AlarmStopModel result = query.findFirst();
        return result;
    }

    public static ArrayList<AlarmStopModel> getAll() {
        ArrayList<AlarmStopModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AlarmStopModel> query = realm.where(AlarmStopModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(AlarmStopModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(AlarmStopModel.class);
        realm.commitTransaction();
    }

}
