package com.paramount.bed.util.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.ui.main.HomeActivity;

public class AlarmsAutoDriveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && context != null) {
            //Set Alarm When Android Boot Completed
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                AlarmsAutoScheduler.setAllAlarms(context);
                return;
            }
        }
        //Run Notification Only If : User Status Is Login & Bed Is Exist
        if (UserLogin.isUserExist() && NemuriScanModel.getBedActive()) {
            AlarmsAutoScheduler.showNotification(context, HomeActivity.class, "Paramount Bed App", "");
            AlarmsAutoScheduler.setAllAlarms(context);
        }
    }
}


