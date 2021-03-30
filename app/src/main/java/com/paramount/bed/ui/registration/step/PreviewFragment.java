package com.paramount.bed.ui.registration.step;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.paramount.bed.BedApplication;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.AnswerResult;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MattressHardnessSettingModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.NemuriScanTemporaryModel;
import com.paramount.bed.data.model.QSSleepDailyModel;
import com.paramount.bed.data.model.QuestionnaireModel;
import com.paramount.bed.data.model.QuestionnaireQuestionModel;
import com.paramount.bed.data.model.QuestionnaireQuestionResult;
import com.paramount.bed.data.model.QuestionnaireResult;
import com.paramount.bed.data.model.RegisteringModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.UserRegistrationModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.LoginResponse;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DataBaseUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class PreviewFragment extends BLEFragment {
    public static List<Preview> previewList;
    String qustionResult;
    String ns_serial_number;
    static int MAX_LENGTH = 8;

    RegistrationStepActivity activity;
    public Boolean isRegisterButton = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_preview, container, false);

        activity = (RegistrationStepActivity) getActivity();
        Button btnRegister = (Button) view.findViewById(R.id.btnRegister);
        RecyclerView rvPreview = (RecyclerView) view.findViewById(R.id.rvPreview);
        btnRegister.setOnClickListener(register());

        String gender = "-";

        switch (activity.GENDER) {
            case 1:
                gender = LanguageProvider.getLanguage("UI000440C006");
                break;
            case 2:
                gender = LanguageProvider.getLanguage("UI000440C007");
                break;
            case 3:
                gender = LanguageProvider.getLanguage("UI000440C008");
                break;
            default:
                gender = "-";
                break;
        }
        previewList = new ArrayList<>();
        if (BuildConfig.DEMO_MODE) {
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C003"), "taro.yamada@gmail.com"));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C004"), "123-4567-8910"));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C005"), "山田太郎"));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C006"), "1900/12/12"));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C007"), gender));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C008"), "154-8543"));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C009"), "170 cm"));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C010"), "60 kg"));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C011"), "999"));
        } else {
            loadQuestionResult();
//            activity.SERIAL_NUMBER = UUID.randomUUID().toString().substring(0, 8);

            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C003"), activity.EMAIL));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C004"), activity.PHONE_NUMBER));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C005"), StringUtil.nickName(activity.NICK_NAME)));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C006"), activity.BIRTH_DAY));
            previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C007"), gender));

            String zipBegin;
            String zipLast;
            if (!activity.ZIP_CODE.equals("")) {
                zipBegin = activity.ZIP_CODE.substring(0, 3);
                zipLast = activity.ZIP_CODE.substring(3, 7);
                previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C008"), zipBegin + "-" + zipLast));
            } else {
                previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C008"), "-"));
                activity.CITY = "";
                activity.PREFECTURE = "";
                activity.ADDRESS = "";
            }
            if (activity.HEIGHT.toString().equals("") || !activity.HEIGHT.toString().contains("cm")) {
                previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C009"), "-"));
            } else {
                previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C009"), activity.HEIGHT));
            }

            if (activity.WEIGHT.toString().equals("") || !activity.WEIGHT.toString().contains("kg")) {
                previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C010"), "-"));
            } else {
                previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C010"), activity.WEIGHT));
            }

            if (activity.IS_COMPANY_REGISTER == 1) {
                if (!activity.COMPANY_CODE.equals("-")) {
                    previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C011"), activity.COMPANY_CODE));
                }
            } else {
                activity.COMPANY_CODE = "-";
            }
            NemuriScanModel nemuriScanModel = BluetoothListFragment.selectedNemuriScan;
            if(nemuriScanModel != null && nemuriScanModel.isMattressExist()){
                FormPolicyModel formPolicyModel = FormPolicyModel.getPolicy();
                MattressHardnessSettingModel selectedHardness = formPolicyModel.getMattressHardnessSettingById(RegisteringModel.getProfile().getDesiredHardness());
                previewList.add(new Preview(LanguageProvider.getLanguage("UI000465C013"), selectedHardness.getValue()));
            }
        }

        Log.d("TAG", "onCreateView: TYPE : " + activity.TYPE +
                " EMAIL : " + activity.EMAIL +
                " PASSWORD : " + activity.PASSWORD +
                " ACCESS TOKEN : " + activity.ACCESS_TOKEN +
                " PHONE NUMBER : " + activity.PHONE_NUMBER +
                " NICK NAME : " + activity.NICK_NAME +
                " BIRTH DAY : " + activity.BIRTH_DAY +
                " GENDER : " + activity.GENDER +
                " ZIP CODE : " + activity.ZIP_CODE +
                " CITY : " + activity.CITY +
                " PREFECTURE : " + activity.PREFECTURE +
                " ADDRESS : " + activity.ADDRESS +
                " COMPANY CODE : " + activity.COMPANY_CODE +
                " HEIGHT : " + activity.HEIGHT +
                " WEIGHT : " + activity.WEIGHT +
                " USER TYPE : " + activity.USER_TYPE +
                " QUESTIONNAIRE_RESULT : " + activity.QUESTIONNAIRE_RESULT +
                " SERIAL_NUMBER : " + activity.SERIAL_NUMBER +
                " COMPANY_CODE : " + activity.COMPANY_CODE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvPreview.setLayoutManager(layoutManager);
        PreviewListAdapter adapter = new PreviewListAdapter(previewList);
        rvPreview.setAdapter(adapter);
        applyLocalization(view);
        return view;
    }

    private View.OnClickListener back() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                activity.onBackPressed();
            }
        };
    }

    private View.OnClickListener register() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!isRegisterButton) {
                    isRegisterButton = true;
                    if (BuildConfig.DEMO_MODE) {
                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    if (activity.COMPANY_CODE.equals("-")) {
                        activity.COMPANY_CODE = "";
                    }

                    int height = 0, weight = 0;
                    String temp = activity.HEIGHT.toString();

                    if (activity.HEIGHT.toString().equals("") || !activity.HEIGHT.toString().contains("cm")) {
                        height = 0;
                    } else {
                        height = Integer.parseInt(activity.HEIGHT.toString().replace("cm", "").trim());
                    }

                    if (activity.WEIGHT.toString().equals("") || !activity.WEIGHT.toString().contains("kg")) {
                        weight = 0;
                    } else {
                        weight = Integer.parseInt(activity.WEIGHT.toString().replace("kg", "").trim());
                    }

//                    if (BluetoothListFragment.selectedNemuriScan != null) {
//                        NemuriScanModel.clear();
//                        BluetoothListFragment.selectedNemuriScan.insert();
//                        BluetoothListFragment.selectedNemuriScan = null;
//                    }
                    NemuriScanTemporaryModel temporaryModel = NemuriScanTemporaryModel.get();
                    int desiredHardness = RegisteringModel.getProfile().getDesiredHardness();
                    activity.mDisposable = activity.userService.register(activity.EMAIL, activity.PASSWORD,
                            activity.TYPE, activity.ACCESS_TOKEN, activity.PHONE_NUMBER, StringUtil.nickName(activity.NICK_NAME), activity.BIRTH_DAY,
                            activity.GENDER, activity.ZIP_CODE, activity.CITY,
                            activity.PREFECTURE, activity.ADDRESS, activity.COMPANY_CODE, height,
                            weight, activity.USER_TYPE, activity.QUESTIONNAIRE_RESULT, activity.SERIAL_NUMBER, 1,temporaryModel.getMajor(),temporaryModel.getMinor(),temporaryModel.getRevision(),desiredHardness)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(new DisposableSingleObserver<BaseResponse<LoginResponse>>() {
                                @Override
                                public void onSuccess(BaseResponse<LoginResponse> response) {
                                    if (response.isSucces()) {
                                        if (activity.getIntent().getBooleanExtra(IntentUtil.User.IS_KICK_USER, false)) {
                                            LogUserAction.sendKickLog(activity.userService, "kick_user_execute", "UI000465");
                                            DataBaseUtil.wipeData(getActivity(), true);
                                        }
                                        UserLogin.clear();
                                        DataBaseUtil.wipeData(getActivity(), true);
                                        //Insert NemuriScan Temporary To Persistent
                                        NemuriScanTemporaryModel nSTM = NemuriScanTemporaryModel.get();
                                        NemuriScanModel nSM = new NemuriScanModel();
                                        nSM.setSerialNumber(nSTM.getSerialNumber());
                                        nSM.setMacAddress(nSTM.getMacAddress());
                                        nSM.setServerGeneratedId(nSTM.getServerGeneratedId());
                                        nSM.setServerURL(nSTM.getServerURL());
                                        nSM.setInfoType(nSTM.getInfoType());
                                        nSM.setIntranet(nSTM.isIntranet());
                                        nSM.setMattressExist(nSTM.isMattressExist());
                                        nSM.setBedExist(nSTM.isBedExist());
                                        nSM.setLastConnectionTime(nSTM.getLastConnectionTime());
                                        nSM.setLastUpdate(nSTM.getLastUpdate());
                                        nSM.setHeightSupported(nSTM.isHeightSupported());
                                        nSM.setMajor(nSTM.getMajor());
                                        nSM.setMinor(nSTM.getMinor());
                                        nSM.setRevision(nSTM.getRevision());
                                        nSM.insert();
                                        NemuriScanTemporaryModel.clear();

                                        UserRegistrationModel.clear();
                                        UserLogin userLogin = new UserLogin();
                                        userLogin.setId(response.getData().getId());
                                        userLogin.setGroupId(response.getData().getGroup_id());
                                        userLogin.setGroupName(response.getData().getGroup_name());
                                        userLogin.setEmail(response.getData().getEmail());
                                        userLogin.setNickname(response.getData().getNickname());
                                        userLogin.setPassword(response.getData().getPassword());
                                        userLogin.setZipCode(response.getData().getZip_code());
                                        userLogin.setPrefecture(response.getData().getPrefecture());
                                        userLogin.setCity(response.getData().getCity());
                                        userLogin.setStreetAddress(response.getData().getStreet_address());
                                        userLogin.setBirthDate(response.getData().getBirth_date());
                                        userLogin.setGender(response.getData().getGender());
                                        userLogin.setPhoneNumber(response.getData().getPhone_number());
                                        userLogin.setSleepQuestionnaireId(response.getData().getSleep_questionnaire_id());
                                        userLogin.setOptionalQuestionnaireId(response.getData().getOptional_questionnaire_id());
                                        userLogin.setUserType(response.getData().getUser_type());
                                        userLogin.setUserActiveFrom(response.getData().getUser_active_from());
                                        userLogin.setUserActiveTo(response.getData().getUser_active_to());
                                        userLogin.setBlocked(response.getData().is_blocked());
                                        userLogin.setPasswordAttempt(response.getData().getPassword_attempt());
                                        userLogin.setCreatedDate(response.getData().getCreated_date());
                                        userLogin.setLastActivityDate(response.getData().getLast_activity_date());
                                        userLogin.setPhoneActivated(response.getData().isPhone_activated());
                                        userLogin.setSnsToken(response.getData().getSns_token());
                                        userLogin.setSnsProvider(response.getData().getSns_provider());
                                        userLogin.setCompanyId(response.getData().getCompany_id());
                                        userLogin.setHeight(response.getData().getHeight());
                                        userLogin.setWeight(response.getData().getWeight());
                                        userLogin.setRecommendationQuestionnaireId(response.getData().getRecommendation_questionnaire_id());
                                        userLogin.setScanSerialNumber(response.getData().getNs_serial_number());
                                        userLogin.setCompanyCode(response.getData().getCompany_code());
                                        userLogin.setLogin(true);
                                        userLogin.setApiToken(response.getData().getApi_token());
                                        BedApplication.token = response.getData().getApi_token();
                                        ApiClient.LogData.setLogData(getActivity(), userLogin);
                                        userLogin.insert();


                                        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                                        QSSleepDailyModel.adsShowed(day);

                                        Intent intent = new Intent(getActivity(), HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        startActivity(intent);
                                        getActivity().finish();
                                    } else {
                                        isRegisterButton = false;
                                        DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(response.getMessage()));
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.d("abx", e.getMessage());
                                    isRegisterButton = false;
                                    if (!NetworkUtil.isNetworkConnected(getContext())) {
                                        DialogUtil.offlineDialog(activity, getContext());
                                    } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                        DialogUtil.tokenExpireDialog(activity);
                                    } else {
                                        DialogUtil.serverFailed(activity, "UI000802C133", "UI000802C134", "UI000802C135", "UI000802C136");
                                    }
                                }
                            });
                }
//                else {
//                    Toast.makeText(activity, "Wait !!!", Toast.LENGTH_SHORT).show();
//                }
            }
        };
    }

    public class Preview {
        String label;
        String content;

        public Preview(String label, String content) {
            this.label = label;
            this.content = content;
        }
    }

    public void loadQuestionResult() {

        Gson gson = new Gson();
        QuestionnaireResult questionnaireResult = new QuestionnaireResult();
        if (QuestionnaireModel.getAll().size() > 0) {
            questionnaireResult.setId(QuestionnaireModel.getTNC().getQuestionnaire_id());
            questionnaireResult.setTitle(QuestionnaireModel.getTNC().getTitle());
            questionnaireResult.setDescription(QuestionnaireModel.getTNC().getDescription());
        }

        List<QuestionnaireQuestionResult> listQuestion = new ArrayList<>();
        List<String> listAnswer;
        for (int i = 0; i < QuestionnaireQuestionModel.getAll().size(); i++) {
            listAnswer = new ArrayList<>();
            for (int j = 0; j < AnswerResult.getAllById(QuestionnaireQuestionModel.getAll().get(i).getQuestion_id()).size(); j++) {
                listAnswer.add(String.valueOf(AnswerResult.getAllById(QuestionnaireQuestionModel.getAll().get(i).getQuestion_id()).get(j).getAnswer()));
            }
            listQuestion.add(new QuestionnaireQuestionResult(QuestionnaireQuestionModel.getAll().get(i).getQuestion_id(), QuestionnaireQuestionModel.getAll().get(i).getContent(), listAnswer));
        }
        questionnaireResult.setResult(listQuestion);
        activity.QUESTIONNAIRE_RESULT = gson.toJson(questionnaireResult);

    }
}


