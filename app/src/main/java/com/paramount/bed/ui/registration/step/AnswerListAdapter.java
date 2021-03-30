package com.paramount.bed.ui.registration.step;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.paramount.bed.R;
import com.paramount.bed.data.model.AnswerResult;
import com.paramount.bed.data.model.Question;
import com.paramount.bed.data.model.QuestionAnswer;

import java.util.UUID;

public class AnswerListAdapter extends RecyclerView.Adapter<AnswerListAdapter.AnswerViewHolder> {
    private Question question;
    private Context context;
    private QuizFragment quizFragment;

    public AnswerListAdapter(Context context, Question question, QuizFragment quizFragment) {
        this.question = question;
        this.context = context;
        this.quizFragment = quizFragment;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_answer_list, parent, false);
        AnswerViewHolder vh = new AnswerViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        ToggleButton toggleButton = (ToggleButton) holder.view;
        holder.setIsRecyclable(false);
        QuestionAnswer answer = question.getAnswers().get(position);

        toggleButton.setTextOn(answer.getContent());
        toggleButton.setText(answer.getContent());
        toggleButton.setTextOff(answer.getContent());

        boolean selected = question.isAnswer(position);
        if (AnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
            selected = true;
            question.toggleAnswer(position);
            quizFragment.setToogleNext();
            quizFragment.setAnswer(answer.getAnswerId(), answer.getContent());
        } else {
            selected = false;
        }
        if (question.getType() == Question.ONE_CHOICE && AnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
            toggleButton.setChecked(selected);
            if (!selected) {
                toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button_disabled));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        if (question.getType() == Question.ONE_CHOICE && AnswerResult.getAllById(question.getQuestionId()).size() > 0) {
            if (!selected) {
                toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button_disabled));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            } else {
                toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
            }
        }

        if (question.getType() == Question.MULTIPLE_CHOICE && AnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
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
                    AnswerResult.clear(question.getQuestionId());
                }
                AnswerResult answerResult = new AnswerResult();
                answerResult.setIdResult(UUID.randomUUID().toString());
                answerResult.setQuestionId(question.getQuestionId());
                answerResult.setAnswerId(answer.getAnswerId());
                answerResult.setAnswer(answer.getContent());
                answerResult.insert();

                if (question.getType() == Question.ONE_CHOICE && AnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (question.getType() == Question.ONE_CHOICE && AnswerResult.getAllById(question.getQuestionId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (question.getType() == Question.MULTIPLE_CHOICE && AnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.secondary_button_selected));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
            } else {
                if (question.getType() == Question.ONE_CHOICE && AnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button_disabled));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (question.getType() == Question.ONE_CHOICE && AnswerResult.getAllById(question.getQuestionId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button_disabled));
                    toggleButton.setTextColor(Color.parseColor("#FFFFFF"));
                }
                if (question.getType() == Question.MULTIPLE_CHOICE && AnswerResult.getAnswer(question.getQuestionId(), answer.getAnswerId()).size() > 0) {
                    toggleButton.setBackground(context.getDrawable(R.drawable.questionnaire_button));
                    toggleButton.setTextColor(Color.parseColor("#00576A"));
                }
                if (question.getType() == Question.MULTIPLE_CHOICE) {
                    AnswerResult.clearByAnswerId(answer.getAnswerId());
                }
            }

            quizFragment.setToogleNext();
            quizFragment.setAnswer(answer.getAnswerId(), answer.getContent());
            quizFragment.refreshAdapter(quizFragment);
        });

    }

    @Override
    public int getItemCount() {
        return question.getAnswers().size();
    }

    public void setList(QuizFragment quizFragment) {
        this.question = quizFragment.question;
        this.quizFragment = quizFragment;
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public AnswerViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}
