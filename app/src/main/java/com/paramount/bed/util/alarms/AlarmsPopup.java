package com.paramount.bed.util.alarms;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.QSSleepDailyModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BaseCompatibilityScreenActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.main.SnoreActivity;
import com.paramount.bed.util.MediaPlayerUtil;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmsPopup extends BaseCompatibilityScreenActivity {
    @BindView(R.id.close)
    Button btnClose;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvDescription)
    TextView tvDescription;

    NotificationManager notificationManager;
    Vibrator vibrator;
    Boolean isFromForeground, isAutoDrive;
    public static boolean isAboutToTriggerQuestionnaire;
    public static boolean isPresenting;
    Handler stopTimeoutHandler = new Handler();
    private Runnable stopTimeoutTimer = (() -> runOnUiThread(() -> closePopup()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmsPopup.isPresenting = true;
        AlarmsPopup.isAboutToTriggerQuestionnaire = false;
        setContentView(R.layout.alarm_popup);
        ButterKnife.bind(this);
        dismissNotification();
        applyView();
        applyVibrateProfile();
        applyAudioProfile();
        stopTimeoutHandler.postDelayed(stopTimeoutTimer, 2 * 60 * 1000);
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimeoutHandler.removeCallbacks(stopTimeoutTimer);
        stopVibrate();
        MediaPlayerUtil.stopAudio();
        AlarmsScheduler.cancelNotificationToday(this);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopVibrate();
        MediaPlayerUtil.stopAudio();
        AlarmsScheduler.cancelNotificationToday(this);
        stopTimeoutHandler.removeCallbacks(stopTimeoutTimer);
        AlarmsPopup.isPresenting = false;
    }


    @Override
    public void onBackPressed() {
        return;
    }

    public void applyView() {
        tvTitle.setText(LanguageProvider.getLanguage("UI000801C001"));
        tvDescription.setText(LanguageProvider.getLanguage("UI000801C002"));
        btnClose.setText(LanguageProvider.getLanguage("UI000801C003"));
        btnClose.setEnabled(true);
        btnClose.setOnClickListener((view -> closePopup()));
    }

    private void applyVibrateProfile() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 1000, 500, 1000}, 0));
        } else {
            vibrator.vibrate(new long[]{0, 1000, 500, 1000}, 0);
        }
    }

    public void applyAudioProfile() {
        isFromForeground = Boolean.parseBoolean(getIntent().getExtras().getString("isFromForeground") == null ? "false" : getIntent().getExtras().getString("isFromForeground"));
        isAutoDrive = Boolean.parseBoolean(getIntent().getExtras().getString("isAutoDrive") == null ? "false" : getIntent().getExtras().getString("isAutoDrive"));
        if (isFromForeground && !isAutoDrive) {
            int alarmid = SettingModel.getSetting().getAutomatic_operation_alarm_id();
            if (alarmid == 1 || alarmid == 2 || alarmid == 3) {
                AudioManager AUDIOMANAGER = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = AUDIOMANAGER.getStreamMaxVolume(AUDIOMANAGER.STREAM_MUSIC);
                int curVolume = AUDIOMANAGER.getStreamVolume(AUDIOMANAGER.STREAM_MUSIC);
                if (curVolume < maxVolume) {
                    AUDIOMANAGER.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
                }
                MediaPlayerUtil.playAudio(this, alarmid == 1 ? R.raw.audio1 : alarmid == 2 ? R.raw.audio2 : alarmid == 3 ? R.raw.audio3 : 0, AudioManager.STREAM_MUSIC, true, null);
            }
        }
    }

    public void dismissNotification() {
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void closePopup() {
        btnClose.setEnabled(false);
        MediaPlayerUtil.stopAudio();
        stopVibrate();
        AlarmsScheduler.cancelNotificationToday(this);
        finish();
        Intent alarmPopup = getIntent();
        String currentScreen = alarmPopup.getExtras().getString(AlarmsSleepQuestionnaire.CURRENT_SCREEN);
        if (!isAutoDrive && !HomeActivity.isHomeActivity(currentScreen)) { //dont trigger questionnaire on home, HomeSequenceManager will do that
            AlarmsQuizModule.shouldShowSleepQuestionnaire(this, shouldShow -> {
                if (shouldShow) {
                    int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                    QSSleepDailyModel.adsShowed(day);
                    AlarmsPopup.isAboutToTriggerQuestionnaire = true;
                    Intent intent = new Intent(AlarmsPopup.this, AlarmsSleepQuestionnaire.class);
                    intent.putExtra(AlarmsSleepQuestionnaire.CURRENT_SCREEN, currentScreen);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            },0);
        }
        stopTimeoutHandler.removeCallbacks(stopTimeoutTimer);
    }

    public void stopVibrate() {
        if (vibrator != null) {
            vibrator.cancel();
        }
    }
}
