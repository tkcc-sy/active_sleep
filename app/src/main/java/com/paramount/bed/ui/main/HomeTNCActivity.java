package com.paramount.bed.ui.main;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.view.GravityCompat;

import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.paramount.bed.R;
import com.paramount.bed.data.model.ContentTNCModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.SystemSettingUtil;
import com.paramount.bed.util.WebViewUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class HomeTNCActivity extends BaseActivity {


    @BindView(R.id.main_webview)
    public WebView mainWebView;

    ContentTNCModel tnc;
    HomeService tncService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000770C001"));
        tncService = ApiClient.getClient(getApplicationContext()).create(HomeService.class);
        tnc = new ContentTNCModel();
        getTNCContent();
        mainWebView.setScrollbarFadingEnabled(true);
        mainWebView.setBackgroundColor(Color.TRANSPARENT);

        WebSettings webSettings = mainWebView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        WebViewUtil.fixWebViewFonts(mainWebView);
//        initWebview();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home_tnc;
    }

    private void initWebview() {


        String html = ContentTNCModel.getTNC() == null ? "" : ContentTNCModel.getTNC().getData();

        if (html.equals("")) {
            try {
                InputStream is = getAssets().open("tnc.html");
                int size = is.available();

                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                String str = new String(buffer);

                mainWebView.setWebViewClient(new WebViewClient() {
                    String currentUrl;

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        // Inject CSS when page is done loading
                        injectCSS();
                        // code here
                        super.onPageFinished(view, url);
                        DisplayUtils.FONTS.applyFontScale(HomeTNCActivity.this,mainWebView);
                    }
                });

                mainWebView.loadData(str, "text/html", "UTF-8");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else {

            mainWebView.setWebViewClient(new WebViewClient() {
                String currentUrl;

                @Override
                public void onPageFinished(WebView view, String url) {
                    // Inject CSS when page is done loading
                    injectCSS();
                    // code here
                    super.onPageFinished(view, url);
                    DisplayUtils.FONTS.applyFontScale(HomeTNCActivity.this,mainWebView);
                }
            });

            mainWebView.loadData(html, "text/html", "UTF-8");
        }
//        mainWebView.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onPageFinished(WebView view, String url) {
//            }
//        });
//        mainWebView.loadUrl("file:///android_asset/tnc.html");
//        if (UserLogin.getUserLogin() != null) {
//            LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId()), "open_screen", "UI000770", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber());
//        }
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }


    ContentTNCModel tncModel;
    Disposable mDisposable;

    private void getTNCContent() {
        showLoading();
        int iCompanyID = 0;
        try {
            iCompanyID = UserLogin.getUserLogin().getCompanyId();
        } catch (Exception e) {
            e.printStackTrace();
            iCompanyID = 0;
        }
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
                                        ContentTNCModel.clear();
                                        tncModel = new ContentTNCModel();
                                        tncModel.setData(str);

                                        mainWebView.setWebViewClient(new WebViewClient() {
                                            String currentUrl;

                                            @Override
                                            public void onPageFinished(WebView view, String url) {
                                                // Inject CSS when page is done loading
                                                injectCSS();
                                                // code here
                                                super.onPageFinished(view, url);
                                                DisplayUtils.FONTS.applyFontScale(HomeTNCActivity.this,mainWebView);
                                            }
                                        });

                                        mainWebView.loadData(str, "text/html", "UTF-8");

                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }

                                } else {
                                    ContentTNCModel.clear();
                                    tncModel = new ContentTNCModel();
                                    tncModel.setData(html);
                                    tncModel.insert();

                                    mainWebView.setWebViewClient(new WebViewClient() {
                                        String currentUrl;

                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            // Inject CSS when page is done loading
                                            injectCSS();
                                            // code here
                                            super.onPageFinished(view, url);
                                            DisplayUtils.FONTS.applyFontScale(HomeTNCActivity.this,mainWebView);
                                        }
                                    });

                                    mainWebView.loadData(html, "text/html", "UTF-8");
                                }

                                if (mainWebView != null) {
                                    hideLoading();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.d("abx load content 2");
                                Log.d("abx", e.getMessage());

                                try {
                                    String str = "";
                                    if (ContentTNCModel.getTNC() == null || ContentTNCModel.getTNC().getData() == null) {


                                        InputStream is = getAssets().open("tnc.html");
                                        int size = is.available();

                                        byte[] buffer = new byte[size];
                                        is.read(buffer);
                                        is.close();

                                        str = new String(buffer);

                                        tnc.data = str;
                                        tnc.insert();
                                    } else {
                                        str = ContentTNCModel.getTNC().getData();
                                    }
                                    mainWebView.setWebViewClient(new WebViewClient() {
                                        String currentUrl;

                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            // Inject CSS when page is done loading
                                            injectCSS();
                                            // code here
                                            super.onPageFinished(view, url);
                                            DisplayUtils.FONTS.applyFontScale(HomeTNCActivity.this,mainWebView);
                                        }
                                    });

                                    mainWebView.loadData(str, "text/html", "UTF-8");

                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }

                                if (mainWebView != null) {
                                    hideLoading();
                                }

                                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
//                                    DialogUtil.offlineDialog(TncActivity.this, getApplicationContext());
                                } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                    DialogUtil.tokenExpireDialog(HomeTNCActivity.this);
                                } else {
                                    DialogUtil.serverFailed(HomeTNCActivity.this, "UI000802C073", "UI000802C074", "UI000802C075", "UI000802C076");
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
//            mainWebView.loadUrl("javascript:(function() {" +
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
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HomeActivity.drawerLayout.closeDrawer(GravityCompat.START, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    public float getFontScale() {
        return SystemSettingUtil.getFontScale(getContentResolver(), getResources().getConfiguration());
    }
}
