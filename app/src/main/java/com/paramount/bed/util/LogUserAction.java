package com.paramount.bed.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.paramount.bed.data.model.LogUserModel;
import com.paramount.bed.data.model.MaxRowModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.data.remote.service.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.paramount.bed.util.LogUtil.Logx;

public class LogUserAction {


    public static void sendKickLog(UserService userService, String key, String screenId) {
        UserLogin userLogin = UserLogin.getUserLogin();
        if (userLogin != null && userLogin.getEmail() != null && userLogin.getPhoneNumber() != null) {
            String email = userLogin.getEmail();
            String phone = userLogin.getPhoneNumber();
            String serial = userLogin.getScanSerialNumber() != null ? userLogin.getScanSerialNumber() : "";
            sendNewLog(userService, key, email + ":" + phone + ":" + serial, serial, screenId);
        }
    }

    public static void sendApiLog(Context ctx, Request request, Response response) {
        String endpoint = "";
        String errCode = "";
        HttpUrl requestUrl = null;
        int httpCode = 0;

        if(request != null){
            requestUrl = request.url();
            endpoint = requestUrl.encodedPath();
        }
        if(response != null) {
            httpCode = response.code();
            if (response.body() != null) {
                try {
                    ResponseBody responseBodyCopy = response.peekBody(Long.MAX_VALUE);
                    String responseString = responseBodyCopy.string();
                    JSONObject obj = new JSONObject(responseString);
                    //success can be integer or boolean
                    int successStatus = obj.optInt("success",obj.optBoolean("success",false) ? 1 : 0);

                    if(successStatus != 1){
                        String errorCode = obj.optString("error_code",obj.optString("error",""));
                        String message = obj.optString("message","");
                        if(errorCode != null && !errorCode.isEmpty()){
                            errCode = errorCode;
                        }else if(message != null && !message.isEmpty()){
                            errCode = message;
                        }
                        LogUserAction.sendApiRespnseFailedLog(ctx,endpoint,httpCode,errCode,requestUrl);
                    }
                    return;
                } catch (JSONException | IOException e) {
                    LogUserAction.sendApiFailedLog(ctx,endpoint,httpCode,errCode,requestUrl);
                    return;
                }
            }
        }
        LogUserAction.sendApiFailedLog(ctx,endpoint,httpCode,errCode,requestUrl);
    }
    public static void sendApiFailedLog(Context ctx,String endpoint, int httpCode, String errCode, HttpUrl url){
        boolean shouldLog = !LogUserAction.shouldSkipApiLog(url);
        if(shouldLog) {
            UserService userService = ApiClient.getClient(ctx).create(UserService.class);
            String payloadString = LogUserAction.buildApiResultPayload(endpoint, httpCode, errCode);
            LogUserAction.sendNewLog(userService, "API_ACCESS_FAILED", payloadString, "", "");
        }

    }
    public static void sendApiRespnseFailedLog(Context ctx,String endpoint, int httpCode, String errCode, HttpUrl url){
        boolean shouldLog = !LogUserAction.shouldSkipApiLog(url);
        if(shouldLog) {
            UserService userService = ApiClient.getClient(ctx).create(UserService.class);
            String payloadString = LogUserAction.buildApiResultPayload(endpoint, httpCode, errCode);
            LogUserAction.sendNewLog(userService, "API_RESPONSE_FAILED", payloadString, "", "");
        }

    }
    public static void sendApiOfflineLog(Context ctx, Request request) {
        UserService userService = ApiClient.getClient(ctx).create(UserService.class);
        String endpoint = "";
        boolean shouldLog = false;
        if(request != null){
            endpoint = request.url().encodedPath();
            shouldLog = !LogUserAction.shouldSkipApiLog(request.url());
        }
        if(shouldLog) {
            endpoint += ",INTERNET_CONNECTION_FAILED";
            String payloadString = LogUserAction.buildApiResultPayload(endpoint, 0, "");
            LogUserAction.sendNewLog(userService, "API_ACCESS_OFFLINE", payloadString, "", "");
        }
    }

