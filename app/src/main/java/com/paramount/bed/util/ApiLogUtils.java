package com.paramount.bed.util;

import com.paramount.bed.data.model.UserLogin;

public class ApiLogUtils {
    public static class Global {
        private static int lgCompanyId;
        private static int lgUserId;
        private static String lgNickname;
        private static String lgEmail;
        private static String lgNSSerialNumber;
        private static String lgDeviceType;
        private static String lgOSVersion;
        private static int lgAppType;

        public static int getLgCompanyId() {
            return UserLogin.getUserLogin().getCompanyId();
        }

        public static int getLgUserId() {
            return UserLogin.getUserLogin().getId();
        }

        public static String getLgNickname() {
            return UserLogin.getUserLogin().getNickname();
        }

        public static String getLgEmail() {
            return UserLogin.getUserLogin().getEmail();
        }

        public static String getLgNSSerialNumber() {
            return UserLogin.getUserLogin().getScanSerialNumber();
        }

        public static String getLgDeviceType() {
            return new AndroidSystemUtil().getDeviceType();
        }

        public static String getLgOSVersion() {
            return new AndroidSystemUtil().getOsVersion();
        }

        public static int getLgAppType() {
            return 1;
        }
    }


}
