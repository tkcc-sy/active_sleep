package com.paramount.bed.data.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.paramount.bed.BedApplication;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class UserLogin extends RealmObject {

    @PrimaryKey
    Integer id;
    Integer groupId;
    String groupName;
    String email;
    String nickname;
    String password;
    String zipCode;
    String prefecture;
    String city;
    String streetAddress;
    String birthDate;
    Integer gender;
    String phoneNumber;
    String sleepQuestionnaireId;
    String optionalQuestionnaireId;
    String userType;
    String userActiveFrom;
    String userActiveTo;
    boolean isBlocked;
    String passwordAttempt;
    String passwordOffline;
    String createdDate;
    String lastActivityDate;
    boolean phoneActivated;
    String snsToken;
    Integer snsProvider;
    Integer companyId;
    Integer height;
    Integer weight;
    Integer recommendationQuestionnaireId;
    String scanSerialNumber;
    String companyCode;
    String apiToken;
    boolean isLogin;
    String fcmToken;

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Integer getId() {
        if (UserLogin.getUserLogin() != null && UserLogin.getUserLogin().id != null) {
            BedApplication.UserId = UserLogin.getUserLogin().id;
        }
        if (BedApplication.UserId != null) {
            return BedApplication.UserId;
        }
        return id;
    }

    public String getPasswordOffline() {
        return passwordOffline;
    }

    public void setPasswordOffline(String passwordOffline) {
        this.passwordOffline = passwordOffline;
    }

    public void setId(Integer id) {
        BedApplication.UserId = id;
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPrefecture() {
        return prefecture;
    }

    public void setPrefecture(String prefecture) {
        this.prefecture = prefecture;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSleepQuestionnaireId() {
        return sleepQuestionnaireId;
    }

    public void setSleepQuestionnaireId(String sleepQuestionnaireId) {
        this.sleepQuestionnaireId = sleepQuestionnaireId;
    }

    public String getOptionalQuestionnaireId() {
        return optionalQuestionnaireId;
    }

    public void setOptionalQuestionnaireId(String optionalQuestionnaireId) {
        this.optionalQuestionnaireId = optionalQuestionnaireId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserActiveFrom() {
        return userActiveFrom;
    }

    public void setUserActiveFrom(String userActiveFrom) {
        this.userActiveFrom = userActiveFrom;
    }

    public String getUserActiveTo() {
        return userActiveTo;
    }

    public void setUserActiveTo(String userActiveTo) {
        this.userActiveTo = userActiveTo;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getPasswordAttempt() {
        return passwordAttempt;
    }

    public void setPasswordAttempt(String passwordAttempt) {
        this.passwordAttempt = passwordAttempt;
    }

    public String getCreatedDate() {
        return createdDate;

    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(String lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    public boolean isPhoneActivated() {
        return phoneActivated;
    }

    public void setPhoneActivated(boolean phoneActivated) {
        this.phoneActivated = phoneActivated;
    }

    public String getSnsToken() {
        return snsToken;
    }

    public void setSnsToken(String snsToken) {
        this.snsToken = snsToken;
    }

    public Integer getSnsProvider() {
        return snsProvider;
    }

    public void setSnsProvider(Integer snsProvider) {
        this.snsProvider = snsProvider;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getRecommendationQuestionnaireId() {
        return recommendationQuestionnaireId;
    }

    public void setRecommendationQuestionnaireId(Integer recommendationQuestionnaireId) {
        this.recommendationQuestionnaireId = recommendationQuestionnaireId;
    }

    public String getScanSerialNumber() {
        return scanSerialNumber;
    }

    public void setScanSerialNumber(String scanSerialNumber) {
        this.scanSerialNumber = scanSerialNumber;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public static String getApiToken() {
        UserLogin userLogin = UserLogin.getUserLogin();
//        if (userLogin != null && userLogin.apiToken != null && userLogin.apiToken.trim().length() != 0) {
//            BedApplication.token = userLogin.apiToken;
//        }
//        if (BedApplication.token != null) {
//            return BedApplication.token;
//        }
        if (userLogin == null) {
            return "";
        }
        return userLogin.apiToken;
    }

    public void setApiToken(String apiToken) {
        //#region Register Token
        SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("BED_TOKEN_SECURE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("BED_TOKEN_SECURE_KEY", apiToken);
        editor.apply();
        //#endregion
        BedApplication.token = apiToken;
        this.apiToken = apiToken;
    }

    public static boolean isLogin() {
        UserLogin userLogin = UserLogin.getUserLogin();
        if (userLogin == null) {
            return false;
        }
        return userLogin.isLogin;
    }

    public void setLogin(boolean login) {
        this.isLogin = login;
    }

    public static UserLogin getUserLogin() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<UserLogin> query = realm.where(UserLogin.class);
        UserLogin result = query.findFirst();
        return result;
    }

    public static void init() {
        UserLogin.clear();
        UserLogin defaultLogin = new UserLogin();
        defaultLogin.setLogin(false);
        defaultLogin.insert();
    }

    public void insert() {
        SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("BED_TOKEN_SECURE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("BED_TOKEN_SECURE_KEY", this.apiToken == null ? "" : this.apiToken);
        editor.apply();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public void update() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserLogin.getUserLogin().setNickname(this.nickname);

        //TODO:ChangePhoneNumber
        UserLogin.getUserLogin().setEmail(this.email);
        UserLogin.getUserLogin().setPhoneNumber(this.phoneNumber);
        UserLogin.getUserLogin().setGender(this.gender);
        UserLogin.getUserLogin().setSnsProvider(this.snsProvider);
        UserLogin.getUserLogin().setSnsToken(this.snsToken);

        UserLogin.getUserLogin().setBirthDate(this.birthDate);
        UserLogin.getUserLogin().setZipCode(this.zipCode);
        UserLogin.getUserLogin().setHeight(this.height);
        UserLogin.getUserLogin().setWeight(this.weight);
        UserLogin.getUserLogin().setStreetAddress(this.streetAddress);
        UserLogin.getUserLogin().setPasswordOffline(this.passwordOffline);
        realm.copyToRealmOrUpdate(this);
        realm.commitTransaction();
    }

    public static void clear() {
        BedApplication.token = null;
        //#region Register Token
        SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("BED_TOKEN_SECURE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("BED_TOKEN_SECURE_KEY", "");
        editor.apply();
        //#endregion
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(UserLogin.class);
        realm.commitTransaction();
    }

    public static void logout() {
        BedApplication.token = null;
        //#region Register Token
        SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("BED_TOKEN_SECURE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("BED_TOKEN_SECURE_KEY", "");

        //clear stored NS
        mSettings = BedApplication.getsApplication().getSharedPreferences("SN_NEMURI_SCAN", Context.MODE_PRIVATE);
        editor = mSettings.edit();
        editor.putString("SERIAL_NUMBER", "");
        editor.apply();

        ApiClient.LogData.clearLogData(BedApplication.getsApplication());
        ApiClient.LogData.setLoginStatus(BedApplication.getsApplication(), 2);
        //#endregion
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
//        UserLogin.getUserLogin().setPhoneNumber(null);
        UserLogin.getUserLogin().setApiToken(null);
        UserLogin.getUserLogin().setLogin(false);
        realm.copyToRealmOrUpdate(getUserLogin().getUserLogin());
        realm.commitTransaction();
    }

    public void updateFcmToken() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserLogin.getUserLogin().setFcmToken(this.fcmToken);
        realm.copyToRealmOrUpdate(this);
        realm.commitTransaction();
    }

    public static void updateIsLogin(boolean login) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserLogin user = UserLogin.getUserLogin();
        user.setLogin(login);
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }

    public static Boolean isUserExist() {
        if (getUserLogin() != null && getUserLogin().getId() != null && getUserLogin().isLogin == true) {
            return true;
        }
        return false;
    }

    public static ArrayList<UserLogin> getAll() {
        ArrayList<UserLogin> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<UserLogin> query = realm.where(UserLogin.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public static boolean haveRegisteredNS(){
        UserLogin userLoginData = UserLogin.getUserLogin();
        if (userLoginData != null){
            if(userLoginData.getScanSerialNumber() != null){
                return !userLoginData.getScanSerialNumber().isEmpty();
            }
        }
        return false;
    }
    public static void setRegisterdNSSerialNumber(String serialNumber) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        UserLogin user = UserLogin.getUserLogin();
        user.setScanSerialNumber(serialNumber);
        realm.copyToRealmOrUpdate(user);
        realm.commitTransaction();
    }
    public static void clearRegisteredNS(){
        UserLogin.setRegisterdNSSerialNumber("");
    }

    public int getAge(){
        int age = getAge("yyyy-MM-dd");
        if(age == 0){
            age = getAge("yyyy/MM/dd");
        }
        return age;
    }
    public int getAge(String format){
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date parsedBirthDate = new Date();
        int age = 0;
        try {
            parsedBirthDate = df.parse(birthDate);
            Calendar now = Calendar.getInstance();
            Calendar dob = Calendar.getInstance();
            dob.setTime(parsedBirthDate);

            int year1 = now.get(Calendar.YEAR);
            int year2 = dob.get(Calendar.YEAR);
            age = year1 - year2;
            int month1 = now.get(Calendar.MONTH);
            int month2 = dob.get(Calendar.MONTH);
            if (month2 > month1) {
                age--;
            } else if (month1 == month2) {
                int day1 = now.get(Calendar.DAY_OF_MONTH);
                int day2 = dob.get(Calendar.DAY_OF_MONTH);
                if (day2 > day1) {
                    age--;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return age ;
    }

    public int getRoundedAge(){
        int roundedAge = getAge() / 10;
        return roundedAge*10;
    }

    public String getGenderString(){
        if(gender == 1){
            return LanguageProvider.getLanguage("UI000670C005");
        }else if(gender == 2){
            return LanguageProvider.getLanguage("UI000670C006");
        }else if(gender == 3){
            return LanguageProvider.getLanguage("UI000670C007");
        }else {
            return LanguageProvider.getLanguage("UI000670C005");
        }
    }
}
