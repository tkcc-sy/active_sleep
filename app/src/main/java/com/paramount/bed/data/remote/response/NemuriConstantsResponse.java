package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.SliderModel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NemuriConstantsResponse extends BaseResponse {
    private NemuriConstantsModel data;

    @Override
    public NemuriConstantsModel getData() {
        return data;
    }

    public void setData(NemuriConstantsModel data) {
        this.data = data;
    }
}
