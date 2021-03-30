package com.paramount.bed.util.homesequence;

import com.paramount.bed.data.remote.response.WeeklyScoreAdviceResponse;
import com.paramount.bed.ui.main.HomeActivity;

import java.util.ArrayList;

public class HomeSequenceManager {
    private HomeActivity homeActivity;
    private ArrayList<HomeSequenceItem> sequences;
    private int currentIndex;
    private HomeSequenceItem currentSequence;

    //TODO : find a better way to send data between sequences
    public WeeklyScoreAdviceResponse weeklyScoreAdviceResponse;

    public HomeSequenceManager(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
        sequences = new ArrayList<>();
    }


    public void enque(HomeSequenceItem sequence){
        sequence.setHomeActivityRef(homeActivity);
        sequences.add(sequence);
    }

    public HomeSequenceItem getCurrentSequence(){
        return currentSequence;
    }

    public HomeSequenceItem getNextSequence(){
        if(currentIndex >= sequences.size()){
            return null;
        }else{
            return sequences.get(currentIndex);
        }
    }

    public boolean next(){
        HomeSequenceItem nextSequence = getNextSequence();
        if(nextSequence != null){
            currentIndex += 1;
            currentSequence = nextSequence;
            currentSequence.execute();
            return true;
        }else{
            homeActivity.hideProgress();
        }
        currentIndex += 1;

        return false;
    }

    public boolean ended(){
        return currentIndex > sequences.size();
    }
}
