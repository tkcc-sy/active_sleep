package com.paramount.bed.data.remote.response;

import com.paramount.bed.data.model.ContentTNCModel;

public class ContentTNCAppUpdateResponse extends BaseResponse {
    private ContentTNCModel data[];

    @Override
    public ContentTNCModel[] getData() {
        return data;
    }

    public void setData(ContentTNCModel[] data) {
        this.data = data;
    }
}
