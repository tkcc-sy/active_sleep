package com.paramount.bed.data.model;


import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class SleepQuestionnaireQuestionModel extends RealmObject {
    private int question_id;
    private int questionnaire_id;
    private Boolean is_multiple_choice;
    private String content;
    private String answers;

    public int getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(int questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public Boolean getIs_multiple_choice() {
        return is_multiple_choice==null?false:is_multiple_choice;
    }

    public void setIs_multiple_choice(Boolean is_multiple_choice) {
        this.is_multiple_choice = is_multiple_choice==null?false:is_multiple_choice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
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
    public static SleepQuestionnaireQuestionModel getTNC(){
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepQuestionnaireQuestionModel> query = realm.where(SleepQuestionnaireQuestionModel.class);
        SleepQuestionnaireQuestionModel result = query.findFirst();
        return result;
    }
    public static ArrayList<SleepQuestionnaireQuestionModel> getAll(){
        ArrayList<SleepQuestionnaireQuestionModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepQuestionnaireQuestionModel> query = realm.where(SleepQuestionnaireQuestionModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SleepQuestionnaireQuestionModel.class);
        realm.commitTransaction();
    }
    public static void truncate(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SleepQuestionnaireQuestionModel.class);
        realm.commitTransaction();
    }
}
