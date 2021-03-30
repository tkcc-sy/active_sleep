package com.paramount.bed.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.paramount.bed.ble.NSManager;
import com.paramount.bed.data.model.ForceLogoutModel;
import com.paramount.bed.data.model.StatusLogin;

import static com.paramount.bed.util.LogUtil.Logx;
import static com.paramount.bed.util.TokenExpiredReceiver.BROADCAST_ACTION;

public class NSCommandReceiver extends BroadcastReceiver {
    public static final String NS_COMMAND_RECEIVER = "com.paramount.bed.NS_COMMAND_RECEIVER";
    private static final String TAG = ApplicationLifecycleHandler.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        NSManager nsManager = NSManager.instance;
        Logx(TAG, "onCommandReceived : ");
        if (intent.getAction() != null && nsManager != null && nsManager.isBLEReady()) {
            if (intent.getAction().equals(NS_COMMAND_RECEIVER)) {
                nsManager.disconnectCurrentDevice();
            }
        }
    }

    public static NSCommandReceiver register(Context context, NSCommandReceiver receiver) {
        Logx(TAG, "onReceiverStart : ");
        //#region Line For Token Expired / Force Logout
        IntentFilter nsCommandReceiver = new IntentFilter();
        nsCommandReceiver.addAction(NSCommandReceiver.NS_COMMAND_RECEIVER);
        context.registerReceiver(receiver, nsCommandReceiver);
        return receiver;
        //#endregion
    }

    public static void unregister(Context context, NSCommandReceiver tokenExpiredReceiver) {
        Logx(TAG, "onReceiverStop : ");
        try {
            context.unregisterReceiver(tokenExpiredReceiver);
        } catch (Exception e) {

        }
    }

    public static void sendCommand(Context context, byte commandCode) {
        Logx(TAG, "onCommandFire : ");
        Intent broadcast = new Intent();
        broadcast.setAction(NS_COMMAND_RECEIVER);
        broadcast.putExtra("commandCode", commandCode);
        broadcast.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(broadcast);
    }
}
