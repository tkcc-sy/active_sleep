package com.paramount.bed.data.model;

import io.realm.RealmObject;

public class MattressHardnessSettingModel extends RealmObject {
    int id;
    String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
