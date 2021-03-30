package com.paramount.bed.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class RemoteSettingUtil {
    public static void lastBedTemplate(Activity activity, int value) {
        SharedPreferences mSettings = activity.getSharedPreferences("BED_REMOTE_SETTINGS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("BED_REMOTE_SETTINGS_LAST_TEMPLATE", value);
        editor.apply();
    }

    public static int lastBedTemplate(Activity activity) {
        SharedPreferences monitoringUserLog = activity.getSharedPreferences("BED_REMOTE_SETTINGS", Context.MODE_PRIVATE);
        return monitoringUserLog.getInt("BED_REMOTE_SETTINGS_LAST_TEMPLATE", 0);
    }

    public static void lastMattressTemplate(Activity activity, int value) {
        SharedPreferences mSettings = activity.getSharedPreferences("MATTRESS_REMOTE_SETTINGS", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("MATTRESS_REMOTE_SETTINGS_LAST_TEMPLATE", value);
        editor.apply();
    }

    public static int lastMattressTemplate(Activity activity) {
        SharedPreferences monitoringUserLog = activity.getSharedPreferences("MATTRESS_REMOTE_SETTINGS", Context.MODE_PRIVATE);
        return monitoringUserLog.getInt("MATTRESS_REMOTE_SETTINGS_LAST_TEMPLATE", 0);
    }
}
