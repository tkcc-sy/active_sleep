package com.paramount.bed.util.homesequence;

import com.paramount.bed.ui.main.TopNewsHomeDialog;

public class NewsSequence extends HomeSequenceItem {
    public NewsSequence(HomeSequenceManager sequenceManager, SequenceDelegate delegate) {
        super(sequenceManager, delegate);
    }

    @Override
    public void execute() {
        super.execute();
        new TopNewsHomeDialog(getHomeActivityRef(), new TopNewsHomeDialog.DialogListener() {
            @Override
            public void start() {
            }

            @Override
            public void finish() {
                end();
            }
        });
    }
}