    public static boolean shouldSkipApiLog(HttpUrl url){
        boolean shouldSkip = true;
        if(url != null){
            List<String> pathSegment = url.encodedPathSegments();
            if(pathSegment.size() > 0){
                String lastPath = pathSegment.get(pathSegment.size()-1);
                if(!lastPath.equalsIgnoreCase("log_action")){
                    shouldSkip = false;
                }
            }
        }
        return shouldSkip;
    }

    public static String buildApiResultPayload(String endpoint, int httpCode, String errCode){
        ArrayList<String> payloadList = new ArrayList<>();
        if(endpoint != null && !endpoint.isEmpty()) {
            payloadList.add(endpoint);
        }

        payloadList.add(String.valueOf(httpCode));

        if(errCode != null && !errCode.isEmpty()) {
            payloadList.add(errCode);
        }
        payloadList.add(errCode);
        String payloadString = Arrays.toString(payloadList.toArray());
        return payloadString;
    }

    public static void sendNewLog(UserService userService, String key, String value, String serial, String screenId) {
        try {
            String serialNumber = "";
            try {
                serialNumber = NemuriScanModel.get().getSerialNumber() == null ? "" : NemuriScanModel.get().getSerialNumber();
            } catch (Exception e) {
                serialNumber = "";
            }
            if (UserLogin.getUserLogin() != null) {
                LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), key, value, new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), serialNumber, screenId);
            }
        } catch (Exception e) {

        }
    }

    public static void InsertLog(UserService userService, String userId, String key, String value, String deviceType, String osVersion, String nemuriScanSN, String screenId) {
        userService.logAction(userId, key, value, deviceType, osVersion, nemuriScanSN, screenId, screenId, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (response.isSucces()) {
                            Logx("LogUserModel", "Log Uploadeded ->" + LogUserModel.getAll().size());
                            return;
                        }
                        storeLogInRealm(userId, key, value, deviceType, osVersion, nemuriScanSN, screenId);
                    }

                    @Override
                    public void onError(Throwable e) {
                        storeLogInRealm(userId, key, value, deviceType, osVersion, nemuriScanSN, screenId);
                    }
                });
    }

    public static void limitLogData() {
        if (LogUserModel.getAll().size() >= MaxRowModel.getMaxRow().getMaxRowLog()) {
            LogUserModel logUserModel = LogUserModel.getOldest();
            if(logUserModel != null) {
                logUserModel.delete();
                Logx("LogUserModel:->DeletedByOverLimit", String.valueOf(LogUserModel.getAll().size()));
                limitLogData();
            }
        }
    }

    public static void storeLogInRealm(String userId, String key, String value, String deviceType, String osVersion, String nemuriScanSN, String screenId) {
        limitLogData();
        LogUserModel logUserModel = new LogUserModel();
        logUserModel.setId(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        logUserModel.setUserId(userId == null || userId.equals("") ? "0" : userId);
        logUserModel.setKey(key);
        logUserModel.setDeviceType(deviceType);
        logUserModel.setOsVersion(osVersion);
        logUserModel.setNemuriScanSN(nemuriScanSN);
        logUserModel.setScreenId(screenId);
        logUserModel.setValue(value);
        logUserModel.setStatus(false);
        logUserModel.insert();
        Logx("LogUserModel", "Total Row->" + LogUserModel.getAll().size());
    }

    public static void sendPendingLogAction(Activity activity) {
        Logx("LogUserModel", "Send Pending Log->" + LogUserModel.getAll().size());
        if (!activity.isFinishing()) {
            if (!NetworkUtil.isNetworkConnected(activity.getApplicationContext())) {
                return;
            }
            LogUserModel logUserModel = LogUserModel.getFirst();
            if (logUserModel != null) {
                UserService userService = ApiClient.getClient(activity.getApplicationContext()).create(UserService.class);
                userService.logAction(logUserModel.getUserId(), logUserModel.getKey(), logUserModel.getValue(), logUserModel.getDeviceType(), logUserModel.getOsVersion(), logUserModel.getNemuriScanSN(), logUserModel.getScreenId(), logUserModel.getScreenId(), 1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                            @Override
                            public void onSuccess(BaseResponse<String> response) {
                                if (response.isSucces()) {
                                    logUserModel.delete();
                                    Logx("LogUserModel", "Deleted->" + LogUserModel.getAll().size());
                                    if (!activity.isFinishing()) {
                                        sendPendingLogAction(activity);
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        });
            }
        }
    }
}