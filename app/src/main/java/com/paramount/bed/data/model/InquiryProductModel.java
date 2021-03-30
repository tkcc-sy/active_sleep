package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

/**
 * Created by github/fiyyanputra on 11/3/2018.
 */

public class InquiryProductModel extends RealmObject{
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
    public static InquiryProductModel getProduct(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<InquiryProductModel> query = realm.where(InquiryProductModel.class);
        InquiryProductModel result = query.findFirst();
        return result;
    }
    public static ArrayList<InquiryProductModel> getAll(){
        ArrayList<InquiryProductModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<InquiryProductModel> query = realm.where(InquiryProductModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(InquiryProductModel.class);
        realm.commitTransaction();
    }
}
