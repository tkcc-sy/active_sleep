package com.paramount.bed.data.model;


import com.paramount.bed.BedApplication;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class ServerModel extends RealmObject {
    public String url;

    public String getUrl() {
        if (ServerModel.getHost() != null && ServerModel.getHost().url != null && ServerModel.getHost().url.trim().length() != 0) {
            BedApplication.serverHost = ServerModel.getHost().url;
        }
        if (BedApplication.serverHost != null) {
            return BedApplication.serverHost;
        }
        return url;
    }

    public void setUrl(String url) {
        BedApplication.serverHost = url;
        this.url = url;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }

    public static ServerModel getHost() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ServerModel> query = realm.where(ServerModel.class);
        ServerModel result = query.findFirst();
        return result;
    }

    public static ArrayList<ServerModel> getAll() {
        ArrayList<ServerModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<ServerModel> query = realm.where(ServerModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));

        return list;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(ServerModel.class);
        realm.commitTransaction();
    }
}
