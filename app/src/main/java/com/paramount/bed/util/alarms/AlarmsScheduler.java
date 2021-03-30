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
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.paramount.bed.R;
import com.paramount.bed.data.model.ActivityModel;
import com.paramount.bed.data.model.AlarmStopModel;
import com.paramount.bed.data.model.IsForegroundModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.PendingAlarmModel;
import com.paramount.bed.data.model.PendingQuisShowModel;
import com.paramount.bed.data.model.QSSleepDailyModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.front.SplashActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.LogUserAction;

import java.util.Calendar;
import java.util.UUID;

import static com.paramount.bed.util.LogUtil.Logx;


public class AlarmsScheduler extends HomeActivity {
    public static final int DAILY_REMINDER_REQUEST_CODE_SUNDAY = 5101;
    public static final int DAILY_REMINDER_REQUEST_CODE_MONDAY = 5102;
    public static final int DAILY_REMINDER_REQUEST_CODE_TUESDAY = 5103;
    public static final int DAILY_REMINDER_REQUEST_CODE_WEDNESDAY = 5104;
    public static final int DAILY_REMINDER_REQUEST_CODE_THURSDAY = 5105;
    public static final int DAILY_REMINDER_REQUEST_CODE_FRIDAY = 5106;
    public static final int DAILY_REMINDER_REQUEST_CODE_SATURDAY = 5107;
    public static final String TAG = "AlarmsScheduler";

