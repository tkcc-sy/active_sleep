package com.paramount.bed.util.alarms;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.ForestModel;
import com.paramount.bed.data.model.PendingAlarmModel;
import com.paramount.bed.data.model.QSSleepDailyModel;
import com.paramount.bed.data.model.Question;
import com.paramount.bed.data.model.QuestionAnswer;
import com.paramount.bed.data.model.SleepAnswerResult;
import com.paramount.bed.data.model.SleepQuestionnaireAnswerModel;
import com.paramount.bed.data.model.SleepQuestionnaireModel;
import com.paramount.bed.data.model.SleepQuestionnaireQuestionModel;
import com.paramount.bed.data.model.SleepQuestionnaireQuestionResult;
import com.paramount.bed.data.model.SleepQuestionnaireResult;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.WeeklyScoreReviewModel;
import com.paramount.bed.data.provider.ForestProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.SleepQuestionnaireProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.ForrestScoreAdviceResponse;
import com.paramount.bed.data.remote.response.SleepQuestionnaireResponse;
import com.paramount.bed.data.remote.response.WeeklyScoreAdviceResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BaseCompatibilityScreenActivity;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.NetworkUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class AlarmsSleepQuestionnaire extends BaseCompatibilityScreenActivity {
    Disposable mDisposables;
    UserService questionnareService;
    RecyclerView answerView;
    public static Button close;
    public static AlarmsSleepQuestionnaire activity;
    ConstraintLayout autoDialogShareContainer;
    int answerId;
    String answerContent;
    TextView tvQuestion, tvTitle;
    LinearLayout quisLayout;
    CircularProgressView circularProgressView;
    CardView cardViewQuestion;

    WeeklyScoreAdviceResponse weeklyScoreAdvice;
    public static final String CURRENT_SCREEN = "current_screen";

    public static ArrayList<Question> questions;


    private static SVProgressHUD progressDialog = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmsPopup.isAboutToTriggerQuestionnaire = false;
        setContentView(R.layout.alarm_sleep_questionnaire);
        activity = this;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (NetworkUtil.isNetworkConnected(this)) {
            QSSleepDailyModel.adsShowed(day);
        }
        cardViewQuestion = findViewById(R.id.cardViewQuestion);
        autoDialogShareContainer = findViewById(R.id.autoDialogShareContainer);
        questionnareService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        close = (Button) findViewById(R.id.close);
        close.setText(LanguageProvider.getLanguage("UI000781C010"));
        answerView = (RecyclerView) findViewById(R.id.answerView);
        quisLayout = (LinearLayout) findViewById(R.id.quisLayout);
        circularProgressView = findViewById(R.id.progress_view);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvQuestion = (TextView) findViewById(R.id.tvQuestion);
        quisLayout.setVisibility(View.GONE);
        showLoading();
        close.setOnClickListener((view) -> {
            try {
                showLoading();
                insertWeeklyAdvice();
                sendToServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        SleepQuestionnaireModel.clear();
        SleepQuestionnaireQuestionModel.clear();
        SleepQuestionnaireAnswerModel.clear();
        ForestModel.clear();
        getSleepQuestionnare();
    }


    AlarmsSleepAnswerListAdapter adapter;

    private void setupView() {
        if (questions.size() > 0 && questions.get(0) != null) {
            tvTitle.setText(SleepQuestionnaireModel.getFirst().getTitle());
            tvQuestion.setText(questions.get(0).getQuestion());
            close.setEnabled(false);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            answerView.setLayoutManager(layoutManager);
            // specify an adapter (see also next example)
            adapter = new AlarmsSleepAnswerListAdapter(getApplicationContext(), questions.get(0), activity);
            answerView.setAdapter(adapter);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void loadQuestion() {
        questions = new ArrayList<>();
        SleepQuestionnaireQuestionModel qqmread = new SleepQuestionnaireQuestionModel();
        for (int i = 0; i < qqmread.getAll().size(); i++) {
            SleepQuestionnaireAnswerModel qqaread = new SleepQuestionnaireAnswerModel();

            ArrayList<QuestionAnswer> answers = new ArrayList<>();
            //filter
            ArrayList<SleepQuestionnaireAnswerModel> qqafilter = qqaread.getByQuestionID(qqmread.getAll().get(i).getQuestion_id());
            for (int j = 0; j < qqafilter.size(); j++) {
                answers.add(new QuestionAnswer(qqafilter.get(j).getAnswer_id(), qqafilter.get(j).getContent(), qqafilter.get(j).getIconIndex()));
            }
            Question question = new Question(Question.ONE_CHOICE, qqmread.getAll().get(i).getQuestion_id(), qqmread.getAll().get(i).getContent(), answers);
            questions.add(question);
        }
        setupView();
    }

    public void refreshAdapter(AlarmsSleepQuestionnaire alarmsSleepQuestionnaire, Question question) {
//        loadQuestion();
        adapter.setList(question, alarmsSleepQuestionnaire);
        adapter.notifyDataSetChanged();
    }

    public void setAnswer(int Id, String answer) {
        answerId = Id;
        answerContent = answer;
        close.setEnabled(false);
        if (answer != null && answer.trim().length() > 0) {
            close.setEnabled(true);
        }
    }

    public int getAnswer(){
        return answerId;
    }

    @SuppressLint("CheckResult")
    public void sendToServer() {
        SleepQuestionnaireResult questionnaireResult = new SleepQuestionnaireResult();
        if (SleepQuestionnaireModel.getAll().size() > 0) {
            questionnaireResult.setId(SleepQuestionnaireModel.getFirst().getQuestionnaire_id());
            questionnaireResult.setTitle(SleepQuestionnaireModel.getFirst().getTitle());
            questionnaireResult.setDescription(SleepQuestionnaireModel.getFirst().getDescription());
        }

        List<SleepQuestionnaireQuestionResult> listQuestion = new ArrayList<>();
        List<String> listAnswer;
        List<Integer> listIconIndex;
        List<Integer> listAnswerId;

        for (int i = 0; i < SleepQuestionnaireQuestionModel.getAll().size(); i++) {
            SleepQuestionnaireQuestionModel aaa = SleepQuestionnaireQuestionModel.getAll().get(i);
            listAnswer = new ArrayList<>();
            listIconIndex = new ArrayList<>();
            listAnswerId = new ArrayList<>();

            for (int j = 0; j < SleepAnswerResult.getAllById(SleepQuestionnaireQuestionModel.getAll().get(i).getQuestion_id()).size(); j++) {
                SleepAnswerResult temp = SleepAnswerResult.getAllById(SleepQuestionnaireQuestionModel.getAll().get(i).getQuestion_id()).get(j);
                listAnswer.add(temp.getAnswer());
                listIconIndex.add(temp.getIconIndex());
                listAnswerId.add(temp.getAnswerId());
            }
            listQuestion.add(new SleepQuestionnaireQuestionResult(aaa.getQuestion_id(), aaa.getContent(), listAnswer, listIconIndex, listAnswerId));
        }
        questionnaireResult.setResult(listQuestion);
        if (SleepAnswerResult.getDataAnswerbyId((questions.get(0).getQuestionId())) != null) {
            SleepQuestionnaireProvider.sendSleepQuestionnareToServer(this, questionnaireResult, new SleepQuestionnaireProvider.SendSleepQuestionnaireListener() {
                @Override
                public void onSuccessSend(BaseResponse<String> result) {
                    if(result!=null) {
                        SleepQuestionnaireModel.clear();
                        SleepQuestionnaireQuestionModel.clear();
                        SleepQuestionnaireAnswerModel.clear();
                    }else {
                        hideLoading();
                    }

                    ForestProvider.getForestCalculation(new ForestProvider.ForestListener() {
                        @Override
                        public void onCalculateForestScoreSuccess() {
                            finish();
                            hideLoading();
                            close(null);
                        }

                        @Override
                        public void onCalculateForestScoreError(boolean error, Throwable e) {
                            finish();
                            hideLoading();
                            close(null);
                        }
                    },0, 0);
                }

                @Override
                public void onErrorSend(String errTag) {
                    hideLoading();
                    SleepQuestionnaireModel.clear();
                    SleepQuestionnaireQuestionModel.clear();
                    SleepQuestionnaireAnswerModel.clear();
                    finish();
                }
            }, 0);
        }
    }

    public void close(View view) {
        setResult(1);//arbitrary value just to notify HomeActivity
        finish();
    }

    private void getSleepQuestionnare() {
        UserService userService = ApiClient.getClient(this).create(UserService.class);
        mDisposables = questionnareService.getSleepQuestionnaire(UserLogin.getUserLogin().getId(), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<SleepQuestionnaireResponse>>() {
                    public void onSuccess(BaseResponse<SleepQuestionnaireResponse> qResponse) {
                        if(qResponse != null && qResponse.getData() != null && qResponse.getSuccess()) {
                            runOnUiThread(() -> {
                                PendingAlarmModel.clear();
                            });

                            if (qResponse.getData() != null) {
                                LogUserAction.sendNewLog(userService,"WEEKLY_SCORE_TRACK","getSleepQuestionnaire data not null","","UI000502");
                                weeklyScoreAdvice = qResponse.getData().getWeeklyScore();
                            }

                            InsertToDatabase(qResponse.getData());
                        }else{
                            close(null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        close(null);
                    }
                });
    }

    private void insertWeeklyAdvice() {
        UserService userService = ApiClient.getClient(this).create(UserService.class);
        LogUserAction.sendNewLog(userService,"WEEKLY_SCORE_TRACK","insertWeeklyAdvice","","UI000502");
        if (weeklyScoreAdvice != null) {
            LogUserAction.sendNewLog(userService,"WEEKLY_SCORE_TRACK","insertWeeklyAdvice weeklyScoreAdvice not null","","UI000502");
            if (weeklyScoreAdvice.getAdvice() != null) {
                LogUserAction.sendNewLog(userService,"WEEKLY_SCORE_TRACK","insertWeeklyAdvice weeklyScoreAdvice advice null","","UI000502");
                // clear old data
                WeeklyScoreReviewModel.clear();

                // insert new data
                Date currDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

                WeeklyScoreReviewModel wsr = new WeeklyScoreReviewModel();
                wsr.setDatePrimary(sdf.format(currDate));
                wsr.setAdvice(weeklyScoreAdvice.getAdvice());
                wsr.setLastUpdate(currDate);
                wsr.insert();
            }
        }
    }

    private void getForestCalculation(int retryCount){
        mDisposables = questionnareService.forestCalculation(UserLogin.getUserLogin().getId(),0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse<ForrestScoreAdviceResponse>>() {
                    @Override
                    public void onNext(BaseResponse<ForrestScoreAdviceResponse> baseResponse) {
                        hideLoading();
                        if (baseResponse.getData() != null) {
                            ForestModel fm = new ForestModel();
                            fm.setScore(baseResponse.getData().getScore());
                            fm.setUserNickname(baseResponse.getData().getUser_nickname());
                            fm.setAdvice(baseResponse.getData().getAdvice());
                            fm.setImg(baseResponse.getData().getImg());
                            fm.setDate(baseResponse.getData().getDate());
                            fm.insert();

                            close(null);
                        }else {
                            if(retryCount<BuildConfig.MAX_RETRY){
                                new Handler().postDelayed(() -> getForestCalculation(retryCount+1),BuildConfig.REQUEST_TIME_OUT);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            new Handler().postDelayed(() -> {
                                getForestCalculation(retryCount+1);
                            },BuildConfig.REQUEST_TIME_OUT);
                        }else {
                            hideLoading();
                            close(null);
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void InsertToDatabase(SleepQuestionnaireResponse data) {
        if (data != null) {
            SleepQuestionnaireModel qtrunc = new SleepQuestionnaireModel();
            qtrunc.truncate();
            SleepQuestionnaireQuestionModel qqmtrunc = new SleepQuestionnaireQuestionModel();
            qqmtrunc.truncate();
            SleepQuestionnaireAnswerModel qamtrunc = new SleepQuestionnaireAnswerModel();
            qamtrunc.truncate();
            SleepAnswerResult.clear();
            SleepQuestionnaireModel qm = new SleepQuestionnaireModel();
            qm.setQuestionnaire_id(data.getQuestionnaireId());
            qm.setTitle(data.getTitle());
            qm.setDescription(data.getDescription());
            for (int i = 0; i < data.getQuestions().size(); i++) {
                SleepQuestionnaireQuestionModel qqm = new SleepQuestionnaireQuestionModel();
                qqm.setQuestionnaire_id(data.getQuestionnaireId());
                qqm.setQuestion_id(data.getQuestions().get(i).getQuestionId());
                qqm.setIs_multiple_choice(data.getQuestions().get(i).getisMultipleChoice() == null ? false : data.getQuestions().get(i).getisMultipleChoice());
                qqm.setContent(data.getQuestions().get(i).getContent());
                for (int j = 0; j < data.getQuestions().get(i).getAnswers().size(); j++) {
                    SleepQuestionnaireAnswerModel qam = new SleepQuestionnaireAnswerModel();
                    qam.setQuestion_id(data.getQuestions().get(i).getQuestionId());
                    qam.setAnswer_id(data.getQuestions().get(i).getAnswers().get(j).getAnswerId());
                    qam.setContent(data.getQuestions().get(i).getAnswers().get(j).getContent());
                    qam.setIconIndex(data.getQuestions().get(i).getAnswers().get(j).getIconIndex());
                    qam.insert();
                }
                qqm.insert();
            }
            qm.insert();
        }

        if (SleepQuestionnaireModel.getAll().size() > 0 && SleepQuestionnaireQuestionModel.getAll().size() > 0 && SleepQuestionnaireAnswerModel.getAll().size() > 0) {
            loadQuestion();
            cardViewQuestion.setVisibility(View.VISIBLE);
            quisLayout.setVisibility(View.VISIBLE);
            hideLoading();
        } else {
            close(null);
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }



    private void showLoading() {
        progressDialog = new SVProgressHUD(this);
        progressDialog.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Clear);
    }

    private void hideLoading() {
        if (progressDialog != null) progressDialog.dismissImmediately();

    }


}

