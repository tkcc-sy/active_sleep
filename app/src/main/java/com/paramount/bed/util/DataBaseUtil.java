package com.paramount.bed.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.paramount.bed.BedApplication;
import com.paramount.bed.data.model.ActivityModel;
import com.paramount.bed.data.model.AdsDailyModel;
import com.paramount.bed.data.model.AdvertiseModel;
import com.paramount.bed.data.model.AlarmStopModel;
import com.paramount.bed.data.model.AnswerResult;
import com.paramount.bed.data.model.AppStateModel;
import com.paramount.bed.data.model.ContentFaqModel;
import com.paramount.bed.data.model.ContentTNCAppUpdateModel;
import com.paramount.bed.data.model.ContentTNCModel;
import com.paramount.bed.data.model.ContentVersionModel;
import com.paramount.bed.data.model.DailyScoreModel;
import com.paramount.bed.data.model.DashboardModel;
import com.paramount.bed.data.model.DeviceSettingBedModelLog;
import com.paramount.bed.data.model.DeviceTemplateBedModel;
import com.paramount.bed.data.model.DeviceTemplateBedModelLog;
import com.paramount.bed.data.model.DeviceTemplateMattressModel;
import com.paramount.bed.data.model.DeviceTemplateMattressModelLog;
import com.paramount.bed.data.model.FAQLinkModel;
import com.paramount.bed.data.model.FAQModel;
import com.paramount.bed.data.model.ForceLogoutModel;
import com.paramount.bed.data.model.ForestModel;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.InquiryContentModel;
import com.paramount.bed.data.model.InquiryProductModel;
import com.paramount.bed.data.model.InquiryTypeModel;
import com.paramount.bed.data.model.IsForegroundModel;
import com.paramount.bed.data.model.LanguageModel;
import com.paramount.bed.data.model.LogUserModel;
import com.paramount.bed.data.model.MigrateLanguageModel;
import com.paramount.bed.data.model.MonitoringModel;
import com.paramount.bed.data.model.NemuriConstantsModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.NemuriScanTemporaryModel;
import com.paramount.bed.data.model.PasswordPolicyModel;
import com.paramount.bed.data.model.PendingAlarmModel;
import com.paramount.bed.data.model.PendingQuisShowModel;
import com.paramount.bed.data.model.PermissionRequestModel;
import com.paramount.bed.data.model.QSDailyModel;
import com.paramount.bed.data.model.QSSleepDailyModel;
import com.paramount.bed.data.model.QuestionGeneralModel;
import com.paramount.bed.data.model.QuestionnaireAnswerModel;
import com.paramount.bed.data.model.QuestionnaireModel;
import com.paramount.bed.data.model.QuestionnaireQuestionModel;
import com.paramount.bed.data.model.RegisterStep;
import com.paramount.bed.data.model.RegisteringModel;
import com.paramount.bed.data.model.SenderBirdieListModel;
import com.paramount.bed.data.model.SenderBirdieModel;
import com.paramount.bed.data.model.ServerModel;
import com.paramount.bed.data.model.SettingBedModel;
import com.paramount.bed.data.model.SettingBedTemplateDefaultModel;
import com.paramount.bed.data.model.SettingBedTemplateModel;
import com.paramount.bed.data.model.SettingMatressTemplateDefaultModel;
import com.paramount.bed.data.model.SettingMatressTemplateModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.SleepAnswerResult;
import com.paramount.bed.data.model.SleepQuestionnaireAnswerModel;
import com.paramount.bed.data.model.SleepQuestionnaireModel;
import com.paramount.bed.data.model.SleepQuestionnaireQuestionModel;
import com.paramount.bed.data.model.SleepResetModel;
import com.paramount.bed.data.model.SliderModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.TutorialImageModel;
import com.paramount.bed.data.model.TutorialShowModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.UserRegistrationModel;
import com.paramount.bed.data.model.ValidationEmailModel;
import com.paramount.bed.data.model.ValidationPhoneModel;
import com.paramount.bed.data.model.ValidationSNSModel;
import com.paramount.bed.data.model.VersionModel;
import com.paramount.bed.data.model.WeeklyScoreModel;
import com.paramount.bed.data.model.WeeklyScoreReviewModel;

import static com.paramount.bed.util.LogUtil.Logx;

