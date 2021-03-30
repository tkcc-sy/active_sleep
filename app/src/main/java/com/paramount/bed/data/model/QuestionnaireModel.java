package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class QuestionnaireModel extends RealmObject {
    private int questionnaire_id;
    private String title;
    private String description;
    private String questions;

    public QuestionnaireModel() {
    }

    public QuestionnaireModel(int questionnaire_id, String title, String description, String questions) {
        this.questionnaire_id = questionnaire_id;
        this.title = title;
        this.description = description;
        this.questions = questions;
    }

    public int getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(int questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
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
    public static QuestionnaireModel getTNC(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<QuestionnaireModel> query = realm.where(QuestionnaireModel.class);
        QuestionnaireModel result = query.findFirst();
        return result;
    }
    public static ArrayList<QuestionnaireModel> getAll(){
        ArrayList<QuestionnaireModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<QuestionnaireModel> query = realm.where(QuestionnaireModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(QuestionnaireModel.class);
        realm.commitTransaction();
    }
    public static void truncate(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(QuestionnaireModel.class);
        realm.commitTransaction();
    }
}
