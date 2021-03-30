package com.paramount.bed.util;

import com.paramount.bed.data.model.DailyScoreModel;
import com.paramount.bed.data.model.WeeklyScoreModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmResults;
import io.realm.RealmSchema;
import io.realm.Sort;

// Example migration adding a new class
public class MigrationUtil implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        //Check versi Database yang akan di Migrasi
        if (oldVersion == 0) {
            RealmObjectSchema maxRowModel = schema.get("MaxRowModel");
            if (maxRowModel == null) {
                schema.create("MaxRowModel")
                        .addField("maxRowLog", int.class)
                        .addField("maxRowDailyScore", int.class)
                        .addField("maxRowWeeklyScore", int.class);
            }

            RealmObjectSchema dailyScoreSchema = schema.get("DailyScoreModel");
            if (dailyScoreSchema != null && !dailyScoreSchema.hasField("datePrimary")) {
                //get current data, distinct to filter duplicates
                RealmResults<DynamicRealmObject> distinctDailyScore = realm.where("DailyScoreModel").sort("lastUpdate", Sort.DESCENDING).distinct("date").findAll();

                //store in a temporary list
                ArrayList<DailyScoreModel> dailyScoreModels = new ArrayList<>();
                for (DynamicRealmObject singleScore : distinctDailyScore
                ) {
                    DailyScoreModel newItem = new DailyScoreModel();
                    newItem.date = singleScore.getDate("date");
                    newItem.data = singleScore.getString("data");
                    newItem.lastUpdate = singleScore.getString("lastUpdate");

                    dailyScoreModels.add(newItem);
                }

                //delete all data
                realm.delete("DailyScoreModel");

                //reinsert unique rows
                for (DailyScoreModel singleScore : dailyScoreModels
                ) {
                    DynamicRealmObject newScore = realm.createObject("DailyScoreModel");
                    newScore.set("date", singleScore.date);
                    newScore.set("data", singleScore.data);
                    newScore.set("lastUpdate", singleScore.lastUpdate);
                }

                schema.get("DailyScoreModel")
                        .addField("datePrimary", String.class)
                        .transform(obj -> {

                            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                            Date oldDate = obj.getDate("date");
                            obj.setString("datePrimary", format.format(oldDate));
                        })
                        .addPrimaryKey("datePrimary");
            }

            RealmObjectSchema weeklyScoreSchema = schema.get("WeeklyScoreModel");
            if (weeklyScoreSchema != null && !weeklyScoreSchema.hasField("datePrimary")) {
                //get current data, distinct to filter duplicates
                RealmResults<DynamicRealmObject> distinctWeeklyScore = realm.where("WeeklyScoreModel").sort("lastUpdate", Sort.DESCENDING).distinct("start_date").findAll();

                //store in a temporary list
                ArrayList<WeeklyScoreModel> weeklyScoreModels = new ArrayList<>();
                for (DynamicRealmObject singleScore : distinctWeeklyScore
                ) {
                    WeeklyScoreModel newItem = new WeeklyScoreModel();
                    newItem.start_date = singleScore.getDate("start_date");
                    newItem.end_date = singleScore.getDate("end_date");
                    newItem.data = singleScore.getString("data");
                    newItem.lastUpdate = singleScore.getString("lastUpdate");

                    weeklyScoreModels.add(newItem);
                }

                //delete all data
                realm.delete("WeeklyScoreModel");

                //reinsert unique rows
                for (WeeklyScoreModel singleScore : weeklyScoreModels
                ) {
                    DynamicRealmObject newScore = realm.createObject("WeeklyScoreModel");
                    newScore.set("start_date", singleScore.start_date);
                    newScore.set("end_date", singleScore.end_date);
                    newScore.set("data", singleScore.data);
                    newScore.set("lastUpdate", singleScore.lastUpdate);
                }

                schema.get("WeeklyScoreModel")
                        .addField("datePrimary", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {

                                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                                Date startDate = obj.getDate("start_date");
                                Date endDate = obj.getDate("end_date");
                                obj.setString("datePrimary", format.format(startDate) + format.format(endDate));
                            }
                        })
                        .addPrimaryKey("datePrimary");
            }
            RealmObjectSchema nemuriConstantSchema = schema.get("NemuriConstantsModel");
            if (nemuriConstantSchema != null && !nemuriConstantSchema.hasField("reconnectCount")) {
                schema.get("NemuriConstantsModel")
                        .addField("reconnectCount", int.class);
            }

            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema == null) {
                schema.create("FormPolicyModel")
                        .addField("ssidMinLength", int.class)
                        .addField("ssidMaxLength", int.class)
                        .addField("emailMinLength", int.class)
                        .addField("emailMaxLength", int.class)
                        .addField("nicknameMinLength", int.class)
                        .addField("nicknameMaxLength", int.class)
                        .addField("phoneNumberMinLength", int.class)
                        .addField("phoneNumberMaxLength", int.class)
                        .addField("pinLength", int.class)
                        .addField("companyCodeLength", int.class)
                        .addField("inquiryMinLength", int.class)
                        .addField("inquiryMaxLength", int.class)
                        .addField("ssidPassMinLength", int.class)
                        .addField("ssidPassMaxLength", int.class)
                        .addField("zipCodeLength", int.class);
            }

            RealmObjectSchema permissionRequestSchema = schema.get("PermissionRequestModel");
            if (permissionRequestSchema == null) {
                schema.create("PermissionRequestModel")
                        .addField("hasRequestPermission", boolean.class);
            }

            RealmObjectSchema registeringSchema = schema.get("RegisteringModel");
            if (registeringSchema == null) {
                schema.create("RegisteringModel")
                        .addField("idData", int.class, FieldAttribute.PRIMARY_KEY)
                        .addField("phoneNumber", String.class)
                        .addField("nickName", String.class)
                        .addField("birthDay", String.class)
                        .addField("gender", int.class)
                        .addField("zipCode", String.class)
                        .addField("address", String.class)
                        .addField("height", int.class)
                        .addField("weight", int.class);
            }
            RealmObjectSchema logUserSchema = schema.get("LogUserModel");
            if (logUserSchema != null && !logUserSchema.hasField("screenId")) {
                logUserSchema.addField("screenId", String.class);
            }
            oldVersion++;
        }
        if(oldVersion == 1){
            RealmObjectSchema nemuriScanModelSchema = schema.get("NemuriScanModel");
            if (nemuriScanModelSchema != null) {
                nemuriScanModelSchema.addField("minor", int.class)
                        .addField("revision", int.class)
                        .addField("major", int.class);
            }

            RealmObjectSchema nemuriScanTemporaryModelSchema = schema.get("NemuriScanTemporaryModel");
            if (nemuriScanTemporaryModelSchema != null) {
                nemuriScanTemporaryModelSchema.addField("minor", int.class)
                        .addField("revision", int.class)
                        .addField("major", int.class);
            }
            oldVersion++;
        }

        if(oldVersion == 2){
            RealmObjectSchema nemuriConstantsModelSchema = schema.get("NemuriConstantsModel");
            if (nemuriConstantsModelSchema != null) {
                nemuriConstantsModelSchema.addField("switchFWReconnectTime", int.class);
            }
            oldVersion++;
        }


        if(oldVersion == 3){
            RealmObjectSchema nemuriScanModelSchema = schema.get("NemuriScanModel");
            if (nemuriScanModelSchema != null) {
                nemuriScanModelSchema.addField("lastFWUpdate", long.class);
            }
            oldVersion++;
        }

        if(oldVersion == 4){
            RealmObjectSchema nemuriScanModelSchema = schema.get("NemuriScanModel");
            if (nemuriScanModelSchema != null) {
                nemuriScanModelSchema.addField("isFWUpdateFailed", boolean.class);
            }
            oldVersion++;
        }

        if(oldVersion == 5){
            RealmObjectSchema sleepQuestionnaireAnswerModelSchema = schema.get("SleepQuestionnaireAnswerModel");
            if (sleepQuestionnaireAnswerModelSchema != null) {
                sleepQuestionnaireAnswerModelSchema.addField("iconIndex", int.class);
            }
            RealmObjectSchema questionnaireAnswerModelSchema = schema.get("QuestionnaireAnswerModel");
            if (questionnaireAnswerModelSchema != null) {
                questionnaireAnswerModelSchema.addField("iconIndex", int.class);
            }

            oldVersion++;
        }

        if(oldVersion == 6){
            RealmObjectSchema firmwareIntroContentModel = schema.get("FirmwareIntroContentModel");
            if (firmwareIntroContentModel == null) {
                schema.create("FirmwareIntroContentModel")
                        .addField("content", String.class);
            }
            oldVersion++;
        }

        if(oldVersion == 7) {
            RealmObjectSchema sleepAnswerResultSchema = schema.get("SleepAnswerResult");
            if (sleepAnswerResultSchema != null) {
                sleepAnswerResultSchema.addField("iconIndex", int.class);
            }
            oldVersion++;
        }

        if(oldVersion == 8) {
            RealmObjectSchema constantsSchema = schema.get("NemuriConstantsModel");
            if (constantsSchema != null && !constantsSchema.hasField("splitByteTimeout")) {
                constantsSchema.addField("splitByteTimeout", float.class);
            }
            oldVersion++;
        }
        if(oldVersion == 9) {
            RealmObjectSchema settingSchema = schema.get("SettingModel");
            if (settingSchema != null) {
                settingSchema.addField("autodriveDegreeSetting", int.class);
            }
            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema != null) {
                formPolicySchema.addRealmListField("autodriveDegreeSetting",Integer.class);
            }
            oldVersion++;
        }
        if(oldVersion == 10) {
            RealmObjectSchema settingSchema = schema.get("SettingModel");
            if (settingSchema != null) {
                settingSchema.addField("timer_setting", int.class);
            }
            oldVersion++;
        }
        if(oldVersion == 11) {
            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema != null) {
                formPolicySchema.addField("asaOldVersionMajor",int.class);
                formPolicySchema.addField("asaOldVersionMinor",int.class);
                formPolicySchema.addField("asaOldVersionRevision",int.class);
            }
            RealmObjectSchema settingSchema = schema.get("SettingModel");
            if (settingSchema != null) {
                settingSchema.addField("mori_feature_active", int.class);
            }
            oldVersion++;
        }
        if (oldVersion == 12) {
            RealmObjectSchema mhsModelSchema = schema.get("MHSModel");
            if (mhsModelSchema == null) {
                schema.create("MHSModel")
                        .addField("score", int.class)
                        .addField("desiredHardness", int.class)
                        .addField("date", String.class)
                        .addRealmListField("mattressHardness",Integer.class);
                mhsModelSchema = schema.get("MHSModel");
            }
            RealmObjectSchema mattressSettingModelSchema = schema.get("MattressSettingModel");
            if (mattressSettingModelSchema == null) {
                schema.create("MattressSettingModel")
                        .addField("desiredHardness", int.class)
                        .addRealmObjectField("highestMHS",mhsModelSchema)
                        .addRealmListField("topMHS",mhsModelSchema)
                        .addRealmListField("historyMHS",mhsModelSchema);
            }
            oldVersion++;
        }
        if(oldVersion == 13) {
            RealmObjectSchema mattressHardnessSettingSchema = schema.get("MattressHardnessSettingModel");
            if (mattressHardnessSettingSchema == null) {
                schema.create("MattressHardnessSettingModel")
                        .addField("id", int.class)
                        .addField("value", String.class);
                mattressHardnessSettingSchema = schema.get("MattressHardnessSettingModel");
            }

            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema != null) {
                formPolicySchema.addRealmListField("mattressHardnessSetting",mattressHardnessSettingSchema);
            }

            RealmObjectSchema settingSchema = schema.get("SettingModel");
            if (settingSchema != null) {
                settingSchema.addField("user_desired_hardness", int.class);
            }
            oldVersion++;
        }
        if(oldVersion == 14) {
            RealmObjectSchema objectSchema = schema.get("RegisteringModel");
            if (objectSchema != null) {
                if(!objectSchema.hasField("desiredHardness"))
                    objectSchema.addField("desiredHardness", int.class);
            }

            RealmObjectSchema schemaWeeklyScoreReview = schema.get("WeeklyScoreReviewModel");
            if (schemaWeeklyScoreReview == null) {
                schema.create("WeeklyScoreReviewModel")
                        .addField("datePrimary", String.class)
                        .addField("advice", String.class)
                        .addField("lastUpdate", Date.class)
                        .addPrimaryKey("datePrimary");
            }

            oldVersion++;
        }
        if(oldVersion == 15) {
            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema != null) {
                if(!formPolicySchema.hasField("timeSleepResetSetting"))
                    formPolicySchema.addRealmListField("timeSleepResetSetting",Integer.class);
            }

            oldVersion++;
        }
        if(oldVersion == 16) {
            RealmObjectSchema settingSchema = schema.get("SettingModel");
            if (settingSchema != null) {
                if(!settingSchema.hasField("sleep_reset_timing"))
                    settingSchema.addField("sleep_reset_timing", int.class);
            }

            oldVersion++;
        }
        if(oldVersion == 17) {
            RealmObjectSchema settingSchema = schema.get("SettingModel");
            if (settingSchema != null) {
                if(!settingSchema.hasField("forest_report_allowed"))
                    settingSchema.addField("forest_report_allowed", boolean.class);
            }

            RealmObjectSchema pendingMHSSchema = schema.get("PendingMHSModel");
            if (pendingMHSSchema == null) {
                schema.create("PendingMHSModel")
                        .addField("epoch", long.class)
                        .addField("score", int.class)
                        .addField("desiredHardness", int.class)
                        .addField("date", String.class)
                        .addRealmListField("mattressHardness",Integer.class)
                        .addField("isSent", Boolean.class)
                        .addPrimaryKey("epoch");
            }

            oldVersion++;
        }
        if(oldVersion == 18) {
            RealmObjectSchema forest = schema.get("ForestModel");
            if (forest == null) {
                schema.create("ForestModel")
                        .addField("score", Integer.class)
                        .addField("advice", String.class)
                        .addField("img", String.class)
                        .addField("userNickname", String.class);
            }

            oldVersion++;
        }
        if(oldVersion == 19) {
            RealmObjectSchema forest = schema.get("ForestModel");
            if (forest != null) {
                if(!forest.hasField("date"))
                    forest.addField("date", String.class);
            }

            oldVersion++;
        }

        if(oldVersion == 20) {
            RealmObjectSchema pendingSleepResetSchema = schema.get("SleepResetModel");
            if (pendingSleepResetSchema == null) {
                schema.create("SleepResetModel")
                        .addField("startDate", Date.class)
                        .addField("endDate", Date.class);
            }

            oldVersion++;
        }

        if(oldVersion == 21) {
            RealmObjectSchema mhsModelSchema = schema.get("MHSModel");
            if (mhsModelSchema != null && !mhsModelSchema.hasField("isDefault")) {
                mhsModelSchema.addField("isDefault", int.class);
            }

            oldVersion++;
        }

        if(oldVersion == 22) {
            RealmObjectSchema settingSchema = schema.get("SettingModel");
            if (settingSchema != null) {
                if(!settingSchema.hasField("snoring_storage_enable"))
                    settingSchema.addField("snoring_storage_enable", int.class);
            }

            oldVersion++;
        }

        if(oldVersion == 23) {
            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema != null) {
                if(!formPolicySchema.hasField("snoringRecordingDelay")){
                    formPolicySchema.addField("snoringRecordingDelay", double.class);
                }

                if(!formPolicySchema.hasField("snoringMinDiskSpace")){
                    formPolicySchema.addField("snoringMinDiskSpace", double.class);
                }

                if(!formPolicySchema.hasField("snoringMaxRecordTime")){
                    formPolicySchema.addField("snoringMaxRecordTime", double.class);
                }
            }

            oldVersion++;
        }

        if(oldVersion == 24) {
            RealmObjectSchema settingSchema = schema.get("SettingModel");
            if (settingSchema != null) {
                if(!settingSchema.hasField("snoring_storage_enable"))
                    settingSchema.addField("snoring_storage_enable", int.class);
            }

            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema != null) {
                if(!formPolicySchema.hasField("snoringRecordingDelay")){
                    formPolicySchema.addField("snoringRecordingDelay", double.class);
                }

                if(!formPolicySchema.hasField("snoringMinDiskSpace")){
                    formPolicySchema.addField("snoringMinDiskSpace", double.class);
                }

                if(!formPolicySchema.hasField("snoringMaxRecordTime")){
                    formPolicySchema.addField("snoringMaxRecordTime", double.class);
                }
            }

            oldVersion++;
        }


        if(oldVersion == 25) {

            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema != null) {
                if(!formPolicySchema.hasField("snoreAnalysisParamSnoreTime")){
                    formPolicySchema.addField("snoreAnalysisParamSnoreTime", int.class);
                }

                if(!formPolicySchema.hasField("snoreAnalysisParamSnoreTh")){
                    formPolicySchema.addField("snoreAnalysisParamSnoreTh", int.class);
                }

                if(!formPolicySchema.hasField("snoreAnalysisParamSnoreInterval")){
                    formPolicySchema.addField("snoreAnalysisParamSnoreInterval", int.class);
                }

                if(!formPolicySchema.hasField("snoreAnalysisParamSnoreFileTime")){
                    formPolicySchema.addField("snoreAnalysisParamSnoreFileTime", int.class);
                }

                if(!formPolicySchema.hasField("snoreAnalysisParamSnoreOutCount")){
                    formPolicySchema.addField("snoreAnalysisParamSnoreOutCount", int.class);
                }

                if(!formPolicySchema.hasField("snoreAnalysisMaxStorage")){
                    formPolicySchema.addField("snoreAnalysisMaxStorage", int.class);
                }
            }

            oldVersion++;
        }

        if(oldVersion == 26) {

            RealmObjectSchema pendingMHSSchema = schema.get("PendingSnoringModel");
            if (pendingMHSSchema == null) {
                schema.create("PendingSnoringModel")
                        .addField("epoch", long.class)
                        .addField("snoringResult", String.class)
                        .addField("isSent",Boolean.class)
                        .addPrimaryKey("epoch");
            }

            oldVersion++;
        }


        if(oldVersion == 27) {

            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema != null) {
                if(!formPolicySchema.hasField("snoringMinDiskSpaceOnRecord")){
                    formPolicySchema.addField("snoringMinDiskSpaceOnRecord", double.class);
                }
            }
            oldVersion++;
        }

        if(oldVersion == 28) {

            RealmObjectSchema formPolicySchema = schema.get("FormPolicyModel");
            if (formPolicySchema != null) {
                if(!formPolicySchema.hasField("snoringMinDiskSpaceMargin")){
                    formPolicySchema.addField("snoringMinDiskSpaceMargin", double.class);
                }
            }
            oldVersion++;
        }

        if(oldVersion == 30) {

            RealmObjectSchema objectSchema = schema.get("SleepResetModel");
            if (objectSchema != null) {
                if(!objectSchema.hasField("backgroundDate")){
                    objectSchema.addField("backgroundDate", Date.class);
                }
            }
        }

    }
}