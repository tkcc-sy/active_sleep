package com.paramount.bed.ui.registration.step;

import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ligl.android.widget.iosdialog.IOSDialog;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.ValidationPhoneModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.BaseFragment;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ValidationUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.paramount.bed.util.NetworkUtil.isNetworkConnected;

public class TelephoneVerificationFragment extends BLEFragment {
    EditText etPIN;
    private Button btnNext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_telephone_verification, container, false);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        etPIN = (EditText) view.findViewById(R.id.etPIN);
        btnNext.setOnClickListener(next());
        applyLocalization(view);
        applyView(view);
        return view;
    }

    public void applyView(View v) {
        TextView tvGuide = v.findViewById(R.id.tvGuide);
        tvGuide.setText(LanguageProvider.getLanguage("UI000430C002").replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength())));
    }


    private View.OnClickListener next() {
        return (view -> {
            String otp = etPIN.getText().toString().trim();
            if (isBadPIN(otp)) return;

            showLoading();
            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
            activity.mDisposable = activity.userService.registerValidateOTP(activity.PHONE_NUMBER, otp, ValidationPhoneModel.getByPhone(activity.PHONE_NUMBER).getToken(), 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                        @Override
                        public void onSuccess(BaseResponse<String> response) {
                            hideLoading();
                            if (response.isSucces()) {
                                DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(response.getMessage()),
                                        LanguageProvider.getLanguage("UI000430C008"), ((dialogInterface, i) -> activity.go(activity.FRAGMENT_ACCOUNT_BIO)));

                            } else {
                                DialogUtil.createSimpleOkDialog(activity, "",
                                        LanguageProvider.getLanguage(response.getMessage())
                                                .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getPinLength())));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideLoading();

                            if (!NetworkUtil.isNetworkConnected(getContext())) {
                                DialogUtil.offlineDialog(activity, getContext());
                            } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                DialogUtil.tokenExpireDialog(activity);
                            } else {
                                DialogUtil.serverFailed(activity, "UI000802C113", "UI000802C114", "UI000802C115", "UI000802C116");
                            }
                        }
                    });
        });
    }

    public boolean isBadPIN(String pin) {
        return ValidationUtils.PIN.isBad(pin, new ValidationUtils.PIN.BadPINListener() {
            @Override
            public void onPINEmpty(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000430C010")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength))
                );
            }

            @Override
            public void onPINLengthWrong(ValidationUtils.LenReplacer lenReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000430C011")
                        .replace(lenReplacer.keyReplacer, String.valueOf(lenReplacer.shouldLength))
                );
            }
        });
    }
}


