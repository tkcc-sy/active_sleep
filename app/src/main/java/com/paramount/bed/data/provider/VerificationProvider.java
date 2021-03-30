package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;

import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.ActivationEmailResponse;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.ValidationEmailResponse;
import com.paramount.bed.data.remote.service.UserService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

public class VerificationProvider {

    private UserService settingService;
    private Context ctx;

    public VerificationProvider(Context ctx) {
        this.ctx = ctx;
        this.settingService = ApiClient.getClient(ctx).create(UserService.class);
    }

    //#region Phone Update
    @SuppressLint("CheckResult")
    public void phoneUpdateReqOTP(String phone, PhoneUpdateReqOTPListener listener) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.phoneUpdateReqOTP(phone, UserLogin.getUserLogin().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                        listener.onPhoneUpdateReqOTPSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onPhoneUpdateReqOTPError(e);
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void phoneUpdateValidateOTP(String phone, String otp, PhoneUpdateValidateOTPListener listener) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.phoneUpdateValidateOTP(phone, UserLogin.getUserLogin().getId(), otp)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                        listener.onphoneUpdateValidateOTPSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onPhoneUpdateValidateError(e);
                    }
                });
    }
    //#endregion Phone Update

    @SuppressLint("CheckResult")
    public void emailValidation(String email, int mailVerification, String token, EmailValidationListener listener) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.editEmailValidation(email, token, UserLogin.getUserLogin().getId(), UserLogin.getUserLogin().getEmail(), mailVerification, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                        listener.onEmailValidationSuccess(email, mailVerification, token, response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onEmailValidationError(e);
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void emailActivation(String email, String token, EmailActivationListener listener) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.editEmailActivation(email, token, UserLogin.getUserLogin().getId(), UserLogin.getUserLogin().getEmail(), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                        listener.onEmailActivationSuccess(email, token, response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onEmailActivationError(e);
                    }
                });
    }

    public interface PhoneUpdateReqOTPListener {
        void onPhoneUpdateReqOTPSuccess(BaseResponse response);

        void onPhoneUpdateReqOTPError(Throwable e);
    }

    public interface PhoneUpdateValidateOTPListener {
        void onphoneUpdateValidateOTPSuccess(BaseResponse response);

        void onPhoneUpdateValidateError(Throwable e);
    }

    public interface EmailValidationListener {
        void onEmailValidationSuccess(String email, int mailVerification, String token, BaseResponse response);

        void onEmailValidationError(Throwable e);
    }

    public interface EmailActivationListener {
        void onEmailActivationSuccess(String email, String token, BaseResponse response);

        void onEmailActivationError(Throwable e);
    }
}
