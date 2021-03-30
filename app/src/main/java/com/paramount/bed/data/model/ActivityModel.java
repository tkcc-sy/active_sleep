package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class ActivityModel extends RealmObject {
    public Boolean isHomeActive;

    public Boolean getHomeActive() {
        return isHomeActive;
    }

    public void setHomeActive(Boolean homeActive) {
        isHomeActive = homeActive;
    }

    public static void setHomeResume() {
        ActivityModel.clear();
        ActivityModel activityModel = new ActivityModel();
        activityModel.setHomeActive(true);
        activityModel.insert();
    }

    public static boolean isHomeResume() {
        if (ActivityModel.getAll().size() > 0) {
            return true;
        }
        return false;
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

    public static ActivityModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ActivityModel> query = realm.where(ActivityModel.class);
        ActivityModel result = query.findFirst();
        return result;
    }

    public static ArrayList<ActivityModel> getAll() {
        ArrayList<ActivityModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ActivityModel> query = realm.where(ActivityModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ActivityModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ActivityModel.class);
        realm.commitTransaction();
    }

}
