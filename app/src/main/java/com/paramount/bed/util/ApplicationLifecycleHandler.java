package com.paramount.bed.util;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;

import com.paramount.bed.ble.NSManager;
import com.paramount.bed.data.model.IsForegroundModel;
import com.paramount.bed.data.model.LogUserModel;
import com.paramount.bed.ui.main.AutomaticSleepOperationActivity;
import com.paramount.bed.ui.main.AutomaticWakeOperationActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.main.RealtimeMonitorDialog;
import com.paramount.bed.ui.main.RemoteActivity;
import com.paramount.bed.ui.main.SleepResetActivity;
import com.paramount.bed.ui.main.UpdateFirmwareActivity;
import com.paramount.bed.ui.main.UpdateFirmwareScanActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.alarms.AlarmsScheduler;

import java.util.Calendar;

import static com.paramount.bed.util.LogUtil.Logx;

public class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {
    public static void setForegroundStatus(boolean value) {
        IsForegroundModel.clear();
        IsForegroundModel fm = new IsForegroundModel();
        fm.setStatus(value);
        fm.insert();
    }

    NSCommandReceiver nsCommandReceiver = new NSCommandReceiver();
    private static final String TAG = ApplicationLifecycleHandler.class.getSimpleName();

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        Logx(TAG, "onActivityCreated : " + activity.getClass().getSimpleName());
        BLECommandReceiver(activity, true);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Logx(TAG, "onActivityStarted : " + activity.getClass().getSimpleName());
        BLECommandReceiver(activity, true);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Logx(TAG, "onActivityResumed : " + activity.getClass().getSimpleName());
        setForegroundStatus(true);
        NotificationManager notificationManager = (NotificationManager) activity.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        BLECommandReceiver(activity, true);
//        DataBaseUtil.LogRowSize();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Logx(TAG, "onActivityPaused : " + activity.getClass().getSimpleName());
        BLECommandReceiver(activity, false);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Logx(TAG, "onActivityStopped : " + activity.getClass().getSimpleName());
        BLECommandReceiver(activity, false);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        Logx(TAG, "onActivitySaveInstanceState : " + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Logx(TAG, "onActivityDestroyed : " + activity.getClass().getSimpleName());
        BLECommandReceiver(activity, false);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(int i) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            setForegroundStatus(false);
            Log.d(TAG, "onTrimMemory: ");
        }

    }

    private void BLECommandReceiver(Activity activity, boolean isActive) {
        //List Of Activity Require BLE Connection
        Class classID = activity.getClass();
        if (classID == RegistrationStepActivity.class) {
            return;
        }
        if (classID == HomeActivity.class) {
            return;
        }
        if (classID == RemoteActivity.class) {
            return;
        }
        if (classID == AutomaticSleepOperationActivity.class) {
            return;
        }
        if (classID == AutomaticWakeOperationActivity.class) {
            return;
        }
        if (classID == RealtimeMonitorDialog.class) {
            return;
        }
        if (classID == UpdateFirmwareActivity.class) {
            return;
        }
        if (classID == UpdateFirmwareScanActivity.class) {
            return;
        }
        if (classID == SleepResetActivity.class) {
            return;
        }


        if (isActive) {
            nsCommandReceiver = NSCommandReceiver.register(activity, nsCommandReceiver);
            return;
        }
        NSCommandReceiver.unregister(activity, nsCommandReceiver);
    }
}