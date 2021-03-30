package com.paramount.bed.data.remote.response;

import com.paramount.bed.data.model.MattressSettingModel;

public class MattressSettingResponse extends BaseResponse {
    private MattressSettingModel data;

    @Override
    public MattressSettingModel getData() {
        return data;
    }

    public void setData(MattressSettingModel data) {
        this.data = data;
    }
}
