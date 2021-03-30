package com.paramount.bed.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.paramount.bed.data.model.ForceLogoutModel;
import com.paramount.bed.data.model.StatusLogin;

import static com.paramount.bed.util.LogUtil.Logx;

public class TokenExpiredReceiver extends BroadcastReceiver {
    public static final String BROADCAST_ACTION = "com.paramount.bed.FORCE_LOGOUT";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (StatusLogin.getUserLogin() != null && ForceLogoutModel.isTokenExpired()) {
            DialogUtil.tokenExpireDialog(context);
        }
    }

    public static TokenExpiredReceiver register(Context context, TokenExpiredReceiver tokenExpiredReceiver) {
        //#region Line For Token Expired / Force Logout
        IntentFilter tokenExpiredFilter = new IntentFilter();
        tokenExpiredFilter.addAction(TokenExpiredReceiver.BROADCAST_ACTION);
        context.registerReceiver(tokenExpiredReceiver, tokenExpiredFilter);
        return tokenExpiredReceiver;
        //#endregion
    }

    public static void unregister(Context context, TokenExpiredReceiver tokenExpiredReceiver) {
        try {
            context.unregisterReceiver(tokenExpiredReceiver);
        } catch (Exception e) {

        }
    }
}
