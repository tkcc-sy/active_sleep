package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class ContentFaqModel extends RealmObject {
    private String data;

    public String getData()
    {
        return data;
    }

    public void setData (String data)
    {
        this.data = data;
    }

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }
    public void delete(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }
    public static ContentFaqModel getFaq(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ContentFaqModel> query = realm.where(ContentFaqModel.class);
        ContentFaqModel result = query.findFirst();
        return result;
    }
    public static ArrayList<ContentFaqModel> getAll(){
        ArrayList<ContentFaqModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ContentFaqModel> query = realm.where(ContentFaqModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ContentFaqModel.class);
        realm.commitTransaction();
    }
}
