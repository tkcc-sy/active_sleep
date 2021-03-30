package com.paramount.bed.data.model;

import android.annotation.SuppressLint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingMHSModel extends RealmObject {
    @PrimaryKey
    long epoch;
    int score = -1;
    @JsonProperty("user_desired_hardness")
    int desiredHardness;
    String date;
    @JsonProperty("mattress_hardness")
    RealmList<Integer> mattressHardness;
    Boolean isSent;

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public long getEpoch() {
        return epoch;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDesiredHardness() {
        return desiredHardness;
    }

    public void setDesiredHardness(int desiredHardness) {
        this.desiredHardness = desiredHardness;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.date =  dateFormat.format(date);
    }
    public RealmList<Integer> getMattressHardness() {
        return mattressHardness;
    }

    public void setMattressHardness(RealmList<Integer> mattressHardness) {
        this.mattressHardness = mattressHardness;
    }

    public void setSent(Boolean sent) {
        isSent = sent;
    }

    public Boolean getSent() {
        return isSent;
    }

    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PendingMHSModel.class);
        realm.commitTransaction();
    }

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static ArrayList<PendingMHSModel> getAll() {
        ArrayList<PendingMHSModel> mhsModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<PendingMHSModel> query = realm.where(PendingMHSModel.class);
        mhsModels.addAll(realm.copyFromRealm(query.findAll()));
        return mhsModels;
    }

    public static ArrayList<PendingMHSModel> getUnsentMattressSetting() {
        ArrayList<PendingMHSModel> mhsModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<PendingMHSModel> query = realm.where(PendingMHSModel.class).equalTo("isSent", false);
        mhsModels.addAll(realm.copyFromRealm(query.findAll()));
        return mhsModels;
    }

    public static void updateSentSetting(PendingMHSModel pendingMHSModel) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        pendingMHSModel.setSent(true);
        realm.copyToRealmOrUpdate(pendingMHSModel);
        realm.commitTransaction();
    }

    public static void deleteSentMattressSetting() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<PendingMHSModel> query = realm.where(PendingMHSModel.class).equalTo("isSent", true);
        query.findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }
}
