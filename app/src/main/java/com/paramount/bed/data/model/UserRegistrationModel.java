package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class UserRegistrationModel extends RealmObject{
    String companyCode;

    public UserRegistrationModel() {
        this.companyCode = "";
    }

    public static String getCompanyCode() {
        UserRegistrationModel userRegistrationModel = UserRegistrationModel.get();
        return userRegistrationModel.companyCode;
    }

    public static void  setCompanyCode(String companyCode) {
        UserRegistrationModel userRegistrationModel = UserRegistrationModel.get();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        userRegistrationModel.companyCode = companyCode;
        realm.commitTransaction();
    }

    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(UserRegistrationModel.class);
        realm.commitTransaction();
    }

    public void delete(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }
    private static UserRegistrationModel get(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<UserRegistrationModel> query = realm.where(UserRegistrationModel.class);
        if(query.count() <= 0){
            UserRegistrationModel.create();
            query = realm.where(UserRegistrationModel.class);
        }

        return query.findFirst();
    }
    private  static void create(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(new UserRegistrationModel());
        realm.commitTransaction();
    }

    public static ArrayList<UserRegistrationModel> getAll() {
        ArrayList<UserRegistrationModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<UserRegistrationModel> query = realm.where(UserRegistrationModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
