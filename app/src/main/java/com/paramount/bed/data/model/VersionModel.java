package com.paramount.bed.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.Sort;

public class VersionModel extends RealmObject {

    @JsonProperty("major")
    private Integer major;

    @JsonProperty("minor")
    private Integer minor;

    @JsonProperty("revision")
    private Integer revision;
    @JsonProperty("revision")
    private Boolean isTNCRead;

    public VersionModel() {
    }

    public VersionModel(Integer major, Integer minor, Integer revision, Boolean isTNCRead) {
        this.major = major;
        this.minor = minor;
        this.revision = revision;
        this.isTNCRead = isTNCRead;
    }

    public Integer getMajor() {
        return major;
    }

    public Integer getMinor() {
        return minor;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Boolean getTNCRead() {
        return isTNCRead;
    }

    public void setTNCRead(Boolean TNCRead) {
        isTNCRead = TNCRead;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static ArrayList<VersionModel> getAll() {
        ArrayList<VersionModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<VersionModel> query = realm.where(VersionModel.class).sort("major", Sort.ASCENDING);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(VersionModel.class);
        realm.commitTransaction();
    }
}
