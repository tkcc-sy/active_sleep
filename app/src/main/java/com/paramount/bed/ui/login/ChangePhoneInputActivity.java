package com.paramount.bed.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.RequestOtpResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.datepicker.DateWheelBuilder;
import com.paramount.bed.ui.datepicker.DateWheelPicker;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IntentUtil;
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

public class ChangePhoneInputActivity extends BaseActivity {

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

    String iDataEmail, iDataPassword;
    int iDataSNSProvider;
    boolean iDataIsKickUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000803C001"));
        applyIntentData(getIntent());
        applyView();
        applyListener();
    }

    private void applyIntentData(Intent intent) {
        iDataEmail = intent.getStringExtra(IntentUtil.User.EMAIL);
        iDataPassword = intent.getStringExtra(IntentUtil.User.PASSWORD);
        iDataSNSProvider = intent.getIntExtra(IntentUtil.User.SNS_PROVIDER, 0);
        iDataIsKickUser = intent.getBooleanExtra(IntentUtil.User.IS_KICK_USER, false);
    }

    private void applyView() {
        int progressBarSegment = 2;
        int currentSegment = 1;
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");
        Drawable d = new ProgressDrawable(fgColor, bgColor, progressBarSegment);
        progressBar.setProgressDrawable(d);
        progressBar.setProgress(1000 * currentSegment / progressBarSegment);
    }

    private void applyListener() {
        etBirthday.setOnFocusChangeListener(((view, b) -> {
            if (b) {
                birthdayPicker.show();
                ViewUtil.hideKeyboard(ChangePhoneInputActivity.this);
            }
        }));

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(1975, 1, 1);

        birthdayPicker = new DateWheelBuilder(this,
                (date, v) -> {
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    String outputDate = df.format(date);
                    etBirthday.setText(outputDate);
                })
                .setDate(selectedDate)
                .setCancelText(LanguageProvider.getLanguage("UI000803C019"))
                .setConfirmText(LanguageProvider.getLanguage("UI000803C020"))
                .build();
    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_change_phone_input;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @OnClick(R.id.btnSendSMS)
    public void onBtnRequestOTPClicked() {
        //Initializing
        String vDataPhoneNumber = etPhoneNumber.getText().toString().trim();
        String vDataBirthDay = etBirthday.getText().toString().trim();

        //Validating
        if (ValidationUtils.PHONE.isBad(vDataPhoneNumber, new ValidationUtils.PHONE.BadPhoneListener() {
            @Override
            public void onPhoneEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ChangePhoneInputActivity.this, "", LanguageProvider.getLanguage("UI000803C012")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ChangePhoneInputActivity.this, "", LanguageProvider.getLanguage("UI000803C021")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ChangePhoneInputActivity.this, "", LanguageProvider.getLanguage("UI000803C008")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onPhoneCharsWrong(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(ChangePhoneInputActivity.this, "", LanguageProvider.getLanguage("UI000803C013")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        })) return;
        if (ValidationUtils.STRING.isEmpty(vDataBirthDay, () -> DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000803C014"))))
            return;

        //Refactoring
        String[] birthDateSplit = vDataBirthDay.split("/");
        String birthDate = birthDateSplit[0] + "/" + birthDateSplit[1] + "/" + birthDateSplit[2];

        //Executing
        showLoading();
        mDisposable = userService.ChangePhoneReqOTPV2(vDataPhoneNumber, birthDate, iDataEmail, iDataPassword, iDataSNSProvider, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<RequestOtpResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<RequestOtpResponse> response) {
                        hideLoading();
                        String messageId = response.getMessage();
                        if (response.isSucces()) {
                            if (response.getMessage().equals("USR26-C099")) {
                                sendIntentData(vDataPhoneNumber, response.getData().validUntil);
                                return;
                            }
                            DialogUtil.createSimpleOkDialog(ChangePhoneInputActivity.this, "",
                                    LanguageProvider.getLanguage(messageId),
                                    LanguageProvider.getLanguage("UI000802C003"),
                                    (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        sendIntentData(vDataPhoneNumber, response.getData().validUntil);
                                    });

                            return;
                        }
                        DialogUtil.createSimpleOkDialog(ChangePhoneInputActivity.this, "", LanguageProvider.getLanguage(messageId));

                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            DialogUtil.offlineDialog(ChangePhoneInputActivity.this, getApplicationContext());
                            return;
                        }
                        DialogUtil.serverFailed(ChangePhoneInputActivity.this, "UI000802C085", "UI000802C086", "UI000802C087", "UI000802C088");
                    }
                });
    }

    private void sendIntentData(String phoneNumber, String validUntil) {
        Intent intent = new Intent(this, ChangePhonePinActivity.class);
        intent.putExtra(IntentUtil.User.PHONE, phoneNumber);
        intent.putExtra(IntentUtil.Validity.PIN_VALIDITY, validUntil);
        intent.putExtra(IntentUtil.User.EMAIL, iDataEmail);
        intent.putExtra(IntentUtil.User.PASSWORD, iDataPassword);
        intent.putExtra(IntentUtil.User.SNS_PROVIDER, iDataSNSProvider);
        intent.putExtra(IntentUtil.User.IS_KICK_USER, iDataIsKickUser);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
