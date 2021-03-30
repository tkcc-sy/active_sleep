package com.paramount.bed.data.model;

public class RegisterData {
    public RegisterData() {
        this.type = 0;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String email;
    int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
