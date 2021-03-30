package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireResponse<T> {

    @JsonProperty("questionnaire_id")
    int questionnaireId;

    String title;
    String description;
    @JsonIgnoreProperties(ignoreUnknown=true)
    T questions;

    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public T getQuestions() {
        return questions;
    }
}
