package com.paramount.bed.data.model;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.BuildConfig;

import java.util.ArrayList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class AppStateModel extends RealmObject{
    boolean isFirstRun;
    String locale;
    public static boolean isFirstRun() {
        AppStateModel appStateModel = AppStateModel.get();
        return appStateModel.isFirstRun;
    }


    public static String getLocale() {
        AppStateModel appStateModel = AppStateModel.get();
//        return appStateModel.locale;
        return getSystemLanguage();
    }

    public static void setLocale(String locale) {
        AppStateModel appStateModel = AppStateModel.get();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        appStateModel.locale = locale;
        realm.commitTransaction();
    }

    public AppStateModel() {
        this.isFirstRun = true;
        this.locale=getSystemLanguage();
    }

    public static String getSystemLanguage(){
        //System Language
        String systemLanguage=Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry();
        if (systemLanguage!=null && (systemLanguage.equals("id-ID") || systemLanguage.equals("in-ID") || systemLanguage.equals("en-US") || systemLanguage.equals("ja-JP") || systemLanguage.equals("jp-JP"))){
            return systemLanguage.equals("ja-JP")?"jp-JP":systemLanguage.equals("in-ID")?"id-ID":systemLanguage;
        } else {
            return "jp-JP";
        }
    }
    public void delete(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        deleteFromRealm();
        realm.commitTransaction();
    }
    private static AppStateModel get(){
        ArrayList<SliderModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AppStateModel> query = realm.where(AppStateModel.class);
        if(query.count() <= 0){
            AppStateModel.create();
            query = realm.where(AppStateModel.class);
        }

        return query.findFirst();
    }
    private  static void create(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(new AppStateModel());
        realm.commitTransaction();
    }

    public static void setFirstRun(boolean firstRun) {
        AppStateModel appStateModel = AppStateModel.get();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        appStateModel.isFirstRun = firstRun;
        realm.commitTransaction();
    }

    public static ArrayList<AppStateModel> getAll() {
        ArrayList<AppStateModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<AppStateModel> query = realm.where(AppStateModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}

