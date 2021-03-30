package com.paramount.bed.data.provider;

import android.annotation.SuppressLint;
import android.content.Context;

import com.paramount.bed.data.model.PasswordPolicyModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.PasswordPolicyResponse;
import com.paramount.bed.data.remote.service.UserService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PasswordPolicyProvider {
    private UserService userService;
    private Context ctx;

    public PasswordPolicyProvider(Context ctx) {
        this.ctx = ctx;
        this.userService = ApiClient.getClient(ctx).create(UserService.class);
    }

    @SuppressLint("CheckResult")
    public void getPasswordPolicy(String companyCode, PasswordPolicyListener passwordPolicyListener) {
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.getPasswordPolicy(companyCode, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<PasswordPolicyResponse>>() {
                    public void onSuccess(BaseResponse<PasswordPolicyResponse> response) {
                        if (response.isSucces()) {
                            passwordPolicyListener.onPasswordPolicyRetrieve(updateDatabase(response.getData()), companyCode);
                            return;
                        }
                        passwordPolicyListener.onPasswordPolicyRetrieve(PasswordPolicyModel.getFirst(), companyCode);
                    }

                    @Override
                    public void onError(Throwable e) {
                        passwordPolicyListener.onPasswordPolicyRetrieve(PasswordPolicyModel.getFirst(), companyCode);
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void checkPasswordPolicy(String companyCode, String password, CheckPasswordPolicyListener checkPasswordPolicyListener) {
        getPasswordPolicy(companyCode, ((passwordPolicyModel, companyCodeResult) -> {
            UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
            sService.validatePassword(companyCodeResult, password, 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                        public void onSuccess(BaseResponse response) {
                            checkPasswordPolicyListener.onCheckPasswordPolicySuccess(true, passwordPolicyModel, response, null);
                        }

                        @Override
                        public void onError(Throwable e) {
                            checkPasswordPolicyListener.onCheckPasswordPolicySuccess(false, passwordPolicyModel, null, e);
                        }
                    });
        }));
    }

    private PasswordPolicyModel updateDatabase(PasswordPolicyResponse passwordPolicyResponse) {
        if (passwordPolicyResponse != null) {
            PasswordPolicyModel.clear();
            PasswordPolicyModel passwordPolicyModel = new PasswordPolicyModel();
            passwordPolicyModel.setMinLength(passwordPolicyResponse.min_length);
            passwordPolicyModel.setMaxLength(passwordPolicyResponse.max_length);
            passwordPolicyModel.setAllowedSymbols(passwordPolicyResponse.allowed_symbols);
            passwordPolicyModel.insert();
            return passwordPolicyModel;
        }
        return PasswordPolicyModel.getFirst();
    }

    public interface PasswordPolicyListener {
        void onPasswordPolicyRetrieve(PasswordPolicyModel response, String companyCode);
    }

    public interface CheckPasswordPolicyListener {
        void onCheckPasswordPolicySuccess(boolean isResponse, PasswordPolicyModel passwordPolicyModel, BaseResponse baseResponse, Throwable e);
    }
}
