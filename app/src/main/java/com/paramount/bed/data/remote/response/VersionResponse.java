package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionResponse extends BaseResponse {
    @JsonProperty("major")
    public Integer major;

    @JsonProperty("minor")
    public Integer minor;

    @JsonProperty("revision")
    public Integer revision;

    public VersionResponse() {
    }

    public VersionResponse(Integer major, Integer minor, Integer revision) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }
}
