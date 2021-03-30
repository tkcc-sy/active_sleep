package com.paramount.bed.ui.registration.step;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.AnswerResult;
import com.paramount.bed.data.model.Question;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.registration.RegistrationStepActivity;

import java.util.UUID;

@SuppressLint("ValidFragment")
public class QuizFragment extends BLEFragment {
    RecyclerView answerView;
    Question question;
    int activeQuiz;
    public static Button btnNext;

    int answerId;
    String answerContent;

    @SuppressLint("ValidFragment")
    public QuizFragment(int activeQuiz, Question q) {
        this.question = q;
        this.activeQuiz = activeQuiz;
    }
    AnswerListAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_quiz, container, false);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        answerView = (RecyclerView) view.findViewById(R.id.answerView);
        AccountAddressFragment.btnNext.setEnabled(false);
        TextView tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);
        TextView tvQuestionNumber = (TextView) view.findViewById(R.id.tvQuestionNumber);
        tvQuestion.setText(question.getQuestion());

        int qnumber = activeQuiz + 1;
        tvQuestionNumber.setText("Q" + qnumber);

        if (AnswerResult.getDataAnswerbyId(question.getQuestionId()) != null) {
            btnNext.setEnabled(true);
        }else {
            btnNext.setEnabled(false);
        }

        btnNext.setOnClickListener(next());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        answerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        adapter = new AnswerListAdapter(getContext(), question, this);
        answerView.setAdapter(adapter);
        applyLocalization(view);
        setToogleNext();
        return view;
    }
    public void refreshAdapter(QuizFragment quizFragment) {
        adapter.setList(quizFragment);
        adapter.notifyDataSetChanged();
    }
    public void setToogleNext() {
        if (AnswerResult.getAllById(question.getQuestionId()).size() > 0) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    public void setAnswer(int Id, String answer) {
        answerId = Id;
        answerContent = answer;
    }

    private View.OnClickListener next() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
//                saveAnswer();
                if (activity.activeQuiz < (activity.questions.size() - 1)) {
                    activity.activeQuiz = activity.activeQuiz + 1;
                    activity.go(activity.FRAGMENT_QUIZ);
                    return;
                }
                activity.go(activity.FRAGMENT_PREVIEW);
            }
        };
    }

    public void saveAnswer() {
        AnswerResult.clear(question.getQuestionId());
        AnswerResult answerResult = new AnswerResult();
        answerResult.setIdResult(UUID.randomUUID().toString());
        answerResult.setQuestionId(question.getQuestionId());
        answerResult.setAnswerId(answerId);
        answerResult.setAnswer(answerContent);
        answerResult.insert();
    }
}