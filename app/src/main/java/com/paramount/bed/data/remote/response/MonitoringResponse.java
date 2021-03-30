package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitoringResponse {

    public int id;
    @JsonProperty("nick_name")
    public String nickName;
    public Integer status;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNick_name() {
        return nickName;
    }
    public Integer getStatus(){
        return  status;
    }

    public void setNick_name(String nick_name) {
        this.nickName = nick_name;
    }
    public void setStatus(Integer status){this.status = status;}


}
