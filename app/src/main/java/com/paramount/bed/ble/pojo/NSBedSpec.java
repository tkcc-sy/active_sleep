package com.paramount.bed.ble.pojo;

public class NSBedSpec {
    private int headLowerRange,headUpperRange,legLowerRange, legUpperRange, heightLowerRange, heightUpperRange, tiltLowerRange, tiltUpperRange;
    private boolean isHeightLockSupported,isHeadLockSupported,isLegLockSupported,isCombiLockSupported;

    public NSBedSpec(int headLowerRange, int headUpperRange, int legLowerRange, int legUpperRange, int heightLowerRange, int heightUpperRange, int tiltLowerRange, int tiltUpperRange) {
        this.headLowerRange = headLowerRange;
        this.headUpperRange = headUpperRange;
        this.legLowerRange = legLowerRange;
        this.legUpperRange = legUpperRange;
        this.heightLowerRange = heightLowerRange;
        this.heightUpperRange = heightUpperRange;
        this.tiltLowerRange = tiltLowerRange;
        this.tiltUpperRange = tiltUpperRange;
    }

    public NSBedSpec(int headLowerRange, int headUpperRange, int legLowerRange, int legUpperRange, int heightLowerRange, int heightUpperRange, int tiltLowerRange, int tiltUpperRange, boolean isHeightLockSupported, boolean isHeadLockSupported, boolean isLegLockSupported, boolean isCombiLockSupported) {
        this.headLowerRange = headLowerRange;
        this.headUpperRange = headUpperRange;
        this.legLowerRange = legLowerRange;
        this.legUpperRange = legUpperRange;
        this.heightLowerRange = heightLowerRange;
        this.heightUpperRange = heightUpperRange;
        this.tiltLowerRange = tiltLowerRange;
        this.tiltUpperRange = tiltUpperRange;
        this.isHeightLockSupported = isHeightLockSupported;
        this.isHeadLockSupported = isHeadLockSupported;
        this.isLegLockSupported = isLegLockSupported;
        this.isCombiLockSupported = isCombiLockSupported;
    }

    public boolean isHeightLockSupported() {
        return isHeightLockSupported;
    }

    public void setHeightLockSupported(boolean heightLockSupported) {
        isHeightLockSupported = heightLockSupported;
    }

    public boolean isHeadLockSupported() {
        return isHeadLockSupported;
    }

    public void setHeadLockSupported(boolean headLockSupported) {
        isHeadLockSupported = headLockSupported;
    }

    public boolean isLegLockSupported() {
        return isLegLockSupported;
    }

    public void setLegLockSupported(boolean legLockSupported) {
        isLegLockSupported = legLockSupported;
    }

    public boolean isCombiLockSupported() {
        return isCombiLockSupported;
    }

    public void setCombiLockSupported(boolean combiLockSupported) {
        isCombiLockSupported = combiLockSupported;
    }

    public int getHeadLowerRange() {
        return headLowerRange;
    }

    public void setHeadLowerRange(int headLowerRange) {
        this.headLowerRange = headLowerRange;
    }

    public int getHeadUpperRange() {
        return headUpperRange;
    }

    public void setHeadUpperRange(int headUpperRange) {
        this.headUpperRange = headUpperRange;
    }

    public int getLegLowerRange() {
        return legLowerRange;
    }

    public void setLegLowerRange(int legLowerRange) {
        this.legLowerRange = legLowerRange;
    }

    public int getLegUpperRange() {
        return legUpperRange;
    }

    public void setLegUpperRange(int legUpperRange) {
        this.legUpperRange = legUpperRange;
    }

    public int getHeightLowerRange() {
        return heightLowerRange;
    }

    public void setHeightLowerRange(int heightLowerRange) {
        this.heightLowerRange = heightLowerRange;
    }

    public int getHeightUpperRange() {
        return heightUpperRange;
    }

    public void setHeightUpperRange(int heightUpperRange) {
        this.heightUpperRange = heightUpperRange;
    }

    public int getTiltLowerRange() {
        return tiltLowerRange;
    }

    public void setTiltLowerRange(int tiltLowerRange) {
        this.tiltLowerRange = tiltLowerRange;
    }

    public int getTiltUpperRange() {
        return tiltUpperRange;
    }

    public void setTiltUpperRange(int tiltUpperRange) {
        this.tiltUpperRange = tiltUpperRange;
    }
}
