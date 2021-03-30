package com.paramount.bed.data.model;

import java.util.List;

public class QuestionnaireQuestionResult {
    int question_id;
    String question;
    List<String> answer;

    public QuestionnaireQuestionResult() {
    }

    public QuestionnaireQuestionResult(int question_id, String question, List<String> answer) {
        this.question_id = question_id;
        this.question = question;
        this.answer = answer;
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
}
