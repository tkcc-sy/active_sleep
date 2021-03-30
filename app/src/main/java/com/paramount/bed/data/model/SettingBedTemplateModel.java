package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class SettingBedTemplateModel extends RealmObject {
    public int id;
    public int head;
    public int leg;
    public int tilt;
    public int height;

    public SettingBedTemplateModel() {
    }

    public SettingBedTemplateModel(int id, int head, int leg, int tilt, int height) {
        this.id = id;
        this.head = head;
        this.leg = leg;
        this.tilt = tilt;
        this.height = height;
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

    public int getLeg() {
        return leg;
    }

    public void setLeg(int leg) {
        this.leg = leg;
    }

    public int getTilt() {
        return tilt;
    }

    public void setTilt(int tilt) {
        this.tilt = tilt;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
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

    public static SettingBedTemplateModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedTemplateModel> query = realm.where(SettingBedTemplateModel.class);
        SettingBedTemplateModel result = query.findFirst();
        return result;
    }

    public static ArrayList<SettingBedTemplateModel> getAll() {
        ArrayList<SettingBedTemplateModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedTemplateModel> query = realm.where(SettingBedTemplateModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingBedTemplateModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingBedTemplateModel.class);
        realm.commitTransaction();
    }

    public static SettingBedTemplateModel getSetting() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedTemplateModel> query = realm.where(SettingBedTemplateModel.class);
        SettingBedTemplateModel result = query.findFirst();
        if (result == null) {
            result = InitialSetting();
        }
        return result;
    }

    public static SettingBedTemplateModel InitialSetting() {
        SettingBedTemplateModel.truncate();
        SettingBedTemplateModel setting = new SettingBedTemplateModel();
        setting.id = 0;
        setting.head = 10;
        setting.leg = 10;
        setting.tilt = 10;
        setting.height = 10;
        return setting;
    }
}
