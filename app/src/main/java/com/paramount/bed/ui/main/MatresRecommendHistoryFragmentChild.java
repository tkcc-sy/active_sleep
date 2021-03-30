package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MHSModel;
import com.paramount.bed.data.model.MattressHardnessSettingModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.util.LogUserAction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import io.realm.RealmList;

public class MatresRecommendHistoryFragmentChild extends MattressRecommendChildBase {

    @BindView(R.id.history_container)
    LinearLayout historyBodyContainer;

    @BindView(R.id.no_history_container)
    LinearLayout noHistoryContainer;

    @BindViews({ R.id.history_segment_container_1, R.id.history_segment_container_2, R.id.history_segment_container_3 , R.id.history_segment_container_4, R.id.history_segment_container_5})
    List<LinearLayout> historyContainers;

    @BindViews({ R.id.history_segment_label_1, R.id.history_segment_label_2, R.id.history_segment_label_3 , R.id.history_segment_label_4, R.id.history_segment_label_5})
    List<TextView> historyLabels;

    @BindViews({ R.id.line_history_segment_label_1, R.id.line_history_segment_label_2, R.id.line_history_segment_label_3 , R.id.line_history_segment_label_4, R.id.line_history_segment_label_5})
    List<View> historyLineContainer;
    static final long DURATION = 500;

    MatressRecommendHistoryEventListener listener;

    @BindView(R.id.close_history)
    RelativeLayout btnCloseRecommendHistory;

    UserService userService;

    public MatresRecommendHistoryFragmentChild() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_child_matress_recommend_history, container, false);

        ButterKnife.bind(this,view);

        userService = ApiClient.getClient(getActivity()).create(UserService.class);
        LogUserAction.sendNewLog(userService, "CHALENGE_MATTRESS_HISTORY_SHOW", "", "", "UI000671");

        btnCloseRecommendHistory.setOnClickListener(v -> {
            listener.onMattresRecommendHistoryCloseTapped();
        });

        initUI(view);
        return view;
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return FlipAnimation.create(FlipAnimation.LEFT, enter, DURATION);
    }

    @Override
    public void applyMHSValues(){
        if(mattressSettingModel != null) {
            RealmList<MHSModel> historyMHS = mattressSettingModel.getHistoryMHS();
            boolean historyAvailable = historyMHS.size() > 0;
            noHistoryContainer.setVisibility(historyAvailable ? View.GONE:View.VISIBLE);
            historyBodyContainer.setVisibility(historyAvailable ? View.VISIBLE:View.GONE);

            for (int i = 0; i < 5; i++) {
                LinearLayout historyContainer = historyContainers.get(i);
                View lineHistoryLable = historyLineContainer.get(i);
                if(historyMHS.size() > i){
                    historyContainer.setVisibility(View.VISIBLE);
                    if(i!=4){
                        lineHistoryLable.setVisibility(View.VISIBLE);
                    }
                    MHSModel history = historyMHS.get(i);
                    if(history != null && history.getScore() > -1) {
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String historyDate = history.getDate();
                        if (historyDate != null && !historyDate.isEmpty()) {
                            TextView historyLabel = historyLabels.get(i);
                            try {
                                Date parsedDate = dateFormat.parse(history.getDate());
                                if(parsedDate != null) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(parsedDate);
                                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                                    int month = calendar.get(Calendar.MONTH) + 1;
                                    int year = calendar.get(Calendar.YEAR);
                                    FormPolicyModel formPolicyModel = FormPolicyModel.getPolicy();
                                    MattressHardnessSettingModel mattressHardnessSettingModel = formPolicyModel.getMattressHardnessSettingById(history.getDesiredHardness());
                                    String labelString = LanguageProvider.getLanguage("UI000671C003")
                                            .replace("%DAY%", String.valueOf(day))
                                            .replace("%MONTH%", String.valueOf(month))
                                            .replace("%YEAR%", String.valueOf(year))
                                            .replace("%SCORE%",String.valueOf(history.getScore()) )
                                            .replace("%HARDNESS%",mattressHardnessSettingModel.getValue());
                                    historyLabel.setText(labelString);
                                }else{
                                    historyLabel.setText("");
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                historyLabel.setText("");
                            }
                        }

                        //appy mhs
                        List<TextView> segments = new ArrayList<>();
                        for (int j = 0; j<historyContainer.getChildCount(); j++){
                            if (historyContainer.getChildAt(j) instanceof TextView){
                                TextView mhsTextView = (TextView) historyContainer.getChildAt(j);
                                segments.add(mhsTextView);
                            }
                        }

                        setMHSLabels(history.getMattressHardness(),segments);
                    }else{
                        historyContainer.setVisibility(View.GONE);
                        lineHistoryLable.setVisibility(View.INVISIBLE);
                    }
                }else{
                    historyContainer.setVisibility(View.GONE);
                    lineHistoryLable.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    public interface MatressRecommendHistoryEventListener{
        void onMattresRecommendHistoryCloseTapped();
    }
}