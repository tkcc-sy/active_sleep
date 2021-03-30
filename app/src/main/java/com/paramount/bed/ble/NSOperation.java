package com.paramount.bed.ble;

public enum NSOperation {
    GET_SERIAL_NUMBER,
    AUTHENTICATE,
    SET_SERVER_URL,
    GET_WIFI,
    SET_WIFI,
    GET_NS_STATUS,
    GET_NS_SPEC,
    GET_BED_SPEC,
    GET_BED_POSITION,
    SET_BED_SETTING,
    FREE_INCREASE_COMBI,
    FREE_DECREASE_COMBI,
    FREE_INCREASE_HEAD,
    FREE_DECREASE_HEAD,
    FREE_INCREASE_HEIGHT,
    FREE_DECREASE_HEIGHT,
    FREE_INCREASE_LEG,
    FREE_DECREASE_LEG,
    FREE_MULTI_BUTTON,
    PRESET_SET_POSITION,
    FREE_TERMINATE,
    SWITCH_FIRMWARE_MODE,
    ENTER_FIRMWARE_MODE,
    WRITE_FIRMWARE,
    EXIT_FIRMWARE_MODE,
    REALTIME_FEED,
    NOTIFY_AUTOMATIC_OPERATION_CHANGE,
    GET_MATTRESS_POSITION,
    SET_MATTRESS_POSITION
    ;

    public byte getCommandCode(){
        switch (this){
            case GET_SERIAL_NUMBER:
                return (byte)0x81;
            case GET_NS_SPEC:
                return (byte)0x82;
            case GET_WIFI:
                return (byte)0x83;
            case AUTHENTICATE:
                return (byte)0x84;
            case SET_WIFI:
                return (byte)0x91;
            case SET_SERVER_URL:
                return (byte)0x92;
            case NOTIFY_AUTOMATIC_OPERATION_CHANGE:
                return (byte)0x93;
            case GET_NS_STATUS:
                return (byte)0xA1;
            case GET_BED_SPEC:
                return (byte)0xB1;
            case GET_BED_POSITION:
                return (byte)0xB2;
            case SET_BED_SETTING:
                return (byte)0xB3;
            case FREE_INCREASE_COMBI:
                return (byte)0xB4;
            case FREE_DECREASE_COMBI:
                return (byte)0xB4;
            case FREE_INCREASE_HEAD:
                return (byte)0xB4;
            case FREE_DECREASE_HEAD:
                return (byte)0xB4;
            case FREE_INCREASE_HEIGHT:
                return (byte)0xB4;
            case FREE_DECREASE_HEIGHT:
                return (byte)0xB4;
            case FREE_INCREASE_LEG:
                return (byte)0xB4;
            case FREE_DECREASE_LEG:
                return (byte)0xB4;
            case FREE_MULTI_BUTTON:
                return (byte)0xB4;
            case FREE_TERMINATE:
                return (byte)0xB4;
            case PRESET_SET_POSITION:
                return (byte)0xB4;
            case SWITCH_FIRMWARE_MODE:
                return (byte)0x94;
            case ENTER_FIRMWARE_MODE:
                return (byte)0xF1;
            case WRITE_FIRMWARE:
                return (byte)0xF2;
            case EXIT_FIRMWARE_MODE:
                return (byte)0xF3;
            case REALTIME_FEED:
                return (byte)0xA2;
            case GET_MATTRESS_POSITION:
                return (byte)0xC1;
            case SET_MATTRESS_POSITION:
                return (byte)0xC2;
        }
        return 0x0;
    }
    public byte getResponseCode(){
        switch (this){
            case GET_SERIAL_NUMBER:
                return (byte)0x01;
            case GET_NS_SPEC:
                return (byte)0x2;
            case GET_WIFI:
                return (byte)0x3;
            case AUTHENTICATE:
                return (byte)0x04;
            case SET_WIFI:
                return (byte)0x11;
            case SET_SERVER_URL:
                return (byte)0x12;
            case NOTIFY_AUTOMATIC_OPERATION_CHANGE:
                return (byte)0x13;
            case GET_NS_STATUS:
                return (byte)0x21;
            case GET_BED_SPEC:
                return (byte)0x31;
            case GET_BED_POSITION:
                return (byte)0x32;
            case SET_BED_SETTING:
                return (byte)0x33;
            case FREE_INCREASE_COMBI:
                return (byte)0x34;
            case FREE_DECREASE_COMBI:
                return (byte)0x34;
            case FREE_INCREASE_HEAD:
                return (byte)0x34;
            case FREE_DECREASE_HEAD:
                return (byte)0x34;
            case FREE_INCREASE_HEIGHT:
                return (byte)0x34;
            case FREE_DECREASE_HEIGHT:
                return (byte)0x34;
            case FREE_INCREASE_LEG:
                return (byte)0x34;
            case FREE_DECREASE_LEG:
                return (byte)0x34;
            case FREE_MULTI_BUTTON:
                return (byte)0x34;
            case FREE_TERMINATE:
                return (byte)0x34;
            case PRESET_SET_POSITION:
                return (byte)0x34;
            case SWITCH_FIRMWARE_MODE:
                return (byte)0x14;
            case ENTER_FIRMWARE_MODE:
                return (byte)0x71;
            case WRITE_FIRMWARE:
                return (byte)0x72;
            case EXIT_FIRMWARE_MODE:
                return (byte)0x73;
            case REALTIME_FEED:
                return (byte)0x22;
            case GET_MATTRESS_POSITION:
                return (byte)0x41;
            case SET_MATTRESS_POSITION:
                return (byte)0x42;
        }
        return 0x0;
    }

    public enum BedOperationType {
        NONE,
        PRESET,
        FREE,
        TERMINATE,
        MULTI_BUTTON,
        OTHER
    }
}
