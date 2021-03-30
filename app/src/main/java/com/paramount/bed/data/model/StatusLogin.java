package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class StatusLogin extends RealmObject {
    public boolean statusLogin;

    public static StatusLogin getUserLogin() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<StatusLogin> query = realm.where(StatusLogin.class);
        StatusLogin result = query.findFirst();
        return result;
    }

    public static void init(){
        StatusLogin.clear();
        StatusLogin defaultLogin = new StatusLogin();
        defaultLogin.insert();
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(StatusLogin.class);
        realm.commitTransaction();
    }

    public static ArrayList<StatusLogin> getAll() {
        ArrayList<StatusLogin> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<StatusLogin> query = realm.where(StatusLogin.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
