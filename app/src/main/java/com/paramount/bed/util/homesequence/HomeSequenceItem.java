package com.paramount.bed.util.homesequence;

import com.orhanobut.logger.Logger;
import com.paramount.bed.ui.main.HomeActivity;

public abstract class HomeSequenceItem {
    protected SequenceDelegate delegate;
    protected HomeActivity homeActivityRef;
    protected HomeSequenceManager sequenceManager;
    public boolean isRunning  = false;

    public HomeSequenceItem(HomeSequenceManager sequenceManager,SequenceDelegate delegate) {
        this.sequenceManager = sequenceManager;
        this.delegate = delegate;
    }

    public HomeActivity getHomeActivityRef() {
        return homeActivityRef;
    }

    public void setHomeActivityRef(HomeActivity homeActivityRef) {
        this.homeActivityRef = homeActivityRef;
    }

    public void execute(){
        Logger.d("HOME SEQUENCE EXECUTE : " + this.getClass().getSimpleName() );
        isRunning = true;
        if(delegate != null){
            delegate.onExecute();
        }
        //stub
    }

    public void end(){
        if(isRunning){
            Logger.d("HOME SEQUENCE END : " + this.getClass().getSimpleName() );
            isRunning = false;
            if(sequenceManager != null){
                sequenceManager.next();
            }
            if(delegate != null){
                delegate.onEnd();
            }
        }
    }
}