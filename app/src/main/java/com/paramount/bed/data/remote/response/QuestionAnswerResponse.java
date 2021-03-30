package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionAnswerResponse {
    @JsonProperty("answer_id")
    int  answerId;
    String      content;

    public int getAnswerId() {
        return answerId;
    }

    public String getContent() {
        return content;
    }
}
