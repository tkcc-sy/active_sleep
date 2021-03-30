package com.paramount.bed.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ligl.android.widget.iosdialog.IOSDialog;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.AutoSizeTextUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IntentConstant;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class InquiryPreviewActivity extends BaseActivity {
    @BindView(R.id.tvInquiryType)
    TextView tvInquiryType;

    @BindView(R.id.textInquiryType)
    TextView textInquiryType;

    @BindView(R.id.textProduct)
    TextView textProduct;

    @BindView(R.id.textEmail)
    TextView textEmail;

    @BindView(R.id.textName)
    TextView textName;

    @BindView(R.id.textPhoneNumber)
    TextView textPhoneNumber;

    @BindView(R.id.textInquiry)
    TextView textInquiry;

    @BindView(R.id.scroll_view)
    LinearLayout scrollView;

    Integer inquiryTypeId = 0;
    String inquirytype = "";
    String product = "";
    String email = "";
    String surname = "";
    String nickname = "";
    String name = "";
    String phoneNumber = "";
    String inquiry = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000781C001"));

        if (getIntent() != null) {
            Intent intent = getIntent();
            inquiryTypeId = intent.getIntExtra(IntentConstant.INQUIRY_TYPE_ID, 0);
            inquirytype = intent.getStringExtra(IntentConstant.INQUIRY_TYPE);
            product = intent.getStringExtra(IntentConstant.PRODUCT);
            email = intent.getStringExtra(IntentConstant.EMAIL);
            surname = intent.getStringExtra(IntentConstant.FIRST_NAME);
            name = intent.getStringExtra(IntentConstant.LAST_NAME);
            nickname = intent.getStringExtra(IntentConstant.NICK_NAME);
            phoneNumber = intent.getStringExtra(IntentConstant.PHONE_NUMBER);
            inquiry = intent.getStringExtra(IntentConstant.INQUIRY);
        }

        initView();

        textInquiryType.setText(inquirytype);
        textProduct.setText(product);
        textEmail.setText(email);
        textName.setText(nickname);
        textPhoneNumber.setText(phoneNumber);
        textInquiry.setText(inquiry);

        AutoSizeTextUtil.setAutoSizeText(textInquiryType);
        AutoSizeTextUtil.setAutoSizeText(textProduct);
        AutoSizeTextUtil.setAutoSizeText(textEmail);
        AutoSizeTextUtil.setAutoSizeText(textName);
    }

    private void initView() {
        textInquiry.setMovementMethod(new ScrollingMovementMethod());
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textInquiry.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        textInquiry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                textInquiry.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_inquiry_preview;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @OnClick(R.id.btnNext)
    public void onNextClick() {
        if (BuildConfig.DEMO_MODE) {
            showDialogSubmitSuccess();
            return;
        }

        showLoading();
        mDisposable = inquiryService.submit(inquiryTypeId, product, email, surname, name, phoneNumber, inquiry, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        hideLoading();
                        if (response.isSucces()) {
                            DialogUtil.createSimpleOkDialog(InquiryPreviewActivity.this, "",
                                    LanguageProvider.getLanguage(response.getMessage()),
                                    LanguageProvider.getLanguage("UI000781C013"),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(InquiryPreviewActivity.this, HomeActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                            startActivity(intent);
                                        }
                                    });
                        } else {
                            DialogUtil.createSimpleOkDialog(InquiryPreviewActivity.this, "", LanguageProvider.getLanguage(response.getMessage()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            DialogUtil.offlineDialog(InquiryPreviewActivity.this, getApplicationContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(InquiryPreviewActivity.this);
                        } else {
                            DialogUtil.serverFailed(InquiryPreviewActivity.this, "UI000802C061", "UI000802C062", "UI000802C063", "UI000802C064");
                        }
                    }
                });
    }

    private void showDialogSubmitSuccess() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogUtil.createSimpleOkDialog(InquiryPreviewActivity.this,
                        "",
                        LanguageProvider.getLanguage("UI000781C012"), LanguageProvider.getLanguage("UI000781C013"), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(InquiryPreviewActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        }, 300);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }
}
