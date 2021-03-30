package com.paramount.bed.ble;

public class NSFailCode {
    public static String bedHumanReadableError(int failCode) {
        switch (failCode) {
            case 1:
                return "U1";
            case 2:
                return "U2";
            case 3:
                return "U3";
            case 4:
                return "U4";
            case 5:
                return "U5";
            case 6:
                return "U6";
            case 7:
                return "U7";
            case 8:
                return "U8";
            case 9:
                return "U9";
            case 80:
                return "U0";
            case 100:
                return "H0";
            case 101:
                return "H1";
            case 102:
                return "H2";
            case 103:
                return "H3";
            case 104:
                return "H4";
            case 105:
                return "H5";
            case 106:
                return "H6";
            case 107:
                return "H7";
            case 108:
                return "H8";
            case 109:
                return "H9";
            default:
                return String.valueOf(failCode);
        }
    }

    public static String mattressHumanReadableError(int failCode) {
        switch (failCode) {
            case 2:
                return "H02";
            case 3:
                return "H03";
            case 7:
                return "H07";
            case 8:
                return "H08";
            case 10:
                return "U10";
            case 11:
                return "U11";
            case 12:
                return "U12";
            case 13:
                return "U13";
            case 14:
                return "U14";
            case 15:
                return "U15";
            case 16:
                return "U16";
            case 21:
                return "U21";
            case 22:
                return "U22";
            case 23:
                return "U23";
            case 24:
                return "U24";
            case 25:
                return "U25";
            case 26:
                return "U26";
            case 28:
                return "U28";
            case 29:
                return "U29";
            default:
                return String.valueOf(failCode);
        }
    }
}
