package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class SettingBedModel extends RealmObject {
    public int bed_fast_mode;
    public int bed_combi_locked;
    public int bed_head_locked;
    public int bed_leg_locked;
    public int bed_height_locked;

    public SettingBedModel(){

    }

    public SettingBedModel(int bed_fast_mode, int bed_combi_locked, int bed_head_locked, int bed_leg_locked, int bed_height_locked) {
        this.bed_fast_mode = bed_fast_mode;
        this.bed_combi_locked = bed_combi_locked;
        this.bed_head_locked = bed_head_locked;
        this.bed_leg_locked = bed_leg_locked;
        this.bed_height_locked = bed_height_locked;
    }

    public int getBed_fast_mode() {
        return bed_fast_mode;
    }

    public void setBed_fast_mode(int bed_fast_mode) {
        this.bed_fast_mode = bed_fast_mode;
    }

    public int getBed_combi_locked() {
        return bed_combi_locked;
    }

    public void setBed_combi_locked(int bed_combi_locked) {
        this.bed_combi_locked = bed_combi_locked;
    }

    public int getBed_head_locked() {
        return bed_head_locked;
    }

    public void setBed_head_locked(int bed_head_locked) {
        this.bed_head_locked = bed_head_locked;
    }

    public int getBed_leg_locked() {
        return bed_leg_locked;
    }

    public void setBed_leg_locked(int bed_leg_locked) {
        this.bed_leg_locked = bed_leg_locked;
    }

    public int getBed_height_locked() {
        return bed_height_locked;
    }

    public void setBed_height_locked(int bed_height_locked) {
        this.bed_height_locked = bed_height_locked;
    }

    public static void saveSetting(String key, String value) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedModel> query = realm.where(SettingBedModel.class);
        SettingBedModel setting = query.findFirst();
        SettingBedModel InsertChanges = new SettingBedModel();



        SettingModel.truncate();
        InsertChanges.insert();
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

    public static SettingBedModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedModel> query = realm.where(SettingBedModel.class);
        SettingBedModel result = query.findFirst();
        return result;
    }

    public static ArrayList<SettingBedModel> getAll() {
        ArrayList<SettingBedModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedModel> query = realm.where(SettingBedModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingBedModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SettingBedModel.class);
        realm.commitTransaction();
    }

    public static SettingBedModel getSetting() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SettingBedModel> query = realm.where(SettingBedModel.class);
        SettingBedModel result = query.findFirst();
        if (result == null) {
            result = InitialSetting();
        }
        return result;
    }

    public static SettingBedModel InitialSetting() {
        SettingBedModel.truncate();
        SettingBedModel setting = new SettingBedModel();
        setting.bed_fast_mode = 1;
        setting.bed_combi_locked = 1;
        setting.bed_head_locked = 1;
        setting.bed_leg_locked = 1;
        setting.bed_height_locked = 1;
        return setting;
    }
}
