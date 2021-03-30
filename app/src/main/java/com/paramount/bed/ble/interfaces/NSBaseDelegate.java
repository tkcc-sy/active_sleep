package com.paramount.bed.ble.interfaces;

import com.paramount.bed.ble.NSOperation;

public interface NSBaseDelegate {
    void onCommandWritten(NSOperation command);
}
