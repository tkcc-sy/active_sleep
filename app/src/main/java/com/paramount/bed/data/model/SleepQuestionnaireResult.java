package com.paramount.bed.data.model;

import java.util.List;

public class SleepQuestionnaireResult {
    int id;
    String title;
    String description;
    List<SleepQuestionnaireQuestionResult> result;

    public SleepQuestionnaireResult() {
    }

    public SleepQuestionnaireResult(int id, String title, String description, List<SleepQuestionnaireQuestionResult> result) {
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

    public List<SleepQuestionnaireQuestionResult> getResult() {
        return result;
    }

    public void setResult(List<SleepQuestionnaireQuestionResult> result) {
        this.result = result;
    }
}
