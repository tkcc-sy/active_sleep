package com.paramount.bed.ble.pojo;

import com.paramount.bed.data.model.SettingModel;

public class NSBedSetting {
    boolean isCombiLocked;
    boolean isHeadLocked;
    boolean isLegLocked;
    boolean isHeightLocked;
    boolean isFastMode;

    public NSBedSetting() {
    }

    public NSBedSetting(SettingModel settingModel) {
        this.isCombiLocked = settingModel.getBed_combi_locked() == 1;
        this.isHeadLocked = settingModel.getBed_head_locked() == 1;
        this.isLegLocked = settingModel.getBed_leg_locked() == 1;
        this.isHeightLocked = settingModel.getBed_height_locked() == 1;
        this.isFastMode = settingModel.getBed_fast_mode() == 1;
    }

    public NSBedSetting(boolean isCombiLocked, boolean isHeightLocked,boolean isLegLocked, boolean isHeadLocked, boolean isFastMode) {
        this.isCombiLocked = isCombiLocked;
        this.isHeadLocked = isHeadLocked;
        this.isLegLocked = isLegLocked;
        this.isHeightLocked = isHeightLocked;
        this.isFastMode = isFastMode;
    }

    public boolean isFastMode() {
        return isFastMode;
    }

    public void setFastMode(boolean fastMode) {
        isFastMode = fastMode;
    }

    public boolean isCombiLocked() {
        return isCombiLocked;
    }

    public void setCombiLocked(boolean combiLocked) {
        isCombiLocked = combiLocked;
    }

    public boolean isHeadLocked() {
        return isHeadLocked;
    }

    public void setHeadLocked(boolean headLocked) {
        isHeadLocked = headLocked;
    }

    public boolean isLegLocked() {
        return isLegLocked;
    }

    public void setLegLocked(boolean legLocked) {
        isLegLocked = legLocked;
    }

    public boolean isHeightLocked() {
        return isHeightLocked;
    }

    public void setHeightLocked(boolean heightLocked) {
        isHeightLocked = heightLocked;
    }

    public boolean isLockSettingDiffrent(NSBedSetting comparator){
        return this.isCombiLocked != comparator.isCombiLocked || this.isHeightLocked != comparator.isHeightLocked ||
                this.isLegLocked != comparator.isLegLocked || this.isHeadLocked != comparator.isHeadLocked;
    }
}
