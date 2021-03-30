package com.paramount.bed.util.homesequence;
import android.content.Intent;

import com.paramount.bed.data.model.SleepResetModel;
import com.paramount.bed.data.provider.SleepResetProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.TimeSleepResetStatusResponse;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.main.SleepResetActivity;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.TimerUtils;
import com.paramount.bed.util.alarms.AlarmsPopup;

import java.util.Date;

public class SleepResetSequence extends HomeSequenceItem {
    public SleepResetSequence(HomeSequenceManager sequenceManager, SequenceDelegate delegate) {
        super(sequenceManager, delegate);
    }

    @Override
    public void execute() {
        super.execute();
        if(AlarmsPopup.isPresenting){
            end();
            return;
        }
        if(homeActivityRef != null){
            homeActivityRef.showProgress();
            SleepResetProvider.fetchSleepReset(homeActivityRef, new SleepResetProvider.StartSleepResetListener() {
                @Override
                public void onFinish(BaseResponse<TimeSleepResetStatusResponse> result, boolean isSuccess, String errTag) {
                    if(!isSuccess){
                        if(result != null && result.getMessage().equalsIgnoreCase("USR47-003")){
                            stopSleepReset();
                            return;
                        }
                    }else if(result != null && result.getData() != null && result.getData().getSleepResetDatetime() != null  && !result.getData().getSleepResetDatetime().isEmpty()){
                        SleepResetProvider.updateSleepResetModel(result.getData());
                    }else{
                        stopSleepReset();
                        return;
                    }


                    SleepResetModel sleepResetModel = SleepResetProvider.getSleepReset();
                    if(sleepResetModel != null){
                        Date startDate = sleepResetModel.getStartDate();
                        Date endDate = sleepResetModel.getEndDate();
                        if(startDate != null && endDate != null){

                            long secondDiff = (endDate.getTime() - startDate.getTime()) ;
                            if(secondDiff > 0){
                                homeActivityRef.hideProgress();
                                showSleepReset();
                            }else{
                                stopSleepReset();
                            }
                        }else{
                            stopSleepReset();
                        }
                    }else{
                        end();
                    }
                }
            },0);
        }
    }
    public void showSleepReset(){
        if(!HomeActivity.isSleepResetActivityActive(homeActivityRef)) {
            Intent intent = new Intent(homeActivityRef, SleepResetActivity.class);
            intent.putExtra("FROM_HOME", true);
            homeActivityRef.startActivityForResult(intent, homeActivityRef.HOME_SLEEP_RESET_SEQUENCE_REQ_CODE);
        }
    }
    public void stopSleepReset(){
        SleepResetProvider.deleteSleepReset();
        LogUserAction.sendNewLog(homeActivityRef.userService,"TERMINATE_STOP_SLEEP","","","UI000500");
        end();
    }
}

