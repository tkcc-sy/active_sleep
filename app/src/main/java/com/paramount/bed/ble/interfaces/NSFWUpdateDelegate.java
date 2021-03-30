package com.paramount.bed.ble.interfaces;

public interface NSFWUpdateDelegate extends NSBaseDelegate {
    void onEnterFirmwareMode(boolean result);
    void onWriteFirmware(boolean result);
    void onExitFirmwareMode(boolean result);
    void onSwitchFirmwareMode(boolean result);
}
