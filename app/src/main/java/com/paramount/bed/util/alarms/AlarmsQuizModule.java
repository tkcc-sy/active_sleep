package com.paramount.bed.util.alarms;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.service.notification.StatusBarNotification;

import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.AlarmStopModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.PendingAlarmModel;
import com.paramount.bed.data.model.PendingQuisShowModel;
import com.paramount.bed.data.model.QSSleepDailyModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.TutorialShowModel;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.UserDetailResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.main.SnoreActivity;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.NetworkUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmQuery;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.paramount.bed.util.LogUtil.Logx;

public class AlarmsQuizModule {
    public static void run(Context instance) {
        if (NemuriScanModel.get() != null) {
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<PendingQuisShowModel> query = realm.where(PendingQuisShowModel.class);
            PendingQuisShowModel result = query.findFirst();
            int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            if (result == null && AlarmsScheduler.isAlarmActive(today)) {
                NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
                if(nemuriScanModel == null){
                    //cancel alarm
                    UserService userService = ApiClient.getClient(instance).create(UserService.class);
                    LogUserAction.sendNewLog(userService, "alarm_tracking", "AlarmsQuizModule run cancel", "", "");
                    System.out.println("ALARM_TRACE AlarmsQuizModule run cance");
                    runPendingQuis(instance);
                }else {
                    NotificationManager mNotificationManager = (NotificationManager) instance.getSystemService(NOTIFICATION_SERVICE);
                    StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
                    if (notifications.length > 0) {
                        boolean isThereNotification = false;
                        for (StatusBarNotification notification : notifications) {
                            if (notification.getId() == AlarmsScheduler.getReminderCode(today)) {
                                isThereNotification = true;
                                AudioManager am = (AudioManager) instance.getSystemService(Context.AUDIO_SERVICE);
                                if (am.isMusicActive()) {
                                    Intent alarmpopup = new Intent(instance, AlarmsPopup.class);
                                    alarmpopup.putExtra("isFromForeground", String.valueOf(false));
                                    alarmpopup.putExtra("isAutoDrive", String.valueOf(false));
                                    alarmpopup.putExtra(AlarmsSleepQuestionnaire.CURRENT_SCREEN, instance.getClass().getSimpleName());
                                    alarmpopup.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    instance.startActivity(alarmpopup);
                                } else {
                                    NotificationManager notificationManager = (NotificationManager) instance.getApplicationContext()
                                            .getSystemService(NOTIFICATION_SERVICE);
                                    notificationManager.cancelAll();
                                    runPendingQuis(instance);
                                }
                            }
                        }
                        if (!isThereNotification) {
                            runPendingQuis(instance);
                        }
                    } else {
                        runPendingQuis(instance);
                    }
                }
            } else {
                runPendingQuis(instance);
            }
        } else {
            runPendingQuis(instance);
        }
    }

