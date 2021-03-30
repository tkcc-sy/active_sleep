package com.paramount.bed.data.model;

import android.annotation.SuppressLint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MHSModel extends RealmObject {
    int score = -1;
    @JsonProperty("is_default")
    int isDefault = 1;
    @JsonProperty("user_desired_hardness")
    int desiredHardness;
    String date;
    @JsonProperty("mattress_hardness")
    RealmList<Integer> mattressHardness;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDesiredHardness() {
        return desiredHardness;
    }

    public void setDesiredHardness(int desiredHardness) {
        this.desiredHardness = desiredHardness;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDate(Date date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.date =  dateFormat.format(date);
    }
    public RealmList<Integer> getMattressHardness() {
        return mattressHardness;
    }

    public void setMattressHardness(RealmList<Integer> mattressHardness) {
        this.mattressHardness = mattressHardness;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }
}
