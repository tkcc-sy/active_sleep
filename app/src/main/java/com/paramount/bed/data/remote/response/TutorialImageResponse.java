package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TutorialImageResponse {

    String home1;
    String home2;
    String home3;
    String remote1;
    String remote2;
    String remote3;
    String remote4;

    public String getHome1() {
        return home1;
    }

    public void setHome1(String home1) {
        this.home1 = home1;
    }

    public String getHome2() {
        return home2;
    }

    public void setHome2(String home2) {
        this.home2 = home2;
    }

    public String getHome3() {
        return home3;
    }

    public void setHome3(String home3) {
        this.home3 = home3;
    }

    public String getRemote1() {
        return remote1;
    }

    public void setRemote1(String remote1) {
        this.remote1 = remote1;
    }

    public String getRemote2() {
        return remote2;
    }

    public void setRemote2(String remote2) {
        this.remote2 = remote2;
    }

    public String getRemote3() {
        return remote3;
    }

    public void setRemote3(String remote3) {
        this.remote3 = remote3;
    }

    public String getRemote4() {
        return remote4;
    }

    public void setRemote4(String remote4) {
        this.remote4 = remote4;
    }

}
