package com.paramount.bed.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class DashboardModel extends RealmObject {

    @PrimaryKey
    private int type;
    @JsonProperty("html")
    private String content;
    @JsonProperty("last_updated")
    private int last_update;

    public String getContent() {
        return content;
    }

    public void setContent(String Content) {
        this.content = Content;
    }

    public int getType ()
    {
        return type;
    }

    public void setType (int type)
    {
        this.type = type;
    }

    public int getLastUpdate ()
    {
        return last_update;
    }

    public void setLastUpdate (int last_update)
    {
        this.last_update = last_update;
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
    public static DashboardModel getByKey(int type){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DashboardModel> query = realm.where(DashboardModel.class).equalTo("type",type);
        DashboardModel result = query.findFirst();

        return result;
    }

    public static DashboardModel updateByKey(int type,String content){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DashboardModel> query = realm.where(DashboardModel.class).equalTo("type",type);
        DashboardModel result = query.findFirst();
        if(result == null){
            DashboardModel data = new DashboardModel();
            data.setType(type);
            data.setContent(content);
            data.insert();
        }else{
            realm.beginTransaction();
            result.setContent(content);
            realm.copyToRealmOrUpdate(result);
            realm.commitTransaction();
        }


        return result;
    }
    public static ArrayList<DashboardModel> getAll(){
        ArrayList<DashboardModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<DashboardModel> query = realm.where(DashboardModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(DashboardModel.class);
        realm.commitTransaction();
    }
}
