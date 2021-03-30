package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.NemuriConstantsModel;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceTemplateResponse {
    @SerializedName("bed")
    public ArrayList<DeviceTemplateBedModel> bed;
    @SerializedName("mattress")
    public ArrayList<DeviceTemplateMattressModel> mattress;
    @JsonProperty("bed_default")
    public ArrayList<DeviceTemplateBedModel> bedDefault;
    @JsonProperty("mattress_default")
    public ArrayList<DeviceTemplateMattressModel> mattressDefault;
    @SerializedName("constants")
    public NemuriConstantsModel constants;

    public DeviceTemplateResponse() {
    }

    public ArrayList<DeviceTemplateBedModel> getBed() {
        return bed;
    }

    public void setBed(ArrayList<DeviceTemplateBedModel> bed) {
        this.bed = bed;
    }

    public ArrayList<DeviceTemplateBedModel> getBedDefault() {
        return bedDefault;
    }

    public void setBedDefault(ArrayList<DeviceTemplateBedModel> bedDefault) {
        this.bedDefault = bedDefault;
    }

    public ArrayList<DeviceTemplateMattressModel> getMattress() {
        return mattress;
    }

    public void setMattress(ArrayList<DeviceTemplateMattressModel> mattress) {
        this.mattress = mattress;
    }

    public ArrayList<DeviceTemplateMattressModel> getMattressDefault() {
        return mattressDefault;
    }

    public void setMattressDefault(ArrayList<DeviceTemplateMattressModel> mattressDefault) {
        this.mattressDefault = mattressDefault;
    }

    public NemuriConstantsModel getConstants() {
        return constants;
    }

    public void setConstants(NemuriConstantsModel constants) {
        this.constants = constants;
    }


}


