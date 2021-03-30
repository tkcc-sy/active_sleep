package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;

import com.paramount.bed.data.model.MaxRowModel;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.MaxRowResponse;
import com.paramount.bed.data.remote.service.HomeService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.paramount.bed.util.LogUtil.APITracker;

public class MaxRowProvider {

    private HomeService homeService;
    private Context ctx;

    public MaxRowProvider(Context ctx) {
        this.ctx = ctx;
        this.homeService = ApiClient.getClient(ctx).create(HomeService.class);
    }

    @SuppressLint("CheckResult")
    public void getMaxRow(MaxRowListener listener) {
        APITracker("Content/home_setting", 0, "Initializing");
        HomeService sService = ApiClient.getClient(getApplicationContext()).create(HomeService.class);
        sService.getMaxRow()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<MaxRowResponse>>() {
                    public void onSuccess(BaseResponse<MaxRowResponse> response) {
                        APITracker("Content/home_setting", 1, "onSuccess");
                        APITracker("Content/home_setting", 2, "Local Data : MAX_ROW_LOG -> " + MaxRowModel.getMaxRow().getMaxRowLog());
                        APITracker("Content/home_setting", 3, "Local Data : MAX_ROW_DAILY_SCORE -> " + MaxRowModel.getMaxRow().getMaxRowDailyScore());
                        APITracker("Content/home_setting", 4, "Local Data : MAX_ROW_WEEKLY_SCORE -> " + MaxRowModel.getMaxRow().getMaxRowWeeklyScore());
                        if (response != null && response.isSucces() && response.getData() != null) {
                            APITracker("Content/home_setting", 5, "Save To MaxRowModel");
                            MaxRowResponse fPR = response.getData();
                            MaxRowModel.clear();
                            MaxRowModel maxRowModel = new MaxRowModel();
                            maxRowModel.setMaxRowLog(fPR.getMaxRowLog());
                            maxRowModel.setMaxRowDailyScore(fPR.getMaxRowDailyScore());
                            maxRowModel.setMaxRowWeeklyScore(fPR.getMaxRowWeeklyScore());
                            maxRowModel.insert();
                            APITracker("Content/home_setting", 6, "Server Data : MAX_ROW_LOG -> " + fPR.getMaxRowLog());
                            APITracker("Content/home_setting", 7, "Server Data : MAX_ROW_DAILY_SCORE -> " + fPR.getMaxRowDailyScore());
                            APITracker("Content/home_setting", 8, "Server Data : MAX_ROW_WEEKLY_SCORE -> " + fPR.getMaxRowWeeklyScore());
                        }
                        listener.onMaxRowDone(MaxRowModel.getMaxRow());
                        APITracker("Content/home_setting", 9, "onMaxRowDone");
                    }

                    @Override
                    public void onError(Throwable e) {
                        APITracker("Content/home_setting", 10, "onError");
                        APITracker("Content/home_setting", 2, "Local Data : MAX_ROW_LOG -> " + MaxRowModel.getMaxRow().getMaxRowLog());
                        APITracker("Content/home_setting", 3, "Local Data : MAX_ROW_DAILY_SCORE -> " + MaxRowModel.getMaxRow().getMaxRowDailyScore());
                        APITracker("Content/home_setting", 4, "Local Data : MAX_ROW_WEEKLY_SCORE -> " + MaxRowModel.getMaxRow().getMaxRowWeeklyScore());
                        listener.onMaxRowDone(MaxRowModel.getMaxRow());
                    }
                });
    }

    public interface MaxRowListener {
        void onMaxRowDone(MaxRowModel maxRowModel);
    }
}
