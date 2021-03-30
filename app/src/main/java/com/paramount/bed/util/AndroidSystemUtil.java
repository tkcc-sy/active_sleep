package com.paramount.bed.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.util.List;

public class AndroidSystemUtil {

    String deviceType;
    String deviceBrand;
    String deviceModel;
    String deviceOS;
    String osVersion;

    public AndroidSystemUtil() {
        this.deviceBrand = android.os.Build.MANUFACTURER;
        this.deviceModel = android.os.Build.MODEL;
        this.deviceOS = android.os.Build.VERSION.RELEASE;
        this.deviceType = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
        this.osVersion = "Android " + android.os.Build.VERSION.RELEASE;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
