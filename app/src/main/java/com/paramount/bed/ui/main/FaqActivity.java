package com.paramount.bed.ui.main;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.ContentFaqModel;
import com.paramount.bed.data.model.FAQLinkModel;
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
import com.paramount.bed.util.RxUtil;
import com.paramount.bed.util.WebViewUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class FaqActivity extends BaseActivity {
    @BindView(R.id.wvFaq)
    WebView wvFaq;
    @BindView(R.id.tvInquiry)
    TextView tvInquiry;
    @BindView(R.id.btnDisplayInquiry)
    Button btnDisplayInquiry;

    @OnClick(R.id.btnDisplayInquiry)
    void displayInquiry() {
        Intent intent = new Intent(this, InquiryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    HomeService faqService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000760C001"));

        faqService = ApiClient.getClientFAQ(getApplicationContext()).create(HomeService.class);

        clearCache();

        if (NetworkUtil.isNetworkConnected(this)) {
            WebSettings webSettings = wvFaq.getSettings();
            wvFaq.setScrollbarFadingEnabled(true);
            webSettings.setJavaScriptEnabled(true);
            WebViewUtil.fixWebViewFonts(wvFaq);
            getFaqContent();
        } else {
            DialogUtil.offlineDialog(FaqActivity.this, getApplicationContext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserLogin.getUserLogin() == null || UserLogin.getUserLogin().getId() == null || !UserLogin.isLogin()) {
            tvInquiry.setVisibility(View.GONE);
            btnDisplayInquiry.setVisibility(View.GONE);
        } else {
            tvInquiry.setVisibility(View.VISIBLE);
            btnDisplayInquiry.setVisibility(View.VISIBLE);
        }
        if(DisplayUtils.FONTS.bigFontStatus(FaqActivity.this)) {
            btnDisplayInquiry.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
        AlarmsQuizModule.run(this);
    }

    Disposable mDisposable;

    private void getFaqContent() {
        showLoading();
        mDisposable =
                faqService.getFaqContent(1)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                            public void onSuccess(BaseResponse<String> faqResponse) {
                                hideLoading();
                                String html = faqResponse.getData();
                                ContentFaqModel faq = new ContentFaqModel();
                                faq.setData(html);
                                faq.insert();

                                wvFaq.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    wvFaq.setWebContentsDebuggingEnabled(true);
                                }
                                wvFaq.setWebViewClient(new WebViewClient() {
                                    public void onPageFinished(WebView view, String url) {
                                        DisplayUtils.FONTS.applyFontScale(FaqActivity.this,wvFaq);
                                        if (getIntent().getStringExtra("ID_FAQ") != null) {
                                            String ID = FAQLinkModel.getLinkByTag(getIntent().getStringExtra("ID_FAQ"));
                                            if(ID != null && !ID.isEmpty()) {
                                                String javascript = "javascript:openLink('" + ID + "')";
                                                wvFaq.loadUrl(javascript);
                                            }
                                        }
                                    }
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                                        String url = request.getUrl().toString();
                                        try {
                                            // ブラウザ起動
                                            view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                                        } catch (ActivityNotFoundException e) {
                                            // ブラウザアプリが有効でない場合はここに入る
                                            // 必要ならエラー表示とかする
                                            e.printStackTrace();
                                        }
                                        return true;
                                    }
                                });

                            }

                            @Override
                            public void onError(Throwable e) {
                                hideLoading();
                                if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                                    DialogUtil.offlineDialog(FaqActivity.this, getApplicationContext());
                                } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                    DialogUtil.tokenExpireDialog(FaqActivity.this);
                                } else {
                                    DialogUtil.serverFailed(FaqActivity.this, "UI000802C057", "UI000802C058", "UI000802C059", "UI000802C060");
                                }
                                Timber.d("abx load content 2");
                                Log.d("abx", e.getMessage());
                            }
                        });
        ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HomeActivity.ISFAQRUN = false;
        RxUtil.dispose(mDisposable);
        try {
            HomeActivity.drawerLayout.closeDrawer(GravityCompat.START, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_faq;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    private void clearCache() {
        wvFaq.clearCache(true);
        wvFaq.clearFormData();
        wvFaq.clearHistory();
        wvFaq.clearSslPreferences();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        WebStorage.getInstance().deleteAllData();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
