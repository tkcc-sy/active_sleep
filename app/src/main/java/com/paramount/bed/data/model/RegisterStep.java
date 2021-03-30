package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class RegisterStep extends RealmObject {

    @PrimaryKey
    String email;
    String companyCode;
    String phoneNumber;
    String nickName;
    String birthDate;
    int gender;
    String zipCode;
    String city;
    String prefecture;
    String streetAddress;
    int height;
    int weight;
    String questionnaireResult;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrefecture() {
        return prefecture;
    }

    public void setPrefecture(String prefecture) {
        this.prefecture = prefecture;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getQuestionnaireResult() {
        return questionnaireResult;
    }

    public void setQuestionnaireResult(String questionnaireResult) {
        this.questionnaireResult = questionnaireResult;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static RegisterStep getRegisterStepbyEmail(String email) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RegisterStep> query = realm.where(RegisterStep.class).equalTo("email", email);
        RegisterStep result = query.findFirst();
        return result;
    }

    public static RegisterStep getRegisterStep() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RegisterStep> query = realm.where(RegisterStep.class);
        RegisterStep result = query.findFirst();
        return result;
    }

    public void update(String email, int page) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        switch (page) {
            case 7:
                RegisterStep.getRegisterStepbyEmail(email).setPhoneNumber(this.phoneNumber);
                break;
            case 9:
                RegisterStep.getRegisterStepbyEmail(email).setNickName(this.nickName);
                RegisterStep.getRegisterStepbyEmail(email).setBirthDate(this.birthDate);
                RegisterStep.getRegisterStepbyEmail(email).setGender(this.gender);
                break;
            case 10:
                RegisterStep.getRegisterStepbyEmail(email).setZipCode(this.zipCode);
                RegisterStep.getRegisterStepbyEmail(email).setCity(this.city);
                RegisterStep.getRegisterStepbyEmail(email).setPrefecture(this.prefecture);
                RegisterStep.getRegisterStepbyEmail(email).setStreetAddress(this.streetAddress);

                RegisterStep.getRegisterStepbyEmail(email).setWeight(this.weight);
                RegisterStep.getRegisterStepbyEmail(email).setHeight(this.height);
                break;
            default:
                RegisterStep.getRegisterStepbyEmail(email).setPhoneNumber(this.phoneNumber);
        }

        realm.copyToRealmOrUpdate(this);
        realm.commitTransaction();
    }

    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(RegisterStep.class);
        realm.commitTransaction();
    }

    public static ArrayList<RegisterStep> getAll() {
        ArrayList<RegisterStep> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<RegisterStep> query = realm.where(RegisterStep.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
