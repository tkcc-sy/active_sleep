package com.paramount.bed.ui.dialog;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.main.RealtimeMonitorDialog;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.ViewUtil;
import com.paramount.bed.util.WebViewUtil;

import org.w3c.dom.Text;


public class CalendarWebviewDialog {
    private Context context;
    private Dialog mainDialog;
    private WebView mainWebview;
    private String htmlContent;
    private CalendarWebviewDialogDelegate delegate;

    public static Dialog create(Context ctx, String htmlContent, CalendarWebviewDialogDelegate delegate) {
        CalendarWebviewDialog calendarWebviewDialog = new CalendarWebviewDialog(ctx, htmlContent, delegate);
        return calendarWebviewDialog.mainDialog;
    }

    public CalendarWebviewDialog(Context ctx, String htmlString, CalendarWebviewDialogDelegate delegate) {
        this.context = ctx;
        this.htmlContent = htmlString;
        this.delegate = delegate;

        mainDialog = new Dialog(ctx, android.R.style.Theme_Dialog);
        mainDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mainDialog.setContentView(R.layout.dialog_calendar_webview);
        mainDialog.setCanceledOnTouchOutside(true);
        mainDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mainDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                // TODO Auto-generated method stub
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    mainDialog.dismiss();
                    HomeActivity.isCalendarOrDetailVisible = false;
                }
                return false;
            }
        });
        mainWebview = mainDialog.findViewById(R.id.main_webview);
        ImageView btnClose = mainDialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener((v) -> {
            mainDialog.dismiss();
            HomeActivity.isCalendarOrDetailVisible = false;
        });

        mainDialog.setCanceledOnTouchOutside(true);
        mainDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mainDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        LinearLayout calendarDialogue = mainDialog.findViewById(R.id.calendar_dialogue);
        Animation anim = new ScaleAnimation(
                0f, 1f, // Start and end values for the X axis scaling
                0f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(600);
        calendarDialogue.setAnimation(anim);
        initWebview();
        mainWebview.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "utf-8", "");
        TextView titleCalendar = mainDialog.findViewById(R.id.titleCalendar);

        titleCalendar.setText(LanguageProvider.getLanguage("UI000521C001"));
    }

    private void initWebview() {
        WebView.setWebContentsDebuggingEnabled(true);
        mainWebview.setScrollbarFadingEnabled(true);
        mainWebview.setBackgroundColor(Color.TRANSPARENT);

        mainWebview.setVisibility(View.GONE);
        mainWebview.addJavascriptInterface(this, "controller");

        WebSettings webSettings = mainWebview.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        WebViewUtil.fixWebViewFonts(mainWebview);
        mainWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                ViewUtil.injectJS(context, mainWebview, "calendar.js");
                DisplayUtils.FONTS.applyFontScale(context,mainWebview);
                mainWebview.setVisibility(View.VISIBLE);
            }
        });

    }

    @JavascriptInterface
    public void chooseDate(String dateText) {
        HomeActivity.isCalendarOrDetailVisible = false;
        if (delegate != null) {
            delegate.onDateChosen(dateText);
        }
        mainDialog.dismiss();

    }

    public interface CalendarWebviewDialogDelegate {
        void onDateChosen(String dateText);
    }
}
