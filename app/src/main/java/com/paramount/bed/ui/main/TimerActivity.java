package com.paramount.bed.ui.main;

import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.work.WorkInfo;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.SettingProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NemuriScanUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.RetryWithDelay;
import com.paramount.bed.util.TimerUtils;
import com.paramount.bed.viewmodel.TimerViewModel;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TimerActivity extends BaseActivity implements NemuriScanUtil.NemuriScanDetailFetchListener{

    @BindView(R.id.tvTimer)
    TextView tvTimer;
    @BindView(R.id.btnStopTimer)
    LinearLayout btnStopTimer;

    private Handler handler;
    private static Runnable runnable, runnable2;
    private TimerViewModel mViewModel;
    private TimerUtils timerUtils;
    private long totalManual;
    private NemuriScanModel nemuriScanDetail = new NemuriScanModel();
    private SettingProvider settingProvider;
    private static SVProgressHUD progressDialog2;

    public static final String STOP_TIMER_ACTION = "stop_timer_action";
    public static final String APPLY_NO_NS_UI = "apply_no_ns_ui";
    public static final String UPDATE_UI_STATE = "update_ui_state";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this).get(TimerViewModel.class);
        settingProvider = new SettingProvider(this);

        timerUtils = new TimerUtils(this);
        tvTimer.setText(TimerUtils.calculateTime(timerUtils.getDuration()));

        btnStopTimer.setOnClickListener(v -> {
            if (UserLogin.getUserLogin() != null) {
                try {
                    LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), "stop_sleep_start", "UI000506", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                if (UserLogin.getUserLogin() != null) {
                    try {
                        LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), "INTERNET_CONNECTION_FAILED", "UI000506", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                DialogUtil.createSimpleOkDialogLink(TimerActivity.this, "", LanguageProvider.getLanguage("UI000802C002"),
                        LanguageProvider.getLanguage("UI000610C043"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(TimerActivity.this, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000610C043");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        }, LanguageProvider.getLanguage("UI000610C031"), (dialogInterface, i) -> dialogInterface.dismiss());

                return;
            }

            NemuriScanModel nemuriScanModel = NemuriScanModel.get();
            if (nemuriScanModel == null) {
                if (UserLogin.getUserLogin() != null) {
                    try {
                        LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), "stop_sleep_failed", "UI000506", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                runOnUiThread(() -> {
                    EventBus.getDefault().post(new HomeActivity.TimerEvent(APPLY_NO_NS_UI));
                    SettingModel.resetNSRelatedSettings();
                });
                settingProvider.noNSSetting((isSuccess) -> runOnUiThread(() -> runOnUiThread(() -> EventBus.getDefault().post(new HomeActivity.TimerEvent(APPLY_NO_NS_UI)))));
                DialogUtil.createSimpleOkDialogLink(TimerActivity.this, "", LanguageProvider.getLanguage("UI000610C030"),
                        LanguageProvider.getLanguage("UI000610C043"), (dialogInterface, i) -> {
                            Intent faqIntent = new Intent(TimerActivity.this, FaqActivity.class);
                            faqIntent.putExtra("ID_FAQ", "UI000610C043");
                            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(faqIntent);
                            dialogInterface.dismiss();
                        }, LanguageProvider.getLanguage("UI000610C031"), (dialogInterface, i) -> dialogInterface.dismiss());
                return;
            }

            showProgress2();
            NemuriScanUtil.fetchSpec(TimerActivity.this, TimerActivity.this);

            DialogUtil.createCustomYesNo(this, null, "判定再開ボタンが押されました。判定再開をするとベッドにいる間に就床として判定されます。判定再開をしてよろしいでしょうか？", "キャンセル", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), "TERMINATE_STOP_SLEEP_CANCEL", "UI000506", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");
                    dialog.dismiss();
                }
            }, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopTimerAction();
                }
            });
        });

        runTimer();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.dialog_timer;
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvTimer.setText(TimerUtils.calculateTime(timerUtils.getLastDuration()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.zoom_in, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void runTimer() {
        handler = new Handler();
        runnable = () -> {
            long elapsed = (timerUtils.getTimestamp("END") - timerUtils.getTimestamp("START")) / 1000;
            long total = timerUtils.getDuration() - elapsed;
            if (total < 0) {
                handler.removeCallbacks(runnable);
            } else {
                timerUtils.setLastDuration(total);
                tvTimer.setText(TimerUtils.calculateTime(total));
                handler.postDelayed(runnable, 1000);
            }
        };

        if (mViewModel.getOutputWorkInfo().getValue() != null && !mViewModel.getOutputWorkInfo().getValue().isEmpty()) {
            if (mViewModel.getOutputWorkInfo().getValue().get(0).getState() != WorkInfo.State.RUNNING) {
                timerWithoutWorker();
            }

            handler.post(runnable);
        }

        if (mViewModel.getOutputWorkInfo().getValue() == null || mViewModel.getOutputWorkInfo().getValue().isEmpty()) {
            mViewModel.startTimer();
            timerUtils.setTimestamp("START", System.currentTimeMillis());
            timerUtils.setTimestamp("END", System.currentTimeMillis());
            handler.post(runnable);
        }
    }

    private void timerWithoutWorker() {
        long duration = timerUtils.getDuration();
        totalManual = timerUtils.getLastDuration();
        runnable2 = () -> {
            if (totalManual > 0) {
                if (mViewModel.getOutputWorkInfo().getValue() != null && !mViewModel.getOutputWorkInfo().getValue().isEmpty()) {
                    if (mViewModel.getOutputWorkInfo().getValue().get(0).getState() != WorkInfo.State.RUNNING) {
                        timerUtils.setTimestamp("END", System.currentTimeMillis());
                        long elapsed = (timerUtils.getTimestamp("END") - timerUtils.getTimestamp("START")) / 1000;
                        totalManual = duration - elapsed;
                        timerUtils.setLastDuration(totalManual);
                        handler.postDelayed(runnable2, 1000);
                    }
                }
            } else {
                mViewModel.deleteWork();
                tvTimer.setText(TimerUtils.calculateTime(timerUtils.getLastDuration()));
                handler.removeCallbacks(runnable);
            }
        };
        handler.post(runnable2);
    }

    private void stopTimerAction(){
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.sendSleepResetStop(UserLogin.getUserLogin().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(BuildConfig.MAX_RETRY, BuildConfig.REQUEST_TIME_OUT))
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<BaseResponse>() {
                    @Override
                    public void onNext(BaseResponse response) {
                        if(response.getSuccess()){
                            LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), "STOP_SLEEP_END", "UI000506", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");

                            mViewModel.stopTimer();
                            handler.removeCallbacks(runnable);
                            timerUtils.setTimestamp("END", System.currentTimeMillis());
                            finish();

                            EventBus.getDefault().post(new HomeActivity.TimerEvent(TimerActivity.STOP_TIMER_ACTION));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), "TERMINATE_STOP_SLEEP_FAILED", "UI000506", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber(), "UI000506");

                        if (!NetworkUtil.isNetworkConnected(getApplicationContext())) {
                            DialogUtil.offlineDialog(TimerActivity.this, getApplicationContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(TimerActivity.this);
                        } else {
                            DialogUtil.serverFailed(TimerActivity.this, "UI000802C045", "UI000802C046", "UI000802C047", "UI000802C048");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void showProgress2() {
        runOnUiThread(() -> {
            if (!this.isFinishing()) {
                if (progressDialog2 == null || !progressDialog2.isShowing()) {
                    progressDialog2 = new SVProgressHUD(TimerActivity.this);
                    progressDialog2.showWithMaskType(SVProgressHUD.SVProgressHUDMaskType.Clear);
                }
            }
        });
    }

    public void hideProgress2() {
        runOnUiThread(() -> {
            if (progressDialog2 != null && progressDialog2.isShowing()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog2 != null && progressDialog2.isShowing()) {
                            progressDialog2.dismissImmediately();
                        }
                    }
                }, 1500);
            }
        });
    }

    @Override
    public void onNemuriScanDetailFetched(NemuriScanModel nemuriScanDetailModel) {
        nemuriScanDetail = nemuriScanDetailModel;
        runOnUiThread(() -> {
            hideProgress2();
            if (!NemuriScanModel.getBedActive()) {
                DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000802C030"), LanguageProvider.getLanguage("UI000802C031"), null);
            }
        });
        EventBus.getDefault().post(new HomeActivity.TimerEvent(TimerActivity.UPDATE_UI_STATE));
    }

}
