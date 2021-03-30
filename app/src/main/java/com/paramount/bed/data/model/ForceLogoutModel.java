package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class ForceLogoutModel extends RealmObject {
    public int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static boolean isTokenExpired() {
        ForceLogoutModel data = getFirst();
        if (data != null) {
            return true;
        }
        return false;
    }


    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static void tokenExpired() {
        clear();
        ForceLogoutModel qsAdsDailyModel = new ForceLogoutModel();
        qsAdsDailyModel.setType(1);
        qsAdsDailyModel.insert();
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static ForceLogoutModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ForceLogoutModel> query = realm.where(ForceLogoutModel.class);
        ForceLogoutModel result = query.findFirst();
        return result;
    }

    public static ArrayList<ForceLogoutModel> getAll() {
        ArrayList<ForceLogoutModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ForceLogoutModel> query = realm.where(ForceLogoutModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ForceLogoutModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ForceLogoutModel.class);
        realm.commitTransaction();
    }

}
