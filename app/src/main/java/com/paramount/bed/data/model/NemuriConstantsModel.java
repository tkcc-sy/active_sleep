package com.paramount.bed.data.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.orhanobut.logger.Logger;
import com.paramount.bed.ble.pojo.NSBedSpec;
import com.paramount.bed.ble.pojo.NSSpec;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NemuriConstantsModel extends RealmObject {
    @JsonProperty("ns_url_ip")
    public String nsUrlIp;
    @JsonProperty("ns_url")
    public String nsUrl;
    @JsonProperty("command_interval")
    public float commandInterval;
    @JsonProperty("operation_timeout")
    public int operationTimeout;
    @JsonProperty("status_polling_interval")
    public int statusPollingInterval;
    @JsonProperty("upper_bed_threshold")
    public int upperBedThreshold;
    @JsonProperty("lower_bed_threshold")
    public int lowerBedThreshold;
    @JsonProperty("bed_response_timeout")
    public float bedResponseTimeout;
    @JsonProperty("height_warning_threshold")
    public int heightWarningThreshold;
    @JsonProperty("wifi_setting_timeout")
    public int wifiSettingTimeout;
    @JsonProperty("wifi_status_polling_interval")
    public int wifiStatusPollingInterval;
    @JsonProperty("mattress_operation_timeout")
    public float mattressOperationTimeout;
    @JsonProperty("mattress_dehumidifier_time")
    public int mattressDehumidifierTime;
    @JsonProperty("mattress_operation_max_retry")
    public int mattressOperationMaxRetry;
    @JsonProperty("log_resend_interval")
    public int logResendInterval;
    @JsonProperty("bed_setting_max_retry")
    public int bedSettingMaxRetry;
    @JsonProperty("mattress_busy_check")
    public boolean mattressBusyCheck;
    @JsonProperty("ns_post_data_max_wait_duration")
    public int nsPostDataMaxWaitDuration;
    @JsonProperty("ns_post_data_max_wait_retry")
    public int nsPostDataMaxWaitRetry;
    @JsonProperty("ns_bed_same_result_timeout")
    public int nsBedSameResultTimeout;
    @JsonProperty("dummy_ble_active")
    public Boolean dummyBleActive;
    @JsonProperty("ns_connection_timeout")
    public int nsConnectionTimeout;
    @JsonProperty("reconnect_poll_max_duration")
    public int reconnectPollMaxDuration;
    @JsonProperty("reconnect_poll_interval")
    public int reconnectPollInterval;
    @JsonProperty("realtime_bell_interval")
    public int realtimBellInterval;
    @JsonProperty("realtime_fetch_interval")
    public float realtimeFetchInterval;
    @JsonProperty("ns_scan_time")
    public int nsScanTime;
    @JsonProperty("sleep_alarm_time")
    public String sleepAlarmTime;
    @JsonProperty("qs_sleep_time")
    public String qsSleepTime;
    @JsonProperty("wifi_auth_delay")
    public float setWifiAuthDelay;
    @JsonProperty("reconnect_count")
    public int reconnectCount=3;
    @JsonProperty("switch_fw_reconnect_time")
    public int switchFWReconnectTime=10;
    @JsonProperty("split_byte_timeout")
    public float splitByteTimeout=0.3f;


    public void copyValue(NemuriConstantsModel from) {
        this.nsUrlIp = from.nsUrlIp;
        this.nsUrl = from.nsUrl;
        this.commandInterval = from.commandInterval;
        this.operationTimeout = from.operationTimeout;
        this.statusPollingInterval = from.statusPollingInterval;
        this.upperBedThreshold = from.upperBedThreshold;
        this.lowerBedThreshold = from.lowerBedThreshold;
        this.heightWarningThreshold = from.heightWarningThreshold;
        this.bedResponseTimeout = from.bedResponseTimeout;
        this.wifiSettingTimeout = from.wifiSettingTimeout;
        this.wifiStatusPollingInterval = from.wifiStatusPollingInterval;
        this.mattressOperationTimeout = from.mattressOperationTimeout;
        this.mattressDehumidifierTime = from.mattressDehumidifierTime;
        this.mattressOperationMaxRetry = from.mattressOperationMaxRetry;
        this.logResendInterval = from.logResendInterval;
        this.bedSettingMaxRetry = from.bedSettingMaxRetry;
        this.mattressBusyCheck = from.mattressBusyCheck;
        this.nsPostDataMaxWaitDuration = from.nsPostDataMaxWaitDuration;
        this.nsPostDataMaxWaitRetry = from.nsPostDataMaxWaitRetry;
        this.nsBedSameResultTimeout = from.nsBedSameResultTimeout;
        this.dummyBleActive = from.dummyBleActive;
        this.nsConnectionTimeout = from.nsConnectionTimeout;
        this.reconnectPollMaxDuration = from.reconnectPollMaxDuration;
        this.reconnectPollInterval = from.reconnectPollInterval;
        this.realtimBellInterval = from.realtimBellInterval;
        this.realtimeFetchInterval = from.realtimeFetchInterval;
        this.nsScanTime = from.nsScanTime;
        this.sleepAlarmTime = from.sleepAlarmTime;
        this.qsSleepTime = from.qsSleepTime;
        this.setWifiAuthDelay = from.setWifiAuthDelay;
        this.reconnectCount = from.reconnectCount;
        this.switchFWReconnectTime = from.switchFWReconnectTime;
        this.splitByteTimeout = from.splitByteTimeout;
    }

    public String getNsUrlIp() {
        return nsUrlIp ==null || nsUrlIp.isEmpty()?"27.86.1.164/as/api/v1/NemuriScan/polling": nsUrlIp;
    }

    public void setNsUrlIp(String nsUrlIp) {
        this.nsUrlIp = nsUrlIp;
    }

    public String getNsUrl() {
        return nsUrl;
    }

    public void setNsUrl(String nsUrl) {
        this.nsUrl = nsUrl;
    }

    public void insert() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.insert(this);
        realm.commitTransaction();
    }

    public static NemuriConstantsModel get() {
        // 高さ閾値の初期値はベッドタイプによって変化する
        NemuriScanModel nemuriScanModel = NemuriScanModel.get();
        int bedType = nemuriScanModel == null ? 0 : nemuriScanModel.getInfoType();
        int heightWarningThreshold = bedType == NSSpec.BED_MODEL.INTIME_COMFORT.ordinal() ? 28 : 31;
        Logger.d("高さ閾値 内部初期値 %d", heightWarningThreshold);

        Realm realm = Realm.getDefaultInstance();
        RealmQuery<NemuriConstantsModel> query = realm.where(NemuriConstantsModel.class);
        NemuriConstantsModel result = query.findFirst();
        if (result == null) {
            NemuriConstantsModel.clear();
            NemuriConstantsModel nemuriConstantsModel = new NemuriConstantsModel();
            nemuriConstantsModel.nsUrlIp = "27.86.1.164/as/api/v1/NemuriScan/polling";
            nemuriConstantsModel.nsUrl = "asanalyzer.paramount.co.jp/as/api/v1/NemuriScan/polling";
            nemuriConstantsModel.commandInterval = (float) 0.2;
            nemuriConstantsModel.operationTimeout = 2;
            nemuriConstantsModel.statusPollingInterval = 1;
            nemuriConstantsModel.upperBedThreshold = 2;
            nemuriConstantsModel.lowerBedThreshold = 2;
            nemuriConstantsModel.bedResponseTimeout = (float) 0.05;
            nemuriConstantsModel.heightWarningThreshold = heightWarningThreshold;
            nemuriConstantsModel.wifiSettingTimeout = 30;
            nemuriConstantsModel.wifiStatusPollingInterval = 2;
            nemuriConstantsModel.mattressOperationTimeout = (float) 0.25;
            nemuriConstantsModel.mattressDehumidifierTime = 120;
            nemuriConstantsModel.mattressOperationMaxRetry = 2;
            nemuriConstantsModel.logResendInterval = 3;
            nemuriConstantsModel.bedSettingMaxRetry = 3;
            nemuriConstantsModel.mattressBusyCheck = false;
            nemuriConstantsModel.nsPostDataMaxWaitDuration = 5;
            nemuriConstantsModel.nsPostDataMaxWaitRetry = 2;
            nemuriConstantsModel.nsBedSameResultTimeout = 1;
            nemuriConstantsModel.dummyBleActive = false;
            nemuriConstantsModel.nsConnectionTimeout = 30;
            nemuriConstantsModel.reconnectPollMaxDuration = 20;
            nemuriConstantsModel.reconnectPollInterval = 5;
            nemuriConstantsModel.realtimBellInterval = 2;
            nemuriConstantsModel.realtimeFetchInterval = 0.1f;
            nemuriConstantsModel.nsScanTime = 3;
            nemuriConstantsModel.sleepAlarmTime = "18:00";
            nemuriConstantsModel.qsSleepTime = "05:00";
            nemuriConstantsModel.setWifiAuthDelay = 0.3f;
            nemuriConstantsModel.reconnectCount = 3;
            nemuriConstantsModel.switchFWReconnectTime = 10;
            nemuriConstantsModel.splitByteTimeout = 1f;
            nemuriConstantsModel.insert();
            result = nemuriConstantsModel;

        }
        return result;
    }

    public static void clear() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(NemuriConstantsModel.class);
        realm.commitTransaction();
    }

    public static ArrayList<NemuriConstantsModel> getAll() {
        ArrayList<NemuriConstantsModel> list = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<NemuriConstantsModel> query = realm.where(NemuriConstantsModel.class);
        list.addAll(realm.copyFromRealm(query.findAll()));
        return list;
    }
    public NemuriConstantsModel getUnmanaged() {
        if(!isManaged()){
            return this;
        }
        Realm realm = Realm.getDefaultInstance();
        NemuriConstantsModel unmanagedObject =  realm.copyFromRealm(this);
        realm.close();
        return unmanagedObject;
    }
}
