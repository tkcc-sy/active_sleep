package com.paramount.bed.util.alarms;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.paramount.bed.R;
import com.paramount.bed.data.model.Question;
import com.paramount.bed.data.model.QuestionAnswer;
import com.paramount.bed.data.model.SleepAnswerResult;

import java.util.UUID;

public class AlarmsSleepAnswerListAdapter extends RecyclerView.Adapter<AlarmsSleepAnswerListAdapter.AnswerViewHolder> {
    private Question question;
    private Context context;
    private AlarmsSleepQuestionnaire activity;

    AlarmsSleepAnswerListAdapter(Context context, Question question, AlarmsSleepQuestionnaire activity) {
        this.question = question;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_sleep_answer_list, parent, false);
        v.getLayoutParams().width = parent.getWidth();
        return new AnswerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        ToggleButton toggleButton =  holder.view.findViewById(R.id.toggleButton);
        ImageView answerIcon = holder.view.findViewById(R.id.answerIcon);

        holder.setIsRecyclable(false);
        QuestionAnswer answer = question.getAnswers().get(position);

        toggleButton.setTextOn(answer.getContent());
        toggleButton.setText(answer.getContent());
        toggleButton.setTextOff(answer.getContent());

        if(answer.getIconIndex() >= 1 && answer.getIconIndex() <= 5){
            answerIcon.setVisibility(View.VISIBLE);
        }else{
            answerIcon.setVisibility(View.GONE);
        }

        switch (answer.getIconIndex()){
            case 1 :
                answerIcon.setImageDrawable(context.getDrawable(R.drawable.sleep_answer_1));
                break;
            case 2 :
                answerIcon.setImageDrawable(context.getDrawable(R.drawable.sleep_answer_2));
                break;
            case 3 :
                answerIcon.setImageDrawable(context.getDrawable(R.drawable.sleep_answer_3));
                break;
            case 4 :
                answerIcon.setImageDrawable(context.getDrawable(R.drawable.sleep_answer_4));
                break;
            case 5 :
                answerIcon.setImageDrawable(context.getDrawable(R.drawable.sleep_answer_5));
                break;
        }

        boolean selected;
        if (SleepAnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
            selected = true;
            question.toggleAnswer(position);
            activity.setAnswer(answer.getAnswerId(), answer.getContent());
        } else {
            selected = false;
        }
        if (question.getType() == Question.ONE_CHOICE && SleepAnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
            toggleButton.setChecked(selected);
            if (!selected) {
                toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button_disabled));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        if (question.getType() == Question.ONE_CHOICE && SleepAnswerResult.getAllById(question.getQuestionId()).size() > 0) {
            if (!selected) {
                toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button_disabled));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        if (question.getType() == Question.MULTIPLE_CHOICE && SleepAnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
            toggleButton.setChecked(selected);
            if (!selected) {
                toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_disabled));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        toggleButton.setOnClickListener((view) -> {
            question.toggleAnswer(position);
            if (toggleButton.isChecked()) {
                if (question.getType() == Question.ONE_CHOICE) {
                    SleepAnswerResult.clear(question.getQuestionId());
                }
                SleepAnswerResult answerResult = new SleepAnswerResult();
                answerResult.setIdResult(UUID.randomUUID().toString());
                answerResult.setQuestionId(question.getQuestionId());
                answerResult.setAnswerId(answer.getAnswerId());
                answerResult.setAnswer(answer.getContent());
                answerResult.setIconIndex(answer.getIconIndex());
                answerResult.insert();
                if (question.getType() == Question.ONE_CHOICE && SleepAnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (question.getType() == Question.ONE_CHOICE && SleepAnswerResult.getAllById(question.getQuestionId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (question.getType() == Question.MULTIPLE_CHOICE && SleepAnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
            } else {
                if (question.getType() == Question.ONE_CHOICE && SleepAnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button_disabled));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (question.getType() == Question.ONE_CHOICE && SleepAnswerResult.getAllById(question.getQuestionId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button_disabled));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (question.getType() == Question.MULTIPLE_CHOICE && SleepAnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button));
                    toggleButton.setTextColor(Color.parseColor("#00576A"));
                }
                if (question.getType() == Question.MULTIPLE_CHOICE) {
                    SleepAnswerResult.clearByAnswerId(answer.getAnswerId());
                }
            }
            activity.setAnswer(answer.getAnswerId(), answer.getContent());
            activity.refreshAdapter(activity,question);
        });
    }

    @Override
    public int getItemCount() {
        return question.getAnswers().size();
    }

    void setList(Question question, AlarmsSleepQuestionnaire activity) {
        this.question = question;
        this.activity = activity;
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        public View view;

        AnswerViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}
