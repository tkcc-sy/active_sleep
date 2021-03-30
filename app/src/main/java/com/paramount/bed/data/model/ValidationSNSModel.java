package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class ValidationSNSModel extends RealmObject {
    public Boolean isSNS;

    public Boolean getIsSNS() {
        return isSNS;
    }

    public void setIsSNS(Boolean SNS) {
        isSNS = SNS;
    }

    public static Boolean isSNS() {
        Realm realm = Realm.getDefaultInstance();

        ValidationSNSModel validationSNSModel = realm.where(ValidationSNSModel.class).findFirst();
        if (validationSNSModel == null) {
            ValidationSNSModel result = new ValidationSNSModel();
            result.setIsSNS(false);
            validationSNSModel = result;
        }
        return validationSNSModel.getIsSNS();

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

    public static ValidationSNSModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ValidationSNSModel> query = realm.where(ValidationSNSModel.class);
        ValidationSNSModel result = query.findFirst();
        return result;
    }

    public static ArrayList<ValidationSNSModel> getAll() {
        ArrayList<ValidationSNSModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ValidationSNSModel> query = realm.where(ValidationSNSModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ValidationSNSModel.class);
        realm.commitTransaction();
    }
}
