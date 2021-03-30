package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.paramount.bed.data.model.SliderModel;
@JsonIgnoreProperties(ignoreUnknown = true)
public class SliderResponse extends BaseResponse {
    private SliderModel data[];

    @Override
    public SliderModel[] getData() {
        return data;
    }

    public void setData(SliderModel[] data) {
        this.data = data;
    }
}
