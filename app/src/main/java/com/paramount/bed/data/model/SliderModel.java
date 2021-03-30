package com.paramount.bed.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

public class SliderModel extends RealmObject {

    @JsonProperty("created_date")
    private String createdDate;
    @PrimaryKey
    private int id;

    @JsonProperty("updated_date")
    private String updatedDate;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("slider_id")
    private int sliderId;


    @JsonProperty("text_color")
    private String textColor;


    private String caption;

    private byte[] imageBytes;
    public Boolean isDownloaded;

    public byte[] getImageBytes() {
        return imageBytes;
    }

    public Boolean getDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(Boolean downloaded) {
        isDownloaded = downloaded;
    }

    public void setImageBytes(byte[] imageBytes) {
        this.imageBytes = imageBytes;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getSliderId() {
        return sliderId;
    }

    public void setSliderId(int sliderId) {
        this.sliderId = sliderId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }


    public static SliderModel updateBySliderId(SliderModel sliderModel) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SliderModel> query = realm.where(SliderModel.class).equalTo("id", sliderModel.id);
        SliderModel result = query.findFirst();
        if (result == null) {
            sliderModel.insert();
        } else {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(sliderModel);
            realm.commitTransaction();
        }


        return result;
    }

    public static ArrayList<SliderModel> getAll() {
        ArrayList<SliderModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SliderModel> query = realm.where(SliderModel.class).sort("id", Sort.ASCENDING);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SliderModel.class);
        realm.commitTransaction();
    }
}
