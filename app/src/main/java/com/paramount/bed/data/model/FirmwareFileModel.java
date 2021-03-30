package com.paramount.bed.data.model;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class FirmwareFileModel {
    ArrayList<FirmwareRecordModel> records;

    public FirmwareFileModel(String motName, Context context) {
        records = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(motName)));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(new FirmwareRecordModel(line));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<FirmwareBLEPacket> getBLEPackets(){
        ArrayList<FirmwareBLEPacket> result = new ArrayList<>();
        //TODO : Sanity check
        ArrayList<FirmwareRecordModel> preparedRecords = new ArrayList<>();
        for(int i=0;i<records.size();i++){
            //only accepts S3 records
            if(records.get(i).fieldType.equals("S3")){
                preparedRecords.add(records.get(i));
            }
        }
        if(preparedRecords.size() <= 1){
            return result;
        }
        int traversedAddressLength = 0;
        int pointer;
        FirmwareRecordModel lastRecord = null;

        //expanding
        long firstRowAddrInt = Long.parseLong(preparedRecords.get(0).address,16);
        long lastRowAddrInt = Long.parseLong(preparedRecords.get(preparedRecords.size()-1).address,16);

        //data length = distance between first and last address + last data length in bytes
        long calculatedLength = lastRowAddrInt - firstRowAddrInt + (preparedRecords.get(preparedRecords.size()-1).data.length()/2);
        byte rawData[] = new byte[(int)calculatedLength];
        Arrays.fill(rawData,(byte)0xff);

        for (FirmwareRecordModel record:preparedRecords
             ) {
            int distance;
            if(lastRecord != null){
                long currentAddrInt = Long.parseLong(record.address,16);
                long lastAddrInt = Long.parseLong(lastRecord.address,16);
                distance = (int)(currentAddrInt - lastAddrInt);
                traversedAddressLength += distance;
            }
            lastRecord = record;
            pointer = traversedAddressLength;
            for (int sequenceIndex = 0; sequenceIndex < record.data.length(); sequenceIndex += 2) {
                //handle odd character count
                int sequenceEndIndex = sequenceIndex+1;
                if(sequenceEndIndex >= record.data.length()){
                    sequenceEndIndex = record.data.length() - 1;
                }
                String dataString = record.data.substring(sequenceIndex,sequenceEndIndex+1);
                rawData[pointer] = (byte)unsigned2sComplement(Integer.valueOf(dataString, 16));
                pointer += 1;
            }
        }
        //splitting
        String address = "";
        for (int sequenceIndex = 0; sequenceIndex < rawData.length; sequenceIndex += 128) {

            if(address.equals("")){
                address = preparedRecords.get(0).address;
            }else{
                address = Long.toHexString(Long.parseLong(address,16) + 128);
            }
            byte subByte[] = new byte[128];
            System.arraycopy(rawData, sequenceIndex, subByte, 0, subByte.length);

            //FF only check
            boolean isFFOnly = true;
            for (byte singleByte:subByte
                 ) {
                if(singleByte != (byte)0xFF){
                    isFFOnly = false;
                }
            }
            if(!isFFOnly) {
                FirmwareBLEPacket firmwareBLEPacket = new FirmwareBLEPacket(address, bytesToHex(subByte));
                result.add(firmwareBLEPacket);
            }
        }
        return result;
    }
    //TODO : make seperate helper
    private int unsigned2sComplement(int signed2sComplement){
        if(signed2sComplement < 0){
            signed2sComplement = 256+signed2sComplement;
        }
        return signed2sComplement;
    }
    private final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    public class FirmwareBLEPacket {
        String address;
        String data;

        FirmwareBLEPacket(String address, String data) {
            this.address = address;
            this.data = data;
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

        @Override
        public String toString() {
            return "FirmwareBLEPacket{" +
                    "address='" + address + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FirmwareFileModel{" +
                "records=" + records +
                ", hexArray=" + Arrays.toString(hexArray) +
                '}';
    }
}
