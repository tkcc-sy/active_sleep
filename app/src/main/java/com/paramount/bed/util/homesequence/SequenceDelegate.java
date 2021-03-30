package com.paramount.bed.util.homesequence;

import com.paramount.bed.data.remote.response.WeeklyScoreAdviceResponse;

public interface SequenceDelegate {
    void onExecute();
    void onEnd();
}
