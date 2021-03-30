package com.paramount.bed.data.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class ContentVersionModel extends RealmObject {

    @JsonProperty("last_updated")
    private int lastUpdated;
    private String key;

    public int getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(int lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public static ContentVersionModel getByKey(String key) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ContentVersionModel> query = realm.where(ContentVersionModel.class).equalTo("key", key);
        ContentVersionModel result = query.findFirst();
        if (result == null) {
            result = new ContentVersionModel();
            result.setLastUpdated(getLastUpdateConstant(key));
            result.setKey(key);
            result.insert();
            result = ContentVersionModel.getByKey(key);
        }
        return result;
    }

    public static ArrayList<ContentVersionModel> getAll() {
        ArrayList<ContentVersionModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ContentVersionModel> query = realm.where(ContentVersionModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public static void insertAll(ContentVersionModel[] contentVersionModels) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (ContentVersionModel contentVersionModel : contentVersionModels
        ) {
            realm.insert(contentVersionModel);
        }
        realm.commitTransaction();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ContentVersionModel.class);
        realm.commitTransaction();
    }

    public static int getLastUpdateConstant(String keyString) {
        switch (keyString) {
            case "tutorial":
                return 1549186535;
            case "realtime_bed":
                return 1534128489;
            case "ns_constants":
                return 1534128488;
            case "calendar":
                return 1534128490;
            case "home":
                return 1549256155;
            case "inquiry":
                return 1549116758;
            case "questionnaire":
                return 1549116809;
            case "faq":
                return 1549247653;
            case "term_and_condition":
                return 1549116742;
            case "device_template":
                return 1547182831;
            case "home_weekly":
                return 1549256155;
            case "detail_weekly":
                return 1549256155;
            case "image_slider":
                return 1549545160;
            case "detail":
                return 1549256155;
            case "en-US":
                return 1549519264;
            case "id-ID":
                return 1547867700;
            case "jp-JP":
                return 1549519265;
            case "appli-validation":
                return 1545898860;
            default:
                return 0;
        }
    }
}
