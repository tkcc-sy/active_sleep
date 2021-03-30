package com.paramount.bed;

import android.app.Activity;
import android.app.Application;

import com.paramount.bed.util.ApplicationLifecycleHandler;
import com.paramount.bed.util.MigrationUtil;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BedApplication extends Application {
    public static BedApplication sApplication;
    public static String serverHost;
    public static String token;
    public static Integer UserId;


    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .schemaVersion(31)
                .migration(new MigrationUtil())
                .build();
        Realm.compactRealm(config);
        Realm.setDefaultConfiguration(config);
        sApplication = this;
        try {
            ApplicationLifecycleHandler handler = new ApplicationLifecycleHandler();
            registerActivityLifecycleCallbacks(handler);
            registerComponentCallbacks(handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Activity mCurrentActivity = null;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity mCurrentActivity) {
        this.mCurrentActivity = mCurrentActivity;
    }

    public static BedApplication getsApplication() {
        return sApplication;
    }
}