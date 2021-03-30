package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;



@JsonIgnoreProperties(ignoreUnknown = true)
public class VersionContent {
    String key;
    @JsonProperty("last_updated")
    int lastUpdate;

    public String getKey() {
        return key;
    }

    public int getLastUpdate() {
        return lastUpdate;
    }
}
