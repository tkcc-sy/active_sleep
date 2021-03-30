package com.paramount.bed.util.alarms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.paramount.bed.data.model.AlarmStopModel;

import java.util.Calendar;

public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        AlarmStopModel.stopStatusRegister(day);
        stopSelf();
    }
}
