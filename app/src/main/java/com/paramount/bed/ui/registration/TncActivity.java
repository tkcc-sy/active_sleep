package com.paramount.bed.ui.registration;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.paramount.bed.R;
import com.paramount.bed.data.model.ContentTNCModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.front.WelcomeActivity;
import com.paramount.bed.ui.main.AutomaticWakeOperationActivity;
import com.paramount.bed.ui.main.FaqActivity;
import com.paramount.bed.ui.main.HomeTNCActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ProgressDrawable;
import com.paramount.bed.util.SystemSettingUtil;
import com.paramount.bed.util.WebViewUtil;
import com.twitter.sdk.android.core.models.User;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.paramount.bed.ui.registration.RegistrationStepActivity.COMPANY_ID;
import static com.paramount.bed.ui.registration.RegistrationStepActivity.CURRENT_FRAGMENT;

public class TncActivity extends BaseActivity {
    private boolean nextEnabled = false;
    private int progressBarStep = 2;
    @BindView(R.id.tncWebView)
    WebView tncWebView;

    @BindView(R.id.btnNext)
    Button btnNext;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    HomeService tncService;

    @OnClick(R.id.btnBack)
    void goBack() {
        this.onBackPressed();
    }

    @OnClick(R.id.btnNext)
    void goNext() {
        CURRENT_FRAGMENT = -1;
        Intent welcomeIntent = new Intent(TncActivity.this, WelcomeActivity.class);
        String companyCode;
        try {
            companyCode = getIntent().getStringExtra("companyCode");
        } catch (Exception e) {
            companyCode = "";
        }
        int iCompanyID = getIntent().getIntExtra("companyId", 0);
//        Log.d("TAG", "goNext: " + companyCode);
        welcomeIntent.putExtra("companyCode", companyCode);
        welcomeIntent.putExtra("companyId", iCompanyID);
        welcomeIntent.putExtra(IntentUtil.User.IS_KICK_USER, getIntent().getBooleanExtra(IntentUtil.User.IS_KICK_USER, false));
        welcomeIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TncActivity.this.startActivity(welcomeIntent);
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @BindView(R.id.chkAgree)
    CheckBox chkAgree;

    @BindView(R.id.lin_chk_aggree)
    LinearLayout lin_chk_aggree;

    boolean statusChecked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        RegistrationStepActivity.PROGRESSBAR_SEGMENT_SUM = 15;
        setToolbarTitle(LanguageProvider.getLanguage("UI000300C001"));

        tncService = ApiClient.getClient(getApplicationContext()).create(HomeService.class);
        tnc = new ContentTNCModel();

        WebSettings mWebSettings = tncWebView.getSettings();
        tncWebView.setScrollbarFadingEnabled(true);
        mWebSettings.setJavaScriptEnabled(true);
        tncWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

        tncWebView.setBackgroundColor(0);

        tncWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        tncWebView.setLongClickable(false);
        WebViewUtil.fixWebViewFonts(tncWebView);
        getTNCContent();

        chkAgree.setOnClickListener((v) -> {
            if (statusChecked) {
                chkAgree.setChecked(true);
                btnNext.setEnabled(true);
                statusChecked = false;
            } else {
                statusChecked = true;
                chkAgree.setChecked(false);
                btnNext.setEnabled(false);
            }
        });

        lin_chk_aggree.setOnClickListener((v) -> {
            if (statusChecked) {
                chkAgree.setChecked(true);
                btnNext.setEnabled(true);
                statusChecked = false;
            } else {
                statusChecked = true;
                chkAgree.setChecked(false);
                btnNext.setEnabled(false);
            }
        });

        Intent myIntent = getIntent();
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");

        if (StatusLogin.getUserLogin() == null) {
            RegistrationStepActivity.PROGRESSBAR_SEGMENT_SUM = 15;
        }

        Drawable d = new ProgressDrawable(fgColor, bgColor, RegistrationStepActivity.PROGRESSBAR_SEGMENT_SUM);
        progressBar.setProgressDrawable(d);
        progressBar.setProgress(1000 * progressBarStep / RegistrationStepActivity.PROGRESSBAR_SEGMENT_SUM);

        applyLocalization();
//        if (UserLogin.getUserLogin() != null) {
//            LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId()), "open_screen", "UI000300", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber());
//        }


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_tnc;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    ContentTNCModel tnc;
    Disposable mDisposable;

    private void getTNCContent() {
        showLoading();
        int iCompanyID = getIntent().getIntExtra("companyId", 0);
        mDisposable =
                tncService.getTNCContent(iCompanyID, 1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                            public void onSuccess(BaseResponse<String> tncResponse) {
                                String html = tncResponse.getData();

                                if (html.equals("")) {
                                    try {
                                        InputStream is = getAssets().open("tnc.html");
                                        int size = is.available();

                                        byte[] buffer = new byte[size];
                                        is.read(buffer);
                                        is.close();

                                        String str = new String(buffer);

                                        tnc.data = str;
                                        tnc.insert();

                                        tncWebView.setWebViewClient(new WebViewClient() {
                                            String currentUrl;

                                            @Override
                                            public void onPageFinished(WebView view, String url) {
                                                // Inject CSS when page is done loading
                                                injectCSS();
                                                // code here
                                                super.onPageFinished(view, url);
                                                DisplayUtils.FONTS.applyFontScale(TncActivity.this,tncWebView);
                                            }
                                        });

                                        tncWebView.loadData(str, "text/html", "UTF-8");

                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }

                                } else {
                                    tnc.data = html;
                                    tnc.insert();

                                    tncWebView.setWebViewClient(new WebViewClient() {
                                        String currentUrl;

                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            // Inject CSS when page is done loading
                                            injectCSS();
                                            // code here
                                            super.onPageFinished(view, url);
                                            DisplayUtils.FONTS.applyFontScale(TncActivity.this,tncWebView);
                                        }
                                    });

                                    tncWebView.loadData(html, "text/html", "UTF-8");
                                }

                                if (tncWebView != null) {
                                    hideLoading();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.d("abx load content 2");
                                Log.d("abx", e.getMessage());

                                try {
                                    InputStream is = getAssets().open("tnc.html");
                                    int size = is.available();

                                    byte[] buffer = new byte[size];
                                    is.read(buffer);
                                    is.close();

                                    String str = new String(buffer);

                                    tnc.data = str;
                                    tnc.insert();

                                    tncWebView.setWebViewClient(new WebViewClient() {
                                        String currentUrl;

                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            // Inject CSS when page is done loading
                                            injectCSS();
                                            // code here
                                            super.onPageFinished(view, url);
                                            DisplayUtils.FONTS.applyFontScale(TncActivity.this,tncWebView);
                                        }
                                    });

                                    tncWebView.loadData(str, "text/html", "UTF-8");

                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }

                                if (tncWebView != null) {
                                    hideLoading();
                                }

                                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
//                                    DialogUtil.offlineDialog(TncActivity.this, getApplicationContext());
                                } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                    DialogUtil.tokenExpireDialog(TncActivity.this);
                                } else {
                                    DialogUtil.serverFailed(TncActivity.this, "UI000802C141", "UI000802C142", "UI000802C143", "UI000802C144");
                                }
                            }
                        });
        ;
    }

    // Inject CSS method: read style.css from assets folder
    // Append stylesheet to document head
    private void injectCSS() {
//        try {
//            InputStream inputStream = getAssets().open("tnc-style.css");
//            byte[] buffer = new byte[inputStream.available()];
//            inputStream.read(buffer);
//            inputStream.close();
//            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
//            tncWebView.loadUrl("javascript:(function() {" +
//                    "var parent = document.getElementsByTagName('head').item(0);" +
//                    "var style = document.createElement('style');" +
//                    "style.type = 'text/css';" +
//                    // Tell the browser to BASE64-decode the string into your script !!!
//                    "style.innerHTML = window.atob('" + encoded + "');" +
//                    "parent.appendChild(style)" +
//                    "})()");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onBackPressed() {
        if (!isLoading && !IOSDialogRight.getDialogVisibility()) {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    public float getFontScale() {
        return SystemSettingUtil.getFontScale(getContentResolver(), getResources().getConfiguration());
    }
}