    public static void runPendingQuis(Context instance) {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (isAlarmIsLessThanThreeMinute() && !AlarmStopModel.isStopPressed(day) && AlarmsScheduler.isAlarmActive(day)) {
            NemuriScanModel nemuriScanModel = NemuriScanModel.getUnmanagedModel();
            if(nemuriScanModel == null){
                //cancel alarm
                UserService userService = ApiClient.getClient(instance).create(UserService.class);
                LogUserAction.sendNewLog(userService, "alarm_tracking", "AlarmsQuizModule runPendingQuis cancel", "", "");
                System.out.println("ALARM_TRACE AlarmsQuizModule runPendingQuis cance");

                if(!HomeActivity.isHomeActivity(instance.getClass().getSimpleName()) && !SnoreActivity.isRecording && !SnoreActivity.isAnalyzing){ //dont trigger questionnaire on home, HomeSequenceManager will do that
                    AlarmsQuizModule.shouldShowSleepQuestionnaire(instance, shouldShow -> {
                        if(shouldShow) {
                            QSSleepDailyModel.adsShowed(day);
                            Intent pendingQuiz = new Intent(instance, AlarmsSleepQuestionnaire.class);
                            pendingQuiz.putExtra(AlarmsSleepQuestionnaire.CURRENT_SCREEN, instance.getClass().getSimpleName());
                            pendingQuiz.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            instance.startActivity(pendingQuiz);
                        }
                    },0);
                }
            }else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent alarmpopup = new Intent(instance, AlarmsPopup.class);
                        alarmpopup.putExtra("isFromForeground", String.valueOf(true));
                        alarmpopup.putExtra("isAutoDrive", String.valueOf(false));
                        alarmpopup.putExtra(AlarmsSleepQuestionnaire.CURRENT_SCREEN, instance.getClass().getSimpleName());
                        alarmpopup.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        instance.startActivity(alarmpopup);
                    }
                }, 500);
            }
        } else if(!HomeActivity.isHomeActivity(instance.getClass().getSimpleName()) && !SnoreActivity.isRecording && !SnoreActivity.isAnalyzing){ //dont trigger questionnaire on home, HomeSequenceManager will do that
            AlarmsQuizModule.shouldShowSleepQuestionnaire(instance, shouldShow -> {
                if(shouldShow) {
                    QSSleepDailyModel.adsShowed(day);
                    Intent pendingQuiz = new Intent(instance, AlarmsSleepQuestionnaire.class);
                    pendingQuiz.putExtra(AlarmsSleepQuestionnaire.CURRENT_SCREEN, instance.getClass().getSimpleName());
                    pendingQuiz.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    instance.startActivity(pendingQuiz);
                }
            },0);
        }
    }

    private static boolean isTimeMoreOrEqualsThan(int hour, int minute) {
        return AlarmsQuizModule.isTimeMoreOrEqualsThan(String.format("%02d", hour)+":"+String.format("%02d", minute));
    }
    private static boolean isTimeMoreOrEqualsThan(String timeClock) {
        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date tresholdDate;
        long tresholdTime = 0;
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = df.format(nowDate);
            Logx("TIME NOW", strDate);
            Logx("TIME ALARM", strDate + " " + timeClock + ":00");
            tresholdDate = dateFormat.parse(strDate + " " + timeClock + ":00");
            tresholdTime = tresholdDate.getTime();
        } catch (ParseException e) {
            tresholdTime = 0;
        }
        if (nowDate.getTime() >= tresholdTime) {
            return true;
        }
        return false;
    }

    public static boolean isAlarmIsLessThanThreeMinute() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        String timeClock = "00:00";
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowDate = new Date();
        Date alarmDate;
        Date alarmDateThreshold;

        //construct alarm date
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String strDate = df.format(nowDate);
            alarmDate = dateFormat.parse(strDate + " " + timeClock + ":00");

        } catch (ParseException e) {
            alarmDate = nowDate;
        }

        //don't ring alarm if 3 minutes has elapsed
        Calendar cal = Calendar.getInstance();
        cal.setTime(alarmDate);
        cal.add(Calendar.MINUTE, 3);
        alarmDateThreshold = cal.getTime();

        Logger.d("AlarmFlow Time "+dateFormat.format(nowDate)+" - "+dateFormat.format(alarmDate)+" - "+dateFormat.format(alarmDateThreshold));
        //compare date
        return nowDate.getTime() >= alarmDate.getTime() && nowDate.getTime() < alarmDateThreshold.getTime();
    }

    @SuppressLint("CheckResult")
    public static void shouldShowSleepQuestionnaire(Context context, ShouldShowSleepQuestionnaireCallback callback, int retryCount){
        if(callback == null){
            //prevent null pointer exception
            callback = shouldShow -> {};
        }
        ShouldShowSleepQuestionnaireCallback finalCallback = callback;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        UserService userService = ApiClient.getClient(context).create(UserService.class);
        userService.getUserDetail().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<UserDetailResponse>>() {
                    public void onSuccess(BaseResponse<UserDetailResponse> response) {
                        if(response != null){
                            if(response.isSucces() && response.getData() != null){
                                UserDetailResponse data = response.getData();
                                boolean isTimePassed = AlarmsQuizModule.isTimeMoreOrEqualsThan(data.getSleepQuestionnaireMinHour(),data.getSleepQuestionnaireMinMinute());
                                //1.check time
                                if(isTimePassed) {
                                    //2.check ns availability
                                    if (!response.getData().getNsSerialNumber().isEmpty()) {
                                        //3.check network connected
                                        if(NetworkUtil.isNetworkConnected(context)){
                                            //4.check last showing & 5.make sure it's not fresh add of ASA
                                            if(!QSSleepDailyModel.isAdsShowed(day)){
                                                //platform specific, checking if the tutorial is shown or not
                                                if(!TutorialShowModel.get().getBedShowed()){
                                                    finalCallback.onResultReceived(true);
                                                    return;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            finalCallback.onResultReceived(false);
                        }else {
                            if(retryCount < BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Logger.d("SLEEP QUESTIONNAIRE: "+AlarmsQuizModule.class.getSimpleName());
                                        AlarmsQuizModule.shouldShowSleepQuestionnaire(context, finalCallback, retryCount+1);
                                    }
                                },BuildConfig.REQUEST_TIME_OUT);
                            }else{
                                finalCallback.onResultReceived(false);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount < BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Logger.d("SLEEP QUESTIONNAIRE: "+AlarmsQuizModule.class.getSimpleName());
                                    AlarmsQuizModule.shouldShowSleepQuestionnaire(context, finalCallback, retryCount+1);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            finalCallback.onResultReceived(false);
                        }
                    }
                });
    }
    public interface ShouldShowSleepQuestionnaireCallback{
        public void onResultReceived(boolean shouldShow);
    }
}
