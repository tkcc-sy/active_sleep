package com.paramount.bed.ble.pojo;

public class NSSpec {
    private boolean isNSExist;
    private boolean isBedExist;
    private boolean isMattressExist;
    private int bedModel;
    private int nsModel;
    private int mattressModel;
    private int deviceMode;
    private int revision;
    private int minor;
    private int major;

    public NSSpec() {
    }

    public NSSpec(boolean isNSExist, boolean isBedExist, boolean isMattressExist, int bedModel, int nsModel, int mattressModel,int deviceMode, int revision, int minor, int major) {
        this.isNSExist = isNSExist;
        this.isBedExist = isBedExist;
        this.isMattressExist = isMattressExist;
        this.bedModel = bedModel;
        this.nsModel = nsModel;
        this.mattressModel = mattressModel;
        this.deviceMode = deviceMode;
        this.revision = revision;
        this.minor = minor;
        this.major = major;
    }

    public boolean isNSExist() {
        return isNSExist;
    }

    public void setNSExist(boolean NSExist) {
        isNSExist = NSExist;
    }

    public boolean isBedExist() {
        return isBedExist;
    }

    public void setBedExist(boolean bedExist) {
        isBedExist = bedExist;
    }

    public boolean isMattressExist() {
        return isMattressExist;
    }

    public void setMattressExist(boolean mattressExist) {
        isMattressExist = mattressExist;
    }

    public int getBedModel() {
        return bedModel;
    }

    public void setBedModel(int bedModel) {
        this.bedModel = bedModel;
    }

    public int getNsModel() {
        return nsModel;
    }

    public void setNsModel(int nsModel) {
        this.nsModel = nsModel;
    }

    public int getMattressModel() {
        return mattressModel;
    }

    public void setMattressModel(int mattressModel) {
        this.mattressModel = mattressModel;
    }

    public int getDeviceMode() {
        return deviceMode;
    }

    public void setDeviceMode(int deviceMode) {
        this.deviceMode = deviceMode;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public BED_MODEL getBedType(){
        if(bedModel == BED_MODEL.INTIME.ordinal()){
            return BED_MODEL.INTIME;
        }else if(bedModel == BED_MODEL.ACTIVE_SLEEP.ordinal()){
            return BED_MODEL.ACTIVE_SLEEP;
        }else{
            return BED_MODEL.UNKNOWN;
        }
    }

    public enum BED_MODEL {
        UNKNOWN,INTIME,ACTIVE_SLEEP
    }

    public boolean isFWMode(){
        return deviceMode == 1;
    }
}