package com.paramount.bed.util.homesequence;

import android.content.Intent;

import com.paramount.bed.data.model.TutorialShowModel;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.main.TutorialActivity;

public class TutorialSequence extends HomeSequenceItem {
    public TutorialSequence(HomeSequenceManager sequenceManager, SequenceDelegate delegate) {
        super(sequenceManager, delegate);
    }

    @Override
    public void execute() {
        super.execute();
        Boolean isHomeTutorialShown = TutorialShowModel.get().getBedShowed();
        if(isHomeTutorialShown != null && isHomeTutorialShown){
            Intent i = new Intent(homeActivityRef, TutorialActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.putExtra("type", 0);
            i.putExtra("isOtherShowed", true);

            //end() function will be called from HomeActivity in onActivityResult
            homeActivityRef.startActivityForResult(i, HomeActivity.HOME_TUTORIAL_SEQUENCE_REQ_CODE);
        }else{
            end();
        }
    }
}
