package com.paramount.bed.data.model;

import java.util.List;

public class QuestionnaireResult {
    int id;
    String title;
    String description;
    List<QuestionnaireQuestionResult> result;

    public QuestionnaireResult() {
    }

    public QuestionnaireResult(int id, String title, String description, List<QuestionnaireQuestionResult> result) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<QuestionnaireQuestionResult> getResult() {
        return result;
    }

    public void setResult(List<QuestionnaireQuestionResult> result) {
        this.result = result;
    }
}
