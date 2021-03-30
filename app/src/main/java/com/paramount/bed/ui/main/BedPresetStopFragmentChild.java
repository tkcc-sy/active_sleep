package com.paramount.bed.ui.main;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.paramount.bed.R;
import com.paramount.bed.ui.BaseV4Fragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BedPresetStopFragmentChild extends BaseV4Fragment {

    private static final long DURATION = 500;

    public BedPresetStopEventListener listener;

    @BindView(R.id.btnStopBed)
    LinearLayout btnStopBed;

    @BindView(R.id.vwPositionNow)
    View vwPositionNow;

    private int stopButtonDrawable = -1;


    private static int[] STOP_BUTTON_DRAWABLE = {R.drawable.controller_17,R.drawable.controller_21,
            R.drawable.controller_20,R.drawable.controller_18,
            R.drawable.controller_16,R.drawable.controller_15,
            R.drawable.controller_19,R.drawable.ctrl2_selected,
            R.drawable.ctrl1_selected};

    public BedPresetStopFragmentChild() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull  LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_bed_preset_stop, container, false);

        ButterKnife.bind(this, view);

        btnStopBed.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStopTapped();
            }
        });

        applyLocalization(view);
        if(stopButtonDrawable >= 0){
            setPresetIcon(stopButtonDrawable);
        }
        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return FlipAnimation.create(FlipAnimation.RIGHT, enter, DURATION);
    }

    public void setPresetIcon(int presetIndex) {
        if (vwPositionNow != null) {
            if (presetIndex >= 0 && presetIndex < STOP_BUTTON_DRAWABLE.length) {
                vwPositionNow.setBackground(Objects.requireNonNull(getActivity()).getDrawable(STOP_BUTTON_DRAWABLE[presetIndex]));
            } else {
                vwPositionNow.setVisibility(View.INVISIBLE);

            }
        }
        stopButtonDrawable = presetIndex;
    }
    public interface BedPresetStopEventListener {
        void onStopTapped();
    }
}
