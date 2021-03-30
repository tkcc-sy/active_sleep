package com.paramount.bed.data.remote.service;

import com.paramount.bed.data.model.InquiryTypeModel;
import com.paramount.bed.data.remote.response.BaseResponse;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Alham Wa on 26/09/18
 */
public interface InquiryService {
    @FormUrlEncoded
    @POST("Inquiry")
    Single<BaseResponse<String>> submit(@Field("inquiry_type_id") Integer inquiryTypeId,
                                        @Field("product_name") String productName,
                                        @Field("email_address") String emailAddress,
                                        @Field("first_name") String firstName,
                                        @Field("last_name") String lastName,
                                        @Field("phone_number") String phoneNumber,
                                        @Field("content") String content,@Query("app_type") int app_type);

    @GET("Inquiry/type")
    Single<BaseResponse<List<InquiryTypeModel>>> getInquiryType(@Query("app_type") int app_type);

    @GET("Inquiry/product")
    Single<BaseResponse<List<String>>> getInquiryProduct(@Query("app_type") int app_type);

    @GET("Inquiry/content")
    Single<BaseResponse<String>> getTextContent(@Query("app_type") int app_type);

}
