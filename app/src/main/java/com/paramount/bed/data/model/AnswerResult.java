package com.paramount.bed.data.model;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class AnswerResult extends RealmObject {
    String idResult;
    int questionId;
    int answerId;
    String answer;

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

    public static AnswerResult getDataAnswer() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AnswerResult> query = realm.where(AnswerResult.class);
        AnswerResult result = query.findFirst();
        return result;
    }

    public static AnswerResult getDataAnswerbyId(int id) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AnswerResult> query = realm.where(AnswerResult.class).equalTo("questionId", id);
        AnswerResult result = query.findFirst();
        return result;

    }

    public static ArrayList<AnswerResult> getAllById(int id) {
        ArrayList<AnswerResult> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AnswerResult> query = realm.where(AnswerResult.class).equalTo("questionId", id);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static ArrayList<AnswerResult> getAnswer(int questionId,int answerId) {
        ArrayList<AnswerResult> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AnswerResult> query = realm.where(AnswerResult.class).equalTo("questionId", questionId).and().equalTo("answerId",answerId);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static void clear(int questionId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(AnswerResult.class).equalTo("questionId", questionId).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static void clearByAnswerId(int answerId) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(AnswerResult.class).equalTo("answerId", answerId).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    public static ArrayList<AnswerResult> getAll() {
        ArrayList<AnswerResult> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AnswerResult> query = realm.where(AnswerResult.class);
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
        realm.delete(AnswerResult.class);
        realm.commitTransaction();
    }
}
