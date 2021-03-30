package com.paramount.bed.data.remote;

import android.content.Context;
import android.content.SharedPreferences;

import com.paramount.bed.BedApplication;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.data.model.ServerModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.ServerUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static com.paramount.bed.util.LogUtil.Logx;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static int REQUEST_TIMEOUT = 15;
    private static OkHttpClient okHttpClient;
    public static Request.Builder requestBuilder;
    public static String ASS_API_KEY = "ASS-API-KEY";

    public static Retrofit getClient(Context context) {
        REQUEST_TIMEOUT = 15;
        if (ServerModel.getAll().size() != 1) {
            ServerModel.clear();
            ServerModel serverModel = new ServerModel();
            //SERVER SETTINGS
            //SERVER_ASSQC to build app run on Development Server
            //SERVER_ASAPI to build app run on Production Server
            serverModel.setUrl(ServerUtil.SERVER_ASAPI);
            serverModel.insert();
        }
        initOkHttp(context);

        retrofit = new Retrofit.Builder()
                .baseUrl(ServerModel.getHost().getUrl())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit;
    }

    public static Retrofit getClientFAQ(Context context) {
        REQUEST_TIMEOUT = 60;
        initOkHttp(context);

        retrofit = new Retrofit.Builder()
                .baseUrl(ServerModel.getHost().getUrl())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return retrofit;
    }

    private static void initOkHttp(final Context context) {

        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(interceptor);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                //Url Param
                HttpUrl originalHttpUrl = original.url();

                int user_id = LogData.getLogUserId(context);
                int company_id = LogData.getLogCompany(context);
                String email = LogData.getLogEmail(context);
                String nickname = LogData.getLogNickName(context);

                String ns_serial_number = "";
                SharedPreferences sn = BedApplication.getsApplication().getSharedPreferences("SN_NEMURI_SCAN", Context.MODE_PRIVATE);
                if (sn != null) {
                    ns_serial_number = sn.getString("SERIAL_NUMBER", "");
                }

                //Add Url Query
                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("lg_company_id", String.valueOf(company_id))
                        .addQueryParameter("lg_user_id", String.valueOf(user_id))
                        .addQueryParameter("lg_nickname", nickname)
                        .addQueryParameter("lg_email", email)
                        .addQueryParameter("lg_ns_serial_number", ns_serial_number)
                        .addQueryParameter("lg_device_type", new AndroidSystemUtil().getDeviceType())
                        .addQueryParameter("lg_os_version", new AndroidSystemUtil().getOsVersion())
                        .addQueryParameter("lg_app_type", "1")
                        .addQueryParameter("lg_appli_version", getAppVersion())
                        .build();

                RequestBody formBody = new FormBody.Builder()
                        .add("lg_company_id", String.valueOf(company_id))
                        .add("lg_user_id", String.valueOf(user_id))
                        .add("lg_email", email)
                        .add("lg_nickname", nickname)
                        .add("lg_ns_serial_number", ns_serial_number)
                        .add("lg_device_type", new AndroidSystemUtil().getDeviceType())
                        .add("lg_os_version", new AndroidSystemUtil().getOsVersion())
                        .add("lg_app_type", "1")
                        .add("lg_appli_version", getAppVersion())
                        .build();

                String postBodyString = bodyToString(original.body());
                postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);

                if (original.method().equals("POST")) {
                    requestBuilder = original.newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString))
                    ;
                } else {
                    requestBuilder = original.newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .url(url)
                    ;
                }

                // Adding Authorization token (API Key)
                // Requests will be denied without API key
                String apiToken = "";
                SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("BED_TOKEN_SECURE", Context.MODE_PRIVATE);
                if (mSettings != null) {
                    apiToken = mSettings.getString("BED_TOKEN_SECURE_KEY", "");
                }
                try {
                    requestBuilder.removeHeader("ASS-API-KEY");
                } catch (Exception e) {

                }

                if (apiToken != "" && apiToken != null) {
                    requestBuilder.addHeader("ASS-API-KEY", apiToken);
                }

                Request request = requestBuilder.build();
                Response response = null;
                try {
                    response = chain.proceed(request);
                } catch (Exception e) {
                    LogUserAction.sendApiOfflineLog(context,request);
                    throw e;
                }
                LogUserAction.sendApiLog(context,request,response);
                if (apiToken != "" && apiToken != null) {
                    if (response != null && response.code() == 401) {
                        Response responseWithFreshToken = retryWithFreshToken(request, chain);
                        return responseWithFreshToken;
                    }
                }
                Logx("ApiClientLog",
                        "Token : " + apiToken +
                                " | lg_company_id : " + company_id +
                                " | lg_user_id : " + user_id +
                                " | lg_nickname : " + nickname +
                                " | lg_email : " + email +
                                " | lg_ns_serial_number : " + "" +
                                " | lg_device_type : " + new AndroidSystemUtil().getDeviceType() +
                                " | lg_os_version : " + new AndroidSystemUtil().getOsVersion() +
                                " | lg_app_type : " + "1" +
                                " | lg_appli_version : " + getAppVersion()
                );

                return response;
            }
        });

        okHttpClient = httpClient.build();
    }

    public static String getAppVersion() {
        return String.valueOf(BuildConfig.VERSION_MAJOR) + String.valueOf(BuildConfig.VERSION_MINOR) + String.valueOf(BuildConfig.VERSION_REVISION);
    }

    private static Response retryWithFreshToken(Request req, Interceptor.Chain
            chain) throws IOException {
        String newToken = refreshToken();
        Request newRequest;
        newRequest = req.newBuilder().header("ASS-API-KEY", newToken).build();
        return chain.proceed(newRequest);
    }

    private static String refreshToken() {
        SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("BED_TOKEN_SECURE", Context.MODE_PRIVATE);
        return mSettings.getString("BED_TOKEN_SECURE_KEY", "");
    }

    public static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static class LogData {
        public static int getLoginStatus(Context context) {
            SharedPreferences monitoringUserLog = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            return monitoringUserLog.getInt("BED_USER_LOGIN_STATUS", 0);
        }

        public static String getLogEmail(Context context) {
            SharedPreferences monitoringUserLog = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            return monitoringUserLog.getString("BED_USER_EMAIL", "");
        }

        public static String getLogNickName(Context context) {
            SharedPreferences monitoringUserLog = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            return monitoringUserLog.getString("BED_USER_NICKNAME", "");
        }

        public static String getLogPhone(Context context) {
            SharedPreferences monitoringUserLog = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            return monitoringUserLog.getString("BED_USER_PHONE", "");
        }

        public static int getLogCompany(Context context) {
            SharedPreferences monitoringUserLog = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            return monitoringUserLog.getInt("BED_USER_COMPANY", 0);
        }

        public static int getLogUserId(Context context) {
            SharedPreferences monitoringUserLog = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            return monitoringUserLog.getInt("BED_USER_ID", 0);
        }

        public static void setLoginStatus(Context context, int value) {
            SharedPreferences mSettings = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt("BED_USER_LOGIN_STATUS", value);
            editor.apply();
        }

        public static void setLogEmail(Context context, String value) {
            SharedPreferences mSettings = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("BED_USER_EMAIL", value);
            editor.apply();
        }

        public static void setLogNickName(Context context, String value) {
            SharedPreferences mSettings = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("BED_USER_NICKNAME", value);
            editor.apply();
        }

        public static void setLogPhone(Context context, String value) {
            SharedPreferences mSettings = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("BED_USER_PHONE", value);
            editor.apply();
        }

        public static void setLogCompany(Context context, int value) {
            SharedPreferences mSettings = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt("BED_USER_COMPANY", value);
            editor.apply();
        }

        public static void setLogUserId(Context context, int value) {
            SharedPreferences mSettings = context.getSharedPreferences("BED_LOG_USER", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putInt("BED_USER_ID", value);
            editor.apply();
        }

        public static void setLogData(Context context, UserLogin userLogin) {
            setLogEmail(context, userLogin.getEmail());
            setLogNickName(context, userLogin.getNickname());
            setLogPhone(context, userLogin.getPhoneNumber());
            setLogCompany(context, userLogin.getCompanyId());
            setLogUserId(context, userLogin.getId());
            setLoginStatus(context, 1);
        }

        public static void clearLogData(Context context) {
            setLogEmail(context, "");
            setLogNickName(context, "");
            setLogPhone(context, "");
            setLogCompany(context, 0);
            setLogUserId(context, 0);
        }
    }
}