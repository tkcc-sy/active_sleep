package com.paramount.bed.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.paramount.bed.R;
import com.paramount.bed.data.model.SenderBirdieModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.HomeService;
import com.paramount.bed.data.remote.service.InquiryService;
import com.paramount.bed.data.remote.service.NemuriScanService;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.login.LoginEmailActivity;
import com.paramount.bed.ui.main.SettingActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.RxUtil;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseActivity extends BaseCompatibilityScreenActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();
    public static final int STATUS_BAR_LIGHT = 1111;
    public static final int STATUS_BAR_DARK = 0000;
    public static final int STATUS_BAR_SNORE = 2222;
    public final static boolean IS_DEVELOPMENT = false;

    private String toolbarTitleText;
    public Disposable mDisposable;
    public HomeService homeService;
    public NemuriScanService nemuriScanService;
    public UserService userService;
    public InquiryService inquiryService;
    TextView toolbarTitle;
    public static Boolean isLoading = false;
    public Boolean isSettingActivity = false;
    public Boolean isLauchActivity = false;
    protected boolean shouldHideBackButton = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (isLauchActivity && !isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        setContentView(getLayoutResourceId());

        userService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        homeService = ApiClient.getClient(getApplicationContext()).create(HomeService.class);
        nemuriScanService = ApiClient.getClient(getApplicationContext()).create(NemuriScanService.class);
        inquiryService = ApiClient.getClient(getApplicationContext()).create(InquiryService.class);

        if (getStatusBarTheme() == STATUS_BAR_LIGHT) {
            setLightStatusBar(this);
        } else if(getStatusBarTheme() == STATUS_BAR_SNORE){
            setSnoreStatusBar(this);
        } else {
            setDarkStatusBar(this);
        }

        if (useToolbar()) {
            toolbarTitle = findViewById(R.id.toolbarTitle);
            ImageView btnBack = findViewById(R.id.btnBack);

            btnBack.setOnClickListener((v) -> {
                IOSDialogRight.Builder.isDialogVisible = false;
                isLoading = false;
                onBackPressed();
            });

            btnBack.setVisibility(shouldHideBackButton ? View.GONE : View.VISIBLE);
        }
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        applyLocalization();
        if(isLauchActivity){
            onLaunchActivityPassedFilter();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxUtil.dispose(mDisposable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("onNotif")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    protected boolean allowAlertNotif = true;
    protected BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("onNotif") && allowAlertNotif) {
                String clickAction = intent.getExtras().getString("click_action");
//                String title = LanguageProvider.getLanguage("UI000802C010");
//                String msg = LanguageProvider.getLanguage("UI000802C011");
//                showNotifAlert(title, msg);

                Boolean isApprovalReminder = clickAction == null ? false : clickAction.equals("APPROVAL_REMINDER");
                Boolean isMonitoringRequest = clickAction == null ? false : clickAction.equals("MONITORING_REQUEST");
                Boolean isBirdie = clickAction == null ? false : clickAction.equals("BIRDIE_BUTTON");
                Boolean isSleepAlarm = clickAction == null ? false : clickAction.equals("SLEEP_ALARM");

                if (isApprovalReminder) {
                    if (UserLogin.isLogin()) {
                        String title = LanguageProvider.getLanguage("UI000802C010");
                        String msg = LanguageProvider.getLanguage("UI000802C011");
                        showNotifAlert(title, msg);
                    } else {
                        Intent i = new Intent(BaseActivity.this, LoginEmailActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        finish();
                    }
                } else if (isMonitoringRequest) {
                    if (UserLogin.isLogin()) {
                        String title = LanguageProvider.getLanguage("UI000802C010");
                        String msg = LanguageProvider.getLanguage("UI000802C011");
                        showNotifAlert(title, msg);
                    } else {
                        Intent i = new Intent(BaseActivity.this, LoginEmailActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        finish();
                    }
                } else if (isBirdie) {
                    if (UserLogin.isLogin()) {
                        String sender = intent.getExtras().getString("sender");
                        if (sender != null && !sender.isEmpty()) {
                            SenderBirdieModel.updateByName(sender);
                        }
                        showNotifBirdie(sender);
                    } else {
                        Intent i = new Intent(BaseActivity.this, LoginEmailActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        finish();
                    }
                } else if (isSleepAlarm) {
                    if (UserLogin.isLogin()) {
//                        Intent alarmpopup = new Intent(BaseActivity.this, AlarmsPopup.class);
//                        alarmpopup.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        startActivity(alarmpopup);
                    } else {
                        Intent i = new Intent(BaseActivity.this, LoginEmailActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                        finish();
                    }
                }

            }
        }
    };

    protected void showNotifAlert(String title, String msg) {
        allowAlertNotif = false;
//                awaitingDriverLng.setText(intent.getExtras().getDouble("lng"));

        if (isSettingActivity) {
            DialogUtil.createSimpleOkDialog(BaseActivity.this, "", LanguageProvider.getLanguage("UI000750C006"), LanguageProvider.getLanguage("UI000750C007"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    allowAlertNotif = true;
                    SettingActivity.mInstance.getMonitoring();
                    dialogInterface.dismiss();
                }
            });
        } else {
            //overide from LanguageProfiver
            DialogUtil.createCustomYesNo(BaseActivity.this, "", msg, LanguageProvider.getLanguage("UI000802C012"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    allowAlertNotif = true;
                    dialogInterface.dismiss();
                    Intent intent = new Intent(BaseActivity.this, SettingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
            }, LanguageProvider.getLanguage("UI000802C013"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    allowAlertNotif = true;
                    dialogInterface.dismiss();
                }
            });
        }
    }

    protected abstract int getLayoutResourceId();

    protected int getStatusBarTheme() {
        return STATUS_BAR_LIGHT;
    }

    private static void setLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(Color.WHITE);
            activity.getWindow().setNavigationBarColor(Color.WHITE);
        }
    }

    private static void setDarkStatusBar(Activity activity) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            winParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            winParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            activity.getWindow().setStatusBarColor(activity.getColor(R.color.status_bar_trans));
        }

        win.setAttributes(winParams);

    }

    private void setSnoreStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;

            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity.getApplicationContext(),R.color.snore_status_bar));
            activity.getWindow().setNavigationBarColor(Color.WHITE);
        }
    }

    protected boolean useToolbar() {
        return false;
    }

    protected void setToolbarTitle(String title) {
        if (useToolbar()) {
            toolbarTitle.setText(title);
        }
    }

    public static SVProgressHUD progressDialog = null;

    public void showLoading() {
        isLoading = true;
        progressDialog = new SVProgressHUD(BaseActivity.this);
        progressDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                isLoading = false;
            }
        });
        progressDialog.show();
    }

    public void showLoadingIfNotShown() {
        if(!isLoading) {
            isLoading = true;
            progressDialog = new SVProgressHUD(BaseActivity.this);
            progressDialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(SVProgressHUD hud) {
                    isLoading = false;
                }
            });
            progressDialog.show();
        }
    }

    public void hideLoading() {
        isLoading = false;
        if (progressDialog != null) progressDialog.dismissImmediately();
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void applyLocalization() {
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) (findViewById(android.R.id.content))).getChildAt(0);
        ArrayList<View> allChild = getAllChildren(viewGroup);
        for (View child : allChild
        ) {
            if (child instanceof ToggleButton) {
                String textOn = ((ToggleButton) child).getTextOn().toString();
                String textOff = ((ToggleButton) child).getTextOff().toString();
                String content = LanguageProvider.getLanguage(textOn);
                ((ToggleButton) child).setText(content);
                if (content != null && content != "")
                    ((ToggleButton) child).setTextOn(content);
                content = LanguageProvider.getLanguage(textOff);
                if (content != null && content != "")
                    ((ToggleButton) child).setTextOff(content);
            } else if (child instanceof EditText) {
                String tag = ((EditText) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((EditText) child).setText(content);
                if (((EditText) child).getHint() != null) {
                    tag = ((EditText) child).getHint().toString();
                    content = LanguageProvider.getLanguage(tag);
                    if (content != null && content != "")
                        ((EditText) child).setHint(content);
                }
            } else if (child instanceof CheckBox) {
                String tag = ((CheckBox) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((CheckBox) child).setText(content);
            } else if (child instanceof TextView) {
                String tag = ((TextView) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((TextView) child).setText(content);
            } else if (child instanceof Button) {
                String tag = ((Button) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((Button) child).setText(content);
            }
        }
    }

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    public void showNotifBirdie(String sender) {

    }

    public void getfcmtoken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        String token = task.getResult().getToken();
                        Log.d(TAG, "Refreshed token: " + token);
                        userService.fcmUpdateToServer(UserLogin.getUserLogin().getId(), token, 1)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                                    @Override
                                    public void onSuccess(BaseResponse<String> stringBaseResponse) {
                                        Log.d(TAG, "onsuccess: " + stringBaseResponse.getMessage());
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.d(TAG, "onerror: " + e.getMessage());
                                    }
                                });
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (!isLoading) {
            super.onBackPressed();
        }
    }

    public Dialog progressDialogs;

    public void showProgress() {
        runOnUiThread(() -> {
            if (!this.isFinishing()) {
                if (progressDialogs == null || !progressDialogs.isShowing()) {
                    progressDialogs = new Dialog(this);
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View iview = inflater.inflate(R.layout.ios_dialog_suv, null);
                    progressDialogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    progressDialogs.setContentView(iview);
                    progressDialogs.setCancelable(false);
                    progressDialogs.show();
                }
            }
        });
    }

    public void hideProgress() {
        runOnUiThread(() -> {
            if (!this.isFinishing()) {
                if (progressDialogs != null && progressDialogs.isShowing()) {
                    progressDialogs.dismiss();
                }
            }
        });
    }

    protected void onLaunchActivityPassedFilter(){
        //meant to be overriden by launch activity
    }
}

