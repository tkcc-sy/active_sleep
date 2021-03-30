package com.paramount.bed.util.homesequence;

import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.util.AppUpdaterUtil;

public class AppUpdateSequence extends HomeSequenceItem {
    public AppUpdateSequence(HomeSequenceManager sequenceManager, SequenceDelegate delegate) {
        super(sequenceManager, delegate);
    }

    @Override
    public void execute() {
        super.execute();
        HomeService homeService = ApiClient.getClient(getHomeActivityRef()).create(HomeService.class);
        AppUpdaterUtil.checkVersionSequence(AppUpdaterUtil.ANDROID_APPLICATION_TYPE_BED, homeService, homeActivityRef, new AppUpdaterUtil.AppListener() {
            @Override
            public void start() {
            }

            @Override
            public void finish() {
                end();
            }
        });
    }
}
