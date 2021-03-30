package com.paramount.bed.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;

//Created by Angga Fachri Hamdani

public class WifiUtil {
    public static boolean isWifiEnable(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return false;
        } else {
            return wifi.isWifiEnabled();
        }
    }

}
