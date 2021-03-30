package com.paramount.bed.data.model;


import com.paramount.bed.data.remote.response.FAQLinkResponse;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class FAQLinkModel extends RealmObject {
    @PrimaryKey
    public String PID;
    public String linkNo;
    public String appliTag;

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getLinkNo() {
        return linkNo;
    }

    public void setLinkNo(String linkNo) {
        this.linkNo = linkNo;
    }

    public String getAppliTag() {
        return appliTag;
    }

    public void setAppliTag(String appliTag) {
        this.appliTag = appliTag;
    }

    public FAQLinkModel() {
    }

    public FAQLinkModel(String PID, String linkNo, String appliTag) {
        this.PID = PID;
        this.linkNo = linkNo;
        this.appliTag = appliTag;
    }

    public static String getLinkByTag(String tag) {
        FAQLinkModel faqLinkModel = getByTag(tag);
        if(faqLinkModel != null){
            return faqLinkModel.getLinkNo();
        }
        return null;
    }

    public static FAQLinkModel getByTag(String tag) {
        if (FAQLinkModel.getAll().size() == 0) {
            ArrayList<FAQLinkModel> data = FAQLinkModel.initialFAQ();
            FAQLinkModel.clear();
            for (int i = 0; i < data.size(); i++) {
                FAQLinkModel faqLinkModel = new FAQLinkModel();
                faqLinkModel.setPID(UUID.randomUUID().toString());
                faqLinkModel.setLinkNo(data.get(i).getLinkNo());
                faqLinkModel.setAppliTag(data.get(i).getAppliTag());
                faqLinkModel.insert();
            }
        }

        Realm realm = Realm.getDefaultInstance();
        FAQLinkModel faqLinkModel = realm.where(FAQLinkModel.class).equalTo("appliTag", tag.trim()).findFirst();

        return faqLinkModel;

    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insertOrUpdate(this);
        realm.commitTransaction();
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static FAQLinkModel getFirst() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<FAQLinkModel> query = realm.where(FAQLinkModel.class);
        FAQLinkModel result = query.findFirst();
        return result;
    }

    public static ArrayList<FAQLinkModel> getAll() {
        ArrayList<FAQLinkModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<FAQLinkModel> query = realm.where(FAQLinkModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        if (list.size() == 0) {
            list = initialFAQ();
        }
        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FAQLinkModel.class);
        realm.commitTransaction();
    }

    public static void truncate() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(FAQLinkModel.class);
        realm.commitTransaction();
    }

    public static ArrayList<FAQLinkModel> initialFAQ() {
        ArrayList<FAQLinkModel> defaultFAQLink = new ArrayList<>();
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "1", "UI000531C000"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "2", "UI000532C000"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "3", "UI000533C000"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "4", "UI000534C000"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "5", "UI000541C000"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "6", "UI000542C000"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "7", "UI000543C000"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "8", "UI000544C000"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "9", "UI000610C048"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "10", "UI000610C049"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "11", "UI000310C009"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "12", "UI000802C177"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "13", "UI000802C171"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "14", "UI000802C178"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "14", "UI000802C169"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "14", "UI000802C173"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "14", "UI000802C174"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "14", "UI000802C175"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "15", "UI000802C167"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "16", "UI000802C168"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "17", "UI000802C170"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "18", "UI000802C172"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "19", "UI000610C043"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "20", "UI000802C176"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "21", "UI000610C041"));
        defaultFAQLink.add(new FAQLinkModel(UUID.randomUUID().toString(), "22", "UI000802C027"));
        return defaultFAQLink;
    }
}
