package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SleepQuestionResponse {
    @JsonProperty("question_id")
    int    questionId;
    @JsonProperty("is_multiple_choice")
    Boolean            isMultipleChoice;
    String             content;
    ArrayList<SleepQuestionAnswerResponse> answers;

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

    public ArrayList<SleepQuestionAnswerResponse> getAnswers() {
        return answers;
    }
}
