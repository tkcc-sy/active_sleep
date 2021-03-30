package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyScoreStatusResponse {
    @JsonProperty("date")
    public String date;
    @JsonProperty("daily_score")
    public int dailyScore;
    @JsonProperty("last_update")
    public String lastUpdate;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDailyScore() {
        return dailyScore;
    }

    public void setDailyScore(int dailyScore) {
        this.dailyScore = dailyScore;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}