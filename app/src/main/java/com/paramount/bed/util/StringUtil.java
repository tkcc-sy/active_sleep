package com.paramount.bed.util;

import java.util.regex.Pattern;

public class StringUtil {
    private final static Pattern LTRIM = Pattern.compile("^\\s+");
    private final static Pattern RTRIM = Pattern.compile("\\s+$");

    public static String ltrim(String s) {
        return LTRIM.matcher(s).replaceAll("");
    }

    public static String rtrim(String s) {
        return RTRIM.matcher(s).replaceAll("");
    }

    public static String lrtrim(String s) {
        return rtrim(ltrim(s));
    }

    public static String nickName(String s) {
        return lrtrim(s).replaceAll(" +"," ");
    }
}
