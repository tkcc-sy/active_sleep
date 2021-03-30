package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NemuriScanCheckResponse extends BaseResponse {
    private NemuriScanCheckContainer data;

    @Override
    public NemuriScanCheckContainer getData() {
        return data;
    }

    public void setData(NemuriScanCheckContainer data) {
        this.data = data;
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class NemuriScanCheckContainer {
        @JsonProperty("ns_url_ip")
        private String nsUrlIp;
        @JsonProperty("ns_url")
        private String nsUrl;
        @JsonProperty("server_id")
        private String serverId;

        public String getNsUrlIp() {
            return nsUrlIp == null || nsUrlIp.isEmpty() ? "27.86.1.164/as/api/v1/NemuriScan/polling" : nsUrlIp;
        }

        public void setNsUrlIp(String nsUrlIp) {
            this.nsUrlIp = nsUrlIp;
        }

        public String getNsUrl() {
            return nsUrl;
        }

        public void setNsUrl(String nsUrl) {
            this.nsUrl = nsUrl;
        }

        public String getServerId() {
            return serverId;
        }

        public void setServerId(String serverId) {
            this.serverId = serverId;
        }
    }
}
