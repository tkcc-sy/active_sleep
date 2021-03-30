package com.paramount.bed.data.remote.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SleepQuestionnaireResponse {

    @JsonProperty("questionnaire_id")
    int questionnaireId;

    String title;
    String description;
    ArrayList<SleepQuestionResponse> questions;

    @JsonProperty("weekly_score")
    WeeklyScoreAdviceResponse weeklyScore;

    ForrestScoreAdviceResponse forrest;


    public int getQuestionnaireId() {
        return questionnaireId;
    }

    public void setQuestionnaireId(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<SleepQuestionResponse> getQuestions() {
        return questions;
    }

    public WeeklyScoreAdviceResponse getWeeklyScore() {
        return weeklyScore;
    }

    public void setWeeklyScore(WeeklyScoreAdviceResponse weeklyScore) {
        this.weeklyScore = weeklyScore;
    }

    public ForrestScoreAdviceResponse getForrest() {
        return forrest;
    }

    public void setForrest(ForrestScoreAdviceResponse forrest) {
        this.forrest = forrest;
    }
}
