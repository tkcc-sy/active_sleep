package com.paramount.bed.ble.pojo;

public class NSRealtimeFeed {
    public int sequence;
    public int data1;
    public int data2;
    public int data3;
    public int data4;
    public int data5;
    public int data6;
    public int data7;
    public int data8;
    public int patientStat;
    public int respRate;
    public int heartRate;

    public NSRealtimeFeed() {
    }
    public NSRealtimeFeed(NSRealtimeFeed nsRealtimeFeed) {
        this.sequence = nsRealtimeFeed.sequence;
        this.data1 = nsRealtimeFeed.data1;
        this.data2 = nsRealtimeFeed.data2;
        this.data3 = nsRealtimeFeed.data3;
        this.data4 = nsRealtimeFeed.data4;
        this.data5 = nsRealtimeFeed.data5;
        this.data6 = nsRealtimeFeed.data6;
        this.data7 = nsRealtimeFeed.data7;
        this.data8 = nsRealtimeFeed.data8;
        this.patientStat = nsRealtimeFeed.patientStat;
        this.respRate = nsRealtimeFeed.respRate;
        this.heartRate = nsRealtimeFeed.heartRate;
    }
    public NSRealtimeFeed(int sequence, int data1, int data2, int data3, int data4, int data5, int data6, int data7, int data8, int patientStat, int respRate, int heartRate) {
        this.sequence = sequence;
        this.data1 = data1;
        this.data2 = data2;
        this.data3 = data3;
        this.data4 = data4;
        this.data5 = data5;
        this.data6 = data6;
        this.data7 = data7;
        this.data8 = data8;
        this.patientStat = patientStat;
        this.respRate = respRate;
        this.heartRate = heartRate;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getData1() {
        return data1;
    }

    public void setData1(int data1) {
        this.data1 = data1;
    }

    public int getData2() {
        return data2;
    }

    public void setData2(int data2) {
        this.data2 = data2;
    }

    public int getData3() {
        return data3;
    }

    public void setData3(int data3) {
        this.data3 = data3;
    }

    public int getData4() {
        return data4;
    }

    public void setData4(int data4) {
        this.data4 = data4;
    }

    public int getData5() {
        return data5;
    }

    public void setData5(int data5) {
        this.data5 = data5;
    }

    public int getData6() {
        return data6;
    }

    public void setData6(int data6) {
        this.data6 = data6;
    }

    public int getData7() {
        return data7;
    }

    public void setData7(int data7) {
        this.data7 = data7;
    }

    public int getData8() {
        return data8;
    }

    public void setData8(int data8) {
        this.data8 = data8;
    }

    public int getPatientStat() {
        return patientStat;
    }

    public void setPatientStat(int patientStat) {
        this.patientStat = patientStat;
    }

    public int getRespRate() {
        return respRate;
    }

    public void setRespRate(int respRate) {
        this.respRate = respRate;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }
}
