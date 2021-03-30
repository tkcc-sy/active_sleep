package com.paramount.bed.ble.interfaces;

import com.paramount.bed.ble.pojo.NSBedPosition;
import com.paramount.bed.ble.pojo.NSBedSetting;
import com.paramount.bed.ble.pojo.NSBedSpec;
import com.paramount.bed.ble.pojo.NSRealtimeFeed;

public interface NSBedDelegate extends NSBaseDelegate {
    void onBedSpecReceived(NSBedSpec bedSpec);

    void onBedPositionReceived(NSBedPosition bedPosition,int failCode);

    void onBedFreePositionReceived(NSBedPosition bedPosition,int failCode,int buttonCode,boolean isButtonCodeMatched);

    void onBedPresetPositionReceived(NSBedPosition bedPosition,int failCode,int buttonCode,boolean isButtonCodeMatched);

    void onBedSettingReceived(NSBedSetting bedSetting);

    void onBedSettingResult();
}
