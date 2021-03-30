package com.paramount.bed.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.ContentTNCAppUpdateModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.VersionModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.front.SliderActivity;
import com.paramount.bed.ui.front.WelcomeActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.ui.registration.TncActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ProgressDrawable;
import com.paramount.bed.util.WebViewUtil;

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

public class TncAppUpdateActivity extends BaseActivity {
    private boolean nextEnabled = false;
    @BindView(R.id.tncWebView)
    WebView tncWebView;

    @BindView(R.id.btnNext)
    Button btnNext;

    HomeService tncService;

    @OnClick(R.id.btnBack)
    void goBack() {
        this.onBackPressed();
    }

    @OnClick(R.id.btnNext)
    void goNext() {
        //Insert OldVersion
        VersionModel.clear();
        VersionModel versionModel = new VersionModel();
        versionModel.setMajor(BuildConfig.VERSION_MAJOR);
        versionModel.setMinor(BuildConfig.VERSION_MINOR);
        versionModel.setRevision(BuildConfig.VERSION_REVISION);
        versionModel.setTNCRead(true);
        versionModel.insert();
        finish();
        try {
            if (!getIntent().getBooleanExtra("IsHome", false)) {
                SliderActivity.activity.getLatestContentVersion();
            } else if (getIntent().getBooleanExtra("IsHome", true)) {
                HomeActivity.activity.getHomeFromAPI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


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

        setToolbarTitle(LanguageProvider.getLanguage("UI000300C001"));

        tncService = ApiClient.getClient(getApplicationContext()).create(HomeService.class);
        tnc = new ContentTNCAppUpdateModel();

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

        applyLocalization();
//        if (UserLogin.getUserLogin() != null) {
//            LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId()), "open_screen", "UI000300", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber());
//        }


    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_appupdate_tnc;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    ContentTNCAppUpdateModel tnc;
    Disposable mDisposable;

    private void getTNCContent() {
        showLoadingIfNotShown();
        mDisposable =
                tncService.getTNCAppUpdateContent(1)
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
                                                DisplayUtils.FONTS.applyFontScale(TncAppUpdateActivity.this,tncWebView);
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
                                            DisplayUtils.FONTS.applyFontScale(TncAppUpdateActivity.this,tncWebView);
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
                                            DisplayUtils.FONTS.applyFontScale(TncAppUpdateActivity.this,tncWebView);
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
                                    DialogUtil.tokenExpireDialog(TncAppUpdateActivity.this);
                                } else {
                                    DialogUtil.serverFailed(TncAppUpdateActivity.this, "UI000802C053", "UI000802C054", "UI000802C055", "UI000802C056");
                                }
                            }
                        });
        ;
    }

    // Inject CSS method: read style.css from assets folder
    // Append stylesheet to document head
    private void injectCSS() {
        try {
            InputStream inputStream = getAssets().open("tnc-style.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            tncWebView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isLoading && !IOSDialogRight.getDialogVisibility()) {
//            super.onBackPressed();
//            finish();
        }
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }
}
