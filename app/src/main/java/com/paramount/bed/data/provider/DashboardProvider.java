package com.paramount.bed.data.provider;

import android.content.Context;

import com.paramount.bed.data.model.ContentTNCModel;
import com.paramount.bed.data.model.DashboardModel;
import com.paramount.bed.data.model.LanguageModel;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.DashboardResponse;
import com.paramount.bed.data.remote.response.HomeContent;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.ui.front.SplashActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardProvider {

    public static void refreshContent(HomeService service, ContentProvider.ContentRefreshListener listener) {
        service.getHomeContent(0, 1).enqueue(new Callback<BaseResponse<DashboardResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<DashboardResponse>> call, Response<BaseResponse<DashboardResponse>> response) {
                BaseResponse<DashboardResponse> HomeContentBaseResponse = response.body();
                if (HomeContentBaseResponse != null) {
                    if (HomeContentBaseResponse.isSucces()) {
                        DashboardResponse data = HomeContentBaseResponse.getData();
                        DashboardModel.updateByKey(0, data.getRealtimeBed().getContent());
                        DashboardModel.updateByKey(1, data.getCalendar().getContent());
                        DashboardModel.updateByKey(2, data.getHomeWeekly().getContent());
                        DashboardModel.updateByKey(3, data.getDetailWeekly().getContent());
                        DashboardModel.updateByKey(4, data.getHome().getContent());
                        DashboardModel.updateByKey(5, data.getDetail().getContent());

                        listener.onContentHomeRefreshSuccess();
                    } else {
                        if (listener != null)
                            listener.onContentRefreshFailed(HomeContentBaseResponse.getMessage());
                    }
                } else {
                    if (listener != null) listener.onContentRefreshFailed("Server failure");
                }
            }


            @Override
            public void onFailure(Call<BaseResponse<DashboardResponse>> call, Throwable t) {
                if (listener != null) listener.onContentRefreshNetworkFailure(t);
            }
        });
    }

    public static void init(Context context) {
        try {
            InputStream isRealtime = context.getAssets().open("default/realtimebed/default_realtimebed.html");
            int size = isRealtime.available();

            byte[] buffer = new byte[size];
            isRealtime.read(buffer);
            isRealtime.close();

            String strisRealtime = new String(buffer);
            DashboardModel.updateByKey(0, strisRealtime);
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(0) == null) {
                DashboardModel.updateByKey(0, "");
            }
        }
        try {
            InputStream is = context.getAssets().open("default/calendar/default_calendar.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            DashboardModel.updateByKey(1, str);
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(1) == null) {
                DashboardModel.updateByKey(1, "");
            }
        }
        try {
            InputStream is = context.getAssets().open("default/home/default_homeweekly.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            DashboardModel.updateByKey(2, str);
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(2) == null) {
                DashboardModel.updateByKey(2, "");
            }
        }
        try {
            InputStream is = context.getAssets().open("default/home/default_detailweekly.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            DashboardModel.updateByKey(3, str);
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(3) == null) {
                DashboardModel.updateByKey(3, "");
            }
        }
        try {
            InputStream is = context.getAssets().open("default/home/default_home.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            DashboardModel.updateByKey(4, str);
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(4) == null) {
                DashboardModel.updateByKey(4, "");
            }
        }
        try {
            InputStream is = context.getAssets().open("default/home/default_detail.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            DashboardModel.updateByKey(5, str);
        } catch (IOException e1) {
            e1.printStackTrace();
            if (DashboardModel.getByKey(5) == null) {
                DashboardModel.updateByKey(5, "");
            }
        }

        try {
            InputStream is = context.getAssets().open("tnc.html");
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String str = new String(buffer);
            ContentTNCModel.clear();
            ContentTNCModel tncModel = new ContentTNCModel();
            tncModel.setData(str);
            tncModel.insert();
        } catch (IOException e1) {
            e1.printStackTrace();
            if (ContentTNCModel.getTNC() == null) {
                ContentTNCModel.clear();
                ContentTNCModel tncModel = new ContentTNCModel();
                tncModel.setData("");
                tncModel.insert();
            }
        }
    }
}
