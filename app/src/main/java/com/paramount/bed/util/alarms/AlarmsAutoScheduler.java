package com.paramount.bed.util.alarms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.paramount.bed.R;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.front.SplashActivity;
import com.paramount.bed.ui.main.SnoreActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;
import static com.paramount.bed.util.LogUtil.Logx;


public class AlarmsAutoScheduler {
    public static final int DAILY_REMINDER_REQUEST_CODE_AUTODRIVE = 5100;
    public static final String TAG = "AlarmsScheduler";

    public static void setReminder(Context context, Class<?> cls, int hour, int min) {
        int DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_AUTODRIVE;

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.setTimeInMillis(System.currentTimeMillis());
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);

        // cancel already scheduled reminders
        cancelReminder(context, cls);

        if (setcalendar.getTimeInMillis() < System.currentTimeMillis()) {
            setcalendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        // Enable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelReminder(Context context, Class<?> cls) {
        int DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_AUTODRIVE;
        // Disable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void showNotification(Context context, Class<?> cls, String title, String content) {
        int DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_AUTODRIVE;
        Intent notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Intent.ACTION_MAIN);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(cls);
//        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);//stackBuilder.getPendingIntent(DAILY_REMINDER_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //Notification Compatible
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "N_ASS_BED_ALARM_AUTO_CHANNEL";

        //#region String For Notification Message :
        SettingModel sM = SettingModel.getSetting();
        TommorowAlarm tA = getTomorrowAlarm(sM);

        String iTitle = LanguageProvider.getLanguage("UI000802C164");
        String iMessage = LanguageProvider.getLanguage("UI000802C165");
        String iSleepStatus = sM.isAutomatic_operation_wake_active() ? LanguageProvider.getLanguage("UI000802C181") : LanguageProvider.getLanguage("UI000802C182");
        String iWakeStatus = tA.iTomorrowStatus ? LanguageProvider.getLanguage("UI000802C183") : LanguageProvider.getLanguage("UI000802C184");
        String iWakeTime = tA.iTomorrowTime;

        String iContent = LanguageProvider.getLanguage("UI000802C180")
                .replace("%SLEEP_STATUS%", iSleepStatus)
                .replace("%WAKE_STATUS%", iWakeStatus)
                .replace("%WAKE_TIME%", "(" + iWakeTime + ")");
        iMessage = iContent;
        //#endregion

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Active Sleep Notification", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription(iContent);
                notificationChannel.enableLights(true);
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

                if(!SnoreActivity.isRecording){
                    notificationChannel.setSound(sound, attributes);
                }
            } catch (Exception e) {
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("")
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(iTitle)
                .setContentText(iMessage)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(iContent))
                .setSmallIcon(R.drawable.ic_launcher)
                .setColor(Color.parseColor("#005264"))
                .setSmallIcon(R.drawable.logo_active_sleep_notification)
                .setContentIntent(pendingIntent)
                .setContentInfo("");
        if(!SnoreActivity.isRecording){
            notificationBuilder.setSound(sound);
        }
        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, notificationBuilder.build());
    }

    public static void setAllAlarms(Context context) {
        SettingModel userSetting = SettingModel.getSetting();
        String alarmTime = String.valueOf(NemuriConstantsModel.get().sleepAlarmTime == null || NemuriConstantsModel.get().sleepAlarmTime.isEmpty() ? "18:00" : NemuriConstantsModel.get().sleepAlarmTime);
        setAlarms(
                alarmTime, userSetting.automatic_operation_reminder_allowed,
                context
        );
    }

    public static void setAlarms(String time, boolean isActive, Context context) {
        String[] TIME = time.split(":");
        if (isActive) {
            AlarmsAutoScheduler.setReminder(context, AlarmsAutoDriveReceiver.class, Integer.parseInt(TIME[0]), Integer.parseInt(TIME[1]));
        } else {
            AlarmsAutoScheduler.cancelReminder(context, AlarmsAutoDriveReceiver.class);
        }
    }

    static class TommorowAlarm {
        boolean iTomorrowStatus;
        String iTomorrowTime;
    }

    public static TommorowAlarm getTomorrowAlarm(SettingModel sM) {
        TommorowAlarm tA = new TommorowAlarm();
        switch (nextAlarm("")) {
            case Calendar.SUNDAY:
                tA.iTomorrowStatus = sM.isAutomatic_operation_sleep_sunday_active();
                tA.iTomorrowTime = sM.getAutomatic_operation_sleep_sunday_time();
                break;
            case Calendar.MONDAY:
                tA.iTomorrowStatus = sM.isAutomatic_operation_sleep_monday_active();
                tA.iTomorrowTime = sM.getAutomatic_operation_sleep_monday_time();
                break;
            case Calendar.TUESDAY:
                tA.iTomorrowStatus = sM.isAutomatic_operation_sleep_tuesday_active();
                tA.iTomorrowTime = sM.getAutomatic_operation_sleep_tuesday_time();
                break;
            case Calendar.WEDNESDAY:
                tA.iTomorrowStatus = sM.isAutomatic_operation_sleep_wednesday_active();
                tA.iTomorrowTime = sM.getAutomatic_operation_sleep_wednesday_time();
                break;
            case Calendar.THURSDAY:
                tA.iTomorrowStatus = sM.isAutomatic_operation_sleep_thursday_active();
                tA.iTomorrowTime = sM.getAutomatic_operation_sleep_thursday_time();
                break;
            case Calendar.FRIDAY:
                tA.iTomorrowStatus = sM.isAutomatic_operation_sleep_friday_active();
                tA.iTomorrowTime = sM.getAutomatic_operation_sleep_friday_time();
                break;
            case Calendar.SATURDAY:
                tA.iTomorrowStatus = sM.isAutomatic_operation_sleep_saturday_active();
                tA.iTomorrowTime = sM.getAutomatic_operation_sleep_saturday_time();
                break;
        }
        return tA;
    }

    public static int nextAlarm(String timeClock) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (timeClock.isEmpty()) {
            SettingModel settingModel = SettingModel.getSetting();
            switch (day) {
                case Calendar.SUNDAY:
                    timeClock = settingModel.getAutomatic_operation_sleep_sunday_time();
                    break;
                case Calendar.MONDAY:
                    timeClock = settingModel.getAutomatic_operation_sleep_monday_time();
                    break;
                case Calendar.TUESDAY:
                    timeClock = settingModel.getAutomatic_operation_sleep_tuesday_time();
                    break;
                case Calendar.WEDNESDAY:
                    timeClock = settingModel.getAutomatic_operation_sleep_wednesday_time();
                    break;
                case Calendar.THURSDAY:
                    timeClock = settingModel.getAutomatic_operation_sleep_thursday_time();
                    break;
                case Calendar.FRIDAY:
                    timeClock = settingModel.getAutomatic_operation_sleep_friday_time();
                    break;
                case Calendar.SATURDAY:
                    timeClock = settingModel.getAutomatic_operation_sleep_saturday_time();
                    break;
            }
        }
        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date alarmTime;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = df.format(nowDate);
            Logx("TIME NOW", strDate);
            Logx("TIME ALARM", strDate + " " + timeClock + ":00");
            alarmTime = dateFormat.parse(strDate + " " + timeClock + ":00");
        } catch (ParseException e) {
            alarmTime = nowDate;
        }
        if (alarmTime.getTime() <= nowDate.getTime()) {
            day = (day + 1 > 7) ? 1 : day + 1;
        }
        return day;
    }
}