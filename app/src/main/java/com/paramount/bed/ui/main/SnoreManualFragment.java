package com.paramount.bed.ui.main;


import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.paramount.bed.R;
import com.paramount.bed.ui.BaseV4Fragment;
import com.paramount.bed.util.ActivityUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SnoreManualFragment extends BaseV4Fragment {

    public Boolean shouldUseFlipAnimation = false;

    public SnoreManualFragment() {
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (shouldUseFlipAnimation) {
            return FlipAnimation.create(FlipAnimation.RIGHT, enter, 500);
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_snore_manual, container, false);
        applyLocalization(view);
        view = ActivityUtil.handleBackButton(view);
        return view;
    }

}
