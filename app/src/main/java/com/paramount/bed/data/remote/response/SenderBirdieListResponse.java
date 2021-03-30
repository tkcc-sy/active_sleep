package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SenderBirdieListResponse {
    @JsonProperty("monitored_user_id")
    public String monitoredUserId;
    @JsonProperty("monitored_nickname")
    public String monitoredNickname;
    @JsonProperty("created_date")
    public String createdDate;

    public String getMonitoredUserId() {
        return monitoredUserId;
    }

    public void setMonitoredUserId(String monitoredUserId) {
        this.monitoredUserId = monitoredUserId;
    }

    public String getMonitoredNickname() {
        return monitoredNickname;
    }

    public void setMonitoredNickname(String monitoredNickname) {
        this.monitoredNickname = monitoredNickname;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}


