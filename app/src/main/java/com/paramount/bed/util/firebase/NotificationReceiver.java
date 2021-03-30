package com.paramount.bed.util.firebase;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.legacy.content.WakefulBroadcastReceiver;

import com.paramount.bed.R;
import com.paramount.bed.data.model.UserLogin;

public class NotificationReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        playNotificationSound(context);
    }

    public void playNotificationSound(Context context) {
        try {
            if(UserLogin.isUserExist()) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                notification = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.birdie);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}