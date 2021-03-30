package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class SenderBirdieModel extends RealmObject {
    @PrimaryKey
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static SenderBirdieModel getByName(String Name) {
        Realm realm = Realm.getDefaultInstance();

        SenderBirdieModel validationNameModel = realm.where(SenderBirdieModel.class).equalTo("name", Name.trim()).findFirst();
        if (validationNameModel == null) {
            SenderBirdieModel result = new SenderBirdieModel();
            result.setName("");
            validationNameModel = result;
        }
        return validationNameModel;

    }

    public static void deleteByName(String Name) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(SenderBirdieModel.class).equalTo("name", Name).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static SenderBirdieModel updateByName(String Name) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SenderBirdieModel> query = realm.where(SenderBirdieModel.class).equalTo("name", Name.trim());
        SenderBirdieModel result = query.findFirst();
        if (result == null) {
            SenderBirdieModel data = new SenderBirdieModel();
            data.setName(Name.trim());
            data.insert();
        } else {
//            realm.beginTransaction();
//            result.setName(Name.trim());
//            realm.copyToRealmOrUpdate(result);
//            realm.commitTransaction();
        }


        return result;
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
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static SenderBirdieModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SenderBirdieModel> query = realm.where(SenderBirdieModel.class);
        SenderBirdieModel result = query.findFirst();
        return result;
    }

    public static ArrayList<SenderBirdieModel> getAll() {
        ArrayList<SenderBirdieModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SenderBirdieModel> query = realm.where(SenderBirdieModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SenderBirdieModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SenderBirdieModel.class);
        realm.commitTransaction();
    }

}
