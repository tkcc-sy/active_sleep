package com.paramount.bed.util.homesequence;

import android.content.Intent;

import com.paramount.bed.data.model.WeeklyScoreReviewModel;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.alarms.WeeklyScoreReviewDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeeklyScoreReviewSequence extends HomeSequenceItem {
    public WeeklyScoreReviewSequence(HomeSequenceManager sequenceManager, SequenceDelegate delegate) {
        super(sequenceManager, delegate);
    }

    @Override
    public void execute() {
        super.execute();
        if(homeActivityRef != null){
            Date currDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            WeeklyScoreReviewModel wsr = WeeklyScoreReviewModel.getByDate(sdf.format(currDate));

            String currentScreen = homeActivityRef.getClass().getSimpleName();

            if (wsr != null && currentScreen.equals(HomeActivity.class.getSimpleName())) {
                if (wsr.getAdvice() != null) {
                    Intent intent = new Intent(homeActivityRef, WeeklyScoreReviewDialog.class);
                    homeActivityRef.startActivityForResult(intent,homeActivityRef.HOME_WEEKLY_SEQUENCE_REQ_CODE);
                }
                else {
                    LogUserAction.sendNewLog(homeActivityRef.userService,"WEEKLY_SCORE_SKIP","1","","UI000500");
                    end();
                }
            }
            else {
                LogUserAction.sendNewLog(homeActivityRef.userService,"WEEKLY_SCORE_SKIP","2","","UI000500");
                end();
            }
        } else {
            end();
        }
    }
}
