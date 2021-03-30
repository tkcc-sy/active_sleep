package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class IsForegroundModel extends RealmObject {
    public boolean status;

    public IsForegroundModel() {
    }

    public IsForegroundModel(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

    public static IsForegroundModel getForeGroundStatus() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<IsForegroundModel> query = realm.where(IsForegroundModel.class);
        IsForegroundModel result = query.findFirst();
        if (result == null) {
            IsForegroundModel.clear();
            IsForegroundModel fm = new IsForegroundModel();
            fm.setStatus(true);
            fm.insert();
        }
        return result;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(IsForegroundModel.class);
        realm.commitTransaction();
    }

    public static ArrayList<IsForegroundModel> getAll() {
        ArrayList<IsForegroundModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<IsForegroundModel> query = realm.where(IsForegroundModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
