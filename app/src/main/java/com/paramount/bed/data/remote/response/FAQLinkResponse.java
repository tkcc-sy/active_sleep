package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FAQLinkResponse {
    @JsonProperty("link_no")
    public String linkNo;
    @JsonProperty("appli_tag")
    public String appliTag;

    public String getLinkNo() {
        return linkNo;
    }

    public void setLinkNo(String linkNo) {
        this.linkNo = linkNo;
    }

    public String getAppliTag() {
        return appliTag;
    }

    public void setAppliTag(String appliTag) {
        this.appliTag = appliTag;
    }
}


