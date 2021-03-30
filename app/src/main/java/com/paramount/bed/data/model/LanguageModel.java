package com.paramount.bed.data.model;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LanguageModel extends RealmObject {
    String tag, content, languageCode;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTag() {
        return tag != null && !tag.isEmpty() ? tag.trim() : tag;
    }

    public void setTag(String tag) {
        this.tag = tag != null && !tag.isEmpty() ? tag.trim() : tag;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static LanguageModel getByTag(String tag, String languageCode) {
        Realm realm = Realm.getDefaultInstance();

        LanguageModel languageModel = realm.where(LanguageModel.class).equalTo("languageCode", languageCode).equalTo("tag", tag).findFirst();
        return languageModel;

    }

    public static ArrayList<LanguageModel> getAll() {
        ArrayList<LanguageModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<LanguageModel> query = realm.where(LanguageModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear(String languageCode) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(LanguageModel.class).equalTo("languageCode", languageCode).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(LanguageModel.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static void batchInsert(ArrayList<LanguageModel> data) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(data);
        realm.commitTransaction();
    }

}
