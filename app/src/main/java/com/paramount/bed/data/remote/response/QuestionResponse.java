package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionResponse<T> {
    @JsonProperty("question_id")
    int    questionId;
    @JsonProperty("is_multiple_choice")
    Boolean            isMultipleChoice;
    String             content;
    T answers;

    public int getQuestionId() {
        return questionId;
    }
    public Boolean getisMultipleChoice() {
        return isMultipleChoice;
    }

    public boolean isMultipleChoice() {
        return isMultipleChoice;
    }

    public String getContent() {
        return content;
    }

    public T getAnswers() {
        return answers;
    }
}
