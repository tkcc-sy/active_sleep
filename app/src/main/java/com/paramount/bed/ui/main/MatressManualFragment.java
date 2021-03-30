package com.paramount.bed.ui.main;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.paramount.bed.R;
import com.paramount.bed.ui.BaseV4Fragment;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.PermissionUtil;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class MatressManualFragment extends BaseV4Fragment {
    @BindView(R.id.seekBar)
    IndicatorSeekBar seekBar;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTag)
    TextView tvTag;
    @BindView(R.id.cardTextLeft)
    TextView cardTextLeft;
    @BindView(R.id.cardTextRight)
    TextView cardTextRight;
    @BindViews({R.id.mattressPosButton1, R.id.mattressPosButton2, R.id.mattressPosButton3,
            R.id.mattressPosButton4, R.id.mattressPosButton5, R.id.mattressPosButton6})
    List<ToggleButton> mattressPosButtons;

    List<SegmentButton> segmentButtons;
    public int selectedSegmentIndex = -1;
    public int newSegmentValue = 1;
    public MattressFreeEventListener listener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_matress_manual, container, false);
        ButterKnife.bind(this, view);
        //init seekbar
        seekBar.setEnabled(false);
        seekBar.getIndicator().getContentView().findViewById(R.id.isb_progress).setVisibility(View.INVISIBLE);
        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                newSegmentValue = seekBar.getProgress();
            }
        });

        //init buttons
        int index = 0;
        segmentButtons = new ArrayList<>();
        for (ToggleButton mattressPosButton : mattressPosButtons) {
            SegmentButton segmentButton = new SegmentButton(index++, mattressPosButton);
            mattressPosButton.setOnClickListener(v -> {
                if (segmentButton.active) {
                    segmentButton.setInactive();
                    selectedSegmentIndex = -1;

                    seekBar.setEnabled(false);
                    seekBar.getIndicator().getContentView().findViewById(R.id.isb_progress).setVisibility(View.INVISIBLE);
                    seekBar.setProgress(1);
                } else {
                    deactiveAllButton();
                    segmentButton.toggleActive();
                    selectedSegmentIndex = segmentButton.id;

                    seekBar.setEnabled(true);
                    seekBar.getIndicator().getContentView().findViewById(R.id.isb_progress).setVisibility(View.VISIBLE);
                    if (listener != null) {
                        seekBar.setProgress(listener.getMattressValueFor(selectedSegmentIndex));
                    } else {
                        seekBar.setProgress(1);
                    }
                }
                if (listener != null) {
                    listener.onMattressSegmentSelected(selectedSegmentIndex);
                }
            });
            segmentButtons.add(segmentButton);
        }
        applyLocalization(view);
        disableUIByLocPermission();
        if(DisplayUtils.FONTS.bigFontStatus(getActivity())) {
            mattressPosButtons.get(3).setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            mattressPosButtons.get(4).setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            mattressPosButtons.get(5).setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        }
        return view;
    }

    private void disableUIByLocPermission() {
        if (!PermissionUtil.locationFeatureEnabled(getActivity())) disableUI();
    }

    public void deactiveAllButton() {
        if (segmentButtons != null) {
            for (SegmentButton segmentButton : segmentButtons) {
                segmentButton.setInactive();
            }
        }

        if (seekBar != null) seekBar.setProgress(1);
        if (seekBar != null) seekBar.setEnabled(false);
    }

    public void enableMatressPosBtton() {
        for (int i = 0; i < mattressPosButtons.size(); i++) {
            mattressPosButtons.get(i).setAlpha(1f);
            mattressPosButtons.get(i).setEnabled(true);
        }
        seekBar.setEnabled(true);
    }

    class SegmentButton {
        int id;
        boolean active;
        ToggleButton btn;

        SegmentButton(int id, ToggleButton btn) {
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

    public void enableAllButton() {
        if (segmentButtons != null) {
            for (SegmentButton segmentButton : segmentButtons
            ) {
                segmentButton.btn.setEnabled(true);
            }
        }
    }

    public void disableAllButton() {
        if (segmentButtons != null) {
            for (SegmentButton segmentButton : segmentButtons
            ) {
                segmentButton.btn.setEnabled(false);
            }
        }
    }

    public void enableUI() {
        enableAllButton();
        if (tvTitle != null) tvTitle.setEnabled(true);
        if (tvTag != null) tvTag.setEnabled(true);
        if (cardTextLeft != null) cardTextLeft.setEnabled(true);
        if (cardTextRight != null) cardTextRight.setEnabled(true);
    }

    public void disableUI() {
        disableAllButton();
        if (seekBar != null) seekBar.setProgress(1);
        if (seekBar != null) seekBar.setEnabled(false);
        if (tvTitle != null) tvTitle.setEnabled(false);
        if (tvTag != null) tvTag.setEnabled(false);
        if (cardTextLeft != null) cardTextLeft.setEnabled(false);
        if (cardTextRight != null) cardTextRight.setEnabled(false);
    }

    public void clearSelection() {
        deactiveAllButton();
        selectedSegmentIndex = -1;
        newSegmentValue = 1;
    }

    public interface MattressFreeEventListener {
        void onMattressSegmentSelected(int index);

        int getMattressValueFor(int index);
    }
}



