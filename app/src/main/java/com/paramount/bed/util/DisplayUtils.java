package com.paramount.bed.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.ui.main.HomeActivity;

import static android.content.Context.WINDOW_SERVICE;
import static com.paramount.bed.util.LogUtil.Logx;

public class DisplayUtils {
    public static void adjustFontScale(Context context, Configuration configuration) {
        if (configuration.fontScale != 1) {
            configuration.fontScale = 1;
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            context.getResources().updateConfiguration(configuration, metrics);
        }
    }

    public static void adjustDisplayScale(Context context, Configuration configuration) {
        if (configuration != null) {
            Log.d("TAG", "adjustDisplayScale: " + configuration.densityDpi);
            if (configuration.densityDpi >= 485) //for 6 inch device OR for 538 ppi
                configuration.densityDpi = 500; //decrease "display size" by ~30
            else if (configuration.densityDpi >= 300) //for 5.5 inch device OR for 432 ppi
                configuration.densityDpi = 400; //decrease "display size" by ~30
            else if (configuration.densityDpi >= 100) //for 4 inch device OR for 233 ppi
                configuration.densityDpi = 200; //decrease "display size" by ~30
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.densityDpi * metrics.density;
            context.getResources().updateConfiguration(configuration, metrics);
        }
    }

    public static boolean hasSoftKeys(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;
        boolean hasSoftKey = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        Logx("DisplayUtils",
                "Has Soft Key : " + hasSoftKey +
                        " | Real Height : " + realHeight +
                        " | Real Width : " + realWidth +
                        " | Available Height : " + displayHeight +
                        " | Available Width : " + displayWidth
        );
        return hasSoftKey;
    }

    public static boolean isEqualsAspectRatio(int height, int width, Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        float aspectRatio = (float) height / (float) width;
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;
        float realAspectRatio = (float) realHeight / (float) realWidth;
        boolean isAspectRatio = aspectRatio == realAspectRatio;
        Logx("DisplayUtils",
                "isEqualsAspectRatio : " + isAspectRatio +
                        " | Real Height : " + realHeight +
                        " | Real Width : " + realWidth +
                        " | Real Aspect Ratio : " + realAspectRatio +
                        " | Height : " + height +
                        " | Width : " + width +
                        " | Aspect Ratio : " + aspectRatio

        );
        return isAspectRatio;
    }

    public static class DisplayProperty {
        public int height;
        public int width;
        public float aspectRatio;
        public float density;
        public int densityDPI;
        public double realInch;

        public DisplayProperty(int height, int width, float aspectRatio, float density, int densityDPI, double realInch) {
            this.height = height;
            this.width = width;
            this.aspectRatio = aspectRatio;
            this.density = density;
            this.densityDPI = densityDPI;
            this.realInch = realInch;
        }
    }

