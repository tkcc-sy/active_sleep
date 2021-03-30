package com.paramount.bed.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;


import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkContinuation;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.paramount.bed.util.worker.TimerWorker;

import java.util.List;


public class TimerViewModel extends AndroidViewModel {
    private WorkManager mWorkManager;
    private LiveData<List<WorkInfo>> mSavedWorkInfo;

    public TimerViewModel(@NonNull Application application) {
        super(application);
        mWorkManager = WorkManager.getInstance();
        mSavedWorkInfo = mWorkManager.getWorkInfosForUniqueWorkLiveData("Timer");
    }

    public void deleteWork() {
        mWorkManager.pruneWork();
    }

    public LiveData<List<WorkInfo>> getOutputWorkInfo() {
        return mSavedWorkInfo;
    }

    public void startTimer() {
        OneTimeWorkRequest.Builder timerBuilder = new OneTimeWorkRequest.Builder(TimerWorker.class);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        //timerBuilder.setConstraints(constraints);
        WorkContinuation continuation = mWorkManager.beginUniqueWork("Timer", ExistingWorkPolicy.REPLACE, timerBuilder.build());
        continuation.enqueue();
    }

    public void stopTimer() {
        mWorkManager.cancelUniqueWork("Timer");
    }
}
