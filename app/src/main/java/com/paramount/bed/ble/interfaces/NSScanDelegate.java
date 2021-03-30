package com.paramount.bed.ble.interfaces;

import android.bluetooth.le.ScanResult;

public interface NSScanDelegate extends NSBaseDelegate {
    void onStartScan();

    void onLocationPermissionDenied();

    void onLocationServiceDisabled();

    void onCancelScan();

    void onStopScan();

    void onScanResult(ScanResult scanResult);
}