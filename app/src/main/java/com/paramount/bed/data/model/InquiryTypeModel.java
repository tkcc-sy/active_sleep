package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

/**
 * Created by Alham Wa on 27/09/18
 */
public class InquiryTypeModel extends RealmObject {
    private Integer id;
    private String label;
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
    public static InquiryTypeModel getInquiry(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<InquiryTypeModel> query = realm.where(InquiryTypeModel.class);
        InquiryTypeModel result = query.findFirst();
        return result;
    }
    public static ArrayList<InquiryTypeModel> getAll(){
        ArrayList<InquiryTypeModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<InquiryTypeModel> query = realm.where(InquiryTypeModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(InquiryTypeModel.class);
        realm.commitTransaction();
    }
}
