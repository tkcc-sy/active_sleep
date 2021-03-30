package com.paramount.bed.util;

import android.content.ContentResolver;
import android.content.res.Configuration;
import android.provider.Settings;

import static android.provider.Settings.System.FONT_SCALE;

public class SystemSettingUtil {
    public static float getFontScale(ContentResolver cr,
                                         Configuration outConfig) {
        outConfig.fontScale = Settings.System.getFloat(
                cr, FONT_SCALE, outConfig.fontScale);
        return outConfig.fontScale;
    }
}