    public static class SCREEN {
        public static boolean isSupportedScreen(Context context, SupportScreenListener supportScreenListener) {
            WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            Display d = windowManager.getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;
            int realDensityDPI = realDisplayMetrics.densityDpi;
            float realDensity = realDisplayMetrics.density;
            float realAspectRatio = (float) realHeight / (float) realWidth;

            double x = Math.pow(realWidth / realDisplayMetrics.xdpi, 2);
            double y = Math.pow(realHeight / realDisplayMetrics.ydpi, 2);
            double screenInches = Math.round(Math.sqrt(x + y) * 100) / 100D;

            Log.d("debug", "Screen inches : " + screenInches);

            String screenDescription = "Real Width : " + realWidth +
                    "| Real Height : " + realHeight +
                    " | Real Aspect Ratio : " + realAspectRatio +
                    " | Real Density DPI : " + realDensityDPI +
                    " | Real Density : " + realDensity +
                    " | Real Inch : " + screenInches;
            Logx("DisplayUtils", screenDescription);

            DisplayProperty displayProperty = new DisplayProperty(realHeight, realWidth, realAspectRatio, realDensity, realDensityDPI, screenInches);

            if (anomalyHandler(realWidth, 720) && anomalyHandler(realHeight, 1280)) {
                supportScreenListener.isHD(displayProperty);
                Logx("DisplayUtils", "isHD");
                return true;
            }
            if (anomalyHandler(realWidth, 720) && anomalyHandler(realHeight, 1440)) {
                supportScreenListener.isHD2(displayProperty);
                Logx("DisplayUtils", "isHD2");
                return true;
            }
            if (anomalyHandler(realWidth, 720) && anomalyHandler(realHeight, 1520)) {
                supportScreenListener.isHD2Plus(displayProperty);
                Logx("DisplayUtils", "isHD2Plus");
                return true;
            }
            if (anomalyHandler(realWidth, 768) && anomalyHandler(realHeight, 1366)) {
                supportScreenListener.isWXGA(displayProperty);
                Logx("DisplayUtils", "isWXGA");
                return true;
            }
            if (anomalyHandler(realWidth, 900) && anomalyHandler(realHeight, 1600)) {
                supportScreenListener.isHDPlus(displayProperty);
                Logx("DisplayUtils", "isHDPlus");
                return true;
            }
            if (anomalyHandler(realWidth, 1080) && anomalyHandler(realHeight, 1920)) {
                supportScreenListener.isFHD(displayProperty);
                Logx("DisplayUtils", "isFHD");
                return true;
            }
            if (anomalyHandler(realWidth, 1080) && anomalyHandler(realHeight, 2160)) {
                supportScreenListener.isFHD2(displayProperty);
                Logx("DisplayUtils", "isFHD2");
                return true;
            }
            if (anomalyHandler(realWidth, 1080) && anomalyHandler(realHeight, 2220)) {
                supportScreenListener.isFHDPlus(displayProperty);
                Logx("DisplayUtils", "isFHDPlus");
                return true;
            }
            if (anomalyHandler(realWidth, 1080) && anomalyHandler(realHeight, 2240)) {
                supportScreenListener.isFHD3(displayProperty);
                Logx("DisplayUtils", "isFHD3");
                return true;
            }
            if (anomalyHandler(realWidth, 1080) && anomalyHandler(realHeight, 2270)) {
                supportScreenListener.isFHD4(displayProperty);
                Logx("DisplayUtils", "isFHD4");
                return true;
            }
            if (anomalyHandler(realWidth, 1080) && anomalyHandler(realHeight, 2280)) {
                supportScreenListener.isFHD5(displayProperty);
                Logx("DisplayUtils", "isFHD5");
                return true;
            }
            if (anomalyHandler(realWidth, 1080) && anomalyHandler(realHeight, 2340)) {
                supportScreenListener.isFHD6(displayProperty);
                Logx("DisplayUtils", "isFHD6");
                return true;
            }
            if (anomalyHandler(realWidth, 1440) && anomalyHandler(realHeight, 2560)) {
                supportScreenListener.isQHD(displayProperty);
                Logx("DisplayUtils", "isQHD");
                return true;
            }
            if (anomalyHandler(realWidth, 1440) && anomalyHandler(realHeight, 2880)) {
                supportScreenListener.isQHD2(displayProperty);
                Logx("DisplayUtils", "isQHD2");
                return true;
            }
            if (anomalyHandler(realWidth, 1440) && anomalyHandler(realHeight, 2960)) {
                supportScreenListener.isWQHDPlus(displayProperty);
                Logx("DisplayUtils", "isWQHDPlus");
                return true;
            }
            if (anomalyHandler(realWidth, 1440) && anomalyHandler(realHeight, 3040)) {
                supportScreenListener.isWQHD2Plus(displayProperty);
                Logx("DisplayUtils", "isWQHD2Plus");
                return true;
            }
            if (anomalyHandler(realWidth, 1152) && anomalyHandler(realHeight, 2048)) {
                supportScreenListener.isWQHD3(displayProperty);
                Logx("DisplayUtils", "isWQHD3");
                return true;
            }
            if (anomalyHandler(realWidth, 1620) && anomalyHandler(realHeight, 2880)) {
                supportScreenListener.isWQHD4(displayProperty);
                Logx("DisplayUtils", "isWQHD4");
                return true;
            }
            if (anomalyHandler(realWidth, 1644) && anomalyHandler(realHeight, 3840)) {
                supportScreenListener.isUHDMin(displayProperty);
                Logx("DisplayUtils", "isUHDMin");
                return true;
            }
            if (anomalyHandler(realWidth, 1800) && anomalyHandler(realHeight, 3200)) {
                supportScreenListener.isQHDPlus(displayProperty);
                Logx("DisplayUtils", "isQHDPlus");
                return true;
            }
            if (anomalyHandler(realWidth, 2160) && anomalyHandler(realHeight, 3840)) {
                supportScreenListener.isUHD(displayProperty);
                Logx("DisplayUtils", "isUHD");
                return true;
            }

            return false;
        }

