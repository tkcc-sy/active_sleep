package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ZipResponse<T> {
    boolean success;

    @JsonProperty("zip_code")
    String zipCode;
    String prefecture;
    String city;
    String town;

    public boolean getSuccess() {
        return success;
    }

    public boolean isSucces(){
        return success;
    }


    public String getZipCode(){return zipCode;}

    public String getPrefecture(){return prefecture;}

    public String getCity(){return city;}

    public String getTown(){return town;}


}
