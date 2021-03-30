package com.paramount.bed.data.model;

import java.util.List;

public class SleepQuestionnaireQuestionResult {
    int question_id;
    String question;
    List<String> answer;
    List<Integer> iconIndex;
    List<Integer> answerId;

    public SleepQuestionnaireQuestionResult() {
    }

    public SleepQuestionnaireQuestionResult(int question_id, String question, List<String> answer, List<Integer> iconIndex, List<Integer> answerId) {
        this.question_id = question_id;
        this.question = question;
        this.answer = answer;
        this.iconIndex = iconIndex;
        this.answerId = answerId;
    }


    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getResult() {
        return answer;
    }

    public void setResult(List<String> result) {
        this.answer = result;
    }

    public List<String> getAnswer() {
        return answer;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public List<Integer> getIconIndex() {
        return iconIndex;
    }

    public void setIconIndex(List<Integer> iconIndex) {
        this.iconIndex = iconIndex;
    }

    public List<Integer> getAnswerId() {
        return answerId;
    }

    public void setAnswerId(List<Integer> answerId) {
        this.answerId = answerId;
    }
}
