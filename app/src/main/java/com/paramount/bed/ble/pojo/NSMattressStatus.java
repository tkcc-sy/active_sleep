package com.paramount.bed.ble.pojo;

public class NSMattressStatus {
    private boolean isDehumidifierBusy;
    private boolean isFeetBusy;
    private boolean isCalfBusy;
    private boolean isThighBusy;
    private boolean isHipBusy;
    private boolean isShoulderBusy;
    private boolean isHeadBusy;
    private boolean isFailCodeH;

    public boolean isDehumidifierBusy() {
        return isDehumidifierBusy;
    }

    public void setDehumidifierBusy(boolean dehumidifierBusy) {
        isDehumidifierBusy = dehumidifierBusy;
    }

    public boolean isFeetBusy() {
        return isFeetBusy;
    }

    public void setFeetBusy(boolean feetBusy) {
        isFeetBusy = feetBusy;
    }

    public boolean isCalfBusy() {
        return isCalfBusy;
    }

    public void setCalfBusy(boolean calfBusy) {
        isCalfBusy = calfBusy;
    }

    public boolean isThighBusy() {
        return isThighBusy;
    }

    public void setThighBusy(boolean thighBusy) {
        isThighBusy = thighBusy;
    }

    public boolean isHipBusy() {
        return isHipBusy;
    }

    public void setHipBusy(boolean hipBusy) {
        isHipBusy = hipBusy;
    }

    public boolean isShoulderBusy() {
        return isShoulderBusy;
    }

    public void setShoulderBusy(boolean shoulderBusy) {
        isShoulderBusy = shoulderBusy;
    }

    public boolean isHeadBusy() {
        return isHeadBusy;
    }

    public void setHeadBusy(boolean headBusy) {
        isHeadBusy = headBusy;
    }

    public boolean isFailCodeH() {
        return isFailCodeH;
    }

    public void setFailCodeH(boolean failCodeH) {
        isFailCodeH = failCodeH;
    }

    @Override
    public String toString() {
        return "NSMattressStatus{" +
                "isDehumidifierBusy=" + isDehumidifierBusy +
                ", isFeetBusy=" + isFeetBusy +
                ", isCalfBusy=" + isCalfBusy +
                ", isThighBusy=" + isThighBusy +
                ", isHipBusy=" + isHipBusy +
                ", isShoulderBusy=" + isShoulderBusy +
                ", isHeadBusy=" + isHeadBusy +
                ", isFailCodeH=" + isFailCodeH +
                '}';
    }
}
