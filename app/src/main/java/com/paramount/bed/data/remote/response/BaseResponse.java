package com.paramount.bed.data.remote.response;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse<T> {
    boolean success;
    @JsonProperty("error_code")
    @Nullable String errorCode;
    String message;
    @Nullable  T data;

    public boolean getSuccess() {
        return success;
    }

    public boolean isSucces(){
        return success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
