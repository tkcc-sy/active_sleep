package com.paramount.bed.util;

import android.app.ActivityManager;
import android.content.Context;

import com.paramount.bed.data.model.LanguageModel;

import java.util.List;

public class LogUtil {
    public static void Logx(String tag, String value) {
        //Only For Debugging Mode
        //        String pre = "[LOGX] ";
        //        try {
        //            System.out.println(pre + "" + tag + " -> " + value);
        //        } catch (Exception e) {
        //            System.out.println(pre + "Exception :" + e.getMessage());
        //        }
    }

    public static void APITracker(String apiName, int sequence, String description) {
        //Only For Debugging Mode
        //        Logx("APITracker", apiName + "[" + sequence + "] -> " + description);
    }

    public static void LanguageTracker(LanguageModel languageModel) {
        //Only For Debugging Mode
        //        Logx("LanguageTracker", "[" + languageModel.getLanguageCode() + "] -> " + languageModel.getTag() + ":" + languageModel.getContent());
    }
}
