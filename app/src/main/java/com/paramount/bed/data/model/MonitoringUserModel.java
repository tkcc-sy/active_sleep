package com.paramount.bed.data.model;


public class MonitoringUserModel {
    public enum MonitoringUserStatus {
        PENDING_APPROVAL,
        PENDING_PAYMENT,
        PROCESSING_PAYMENT,
        ACTIVE
    }

    String username;
    int status;
    int id;

    public MonitoringUserModel(String username, MonitoringUserStatus status) {
        this.username = username;
        this.status = status.ordinal();
    }

    public MonitoringUserModel(int id, String username, MonitoringUserStatus status) {
        this.id = id;
        this.username = username;
        this.status = status.ordinal();
    }

    public boolean isActive(){
        return this.status == MonitoringUserStatus.ACTIVE.ordinal();
    }
    public String getUsername() {
        return username;
    }
    public String getStringStatus(){
        String statusString = "Unknown";
        switch (status){
            case 0 :
                statusString = "承認待ち";
                break;
            case 1 :
                statusString = "";
            break;
            case 2 :
                statusString = "";
            break;
            case 3 :
                statusString = "";
            break;
            default:
                break;

        }
        return statusString;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }
}
