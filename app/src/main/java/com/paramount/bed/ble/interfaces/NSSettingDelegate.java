package com.paramount.bed.ble.interfaces;

import com.paramount.bed.ble.pojo.NSWifiSetting;

public interface NSSettingDelegate extends NSBaseDelegate {
    void onSetNSURLFinished(boolean isSuccess);
    void onGetWifiReceived(NSWifiSetting data);
    void onSetWifiFinished(boolean isSuccess);
}
