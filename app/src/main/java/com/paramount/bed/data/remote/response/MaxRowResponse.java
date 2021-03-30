package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MaxRowResponse {
    @JsonProperty("max_row_log")
    public int maxRowLog;
    @JsonProperty("max_row_daily_score")
    public int maxRowDailyScore;
    @JsonProperty("max_row_weekly_score")
    public int maxRowWeeklyScore;

    public int getMaxRowLog() {
        return maxRowLog;
    }

    public void setMaxRowLog(int maxRowLog) {
        this.maxRowLog = maxRowLog;
    }

    public int getMaxRowDailyScore() {
        return maxRowDailyScore;
    }

    public void setMaxRowDailyScore(int maxRowDailyScore) {
        this.maxRowDailyScore = maxRowDailyScore;
    }

    public int getMaxRowWeeklyScore() {
        return maxRowWeeklyScore;
    }

    public void setMaxRowWeeklyScore(int maxRowWeeklyScore) {
        this.maxRowWeeklyScore = maxRowWeeklyScore;
    }
}