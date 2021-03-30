package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SleepQuestionAnswerResponse {
    @JsonProperty("answer_id")
    int  answerId;
    String      content;
    @JsonProperty("icon_index")
    int  iconIndex;

    public int getAnswerId() {
        return answerId;
    }

    public String getContent() {
        return content;
    }

    public int getIconIndex() {
        return iconIndex;
    }
}
