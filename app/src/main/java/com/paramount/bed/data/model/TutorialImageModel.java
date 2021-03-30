package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;

public class TutorialImageModel extends RealmObject {

    private String tutorialId;
    private String device; // device : 6 / 6P / X
    private int imageType; //type : 0 = home / 1 = remmote
    private int imageId;
    private String imageUrl;
    private byte[] imageBytes;

    public String getTutorialId() {
        return tutorialId;
    }

    public void setTutorialId(String tutorialId) {
        this.tutorialId = tutorialId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static ArrayList<TutorialImageModel> getImageByDevice(String device) {
        ArrayList<TutorialImageModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<TutorialImageModel> query = realm.where(TutorialImageModel.class).equalTo("device", device);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static ArrayList<TutorialImageModel> getImageByDeviceType(String device, int imageType) {
        ArrayList<TutorialImageModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<TutorialImageModel> query = realm.where(TutorialImageModel.class).equalTo("device", device).equalTo("imageType", imageType);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static TutorialImageModel getById(String device, int imageId) {
        Realm realm = Realm.getDefaultInstance();

        TutorialImageModel tutorialImageModel = realm.where(TutorialImageModel.class).equalTo("device", device).equalTo("imageId", imageId).findFirst();
        return tutorialImageModel;

    }

    public static TutorialImageModel getByIdType(String device, int imageType, int imageId) {
        Realm realm = Realm.getDefaultInstance();

        TutorialImageModel tutorialImageModel = realm.where(TutorialImageModel.class).equalTo("device", device).equalTo("imageType", imageType).equalTo("imageId", imageId).findFirst();
        return tutorialImageModel;

    }
    public static ArrayList<TutorialImageModel> getsByIdType(String device, int imageType, int imageId) {
        ArrayList<TutorialImageModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();

        RealmQuery<TutorialImageModel> query = realm.where(TutorialImageModel.class).equalTo("device", device).equalTo("imageType", imageType).equalTo("imageId", imageId);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;

    }
    public static ArrayList<TutorialImageModel> getAll() {
        ArrayList<TutorialImageModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<TutorialImageModel> query = realm.where(TutorialImageModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(TutorialImageModel.class);
        realm.commitTransaction();
    }
}
