package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationEmailResponse {
    public String activation_id;
    public String token;

    public String getActivation_id() {
        return activation_id;
    }

    public void setActivation_id(String activation_id) {
        this.activation_id = activation_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


