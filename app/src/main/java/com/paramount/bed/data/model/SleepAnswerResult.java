package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class SleepAnswerResult extends RealmObject {
    String idResult;
    int questionId;
    int answerId;
    String answer;
    int iconIndex;

    public int getIconIndex() {
        return iconIndex;
    }

    public void setIconIndex(int iconIndex) {
        this.iconIndex = iconIndex;
    }

    public String getIdResult() {
        return idResult;
    }

    public void setIdResult(String idResult) {
        this.idResult = idResult;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public static SleepAnswerResult getDataAnswer() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepAnswerResult> query = realm.where(SleepAnswerResult.class);
        SleepAnswerResult result = query.findFirst();
        return result;
    }

    public static SleepAnswerResult getDataAnswerbyId(int id) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepAnswerResult> query = realm.where(SleepAnswerResult.class).equalTo("questionId", id);
        SleepAnswerResult result = query.findFirst();
        return result;

    }

    public static ArrayList<SleepAnswerResult> getAllById(int id) {
        ArrayList<SleepAnswerResult> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepAnswerResult> query = realm.where(SleepAnswerResult.class).equalTo("questionId", id);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static ArrayList<SleepAnswerResult> getAnswer(int questionId,int answerId) {
        ArrayList<SleepAnswerResult> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepAnswerResult> query = realm.where(SleepAnswerResult.class).equalTo("questionId", questionId).and().equalTo("answerId",answerId);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear(int questionId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(SleepAnswerResult.class).equalTo("questionId", questionId).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static void clearByAnswerId(int answerId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(SleepAnswerResult.class).equalTo("answerId", answerId).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static ArrayList<SleepAnswerResult> getAll() {
        ArrayList<SleepAnswerResult> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<SleepAnswerResult> query = realm.where(SleepAnswerResult.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(SleepAnswerResult.class);
        realm.commitTransaction();
    }
}
