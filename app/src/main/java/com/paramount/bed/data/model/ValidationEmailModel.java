package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class ValidationEmailModel extends RealmObject {
    @PrimaryKey
    public String email;
    public String activationId;
    public String token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public static ValidationEmailModel getByEmail(String email) {
        Realm realm = Realm.getDefaultInstance();

        ValidationEmailModel validationEmailModel = realm.where(ValidationEmailModel.class).equalTo("email", email.trim()).findFirst();
        if (validationEmailModel == null) {
            ValidationEmailModel result = new ValidationEmailModel();
            result.setEmail("");
            result.setActivationId("");
            result.setToken("");
            validationEmailModel = result;
        }
        return validationEmailModel;

    }

    public static ValidationEmailModel updateByEmail(String email, String activationId, String token) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ValidationEmailModel> query = realm.where(ValidationEmailModel.class).equalTo("email", email.trim());
        ValidationEmailModel result = query.findFirst();
        if (result == null) {
            ValidationEmailModel data = new ValidationEmailModel();
            data.setEmail(email.trim());
            data.setActivationId(activationId);
            data.setToken(token);
            data.insert();
        } else {
            realm.beginTransaction();
//            result.setEmail(email.trim());
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

    public static ValidationEmailModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ValidationEmailModel> query = realm.where(ValidationEmailModel.class);
        ValidationEmailModel result = query.findFirst();
        return result;
    }

    public static ArrayList<ValidationEmailModel> getAll() {
        ArrayList<ValidationEmailModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ValidationEmailModel> query = realm.where(ValidationEmailModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ValidationEmailModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ValidationEmailModel.class);
        realm.commitTransaction();
    }

}