public class DataBaseUtil {
    public static void wipeData(Activity activity, boolean isReset) {
        if (!isReset) {
            return;
        }
        AdsDailyModel.clear();
        AdvertiseModel.clear();
        AlarmStopModel.clear();
        AnswerResult.clear();
        DailyScoreModel.clear();
        WeeklyScoreModel.clear();
        NemuriScanModel.clear();
        PendingAlarmModel.clear();
        PendingQuisShowModel.clear();
        QSDailyModel.clear();
        QSSleepDailyModel.clear();
        RegisterStep.clear();
        SenderBirdieListModel.clear();
        SenderBirdieModel.clear();
        ValidationEmailModel.clear();
        ValidationPhoneModel.clear();
        ValidationSNSModel.clear();
        TutorialShowModel.clear();
        SettingModel.clear();
        RegisteringModel.clear();
        ForceLogoutModel.clear();
        LogUserModel.clear();
        WeeklyScoreReviewModel.clear();
        ForestModel.clear();
        SleepResetModel.clear();
        if(activity != null) {
            SnoreFileUtil.wipeAnalyzerFiles(activity.getApplicationContext());
        }

        if (activity != null && !activity.isFinishing()) {
            DisplayUtils.FONTS.bigFontStatus(activity, false);
        }
    }

