package com.paramount.bed.data.model;


import com.paramount.bed.data.provider.LanguageProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class FAQModel extends RealmObject {
    public String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public static FAQModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<FAQModel> query = realm.where(FAQModel.class);
        FAQModel result = query.findFirst();
        return result;
    }

    public static ArrayList<FAQModel> getAll() {
        ArrayList<FAQModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<FAQModel> query = realm.where(FAQModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FAQModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FAQModel.class);
        realm.commitTransaction();
    }

}
