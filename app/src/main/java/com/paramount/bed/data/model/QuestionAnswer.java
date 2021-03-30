package com.paramount.bed.data.model;


public class QuestionAnswer {
    int answerId;
    String content;
    int iconIndex;

    public String getContent() {
        return content;
    }

    public int getAnswerId() {
        return answerId;
    }

    public int getIconIndex() {
        return iconIndex;
    }

    public QuestionAnswer(int answerId, String content, int iconIndex) {
        this.answerId = answerId;
        this.content = content;
        this.iconIndex = iconIndex;
    }
    public QuestionAnswer(int answerId, String content) {
        this.answerId = answerId;
        this.content = content;
        this.iconIndex = 0;
    }
}
