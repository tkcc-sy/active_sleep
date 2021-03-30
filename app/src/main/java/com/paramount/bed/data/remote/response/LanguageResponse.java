package com.paramount.bed.data.remote.response;

import com.paramount.bed.data.model.LanguageModel;
import com.paramount.bed.data.remote.response.BaseResponse;

public class LanguageResponse  extends BaseResponse {
    private LanguageModel data[];

    @Override
    public LanguageModel[] getData() {
        return data;
    }

    public void setData(LanguageModel[] data) {
        this.data = data;
    }
}
