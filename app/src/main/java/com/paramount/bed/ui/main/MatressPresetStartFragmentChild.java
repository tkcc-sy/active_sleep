package com.paramount.bed.ui.main;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.paramount.bed.R;
import com.paramount.bed.ui.BaseV4Fragment;
import com.paramount.bed.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.ViewCollections;

public class MatressPresetStartFragmentChild extends BaseV4Fragment {
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.tvTag)
    TextView tvTag;
    @BindViews({R.id.btnPreset1, R.id.btnPreset2, R.id.btnPreset3, R.id.btnPreset4, R.id.btnPreset5, R.id.btnFukato})
    List<ToggleButton> toggleButtons;

    List<PresetButton> btnPresets = new ArrayList<>();

    public int selectedPresetIndex = -1;
    static final long DURATION = 500;
    MatressPresetStartEventListener listener;
    public Boolean shouldUseFlipAnimation = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_child_matress_presset_start, container, false);
        ButterKnife.bind(this, view);
        registerButton();
        applyLocalization(view);
        disableUIByLocPermission();
        return view;
    }

    private void disableUIByLocPermission() {
        if (!PermissionUtil.locationFeatureEnabled(getActivity())) disableUI();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (shouldUseFlipAnimation) {
            return FlipAnimation.create(FlipAnimation.RIGHT, enter, DURATION);
        } else {
            clearSelection();
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    public void setSelectedItem(int index) {
        if (selectedPresetIndex != index || (selectedPresetIndex == index && !btnPresets.get(index).btn.isChecked())) {
            btnPresets.get(index).btn.performClick();
        }
    }

    private void registerButton() {
        int index = 0;
        for (ToggleButton toggleButton : toggleButtons
        ) {
            PresetButton presetButton = new PresetButton(index++, toggleButton);
            presetButton.btn.setOnClickListener(v -> {
                if (presetButton.active) {
                    presetButton.setInactive();
                    selectedPresetIndex = -1;

                } else {
                    deactiveAllButton();
                    presetButton.toggleActive();
                    selectedPresetIndex = presetButton.id;
                }
                if (listener != null) {
                    listener.onPresetSelected(selectedPresetIndex);
                }
            });
            btnPresets.add(presetButton);
        }
    }

    public void clearSelection() {
        deactiveAllButton();
        selectedPresetIndex = -1;
    }

    public void deactiveAllButton() {
        for (PresetButton presetButton : btnPresets
        ) {
            presetButton.setInactive();
        }
    }

    public void enableAllButton() {
        for (PresetButton presetButton : btnPresets
        ) {
            presetButton.btn.setEnabled(true);
        }
    }

    public void disableAllButton() {
        for (PresetButton presetButton : btnPresets
        ) {
            presetButton.btn.setEnabled(false);
        }
    }

    class PresetButton {
        int id;
        boolean active;
        ToggleButton btn;

        PresetButton(int id, ToggleButton btn) {
            this.id = id;
            this.btn = btn;
        }

        void toggleActive() {
            active = !active;
            btn.setChecked(active);
        }

        void setInactive() {
            active = false;
            btn.setChecked(false);
        }
    }

    public void enableUI() {
        if (toggleButtons == null) return;
        ViewCollections.run(toggleButtons, (view, index) -> view.setEnabled(true));
        tvTitle.setEnabled(true);
        tvTag.setEnabled(true);
    }

    public void disableUI() {
        if (toggleButtons == null) return;
        ViewCollections.run(toggleButtons, (view, index) -> view.setEnabled(false));
        tvTitle.setEnabled(false);
        tvTag.setEnabled(false);
    }

    public interface MatressPresetStartEventListener {
        void onPresetSelected(int index);
    }
}
