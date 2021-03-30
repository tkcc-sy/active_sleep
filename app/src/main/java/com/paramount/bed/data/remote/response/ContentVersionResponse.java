package com.paramount.bed.data.remote.response;

import com.paramount.bed.data.model.ContentVersionModel;

public class ContentVersionResponse extends BaseResponse {
    private ContentVersionModel data[];

    @Override
    public ContentVersionModel[] getData() {
        return data;
    }

    public void setData(ContentVersionModel[] data) {
        this.data = data;
    }
}
