package com.paramount.bed.data.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.orhanobut.logger.Logger;
import com.paramount.bed.BedApplication;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.ble.pojo.NSSpec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class NemuriScanModel extends RealmObject {
    @PrimaryKey
    String serialNumber;
    String macAddress;
    String serverGeneratedId;
    String serverURL;
    Integer infoType;
    boolean isIntranet;
    boolean isMattressExist;
    boolean isBedExist;
    String lastConnectionTime;
    long lastUpdate;
    boolean isHeightSupported;
    int revision;
    int minor;
    int major;
    long lastFWUpdate;
    boolean isFWUpdateFailed;

    public NemuriScanModel() {
    }

    public NemuriScanModel(NSSpec nsSpec) {
        this.setSerialNumber("");
        this.setBedExist(nsSpec.isBedExist());
        this.setMattressExist(nsSpec.isMattressExist());
    }

    public void setInfoType(Integer infoType) {
        this.infoType = infoType;
    }

    public boolean isIntranet() {
        return isIntranet;
    }

    public void setIntranet(boolean intranet) {
        isIntranet = intranet;
    }

    public boolean isMattressExist() {
        return isMattressExist;
    }

    public void setMattressExist(boolean mattressExist) {
        isMattressExist = mattressExist;
    }

    public boolean isBedExist() {
        return isBedExist;
    }

    public void setBedExist(boolean bedExist) {
        isBedExist = bedExist;
    }

    public String getLastConnectionTime() {
        return lastConnectionTime;
    }

    public void setLastConnectionTime(String lastConnectionTime) {
        this.lastConnectionTime = lastConnectionTime;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getServerGeneratedId() {
        return serverGeneratedId;
    }

    public void setServerGeneratedId(String serverGeneratedId) {
        this.serverGeneratedId = serverGeneratedId;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public Integer getInfoType() {
        return infoType;
    }

    public void setInfoType(NSSpec.BED_MODEL bedModel) {
        if (bedModel == NSSpec.BED_MODEL.INTIME) {
            infoType = 2;
        } else if (bedModel == NSSpec.BED_MODEL.ACTIVE_SLEEP) {
            infoType = 1;
        } else {
            infoType = 0;
        }
    }

    public static String getLastConnect() {
        return NemuriScanModel.get() == null || NemuriScanModel.get().getLastConnectionTime() == null ? "" : NemuriScanModel.get().getLastConnectionTime();
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public static boolean getBedActive() {
        return NemuriScanModel.get() != null && NemuriScanModel.get().isBedExist();
    }


    public static boolean getMattressActive() {
        return NemuriScanModel.get() != null && NemuriScanModel.get().isMattressExist();
    }

    public long getLastFWUpdate() {
        return lastFWUpdate;
    }

    public void setLastFWUpdate(long lastFWUpdate) {
        this.lastFWUpdate = lastFWUpdate;
    }

    public boolean isHeightSupported() {
        return isHeightSupported;
    }

    public void setHeightSupported(boolean heightSupported) {
        isHeightSupported = heightSupported;
    }

    public void updateInfoType(NSSpec.BED_MODEL bedModel) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setInfoType(bedModel);
        realm.commitTransaction();
    }

    public NemuriScanModel getUnmanaged() {
        if(!isManaged()){
            return this;
        }
        Realm realm = Realm.getDefaultInstance();
        NemuriScanModel unmanagedObject =  realm.copyFromRealm(this);
        realm.close();
        return unmanagedObject;
    }
    public void updateMattressExist(boolean isMattressExist) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setMattressExist(isMattressExist);
        realm.commitTransaction();
    }

    public void updateVersion(int revision,int minor, int major,long lastFWUpdate) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setRevision(revision);
        setMajor(major);
        setMinor(minor);
        setLastFWUpdate(lastFWUpdate);
        realm.commitTransaction();
    }

    public void updateBedExist(boolean isBedExist) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setBedExist(isBedExist);
        realm.commitTransaction();
    }

    public void updateLastUpdate(long lastUpdate) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setLastUpdate(lastUpdate);
        realm.commitTransaction();
    }

    public void updateHeightSuppored(boolean isSupported) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setHeightSupported(isSupported);
        realm.commitTransaction();
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static NemuriScanModel get() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<NemuriScanModel> query = realm.where(NemuriScanModel.class);

        return query.findFirst();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(NemuriScanModel.class);
        realm.commitTransaction();

        SharedPreferences mSettings = BedApplication.getsApplication().getSharedPreferences("SN_NEMURI_SCAN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString("SERIAL_NUMBER", "");
        editor.apply();
    }

    public void updateSpec(NSSpec spec) {
        updateBedExist(spec.isBedExist());
        updateMattressExist(spec.isMattressExist());
        updateLastUpdate(System.currentTimeMillis() / 1000);
        updateVersion(spec.getRevision(),spec.getMinor(),spec.getMajor(),System.currentTimeMillis() / 1000);
    }

    public static NemuriScanModel updateDetail(boolean isBedExist, boolean isMattressExist, String lastConnectionTime) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<NemuriScanModel> query = realm.where(NemuriScanModel.class);
        NemuriScanModel result = query.findFirst();
        //Dummy
        if (result.getSerialNumber().startsWith("F")) {
            realm.beginTransaction();
            result.setBedExist(isBedExist);
            result.setMattressExist(isMattressExist);
            result.setLastConnectionTime(lastConnectionTime);
            result.setLastUpdate(0);
            realm.copyToRealmOrUpdate(result);
            realm.commitTransaction();
            return result;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastConnection = null;
        if(lastConnectionTime != null && !lastConnectionTime.isEmpty()) {
            try {
                lastConnection = dateFormat.parse(lastConnectionTime);
            } catch (ParseException e) {
                Logger.e(Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }


        realm.beginTransaction();
        if (lastConnection != null && (lastConnection.getTime() / 1000) > result.getLastUpdate()) {
            result.setBedExist(isBedExist);
            result.setMattressExist(isMattressExist);
            result.setLastUpdate(lastConnection.getTime() / 1000);
        }

        result.setLastConnectionTime(lastConnectionTime);
        realm.copyToRealmOrUpdate(result);
        realm.commitTransaction();

        return result;
    }

    public static ArrayList<NemuriScanModel> getAll() {
        ArrayList<NemuriScanModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<NemuriScanModel> query = realm.where(NemuriScanModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }

    public boolean needsFWUpdate(){
        int localRev = this.revision;
        int localMin =  this.minor;
        int localMaj =  this.major;
        boolean needsUpdate = false;

        if(localMaj < BuildConfig.FIRMWARE_MAJOR){
            needsUpdate = true;
        }else if(localMaj <= BuildConfig.FIRMWARE_MAJOR &&
                localMin < BuildConfig.FIRMWARE_MINOR){
            needsUpdate = true;
        }else if(localMaj <= BuildConfig.FIRMWARE_MAJOR &&
                localMin <= BuildConfig.FIRMWARE_MINOR &&
                localRev < BuildConfig.FIRMWARE_REVISION){
            needsUpdate = true;
        }
        return needsUpdate && !isDefaultFWVersion();
    }

    public boolean isOldFWVersion(int oldMajor, int oldMinor, int oldRevision){
        int localRev = this.revision;
        int localMin =  this.minor;
        int localMaj =  this.major;
        boolean isOldVersion = true;

        if(oldMajor < localMaj){
            isOldVersion = false;
        }else if(oldMajor <= localMaj &&
                oldMinor < localMin){
            isOldVersion = false;
        }else if(oldMajor <= localMaj &&
                oldMinor <= localMin &&
                oldRevision < localRev){
            isOldVersion = false;
        }
        return isOldVersion || isDefaultFWVersion();
    }

    public String getVersionString(){
        return this.major+"."+this.minor+"."+this.revision;
    }
    public boolean isDefaultFWVersion(){
        int localRev = this.revision;
        int localMin =  this.minor;
        int localMaj =  this.major;

        return (localRev == 0 && localMin == 0 && localMaj == 0);
    }
    public boolean isFWUpdateFailed() {
        return isFWUpdateFailed;
    }


    public void updateIsFWUpdateFailed(boolean isFWUpdateFailed) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.isFWUpdateFailed = isFWUpdateFailed;
        realm.commitTransaction();
    }

    public static NemuriScanModel getUnmanagedModel(){
        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        if(nemuriScanModel != null){
            nemuriScanModel = nemuriScanModel.getUnmanaged();
        }
        return nemuriScanModel;
    }

    public boolean onlyMattress(){
        return isMattressExist && !isBedExist;
    }
}
