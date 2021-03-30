package com.paramount.bed.ui.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paramount.bed.R;
import com.paramount.bed.ble.pojo.NSBedSetting;

public class BedPresetFragment extends Fragment implements BedPresetStopFragmentChild.BedPresetStopEventListener, BedPresetStartFragmentChild.BedPresetStartEventListener {

    private BedPresetStopFragmentChild stopFragmentChild;
    private BedPresetStartFragmentChild startFragmentChild;
    public BedPresetEventListener listener;
    public int selectedPresetIndex = -1;
    public final static int[] UNSELECTED_BED_PRESET_DRAWABLE = {R.drawable.controller_17, R.drawable.controller_21,
            R.drawable.controller_20, R.drawable.controller_18,
            R.drawable.controller_16, R.drawable.controller_15,
            R.drawable.controller_19, R.drawable.bed_reset_button,
            R.drawable.bed_memory_button};
    public final static int[] SELECTED_BED_PRESET_DRAWABLE = {R.drawable.controller_26, R.drawable.controller_30,
            R.drawable.controller_29, R.drawable.controller_27,
            R.drawable.controller_25, R.drawable.controller_24,
            R.drawable.controller_28, R.drawable.bed_reset_button_selected,
            R.drawable.bed_memory_button_selected};
    public static final int[] DISABLED_BED_PRESET_DRAWABLE = {R.drawable.controller_17_disabled, R.drawable.controller_21_disabled,
            R.drawable.controller_20_disabled, R.drawable.controller_18_disabled,
            R.drawable.controller_16_disabled, R.drawable.controller_15_disabled,
            R.drawable.controller_19_disabled, R.drawable.bed_reset_button_disabled,
            R.drawable.bed_memory_button_disabled};

    public BedPresetFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        startFragmentChild = new BedPresetStartFragmentChild();
        startFragmentChild.listener = this;

        stopFragmentChild = new BedPresetStopFragmentChild();
        stopFragmentChild.listener = this;

        View view = inflater.inflate(R.layout.fragment_bed_preset, container, false);
        showStartUI();

        return view;
    }

    public void showStopUI() {
        setupFragment(stopFragmentChild);
        stopFragmentChild.setPresetIcon(selectedPresetIndex);
    }

    public void showStartUI() {
        if (startFragmentChild != null) setupFragment(startFragmentChild);
    }

    public void enableUI() {
        if (startFragmentChild != null) {
            startFragmentChild.enableUI();
        }
    }

    public void disableUI() {
        if (startFragmentChild != null) {
            startFragmentChild.disableUI();
        }
    }

    public void clearSelection() {
        selectedPresetIndex = -1;
        if (startFragmentChild != null) startFragmentChild.deactiveAllButton();
    }

    private void setupFragment(Fragment fragment) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container_bed_preset, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void setIsBedOperationRunning(boolean isRunning) {
        if (startFragmentChild != null) startFragmentChild.shouldUseFlipAnimation = isRunning;
    }

    public void setIsTouchHoldMode(boolean isTouchHoldMode) {
        if (startFragmentChild != null) startFragmentChild.setTouchHoldMode(isTouchHoldMode);
    }

    @Override
    public void onPresetSelected(int index) {
        selectedPresetIndex = index;
        if (listener != null) {
            listener.onPresetSelected(index);
        }
    }

    @Override
    public void onPresetTouchStart(int index) {
        selectedPresetIndex = index;
        if (listener != null) {
            listener.onPresetTouchStart(index);
        }
    }

    @Override
    public void onPresetTouchEnd(int index) {
        selectedPresetIndex = -1;
        if (listener != null) {
            listener.onPresetTouchEnd(index);
        }
    }

    @Override
    public void onStopTapped() {
        showStartUI();
        if (listener != null) {
            listener.onPresetStopTapped();
        }
    }

    public boolean isPresetSelected() {
        return selectedPresetIndex != -1;
    }

    public void applyLock(NSBedSetting bedSetting) {
        if (startFragmentChild != null) {
            if (bedSetting.isHeadLocked() || bedSetting.isLegLocked() || bedSetting.isCombiLocked()) {
                startFragmentChild.disableUI();
            } else {
                startFragmentChild.enableUI();
            }
        }
    }

    public interface BedPresetEventListener {
        void onPresetSelected(int index);

        void onPresetStopTapped();

        void onPresetTouchStart(int index);

        void onPresetTouchEnd(int index);
    }
}
