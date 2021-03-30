package com.paramount.bed.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionGeneralModel extends RealmObject {
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

    public Integer getUpdated_date() {
        return updated_date;
    }

    public void setUpdated_date(Integer updated_date) {
        this.updated_date = updated_date;
    }

    private Integer id;
    @JsonProperty("updated_date")
    private Integer updated_date;

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static QuestionGeneralModel getQuestionData(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<QuestionGeneralModel> query = realm.where(QuestionGeneralModel.class);
        QuestionGeneralModel result = query.findFirst();
        return result;
    }

    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(QuestionGeneralModel.class);
        realm.commitTransaction();
    }

    public static ArrayList<QuestionGeneralModel> getAll() {
        ArrayList<QuestionGeneralModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<QuestionGeneralModel> query = realm.where(QuestionGeneralModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
