package com.paramount.bed.ble.pojo;

import java.util.Arrays;

public class NSWifiSetting {
    private int isWifiEnabled;
    private int wifiType;
    private int wifiCommStatus;
    private int wifiRSSI;
    private int operationMode;
    private int channel;
    private String ssid = "";
    private int encryptionMethod;
    private String password = "";
    private int dhcpStatus;
    private int autoDNSStatus;
    private int[] ipAddress = new int[4];
    private int[] subnetMask  = new int[4];
    private int[] defaultGateway  = new int[4];
    private int[] dns1  = new int[4];
    private int[] dns2 = new int[4];
    private int[] macAddress  = new int[17];

    public NSWifiSetting(String ssid, int encryptionMethod, String password) {
        this.ssid = ssid;
        this.encryptionMethod = encryptionMethod;
        this.password = password;

        this.isWifiEnabled = 1;
        this.wifiType = 0;
        this.operationMode = 0;
        this.channel = 0;
        this.dhcpStatus = 1;
        this.autoDNSStatus = 1;
        this.ipAddress = new int[]{0, 0, 0, 0};
        this.subnetMask = new int[]{0, 0, 0, 0};
        this.defaultGateway = new int[]{0, 0, 0, 0};
        this.dns1 = new int[]{0, 0, 0, 0};
        this.dns2 = new int[]{0, 0, 0, 0};

    }

    public NSWifiSetting(byte[] data) {
        this.isWifiEnabled = data[2];
        this.wifiType = data[3];
        this.wifiCommStatus = data[4];
        this.wifiRSSI = data[5];
        this.operationMode = data[6];
        this.channel = data[7];
        this.ssid = new String(Arrays.copyOfRange(data,8,40));
        this.encryptionMethod = data[41];
        this.password = "";//new String(Arrays.copyOfRange(data,42,106));
        this.dhcpStatus = 1; //data[107];
        this.autoDNSStatus = 1;//data[108];
//        int index = 0;
//        for(int i = 109; i<=112;i++){
//            this.ipAddress[index] = data[i];
//            index++;
//        }
//
//        index = 0;
//        for(int i = 113; i<=116;i++){
//            this.subnetMask[index] = data[i];
//            index++;
//        }
//
//        index = 0;
//        for(int i = 117; i<=120;i++){
//            this.defaultGateway[index] = data[i];
//            index++;
//        }
//
//        index = 0;
//        for(int i = 121; i<=124;i++){
//            this.dns1[index] = data[i];
//            index++;
//        }
//
//        index = 0;
//        for(int i = 125; i<=128;i++){
//            this.dns2[index] = data[i];
//            index++;
//        }
//
//        index = 0;
//        for(int i = 129; i<=145;i++){
//            this.macAddress[index] = data[i];
//            index++;
//        }
    }

    public NSWifiSetting(int isWifiEnabled, int wifiType, int wifiCommStatus, int wifiRSSI, int operationMode, int channel, String ssid, int encryptionMenthod, String password, int dhcpStatus, int autoDNSStatus, int[] ipAddress, int[] subnetMask, int[] defaultGateway, int[] dns1, int[] dns2, int[] macAddress) {
        this.isWifiEnabled = isWifiEnabled;
        this.wifiType = wifiType;
        this.wifiCommStatus = wifiCommStatus;
        this.wifiRSSI = wifiRSSI;
        this.operationMode = operationMode;
        this.channel = channel;
        this.ssid = ssid;
        this.encryptionMethod = encryptionMenthod;
        this.password = password;
        this.dhcpStatus = dhcpStatus;
        this.autoDNSStatus = autoDNSStatus;
        this.ipAddress = ipAddress;
        this.subnetMask = subnetMask;
        this.defaultGateway = defaultGateway;
        this.dns1 = dns1;
        this.dns2 = dns2;
        this.macAddress = macAddress;
    }

    @Override
    public String toString() {
        return "NSWifiSetting{" +
                "isWifiEnabled=" + isWifiEnabled +
                ", wifiType=" + wifiType +
                ", wifiCommStatus=" + wifiCommStatus +
                ", wifiRSSI=" + wifiRSSI +
                ", operationMode=" + operationMode +
                ", channel=" + channel +
                ", ssid='" + ssid + '\'' +
                ", encryptionMethod=" + encryptionMethod +
                ", password='" + password + '\'' +
                ", dhcpStatus=" + dhcpStatus +
                ", autoDNSStatus=" + autoDNSStatus +
                ", ipAddress='" + ipAddress + '\'' +
                ", subnetMask='" + subnetMask + '\'' +
                ", defaultGateway='" + defaultGateway + '\'' +
                ", dns1='" + dns1 + '\'' +
                ", dns2='" + dns2 + '\'' +
                ", macAddress='" + macAddress + '\'' +
                '}';
    }

    public int getIsWifiEnabled() {
        return isWifiEnabled;
    }

    public void setIsWifiEnabled(int isWifiEnabled) {
        this.isWifiEnabled = isWifiEnabled;
    }

    public int getWifiType() {
        return wifiType;
    }

    public void setWifiType(int wifiType) {
        this.wifiType = wifiType;
    }

    public int getWifiCommStatus() {
        return wifiCommStatus;
    }

    public void setWifiCommStatus(int wifiCommStatus) {
        this.wifiCommStatus = wifiCommStatus;
    }

    public int getWifiRSSI() {
        return wifiRSSI;
    }

    public void setWifiRSSI(int wifiRSSI) {
        this.wifiRSSI = wifiRSSI;
    }

    public int getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(int operationMode) {
        this.operationMode = operationMode;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getEncryptionMethod() {
        return encryptionMethod;
    }

    public void setEncryptionMethod(int encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDhcpStatus() {
        return dhcpStatus;
    }

    public void setDhcpStatus(int dhcpStatus) {
        this.dhcpStatus = dhcpStatus;
    }

    public int getAutoDNSStatus() {
        return autoDNSStatus;
    }

    public void setAutoDNSStatus(int autoDNSStatus) {
        this.autoDNSStatus = autoDNSStatus;
    }

    public int[] getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(int[] ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int[] getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(int[] subnetMask) {
        this.subnetMask = subnetMask;
    }

    public int[] getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(int[] defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

    public int[] getDns1() {
        return dns1;
    }

    public void setDns1(int[] dns1) {
        this.dns1 = dns1;
    }

    public int[] getDns2() {
        return dns2;
    }

    public void setDns2(int[] dns2) {
        this.dns2 = dns2;
    }

    public int[] getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(int[] macAddress) {
        this.macAddress = macAddress;
    }
}
