package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.paramount.bed.data.model.DashboardModel;

public class DashboardResponse {
    DashboardModel home;

    @JsonProperty("home_weekly")
    DashboardModel homeWeekly;

    DashboardModel detail;
    @JsonProperty("detail_weekly")
    DashboardModel detailWeekly;

    @JsonProperty("realtime_bed")
    DashboardModel realtimeBed;

    DashboardModel calendar;

    public DashboardModel getHome() {
        return home;
    }

    public DashboardModel getHomeWeekly() {
        return homeWeekly;
    }

    public DashboardModel getDetailWeekly() {
        return detailWeekly;
    }

    public DashboardModel getDetail() {
        return detail;
    }

    public DashboardModel getCalendar() {
        return calendar;
    }

    public DashboardModel getRealtimeBed() {
        return realtimeBed;
    }

    public void setHome(DashboardModel home) {
        this.home = home;
    }

    public void setHomeWeekly(DashboardModel homeWeekly) {
        this.homeWeekly = homeWeekly;
    }

    public void setDetail(DashboardModel detail) {
        this.detail = detail;
    }

    public void setDetailWeekly(DashboardModel detailWeekly) {
        this.detailWeekly = detailWeekly;
    }

    public void setRealtimeBed(DashboardModel realtimeBed) {
        this.realtimeBed = realtimeBed;
    }

    public void setCalendar(DashboardModel calendar) {
        this.calendar = calendar;
    }


}
