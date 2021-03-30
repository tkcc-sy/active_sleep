package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class TutorialShowModel extends RealmObject {
    public Boolean isBedShowed = true;
    public Boolean isRemoteShowed = true;
    public Boolean isIntimeShowed = true;

    public Boolean getBedShowed() {
        return isBedShowed;
    }

    public void setBedShowed(Boolean bedShowed) {
        isBedShowed = bedShowed;
    }

    public Boolean getRemoteShowed() {
        return isRemoteShowed;
    }

    public void setRemoteShowed(Boolean remoteShowed) {
        isRemoteShowed = remoteShowed;
    }

    public Boolean getIntimeShowed() {
        return isIntimeShowed;
    }

    public void setIntimeShowed(Boolean intimeShowed) {
        isIntimeShowed = intimeShowed;
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

    public static TutorialShowModel get() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<TutorialShowModel> query = realm.where(TutorialShowModel.class);
        TutorialShowModel result = query.findFirst();
        if (result == null) {
            TutorialShowModel tutorialShowModel = new TutorialShowModel();
            tutorialShowModel.isBedShowed = true;
            tutorialShowModel.isRemoteShowed = true;
            tutorialShowModel.isIntimeShowed = true;
            result = tutorialShowModel;
        }

        return result;
    }

    public static ArrayList<TutorialShowModel> getAll() {
        ArrayList<TutorialShowModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<TutorialShowModel> query = realm.where(TutorialShowModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(TutorialShowModel.class);
        realm.commitTransaction();
    }
    public static void resetRemoteTutorial() {
        TutorialShowModel.clear();
        TutorialShowModel tutorialShowModel = new TutorialShowModel();
        tutorialShowModel.setBedShowed(false);
        tutorialShowModel.setRemoteShowed(true);
        tutorialShowModel.setIntimeShowed(true);
        tutorialShowModel.insert();
    }
}
