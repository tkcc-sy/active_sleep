package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMonitoringResponse {
    int id;
    String nickname;
    int status;

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public int getStatus() {
        return status;
    }

}
