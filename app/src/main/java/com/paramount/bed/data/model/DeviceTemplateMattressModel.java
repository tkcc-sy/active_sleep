package com.paramount.bed.data.model;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class DeviceTemplateMattressModel extends RealmObject{
    private int id;
    private int head;
    private int shoulder;
    private int hip;
    private int thigh;
    private int calf;
    private int feet;
    private boolean isDefault;


    public DeviceTemplateMattressModel() {
    }

    public DeviceTemplateMattressModel(int id, int head, int shoulder, int hip, int thigh, int calf, int feet) {
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
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.head = head;
        realm.commitTransaction();
    }

    public int getShoulder() {
        return shoulder;
    }

    public void setShoulder(int shoulder) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.shoulder = shoulder;
        realm.commitTransaction();
    }

    public int getHip() {
        return hip;
    }

    public void setHip(int hip) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.hip = hip;
        realm.commitTransaction();
    }

    public int getThigh() {
        return thigh;
    }

    public void setThigh(int thigh) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.thigh = thigh;
        realm.commitTransaction();
    }

    public int getCalf() {
        return calf;
    }

    public void setCalf(int calf) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.calf = calf;
        realm.commitTransaction();
    }

    public int getFeet() {
        return feet;
    }

    public void setFeet(int feet) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.feet = feet;
        realm.commitTransaction();
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }
    public void delete(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }
    public static ArrayList<DeviceTemplateMattressModel> getAll(boolean isDefault){
        ArrayList<DeviceTemplateMattressModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateMattressModel> query = realm.where(DeviceTemplateMattressModel.class).equalTo("isDefault",isDefault);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }
    public static DeviceTemplateMattressModel getById(int id,boolean isDefault) {
        Realm realm = Realm.getDefaultInstance();

        DeviceTemplateMattressModel deviceTemplateBedModel = realm.where(DeviceTemplateMattressModel.class).equalTo("id", id).equalTo("isDefault",isDefault).findFirst();
        return deviceTemplateBedModel;

    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(DeviceTemplateMattressModel.class);
        realm.commitTransaction();
    }
    //for offline purposes
    public static List<DeviceTemplateMattressModel> getOriginalValues(){
        ArrayList<DeviceTemplateMattressModel> defaultMattressTemplate = new ArrayList<>();

        DeviceTemplateMattressModel template1 = new DeviceTemplateMattressModel();
        template1.id = 1;
        template1.head = 5;
        template1.shoulder = 5;
        template1.hip = 5;
        template1.thigh = 1;
        template1.calf = 1;
        template1.feet = 1;
        defaultMattressTemplate.add(template1);

        DeviceTemplateMattressModel template2 = new DeviceTemplateMattressModel();
        template2.id = 2;
        template2.head = 2;
        template2.shoulder = 4;
        template2.hip = 3;
        template2.thigh = 2;
        template2.calf = 1;
        template2.feet = 6;
        defaultMattressTemplate.add(template2);

        DeviceTemplateMattressModel template3 = new DeviceTemplateMattressModel();
        template3.id = 3;
        template3.head = 3;
        template3.shoulder = 3;
        template3.hip = 4;
        template3.thigh = 1;
        template3.calf = 3;
        template3.feet = 1;
        defaultMattressTemplate.add(template3);

        DeviceTemplateMattressModel template4 = new DeviceTemplateMattressModel();
        template4.id = 4;
        template4.head = 4;
        template4.shoulder = 2;
        template4.hip = 1;
        template4.thigh = 2;
        template4.calf = 1;
        template4.feet = 1;
        defaultMattressTemplate.add(template4);

        DeviceTemplateMattressModel template5 = new DeviceTemplateMattressModel();
        template5.id = 5;
        template5.head = 5;
        template5.shoulder = 1;
        template5.hip = 5;
        template5.thigh = 1;
        template5.calf = 1;
        template5.feet = 4;
        defaultMattressTemplate.add(template5);

        return defaultMattressTemplate;
    }

    public static ArrayList<DeviceTemplateMattressModel> getAll() {
        ArrayList<DeviceTemplateMattressModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DeviceTemplateMattressModel> query = realm.where(DeviceTemplateMattressModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
