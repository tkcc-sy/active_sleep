package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class MaxRowModel extends RealmObject {
    public int maxRowLog;
    public int maxRowDailyScore;
    public int maxRowWeeklyScore;

    public int getMaxRowLog() {
        return maxRowLog;
    }

    public void setMaxRowLog(int maxRowLog) {
        this.maxRowLog = maxRowLog;
    }

    public int getMaxRowDailyScore() {
        return maxRowDailyScore;
    }

    public void setMaxRowDailyScore(int maxRowDailyScore) {
        this.maxRowDailyScore = maxRowDailyScore;
    }

    public int getMaxRowWeeklyScore() {
        return maxRowWeeklyScore;
    }

    public void setMaxRowWeeklyScore(int maxRowWeeklyScore) {
        this.maxRowWeeklyScore = maxRowWeeklyScore;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(MaxRowModel.class);
        realm.commitTransaction();
    }

    public static MaxRowModel getMaxRow() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<MaxRowModel> query = realm.where(MaxRowModel.class);
        MaxRowModel result = query.findFirst();
        if (result == null) {
            MaxRowModel maxRowModel = new MaxRowModel();
            maxRowModel.setMaxRowLog(1000);
            maxRowModel.setMaxRowDailyScore(730);
            maxRowModel.setMaxRowWeeklyScore(104);
            return maxRowModel;
        }
        return result;
    }

    public static ArrayList<MaxRowModel> getAll() {
        ArrayList<MaxRowModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<MaxRowModel> query = realm.where(MaxRowModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
