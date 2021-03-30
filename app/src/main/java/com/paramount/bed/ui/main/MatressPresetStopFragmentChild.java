package com.paramount.bed.ui.main;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.orhanobut.logger.Logger;
import com.paramount.bed.R;
import com.paramount.bed.ui.BaseV4Fragment;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatressPresetStopFragmentChild extends BaseV4Fragment {

    private static final long DURATION = 500;

    public MatressPresetStopEventListener listener;

    @BindView(R.id.btnStopMatress)
    Button btnStopMatress;

    @BindView(R.id.btnFukato)
    TextView btnFukato;

    private int stopButtonDrawable = -1;
    public Boolean shouldUseFlipAnimation = false;

    public MatressPresetStopFragmentChild() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_matress_preset_stop, container, false);

        ButterKnife.bind(this, view);

        btnStopMatress.setOnClickListener(v -> {
            Logger.d("fukatto counter refreshing onclicklistener");
            if (listener != null) {
                listener.onMattresPresetStopTapped();
            }
        });

        applyLocalization(view);
        if (stopButtonDrawable >= 0) {
            setPresetIcon(stopButtonDrawable);
        }
        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return FlipAnimation.create(FlipAnimation.LEFT, enter, DURATION);
    }

    public void setPresetIcon(int presetIndex) {
        if (btnFukato != null) {
//            btnFukato.setText(LanguageProvider.getLanguage("UI000630C009"));
        }
    }

    public void isStopButtonEnable(boolean isEnable) {
        FragmentActivity activity = getActivity();
        if (btnStopMatress != null && activity != null) {
            btnStopMatress.setEnabled(isEnable);
        }else{
            //defer until UI is available
            deferStopButtonEnable(isEnable);
        }
    }
    private void deferStopButtonEnable(boolean isEnable){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                FragmentActivity activity = getActivity();
                if(activity != null){
                    activity.runOnUiThread(() -> isStopButtonEnable(isEnable));
                }else{
                    deferStopButtonEnable(isEnable);
                }
            }
        },500);
    }

    public interface MatressPresetStopEventListener {
        void onMattresPresetStopTapped();
    }
}
