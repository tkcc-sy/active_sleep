package com.paramount.bed.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.orhanobut.logger.Logger;
import com.paramount.bed.R;
import com.paramount.bed.data.model.MHSModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.button.MHSButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmList;

public class MatresRecommendFragmentChild extends MattressRecommendChildBase {

    MatressRecommendEventListener listener;
    @BindViews({ R.id.mhs_button_1, R.id.mhs_button_2, R.id.mhs_button_3, R.id.mhs_button_4 })
    List<MHSButton> mhsButtons;
    @BindViews({ R.id.highest_mhs_1, R.id.highest_mhs_2, R.id.highest_mhs_3 , R.id.highest_mhs_4, R.id.highest_mhs_5, R.id.highest_mhs_6 })
    List<TextView> highestMHSSegments;
    @BindViews({ R.id.top_1_mhs_1, R.id.top_1_mhs_2, R.id.top_1_mhs_3 , R.id.top_1_mhs_4, R.id.top_1_mhs_5, R.id.top_1_mhs_6 })
    List<TextView> top1MHSSegments;
    @BindViews({ R.id.top_2_mhs_1, R.id.top_2_mhs_2, R.id.top_2_mhs_3 , R.id.top_2_mhs_4, R.id.top_2_mhs_5, R.id.top_2_mhs_6 })
    List<TextView> top2MHSSegments;
    @BindViews({ R.id.top_3_mhs_1, R.id.top_3_mhs_2, R.id.top_3_mhs_3 , R.id.top_3_mhs_4, R.id.top_3_mhs_5, R.id.top_3_mhs_6 })
    List<TextView> top3MHSSegments;

    @BindViews({ R.id.tvTitle, R.id.textView2, R.id.tvTag})
    List<TextView> titles;

    @BindView(R.id.recommend_history)
    LinearLayout btnRecommendHistory;

    @BindView(R.id.setting_hardness)
    LinearLayout btnSettingHardness;

    @BindView(R.id.textView5)
    TextView bioLabel;

    @BindView(R.id.tvTag)
    TextView highestMHSLabel;

    @OnClick({ R.id.mhs_button_1, R.id.mhs_button_2, R.id.mhs_button_3, R.id.mhs_button_4 })
    public void actionMHSButton(View view){
        if(!view.isEnabled()) return;
        for (MHSButton mhsButton:mhsButtons
             ) {
            if(mhsButton.getId() == view.getId()){
                mhsButton.setChecked(true);
            }else{
                mhsButton.setChecked(false);
            }
        }

        if(listener != null){
            listener.onMattressSelectionChanged(((MHSButton)view).associatedMHS);
        }
    }

    public Boolean shouldUseFlipAnimation = false;
    static final long DURATION = 500;

    public MatresRecommendFragmentChild() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_matress_recommend, container, false);

        ButterKnife.bind(this,view);

        btnRecommendHistory.setOnClickListener(v->{
            if(listener != null){
                uncheckAllButtons();
                listener.onMattressSelectionChanged(null);
                listener.onMattresRecommendHistoryTapped();
            }
        });

        btnSettingHardness.setOnClickListener(v -> {
            ((RemoteActivity) Objects.requireNonNull(getActivity())).initDialogSettingHardness();
        });

        initUI(view);

        //set bio label
        UserLogin userLogin = UserLogin.getUserLogin();

        String bioLabelText = LanguageProvider.getLanguage("UI000670C004")
                                .replace("%AGE%", String.valueOf(userLogin.getRoundedAge()))
                                .replace("%GENDER%", userLogin.getGenderString());
        bioLabel.setText(bioLabelText);
        return view;
    }


    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if(shouldUseFlipAnimation){
            return FlipAnimation.create(FlipAnimation.RIGHT, enter, DURATION);
        }else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    @Override
    public void applyMHSValues(){
        if(mattressSettingModel != null){
            MHSModel highestMHS = mattressSettingModel.getHighestMHS();
            RealmList<MHSModel> topMHS = mattressSettingModel.getTopMHS();
            //Highest MHS
            if(highestMHS != null){
                RealmList<Integer> highestMHSValues = highestMHS.getMattressHardness();
                if(highestMHSValues != null) {
                    setMHSLabels(highestMHSValues,highestMHSSegments);
                }
                mhsButtons.get(0).associatedMHS = highestMHS;
                highestMHSLabel.setText(highestMHS.getIsDefault() == 1 ? LanguageProvider.getLanguage("UI000670C008") : LanguageProvider.getLanguage("UI000670C003"));
            }else{
                highestMHSLabel.setText(LanguageProvider.getLanguage("UI000670C008"));
            }

            //Top MHS
            List<List<TextView>> topSegments = new ArrayList<>();
            topSegments.add(top1MHSSegments);
            topSegments.add(top2MHSSegments);
            topSegments.add(top3MHSSegments);

            if(topMHS != null){
                for (int i = 0; i < 3 ; i++) {
                    MHSButton mhsButton = mhsButtons.get(i+1);
                    if (topMHS.size() > i){;
                        mhsButton.setEnabled(true);
                        mhsButton.associatedMHS = topMHS.get(i);
                        setMHSLabels(mhsButton.associatedMHS.getMattressHardness(),topSegments.get(i));
                    }else{
                        mhsButton.associatedMHS = null;
                        mhsButton.setEnabled(false);
                        disableMHSLabels(topSegments.get(i));
                    }
                }
            }else{
                //disable all
                disableMHSLabels(top1MHSSegments);
                disableMHSLabels(top2MHSSegments);
                disableMHSLabels(top3MHSSegments);
            }
        }
    }

    public void uncheckAllButtons(){
        for (MHSButton mhsButton:mhsButtons
        ) {
            mhsButton.setChecked(false);
        }
    }

    public void enableAllButton() {
        for (MHSButton mhsButton:mhsButtons
        ) {
            mhsButton.setEnabled(mhsButton.associatedMHS != null);
        }
    }

    public void disableAllButton() {
        for (MHSButton mhsButton:mhsButtons
        ) {
            mhsButton.setEnabled(false);
        }
    }

    public interface MatressRecommendEventListener {
        void onMattresRecommendHistoryTapped();
        void onMattressSelectionChanged(MHSModel newSelection);
    }


    public void enableUI() {
        uncheckAllButtons();
        enableAllButton();
        for (TextView textView:titles
             ) {
            textView.setEnabled(true);
        }
        btnSettingHardness.setEnabled(true);
        btnRecommendHistory.setEnabled(true);
    }

    public void disableUI() {
        uncheckAllButtons();
        disableAllButton();
        for (TextView textView:titles
        ) {
            textView.setEnabled(false);
        }
        btnSettingHardness.setEnabled(false);
        btnRecommendHistory.setEnabled(false);
    }

}