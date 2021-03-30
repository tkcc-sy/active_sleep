package com.paramount.bed.ble;

public class NSConstants {
    public static final String SERVICE_UUID = "BC2F4CC6-AAEF-4351-9034-D66268E328F0";
    public static final String CHARACTERISTICS_UUID = "06D1E5E7-79AD-4A71-8FAA-373789F7D93C";
    public static final int SCAN_PERIOD = 5000; //in milliseconds
    public static final int REQUEST_ENABLE_BT = 111;
    public static final int MAX_PACKET_LENGTH = 77;
    public static final int NS_CONNECTION_POLL_INTERVAL = 3; // in seconds
    public static final int NS_SET_WIFI_TIMEOUT_INTERVAL = 60; // in seconds
    public static final int NS_CONNECTION_TIMEOUT = 20;
    public static final int NS_GET_POS_POLL_INTERVAL = 1;

    //Bed control
    public static final float REMOTE_COMMAND_TIME_INTERVAL = 0.2f ; //in seconds
    public static final float REMOTE_COMMAND_TIMEOUT = 0.5f ; //in seconds
    public static final int BED_LOWER_THRESHOLD = 2;
    public static final int BED_UPPER_THRESHOLD = 2;

    //Authentication constants
    public static int NS_AUTH_SUCCESS = 0;
    public static int NS_AUTH_FAIL = 1;
    public static int NS_AUTH_REG_SUCCESS = 2;
    public static int NS_AUTH_REG_FAIL = 3;

    //NS Wifi Conn Status
    public static int NS_WIFI_SERVER_CONNECTED = 101;

    //Bed Remote UI
    public static int MAX_BED_HEAD_UI_DEGREE = 55;
    public static int MAX_BED_LEG_UI_DEGREE = -24;
    public static float MAX_BED_LEG_UI_X = -0.03f;
    public static float MAX_BED_LEG_UI_Y = -1.2f;
}
