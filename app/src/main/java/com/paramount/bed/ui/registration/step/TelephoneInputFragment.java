package com.paramount.bed.ui.registration.step;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.RegisterStep;
import com.paramount.bed.data.model.RegisteringModel;
import com.paramount.bed.data.model.ValidationPhoneModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.ValidationPhoneResponse;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ValidationUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TelephoneInputFragment extends BLEFragment {
    EditText etPhoneNumber;
    private Button btnNext;
    public static Boolean isPhoneValidating;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_telephone_input, container, false);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        etPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);

        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        try {
            if (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getPhoneNumber() != null) {
                etPhoneNumber.setText(RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getPhoneNumber());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        etPhoneNumber.setText(RegisteringModel.getProfile().getPhoneNumber());
        isPhoneValidating = false;
        btnNext.setOnClickListener(next());
        applyLocalization(view);
        return view;
    }

    private View.OnKeyListener watchInput() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        };
    }

    private View.OnClickListener back() {
        return (view -> {
            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
            activity.onBackPressed();
        });
    }

    private View.OnClickListener next() {
        return (view -> {
//            ApiClient.LogData.setLogPhone(getActivity(), etPhoneNumber.getText().toString());
            if (isBadPhoneNumber()) return;
            RegisteringModel.updatePhoneNumber(etPhoneNumber.getText().toString().trim());
            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
            if (!isPhoneValidating) {
                validatePhoneUnique(activity, view);
            }
        });
    }

    private void validatePhoneUnique(RegistrationStepActivity activity, View v) {
        isPhoneValidating = true;
        String phone = etPhoneNumber.getText().toString().replace("-", "").trim();
        showLoading();
        activity.mDisposable = activity.userService.phoneValidation(phone, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {

                        if (response.isSucces()) {
                            requestOtp(activity, v);
                        } else {
                            hideLoading();
                            isPhoneValidating = false;
                            DialogUtil.createSimpleOkDialog(activity, "",
                                    LanguageProvider.getLanguage(response.getMessage()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        isPhoneValidating = false;
                        if (!NetworkUtil.isNetworkConnected(getContext())) {
                            DialogUtil.offlineDialog(activity, getContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(activity);
                        } else {
                            DialogUtil.serverFailed(activity, "UI000802C117", "UI000802C118", "UI000802C119", "UI000802C120");
                        }
                        Log.d("abx", e.getMessage());
                    }
                });
    }


    protected void requestOtp(RegistrationStepActivity activity, View v) {
        String phone = etPhoneNumber.getText().toString().replace("-", "").trim();
        activity.PHONE_NUMBER = phone;
        activity.mDisposable = activity.userService.registerRequestOTP(phone, ValidationPhoneModel.getByPhone(phone).getToken(), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<ValidationPhoneResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<ValidationPhoneResponse> response) {
                        hideLoading();
                        if (response.isSucces()) {
                            isPhoneValidating = false;
                            ValidationPhoneModel.updateByPhone(phone, "", response.getData().getToken());
                            DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(response.getMessage()), LanguageProvider.getLanguage("UI000420C011"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RegisterStep registerStep = new RegisterStep();
                                    registerStep.setPhoneNumber(phone);
                                    registerStep.update(activity.EMAIL, 7);

                                    activity.go(activity.FRAGMENT_TELEPHONE_VERIFICATION);
                                }
                            });

                        }
                        //Skip Check Phone PIN Request (Authorized)
                        else if (response.getMessage().equals("USR04-C006")) {
                            activity.go(activity.FRAGMENT_ACCOUNT_BIO);
                        }
                        //ByPass Check Phone PIN Request
                        else if (response.getMessage().equals("USR04-C007")) {
                            RegisterStep registerStep = new RegisterStep();
                            registerStep.setPhoneNumber(phone);
                            registerStep.update(activity.EMAIL, 7);

                            activity.go(activity.FRAGMENT_TELEPHONE_VERIFICATION);
                        } else {
                            isPhoneValidating = false;
                            DialogUtil.createSimpleOkDialog(activity, "",
                                    LanguageProvider.getLanguage(response.getMessage()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        isPhoneValidating = false;
                        Log.d("abx", e.getMessage());

                        if (!NetworkUtil.isNetworkConnected(getContext())) {
                            DialogUtil.offlineDialog(activity, getContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(activity);
                        } else {
                            DialogUtil.serverFailed(activity, "UI000802C117", "UI000802C118", "UI000802C119", "UI000802C120");
                        }
                    }
                });
    }

    public boolean isBadPhoneNumber() {
        String phoneNumber = etPhoneNumber.getText().toString().trim().replace("-", "");
        return ValidationUtils.PHONE.isBad(phoneNumber, new ValidationUtils.PHONE.BadPhoneListener() {
            @Override
            public void onPhoneEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000420C014")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000420C012")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength))
                );
            }

            @Override
            public void onPhoneLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000420C013")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength))
                );
            }

            @Override
            public void onPhoneCharsWrong(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000420M012")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }
}




