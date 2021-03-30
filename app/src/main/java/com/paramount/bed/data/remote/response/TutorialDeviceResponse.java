package com.paramount.bed.data.remote.response;

public class TutorialDeviceResponse {

    String device;
    TutorialImageResponse data;

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public TutorialImageResponse getData() {
        return data;
    }

    public void setData(TutorialImageResponse data) {
        this.data = data;
    }
}
