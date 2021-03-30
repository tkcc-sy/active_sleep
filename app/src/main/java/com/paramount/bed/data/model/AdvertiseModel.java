package com.paramount.bed.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdvertiseModel extends RealmObject {
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private String data;

    private Integer id;
    @JsonProperty("updated_date")
    private Integer updated_date;

    public Integer getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(Integer updated_date) {
        this.updated_date = updated_date;
    }

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static AdvertiseModel getAdvData(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AdvertiseModel> query = realm.where(AdvertiseModel.class);
        AdvertiseModel result = query.findFirst();
        return result;
    }

    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(AdvertiseModel.class);
        realm.commitTransaction();
    }

    public static ArrayList<AdvertiseModel> getAll() {
        ArrayList<AdvertiseModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AdvertiseModel> query = realm.where(AdvertiseModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

}
