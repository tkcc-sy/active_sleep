package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class ValidationPhoneModel extends RealmObject {
    @PrimaryKey
    public String phone;
    public String activationId;
    public String token;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActivationId() {
        return activationId;
    }

    public void setActivationId(String activationId) {
        this.activationId = activationId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static ValidationPhoneModel getByPhone(String phone) {
        Realm realm = Realm.getDefaultInstance();

        ValidationPhoneModel validationPhoneModel = realm.where(ValidationPhoneModel.class).equalTo("phone", phone.trim()).findFirst();
        if (validationPhoneModel == null) {
            ValidationPhoneModel result = new ValidationPhoneModel();
            result.setPhone("");
            result.setActivationId("");
            result.setToken("");
            validationPhoneModel = result;
        }
        return validationPhoneModel;

    }

    public static ValidationPhoneModel updateByPhone(String phone, String activationId, String token) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ValidationPhoneModel> query = realm.where(ValidationPhoneModel.class).equalTo("phone", phone.trim());
        ValidationPhoneModel result = query.findFirst();
        if (result == null) {
            ValidationPhoneModel data = new ValidationPhoneModel();
            data.setPhone(phone.trim());
            data.setActivationId(activationId);
            data.setToken(token);
            data.insert();
        } else {
            realm.beginTransaction();
//            result.setPhone(phone.trim());
            result.setActivationId(activationId);
            result.setToken(token);
            realm.copyToRealmOrUpdate(result);
            realm.commitTransaction();
        }


        return result;
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

    public static ValidationPhoneModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ValidationPhoneModel> query = realm.where(ValidationPhoneModel.class);
        ValidationPhoneModel result = query.findFirst();
        return result;
    }

    public static ArrayList<ValidationPhoneModel> getAll() {
        ArrayList<ValidationPhoneModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ValidationPhoneModel> query = realm.where(ValidationPhoneModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ValidationPhoneModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ValidationPhoneModel.class);
        realm.commitTransaction();
    }

}
