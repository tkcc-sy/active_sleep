package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class SettingBedTemplateDefaultModel extends RealmObject {
    public int id;
    public int head;
    public int leg;
    public int tilt;
    public int height;
    public int tilt_default;
    public int height_default;

    public SettingBedTemplateDefaultModel() {
    }

    public SettingBedTemplateDefaultModel(int id, int head, int leg, int tilt, int height, int tilt_default, int height_default) {
        this.id = id;
        this.head = head;
        this.leg = leg;
        this.tilt = tilt;
        this.height = height;
        this.tilt_default = tilt_default;
        this.height_default = height_default;
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

    public int getTilt_default() {
        return tilt_default;
    }

    public void setTilt_default(int tilt_default) {
        this.tilt_default = tilt_default;
    }

    public int getHeight_default() {
        return height_default;
    }

    public void setHeight_default(int height_default) {
        this.height_default = height_default;
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

    public static SettingBedTemplateDefaultModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedTemplateDefaultModel> query = realm.where(SettingBedTemplateDefaultModel.class);
        SettingBedTemplateDefaultModel result = query.findFirst();
        return result;
    }

    public static ArrayList<SettingBedTemplateDefaultModel> getAll() {
        ArrayList<SettingBedTemplateDefaultModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedTemplateDefaultModel> query = realm.where(SettingBedTemplateDefaultModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingBedTemplateDefaultModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingBedTemplateDefaultModel.class);
        realm.commitTransaction();
    }

    public static SettingBedTemplateDefaultModel getSetting() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedTemplateDefaultModel> query = realm.where(SettingBedTemplateDefaultModel.class);
        SettingBedTemplateDefaultModel result = query.findFirst();
        if (result == null) {
            result = InitialSetting();
        }
        return result;
    }

    public static SettingBedTemplateDefaultModel InitialSetting() {
        SettingBedTemplateDefaultModel.truncate();
        SettingBedTemplateDefaultModel setting = new SettingBedTemplateDefaultModel();
        setting.id = 0;
        setting.head = 10;
        setting.leg = 10;
        setting.tilt = 10;
        setting.height = 10;
        setting.tilt_default = 255;
        setting.height_default = 30;
        return setting;
    }
}
