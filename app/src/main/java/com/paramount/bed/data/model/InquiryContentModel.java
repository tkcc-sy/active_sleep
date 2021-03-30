package com.paramount.bed.data.model;


import com.paramount.bed.data.provider.LanguageProvider;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class InquiryContentModel extends RealmObject {
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

    public static InquiryContentModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<InquiryContentModel> query = realm.where(InquiryContentModel.class);
        InquiryContentModel result = query.findFirst();
        if (result == null) {
            InquiryContentModel inquiryContentModel = new InquiryContentModel();
            inquiryContentModel.setContent(LanguageProvider.getLanguage("UI000780C002"));
            result = inquiryContentModel;
        }
        return result;
    }

    public static ArrayList<InquiryContentModel> getAll() {
        ArrayList<InquiryContentModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<InquiryContentModel> query = realm.where(InquiryContentModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(InquiryContentModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(InquiryContentModel.class);
        realm.commitTransaction();
    }

}
