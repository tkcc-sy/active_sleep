package com.paramount.bed.ui.main;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;
import com.paramount.bed.R;
import com.paramount.bed.ui.BaseV4Fragment;

public class MatressPresetFragment extends BaseV4Fragment implements MatressPresetStopFragmentChild.MatressPresetStopEventListener, MatressPresetStartFragmentChild.MatressPresetStartEventListener {
    private MatressPresetStopFragmentChild stopFragmentChild;
    private MatressPresetStartFragmentChild startFragmentChild;
    MattressPresetEventListener listener;
    public int selectedPresetIndex = -1;
    private boolean isStopUIShowing = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        startFragmentChild = new MatressPresetStartFragmentChild();
        startFragmentChild.listener = this;

        stopFragmentChild = new MatressPresetStopFragmentChild();
        stopFragmentChild.listener = this;

        View view = inflater.inflate(R.layout.fragment_matress_preset, container, false);
        if (startFragmentChild != null) setupFragment(startFragmentChild);

        return view;
    }

    public void showStopUI() {
        if (!isStopUIShowing) {
            isStopUIShowing = true;
            setupFragment(stopFragmentChild);
            stopFragmentChild.setPresetIcon(selectedPresetIndex);
        }
    }

    public void showStartUI() {
        if (isStopUIShowing) {
            isStopUIShowing = false;
            if (startFragmentChild != null) setupFragment(startFragmentChild);
        }
    }

    public void enableAllButton() {
        if (startFragmentChild != null) {
            startFragmentChild.enableAllButton();
        }
    }

    public void disableAllButton() {
        if (startFragmentChild != null) {
            startFragmentChild.disableAllButton();
        }
    }

    public void setSelectedItem(int index) {
        if (startFragmentChild != null) {
            startFragmentChild.setSelectedItem(index);
        }
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
        ft.replace(R.id.container_mattress_preset, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void setIsFukattoOperationRunning(boolean isRunning) {
        if (startFragmentChild != null) startFragmentChild.shouldUseFlipAnimation = isRunning;
        if (stopFragmentChild != null) stopFragmentChild.shouldUseFlipAnimation = isRunning;
    }

    @Override
    public void onPresetSelected(int index) {
        selectedPresetIndex = index;
        if (listener != null) {
            listener.onMattressPresetSelected(index);
        }
    }

    @Override
    public void onMattresPresetStopTapped() {
        stopFragmentChild.isStopButtonEnable(false);
        if (listener != null) {
            listener.onMattresFukattoStopTapped();
            clearSelection();
        }
    }

    final int fukattoTimeout = 6;
    boolean isInitializing = true;
    boolean currentVerifiedState = false;
    int fukattoVerifyCounter;
    int VERIFY_ROUND = 2;

    private Handler fukattoTimeoutHandler = new Handler();
    private Runnable fukattoTimeoutTimer = () -> {
        Logger.d("fukatto counter timeout triggered ");
        refreshFukattoUI();
    };

    public void requestFukattoChanged(boolean requestValue) {
        if (requestValue) {
            listener.onMattressFukattoStartUI();
            clearSelection();
            showStopUI();
            stopFragmentChild.isStopButtonEnable(false);
            fukattoTimeoutHandler.removeCallbacks(fukattoTimeoutTimer);
            fukattoTimeoutHandler.postDelayed(fukattoTimeoutTimer, fukattoTimeout * 1000);

        } else {
            listener.onMattresFukattoStopTapped();
            fukattoTimeoutHandler.removeCallbacks(fukattoTimeoutTimer);
            fukattoTimeoutHandler.postDelayed(fukattoTimeoutTimer, fukattoTimeout * 1000);
        }
    }

    public void applyFukattoCounter(boolean fukattoStatus) {
        if (isInitializing) { //follow whatever value ASA given us for the first time
            currentVerifiedState = fukattoStatus;
            isInitializing = false;
            refreshFukattoUI();
        } else {
            if (fukattoStatus != currentVerifiedState) {
                fukattoVerifyCounter += 1;
                if (fukattoVerifyCounter >= VERIFY_ROUND) {
                    //clear timeout
                    fukattoTimeoutHandler.removeCallbacks(fukattoTimeoutTimer);
                    fukattoVerifyCounter = 0;
                    currentVerifiedState = fukattoStatus;
                    refreshFukattoUI();
                }
            } else {
                fukattoVerifyCounter = 0;
            }
        }

    }

    private void refreshFukattoUI() {
        if (currentVerifiedState) {
            disableAllButton();
            showStopUI();
            if (stopFragmentChild != null) stopFragmentChild.isStopButtonEnable(true);
            if (listener != null) listener.onMattressFukattoStartUI();
        } else {
            enableAllButton();
            showStartUI();
            if (stopFragmentChild != null) stopFragmentChild.isStopButtonEnable(false);
            if (listener != null) listener.onMattressFukattoStopUI();
        }
    }

    public boolean isStopUIShowing() {
        return isStopUIShowing;
    }

    public boolean isFukattoActive() {
        return currentVerifiedState;
    }

    public void abortFukatto() {
        currentVerifiedState = false;
        refreshFukattoUI();
        fukattoTimeoutHandler.removeCallbacks(fukattoTimeoutTimer);
    }

    public interface MattressPresetEventListener {
        void onMattressPresetSelected(int index);

        void onMattresFukattoStopTapped();

        void onMattressFukattoStartUI();

        void onMattressFukattoStopUI();
    }
}
