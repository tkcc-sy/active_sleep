package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class SettingMatressTemplateDefaultModel extends RealmObject {
    public int id;
    public int head;
    public int shoulder;
    public int hip;
    public int thigh;
    public int calf;
    public int feet;

    public SettingMatressTemplateDefaultModel() {
    }

    public SettingMatressTemplateDefaultModel(int id, int head, int shoulder, int hip, int thigh, int calf, int feet) {
        this.id = id;
        this.head = head;
        this.shoulder = shoulder;
        this.hip = hip;
        this.thigh = thigh;
        this.calf = calf;
        this.feet = feet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getShoulder() {
        return shoulder;
    }

    public void setShoulder(int shoulder) {
        this.shoulder = shoulder;
    }

    public int getHip() {
        return hip;
    }

    public void setHip(int hip) {
        this.hip = hip;
    }

    public int getThigh() {
        return thigh;
    }

    public void setThigh(int thigh) {
        this.thigh = thigh;
    }

    public int getCalf() {
        return calf;
    }

    public void setCalf(int calf) {
        this.calf = calf;
    }

    public int getFeet() {
        return feet;
    }

    public void setFeet(int feet) {
        this.feet = feet;
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

    public static SettingMatressTemplateDefaultModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingMatressTemplateDefaultModel> query = realm.where(SettingMatressTemplateDefaultModel.class);
        SettingMatressTemplateDefaultModel result = query.findFirst();
        return result;
    }

    public static ArrayList<SettingMatressTemplateDefaultModel> getAll() {
        ArrayList<SettingMatressTemplateDefaultModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingMatressTemplateDefaultModel> query = realm.where(SettingMatressTemplateDefaultModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingMatressTemplateDefaultModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingMatressTemplateDefaultModel.class);
        realm.commitTransaction();
    }

    public static SettingMatressTemplateDefaultModel getSetting() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingMatressTemplateDefaultModel> query = realm.where(SettingMatressTemplateDefaultModel.class);
        SettingMatressTemplateDefaultModel result = query.findFirst();
        if (result == null) {
            result = InitialSetting();
        }
        return result;
    }

    public static SettingMatressTemplateDefaultModel InitialSetting() {
        SettingMatressTemplateDefaultModel.truncate();
        SettingMatressTemplateDefaultModel setting = new SettingMatressTemplateDefaultModel();
        setting.id = 0;
        setting.head = 1;
        setting.shoulder = 1;
        setting.hip = 1;
        setting.thigh = 1;
        setting.calf = 1;
        setting.feet = 1;
        return setting;
    }
}
