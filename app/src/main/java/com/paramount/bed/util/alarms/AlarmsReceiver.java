package com.paramount.bed.util.alarms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;
import com.paramount.bed.data.model.AlarmStopModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.main.AutomaticWakeOperationActivity;
import com.paramount.bed.ui.main.HomeActivity;

import java.util.Calendar;

import static com.paramount.bed.util.LogUtil.Logx;
import static com.paramount.bed.util.alarms.AlarmsScheduler.setAllAlarms;

public class AlarmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d("AlarmFlow TRIGGER");
        if (intent.getAction() != null && context != null) {
            //Set Alarm When Android Boot Completed
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                setAllAlarms(context);
                return;
            }
        }
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //Run Notification Only If : User Status Is Login & Bed Is Exist
        if (UserLogin.isUserExist() && NemuriScanModel.getBedActive()) {
            AlarmStopModel.clear();
            AlarmsScheduler.showNotification(AlarmsScheduler.getReminderCode(day), day, context, HomeActivity.class, "Paramount Bed App", LanguageProvider.getLanguage("UI000802C163"));
            String alarmTime = SettingModel.getAlarmTime(day);
            boolean alarmActive = SettingModel.getAlarmActive(day);
            System.out.println("TRCNG onReceive set alarm "+alarmTime +" - "+alarmActive);
            if(alarmTime != null) {
                AlarmsScheduler.setAlarms(
                        day,
                        alarmTime,
                        alarmActive,
                        context
                );
            }
        }
    }
}


