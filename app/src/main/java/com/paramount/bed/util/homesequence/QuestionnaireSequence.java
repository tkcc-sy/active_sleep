package com.paramount.bed.util.homesequence;

import android.content.Intent;

import com.paramount.bed.data.model.QSSleepDailyModel;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.util.alarms.AlarmsQuizModule;
import com.paramount.bed.util.alarms.AlarmsSleepQuestionnaire;

import java.util.Calendar;

public class QuestionnaireSequence extends HomeSequenceItem {
    public QuestionnaireSequence(HomeSequenceManager sequenceManager, SequenceDelegate delegate) {
        super(sequenceManager, delegate);
    }

    @Override
    public void execute() {
        super.execute();
        if(homeActivityRef != null){
            homeActivityRef.showProgress();
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            AlarmsQuizModule.shouldShowSleepQuestionnaire(homeActivityRef, shouldShow -> {
                if(shouldShow ) {
                    homeActivityRef.hideProgress();
                    QSSleepDailyModel.adsShowed(day);
                    Intent pendingQuiz = new Intent(homeActivityRef, AlarmsSleepQuestionnaire.class);
                    pendingQuiz.putExtra(AlarmsSleepQuestionnaire.CURRENT_SCREEN, homeActivityRef.getClass().getSimpleName());
                    pendingQuiz.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    homeActivityRef.startActivityForResult(pendingQuiz, HomeActivity.HOME_QUESTIONNAIRE_SEQUENCE_REQ_CODE);
                }else{
                    end();
                }
            },0);
        } else {
            end();
        }
    }
}
