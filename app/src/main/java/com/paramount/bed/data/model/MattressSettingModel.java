package com.paramount.bed.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;

public class MattressSettingModel extends RealmObject {
    @JsonProperty("user_desired_hardness")
    int desiredHardness;
    @JsonProperty("highest_mhs")
    MHSModel highestMHS;
    @JsonProperty("top_mhs")
    RealmList<MHSModel> topMHS;
    @JsonProperty("history_mhs")
    RealmList<MHSModel> historyMHS;

    public int getDesiredHardness() {
        return desiredHardness;
    }

    public void setDesiredHardness(int desiredHardness) {
        this.desiredHardness = desiredHardness;
    }

    public MHSModel getHighestMHS() {
        return highestMHS;
    }

    public void setHighestMHS(MHSModel highestMHS) {
        this.highestMHS = highestMHS;
    }

    public RealmList<MHSModel> getTopMHS() {
        return topMHS;
    }

    public void setTopMHS(RealmList<MHSModel> topMHS) {
        this.topMHS = topMHS;
    }

    public RealmList<MHSModel> getHistoryMHS() {
        return historyMHS;
    }

    public void setHistoryMHS(RealmList<MHSModel> historyMHS) {
        this.historyMHS = historyMHS;
    }
    public void enqueHistoryMHS(MHSModel newHistoryMHS){
        //only for unmanaged object
        historyMHS.remove(historyMHS.size()-1);
        historyMHS.add(0,newHistoryMHS);
    }
    public static void clear(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(MattressSettingModel.class);
        realm.commitTransaction();
    }

    public void insert(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static MattressSettingModel get() {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<MattressSettingModel> query = realm.where(MattressSettingModel.class);
        MattressSettingModel result = query.findFirst();
        if (result == null) {
            result = MattressSettingModel.defaultSetting();
        }

        return result;
    }

    static MattressSettingModel defaultSetting(){
        MHSModel highestMHS = MattressSettingModel.defaultHighestMHS();
        MHSModel top1MHS = MattressSettingModel.defaultTopMHS();

        MattressSettingModel retVal = new MattressSettingModel();
        retVal.highestMHS = highestMHS;
        retVal.topMHS = new RealmList<>();
        retVal.topMHS.add(top1MHS);
        retVal.desiredHardness = 3;

        return retVal;
    }

    static MHSModel defaultHighestMHS(){
        MHSModel highestMHS = new MHSModel();
        highestMHS.setIsDefault(1);
        highestMHS.mattressHardness = new RealmList<>();
        highestMHS.mattressHardness.add(7);
        highestMHS.mattressHardness.add(7);
        highestMHS.mattressHardness.add(5);
        highestMHS.mattressHardness.add(7);
        highestMHS.mattressHardness.add(3);
        highestMHS.mattressHardness.add(3);

        return highestMHS;
    }

    static MHSModel defaultTopMHS(){
        MHSModel topMHS = new MHSModel();
        topMHS.mattressHardness = new RealmList<>();
        topMHS.mattressHardness.add(7);
        topMHS.mattressHardness.add(7);
        topMHS.mattressHardness.add(5);
        topMHS.mattressHardness.add(7);
        topMHS.mattressHardness.add(3);
        topMHS.mattressHardness.add(3);

        return topMHS;
    }

    public MattressSettingModel getUnmanaged() {
        if(!isManaged()){
            return this;
        }
        Realm realm = Realm.getDefaultInstance();
        MattressSettingModel unmanagedObject =  realm.copyFromRealm(this);
        realm.close();
        return unmanagedObject;
    }
}
