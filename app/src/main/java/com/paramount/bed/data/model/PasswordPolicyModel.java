package com.paramount.bed.data.model;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class PasswordPolicyModel extends RealmObject {
    public String minLength;
    public String maxLength;
    public String allowedSymbols;

    public PasswordPolicyModel() {
    }

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getAllowedSymbols() {
        return allowedSymbols;
    }

    public void setAllowedSymbols(String allowedSymbols) {
        this.allowedSymbols = allowedSymbols;
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

    public static PasswordPolicyModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<PasswordPolicyModel> query = realm.where(PasswordPolicyModel.class);
        PasswordPolicyModel result = query.findFirst();
        if (result == null) {
            PasswordPolicyModel.clear();
            PasswordPolicyModel passwordPolicyModel = new PasswordPolicyModel();
            passwordPolicyModel.setMaxLength("16");
            passwordPolicyModel.setMinLength("8");
            passwordPolicyModel.setAllowedSymbols("@、#、?、-、_、*、$");
            result = passwordPolicyModel;
        }
        return result;
    }

    public static ArrayList<PasswordPolicyModel> getAll() {
        ArrayList<PasswordPolicyModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<PasswordPolicyModel> query = realm.where(PasswordPolicyModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PasswordPolicyModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(PasswordPolicyModel.class);
        realm.commitTransaction();
    }

}
