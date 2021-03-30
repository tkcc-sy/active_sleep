package com.paramount.bed.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.google.gson.Gson;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.ForestModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BaseCompatibilityScreenActivity;
import com.paramount.bed.util.ActivityUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ForestDialog extends BaseCompatibilityScreenActivity {
    @BindView(R.id.autoDialogShareContainer)
    LinearLayout autoDialogShareContainer;
    @BindView(R.id.tvHeader) TextView tvHeader;
    @BindView(R.id.tvSubHeader) TextView tvSubHeader;
    @BindView(R.id.tvScore) TextView tvScore;
    @BindView(R.id.tvLabelScore) TextView tvLabelScore;
    @BindView(R.id.headerDialogShare) TextView headerDialogShare;
    @BindView(R.id.descriptionDialogTimer) TextView descriptionDialogTimer;
    @BindView(R.id.labelCheckbox) TextView labelCheckbox;
    @BindView(R.id.btnHelp) Button btnHelp;
    @BindView(R.id.btnShare) Button btnShare;
    @BindView(R.id.btnCloseShare) ImageView btnCloseShare;
    @BindView(R.id.tvIvScore) ImageView ivScore;
    @BindView(R.id.chkDismiss)
    CheckBox chkDismiss;
    @BindView(R.id.layHeader)
    LinearLayout layHeader;
    @BindView(R.id.layFooter)
    LinearLayout layFooter;
    @BindView(R.id.tvDateHeader)
    TextView tvDateHeader;

    private static final int REQUEST_STORAGE_PERMISSION = 200;
    private static final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean storageGrated = false;
    private boolean statusChecked = false;
    UserService questionnareService;
    Disposable mDisposables;

    private static SVProgressHUD progressDialog = null;
    public static boolean fromMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_dialog_forrest);
        ButterKnife.bind(this);
        questionnareService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        fromMenu = getIntent().getExtras().getBoolean("from_menu",false);
        initForest();
        getForestScore();
    }

    private void initForest() {
        LogUserAction.sendNewLog(questionnareService, "FOREST_HOME_SHOW", "", "", "UI000507");
        Boolean forest_report_allowed = SettingModel.getSetting().getForest_report_allowed();
        if(forest_report_allowed){
            chkDismiss.setChecked(false);
            statusChecked = false;
        }else {
            chkDismiss.setChecked(true);
            statusChecked = true;
        }

        btnCloseShare.setOnClickListener(v -> {
            if(chkDismiss.isChecked()){
                saveSetting("forest_report_allowed", String.valueOf(false), 0);
            }else {
                saveSetting("forest_report_allowed", String.valueOf(true), 0);
            }
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        });

        btnHelp.setOnClickListener(v -> {
            Intent faqIntent = new Intent(this, FaqActivity.class);
            faqIntent.putExtra("ID_FAQ", "UI000507C011");
            faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(faqIntent);

        });

        headerDialogShare.setText(LanguageProvider.getLanguage("UI000507C004"));
        tvLabelScore.setText(LanguageProvider.getLanguage("UI000507C003"));
        btnShare.setText(LanguageProvider.getLanguage("UI000507C006"));
        labelCheckbox.setText(LanguageProvider.getLanguage("UI000507C007"));
        tvHeader.setText(LanguageProvider.getLanguage("UI000507C001"));
        tvSubHeader.setText(LanguageProvider.getLanguage("UI000507C002"));
        descriptionDialogTimer.setText(LanguageProvider.getLanguage("UI000507C005"));


        btnShare.setOnClickListener(v -> {
            if (storageGrated) {
                shareAction();
            }else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE_PERMISSION);
            }

        });

        chkDismiss.setOnClickListener((v) -> {
            if (!statusChecked) {
                chkDismiss.setChecked(false);
                statusChecked = false;
                DialogUtil.createCustomYesNo(this, "", LanguageProvider.getLanguage("UI000507C008"), LanguageProvider.getLanguage("UI000507C009"),
                        (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        },
                        LanguageProvider.getLanguage("UI000507C010"), (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            chkDismiss.setChecked(true);
                            statusChecked = true;
                        });
            } else {
                chkDismiss.setChecked(false);
                statusChecked = false;
            }
        });
    }


    private void shareAction(){
        if (NetworkUtil.isNetworkConnected(ForestDialog.this)) {
            LogUserAction.sendNewLog(questionnareService, "SNS_SHARE_SHOW", "", "", "UI000507");

            hideButtonShare();

            CountDownTimer ss = new CountDownTimer(200,10) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    View viewDialog = autoDialogShareContainer.findViewById(R.id.autoDialogShareContainer);
                    Bitmap b = ActivityUtil.takescreenshotOfRootView(viewDialog);
                    File file = ActivityUtil.storeScreenshot(b);
                    ActivityUtil.shareActionView(ForestDialog.this, file);

                    CountDownTimer cdt = new CountDownTimer(50, 10) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            showButtonShare();
                        }
                    };

                    cdt.start();
                }
            };

            ss.start();

        }else{
            LogUserAction.sendNewLog(questionnareService, "INTERNET_CONNECTION_FAILED", "", "", "UI000507");
            LogUserAction.sendNewLog(questionnareService, "SNS_SHARE_SHOW_SKIP", "", "", "UI000507");
            DialogUtil.createSimpleOkDialog(ForestDialog.this, "", LanguageProvider.getLanguage("UI000802C002"), LanguageProvider.getLanguage("UI000731C006"), (dialog, which) -> {
                dialog.dismiss();
            });
        }
    }

    private void hideButtonShare(){
        layHeader.setVisibility(View.GONE);
        layFooter.setVisibility(View.GONE);
    }

    private void showButtonShare(){
        layHeader.setVisibility(View.VISIBLE);
        layFooter.setVisibility(View.VISIBLE);
    }

    public class VMSaveSetting {
        String key;
        String value;
    }

    @SuppressLint("CheckResult")
    private void saveSetting(String key, String value, int retryCount) {
        SettingModel.saveSetting(key, value);
        ArrayList<VMSaveSetting> isave = new ArrayList<>();
        VMSaveSetting a = new VMSaveSetting();
        a.key = key;
        a.value = value;
        isave.add(a);
        Gson gs = new Gson();
        String t = gs.toJson(isave);
        UserService sService = ApiClient.getClient(getApplicationContext()).create(UserService.class);
        sService.saveSetting(UserLogin.getUserLogin().getId(), t, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse>() {
                    public void onSuccess(BaseResponse response) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(retryCount<BuildConfig.MAX_RETRY){
                            saveSetting(key,value,retryCount+1);
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(!checkAllGrant(grantResults)){
            LogUserAction.sendNewLog(questionnareService, "SNS_SHARE_FAILED", "PERMISSION_FAILED", "", "UI000507");
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000507C013"),
                    LanguageProvider.getLanguage("UI000507C014"), (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    });
        }else {
            storageGrated = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            shareAction();
        }

    }

    private boolean checkAllGrant(int[] grantResults){
        boolean grant = true;
        if(grantResults.length>0){
            for (int grantResult:grantResults) {
                if(grantResult!=PackageManager.PERMISSION_GRANTED){
                    grant = false;
                }
            }
        }else {
            grant = false;
        }

        return grant;
    }

    private void getForestScore() {
        ArrayList<ForestModel> forestModels = ForestModel.getAll();
        if(forestModels.size() > 0) {
            ForestModel forestModel = forestModels.get(0);
            if (forestModel != null) {
                if (forestModel.getScore() != null)
                    tvScore.setText(String.valueOf(forestModel.getScore()));

                if (forestModel.getImg() != null)
                    ivScore.setImageBitmap(convertBase64ToBitmap(forestModel.getImg()));


                String labelString = forestModel.getDate() == null ? LanguageProvider.getLanguage("UI000507C012") : forestModel.getDate();
                tvDateHeader.setText(labelString);

                ForestModel.clear();
            } else {
                setResult(1);//arbitrary value just to notify HomeActivity
                finish();
            }
        } else {
            setResult(1);//arbitrary value just to notify HomeActivity
            finish();
        }
    }

    private Bitmap convertBase64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.substring(b64.indexOf(",")  + 1).getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

}
