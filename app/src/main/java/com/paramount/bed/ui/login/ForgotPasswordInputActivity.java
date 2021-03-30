package com.paramount.bed.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.paramount.bed.R;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.RequestOtpResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.datepicker.DateWheelBuilder;
import com.paramount.bed.ui.datepicker.DateWheelPicker;
import com.paramount.bed.ui.datepicker.OnDateWheelSelectListener;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IntentConstant;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ProgressDrawable;
import com.paramount.bed.util.ValidationUtils;
import com.paramount.bed.util.ViewUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ForgotPasswordInputActivity extends BaseActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;

    @BindView(R.id.etBirthday)
    EditText etBirthday;

    @OnClick(R.id.btnBack)
    void back() {
        this.onBackPressed();
    }

    DateWheelPicker birthdayPicker;
    Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        etBirthday.setOnFocusChangeListener(onBirthdayFocus());

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(1975, 1, 1);

        birthdayPicker = new DateWheelBuilder(this, onBirthdaySelect())
                .setDate(selectedDate)
                .setCancelText(LanguageProvider.getLanguage("UI000491C019"))
                .setConfirmText(LanguageProvider.getLanguage("UI000491C020"))
                .build();
        setToolbarTitle(LanguageProvider.getLanguage("UI000491C001"));
        int progressBarSegment = 3;
        int currentSegment = 1;
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");
        Drawable d = new ProgressDrawable(fgColor, bgColor, progressBarSegment);
        progressBar.setProgressDrawable(d);
        progressBar.setProgress(1000 * currentSegment / progressBarSegment);
    }

    private View.OnFocusChangeListener onBirthdayFocus() {
        return ((view, hasFocus) -> {
            if (hasFocus) {
                birthdayPicker.show();
                ViewUtil.hideKeyboard(ForgotPasswordInputActivity.this);
            }
        });
    }

    private OnDateWheelSelectListener onBirthdaySelect() {
        return ((date, v) -> {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            String outputDate = df.format(date);
            etBirthday.setText(outputDate);
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_forgot_password_input;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @OnClick(R.id.btnSendSMS)
    public void onBtnRequestOTPClicked() {
        String dataPhoneNumber = etPhoneNumber.getText().toString().trim();
        String dataBirthday = etBirthday.getText().toString().trim();
        if (isBadPhone(dataPhoneNumber)) return;
        if (isBadBirthday(dataBirthday)) return;

        if (validationStorage()) {
            String[] birthDateSplit = etBirthday.getText().toString().split("/");
            String birthDate = birthDateSplit[0] + "/" + birthDateSplit[1] + "/" + birthDateSplit[2];
            String phoneNumber = etPhoneNumber.getText().toString();
            String phoneNumberExist = UserLogin.getUserLogin() != null && UserLogin.getUserLogin().getPhoneNumber() != null ? UserLogin.getUserLogin().getPhoneNumber() : "";
            showLoading();
            mDisposable = userService.passwordRequestOTP(phoneNumber, birthDate, phoneNumberExist, 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse<RequestOtpResponse>>() {
                        @Override
                        public void onSuccess(BaseResponse<RequestOtpResponse> response) {
                            hideLoading();
                            if (response.isSucces()) {
                                if (response.getMessage().equals("USR11-C099")) {
                                    requestOTPSuccess(phoneNumber, response.getData().validUntil);
                                } else {
                                    DialogUtil.createSimpleOkDialog(ForgotPasswordInputActivity.this, "", LanguageProvider.getLanguage(response.getMessage()),
                                            LanguageProvider.getLanguage("UI000802C003"), ((dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                                requestOTPSuccess(phoneNumber, response.getData().validUntil);
                                            }));
                                }
                            } else {
                                DialogUtil.createSimpleOkDialog(ForgotPasswordInputActivity.this, "", LanguageProvider.getLanguage(response.getMessage()));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideLoading();
                            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                DialogUtil.offlineDialog(ForgotPasswordInputActivity.this, getApplicationContext());

                            } else {
                                DialogUtil.serverFailed(ForgotPasswordInputActivity.this, "UI000802C145", "UI000802C146", "UI000802C147", "UI000802C148");
                            }
                        }
                    });
        }
    }

    private boolean isBadPhone(String dataPhoneNumber) {
        return ValidationUtils.PHONE.isBad(dataPhoneNumber, new ValidationUtils.PHONE.BadPhoneListener() {
            @Override
            public void onPhoneEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotPasswordInputActivity.this, "", LanguageProvider.getLanguage("UI000491C013")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotPasswordInputActivity.this, "", LanguageProvider.getLanguage("UI000491C021")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotPasswordInputActivity.this, "", LanguageProvider.getLanguage("UI000491C015")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneCharsWrong(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotPasswordInputActivity.this, "", LanguageProvider.getLanguage("UI000491C013")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }

    private boolean isBadBirthday(String dateBirhday) {
        if (dateBirhday.isEmpty()) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000491C014"));
            return true;
        }
        return false;
    }

    private boolean validationStorage() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String birthDate = etBirthday.getText().toString().replace("/", "");
        Boolean iretun = false;
        String message = "";
        String title = "";
        if (UserLogin.getUserLogin() != null
                && UserLogin.getUserLogin().getPhoneNumber() != null
                && UserLogin.getUserLogin().getBirthDate() != null
                &&
                (!phoneNumber.equals(UserLogin.getUserLogin().getPhoneNumber().substring(0))
                        || !birthDate.equals(UserLogin.getUserLogin().getBirthDate().replace("/", "").replace("-", ""))
                )
        ) {
            message = LanguageProvider.getLanguage("UI000491C016");
            title = "";
            iretun = false;
            DialogUtil.createSimpleOkDialog(this, title, message);
        } else {
            iretun = true;
        }
        return iretun;
    }

    private void requestOTPSuccess(String phoneNumber, String validUntil) {
        Intent intent = new Intent(this, ForgotPasswordPinActivity.class);
        intent.putExtra(IntentConstant.PHONE_NUMBER, phoneNumber);
        intent.putExtra(IntentConstant.OTP_VALIDITY, validUntil);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}
