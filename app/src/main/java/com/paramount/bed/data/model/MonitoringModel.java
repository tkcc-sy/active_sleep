package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class MonitoringModel extends RealmObject {
    @PrimaryKey
    public int id;
    public String nick_name;
    public int status;
    public MonitoringModel() {
    }

    public MonitoringModel(int id, String nick_name, int status) {
        this.id = id;
        this.nick_name = nick_name;
        this.status = status;
    }

    public MonitoringModel(int id, String nick_name) {
        this.id = id;
        this.nick_name = nick_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(this);
        realm.commitTransaction();
    }
    public void delete(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static ArrayList<MonitoringModel> getAll(){
        ArrayList<MonitoringModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<MonitoringModel> query = realm.where(MonitoringModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(MonitoringModel.class);
        realm.commitTransaction();
    }

    public void update() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();


        realm.insertOrUpdate(this);
        realm.commitTransaction();
    }

    public static void truncate(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(QuestionnaireQuestionModel.class);
        realm.commitTransaction();
    }
    public static MonitoringModel getMonitoringModel() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<MonitoringModel> query = realm.where(MonitoringModel.class);
        MonitoringModel result = query.findFirst();
        return result;
    }

}
