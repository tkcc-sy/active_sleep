package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {
        int id;
        int group_id;
        String group_name;
        String email;
        String nickname;
        String password;
        String zip_code;
        String prefecture;
        String city;
        String street_address;
        String birth_date;
        int gender;
        String phone_number;
        String sleep_questionnaire_id;
        String optional_questionnaire_id;
        String user_type;
        String user_active_from;
        String user_active_to;
        boolean is_blocked;
        String password_attempt;
        String created_date;
        String last_activity_date;
        boolean phone_activated;
        String sns_token;
        int sns_provider;
        int company_id;
        int height;
        int weight;
        int recommendation_questionnaire_id;
        String ns_serial_number;
        String company_code;
        String api_token;
        String message_value;
        String message_value2;

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public int getGroup_id() {
                return group_id;
        }

        public void setGroup_id(int group_id) {
                this.group_id = group_id;
        }

        public String getGroup_name() {
                return group_name;
        }

        public void setGroup_name(String group_name) {
                this.group_name = group_name;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

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

        public String getPrefecture() {
                return prefecture;
        }

        public void setPrefecture(String prefecture) {
                this.prefecture = prefecture;
        }

        public String getCity() {
                return city;
        }

        public void setCity(String city) {
                this.city = city;
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

        public int getGender() {
                return gender;
        }

        public void setGender(int gender) {
                this.gender = gender;
        }

        public String getPhone_number() {
                return phone_number;
        }

        public void setPhone_number(String phone_number) {
                this.phone_number = phone_number;
        }

        public String getSleep_questionnaire_id() {
                return sleep_questionnaire_id;
        }

        public void setSleep_questionnaire_id(String sleep_questionnaire_id) {
                this.sleep_questionnaire_id = sleep_questionnaire_id;
        }

        public String getOptional_questionnaire_id() {
                return optional_questionnaire_id;
        }

        public void setOptional_questionnaire_id(String optional_questionnaire_id) {
                this.optional_questionnaire_id = optional_questionnaire_id;
        }

        public String getUser_type() {
                return user_type;
        }

        public void setUser_type(String user_type) {
                this.user_type = user_type;
        }

        public String getUser_active_from() {
                return user_active_from;
        }

        public void setUser_active_from(String user_active_from) {
                this.user_active_from = user_active_from;
        }

        public String getUser_active_to() {
                return user_active_to;
        }

        public void setUser_active_to(String user_active_to) {
                this.user_active_to = user_active_to;
        }

        public boolean is_blocked() {
                return is_blocked;
        }

        public void set_blocked(boolean is_blocked) {
                this.is_blocked = is_blocked;
        }

        public String getPassword_attempt() {
                return password_attempt;
        }

        public void setPassword_attempt(String password_attempt) {
                this.password_attempt = password_attempt;
        }

        public String getCreated_date() {
                return created_date;
        }

        public void setCreated_date(String created_date) {
                this.created_date = created_date;
        }

        public String getLast_activity_date() {
                return last_activity_date;
        }

        public void setLast_activity_date(String last_activity_date) {
                this.last_activity_date = last_activity_date;
        }

        public boolean isPhone_activated() {
                return phone_activated;
        }

        public void setPhone_activated(boolean phone_activated) {
                this.phone_activated = phone_activated;
        }

        public String getSns_token() {
                return sns_token;
        }

        public void setSns_token(String sns_token) {
                this.sns_token = sns_token;
        }

        public int getSns_provider() {
                return sns_provider;
        }

        public void setSns_provider(int sns_provider) {
                this.sns_provider = sns_provider;
        }

        public int getCompany_id() {
                return company_id;
        }

        public void setCompany_id(int company_id) {
                this.company_id = company_id;
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

        public int getRecommendation_questionnaire_id() {
                return recommendation_questionnaire_id;
        }

        public void setRecommendation_questionnaire_id(int recommendation_questionnaire_id) {
                this.recommendation_questionnaire_id = recommendation_questionnaire_id;
        }

        public String getNs_serial_number() {
                return ns_serial_number;
        }

        public void setNs_serial_number(String ns_serial_number) {
                this.ns_serial_number = ns_serial_number;
        }

        public String getCompany_code() {
                return company_code;
        }

        public void setCompany_code(String company_code) {
                this.company_code = company_code;
        }

        public String getApi_token() {
                return api_token;
        }

        public void setApi_token(String api_token) {
                this.api_token = api_token;
        }

        public String getMessage_value() {
                return message_value;
        }

        public void setMessage_value(String message_value) {
                this.message_value = message_value;
        }

        public String getMessage_value2() {
                return message_value2;
        }

        public void setMessage_value2(String message_value2) {
                this.message_value2 = message_value2;
        }
}
