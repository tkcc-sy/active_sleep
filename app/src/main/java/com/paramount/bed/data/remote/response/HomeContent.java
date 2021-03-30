package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HomeContent {
    String home;

    @JsonProperty("home_weekly")
    String homeWeekly;

    String detail;
    @JsonProperty("detail_weekly")
    String detailWeekly;

    @JsonProperty("realtime_bed")
    String realtimeBed;

    String calendar;

    public String getHome() {
        return home;
    }

    public String getHomeWeekly() {
        return homeWeekly;
    }

    public String getDetailWeekly() {
        return detailWeekly;
    }

    public String getDetail() {
        return detail;
    }

    public String getCalendar() {
        return calendar;
    }

    public String getRealtimeBed() {
        return realtimeBed;
    }

    public void setRealtimeBed(String realtimeBed) {
        this.realtimeBed = realtimeBed;
    }
}
