package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsHomeResponse extends BaseResponse {
    @Nullable
    public NewsResponse regular;
    @Nullable
    public NewsResponse malfunction;
    @Nullable
    public NewsResponse maintenance;
    @Nullable
    public NewsResponse birthday;

    public NewsHomeResponse() {
    }

    public NewsHomeResponse(@Nullable NewsResponse regular, @Nullable NewsResponse malfunction, @Nullable NewsResponse maintenance, @Nullable NewsResponse birthday) {
        this.regular = regular;
        this.malfunction = malfunction;
        this.maintenance = maintenance;
        this.birthday = birthday;
    }

    @Nullable
    public NewsResponse getRegular() {
        return regular;
    }

    public void setRegular(@Nullable NewsResponse regular) {
        this.regular = regular;
    }

    @Nullable
    public NewsResponse getMalfunction() {
        return malfunction;
    }

    public void setMalfunction(@Nullable NewsResponse malfunction) {
        this.malfunction = malfunction;
    }

    @Nullable
    public NewsResponse getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(@Nullable NewsResponse maintenance) {
        this.maintenance = maintenance;
    }

    @Nullable
    public NewsResponse getBirthday() {
        return birthday;
    }

    public void setBirthday(@Nullable NewsResponse birthday) {
        this.birthday = birthday;
    }
}
