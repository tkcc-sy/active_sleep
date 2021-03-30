package com.paramount.bed.data.model;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class DeviceTemplateBedModel extends RealmObject {
    private int id;
    private int head;
    private int leg;
    private int tilt;
    private int height;
    private int tilt_default;
    private int height_default;
    private boolean isDefault = false;

    public DeviceTemplateBedModel() {
    }

    public DeviceTemplateBedModel(int id, int head, int leg, int tilt, int height, int tilt_default, int height_default) {
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
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.head = head;
        realm.commitTransaction();
    }

    public int getLeg() {
        return leg;
    }

    public void setLeg(int leg) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.leg = leg;
        realm.commitTransaction();
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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
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

    public void updateHead(int newVal) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.setHead(newVal);
        realm.commitTransaction();
    }

    public void updateLeg(int newVal) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.setLeg(newVal);
        realm.commitTransaction();
    }

    public static DeviceTemplateBedModel getById(int id) {
        Realm realm = Realm.getDefaultInstance();

        DeviceTemplateBedModel deviceTemplateBedModel = realm.where(DeviceTemplateBedModel.class).equalTo("id", id).findFirst();
        return deviceTemplateBedModel;

    }

    public static DeviceTemplateBedModel getById(int id, boolean isDefault) {
        Realm realm = Realm.getDefaultInstance();

        DeviceTemplateBedModel deviceTemplateBedModel = realm.where(DeviceTemplateBedModel.class).equalTo("id", id).equalTo("isDefault", isDefault).findFirst();
        return deviceTemplateBedModel;

    }

    public static ArrayList<DeviceTemplateBedModel> getAll(boolean isDefault) {
        ArrayList<DeviceTemplateBedModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateBedModel> query = realm.where(DeviceTemplateBedModel.class).equalTo("isDefault", isDefault);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(DeviceTemplateBedModel.class);
        realm.commitTransaction();
    }

    //for offline purposes
    public static List<DeviceTemplateBedModel> getOriginalValues() {
        ArrayList<DeviceTemplateBedModel> defaultBedTemplate = new ArrayList<>();

        DeviceTemplateBedModel template1 = new DeviceTemplateBedModel();
        template1.id = 1;
        template1.head = 70;
        template1.leg = 5;
        template1.tilt = 255;
        template1.height = 30;
        template1.tilt_default = 255;
        template1.height_default = 255;
        defaultBedTemplate.add(template1);

        DeviceTemplateBedModel template2 = new DeviceTemplateBedModel();
        template1.id = 2;
        template1.head = 6;
        template1.leg = 6;
        template1.tilt = 255;
        template1.height = 30;
        template1.tilt_default = 255;
        template1.height_default = 255;
        defaultBedTemplate.add(template2);

        DeviceTemplateBedModel template3 = new DeviceTemplateBedModel();
        template3.id = 3;
        template3.head = 8;
        template3.leg = 8;
        template3.tilt = 255;
        template3.height = 30;
        template3.tilt_default = 255;
        template3.height_default = 255;
        defaultBedTemplate.add(template3);

        DeviceTemplateBedModel template4 = new DeviceTemplateBedModel();
        template4.id = 4;
        template4.head = 10;
        template4.leg = 10;
        template4.tilt = 255;
        template4.height = 30;
        template4.tilt_default = 255;
        template4.height_default = 255;
        defaultBedTemplate.add(template4);

        DeviceTemplateBedModel template5 = new DeviceTemplateBedModel();
        template5.id = 5;
        template5.head = 12;
        template5.leg = 12;
        template5.tilt = 255;
        template5.height = 30;
        template5.tilt_default = 255;
        template5.height_default = 255;
        defaultBedTemplate.add(template5);

        DeviceTemplateBedModel template6 = new DeviceTemplateBedModel();
        template6.id = 6;
        template6.head = 14;
        template6.leg = 14;
        template6.tilt = 255;
        template6.height = 30;
        template6.tilt_default = 255;
        template6.height_default = 255;
        defaultBedTemplate.add(template6);

        DeviceTemplateBedModel template7 = new DeviceTemplateBedModel();
        template7.id = 7;
        template7.head = 21;
        template7.leg = 9;
        template7.tilt = 255;
        template7.height = 30;
        template7.tilt_default = 255;
        template7.height_default = 255;
        defaultBedTemplate.add(template7);

        DeviceTemplateBedModel template8 = new DeviceTemplateBedModel();
        template8.id = 8;
        template8.head = 22;
        template8.leg = 12;
        template8.tilt = 255;
        template8.height = 30;
        template8.tilt_default = 255;
        template8.height_default = 255;
        defaultBedTemplate.add(template8);

        DeviceTemplateBedModel template9 = new DeviceTemplateBedModel();
        template9.id = 9;
        template9.head = 20;
        template9.leg = 5;
        template9.tilt = 255;
        template9.height = 30;
        template9.tilt_default = 255;
        template9.height_default = 255;
        defaultBedTemplate.add(template9);

        return defaultBedTemplate;
    }

    public static ArrayList<DeviceTemplateBedModel> getAll() {
        ArrayList<DeviceTemplateBedModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateBedModel> query = realm.where(DeviceTemplateBedModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