    public static void setReminder(int dayofweek, Context context, Class<?> cls, int hour, int min) {
        System.out.println("ALARM_TRACE setReminder "+hour+" : "+min +" - "+dayofweek);
        NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
        if(nemuriScanModel == null){
            //cancel set reminder
            UserService userService = ApiClient.getClient(context).create(UserService.class);
            LogUserAction.sendNewLog(userService, "alarm_tracking", "setReminder cancel ns nil", "", "");
            System.out.println("ALARM_TRACE setReminder setReminder cancel ns nil");
            return;
        }
        int DAILY_REMINDER_REQUEST_CODE = getReminderCode(dayofweek);
        Calendar calendar = Calendar.getInstance();

        Calendar setcalendar = Calendar.getInstance();
        setcalendar.setTimeInMillis(System.currentTimeMillis());
        setcalendar.set(Calendar.DAY_OF_WEEK, dayofweek);
        setcalendar.set(Calendar.HOUR_OF_DAY, hour);
        setcalendar.set(Calendar.MINUTE, min);
        setcalendar.set(Calendar.SECOND, 0);

        // cancel already scheduled reminders
        cancelReminder(dayofweek, context, cls);

        if (setcalendar.getTimeInMillis() < System.currentTimeMillis()) {
            setcalendar.add(Calendar.DAY_OF_YEAR, 7);
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

    public static void cancelReminder(int dayofweek, Context context, Class<?> cls) {
        int DAILY_REMINDER_REQUEST_CODE = getReminderCode(dayofweek);
        // Disable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        intent1.putExtra("isFromAlarm", String.valueOf(true));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();

    }

    public static void showNotification(int DAILY_REMINDER_REQUEST_CODE, int dayofweek, Context context, Class<?> cls, String title, String content) {
        System.out.println("ALARM_TRACE showNotification "+title+" : "+content +" - "+dayofweek);
        NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
        if(nemuriScanModel == null){
            //cancel set showNotification
            UserService userService = ApiClient.getClient(context).create(UserService.class);
            LogUserAction.sendNewLog(userService, "alarm_tracking", "showNotification cancel ns nil", "", "");
            System.out.println("ALARM_TRACE showNotification cancel ns nil");
            return;
        }
        if (AlarmStopModel.isStopPressed(dayofweek)) {
            return;
        }
        Intent notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.putExtra("isFromAlarm", String.valueOf(true));
        notificationIntent.putExtra("isAutoDrive", String.valueOf(false));
        System.out.println("alarmTimeWake : 1 ");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(cls);
        stackBuilder.addNextIntent(notificationIntent);
        System.out.println("alarmTimeWake : 2 ");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.audio0);
        int alarmSound = SettingModel.getSetting().getAutomatic_operation_alarm_id();
        String NOTIFICATION_CHANNEL_ID = "ASS_BED_ALARM_WAKE_CHANNEL_0";
        switch (alarmSound) {
            case 0:
                sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.audio0);
                NOTIFICATION_CHANNEL_ID = "N_ASS_ALARM_WAKE_CHANNEL_0";
                break;
            case 1:
                sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.audio1short);
                NOTIFICATION_CHANNEL_ID = "N_ASS_ALARM_WAKE_CHANNEL_1";
                break;
            case 2:
                sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.audio2short);
                NOTIFICATION_CHANNEL_ID = "N_ASS_ALARM_WAKE_CHANNEL_2";
                break;
            case 3:
                sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.audio3short);
                NOTIFICATION_CHANNEL_ID = "N_ASS_ALARM_WAKE_CHANNEL_3";
                break;
            default:
                sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.audio0);
                NOTIFICATION_CHANNEL_ID = "N_ASS_ALARM_WAKE_CHANNEL_4";
        }
        AudioManager AUDIOMANAGER = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        System.out.println("alarmTimeWake : 2.1 ");
        if (AUDIOMANAGER.getRingerMode() <= AudioManager.RINGER_MODE_VIBRATE) {
            Logx("ALARMSILENT", "TRUE");
            Logx("MODE", String.valueOf(AUDIOMANAGER.getRingerMode()));
            sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.audio0);
            NOTIFICATION_CHANNEL_ID = "N_ASS_ALARM_WAKE_CHANNEL_5";
            System.out.println("alarmTimeWake : 2.2 ");
            AUDIOMANAGER.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
            System.out.println("alarmTimeWake : 2.3 ");
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                AUDIOMANAGER.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
                System.out.println("alarmTimeWake : 2.4 ");
            }
            AUDIOMANAGER.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
            System.out.println("alarmTimeWake : 2.5 ");
        } else {
            Logx("ALARMSILENT", "FALSE");
            Logx("MODE", String.valueOf(AUDIOMANAGER.getRingerMode()));
            AUDIOMANAGER.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                AUDIOMANAGER.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
            }
            AUDIOMANAGER.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        }

        System.out.println("alarmTimeWake : 3 ");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        System.out.println("alarmTimeWake : 3.1 ");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Active Sleep Alarm", NotificationManager.IMPORTANCE_HIGH);
                System.out.println("alarmTimeWake : 3.2 ");
                // Configure the notification channel.
                notificationChannel.setDescription(content);
                notificationChannel.enableLights(true);
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                System.out.println("alarmTimeWake : 3.3 ");
                notificationChannel.setSound(sound, attributes);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
                notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                System.out.println("alarmTimeWake : 3.4 ");
            } catch (Exception e) {
                System.out.println("alarmTimeWake : 3.5 " + e.getMessage());
            }
        }

        System.out.println("alarmTimeWake : 4 ");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        Intent iActionStop = new Intent(context, AlarmsStopService.class);
        iActionStop.setAction(AlarmsStopService.ACTION_STOP);
        PendingIntent piActionStop = PendingIntent.getService(context, 0, iActionStop, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("")
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .setSound(sound, AudioManager.STREAM_ALARM)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(LanguageProvider.getLanguage("UI000802C162"))
                .setContentText(content)
                .setColor(Color.parseColor("#005264"))
                .setSmallIcon(R.drawable.logo_active_sleep_notification)
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                //Important : If Want Dismiss All Notif When Swipe Left / Right Open This
                //.setDeleteIntent(piActionStop)
                .setContentInfo("")
                .addAction(0, LanguageProvider.getLanguage("UI000801C003"), piActionStop);

        System.out.println("alarmTimeWake : 5 ");
        if (isAlarmActive(dayofweek)) {
            PendingAlarmModel.clear();
            PendingAlarmModel pendingAlarmModel = new PendingAlarmModel();
            pendingAlarmModel.setPendingAlarmId(UUID.randomUUID().toString());
            pendingAlarmModel.setPendingAlarmDay(dayofweek);
            pendingAlarmModel.setPendingAlarmType(1);
            pendingAlarmModel.insert();
        }
        PendingQuisShowModel.clear();
        Notification notification = notificationBuilder.build();
//        notification.flags = Notification.FLAG_INSISTENT;
        try {
            if (AndroidSystemUtil.isAppRunning(context, context.getPackageName())) {
                //ForeGround
                if (IsForegroundModel.getForeGroundStatus().isStatus() && !AlarmStopModel.isStopPressed(dayofweek)) {
                    if (isAlarmActive(dayofweek)) {
                        PendingQuisShowModel.clear();
                        PendingQuisShowModel pendingQuisShowModel = new PendingQuisShowModel();
                        pendingQuisShowModel.setDay(0);
                        pendingQuisShowModel.setType(0);
                        pendingQuisShowModel.insert();
                        Intent i = new Intent(context.getApplicationContext(), AlarmsPopup.class);
                        i.putExtra("isFromForeground", String.valueOf(true));
                        i.putExtra("isAutoDrive", String.valueOf(false));
                        i.putExtra(AlarmsSleepQuestionnaire.CURRENT_SCREEN, context.getClass().getSimpleName());
                        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(i);
                    } else {
                        if (ActivityModel.isHomeResume()) {
                            AlarmsQuizModule.shouldShowSleepQuestionnaire(context, shouldShow -> {
                                if (shouldShow) {
                                    int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                                    QSSleepDailyModel.adsShowed(day);
                                    Intent i = new Intent(context, AlarmsSleepQuestionnaire.class);
                                    i.putExtra(AlarmsSleepQuestionnaire.CURRENT_SCREEN, context.getClass().getSimpleName());
                                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(i);
                                }
                            },0);
                        }
                    }
                } else {
                    if (isAlarmActive(dayofweek)) {
//                        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, notification);
                        repeatNotification(notificationManager, DAILY_REMINDER_REQUEST_CODE, notification, context, dayofweek, cls, title, content);
                    }
                }
            }
            //Pause
            else {
                if (isAlarmActive(dayofweek)) {
//                    notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, notification);
                    repeatNotification(notificationManager, DAILY_REMINDER_REQUEST_CODE, notification, context, dayofweek, cls, title, content);
                }
            }

        } catch (Exception e) {

        }
    }


    public static void repeatNotification(NotificationManager notificationManager, int notificationID, Notification notification, Context context, int dayofweek, Class cls, String title, String content) {
        notificationManager.notify(notificationID, notification);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AlarmStopModel.isStopPressed(dayofweek)) {
                    return;
                }
                if (notificationID < (getReminderCode(dayofweek) + 40)) {
                    showNotification(notificationID + 10, dayofweek, context, cls, title, content);
                } else {
                    int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    AlarmStopModel.stopStatusRegister(day);
                }
            }
        }, 30000);
    }

    public static int getReminderCode(int dayofweek) {
        int DAILY_REMINDER_REQUEST_CODE;
        switch (dayofweek) {
            case Calendar.SUNDAY:
                DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_SUNDAY;
                break;
            case Calendar.MONDAY:
                DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_MONDAY;
                break;
            case Calendar.TUESDAY:
                DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_TUESDAY;
                break;
            case Calendar.WEDNESDAY:
                DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_WEDNESDAY;
                break;
            case Calendar.THURSDAY:
                DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_THURSDAY;
                break;
            case Calendar.FRIDAY:
                DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_FRIDAY;
                break;
            case Calendar.SATURDAY:
                DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_SATURDAY;
                break;
            default:
                DAILY_REMINDER_REQUEST_CODE = DAILY_REMINDER_REQUEST_CODE_SUNDAY;
                break;
        }
        return DAILY_REMINDER_REQUEST_CODE;
    }

    public static void setAllAlarms(Context context) {
        setAlarms(
                Calendar.SUNDAY,
                SettingModel.getSetting().getAutomatic_operation_sleep_sunday_time(),
                SettingModel.getSetting().isAutomatic_operation_sleep_sunday_active(),
                context
        );
        setAlarms(
                Calendar.MONDAY,
                SettingModel.getSetting().getAutomatic_operation_sleep_monday_time(),
                SettingModel.getSetting().isAutomatic_operation_sleep_monday_active(),
                context
        );
        setAlarms(
                Calendar.TUESDAY,
                SettingModel.getSetting().getAutomatic_operation_sleep_tuesday_time(),
                SettingModel.getSetting().isAutomatic_operation_sleep_tuesday_active(),
                context
        );
        setAlarms(
                Calendar.WEDNESDAY,
                SettingModel.getSetting().getAutomatic_operation_sleep_wednesday_time(),
                SettingModel.getSetting().isAutomatic_operation_sleep_wednesday_active(),
                context
        );
        setAlarms(
                Calendar.THURSDAY,
                SettingModel.getSetting().getAutomatic_operation_sleep_thursday_time(),
                SettingModel.getSetting().isAutomatic_operation_sleep_thursday_active(),
                context
        );
        setAlarms(
                Calendar.FRIDAY,
                SettingModel.getSetting().getAutomatic_operation_sleep_friday_time(),
                SettingModel.getSetting().isAutomatic_operation_sleep_friday_active(),
                context
        );
        setAlarms(
                Calendar.SATURDAY,
                SettingModel.getSetting().getAutomatic_operation_sleep_saturday_time(),
                SettingModel.getSetting().isAutomatic_operation_sleep_saturday_active(),
                context
        );
    }

    public static void setAlarms(int dayOfWeek, String time, boolean isActive, Context context) {
        if(time != null) {
            String[] TIME = time.split(":");
            AlarmsScheduler.setReminder(dayOfWeek, context, AlarmsReceiver.class, Integer.parseInt(TIME[0]), Integer.parseInt(TIME[1]));
        }
    }

    public static boolean isAlarmActive(int dayofweek) {
        SettingModel settingModel = SettingModel.getSetting();
        switch (dayofweek) {
            case Calendar.SUNDAY:
                return settingModel.isAutomatic_operation_sleep_sunday_active();
            case Calendar.MONDAY:
                return settingModel.isAutomatic_operation_sleep_monday_active();
            case Calendar.TUESDAY:
                return settingModel.isAutomatic_operation_sleep_tuesday_active();
            case Calendar.WEDNESDAY:
                return settingModel.isAutomatic_operation_sleep_wednesday_active();
            case Calendar.THURSDAY:
                return settingModel.isAutomatic_operation_sleep_thursday_active();
            case Calendar.FRIDAY:
                return settingModel.isAutomatic_operation_sleep_friday_active();
            case Calendar.SATURDAY:
                return settingModel.isAutomatic_operation_sleep_saturday_active();
            default:
                return settingModel.isAutomatic_operation_sleep_sunday_active();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivityModel.setHomeResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityModel.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityModel.clear();
    }

    public static void cancelNotificationToday(Context context) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        try {
            AlarmStopModel.stopStatusRegister(day);
        } finally {
            NotificationManager notificationManager = (NotificationManager) context.getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
    }
}
