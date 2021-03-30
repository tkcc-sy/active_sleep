package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TutorialResponse extends BaseResponse {

    TutorialDeviceResponse data[];

    @Override
    public TutorialDeviceResponse[] getData() {
        return data;
    }

    public void setData(TutorialDeviceResponse[] data) {
        this.data = data;
    }
}