        public interface SupportScreenListener {
            void isHD(DisplayProperty displayProperty);

            void isHD2(DisplayProperty displayProperty);

            void isHD2Plus(DisplayProperty displayProperty);

            void isWXGA(DisplayProperty displayProperty);

            void isHDPlus(DisplayProperty displayProperty);

            void isFHD(DisplayProperty displayProperty);

            void isFHD2(DisplayProperty displayProperty);

            void isFHDPlus(DisplayProperty displayProperty);

            void isFHD3(DisplayProperty displayProperty);

            void isFHD4(DisplayProperty displayProperty);

            void isFHD5(DisplayProperty displayProperty);

            void isFHD6(DisplayProperty displayProperty);

            void isQHD(DisplayProperty displayProperty);

            void isQHD2(DisplayProperty displayProperty);

            void isWQHDPlus(DisplayProperty displayProperty);

            void isWQHD2Plus(DisplayProperty displayProperty);

            void isWQHD3(DisplayProperty displayProperty);

            void isWQHD4(DisplayProperty displayProperty);

            void isUHDMin(DisplayProperty displayProperty);

            void isQHDPlus(DisplayProperty displayProperty);

            void isUHD(DisplayProperty displayProperty);
        }
    }

    public static void applyScreenCompatibility(Activity activity, Context newBaseContext, boolean isFixedFontSize, float normalFont, float bigFont) {
        // ignore the font scale here
        final Configuration nConf = new Configuration(
                newBaseContext.getResources().getConfiguration()
        );

        //If Aspect Ratio 16:9 & Has SoftKeys
        if (isEqualsAspectRatio(16, 9, newBaseContext) && DisplayUtils.hasSoftKeys(newBaseContext)) {
            if (nConf.densityDpi >= 485) //for 6 inch device OR for 538 ppi
                nConf.densityDpi = 500; //decrease "display size" by ~30
            else if (nConf.densityDpi >= 300) //for 5.5 inch device OR for 432 ppi
                nConf.densityDpi = 400; //decrease "display size" by ~30
            else if (nConf.densityDpi >= 100) //for 4 inch device OR for 233 ppi
                nConf.densityDpi = 200; //decrease "display size" by ~30
        }
        Logx("DisplayUtils:FontScale", String.valueOf(nConf.fontScale));
        DisplayUtils.SCREEN.isSupportedScreen(newBaseContext, new SCREEN.SupportScreenListener() {
            @Override
            public void isHD(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 240) {
                    nConf.densityDpi = 280;
                } else if (displayProperty.densityDPI == 320) {
                    nConf.densityDpi = 280;
                } else {
                    nConf.densityDpi = 280;
                }
            }

            @Override
            public void isHD2(DisplayProperty displayProperty) {

            }

            @Override
            public void isHD2Plus(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 240) {
                    nConf.densityDpi = 295;
                }
            }

            @Override
            public void isWXGA(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 320) {
                    nConf.densityDpi = 295;
                }
            }

