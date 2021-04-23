package com.paramount.bed.data.model;

import com.paramount.bed.ble.pojo.NSSpec;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.annotations.PrimaryKey;

public class NemuriScanTemporaryModel extends RealmObject {
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
    int major;
    int minor;
    int revision;

    public NemuriScanTemporaryModel() {
    }

    public NemuriScanTemporaryModel(NSSpec nsSpec) {
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
        } else if (bedModel == NSSpec.BED_MODEL.INTIME_COMFORT) {
            infoType = 3;
        } else {
            infoType = 0;
        }
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public static String getLastConnect() {
        return NemuriScanTemporaryModel.get() == null || NemuriScanTemporaryModel.get().getLastConnectionTime() == null ? "" : NemuriScanTemporaryModel.get().getLastConnectionTime();
    }

    public static boolean getBedActive() {
        return NemuriScanTemporaryModel.get() != null && NemuriScanTemporaryModel.get().isBedExist();
    }


    public static boolean getMattressActive() {
        return NemuriScanTemporaryModel.get() != null && NemuriScanTemporaryModel.get().isMattressExist();
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

    public void updateMattressExist(boolean isMattressExist) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        setMattressExist(isMattressExist);
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

    public static NemuriScanTemporaryModel get() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<NemuriScanTemporaryModel> query = realm.where(NemuriScanTemporaryModel.class);

        return query.findFirst();
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(NemuriScanTemporaryModel.class);
        realm.commitTransaction();
    }

    public void updateSpec(NSSpec spec) {
        updateBedExist(spec.isBedExist());
        updateMattressExist(spec.isMattressExist());
        updateLastUpdate(System.currentTimeMillis() / 1000);
    }

    public static NemuriScanTemporaryModel updateDetail(boolean isBedExist, boolean isMattressExist, String lastConnectionTime) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<NemuriScanTemporaryModel> query = realm.where(NemuriScanTemporaryModel.class);
        NemuriScanTemporaryModel result = query.findFirst();
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastConnection;
        try {
            lastConnection = dateFormat.parse(lastConnectionTime);
        } catch (ParseException e) {
            lastConnection = null;
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

    public static ArrayList<NemuriScanTemporaryModel> getAll() {
        ArrayList<NemuriScanTemporaryModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<NemuriScanTemporaryModel> query = realm.where(NemuriScanTemporaryModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
}
