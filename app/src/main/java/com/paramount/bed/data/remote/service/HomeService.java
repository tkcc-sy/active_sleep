package com.paramount.bed.data.remote.service;

import com.paramount.bed.data.model.LanguageModel;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.ContentVersionResponse;
import com.paramount.bed.data.remote.response.DashboardResponse;
import com.paramount.bed.data.remote.response.DeviceTemplateResponse;
import com.paramount.bed.data.remote.response.FAQLinkResponse;
import com.paramount.bed.data.remote.response.FormPolicyResponse;
import com.paramount.bed.data.remote.response.MaxRowResponse;
import com.paramount.bed.data.remote.response.NemuriConstantsResponse;
import com.paramount.bed.data.remote.response.NemuriScanCheckResponse;
import com.paramount.bed.data.remote.response.NewsHomeResponse;
import com.paramount.bed.data.remote.response.NewsResponse;
import com.paramount.bed.data.remote.response.SliderResponse;
import com.paramount.bed.data.remote.response.TutorialResponse;
import com.paramount.bed.data.remote.response.VersionResponse;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HomeService {
    @GET("Content/faq")
    Single<BaseResponse<String>> getFaqContent(@Query("app_type") int app_type);

    @GET("Content/form_policy")
    Single<BaseResponse<FormPolicyResponse>> getFormPolicy();
    //TODO:MaxRow End Point API
    @GET("Content/home_setting")
    Single<BaseResponse<MaxRowResponse>> getMaxRow();

    @GET("Content/termofservice")
    Single<BaseResponse<String>> getTNCContent(@Query("company_id") int company_id, @Query("app_type") int app_type);

    @GET("Content/termofservice_update")
    Single<BaseResponse<String>> getTNCAppUpdateContent(@Query("app_type") int app_type);

    @GET("Content/inquiry")
    Single<BaseResponse<String>> getInquiryContent(@Query("app_type") int app_type);

    @GET("Content/version")
    Call<ContentVersionResponse> getVersionContent(@Query("app_type") int app_type);

    @GET("Content/slider")
    Call<SliderResponse> getSlider(@Query("app_type") int app_type);

    @GET("Content/language")
    Call<BaseResponse<LanguageModel[]>> getLanguage(@Query("language_code") String languageCode, @Query("app_type") int app_type);

    @GET("Content/home_android")
    Call<BaseResponse<DashboardResponse>> getHomeContent(@Query("id") int id, @Query("app_type") int app_type);

    @GET("Content/tutorial")
    Call<TutorialResponse> getTutorial(@Query("app_type") int app_type);

    @GET("Content/device_template")
    Call<BaseResponse<DeviceTemplateResponse>> getDeviceTemplate(@Query("user_id") int user_id, @Query("app_type") int app_type);

    @GET("Content/appli_version")
    Single<BaseResponse<VersionResponse>> getVersionApp(@Query("type") Integer type, @Query("app_type") int app_type);

    @GET("NemuriScan/Constants")
    Call<NemuriConstantsResponse> getNemuriConstants();

    @GET("Content/faq_link")
    Single<BaseResponse<ArrayList<FAQLinkResponse>>> getFAQLink();

    @GET("Content/firmwareintro")
    Single<BaseResponse<String>> getFWUpdateIntro();

    @GET("News/top_news_detail")
    Observable<BaseResponse<NewsHomeResponse>> getTopNewsHome(@Query("user_id") int user_id);

    @GET("News/top_news_list")
    Observable<BaseResponse<String>> getTopNewsList(@Query("user_id") int user_id);

    @GET("News/top_news_single")
    Observable<BaseResponse<NewsResponse>> getTopNewsSingle(@Query("user_id") int user_id, @Query("news_id") int news_id);

    @FormUrlEncoded
    @POST("News/top_news_view_log")
    Observable<BaseResponse<String>> logNews(@Field("user_id") int userId,@Field("news_id") int newsId);
}
