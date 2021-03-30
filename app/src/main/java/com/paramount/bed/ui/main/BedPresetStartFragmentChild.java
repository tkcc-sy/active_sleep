package com.paramount.bed.ui.main;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.paramount.bed.R;
import com.paramount.bed.ui.BaseV4Fragment;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.ViewCollections;

public class BedPresetStartFragmentChild extends BaseV4Fragment {
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.tvTag)
    TextView tvTag;

    @BindView(R.id.txt0)
    TextView tvtxt0;
    @BindView(R.id.txt1)
    TextView tvtxt1;
    @BindView(R.id.txt2)
    TextView tvtxt2;
    @BindView(R.id.txt3)
    TextView tvtxt3;
    @BindView(R.id.txt4)
    TextView tvtxt4;
    @BindView(R.id.txt5)
    TextView tvtxt5;
    @BindView(R.id.txt6)
    TextView tvtxt6;
    @BindView(R.id.txt7)
    TextView tvtxt7;
    @BindView(R.id.txt8)
    TextView tvtxt8;

    @BindViews({R.id.btnPreset1, R.id.btnPreset2, R.id.btnPreset3, R.id.btnPreset4,
            R.id.btnPreset5, R.id.btnPreset6, R.id.btnPreset7, R.id.btnPreset8, R.id.btnPreset9})
    List<ImageButton> presetsImageButtons;
    List<PresetButton> btnPresets = new ArrayList<>();
    static final long DURATION = 500;
    public BedPresetStartEventListener listener;

    public Boolean shouldUseFlipAnimation = false;
    private Boolean isTouchHoldMode = false;
    private Handler[] delayHandlers = new Handler[9];
    private Runnable[] delayTimers = new Runnable[9];


    public BedPresetStartFragmentChild() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_bed_presset_start, container, false);

        ButterKnife.bind(this, view);

        registerButton();
        applyLocalization(view);
        applyEvents();
        disableUIByLocPermission();

        if(DisplayUtils.FONTS.bigFontStatus(getActivity())) {
            tvTag.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        }
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
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    public Boolean getTouchHoldMode() {
        return isTouchHoldMode;
    }

    public void setTouchHoldMode(Boolean touchHoldMode) {
        isTouchHoldMode = touchHoldMode;
        applyEvents();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void applyEvents() {
        for (PresetButton presetButton : btnPresets) {
            presetButton.btn.setOnClickListener(null);
            presetButton.btn.setOnTouchListener(null);
            if (!isTouchHoldMode) {
                presetButton.btn.setOnClickListener(v -> {
                    if (presetButton.isActive()) {
                        presetButton.setInactive();
                        if (listener != null) {
                            listener.onPresetSelected(-1); //unselected
                        }

                    } else {
                        deactiveAllButton();
                        presetButton.toggleActive();
                        if (listener != null) {
                            listener.onPresetSelected(presetButton.getId());
                        }
                    }

                });
            } else {
                presetButton.btn.setOnTouchListener((view, motionEvent) -> {
                    Handler delayHandler = delayHandlers[presetButton.getId()];
                    Runnable delayTimer = delayTimers[presetButton.getId()];
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL || isOutside(view, motionEvent)) {

                        if (delayHandler != null && delayTimer != null) {
                            delayHandler.removeCallbacks(delayTimer);

                            delayHandlers[presetButton.getId()] = null;
                            delayTimers[presetButton.getId()] = null;
                        }

                        if (listener != null && delayHandler == null && delayTimer == null) {
                            presetButton.setInactive();
                            listener.onPresetTouchEnd(presetButton.getId());
                        }

                    } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (delayHandler == null) {
                            delayHandler = new Handler();
                            delayTimer = () -> {
                                presetButton.setActive();
                                if (listener != null && delayHandlers[presetButton.getId()] != null) {
                                    listener.onPresetTouchStart(presetButton.getId());
                                    delayHandlers[presetButton.getId()] = null;
                                    delayTimers[presetButton.getId()] = null;
                                }
                            };
                            delayHandlers[presetButton.getId()] = delayHandler;
                            delayTimers[presetButton.getId()] = delayTimer;
                        }
                        delayHandler.postDelayed(delayTimer, 250);

                    }
                    return false;
                });
            }
        }
    }

    public void deactiveAllButton() {
        for (PresetButton presetButton : btnPresets
        ) {
            presetButton.setInactive();
        }
        if (!tvTitle.isEnabled()) {
            disableUI();
        } else {
            enableUI();
        }
    }

    private void registerButton() {
        for (int i = 0; i < 9; i++) {
            PresetButton presetButton = new PresetButton(i, presetsImageButtons.get(i), BedPresetFragment.UNSELECTED_BED_PRESET_DRAWABLE[i],
                    BedPresetFragment.SELECTED_BED_PRESET_DRAWABLE[i]);
            btnPresets.add(presetButton);
        }
    }

    private class PresetButton {
        int id;
        int bgActive;
        int bgInactive;
        boolean active;
        ImageButton btn;

        PresetButton(int id, ImageButton btn, int bgInactive, int bgActive) {
            this.bgActive = bgActive;
            this.bgInactive = bgInactive;
            this.btn = btn;
            this.id = id;
        }


        void toggleActive() {
            active = !active;
            int bg;

            if (active) bg = bgActive;
            else bg = bgInactive;
            if (BedPresetStartFragmentChild.this.getContext() != null)
                btn.setBackground(BedPresetStartFragmentChild.this.getContext().getDrawable(bg));
        }

        void setActive() {
            active = true;
            if (BedPresetStartFragmentChild.this.getContext() != null)
                btn.setBackground(BedPresetStartFragmentChild.this.getContext().getDrawable(bgActive));
        }

        void setInactive() {
            active = false;
            if (BedPresetStartFragmentChild.this.getContext() != null)
                btn.setBackground(BedPresetStartFragmentChild.this.getContext().getDrawable(bgInactive));
        }

        public boolean isActive() {
            return active;
        }

        public int getId() {
            return id;
        }
    }

    public void enableUI() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().runOnUiThread(() -> ViewCollections.run(presetsImageButtons, (view, index) -> {
                        view.setEnabled(true);
                        view.setBackground(getContext().getDrawable(BedPresetFragment.UNSELECTED_BED_PRESET_DRAWABLE[index]));
                        tvtxt0.setEnabled(true);
                        tvtxt1.setEnabled(true);
                        tvtxt2.setEnabled(true);
                        tvtxt3.setEnabled(true);
                        tvtxt4.setEnabled(true);
                        tvtxt5.setEnabled(true);
                        tvtxt6.setEnabled(true);
                        tvtxt7.setEnabled(true);
                        tvtxt8.setEnabled(true);
                        tvTitle.setEnabled(true);
                        tvTag.setEnabled(true);
                    })
            );
        }
    }

    public void disableUI() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            getActivity().runOnUiThread(() -> ViewCollections.run(presetsImageButtons, (view, index) -> {
                view.setEnabled(false);
                view.setBackground(getContext().getDrawable(BedPresetFragment.DISABLED_BED_PRESET_DRAWABLE[index]));
                tvtxt0.setEnabled(false);
                tvtxt1.setEnabled(false);
                tvtxt2.setEnabled(false);
                tvtxt3.setEnabled(false);
                tvtxt4.setEnabled(false);
                tvtxt5.setEnabled(false);
                tvtxt6.setEnabled(false);
                tvtxt7.setEnabled(false);
                tvtxt8.setEnabled(false);

                tvTitle.setEnabled(false);
                tvTag.setEnabled(false);
            }));

        }
    }

    public boolean isOutside(View v, MotionEvent e) {
        if (e.getX() < 0 || e.getY() < 0 || e.getX() > v.getMeasuredWidth() || e.getY() > v.getMeasuredHeight()) {
            return true;
        }
        return false;
    }

    public interface BedPresetStartEventListener {
        void onPresetSelected(int index);

        void onPresetTouchStart(int index);

        void onPresetTouchEnd(int index);
    }
}
