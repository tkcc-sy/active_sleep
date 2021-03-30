package com.paramount.bed.util;

import android.content.Context;
import android.content.Intent;

import com.paramount.bed.data.model.ForceLogoutModel;

import retrofit2.HttpException;

import static com.paramount.bed.util.TokenExpiredReceiver.BROADCAST_ACTION;

public class MultipleDeviceUtil {
    public static Boolean isTokenExpired(Throwable e) {
        if (e instanceof HttpException){
            try {
                if (((HttpException) e).code() == 401) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception x) {
                x.printStackTrace();
                return false;
            }
        }else{
            return false;
        }
    }

    public static boolean checkForceLogout(Throwable e) {
        if (e != null && MultipleDeviceUtil.isTokenExpired(e)) {
            ForceLogoutModel.tokenExpired();
            return true;
        }
        return false;
    }

    public static void sendBroadCast(Context context) {
        Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION);
        broadcast.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        context.sendBroadcast(broadcast);
    }
}
