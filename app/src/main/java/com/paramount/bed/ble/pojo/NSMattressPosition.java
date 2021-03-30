package com.paramount.bed.ble.pojo;

import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.MHSModel;

import io.realm.RealmList;

public class NSMattressPosition {
    private int id;
    private int head;
    private int shoulder;
    private int hip;
    private int thigh;
    private int calf;
    private int feet;

    private int mattressSound;
    private int dehumidifierOperation;
    private int dehumidifierTime;
    private boolean fukatto;
    private int operationMode;

    public NSMattressPosition() {
        id = 1;
        head = 1;
        shoulder = 1;
        hip = 1;
        thigh = 1;
        calf = 1;
        feet = 1;
    }

    public NSMattressPosition(DeviceTemplateMattressModel deviceTemplateMattressModel) {
        this.id = deviceTemplateMattressModel.getId();
        this.head = deviceTemplateMattressModel.getHead();
        this.shoulder = deviceTemplateMattressModel.getShoulder();
        this.hip = deviceTemplateMattressModel.getHip();
        this.thigh = deviceTemplateMattressModel.getThigh();
        this.calf = deviceTemplateMattressModel.getCalf();
        this.feet = deviceTemplateMattressModel.getFeet();
    }
    public NSMattressPosition(MHSModel mhsModel) {
        if(mhsModel != null){
            RealmList<Integer> hardness = mhsModel.getMattressHardness();
            if(hardness != null && hardness.size() == 6){
                Integer head = hardness.get(0);
                if(head != null){
                    this.head = head;
                }
                Integer shoulder = hardness.get(1);
                if(shoulder != null){
                    this.shoulder = shoulder;
                }
                Integer hip = hardness.get(2);
                if(hip != null){
                    this.hip = hip;
                }
                Integer thigh = hardness.get(3);
                if(thigh != null){
                    this.thigh = thigh;
                }
                Integer calf = hardness.get(4);
                if(calf != null){
                    this.calf = calf;
                }
                Integer feet = hardness.get(5);
                if(feet != null){
                    this.feet = feet;
                }
                this.id = 15;
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getShoulder() {
        return shoulder;
    }

    public void setShoulder(int shoulder) {
        this.shoulder = shoulder;
    }

    public int getHip() {
        return hip;
    }

    public void setHip(int hip) {
        this.hip = hip;
    }

    public int getThigh() {
        return thigh;
    }

    public void setThigh(int thigh) {
        this.thigh = thigh;
    }

    public int getCalf() {
        return calf;
    }

    public void setCalf(int calf) {
        this.calf = calf;
    }

    public int getFeet() {
        return feet;
    }

    public void setFeet(int feet) {
        this.feet = feet;
    }

    public int getMattressSound() {
        return mattressSound;
    }

    public void setMattressSound(int mattressSound) {
        this.mattressSound = mattressSound;
    }

    public int getDehumidifierOperation() {
        return dehumidifierOperation;
    }

    public void setDehumidifierOperation(int dehumidifierOperation) {
        this.dehumidifierOperation = dehumidifierOperation;
    }

    public boolean isFukattoOn() {
        return fukatto;
    }

    public void setFukatto(boolean fukatto) {
        this.fukatto = fukatto;
    }

    public int getOperationMode() {
        return operationMode;
    }

    public void setOperationMode(int operationMode) {
        this.operationMode = operationMode;
    }

    public int getDehumidifierTime() {
        return dehumidifierTime;
    }

    public void setDehumidifierTime(int dehumidifierTime) {
        this.dehumidifierTime = dehumidifierTime;
    }

    public NSMattressPosition clone(){
        NSMattressPosition nsMattressPosition = new NSMattressPosition();
        nsMattressPosition.setId(this.id);
        nsMattressPosition.setHead(this.head);
        nsMattressPosition.setShoulder(this.shoulder);
        nsMattressPosition.setHip(this.hip);
        nsMattressPosition.setThigh(this.thigh);
        nsMattressPosition.setCalf(this.calf);
        nsMattressPosition.setFeet(this.feet);

        return nsMattressPosition;
    }

    public int getValueByIndex(int index){
        int currentValue = 0;
        switch (index){
            case 0:
                currentValue = head;
                break;
            case 1:
                currentValue = shoulder;
                break;
            case 2:
                currentValue = hip;
                break;
            case 3:
                currentValue = thigh;
                break;
            case 4:
                currentValue = calf;
                break;
            case 5:
                currentValue = feet;
                break;
        }
        return currentValue;
    }

    public void setValueByIndex(int index,int value){
        switch (index){
            case 0:
                head = value;
                break;
            case 1:
                shoulder = value;
                break;
            case 2:
                hip = value;
                break;
            case 3:
                thigh = value;
                break;
            case 4:
                calf = value;
                break;
            case 5:
                feet = value;
                break;
        }
    }

    public void copyPositionValueOf(NSMattressPosition nsMattressPosition){
        this.head = nsMattressPosition.getHead();
        this.shoulder = nsMattressPosition.getShoulder();
        this.hip = nsMattressPosition.getHip();
        this.thigh = nsMattressPosition.getThigh();
        this.calf = nsMattressPosition.getCalf();
        this.feet = nsMattressPosition.getFeet();
    }


    @Override
    public String toString() {
        return "NSMattressPosition{" +
                "id=" + id +
                ", head=" + head +
                ", shoulder=" + shoulder +
                ", hip=" + hip +
                ", thigh=" + thigh +
                ", calf=" + calf +
                ", feet=" + feet +
                ", mattressSound=" + mattressSound +
                ", dehumidifierOperation=" + dehumidifierOperation +
                ", dehumidifierTime=" + dehumidifierTime +
                ", fukatto=" + fukatto +
                ", operationMode=" + operationMode +
                '}';
    }
}
