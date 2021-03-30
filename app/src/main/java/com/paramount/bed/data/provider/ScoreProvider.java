package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;

import com.paramount.bed.data.model.DailyScoreModel;
import com.paramount.bed.data.model.MaxRowModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.WeeklyScoreModel;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.DailyScoreResponse;
import com.paramount.bed.data.remote.response.DailyScoreStatusResponse;
import com.paramount.bed.data.remote.response.WeeklyScoreResponse;
import com.paramount.bed.data.remote.response.WeeklyScoreStatusResponse;
import com.paramount.bed.data.remote.service.UserService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.paramount.bed.util.LogUtil.Logx;

public class ScoreProvider {
    private UserService scoreProvider;
    private Context ctx;

    public ScoreProvider(Context ctx) {
        this.ctx = ctx;
        this.scoreProvider = ApiClient.getClient(ctx).create(UserService.class);
    }

    @SuppressLint("CheckResult")
    public void getDailyScore(int userId, String startDate, String endDate, DailyScoreListener listener) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String today, yesterday;
        today = getTodayString(df);
        yesterday = getYesterdayString(df);

        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.getUserDailyScoreStatus(userId, yesterday, today, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<DailyScoreStatusResponse>>>() {
                    public void onSuccess(BaseResponse<ArrayList<DailyScoreStatusResponse>> response) {
                        if (response.isSucces() && response.getData() != null && response.getData().size() > 0) {
                            DailyScoreStatusResponse data = response.getData().get(0);
                            sService.getUserDailyScore(userId, startDate, endDate, 1)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<DailyScoreResponse>>>() {
                                        public void onSuccess(BaseResponse<ArrayList<DailyScoreResponse>> response) {
                                            if (response.isSucces() && response.getData() != null) {
                                                updateDailyScore(response.getData(), data.getLastUpdate());
                                            }

                                            listener.onDailyScoreDone(startDate, endDate, true, null);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            listener.onDailyScoreDone(startDate, endDate, false, e);
                                        }
                                    });
                            return;
                        }
                        listener.onDailyScoreDone(startDate, endDate, false, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDailyScoreDone(startDate, endDate, false, e);
                    }
                });
    }

    public interface DailyScoreListener {
        void onDailyScoreDone(String startDate, String endDate, boolean isParse, Throwable e);
    }

    public void limitDailyData() {
        if (DailyScoreModel.getAll().size() > MaxRowModel.getMaxRow().getMaxRowDailyScore()) {
            DailyScoreModel dailyScoreModel = DailyScoreModel.getOldest();
            dailyScoreModel.delete();
            Logx("DashboardScore:Daily->Deleted", String.valueOf(DailyScoreModel.getAll().size()));
            limitDailyData();
        }
    }

    private void updateDailyScore(ArrayList<DailyScoreResponse> dailyScoreResponses, String dateLastUpdate) {
        limitDailyData();
        Logx("DashboardScore:Daily", String.valueOf(dailyScoreResponses.size()));
        for (int i = 0; i < dailyScoreResponses.size(); i++) {
            DailyScoreModel daily = new DailyScoreModel();
            daily.setDatePrimary(dailyScoreResponses.get(i).getDate());
            daily.setData(dailyScoreResponses.get(i).getData());
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date date = format.parse(dailyScoreResponses.get(i).getDate());
                daily.setDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date nowDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            if (dateLastUpdate == null) {
                dateLastUpdate = dateFormat.format(nowDate);
            }
            daily.setLastUpdate(dateLastUpdate);
            daily.insert();
        }
        Logx("DashboardScore:DailyLocal", String.valueOf(DailyScoreModel.getAll().size()));
    }

    @SuppressLint("CheckResult")
    public void getWeeklyScore(int userId, String startDate, String endDate, WeeklyScoreListener listener) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String currentWeek, lastWeek;
        currentWeek = getCurrentWeek(df);
        lastWeek = getLastWeek(df);

        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.getUserWeeklyScoreStatus(userId, lastWeek, currentWeek, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<WeeklyScoreStatusResponse>>>() {
                    public void onSuccess(BaseResponse<ArrayList<WeeklyScoreStatusResponse>> response) {
                        if (response.isSucces() && response.getData() != null && response.getData().size() > 0) {
                            WeeklyScoreStatusResponse data = response.getData().get(0);
                            sService.getUserWeeklyScore(userId, startDate, endDate, 1)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.io())
                                    .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<WeeklyScoreResponse>>>() {
                                        public void onSuccess(BaseResponse<ArrayList<WeeklyScoreResponse>> response) {
                                            if (response.isSucces() && response.getData() != null) {
                                                updateWeeklyScore(response.getData(), data.lastUpdate);
                                            }

                                            listener.onWeeklyScoreDone(startDate, endDate, true, null);
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            listener.onWeeklyScoreDone(startDate, endDate, false, e);
                                        }
                                    });
                            return;
                        }
                        listener.onWeeklyScoreDone(startDate, endDate, true, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onWeeklyScoreDone(startDate, endDate, false, e);
                    }
                });


    }

    public interface WeeklyScoreListener {
        void onWeeklyScoreDone(String startDate, String endDate, boolean isParse, Throwable e);
    }

    public void limitWeeklyData() {
        if (WeeklyScoreModel.getAll().size() > MaxRowModel.getMaxRow().getMaxRowWeeklyScore()) {
            WeeklyScoreModel weeklyScoreModel = WeeklyScoreModel.getOldest();
            weeklyScoreModel.delete();
            Logx("DashboardScore:Weekly->Deleted", String.valueOf(WeeklyScoreModel.getAll().size()));
            limitWeeklyData();
        }
    }

    private void updateWeeklyScore(ArrayList<WeeklyScoreResponse> weeklyScoreResponses, String lastUpdateDate) {
        limitWeeklyData();
        Logx("DashboardScore:Weekly", String.valueOf(weeklyScoreResponses.size()));
        for (int i = 0; i < weeklyScoreResponses.size(); i++) {
            WeeklyScoreModel weekly = new WeeklyScoreModel();
            weekly.setDatePrimary(weeklyScoreResponses.get(i).getStart_date() + weeklyScoreResponses.get(i).getEnd_date());
            weekly.setData(weeklyScoreResponses.get(i).getData());

            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            try {
                Date start_date_format = format.parse(weeklyScoreResponses.get(i).getStart_date());
                Date end_date_format = format.parse(weeklyScoreResponses.get(i).getEnd_date());
                weekly.setStart_date(start_date_format);
                weekly.setEnd_date(end_date_format);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date nowDate = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            if (lastUpdateDate == null) {
                lastUpdateDate = dateFormat.format(nowDate);
            }
            weekly.setLastUpdate(lastUpdateDate);
            weekly.insert();
        }
        Logx("DashboardScore:WeeklyLocal", String.valueOf(WeeklyScoreModel.getAll().size()));
    }

    @SuppressLint("CheckResult")
    public void getDailyScoreStatus(int userId, String startDate, String endDate, DailyScoreStatusListener listener) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.getUserDailyScoreStatus(userId, startDate, endDate, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<DailyScoreStatusResponse>>>() {
                    public void onSuccess(BaseResponse<ArrayList<DailyScoreStatusResponse>> response) {
                        boolean isPull = false;
                        ArrayList<DailyScoreModel> arrayLocalData = getDailyScore(startDate, endDate);
                        if (response.isSucces() && response.getData() != null && response.getData().size() > 0 && arrayLocalData.size() > 0) {
                            DailyScoreStatusResponse data = response.getData().get(0);
                            DailyScoreModel localData = arrayLocalData.get(0);
                            long dataLastUpdate = Long.parseLong(data.lastUpdate.replace("/", "").replace(" ", "").replace(":", ""));
                            long localDataLastUpdate = Long.parseLong(localData.lastUpdate.replace("/", "").replace(" ", "").replace(":", ""));

                            if (dataLastUpdate > localDataLastUpdate)
                                isPull = true;
                        }

                        listener.onDailyScoreStatusDone(startDate, endDate, true, null, isPull);
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onDailyScoreStatusDone(startDate, endDate, false, e, false);
                    }
                });
    }

    public ArrayList<DailyScoreModel> getDailyScore(String startDate, String endDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start_date_format = null;
        Date end_date_format = null;
        try {
            start_date_format = format.parse(startDate);
            end_date_format = format.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DailyScoreModel.getBetween(start_date_format, end_date_format);
    }

    public interface DailyScoreStatusListener {
        void onDailyScoreStatusDone(String startDate, String endDate, boolean isParse, Throwable e, boolean isPull);
    }

    @SuppressLint("CheckResult")
    public void getWeeklyScoreStatus(int userId, String startDate, String endDate, WeeklyScoreStatusListener listener) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.getUserWeeklyScoreStatus(userId, startDate, endDate, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<WeeklyScoreStatusResponse>>>() {
                    public void onSuccess(BaseResponse<ArrayList<WeeklyScoreStatusResponse>> response) {
                        boolean isPull = false;
                        ArrayList<WeeklyScoreModel> arrayLocalData = getWeeklyScore(startDate, endDate);
                        if (response.isSucces() && response.getData() != null && response.getData().size() > 0 && arrayLocalData.size() > 0) {
                            WeeklyScoreStatusResponse data = response.getData().get(0);
                            WeeklyScoreModel localData = arrayLocalData.get(0);
                            long dataLastUpdate = Long.parseLong(data.lastUpdate.replace("/", "").replace(" ", "").replace(":", ""));
                            long localDataLastUpdate = Long.parseLong(localData.lastUpdate.replace("/", "").replace(" ", "").replace(":", ""));

                            if (dataLastUpdate > localDataLastUpdate)
                                isPull = true;
                        }

                        listener.onWeeklyScoreStatusDone(startDate, endDate, true, null, isPull);
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onWeeklyScoreStatusDone(startDate, endDate, false, e, false);
                    }
                });
    }

    public ArrayList<WeeklyScoreModel> getWeeklyScore(String startDate, String endDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date start_date_format = null;
        Date end_date_format = null;
        try {
            start_date_format = format.parse(startDate);
            end_date_format = format.parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return WeeklyScoreModel.getBetween(start_date_format, end_date_format);
    }

    public interface WeeklyScoreStatusListener {
        void onWeeklyScoreStatusDone(String startDate, String endDate, boolean isParse, Throwable e, boolean isPull);
    }

    private String getTodayString(DateFormat df) {
        Date nowDate = new Date();
        return df.format(nowDate);
    }

    private String getYesterdayString(DateFormat df) {
        return df.format(yesterday());
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private String getCurrentWeek(DateFormat df) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return df.format(calendar.getTime());
    }

    private String getLastWeek(DateFormat df) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        calendar.add(Calendar.DATE,-7);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return df.format(calendar.getTime());
    }


    @SuppressLint("CheckResult")
    public void refreshDailyScore( DailyScoreListener listener) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String today, yesterday;
        today = getTodayString(df);
        yesterday = getYesterdayString(df);

        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        int userId = UserLogin.getUserLogin().getId();
        sService.getUserDailyScore(userId, yesterday, today, 1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(new DisposableSingleObserver<BaseResponse<ArrayList<DailyScoreResponse>>>() {
                public void onSuccess(BaseResponse<ArrayList<DailyScoreResponse>> response) {
                    if (response.isSucces() && response.getData() != null) {
                        updateDailyScore(response.getData(), null);
                    }

                    listener.onDailyScoreDone(yesterday, today, true, null);
                }

                @Override
                public void onError(Throwable e) {
                    listener.onDailyScoreDone(yesterday, today, false, e);
                }
            });
    }


}
