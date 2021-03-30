package com.paramount.bed.recorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.orhanobut.logger.Logger;
import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.main.SnoreActivity;

public class RecordingService extends Service {

    String tempFilePath;
    SnoreRecorder snoreRecorder;
    PCMRecorder recorder;

    public static final String CHANNEL_ID = "RecordingServiceChannel";

    public RecordingService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        tempFilePath = intent.getStringExtra("EXTRA_TEMP_PATH");
        Logger.d("Service Started");
        Logger.d("TEMPORARY PATH: "+tempFilePath);

        createNotificationChannel();
        Intent resultIntent = new Intent(this, SnoreActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(LanguageProvider.getLanguage("UI000560C072"))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.logo_active_sleep_notification)
                .setColor(Color.parseColor("#005263"))
                .setContentInfo("")
                .build();
        startForeground(1, notification);

        initRecorder();

        try {
            recorder.start();
        }catch (Exception e){
            e.printStackTrace();
            sendMessageError(e.getMessage());
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        try {
            recorder = new PCMRecorder(err -> sendMessageError(err));
        }catch (Exception e){
            e.printStackTrace();
            sendMessageError(e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Logger.d("Service Destroyed");
            recorder.stop();
        }catch (Exception e){
            e.printStackTrace();
            sendMessageError(e.getMessage());
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            Logger.d("Service Removed");
            this.stopSelf();
            super.onTaskRemoved(rootIntent);
        }catch (Exception e){
            e.printStackTrace();
            sendMessageError(e.getMessage());
        }
    }

    private void initRecorder(){
        try {
            recorder.config(tempFilePath,8820,1,16);
        } catch (Exception e) {
            e.printStackTrace();
            sendMessageError(e.getMessage());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Active Sleep Snoring Recording Service",
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private void sendMessageError(String errorMessage){
        Intent intent = new Intent("snore-activity");
        intent.putExtra("isError", true);
        intent.putExtra("errorMessage", errorMessage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
