package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class SleepQuestionnaireAnswerModel extends RealmObject {
    private int answer_id;
    private int question_id;
    private String content;
    private int iconIndex;

    public int getIconIndex() {
        return iconIndex;
    }

    public void setIconIndex(int iconIndex) {
        this.iconIndex = iconIndex;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public int getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(int answer_id) {
        this.answer_id = answer_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
    public static SleepQuestionnaireAnswerModel getTNC(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepQuestionnaireAnswerModel> query = realm.where(SleepQuestionnaireAnswerModel.class);
        SleepQuestionnaireAnswerModel result = query.findFirst();
        return result;
    }
    public static ArrayList<SleepQuestionnaireAnswerModel> getAll(){
        ArrayList<SleepQuestionnaireAnswerModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepQuestionnaireAnswerModel> query = realm.where(SleepQuestionnaireAnswerModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }
    public static SleepQuestionnaireAnswerModel getByKey(String key){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepQuestionnaireAnswerModel> query = realm.where(SleepQuestionnaireAnswerModel.class).equalTo("question_id",key);
        SleepQuestionnaireAnswerModel result = query.findFirst();
//        if(result == null){
//            result = new QuestionnaireAnswerModel();
//            result.setLastUpdated(0);
//            result.setKey(key);
//            result.insert();
//            result = QuestionnaireAnswerModel.getByKey(key);
//        }
        return result;
    }

    public static ArrayList<SleepQuestionnaireAnswerModel> getByQuestionID(int key){
        ArrayList<SleepQuestionnaireAnswerModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepQuestionnaireAnswerModel> query = realm.where(SleepQuestionnaireAnswerModel.class).equalTo("question_id",key);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SleepQuestionnaireAnswerModel.class);
        realm.commitTransaction();
    }
    public static void truncate(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SleepQuestionnaireAnswerModel.class);
        realm.commitTransaction();
    }
}
