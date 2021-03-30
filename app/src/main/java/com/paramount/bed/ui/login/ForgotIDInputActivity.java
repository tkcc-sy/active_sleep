package com.paramount.bed.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.RequestOtpResponse;
import com.paramount.bed.ui.datepicker.OnDateWheelSelectListener;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.datepicker.DateWheelBuilder;
import com.paramount.bed.ui.datepicker.DateWheelPicker;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IntentConstant;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ProgressDrawable;
import com.paramount.bed.util.ValidationUtils;
import com.paramount.bed.util.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ForgotIDInputActivity extends BaseActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;

    @BindView(R.id.etBirthday)
    EditText etBirthday;

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
                .setCancelText(LanguageProvider.getLanguage("UI000496C019"))
                .setConfirmText(LanguageProvider.getLanguage("UI000496C020"))
                .build();

        setToolbarTitle(LanguageProvider.getLanguage("UI000496C001"));

        int progressBarSegment = 3;
        int currentSegment = 1;
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");
        Drawable d = new ProgressDrawable(fgColor, bgColor, progressBarSegment);
        progressBar.setProgressDrawable(d);
        progressBar.setProgress(1000 * currentSegment / progressBarSegment);
    }

    private View.OnFocusChangeListener onBirthdayFocus() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ViewUtil.hideKeyboard(ForgotIDInputActivity.this);
                    birthdayPicker.show();
                }
            }
        };
    }

    private OnDateWheelSelectListener onBirthdaySelect() {
        return new OnDateWheelSelectListener() {
            @Override
            public void onDatateWheelSelect(Date date, View v) {
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                String outputDate = df.format(date);
                etBirthday.setText(outputDate);
            }
        };
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_forgot_id_input;
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
            mDisposable = userService.idRequestOTP(phoneNumber, birthDate, phoneNumberExist, 1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableSingleObserver<BaseResponse<RequestOtpResponse>>() {
                        @Override
                        public void onSuccess(BaseResponse<RequestOtpResponse> response) {
                            hideLoading();
                            if (response.isSucces()) {
                                if (response.getMessage().equals("USR09-C099")) {
                                    requestOTPSuccess(phoneNumber, response.getData().validUntil);
                                } else {
                                    DialogUtil.createSimpleOkDialog(ForgotIDInputActivity.this, "", LanguageProvider.getLanguage(response.getMessage()),
                                            LanguageProvider.getLanguage("UI000802C003"), ((dialogInterface, i) -> {
                                                dialogInterface.dismiss();
                                                requestOTPSuccess(phoneNumber, response.getData().validUntil);
                                            }));
                                }
                            } else {
                                DialogUtil.createSimpleOkDialog(ForgotIDInputActivity.this, "", LanguageProvider.getLanguage(response.getMessage()));
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            hideLoading();
                            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                DialogUtil.offlineDialog(ForgotIDInputActivity.this, getApplicationContext());

                            } else {
                                DialogUtil.serverFailed(ForgotIDInputActivity.this, "UI000802C089", "UI000802C090", "UI000802C091", "UI000802C092");
                            }
                        }
                    });
        }
    }

    private boolean isBadPhone(String dataPhoneNumber) {
        return ValidationUtils.PHONE.isBad(dataPhoneNumber, new ValidationUtils.PHONE.BadPhoneListener() {
            @Override
            public void onPhoneEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotIDInputActivity.this, "", LanguageProvider.getLanguage("UI000496C012")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotIDInputActivity.this, "", LanguageProvider.getLanguage("UI000496C021")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotIDInputActivity.this, "", LanguageProvider.getLanguage("UI000496C015")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneCharsWrong(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ForgotIDInputActivity.this, "", LanguageProvider.getLanguage("UI000496C013")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }

    private boolean isBadBirthday(String dateBirhday) {
        if (dateBirhday.isEmpty()) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000496C014"));
            return true;
        }
        return false;
    }

    private boolean validationStorage() {
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String birthDate = etBirthday.getText().toString().replace("/", "").replace("-", "");
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
            message = LanguageProvider.getLanguage("UI000496C016");
            title = "";
            iretun = false;
            DialogUtil.createSimpleOkDialog(this, title, message);
        } else {
            iretun = true;
        }
        return iretun;
    }

    private void requestOTPSuccess(String phoneNumber, String validUntil) {
        Intent intent = new Intent(this, ForgotIDPinActivity.class);
        intent.putExtra(IntentConstant.PHONE_NUMBER, phoneNumber);
        intent.putExtra(IntentConstant.OTP_VALIDITY, validUntil);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}

