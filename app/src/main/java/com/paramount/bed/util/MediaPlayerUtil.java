package com.paramount.bed.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;

import com.paramount.bed.ui.main.AutomaticWakeOperationActivity;

public class MediaPlayerUtil {
    public static MediaPlayer mediaPlayer;
    public static boolean isplayingAudio = false;
    public static Context context;

    public static void playAudio(Context c, int id, int streamType, boolean isLooping, MediaPlayer.OnCompletionListener listener) {
        try {
            context = c;
            mediaPlayer = MediaPlayer.create(context, id);
            if (!mediaPlayer.isPlaying()) {
                if (streamType == AudioManager.STREAM_ALARM) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build());
                    }
                }
                mediaPlayer.setAudioStreamType(streamType);
                isplayingAudio = true;
                mediaPlayer.setLooping(isLooping);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(listener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean playAudioFromPath(Context c, String path, int streamType, boolean isLooping, MediaPlayer.OnCompletionListener listener) {
        try {
            context = c;
            mediaPlayer = MediaPlayer.create(context, Uri.parse(path));
            if (!mediaPlayer.isPlaying()) {
                if (streamType == AudioManager.STREAM_ALARM) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build());
                    }
                }
                mediaPlayer.setAudioStreamType(streamType);
                isplayingAudio = true;
                mediaPlayer.setLooping(isLooping);
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(listener);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void stopAudio() {
        isplayingAudio = false;
        if (mediaPlayer == null) {
            return;
        }
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
