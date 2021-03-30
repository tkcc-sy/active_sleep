package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class RegisteringModel extends RealmObject {
    @PrimaryKey
    public int idData;
    public String phoneNumber;
    public String nickName;
    public String birthDay;
    public int gender;
    public String zipCode;
    public String address;
    public int height;
    public int weight;
    public int desiredHardness = 3;

    public static void updatePhoneNumber(String phoneNumber) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RegisteringModel registeringModel = RegisteringModel.getProfile();
        registeringModel.setPhoneNumber(phoneNumber);
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }

    public static void updateNickName(String nickName) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RegisteringModel registeringModel = RegisteringModel.getProfile();
        registeringModel.setNickName(nickName);
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }

    public static void updateBirthDay(String birthDay) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RegisteringModel registeringModel = RegisteringModel.getProfile();
        registeringModel.setBirthDay(birthDay);
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }

    public static void updateGender(int gender) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RegisteringModel registeringModel = RegisteringModel.getProfile();
        registeringModel.setGender(gender);
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }

    public static void updateZipCode(String zipCode) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RegisteringModel registeringModel = RegisteringModel.getProfile();
        registeringModel.setZipCode(zipCode);
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }

    public static void updateAddress(String address) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RegisteringModel registeringModel = RegisteringModel.getProfile();
        registeringModel.setAddress(address);
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }

    public static void updateHeight(int height) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RegisteringModel registeringModel = RegisteringModel.getProfile();
        registeringModel.setHeight(height);
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }

    public static void updateWeight(int weight) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RegisteringModel registeringModel = RegisteringModel.getProfile();
        registeringModel.setWeight(weight);
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }
    public static void updateDesiredHardness(int desiredHardness) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RegisteringModel registeringModel = RegisteringModel.getProfile();
        registeringModel.setDesiredHardness(desiredHardness);
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }

    public int getIdData() {
        return idData;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public int getGender() {
        return gender;
    }

    public String getZipCode() {
        return zipCode;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public void setIdData(int idData) {
        this.idData = idData;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDesiredHardness() {
        return desiredHardness;
    }

    public void setDesiredHardness(int desiredHardness) {
        this.desiredHardness = desiredHardness;
    }

    public static void update(RegisteringModel registeringModel) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(registeringModel);
        realm.commitTransaction();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(RegisteringModel.class);
        realm.commitTransaction();
    }

    public static RegisteringModel getProfile() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RegisteringModel> query = realm.where(RegisteringModel.class);
        RegisteringModel result = query.findFirst();
        if (result == null) {
            RegisteringModel registeringModel = new RegisteringModel();
            registeringModel.setIdData(1);
            registeringModel.setPhoneNumber("");
            registeringModel.setNickName("");
            registeringModel.setBirthDay("");
            registeringModel.setGender(0);
            registeringModel.setZipCode("");
            registeringModel.setAddress("");
            registeringModel.setHeight(0);
            registeringModel.setWeight(0);
            registeringModel.setDesiredHardness(3);
            return registeringModel;
        }
        return result;
    }

    public static ArrayList<RegisteringModel> getAll() {
        ArrayList<RegisteringModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RegisteringModel> query = realm.where(RegisteringModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
