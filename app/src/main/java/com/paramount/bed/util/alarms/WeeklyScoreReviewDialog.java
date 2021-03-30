package com.paramount.bed.util.alarms;

import android.os.Bundle;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.WeeklyScoreReviewModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BaseCompatibilityScreenActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.util.LogUserAction;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeeklyScoreReviewDialog extends BaseCompatibilityScreenActivity {
    @BindView(R.id.cvWeeklyReviewContainer) CardView cvWeeklyReviewContainer;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvAdviceTitle) TextView tvAdviceTitle;
    @BindView(R.id.tvAdviceContent) TextView tvAdviceContent;
    @BindView(R.id.btnToWeeklyScore) Button btnToWeeklyScore;
    @BindView(R.id.ivClose) ImageView ivClose;

    public static final String SET_DAILY = "set_daily";
    public static final String SET_WEEKLY = "set_weekly";

    UserService questionnareService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_weekly_score_review);
        ButterKnife.bind(this);

        questionnareService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        LogUserAction.sendNewLog(questionnareService, "WEEKLY_SCORE_SHOW", "1", "", "UI000502");

        tvTitle.setText(LanguageProvider.getLanguage("UI000502C025"));
        tvAdviceTitle.setText(LanguageProvider.getLanguage("UI000502C026"));
        btnToWeeklyScore.setText(LanguageProvider.getLanguage("UI000502C033"));

        getWeeklyScoreReview();

        ivClose.setOnClickListener(view -> {
            cvWeeklyReviewContainer.setVisibility(View.GONE);
            WeeklyScoreReviewModel.clear();
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        });

        btnToWeeklyScore.setOnClickListener(view -> {
            WeeklyScoreReviewModel.clear();
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
            EventBus.getDefault().post(new HomeActivity.WeeklyScoreEvent(SET_WEEKLY));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getWeeklyScoreReview() {
        String advice = "";
        Date currDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        WeeklyScoreReviewModel wsr = WeeklyScoreReviewModel.getByDate(sdf.format(currDate));

        if (wsr != null) {
            advice = wsr.getAdvice();
            tvAdviceContent.setText(advice);
        }
        else {
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        }
    }
}
