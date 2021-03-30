package com.paramount.bed.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.paramount.bed.R;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CalendarActivity extends BaseActivity {
    @BindView(R.id.wvCalendar)
    WebView wvCalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle("CALENDAR");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        String calendarHTML = bundle.getString("calendar.html");


        WebSettings webSettings = wvCalendar.getSettings();
        webSettings.setJavaScriptEnabled(true);

        WebViewController webViewController = new WebViewController();
        wvCalendar.setVisibility(View.GONE);

        wvCalendar.addJavascriptInterface(webViewController, "controller");
        wvCalendar.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                ViewUtil.injectJS(getApplicationContext(), wvCalendar, "calendar.js");
                wvCalendar.setVisibility(View.VISIBLE);
            }
        });
        wvCalendar.loadData(calendarHTML, "text/html", "UTF-8");
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_calendar;
    }

    protected boolean useToolbar() {
        return true;
    }

    final class WebViewController {

        @JavascriptInterface
        public void chooseDate(String dateText) {
            Intent intent = new Intent();

            intent.putExtra("date", dateText);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
