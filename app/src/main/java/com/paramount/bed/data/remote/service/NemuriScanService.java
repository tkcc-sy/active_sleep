package com.paramount.bed.data.remote.service;

import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.NemuriConstantsResponse;
import com.paramount.bed.data.remote.response.NemuriScanAddResponse;
import com.paramount.bed.data.remote.response.NemuriScanCheckResponse;
import com.paramount.bed.data.remote.response.NemuriScanDetailResponse;
import com.paramount.bed.data.remote.response.SliderResponse;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NemuriScanService {
    @GET("NemuriScan/Constants")
    Single<NemuriConstantsResponse> getNemuriConstants();

    @FormUrlEncoded
    @POST("NemuriScan/check")
    Single<NemuriScanCheckResponse> validateSerialNumber(@Field("ns_serial_number") String nsSerialNumber,
                                                         @Field("user_id") int userId, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("NemuriScan/delete")
    Single<BaseResponse> deleteNemuriScan(@Field("user_id") int userId, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("NemuriScan/add")
    Single<NemuriScanAddResponse> addNemuriScan(@Field("ns_serial_number") String nsSerialNumber,
                                                @Field("user_id") int userId, @Query("app_type") int app_type,
                                                @Field("major") int major, @Field("minor") int minor, @Field("revision") int revision);

    @GET("NemuriScan/detail")
    Single<BaseResponse<NemuriScanDetailResponse>> getNemuriScanDetail(@Query("ns_serial_number") String nsSerialNumber, @Query("end_user_id") int userId);

}
