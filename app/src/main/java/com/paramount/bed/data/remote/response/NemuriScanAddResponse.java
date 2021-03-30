package com.paramount.bed.data.remote.response;

public class NemuriScanAddResponse extends BaseResponse {
    private String data;

    @Override
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
