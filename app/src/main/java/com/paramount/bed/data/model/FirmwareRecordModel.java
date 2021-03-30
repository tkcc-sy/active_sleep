package com.paramount.bed.data.model;

import com.orhanobut.logger.Logger;

public class FirmwareRecordModel {
    String fieldType = "";
    int numOfBytes;
    String address = "";
    String data = "";
    String checksum = "";

    public FirmwareRecordModel(String wholeData) {
        if(wholeData.length() >= 14){
            fieldType = wholeData.substring(0,2);
            address = wholeData.substring(4,12);
            numOfBytes = Integer.parseInt(wholeData.substring(2,4),16);
            int dataLength = (numOfBytes * 2) - 10;
            if(dataLength > 1){
                data = wholeData.substring(12,12+dataLength);
            }

            checksum = wholeData.substring(wholeData.length() - 2, wholeData.length());
        }else{
            Logger.e("Parsing firmware record invalid length "+wholeData);
        }
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public int getNumOfBytes() {
        return numOfBytes;
    }

    public void setNumOfBytes(int numOfBytes) {
        this.numOfBytes = numOfBytes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
}
