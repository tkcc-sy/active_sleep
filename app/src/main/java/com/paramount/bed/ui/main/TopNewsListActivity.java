package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.cardview.widget.CardView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.NewsResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ViewUtil;
import com.paramount.bed.util.WebViewUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TopNewsListActivity extends BaseActivity {

    public HomeService homeService;

    @BindView(R.id.wvTopNewsList) WebView wvTopNewsList;
    @BindView(R.id.dialog_news) ConstraintLayout dialogDetail;
    @BindView(R.id.dialog_news_no_connection) ConstraintLayout dialogNoConnection;
    @BindView(R.id.desc_no_connection) TextView descNoConnection;
    @BindView(R.id.btn_ok) Button btnOk;
    @BindView(R.id.rwvNewsSingle) WebView rwvNewsDetail;
    @BindView(R.id.ivClose) ImageView ivClose;
    @BindView(R.id.contentNews) CardView contentNews;
    @BindView(R.id.txtTitle) TextView txtTitle;
    @BindView(R.id.txtDate) TextView txtDate;
    @BindView(R.id.txtReadmore) TextView txtReadmore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000540C001"));
        showProgress();

        wvTopNewsList.setWebViewClient(new TopNewsClient());
        initWebView();
        homeService = ApiClient.getClient(getApplicationContext()).create(HomeService.class);
        dialogDetail.setVisibility(View.GONE);
        dialogNoConnection.setVisibility(View.GONE);
        btnOk.setOnClickListener(v -> dialogNoConnection.setVisibility(View.GONE));

        // check internet connectivity
        if (NetworkUtil.isNetworkConnected(this)) {
            // populate
            getTopNewsContent(UserLogin.getUserLogin().getId(), 0, new NewsListFetchListener() {
                @Override
                public void onNewsListFetched(boolean isSuccess, String data, String errTag) {
                    hideProgress();
                    if(isSuccess && data != null && !data.isEmpty()){
                        LogUserAction.sendNewLog(userService,"TOP_NEWS_LIST_SHOW","","","UI000540");
                        // load HTML
                        runOnUiThread(() -> wvTopNewsList.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "utf-8", ""));
                    }else{
                        if (errTag.equalsIgnoreCase("UI000802C002")) {
                            DialogUtil.offlineDialog(TopNewsListActivity.this,TopNewsListActivity.this );
                        } else if(!errTag.isEmpty()) {
                            DialogUtil.serverFailed(TopNewsListActivity.this, LanguageProvider.getLanguage(errTag), "UI000802C177", "UI000802C003", "UI000802C177");
                        }else{
                            DialogUtil.serverFailed(TopNewsListActivity.this, LanguageProvider.getLanguage("UI000802C001"), "UI000802C177", "UI000802C003", "UI000802C177");
                        }
                    }
                }
            });
        }
        else {
            hideProgress();
            DialogUtil.offlineDialog(this,getApplicationContext());
            LogUserAction.sendNewLog(userService,"INTERNET_CONNECTION_FAILED","","","UI000540");
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_top_news_list;
    }

    @Override
    protected boolean useToolbar() {return true;}

    /**
     * Request API for news list content
     */
    @SuppressLint("CheckResult")
    private void getTopNewsContent(int userId, int retryCount,NewsListFetchListener listener) {
        homeService.getTopNewsList(userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse<String>>() {
                    @Override
                    public void onNext(BaseResponse<String> response) {
                        if(response!=null){
                            if(response.hashCode()==401){
                                listener.onNewsListFetched(false, null,"");
                                DialogUtil.tokenExpireDialog(TopNewsListActivity.this);
                            }else {
                                listener.onNewsListFetched(response.getSuccess(),response.getData(),"");
                            }
                        }else {
                            if(retryCount<BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(() -> getTopNewsContent(userId,retryCount+1,listener),BuildConfig.REQUEST_TIME_OUT);
                            }else {
                                listener.onNewsListFetched(false, null,"UI000802C001");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(() -> getTopNewsContent(userId,retryCount+1,listener),BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            if (!NetworkUtil.isNetworkConnected(TopNewsListActivity.this)) {
                                listener.onNewsListFetched(false, null,"UI000802C002");
                            } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                listener.onNewsListFetched(false, null,"");
                                DialogUtil.tokenExpireDialog(TopNewsListActivity.this);
                            } else {
                                listener.onNewsListFetched(false, null,"UI000802C001");
                            }
                        }
                    }
                    @Override
                    public void onComplete() {

                    }
                });
    }


    private class TopNewsClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();

            // close news -> move to dashboard
            if (url.contains("closeNews")) {
                Intent intent = new Intent(TopNewsListActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            ViewUtil.injectJS(getApplicationContext(), view, "top_news.js");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings webSettings = wvTopNewsList.getSettings();
        wvTopNewsList.setScrollbarFadingEnabled(true);
        wvTopNewsList.getSettings().setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        wvTopNewsList.addJavascriptInterface(this, "controller");
        wvTopNewsList.setWebContentsDebuggingEnabled(true);
        WebViewUtil.enableHardwareAcceleration(wvTopNewsList);
        WebViewUtil.fixWebViewFonts(wvTopNewsList);
    }


    boolean openDetailExecuted = false;
    private boolean shouldExecuteOpenDetail(){
        if(openDetailExecuted){
            return false;
        }else{
            new Handler().postDelayed(() -> openDetailExecuted = false,250);
            openDetailExecuted = true;
            return true;
        }
    }
    @JavascriptInterface
    public void openDetail(int id) {
        if(!shouldExecuteOpenDetail()){ //prevent this function executed twice in a row by webview
            return;
        }

        Logger.d("proletar");
        showProgress();
        //clear existing news
        runOnUiThread(() -> {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            if (Build.VERSION.SDK_INT >= 19) {
                rwvNewsDetail.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                rwvNewsDetail.loadData("","text/html","utf-8");
            }
            else {
                rwvNewsDetail.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                rwvNewsDetail.clearView();
            }

            clearCache();
        });

        getTopNewsDetail(UserLogin.getUserLogin().getId(), id, 0, new NewsFetchListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onNewsDetailFetched(boolean isSuccess,NewsResponse data,String errTag) {
                hideProgress();
                if(isSuccess && data != null && data.getContent() != null && !data.getContent().isEmpty()){
                    rwvNewsDetail.loadDataWithBaseURL("file:///android_asset/", data.getContent(), "text/html", "utf-8", "");
                    dialogDetail.setVisibility(View.VISIBLE);
                    txtTitle.setText(data.getTitle());

                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(data.getCreated_date());
                        String langDate = LanguageProvider.getLanguage("UI000504C001");
                        txtDate.setText(langDate.replace("%YEAR%", new SimpleDateFormat("yyyy").format(data.getCreated_date()))
                                .replace("%MONTH%", new SimpleDateFormat("MM").format(data.getCreated_date()))
                                .replace("%DAY%", new SimpleDateFormat("dd").format(data.getCreated_date())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String langreadmore = LanguageProvider.getLanguage("UI000504C002");
                    SpannableString readmore = new SpannableString(langreadmore);
                    readmore.setSpan(new UnderlineSpan(), 0, readmore.length(), 0);
                    txtReadmore.setText(readmore);
                    txtReadmore.setPaintFlags(txtReadmore.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    if(data.getUrl().length()>0) {
                        txtReadmore.setVisibility(View.VISIBLE);
                        txtReadmore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getUrl()));
                                TopNewsListActivity.this.startActivity(browserIntent);
                            }
                        });
                    }else {
                        txtReadmore.setVisibility(View.INVISIBLE);
                    }

                    ivClose.setOnClickListener(view -> {
                        dialogDetail.setVisibility(View.GONE);
                    });

                    LogUserAction.sendNewLog(userService,"TOP_NEWS_SINGLE",String.valueOf(id),"","UI000540");
                }else{
                    if (errTag.equalsIgnoreCase("UI000802C002")) {
                        DialogUtil.offlineDialog(TopNewsListActivity.this,TopNewsListActivity.this );
                    } else if(!errTag.isEmpty()) {
                        DialogUtil.serverFailed(TopNewsListActivity.this, LanguageProvider.getLanguage(errTag), "UI000802C177", "UI000802C003", "UI000802C177");
                    }else{
                        DialogUtil.serverFailed(TopNewsListActivity.this, LanguageProvider.getLanguage("UI000802C001"), "UI000802C177", "UI000802C003", "UI000802C177");
                    }
                }
            }
        });
    }

    @SuppressLint("CheckResult")
    private void getTopNewsDetail(int userId, int newsId, int retryCount,NewsFetchListener listener) {
        homeService.getTopNewsSingle(userId, newsId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse<NewsResponse>>() {
                    @Override
                    public void onNext(BaseResponse<NewsResponse> response) {
                        if(response != null){
                            if(response.hashCode() == 401){
                                listener.onNewsDetailFetched(false, null,"");
                                DialogUtil.tokenExpireDialog(TopNewsListActivity.this);
                            }else {
                                listener.onNewsDetailFetched(response.getSuccess(), response.getData(),"");
                            }
                        }else {
                            if(retryCount < BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Logger.d("TOP NEWS DETAIL: " + TopNewsListActivity.class.getSimpleName());
                                        getTopNewsDetail(userId,newsId,retryCount+1,listener);
                                        hideProgress();
                                    }
                                },BuildConfig.REQUEST_TIME_OUT);
                            }else{
                                listener.onNewsDetailFetched(false, null,"UI000802C001");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Logger.d("TOP NEWS DETAIL: " + TopNewsListActivity.class.getSimpleName());
                                    getTopNewsDetail(userId,newsId,retryCount+1,listener);
                                }
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            if (!NetworkUtil.isNetworkConnected(TopNewsListActivity.this)) {
                                listener.onNewsDetailFetched(false, null,"UI000802C002");
                            } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                listener.onNewsDetailFetched(false, null,"");
                                DialogUtil.tokenExpireDialog(TopNewsListActivity.this);
                            } else {
                                listener.onNewsDetailFetched(false, null,"UI000802C001");
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void clearCache() {
        rwvNewsDetail.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        rwvNewsDetail.clearCache(true);
        rwvNewsDetail.clearFormData();
        rwvNewsDetail.clearHistory();
        rwvNewsDetail.clearSslPreferences();
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
        WebStorage.getInstance().deleteAllData();
    }
    interface NewsFetchListener{
        void onNewsDetailFetched(boolean isSuccess,NewsResponse data,String errTag);
    }
    interface NewsListFetchListener{
        void onNewsListFetched(boolean isSuccess,String data,String errTag);
    }
}