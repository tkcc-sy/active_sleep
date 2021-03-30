package com.paramount.bed.util.deeplink;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.paramount.bed.ui.front.SplashActivity;

public class DeeplinkActivity extends AppCompatActivity {
//    CircularProgressView circularProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        if (isTaskRoot()) {
            Intent i = new Intent(DeeplinkActivity.this, SplashActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            int sizeStack = am.getRunningTasks(1).size();
            for (int i = 0; i < sizeStack; i++) {
                ComponentName cn = am.getRunningTasks(1).get(i).topActivity;
                Intent intent = new Intent();
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                startActivity(intent);
            }
            finish();
        }
    }
}
