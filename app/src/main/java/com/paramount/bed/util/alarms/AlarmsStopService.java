package com.paramount.bed.util.alarms;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.paramount.bed.data.model.AlarmStopModel;

import java.util.Calendar;

public class AlarmsStopService extends IntentService {
    public static final String ACTION_STOP = "STOP";

    public AlarmsStopService() {
        super("AlarmsStopService");
    }

    public AlarmsStopService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        if (ACTION_STOP.equals(action)) {
            AlarmsScheduler.cancelNotificationToday(this);
            stopSelf();
        } else {
            throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }
}