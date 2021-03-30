package com.paramount.bed.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.TimeSleepResetStatusResponse;
import com.paramount.bed.data.remote.service.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TimerUtils {
    private SharedPreferences preferences;

    public TimerUtils(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void setDuration(long timestamp) {
        preferences.edit().putLong("DURATION", timestamp).apply();
    }

    public int getTimer(){
        return preferences.getInt("STATUS_TIMER", 0);
    }

    public void setTimer(int number) {
        preferences.edit().putInt("STATUS_TIMER", number).apply();
        long total = number * 60;
        setDuration(total);
        setLastDuration(total);
    }


    public long getDuration() {
        return preferences.getLong("DURATION", 0);
    }

    public void setLastDuration(long timestamp) {
        preferences.edit().putLong("LAST_DURATION", timestamp).apply();
    }

    public long getLastDuration() {
        return preferences.getLong("LAST_DURATION", 0);
    }

    public void setTimestamp(String type, long timestamp) {
        preferences.edit().putLong(type, timestamp).apply();
    }

    public long getTimestamp(String type) {
        return preferences.getLong(type, 0);
    }

    public static String calculateTime(long seconds) {
        long hour = TimeUnit.SECONDS.toHours(seconds) - (TimeUnit.SECONDS.toDays(seconds) * 60);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
        String strMinute, strSecond;

        if (minute <= 9) {
            strMinute = "0" + minute;
        } else {
            strMinute = String.valueOf(minute);
        }

        if (second <= 9) {
            strSecond = "0" + second;
        } else {
            strSecond = String.valueOf(second);
        }

        if (hour > 0) {
            strMinute = String.valueOf((60 * hour) + minute);
        }

        return strMinute + ":" + strSecond;
    }

    public static long getTimeRemain(String time1, String time2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/mm/dd HH:mm:ss");
        Date date1 = format.parse(time1);
        Date date2 = format.parse(time2);
        long difference = (date2.getTime() - date1.getTime())/1000;
        long diff = difference/60;

        return diff;
    }

    public static Date parseDateTime(String timeString)  {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        Date date = null;
        try {
            date = format.parse(timeString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("DefaultLocale")
    public static  String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public interface ShouldShowTimeSleepCallback{
        void onResultReceived(boolean shouldShow);
    }

}
