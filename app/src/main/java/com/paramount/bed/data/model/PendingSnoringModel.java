package com.paramount.bed.data.model;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class PendingSnoringModel extends RealmObject{
    @PrimaryKey
    long epoch;
    String snoringResult;
    Boolean isSent;

    public long getEpoch() {
        return epoch;
    }

    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }

    public String getSnoringResult() {
        return snoringResult;
    }

    public void setSnoringResult(String snoringResult) {
        this.snoringResult = snoringResult;
    }

    public Boolean getSent() {
        return isSent;
    }

    public void setSent(Boolean sent) {
        isSent = sent;
    }

    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PendingSnoringModel.class);
        realm.commitTransaction();
    }

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static PendingSnoringModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<PendingSnoringModel> query = realm.where(PendingSnoringModel.class);
        PendingSnoringModel result = query.findFirst();
        return result;
    }

    public static ArrayList<PendingSnoringModel> getAll() {
        ArrayList<PendingSnoringModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<PendingSnoringModel> query = realm.where(PendingSnoringModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static PendingSnoringModel InitialSnoring() {
        PendingSnoringModel.clear();
        PendingSnoringModel pendingSnoringModel = new PendingSnoringModel();
        pendingSnoringModel.setEpoch(new DateTime().getMillis());
        pendingSnoringModel.setSnoringResult(null);
        pendingSnoringModel.setSent(false);
        pendingSnoringModel.insert();
        return pendingSnoringModel;
    }

    public static ArrayList<PendingSnoringModel> getUnsentSnoringResult() {
        ArrayList<PendingSnoringModel> pendingSnoringModels = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<PendingSnoringModel> query = realm.where(PendingSnoringModel.class).equalTo("isSent", false);
        pendingSnoringModels.addAll(realm.copyFromRealm(query.findAll()));
        return pendingSnoringModels;
    }

    public static void updateSentSnoringResult(PendingSnoringModel pendingSnoringModel) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        pendingSnoringModel.setSent(true);
        realm.copyToRealmOrUpdate(pendingSnoringModel);
        realm.commitTransaction();
    }

    public static void deleteSentSnoringResult() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmQuery<PendingSnoringModel> query = realm.where(PendingSnoringModel.class).equalTo("isSent", true);
        query.findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }
}
