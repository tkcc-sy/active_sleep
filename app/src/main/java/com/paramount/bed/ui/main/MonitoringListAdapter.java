package com.paramount.bed.ui.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.bigkoo.svprogresshud.SVProgressHUD;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MonitoringModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.TimerUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.paramount.bed.util.LogUtil.Logx;

public class MonitoringListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MonitoringUserRowListener mListeneer;
    private Context context;
    private List<MonitoringModel> data;
    OptionsPickerView statusPicker, statusPickerSleep;
    ArrayList<String> statusOptionsWhenActive, statusOptionsWhenInitial, statusOptionsWhenPending;
    Activity activity;
    private MonitoringModel monitoringSelected;
    private MonitoringViewHolder holderSelected;
    Boolean isButtonPress = false;
    private boolean isSpeakButtonLongPressed = false;
    ArrayList<String> statusOptionsTimer;
    ArrayList<Integer> listTimeSleep;
    ArrayList<String> listTimeSleepLabels;
    private int selectedTimeSleep;
    TimerUtils timerUtils;

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public class SleepResetViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView tvTitleSetting;
        TextView tvStatusTimer;
        ImageView imgArrowTimer;

        public SleepResetViewHolder(View view) {
            super(view);
            this.view = view;
            tvTitleSetting = view.findViewById(R.id.tvTitleSetting);
            tvStatusTimer = view.findViewById(R.id.tvStatusTimer);
            imgArrowTimer = view.findViewById(R.id.imgArrowTimer);
        }
    }

    public class MonitoringViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView tvNickName, tvStatus;
        public ImageView imgArrow;
        public LinearLayout layoutAction;
        public View divider0;

        public MonitoringViewHolder(View view) {
            super(view);
            tvNickName = view.findViewById(R.id.tvNickName);
            divider0 = view.findViewById(R.id.divider0);
            tvStatus = view.findViewById(R.id.tvStatus);
            imgArrow = view.findViewById(R.id.imgArrow);
            layoutAction = view.findViewById(R.id.layoutAction);
            this.view = view;
        }
    }

    public MonitoringListAdapter(Activity activity, Context context, List<MonitoringModel> data, MonitoringUserRowListener listener) {
        this.activity = activity;
        this.context = context;
        this.data = data;
        this.mListeneer = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM){
            View v1 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_monitoring_list, parent, false);
            MonitoringViewHolder vh1 = new MonitoringViewHolder(v1);

            statusOptionsWhenActive = new ArrayList<String>();
            statusOptionsWhenActive.add(LanguageProvider.getLanguage("UI000750C005"));
            statusOptionsWhenActive.add(LanguageProvider.getLanguage("UI000751C003"));

            statusOptionsWhenInitial = new ArrayList<String>();
            statusOptionsWhenInitial.add(LanguageProvider.getLanguage("UI000751C001"));
            statusOptionsWhenInitial.add(LanguageProvider.getLanguage("UI000750C005"));
            statusOptionsWhenInitial.add(LanguageProvider.getLanguage("UI000751C004"));

            statusOptionsWhenPending = new ArrayList<String>();
            statusOptionsWhenPending.add(LanguageProvider.getLanguage("UI000750C005"));
            statusOptionsWhenPending.add(LanguageProvider.getLanguage("UI000751C004"));
            return vh1;
        }
        else if(viewType == TYPE_FOOTER){
            View v2 = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_setting_time_sleep, parent, false);
            SleepResetViewHolder vh2 = new SleepResetViewHolder(v2);
            vh2.tvTitleSetting.setText(LanguageProvider.getLanguage("UI000750C013"));
            vh2.tvStatusTimer.setText(LanguageProvider.getLanguage("UI000750C014"));
            initOptionsTimer();
            vh2.imgArrowTimer.setOnClickListener(onStatusSleepClick());
            vh2.tvStatusTimer.setOnClickListener(onStatusSleepClick());
            return vh2;
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return data.size()+1;
//        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionSleepResetSetting(position)){
            return TYPE_FOOTER;
        }

        return TYPE_ITEM;
    }

    private boolean isPositionSleepResetSetting (int position) {
        return position == data.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof SleepResetViewHolder){
            SleepResetViewHolder holder = (SleepResetViewHolder)viewHolder;
            timerUtils = new TimerUtils(context);
            initOptionsPicker(holder);
            if (!NetworkUtil.isNetworkConnected(activity)) {
                holder.tvStatusTimer.setEnabled(false);
                holder.imgArrowTimer.setEnabled(false);
            }
        } else
            if(viewHolder instanceof MonitoringViewHolder){
            MonitoringViewHolder holder = (MonitoringViewHolder)viewHolder;
            final MonitoringModel monitoring = data.get(position);
            Logx("LINE", monitoring.getNick_name() + "->" + monitoring.getStatus() + "->" + String.valueOf(position));
            String optimizeNick = monitoring.getNick_name() != null ? monitoring.getNick_name() : "";
            holder.tvNickName.setText(data.get(position).getNick_name());
            if (position == 0) {
                holder.divider0.setVisibility(View.GONE);
            } else {
                holder.divider0.setVisibility(View.VISIBLE);
            }

            holder.tvNickName.setOnLongClickListener((view -> {
                holder.layoutAction.setVisibility(View.GONE);
                isSpeakButtonLongPressed = true;
                return true;
            }));
            holder.tvNickName.setOnTouchListener(((pView, pEvent) -> {
                pView.onTouchEvent(pEvent);
                if (pEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (isSpeakButtonLongPressed) {
                        holder.layoutAction.setVisibility(View.VISIBLE);
                        isSpeakButtonLongPressed = false;
                    }
                }
                return false;
            }));
            ArrayList<MonitoringModel> monitoringModels = MonitoringModel.getAll();
            holder.tvStatus.setText(getTextStatus(Integer.toString(1)));
            for (MonitoringModel data : monitoringModels) {
                Logx("LINE FOR", data.getNick_name() + "->" + data.getStatus() + "->");
                if (data.getId() == monitoring.getId()) {
                    holder.tvStatus.setText(getTextStatus(Integer.toString(data.getStatus())));
                    if (data.getStatus() == 2 || data.getStatus() == 3) {
                        holder.tvStatus.setTextColor(Color.RED);
                    } else if (data.getStatus() == 0) {
                        holder.tvStatus.setTextColor(Color.parseColor("#89B1B9"));
                    } else {
                        holder.tvStatus.setTextColor(Color.parseColor("#055B6D"));
                    }
                }
            }
            holder.tvStatus.setOnClickListener(onStatusClick(holder, monitoring));
            holder.imgArrow.setOnClickListener(onStatusClick(holder, monitoring));

            statusPicker = new OptionsPickerBuilder(context, onStatusSelect(holder))
                    .setBackgroundId(0)
                    .setCyclic(false, false, false)
                    .setCancelText(LanguageProvider.getLanguage("UI000750C008"))
                    .setSubmitText(LanguageProvider.getLanguage("UI000750C009"))
                    .build();
            if (!NetworkUtil.isNetworkConnected(activity)) {
                holder.tvStatus.setEnabled(false);
                holder.imgArrow.setEnabled(false);
            }
        }
    }

    private void initOptionsPicker(SleepResetViewHolder holder) {
        selectedTimeSleep = SettingModel.getSetting().getSleep_reset_timing();

        if(selectedTimeSleep==0){
            holder.tvStatusTimer.setText(LanguageProvider.getLanguage("UI000750C015"));
        }else {
            holder.tvStatusTimer.setText(selectedTimeSleep+LanguageProvider.getLanguage("UI000750C016"));
        }

        int selectedIndex = listTimeSleep.indexOf(selectedTimeSleep);
        if (selectedIndex < 0) {
            selectedIndex = 0;
        }

        statusPickerSleep = new OptionsPickerBuilder(context, onStatusSleepSelect(holder))
                .setBackgroundId(0)
                .setCyclic(false, false, false)
                .setSelectOptions(selectedIndex)
                .setCancelText(LanguageProvider.getLanguage("UI000750C019"))
                .setSubmitText(LanguageProvider.getLanguage("UI000750C018"))
                .build();
    }

    private OnOptionsSelectListener onStatusSleepSelect(SleepResetViewHolder holder) {
        return ((options1, options2, options3, v) -> {
            if (!NetworkUtil.isNetworkConnected(context)) {
                ((SettingActivity)context).setOfflineMode();
                return;
            }
            ///options1 -> index dari status yang terpilih
            selectedTimeSleep = listTimeSleep.get(options1);

            if(selectedTimeSleep==0){
                holder.tvStatusTimer.setText(LanguageProvider.getLanguage("UI000750C015"));
            }else {
                holder.tvStatusTimer.setText(selectedTimeSleep+LanguageProvider.getLanguage("UI000750C016"));
            }
            timerUtils.setTimer(selectedTimeSleep);

            //set timer
            ((SettingActivity)context).saveSetting("timer_setting", String.valueOf(selectedTimeSleep));
            ((SettingActivity)context).saveSetting("sleep_reset_timing", String.valueOf(selectedTimeSleep));

            LogUserAction.sendNewLog(((SettingActivity)context).userService, "STOP_SLEEP_SETTING", "", "", "UI000507");
        });
    }

    private OnOptionsSelectListener onStatusSelect(final MonitoringViewHolder holder) {
        return ((options1, options2, options3, v) -> {
            ArrayList<String> statusOptions = new ArrayList<>();
            if (monitoringSelected.getStatus() == 0) {
                statusOptions = statusOptionsWhenInitial;
                if (options1 == 1) {
                    updateUserMonitoringReq(holderSelected, monitoringSelected.getId(), statusOptions.get(options1));
                } else if (options1 == 2) {
                    updateUserMonitoringReq(holderSelected, monitoringSelected.getId(), statusOptions.get(options1));
                }
            } else if (monitoringSelected.getStatus() == 1) {
                statusOptions = statusOptionsWhenActive;
                if (options1 == 1) {
                    updateUserMonitoringReq(holderSelected, monitoringSelected.getId(), statusOptions.get(options1));
                }
            } else if (monitoringSelected.getStatus() == 3) {
                statusOptions = statusOptionsWhenPending;
                if (options1 == 0) {
                    updateUserMonitoringReq(holderSelected, monitoringSelected.getId(), statusOptions.get(options1));
                }
            } else if (monitoringSelected.getStatus() == 2) {
                statusOptions = statusOptionsWhenActive;
                if (options1 == 0) {
                    updateUserMonitoringReq(holderSelected, monitoringSelected.getId(), statusOptions.get(options1));
                }
            }
            Logx("MONITORING_USER_STATUS:monitoringSelected.getStatus()", String.valueOf(monitoringSelected.getStatus()));
            Logx("MONITORING_USER_STATUS:options1", String.valueOf(options1) + " -> " + statusOptions.get(options1));

        });
    }

    private View.OnClickListener onStatusClick(MonitoringViewHolder holder, MonitoringModel monitoring) {
        return (view -> {
            monitoringSelected = monitoring;
            holderSelected = holder;
            String tag = getTag(String.valueOf(monitoringSelected.getStatus()));
            if (monitoringSelected.getStatus() == 0) {
                statusPicker.setPicker(statusOptionsWhenInitial);
                statusPicker.setSelectOptions(0);
            } else if (monitoringSelected.getStatus() == 1) {
                statusPicker.setPicker(statusOptionsWhenActive);
                statusPicker.setSelectOptions(statusOptionsWhenActive.indexOf(LanguageProvider.getLanguage(tag)));
            } else if (monitoringSelected.getStatus() == 3) {
                statusPicker.setPicker(statusOptionsWhenPending);
                statusPicker.setSelectOptions(statusOptionsWhenPending.indexOf(LanguageProvider.getLanguage(tag)));
            } else if (monitoringSelected.getStatus() == 2) {
                statusPicker.setPicker(statusOptionsWhenActive);
                statusPicker.setSelectOptions(statusOptionsWhenActive.indexOf(LanguageProvider.getLanguage(tag)));
            }
            statusPicker.show();
        });
    }

    private View.OnClickListener onStatusSleepClick() {
        return (view -> {
            int selectedIndex = listTimeSleep.indexOf(selectedTimeSleep);
            if (selectedIndex < 0) {
                selectedIndex = 0;
            }
            statusPickerSleep.setPicker(listTimeSleepLabels);
            statusPickerSleep.setSelectOptions(selectedIndex);
            statusPickerSleep.show();
        });
    }

    private void initOptionsTimer() {
        int min = 10;
        int max = 60;
        int interval = 10;

        Integer[] listTimeSleepSetting =  FormPolicyModel.getPolicy().getTimeSleepSettingPrimitives();
        listTimeSleep = new ArrayList<>(Arrays.asList(listTimeSleepSetting));
        if(listTimeSleep.isEmpty()){
            for (int i = min; i<=max; i+=interval){
                listTimeSleep.add(i);
            }
        }

        listTimeSleepLabels = new ArrayList<>();
        for (Integer time:listTimeSleep) {
            if(time==0){
                listTimeSleepLabels.add(LanguageProvider.getLanguage("UI000750C015"));
            }else {
                listTimeSleepLabels.add(time+LanguageProvider.getLanguage("UI000750C016"));
            }
        }
    }

    Disposable mDisposableMonitoring;
    UserService settingService;

    private void updateUserMonitoringReq(MonitoringViewHolder holder, Integer monitorerd_user_id, String status) {
        if (!isButtonPress) {
            isButtonPress = true;
            showLoading();
            settingService = ApiClient.getClient(context).create(UserService.class);
            mDisposableMonitoring =
                    settingService.changeStatusMonitoring(Integer.toString(monitorerd_user_id), Integer.toString(UserLogin.getUserLogin().getId()), getIntStatus(status), 1)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                                public void onSuccess(BaseResponse<String> response) {
                                    hideLoading();
                                    isButtonPress = false;
                                    if (response.isSucces()) {
                                        MonitoringModel monitoringModel = new MonitoringModel();
                                        monitoringModel.setId(monitorerd_user_id);
                                        String valIntStatus = getIntStatus(status);
                                        monitoringModel.setStatus(Integer.parseInt(valIntStatus));
                                        monitoringModel.update();
                                        if (Integer.parseInt(valIntStatus) == 2 || Integer.parseInt(valIntStatus) == 3) {
                                            holder.tvStatus.setTextColor(Color.RED);
                                            holder.tvStatus.setText(status);
                                        } else if (Integer.parseInt(valIntStatus) == 0) {
                                            holder.tvStatus.setTextColor(Color.parseColor("#89B1B9"));
                                        } else {
                                            holder.tvStatus.setTextColor(Color.parseColor("#055B6D"));
                                            holder.tvStatus.setText(status);
                                        }
                                        monitoringSelected.setStatus(Integer.parseInt(valIntStatus));
                                    } else {
                                        DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(response.getMessage()));
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Timber.d("abx load content 2");
                                    hideLoading();
                                    isButtonPress = false;
                                    if (!NetworkUtil.isNetworkConnected(activity)) {
                                        DialogUtil.offlineDialog(activity, activity);
                                    } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                                        DialogUtil.tokenExpireDialog(activity);
                                    } else {
                                        DialogUtil.serverFailed(activity, "UI000802C069", "UI000802C070", "UI000802C071", "UI000802C072");
                                    }
                                }
                            });
        }
    }

    public String getTextStatus(String numberStatus) {
        String statusText = "";
        switch (numberStatus) {
            case "0":
                statusText = LanguageProvider.getLanguage("UI000751C001"); //pending
                break;
            case "1":
                statusText = LanguageProvider.getLanguage("UI000750C005"); //active
                break;
            case "2":
                statusText = LanguageProvider.getLanguage("UI000751C003"); //reject
                break;
            case "3":
                statusText = LanguageProvider.getLanguage("UI000751C004"); //ignore
                break;
            default:
                statusText = LanguageProvider.getLanguage("UI000751C001"); //pending
                break;
        }
        return statusText;
    }

    public String getTag(String numberStatus) {
        Log.d("gempi", numberStatus);
        String statusText = "";
        switch (numberStatus) {
            case "0":
                statusText = "UI000750C005"; //pending
                break;
            case "1":
                statusText = "UI000750C005"; //active
                break;
            case "2":
                statusText = "UI000751C003"; //reject
                break;
            case "3":
                statusText = "UI000751C004"; //ignore
                break;
            default:
                statusText = "UI000750C005";
                break;
        }
        return statusText;
    }

    public String getIntStatus(String numberStatus) {
        String statusInt = "";
        if (numberStatus.equals(LanguageProvider.getLanguage("UI000751C001"))) {
            statusInt = "0";
        } else if (numberStatus.equals(LanguageProvider.getLanguage("UI000750C005"))) {
            statusInt = "1";
        } else if (numberStatus.equals(LanguageProvider.getLanguage("UI000751C003"))) {
            statusInt = "2";
        } else if (numberStatus.equals(LanguageProvider.getLanguage("UI000751C004"))) {
            statusInt = "3";
        } else {
            statusInt = "0";
        }
        return statusInt;
    }

    static SVProgressHUD progressDialog = null;

    public void showLoading() {
        progressDialog = new SVProgressHUD(context);
        progressDialog.show();
        BaseActivity.isLoading = true;
    }

    public void hideLoading() {
        if (progressDialog != null) progressDialog.dismissImmediately();
        BaseActivity.isLoading = false;
    }

    public interface MonitoringUserRowListener {
        void onMonitoringUserRowEdit(MonitoringModel selectedUser);
    }
}
