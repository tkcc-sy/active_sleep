package com.paramount.bed.ui.main;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BaseV4Fragment;
import com.paramount.bed.util.ActivityUtil;
import com.paramount.bed.util.DialogUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SnoreRecordFragment extends BaseV4Fragment {

    SnoreRecordListener snoreRecordListener;
    public static boolean START_RECORD = false;

    @BindView(R.id.btnStopSnore)
    LinearLayout btnStopSnore;

    @BindView(R.id.analyze_progress)
    LinearLayout progressDialog;

//    public Dialog progressDialog;

    public SnoreRecordFragment() {
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation flipAnimation = FlipAnimation.create(FlipAnimation.LEFT, enter, 500);

        flipAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                snoreRecordListener.onStartFlip();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                snoreRecordListener.onStopFlip();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return flipAnimation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_snore_record, container, false);

        snoreRecordListener = (SnoreRecordListener)getActivity();

        applyLocalization(view);
        ButterKnife.bind(this, view);
        view = ActivityUtil.handleBackButton(view);

        btnStopSnore.setOnClickListener(v -> {
            DialogUtil.createCustomYesNo(getActivity(), "", LanguageProvider.getLanguage("UI000560C014"), LanguageProvider.getLanguage("UI000560C016"), (dialogInterface, i) -> dialogInterface.dismiss(),
                    LanguageProvider.getLanguage("UI000560C015"), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        SnoreRecordFragment.START_RECORD = false;
                        snoreRecordListener.onStopRecord();
                    });
        });

        return view;
    }
    public void showSubLoading() {
        progressDialog.setVisibility(View.VISIBLE);
        btnStopSnore.setEnabled(false);
    }

    public void hideSubLoading() {
        progressDialog.setVisibility(View.INVISIBLE);
        btnStopSnore.setEnabled(true);
    }

    public interface SnoreRecordListener{
        void onCallback(boolean startRecord);
        void onStartFlip();
        void onStopFlip();
        void onStopRecord();
    }

}
