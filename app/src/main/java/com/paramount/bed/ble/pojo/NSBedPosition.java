package com.paramount.bed.ble.pojo;

import com.paramount.bed.data.model.DeviceTemplateBedModel;

public class NSBedPosition {
    private int head;
    private int leg;
    private int height;
    private int defaultTilt;
    private int defaultHeight;

    public NSBedPosition() {
    }
    public NSBedPosition(DeviceTemplateBedModel deviceTemplateBedModel) {
        this.head = deviceTemplateBedModel.getHead();
        this.leg = deviceTemplateBedModel.getLeg();
        this.height = deviceTemplateBedModel.getHeight();
        this.defaultTilt = deviceTemplateBedModel.getTilt_default();
        this.defaultHeight = deviceTemplateBedModel.getHeight_default();
    }
    public NSBedPosition(int head, int leg, int height) {
        this.head = head;
        this.leg = leg;
        this.height = height;
    }

    public NSBedPosition(int head, int leg, int height, int defaultTilt, int defaultHeight) {
        this.head = head;
        this.leg = leg;
        this.height = height;
        this.defaultTilt = defaultTilt;
        this.defaultHeight = defaultHeight;
    }

    public int getDefaultTilt() {
        return defaultTilt;
    }

    public void setDefaultTilt(int defaultTilt) {
        this.defaultTilt = defaultTilt;
    }

    public int getDefaultHeight() {
        return defaultHeight;
    }

    public void setDefaultHeight(int defaultHeight) {
        this.defaultHeight = defaultHeight;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getLeg() {
        return leg;
    }

    public void setLeg(int leg) {
        this.leg = leg;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public NSBedPosition clone(){
        NSBedPosition nsBedPosition = new NSBedPosition();
        nsBedPosition.setHead(this.getHead());
        nsBedPosition.setLeg(this.getLeg());
        nsBedPosition.setHeight(this.getHeight());
        nsBedPosition.setDefaultHeight(this.getDefaultHeight());
        nsBedPosition.setDefaultTilt(this.getDefaultTilt());

        return nsBedPosition;
    }
    @Override
    public String toString() {
        return "NSBedPosition{" +
                "head=" + head +
                ", leg=" + leg +
                ", height=" + height +
                ", defaultTilt=" + defaultTilt +
                ", defaultHeight=" + defaultHeight +
                '}';
    }
}
