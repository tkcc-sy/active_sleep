package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordPolicyResponse {
    @SerializedName("min_length")
    public String min_length;
    @SerializedName("max_length")
    public String max_length;
    @SerializedName("allowed_symbols")
    public String allowed_symbols;
}
