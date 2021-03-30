package com.paramount.bed.data.provider;

import android.content.Context;
import android.util.Log;

import com.paramount.bed.data.model.ContentVersionModel;
import com.paramount.bed.data.remote.response.ContentVersionResponse;
import com.paramount.bed.data.remote.service.HomeService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentProvider {
    public enum ContentType {
        CONTENT_TUTORIAL,
        CONTENT_REALTIME_BED,
        CONTENT_NS_CONSTANTS,
        CONTENT_CALENDAR,
        CONTENT_HOME_DASHBOARD,
        CONTENT_INQUIRY,
        CONTENT_REGISTRATION_QUESTIONNAIRE,
        CONTENT_FAQ,
        CONTENT_TNC,
        CONTENT_DEVICE_TEMPLATE,
        CONTENT_HOME_DASHBOARD_WEEKLY,
        CONTENT_HOME_DETAIL_DASHBOARD_WEEKLY,
        CONTENT_SLIDER,
        CONTENT_HOME_DETAIL_DASHBOARD,
        CONTENT_LANG_US,
        CONTENT_LANG_ID,
        CONTENT_LANG_JP,
        CONTENT_APPLI_VALIDATION,
        CONTENT_INVALID
    }

    static ContentType keyToContentTypeEnum(String key) {
        switch (key) {
            case "tutorial":
                return ContentType.CONTENT_TUTORIAL;
            case "realtime_bed":
                return ContentType.CONTENT_REALTIME_BED;
            case "ns_constants":
                return ContentType.CONTENT_NS_CONSTANTS;
            case "calendar":
                return ContentType.CONTENT_CALENDAR;
            case "home":
                return ContentType.CONTENT_HOME_DASHBOARD;
            case "inquiry":
                return ContentType.CONTENT_INQUIRY;
            case "questionnaire":
                return ContentType.CONTENT_REGISTRATION_QUESTIONNAIRE;
            case "faq":
                return ContentType.CONTENT_FAQ;
            case "term_and_condition":
                return ContentType.CONTENT_TNC;
            case "device_template":
                return ContentType.CONTENT_DEVICE_TEMPLATE;
            case "home_weekly":
                return ContentType.CONTENT_HOME_DASHBOARD_WEEKLY;
            case "detail_weekly":
                return ContentType.CONTENT_HOME_DETAIL_DASHBOARD_WEEKLY;
            case "image_slider":
                return ContentType.CONTENT_SLIDER;
            case "detail":
                return ContentType.CONTENT_HOME_DETAIL_DASHBOARD;
            case "en-US":
                return ContentType.CONTENT_LANG_US;
            case "id-ID":
                return ContentType.CONTENT_LANG_ID;
            case "jp-JP":
                return ContentType.CONTENT_LANG_JP;
            case "appli-validation":
                return ContentType.CONTENT_APPLI_VALIDATION;
            default:
                return ContentType.CONTENT_INVALID;
        }
    }

    static String contentTypeEnumToString(ContentType contentType) {
        switch (contentType) {
            case CONTENT_TUTORIAL:
                return "tutorial";
            case CONTENT_REALTIME_BED:
                return "realtime_bed";
            case CONTENT_NS_CONSTANTS:
                return "ns_constants";
            case CONTENT_CALENDAR:
                return "calendar";
            case CONTENT_HOME_DASHBOARD:
                return "home";
            case CONTENT_INQUIRY:
                return "inquiry";
            case CONTENT_REGISTRATION_QUESTIONNAIRE:
                return "questionnaire";
            case CONTENT_FAQ:
                return "faq";
            case CONTENT_TNC:
                return "term_and_condition";
            case CONTENT_DEVICE_TEMPLATE:
                return "device_template";
            case CONTENT_HOME_DASHBOARD_WEEKLY:
                return "home_weekly";
            case CONTENT_HOME_DETAIL_DASHBOARD_WEEKLY:
                return "detail_weekly";
            case CONTENT_SLIDER:
                return "image_slider";
            case CONTENT_HOME_DETAIL_DASHBOARD:
                return "detail";
            case CONTENT_LANG_US:
                return "en-US";
            case CONTENT_LANG_ID:
                return "id-ID";
            case CONTENT_LANG_JP:
                return "jp-JP";
            case CONTENT_APPLI_VALIDATION:
                return "appli-validation";
            default:
                return "";
        }
    }

    static int getLastUpdateOf(ContentType contentType) {
        String keyString = contentTypeEnumToString(contentType);
        ContentVersionModel contentVersionModel = ContentVersionModel.getByKey(keyString);
        if (contentVersionModel != null) {
            return contentVersionModel.getLastUpdated();
        }
        return 0;

    }

    public static void refreshContent(Context context, HomeService service, ContentRefreshListener listener) {
        service.getVersionContent(1).enqueue(new Callback<ContentVersionResponse>() {
            @Override
            public void onResponse(Call<ContentVersionResponse> call, Response<ContentVersionResponse> response) {
                ContentVersionResponse contentVersionResponse = response.body();
                if (contentVersionResponse != null) {
                    if (contentVersionResponse.isSucces()) {
                        ContentVersionModel[] data = contentVersionResponse.getData();
                        for (ContentVersionModel contentVersion : data
                        ) {
                            Log.d("CONTENT_VERSIONING", "Start checking content versions of " + contentVersion.getKey());
                            ContentType contentType = ContentProvider.keyToContentTypeEnum(contentVersion.getKey());
                            switch (contentType) {
                                case CONTENT_LANG_US:
                                    Log.d("CONTENT_VERSIONING", "LANG US last " + getLastUpdateOf(ContentType.CONTENT_LANG_US) + " content version " + contentVersion.getLastUpdated()
                                    );
                                    if (getLastUpdateOf(ContentType.CONTENT_LANG_US) < contentVersion.getLastUpdated()) {
                                        Log.d("CONTENT_VERSIONING", "updating LANG US");
                                        LanguageProvider.refreshContent(service, new ContentRefreshListener() {
                                            @Override
                                            public void onContentSliderRefreshSuccess() {

                                            }

                                            @Override
                                            public void onContentLanguageRefreshSuccess() {
                                                ContentVersionModel.getByKey(contentVersion.getKey()).delete();
                                                contentVersion.insert();
                                                listener.onContentLanguageRefreshSuccess();
                                            }

                                            @Override
                                            public void onContentRefreshFailed(String message) {
                                                listener.onContentRefreshFailed(message);
                                            }

                                            @Override
                                            public void onContentRefreshNetworkFailure(Throwable t) {
                                                listener.onContentRefreshNetworkFailure(t);
                                            }

                                            @Override
                                            public void onContentHomeRefreshSuccess() {

                                            }
                                        }, "en-US");
                                    } else {
                                        listener.onContentLanguageRefreshSuccess();
                                    }
                                    break;
                                case CONTENT_LANG_JP:
                                    Log.d("CONTENT_VERSIONING", "LANG JP last " + getLastUpdateOf(ContentType.CONTENT_LANG_JP) + " content version " + contentVersion.getLastUpdated()
                                    );
                                    if (getLastUpdateOf(ContentType.CONTENT_LANG_JP) < contentVersion.getLastUpdated()) {
                                        Log.d("CONTENT_VERSIONING", "updating LANG JP");
                                        LanguageProvider.refreshContent(service, new ContentRefreshListener() {
                                            @Override
                                            public void onContentSliderRefreshSuccess() {
                                            }

                                            @Override
                                            public void onContentLanguageRefreshSuccess() {
                                                ContentVersionModel.getByKey(contentVersion.getKey()).delete();
                                                contentVersion.insert();
                                                listener.onContentLanguageRefreshSuccess();
                                            }

                                            @Override
                                            public void onContentRefreshFailed(String message) {
                                                listener.onContentRefreshFailed(message);
                                            }

                                            @Override
                                            public void onContentRefreshNetworkFailure(Throwable t) {
                                                listener.onContentRefreshNetworkFailure(t);
                                            }

                                            @Override
                                            public void onContentHomeRefreshSuccess() {

                                            }
                                        }, "jp-JP");
                                    } else {
                                        listener.onContentLanguageRefreshSuccess();
                                    }
                                    break;
                                case CONTENT_LANG_ID:
                                    Log.d("CONTENT_VERSIONING", "LANG ID last " + getLastUpdateOf(ContentType.CONTENT_LANG_ID) + " content version " + contentVersion.getLastUpdated()
                                    );
                                    if (getLastUpdateOf(ContentType.CONTENT_LANG_ID) < contentVersion.getLastUpdated()) {
                                        Log.d("CONTENT_VERSIONING", "updating LANG ID");
                                        LanguageProvider.refreshContent(service, new ContentRefreshListener() {
                                            @Override
                                            public void onContentSliderRefreshSuccess() {
                                            }

                                            @Override
                                            public void onContentLanguageRefreshSuccess() {
                                                ContentVersionModel.getByKey(contentVersion.getKey()).delete();
                                                contentVersion.insert();
                                                listener.onContentLanguageRefreshSuccess();
                                            }

                                            @Override
                                            public void onContentRefreshFailed(String message) {
                                                listener.onContentRefreshFailed(message);
                                            }

                                            @Override
                                            public void onContentRefreshNetworkFailure(Throwable t) {
                                                listener.onContentRefreshNetworkFailure(t);
                                            }

                                            @Override
                                            public void onContentHomeRefreshSuccess() {

                                            }
                                        }, "id-ID");
                                    } else {
                                        listener.onContentLanguageRefreshSuccess();
                                    }
                                    break;
                                case CONTENT_SLIDER:
                                    Log.d("CONTENT_VERSIONING", "SLIDER last " + getLastUpdateOf(ContentType.CONTENT_SLIDER) + " content version " + contentVersion.getLastUpdated()
                                    );
                                    if (getLastUpdateOf(ContentType.CONTENT_SLIDER) < contentVersion.getLastUpdated()) {
                                        Log.d("CONTENT_VERSIONING", "updating SLIDER");
                                        SliderProvider.refreshContent(context, service, new ContentRefreshListener() {
                                            @Override
                                            public void onContentSliderRefreshSuccess() {
                                                ContentVersionModel.getByKey(contentVersion.getKey()).delete();
                                                contentVersion.insert();
                                                listener.onContentSliderRefreshSuccess();
                                            }

                                            @Override
                                            public void onContentLanguageRefreshSuccess() {
                                            }

                                            @Override
                                            public void onContentRefreshFailed(String message) {
                                                listener.onContentRefreshFailed(message);
                                            }

                                            @Override
                                            public void onContentRefreshNetworkFailure(Throwable t) {
                                                listener.onContentRefreshNetworkFailure(t);
                                            }

                                            @Override
                                            public void onContentHomeRefreshSuccess() {

                                            }
                                        });

                                    } else {
                                        listener.onContentSliderRefreshSuccess();
                                    }
                                    break;
                                case CONTENT_HOME_DASHBOARD:
                                    Log.d("CONTENT_VERSIONING", "CONTENT HOME last " + getLastUpdateOf(ContentType.CONTENT_HOME_DASHBOARD) + " content version " + contentVersion.getLastUpdated()
                                    );
                                    if (getLastUpdateOf(ContentType.CONTENT_HOME_DASHBOARD) < contentVersion.getLastUpdated()) {
                                        Log.d("CONTENT_VERSIONING", "updating HOME DASHBOARD");
                                        DashboardProvider.refreshContent(service, new ContentRefreshListener() {
                                            @Override
                                            public void onContentSliderRefreshSuccess() {

                                            }

                                            @Override
                                            public void onContentLanguageRefreshSuccess() {

                                            }

                                            @Override
                                            public void onContentRefreshFailed(String message) {

                                            }

                                            @Override
                                            public void onContentRefreshNetworkFailure(Throwable t) {

                                            }

                                            @Override
                                            public void onContentHomeRefreshSuccess() {
                                                ContentVersionModel.getByKey(contentVersion.getKey()).delete();
                                                contentVersion.insert();
                                                listener.onContentHomeRefreshSuccess();

                                            }
                                        });

                                    } else {
                                        listener.onContentHomeRefreshSuccess();
                                    }
                                    break;
                            }
                        }
                    } else {
                        if (listener != null)
                            listener.onContentRefreshFailed(contentVersionResponse.getMessage());
                    }
                } else {
                    if (listener != null) listener.onContentRefreshFailed("Server failure");
                }
            }

            @Override
            public void onFailure(Call<ContentVersionResponse> call, Throwable t) {
                if (listener != null) listener.onContentRefreshNetworkFailure(t);
            }
        });
    }

    public interface ContentRefreshListener {
        public void onContentSliderRefreshSuccess();

        public void onContentLanguageRefreshSuccess();

        public void onContentRefreshFailed(String message);

        public void onContentRefreshNetworkFailure(Throwable t);

        public void onContentHomeRefreshSuccess();


    }
}
