package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateResponse {
        String nickname;
        String password;
        String zip_code;
        String street_address;
        String birth_date;
        int height;
        int weight;




        public String getNickname() {
                return nickname;
        }

        public void setNickname(String nickname) {
                this.nickname = nickname;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public String getZip_code() {
                return zip_code;
        }

        public void setZip_code(String zip_code) {
                this.zip_code = zip_code;
        }


        public String getStreet_address() {
                return street_address;
        }

        public void setStreet_address(String street_address) {
                this.street_address = street_address;
        }

        public String getBirth_date() {
                return birth_date;
        }

        public void setBirth_date(String birth_date) {
                this.birth_date = birth_date;
        }

        public int getHeight() {
                return height;
        }

        public void setHeight(int height) {
                this.height = height;
        }

        public int getWeight() {
                return weight;
        }

        public void setWeight(int weight) {
                this.weight = weight;
        }


}
