package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class ContentTNCModel extends RealmObject {
    public String data;

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
    public static ContentTNCModel getTNC(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ContentTNCModel> query = realm.where(ContentTNCModel.class);
        ContentTNCModel result = query.findFirst();
        return result;
    }
    public static ArrayList<ContentTNCModel> getAll(){
        ArrayList<ContentTNCModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ContentTNCModel> query = realm.where(ContentTNCModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ContentTNCModel.class);
        realm.commitTransaction();
    }
}
