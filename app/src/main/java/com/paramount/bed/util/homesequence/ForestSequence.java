package com.paramount.bed.util.homesequence;

import android.content.Intent;

import com.paramount.bed.data.model.ForestModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.WeeklyScoreReviewModel;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.main.ForestDialog;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.alarms.WeeklyScoreReviewDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ForestSequence extends HomeSequenceItem {
    public ForestSequence(HomeSequenceManager sequenceManager, SequenceDelegate delegate) {
        super(sequenceManager, delegate);
    }

    @Override
    public void execute() {
        super.execute();
        if(homeActivityRef != null){
            String currentScreen = homeActivityRef.getClass().getSimpleName();
            UserService questionnareService = ApiClient.getClient(homeActivityRef).create(UserService.class);

            if(ForestModel.getAll().size() > 0){
                if (currentScreen.equals(HomeActivity.class.getSimpleName())) {
                    Intent intent = new Intent(homeActivityRef, ForestDialog.class);
                    intent.putExtra("from_menu",false);
                    homeActivityRef.startActivityForResult(intent,homeActivityRef.HOME_FOREST_SEQUENCE_REQ_CODE);
                } else {
                    LogUserAction.sendNewLog(questionnareService, "FOREST_HOME_SKIP", "", "", "UI000507");
                    end();
                }
            } else {
                end();
            }

        } else {
            end();
        }
    }
}
