package com.paramount.bed.util;

import com.paramount.bed.data.model.ServerModel;

public class FeatureUtil {
    public static boolean isProduction() {
        if (ServerModel.getHost().url.contains("asapi")) {
            return true;
        }
        return false;
    }
}