            @Override
            public void isHDPlus(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 320) {
                    nConf.densityDpi = 360;
                }
            }

            @Override
            public void isFHD(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 320) {
                    nConf.densityDpi = 430;
                }
                if (displayProperty.densityDPI == 480) {
                    nConf.densityDpi = 420;
                }
            }

            @Override
            public void isFHD2(DisplayProperty displayProperty) {

            }

            @Override
            public void isFHDPlus(DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD3(DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD4(DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD5(DisplayProperty displayProperty) {

            }

            @Override
            public void isFHD6(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 440) {
                    nConf.densityDpi = 480;
                }
            }

            @Override
            public void isQHD(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 480) {
                    nConf.densityDpi = 570;
                }
                if (displayProperty.densityDPI == 640) {
                    nConf.densityDpi = 560;
                }
            }

            @Override
            public void isQHD2(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 480) {
                    nConf.densityDpi = 580;
                }
            }

            @Override
            public void isWQHDPlus(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 480) {
                    nConf.densityDpi = 590;
                }
            }

            @Override
            public void isWQHD2Plus(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 480) {
                    nConf.densityDpi = 580;
                }
                if (displayProperty.densityDPI == 640) {
//                    nConf.densityDpi = 660;
                }
            }

            @Override
            public void isWQHD3(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 480) {
                    nConf.densityDpi = 460;
                }
            }

            @Override
            public void isWQHD4(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 640) {
                    nConf.densityDpi = 645;
                }
            }

            @Override
            public void isUHDMin(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 640) {
                    nConf.densityDpi = 780;
                }
            }

            @Override
            public void isQHDPlus(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 640) {
                    nConf.densityDpi = 720;
                }
            }

            @Override
            public void isUHD(DisplayProperty displayProperty) {
                if (displayProperty.densityDPI == 640) {
                    nConf.densityDpi = 880;
                }
            }
        });
        if (isFixedFontSize) {
            nConf.fontScale = FONTS.bigFontStatus(newBaseContext) ? bigFont : normalFont;
        }
        activity.applyOverrideConfiguration(nConf);
    }


    public static class FONTS {
        public static void bigFontStatus(Context context, boolean value) {
            SharedPreferences mSettings = context.getSharedPreferences("BED_DISPLAY_SETTINGS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean("BED_BIGFONT", value);
            editor.apply();
            needRestart(context, true);
        }

        public static boolean bigFontStatus(Context context) {
            //Revert BigFont Settings
            if (true) {
                return false;
            }
            if (ApiClient.LogData.getLoginStatus(context) == 2) {
                return false;
            }
            SharedPreferences monitoringUserLog = context.getSharedPreferences("BED_DISPLAY_SETTINGS", Context.MODE_PRIVATE);
            return monitoringUserLog.getBoolean("BED_BIGFONT", false);
        }

        public static void needRestart(Context context, boolean value) {
            SharedPreferences mSettings = context.getSharedPreferences("BED_DISPLAY_SETTINGS", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean("BED_BIGFONT_NEEDRESTART", value);
            editor.apply();
        }

        public static boolean needRestart(Context context) {
            SharedPreferences monitoringUserLog = context.getSharedPreferences("BED_DISPLAY_SETTINGS", Context.MODE_PRIVATE);
            return monitoringUserLog.getBoolean("BED_BIGFONT_NEEDRESTART", false);
        }

        public static String injectSetFontValue(Context context) {
            Logx("setFontScale ->", DisplayUtils.FONTS.bigFontStatus(context) ? "BIGFONT (2)" : "NORMAL (1)");
            String fontValue = String.valueOf(DisplayUtils.FONTS.bigFontStatus(context) ? 2 : 1);
            String inject = "setFontScale(" + fontValue + "); console.log('setFontScale(" + fontValue + ")');";
            return inject;
        }

        public static void applyFontScale(Context context, WebView webView) {
            String javascript = "javascript:(function(){" +
                    "if(typeof setFontScale==\"function\"){" +
                    DisplayUtils.FONTS.injectSetFontValue(context) +
                    "}" +
                    "})()";
            webView.loadUrl(javascript);
        }
    }

    public static boolean anomalyHandler(int realValue, int idealValue) {
        //Give Treshold -5 and +5 from ideal resolution value
        int minTreshold = -5;
        int maxTreshold = +5;
        if (realValue >= (idealValue + minTreshold) && realValue <= (idealValue + maxTreshold)) {
            return true;
        }
        return false;
    }
}
