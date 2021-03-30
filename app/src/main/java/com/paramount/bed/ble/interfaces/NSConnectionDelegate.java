package com.paramount.bed.ble.interfaces;

import com.paramount.bed.ble.pojo.NSSpec;

public interface NSConnectionDelegate extends NSBaseDelegate {
    void onConnectionEstablished();
    void onDisconnect();
    void onSerialNumberReceived(String serialNumber);
    void onAuthenticationFinished(int result);
    void onNSStatusReceived(int systemStatus, int bleStatus, int wifiStatus);
    void onNSSpecReceived(NSSpec spec);
    void onConnectionStalled(int status);
}
