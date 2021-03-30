package com.paramount.bed.util.worker;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.paramount.bed.util.TimerUtils;

public class TimerWorker extends Worker {

    public TimerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        TimerUtils timerUtils = new TimerUtils(applicationContext);
        long duration = timerUtils.getDuration();
       /// WorkerUtils.makeStatusNotification(String.valueOf(duration), applicationContext);
        long total = duration;
        while (total > 0) {
            if (!isStopped()) {
                try {
                    long elapsed = (timerUtils.getTimestamp("END") - timerUtils.getTimestamp("START")) / 1000;
                    total = duration - elapsed;
                    timerUtils.setLastDuration(total);
                    //WorkerUtils.makeStatusNotification(TimerUtils.calculateTime(total),applicationContext);
                    Thread.sleep(1000);
                    timerUtils.setTimestamp("END", System.currentTimeMillis());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                return Result.failure();
            }
        }

        timerUtils.setLastDuration(0);

        return Result.success();
    }
}