    public static void wipeUserData(Context context) {
        TutorialShowModel.clear();
        AnswerResult.clear();
        RegisterStep.clear();
        ValidationEmailModel.clear();
        ValidationPhoneModel.clear();
        ValidationSNSModel.clear();
        RegisteringModel.clear();
        ForceLogoutModel.clear();
        LogUserModel.clear();
        WeeklyScoreReviewModel.clear();
        ForestModel.clear();

        SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("SN_NEMURI_SCAN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("SERIAL_NUMBER", "");
        editor.apply();

        //TODO:Comment this if want to keep BigFont Status of Last user Account after logout and Login Again
//        if (context != null) {
//            DisplayUtils.FONTS.bigFontStatus(context, false);
//        }
    }

    public static void LogRowSize() {
        Logx("DataBaseUtil:Row->" + ActivityModel.class.getSimpleName(), String.valueOf(ActivityModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + AdsDailyModel.class.getSimpleName(), String.valueOf(AdsDailyModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + AdvertiseModel.class.getSimpleName(), String.valueOf(AdvertiseModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + AlarmStopModel.class.getSimpleName(), String.valueOf(AlarmStopModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + AnswerResult.class.getSimpleName(), String.valueOf(AnswerResult.getAll().size()));
        Logx("DataBaseUtil:Row->" + AppStateModel.class.getSimpleName(), String.valueOf(AppStateModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + ContentFaqModel.class.getSimpleName(), String.valueOf(ContentFaqModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + ContentTNCAppUpdateModel.class.getSimpleName(), String.valueOf(ContentTNCAppUpdateModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + ContentTNCModel.class.getSimpleName(), String.valueOf(ContentTNCModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + ContentVersionModel.class.getSimpleName(), String.valueOf(ContentVersionModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + DailyScoreModel.class.getSimpleName(), String.valueOf(DailyScoreModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + DashboardModel.class.getSimpleName(), String.valueOf(DashboardModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + DeviceSettingBedModelLog.class.getSimpleName(), String.valueOf(DeviceSettingBedModelLog.getAll().size()));
        Logx("DataBaseUtil:Row->" + DeviceTemplateBedModel.class.getSimpleName(), String.valueOf(DeviceTemplateBedModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + DeviceTemplateBedModelLog.class.getSimpleName(), String.valueOf(DeviceTemplateBedModelLog.getAll().size()));
        Logx("DataBaseUtil:Row->" + DeviceTemplateMattressModel.class.getSimpleName(), String.valueOf(DeviceTemplateMattressModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + DeviceTemplateMattressModelLog.class.getSimpleName(), String.valueOf(DeviceTemplateMattressModelLog.getAll().size()));
        Logx("DataBaseUtil:Row->" + FAQLinkModel.class.getSimpleName(), String.valueOf(FAQLinkModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + FAQModel.class.getSimpleName(), String.valueOf(FAQModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + ForceLogoutModel.class.getSimpleName(), String.valueOf(ForceLogoutModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + FormPolicyModel.class.getSimpleName(), String.valueOf(FormPolicyModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + InquiryContentModel.class.getSimpleName(), String.valueOf(InquiryContentModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + InquiryProductModel.class.getSimpleName(), String.valueOf(InquiryProductModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + InquiryTypeModel.class.getSimpleName(), String.valueOf(InquiryTypeModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + IsForegroundModel.class.getSimpleName(), String.valueOf(IsForegroundModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + LanguageModel.class.getSimpleName(), String.valueOf(LanguageModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + LogUserModel.class.getSimpleName(), String.valueOf(LogUserModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + MigrateLanguageModel.class.getSimpleName(), String.valueOf(MigrateLanguageModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + MonitoringModel.class.getSimpleName(), String.valueOf(MonitoringModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + NemuriConstantsModel.class.getSimpleName(), String.valueOf(NemuriConstantsModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + NemuriScanModel.class.getSimpleName(), String.valueOf(NemuriScanModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + NemuriScanTemporaryModel.class.getSimpleName(), String.valueOf(NemuriScanTemporaryModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + PasswordPolicyModel.class.getSimpleName(), String.valueOf(PasswordPolicyModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + PendingAlarmModel.class.getSimpleName(), String.valueOf(PendingAlarmModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + PendingQuisShowModel.class.getSimpleName(), String.valueOf(PendingQuisShowModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + QSDailyModel.class.getSimpleName(), String.valueOf(QSDailyModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + QSSleepDailyModel.class.getSimpleName(), String.valueOf(QSSleepDailyModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + QuestionGeneralModel.class.getSimpleName(), String.valueOf(QuestionGeneralModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + QuestionnaireAnswerModel.class.getSimpleName(), String.valueOf(QuestionnaireAnswerModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + QuestionnaireModel.class.getSimpleName(), String.valueOf(QuestionnaireModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + QuestionnaireQuestionModel.class.getSimpleName(), String.valueOf(QuestionnaireQuestionModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + RegisteringModel.class.getSimpleName(), String.valueOf(RegisteringModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + RegisterStep.class.getSimpleName(), String.valueOf(RegisterStep.getAll().size()));
        Logx("DataBaseUtil:Row->" + SenderBirdieListModel.class.getSimpleName(), String.valueOf(SenderBirdieListModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SenderBirdieModel.class.getSimpleName(), String.valueOf(SenderBirdieModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + ServerModel.class.getSimpleName(), String.valueOf(ServerModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SettingBedModel.class.getSimpleName(), String.valueOf(SettingBedModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SettingBedTemplateDefaultModel.class.getSimpleName(), String.valueOf(SettingBedTemplateDefaultModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SettingBedTemplateModel.class.getSimpleName(), String.valueOf(SettingBedTemplateModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SettingMatressTemplateDefaultModel.class.getSimpleName(), String.valueOf(SettingMatressTemplateDefaultModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SettingMatressTemplateModel.class.getSimpleName(), String.valueOf(SettingMatressTemplateModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SettingModel.class.getSimpleName(), String.valueOf(SettingModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SleepAnswerResult.class.getSimpleName(), String.valueOf(SleepAnswerResult.getAll().size()));
        Logx("DataBaseUtil:Row->" + SleepQuestionnaireAnswerModel.class.getSimpleName(), String.valueOf(SleepQuestionnaireAnswerModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SleepQuestionnaireModel.class.getSimpleName(), String.valueOf(SleepQuestionnaireModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SleepQuestionnaireQuestionModel.class.getSimpleName(), String.valueOf(SleepQuestionnaireQuestionModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + SliderModel.class.getSimpleName(), String.valueOf(SliderModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + StatusLogin.class.getSimpleName(), String.valueOf(StatusLogin.getAll().size()));
        Logx("DataBaseUtil:Row->" + TutorialImageModel.class.getSimpleName(), String.valueOf(TutorialImageModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + TutorialShowModel.class.getSimpleName(), String.valueOf(TutorialShowModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + UserLogin.class.getSimpleName(), String.valueOf(UserLogin.getAll().size()));
        Logx("DataBaseUtil:Row->" + UserRegistrationModel.class.getSimpleName(), String.valueOf(UserRegistrationModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + ValidationEmailModel.class.getSimpleName(), String.valueOf(ValidationEmailModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + ValidationPhoneModel.class.getSimpleName(), String.valueOf(ValidationPhoneModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + ValidationSNSModel.class.getSimpleName(), String.valueOf(ValidationSNSModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + VersionModel.class.getSimpleName(), String.valueOf(VersionModel.getAll().size()));
        Logx("DataBaseUtil:Row->" + WeeklyScoreModel.class.getSimpleName(), String.valueOf(WeeklyScoreModel.getAll().size()));
    }
}
