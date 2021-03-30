package com.paramount.bed.data.remote.service;

import com.paramount.bed.data.model.MattressSettingModel;
import com.paramount.bed.data.remote.response.ActivationEmailResponse;
import com.paramount.bed.data.remote.response.AdvertisementResponse;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.DailyScoreResponse;
import com.paramount.bed.data.remote.response.DailyScoreStatusResponse;
import com.paramount.bed.data.remote.response.FirmwareVersionResponse;
import com.paramount.bed.data.remote.response.ForrestScoreAdviceResponse;
import com.paramount.bed.data.remote.response.MonitoringResponse;
import com.paramount.bed.data.remote.response.PasswordPolicyResponse;
import com.paramount.bed.data.remote.response.QuestionnaireResponse;
import com.paramount.bed.data.remote.response.RequestOtpResponse;
import com.paramount.bed.data.remote.response.LoginResponse;
import com.paramount.bed.data.remote.response.SenderBirdieListResponse;
import com.paramount.bed.data.remote.response.SettingResponse;
import com.paramount.bed.data.remote.response.SleepQuestionnaireResponse;
import com.paramount.bed.data.remote.response.TimeSleepResetStatusResponse;
import com.paramount.bed.data.remote.response.UserDetailResponse;
import com.paramount.bed.data.remote.response.ValidateCompanyResponse;
import com.paramount.bed.data.remote.response.ValidationPhoneResponse;
import com.paramount.bed.data.remote.response.WeeklyScoreResponse;
import com.paramount.bed.data.remote.response.UpdateResponse;
import com.paramount.bed.data.remote.response.WeeklyScoreStatusResponse;
import com.paramount.bed.data.remote.response.ZipResponse;
import com.paramount.bed.data.remote.response.QuestionAnswerResponse;
import com.paramount.bed.data.remote.response.QuestionResponse;
import com.paramount.bed.data.remote.response.UserMonitoringResponse;
import com.paramount.bed.data.remote.response.ValidationEmailResponse;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {

    @FormUrlEncoded
    @POST("User/validate_company_code")
    Single<BaseResponse<ValidateCompanyResponse>> validateCompany(@Field("company_code") String code, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/mail_validation")
    Single<BaseResponse<ValidationEmailResponse>> registrationEmailValidation(@Field("email") String email, @Field("mail_verification") int emailVerification, @Field("token") String token, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/check_mail_activation")
    Single<BaseResponse<ActivationEmailResponse>> registrationEmailActivation(@Field("email") String email, @Field("token") String token, @Query("app_type") int app_type);

    @GET("User/req_otp")
    Single<BaseResponse<ValidationPhoneResponse>> registerRequestOTP(@Query("phone_number") String phone, @Query("verify_token") String token, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/validate_otp")
    Single<BaseResponse<String>> registerValidateOTP(@Field("phone_number") String phone,
                                                     @Field("otp") String otp, @Field("verify_token") String token, @Query("app_type") int app_type);

    @GET("User/password_req_otp")
    Single<BaseResponse<RequestOtpResponse>> passwordRequestOTP(@Query("phone_number") String phone,
                                                                @Query("birth_date") String birth_date,
                                                                @Query("lg_phone_number") String phone_number,
                                                                @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/password_validate_otp")
    Single<BaseResponse<String>> passwordValidateOTP(@Field("phone_number") String phone,
                                                     @Field("otp") String otp, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/password_reset")
    Single<BaseResponse<String>> passwordReset(@Field("new_password") String new_password,
                                               @Field("token") String token, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/register")
    Single<BaseResponse<LoginResponse>> register(@Field("email") String email,
                                                 @Field("password") String password,
                                                 @Field("type") int type,
                                                 @Field("access_token") String access_token,
                                                 @Field("phone_number") String phone_number,
                                                 @Field("nick_name") String nick_name,
                                                 @Field("birth_date") String birth_date,
                                                 @Field("gender") int gender,
                                                 @Field("zip_code") String zip_code,
                                                 @Field("city") String city,
                                                 @Field("prefecture") String prefecture,
                                                 @Field("address") String address,
                                                 @Field("company_code") String company_code,
                                                 @Field("height") int height,
                                                 @Field("weight") int weight,
                                                 @Field("user_type") int user_type,
                                                 @Field("questionnaire_result") String questionnaire_result,
                                                 @Field("ns_serial_number") String ns_serial_number,
                                                 @Query("app_type") int app_type,
                                                 @Field("major") int major,
                                                 @Field("minor") int minor,
                                                 @Field("revision") int revision,
                                                 @Field("user_desired_hardness") int user_desired_hardness);

    @FormUrlEncoded
    @POST("User/update")
    Single<BaseResponse<UpdateResponse>> update(@Field("user_id") String user_id,
                                                @Field("nick_name") String nick_name,
                                                @Field("birth_date") String birth_date,
                                                @Field("password") String password,
                                                @Field("zip_code") String zip_code,
                                                @Field("address") String address,
                                                @Field("height") String height,
                                                @Field("weight") String weight,
                                                @Query("app_type") int app_type);


    @FormUrlEncoded
    @POST("User/update")
    Single<BaseResponse<String>> updateUser(@Field("user_id") String user_id,
                                            @Field("nick_name") String nick_name,
                                            @Field("birth_date") String birth_date,
                                            @Field("password") String password,
                                            @Field("zip_code") String zip_code,
                                            @Field("address") String address,
                                            @Field("height") String height,
                                            @Field("weight") String weight,
                                            @Field("email") String email,
                                            @Field("phone_number") String phone_number,
                                            @Field("gender") int gender,
                                            @Field("sns_provider") int sns_provider,
                                            @Field("sns_token") String sns_token,
                                            @Field("user_desired_hardness") int user_desired_hardness,
                                            @Query("app_type") int app_type);


    @GET("User/id_req_otp")
    Single<BaseResponse<RequestOtpResponse>> idRequestOTP(@Query("phone_number") String phone,
                                                          @Query("birth_date") String birth_date,
                                                          @Query("lg_phone_number") String phone_number,
                                                          @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/id_validate_otp")
    Single<BaseResponse<String>> idValidateOTP(@Field("phone_number") String phone,
                                               @Field("otp") String otp, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/login")
    Single<BaseResponse<LoginResponse>> login(@Field("type") String type,
                                              @Field("email") String email,
                                              @Field("password") String password,
                                              @Field("access_token") String access_token,
                                              @Field("ns_serial_number") String serial_number,
                                              @Field("phone_number") String phone_number, @Query("app_type") int app_type

    );

    @FormUrlEncoded
    @POST("User/logout")
    Single<BaseResponse<String>> logout(@Field("access_token") String access_token, @Query("app_type") int app_type);

    @POST("User/delete")
    Single<BaseResponse<String>> userDelete(@Query("phone_number") String phone, @Query("app_type") int app_type);

    @GET("Monitoring/account")
    Single<BaseResponse<ArrayList<UserMonitoringResponse>>> getMonitoringList(@Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("Monitoring/account")
    Single<BaseResponse<UserMonitoringResponse>> addMonitoringUser(@Field("user_email") String user_email,
                                                                   @Field("order_id") String order_id, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("Monitoring/account_edit")
    Single<BaseResponse<UserMonitoringResponse>> editMonitoringUser(@Field("id") int id,
                                                                    @Field("nickname") String nickname, @Query("app_type") int app_type);

    @GET("User/zip_lookup")
    Single<BaseResponse<ZipResponse>> requestZipLookup(@Query("zip_code") String zip_code, @Query("app_type") int app_type);

    @DELETE("Monitoring/account")
    Single<BaseResponse<String>> deleteMonitoringUser(@Query("id") int id,
                                                      @Query("nickname") String nickname, @Query("app_type") int app_type);

    @GET("User/setting")
    Single<BaseResponse<SettingResponse>> getSetting(@Query("user_id") int user_id, @Query("app_type") int app_type);

    @GET("User/setting")
    Observable<BaseResponse<SettingResponse>> getSetting1(@Query("user_id") int user_id, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/setting")
    Single<BaseResponse> saveSetting(@Field("user_id") int user_id, @Field("settings") String settings, @Query("app_type") int app_type);

    @GET("User/monitoring")
    Single<BaseResponse<ArrayList<MonitoringResponse>>> getMonitoring(@Query("user_id") int user_id, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("Monitoring/update_status")
    Single<BaseResponse<String>> changeStatusMonitoring(@Field("monitoring_user_id") String monitoring_user_id, @Field("monitored_user_id") String monitored_user_id, @Field("status") String status, @Query("app_type") int app_type);


    @GET("Questionnaire")
    Single<BaseResponse<QuestionnaireResponse<ArrayList<QuestionResponse<ArrayList<QuestionAnswerResponse>>>>>> getQuestionnaire(@Query("company_code") String company_code, @Query("app_type") int app_type);

    @GET("User/daily_score")
    Single<BaseResponse<ArrayList<DailyScoreResponse>>> getUserDailyScore(@Query("user_id") int user_id, @Query("start_date") String start_date, @Query("end_date") String end_date, @Query("app_type") int app_type);

    @GET("User/weekly_score")
    Single<BaseResponse<ArrayList<WeeklyScoreResponse>>> getUserWeeklyScore(@Query("user_id") int user_id, @Query("start_date") String start_date, @Query("end_date") String end_date, @Query("app_type") int app_type);

    @GET("User/daily_score_status")
    Single<BaseResponse<ArrayList<DailyScoreStatusResponse>>> getUserDailyScoreStatus(@Query("user_id") int user_id, @Query("start_date") String start_date, @Query("end_date") String end_date, @Query("app_type") int app_type);

    @GET("User/weekly_score_status")
    Single<BaseResponse<ArrayList<WeeklyScoreStatusResponse>>> getUserWeeklyScoreStatus(@Query("user_id") int user_id, @Query("start_date") String start_date, @Query("end_date") String end_date, @Query("app_type") int app_type);


    @FormUrlEncoded
    @POST("User/delete")
    Single<BaseResponse<String>> deleteUser(@Field("user_id") int user_id, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/fcm_token")
    Single<BaseResponse<String>> fcmUpdateToServer(@Field("user_id") int user_id, @Field("fcm_token") String token, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/log_action")
    Single<BaseResponse<String>> logAction(@Field("user_id") String user_id,
                                           @Field("key") String key,
                                           @Field("value") String value,
                                           @Field("device_type") String device_type,
                                           @Field("os_version") String os_version,
                                           @Field("nemuri_scan_sn") String nemuri_scan_sn,
                                           @Field("lg_screen_id") String fieldScreenId,
                                           @Query("lg_screen_id") String queryScreenId,
                                           @Query("app_type") int app_type);

    @GET("User/login_req_otp")
    Single<BaseResponse<RequestOtpResponse>> ChangePhoneReqOTP(@Query("phone_number") String phone,
                                                               @Query("birth_date") String birth_date,
                                                               @Query("email") String email, @Query("app_type") int app_type
    );


    @FormUrlEncoded
    @POST("User/login_otp")
    Single<BaseResponse<LoginResponse>> ChangePhoneValidateOTP(@Field("phone_number") String phone,
                                                               @Field("otp") String otp,
                                                               @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/login_verify_req_otp")
    Single<BaseResponse<RequestOtpResponse>> ChangePhoneReqOTPV2(@Field("phone_number") String phone,
                                                                 @Field("birth_date") String birth_date,
                                                                 @Field("email") String email,
                                                                 @Field("password") String password,
                                                                 @Field("sns_provider") int sns_provider,
                                                                 @Query("app_type") int app_type
    );


    @FormUrlEncoded
    @POST("User/login_verify_otp")
    Single<BaseResponse<LoginResponse>> ChangePhoneValidateOTPV2(@Field("phone_number") String phone,
                                                                 @Field("email") String email,
                                                                 @Field("otp") String otp,
                                                                 @Field("password") String password,
                                                                 @Field("sns_provider") int sns_provider,
                                                                 @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/phone_validation")
    Single<BaseResponse> phoneValidation(@Field("phone_number") String phone, @Query("app_type") int app_type);

    @GET("User/questionnaire_advertise")
    Single<BaseResponse<AdvertisementResponse>> getAdvertisement(@Query("user_id") Integer user_id, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("questionnaire/send")
    Observable<BaseResponse<String>> sendQuestionnaireHome(@Field("user_id") Integer user_id,
                                                       @Field("questionnaire_type") Integer questionnaire_type,
                                                       @Field("result") String result, @Query("app_type") int app_type);

    @GET("User/sleep_questionnaire")
    Single<BaseResponse<SleepQuestionnaireResponse>> getSleepQuestionnaire(@Query("user_id") Integer user_id, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/user_bed_template")
    Single<BaseResponse> sendBedTemplate(@Field("user_id") Integer user_id,
                                         @Field("bed_templates_id") Integer bed_templates_id,
                                         @Field("head") Integer head,
                                         @Field("leg") Integer leg,
                                         @Field("tilt") Integer tilt,
                                         @Field("height") Integer height,
                                         @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/user_matress_template")
    Single<BaseResponse> sendMatressTemplate(@Field("user_id") Integer user_id,
                                             @Field("matress_templates_id") Integer matress_templates_id,
                                             @Field("head") Integer head,
                                             @Field("shoulder") Integer shoulder,
                                             @Field("hip") Integer hip,
                                             @Field("thigh") Integer thigh,
                                             @Field("calf") Integer calf,
                                             @Field("feet") Integer feet,
                                             @Query("app_type") int app_type);

    @GET("User/password_policy")
    Single<BaseResponse<PasswordPolicyResponse>> getPasswordPolicy(@Query("company_code") String company_code, @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/validate_password")
    Single<BaseResponse> validatePassword(@Field("company_code") String company_code,
                                          @Field("password") String password,
                                          @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("Monitoring/bird_notif_list")
    Single<BaseResponse<ArrayList<SenderBirdieListResponse>>> getBirdieNotifList(@Field("end_user_id") int id);

    @FormUrlEncoded
    @POST("User/see_ads")
    Single<BaseResponse> seeAds(@Field("advertise_id") int advertise_id, @Field("end_user_id") int end_user_id);

    @FormUrlEncoded
    @POST("User/see_questionnaire")
    Single<BaseResponse> seeQS(@Field("questionnaire_id") int advertise_id, @Field("end_user_id") int end_user_id);

    @GET("User/phone_update_req_otp")
    Single<BaseResponse> phoneUpdateReqOTP(@Query("phone_number") String phone, @Query("end_user_id") int end_user_id);

    @FormUrlEncoded
    @POST("User/phone_update_validate_otp")
    Single<BaseResponse> phoneUpdateValidateOTP(@Field("phone_number") String phone, @Field("end_user_id") int end_user_id, @Field("otp") String otp);

    @FormUrlEncoded
    @POST("User/change_email_verification_req")
    Single<BaseResponse> editEmailValidation(@Field("email") String email,
                                             @Field("token") String token,
                                             @Field("end_user_id") int end_user_id,
                                             @Field("existing_email") String existing_email,
                                             @Field("mail_verification") int emailVerification,
                                             @Query("app_type") int app_type);

    @FormUrlEncoded
    @POST("User/change_email_verification")
    Single<BaseResponse> editEmailActivation(@Field("email") String email,
                                             @Field("token") String token,
                                             @Field("end_user_id") int end_user_id,
                                             @Field("existing_email") String existing_email,
                                             @Query("app_type") int app_type);

    @POST("User/detail")
    Single<BaseResponse<UserDetailResponse>> getUserDetail();

    @POST("User/firmware_detail")
    Single<BaseResponse<FirmwareVersionResponse>> getFirmwareVersion();

    @FormUrlEncoded
    @POST("User/firmware_version_update")
    Single<BaseResponse> sendFirmwareVersion(@Field("major") int major,
                                              @Field("minor") int minor,
                                              @Field("revision") int revision,
                                              @Field("serial_number") String serialNumber);

    @GET("User/forest_calculation")
    Observable<BaseResponse<ForrestScoreAdviceResponse>> forestCalculation(@Query("user_id") int userId, @Query("menu_access") int menuAccess);

    @GET("User/mattress_setting")
    Call<BaseResponse<MattressSettingModel>> getMattressSetting(@Query("user_id") int userId);

    @FormUrlEncoded
    @POST("User/set_mattress_hardness_preference")
    Call<BaseResponse<MattressSettingModel>> setMattressSetting(@Field("user_id") int userId, @Field("user_desired_hardness") int mattresHardness);

    @FormUrlEncoded
    @POST("User/mattress_apply_setting")
    Call<BaseResponse<MattressSettingModel>> applyMattressSetting(@Field("user_id") int userId,
                                                    @Field("head") int head,
                                                    @Field("shoulder") int shoulder,
                                                    @Field("hip") int hip,
                                                    @Field("thigh") int thigh,
                                                    @Field("calf") int calf,
                                                    @Field("feet") int feet,
                                                    @Field("user_desired_hardness") int mattresHardness);

    @FormUrlEncoded
    @POST("User/mattress_apply_setting")
    Call<BaseResponse<MattressSettingModel>> applyMattressSettingOffline(@Field("user_id") int userId,
                                                                         @Field("date") String date,
                                                                         @Field("head") int head,
                                                                         @Field("shoulder") int shoulder,
                                                                         @Field("hip") int hip,
                                                                         @Field("thigh") int thigh,
                                                                         @Field("calf") int calf,
                                                                         @Field("feet") int feet,
                                                                         @Field("user_desired_hardness") int mattresHardness);

    @FormUrlEncoded
    @POST("User/sleep_reset_start")
    Observable<BaseResponse<TimeSleepResetStatusResponse>> sendSleepResetStart(@Field("user_id") int userId,
                                             @Field("sleep_reset_timing") int sleepResetTiming);

    @FormUrlEncoded
    @POST("User/sleep_reset_stop")
    Observable<BaseResponse> sendSleepResetStop(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("User/sleep_reset_status")
    Observable<BaseResponse<TimeSleepResetStatusResponse>> getSleepResetStatus(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("User/snore_analysis")
    Observable<BaseResponse> getSnoringAnalysis(@Field("end_user_id") int userId, @Field("jsondata") String jsonData);

    @FormUrlEncoded
    @POST("User/snore_analysis")
    Call<BaseResponse> getSnoringAnalysisOffline(@Field("end_user_id") int userId, @Field("jsondata") String jsonData);
}
