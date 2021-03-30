package com.paramount.bed.ble.interfaces;

import com.paramount.bed.ble.pojo.NSBedPosition;
import com.paramount.bed.ble.pojo.NSBedSetting;
import com.paramount.bed.ble.pojo.NSBedSpec;
import com.paramount.bed.ble.pojo.NSRealtimeFeed;

public interface NSRealtimeDelegate extends NSBaseDelegate {
    void onRealTimeFeedReceived(NSRealtimeFeed realtimeFeed);
}
