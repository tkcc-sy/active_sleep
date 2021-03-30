package com.paramount.bed.util;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class WebViewUtil {
    public static void enableHardwareAcceleration(WebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }
    public static void fixWebViewFonts(WebView webView) {
        webView.getSettings().setTextZoom(100);
    }
}
