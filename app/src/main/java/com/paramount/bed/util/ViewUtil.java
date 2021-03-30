package com.paramount.bed.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;

import java.io.InputStream;

public class ViewUtil {

    public static void disableEditTextKeyboard(EditText et) {
        if (Build.VERSION.SDK_INT >= 11) {
            et.setRawInputType(InputType.TYPE_CLASS_TEXT);
            et.setTextIsSelectable(true);
        } else {
            et.setRawInputType(InputType.TYPE_NULL);
            et.setFocusable(true);
        }
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void injectCSS(Context context, WebView webView, String assetUrl) {
        try {
            InputStream inputStream = context.getAssets().open(assetUrl);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
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

    public static void injectJS(Context context, WebView webView, String assetUrl) {
        try {
            InputStream inputStream = context.getAssets().open(assetUrl);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            String js = ("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
            webView.loadUrl(js);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getString(Context context, String name) {
        Resources res = context.getResources();
        name = name.replace("-", "");
        String text;
        try {
            text = res.getString(res.getIdentifier(name, "string", context.getPackageName()));
        } catch(Exception e) {
            text = name;
        }

        return text;
    }
}
