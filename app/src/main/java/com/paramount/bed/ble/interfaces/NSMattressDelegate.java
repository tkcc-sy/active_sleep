package com.paramount.bed.ble.interfaces;

import com.paramount.bed.ble.pojo.NSMattressPosition;
import com.paramount.bed.ble.pojo.NSMattressStatus;

public interface NSMattressDelegate {
    void onMattressPositionReceived(NSMattressPosition mattressPosition, NSMattressStatus mattressStatus, int failCode);

    void onMattressResultReceived(boolean isSuccess);
}
