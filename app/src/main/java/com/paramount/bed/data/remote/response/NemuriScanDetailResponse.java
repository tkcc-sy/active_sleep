package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NemuriScanDetailResponse {
    @JsonProperty("ns_serial_number")
    public String nsSerialNumber;
    @JsonProperty("bed_active")
    public int bedActive;
    @JsonProperty("mattress_active")
    public int mattressActive;
    @JsonProperty("last_connection_time")
    public String lastConnectionTime;

    public String getNsSerialNumber() {
        return nsSerialNumber;
    }

    public void setNsSerialNumber(String nsSerialNumber) {
        this.nsSerialNumber = nsSerialNumber;
    }

    public int getBedActive() {
        return bedActive;
    }

    public void setBedActive(int bedActive) {
        this.bedActive = bedActive;
    }

    public int getMattressActive() {
        return mattressActive;
    }

    public void setMattressActive(int mattressActive) {
        this.mattressActive = mattressActive;
    }

    public String getLastConnectionTime() {
        return lastConnectionTime;
    }

    public void setLastConnectionTime(String lastConnectionTime) {
        this.lastConnectionTime = lastConnectionTime;
    }
}


