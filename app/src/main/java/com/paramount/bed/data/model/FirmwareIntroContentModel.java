package com.paramount.bed.data.model;

import android.content.Context;

import com.paramount.bed.data.provider.FirmwareProvider;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class FirmwareIntroContentModel extends RealmObject {
    String content;

    public String getContent() {
        return content;
    }

    public void delete() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }
    public FirmwareIntroContentModel getUnmanaged() {
        if(!isManaged()){
            return this;
        }
        Realm realm = Realm.getDefaultInstance();
        FirmwareIntroContentModel unmanagedObject =  realm.copyFromRealm(this);
        realm.close();
        return unmanagedObject;
    }
    public void updateContent(String content) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.content = content;
        realm.commitTransaction();
    }
    public static FirmwareIntroContentModel get(Context context) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<FirmwareIntroContentModel> query = realm.where(FirmwareIntroContentModel.class);
        if (query.count() <= 0) {
            FirmwareIntroContentModel.create(context);
            query = realm.where(FirmwareIntroContentModel.class);
        }

        return query.findFirst();
    }

    private static void create(Context context) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        FirmwareIntroContentModel newContent = new FirmwareIntroContentModel();
        newContent.content = FirmwareProvider.getLocalContent(context);
        realm.insert(newContent);
        realm.commitTransaction();
    }
}