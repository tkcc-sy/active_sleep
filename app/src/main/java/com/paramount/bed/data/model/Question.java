package com.paramount.bed.data.model;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {
    public static final int MULTIPLE_CHOICE = 2;
    public static final int ONE_CHOICE = 1;
    int type;
    int questionId;
    String question;
    ArrayList<QuestionAnswer> answers;

    private List<Integer> userAnswer;

    public List<Integer> getUserAnswer() {
        return userAnswer;
    }

    public void selectAnswer(int id) {
        if (userAnswer.size() == 0) {
            userAnswer.add(0, id);
        } else {
            userAnswer.set(0, id);
        }
    }


    public void toggleAnswer(int id) {
        if (type == ONE_CHOICE)
            selectAnswer(id);
        else {
            int pos = userAnswer.indexOf(id);
            if (pos != -1) {
                userAnswer.remove(pos);
            } else {
                userAnswer.add(id);
            }
        }
    }

    public boolean isAnswer(int id) {
        if(userAnswer.indexOf(id) != -1)
            return true;
        return false;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<QuestionAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<QuestionAnswer>  answers) {
        this.answers = answers;
    }

    public Question(int type, int questionId, String question, ArrayList<QuestionAnswer> answers) {
        this.type = type;
        this.questionId = questionId;
        this.question = question;
        this.answers = answers;

        this.userAnswer = new ArrayList<>();
    }

    public Question(boolean isMultipleChoice, String question, ArrayList<QuestionAnswer> answers) {
        this.type = isMultipleChoice ? MULTIPLE_CHOICE: ONE_CHOICE;
        this.question = question;
        this.answers = answers;

        this.userAnswer = new ArrayList<>();
    }
}
