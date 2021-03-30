package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;

import com.paramount.bed.R;
import com.paramount.bed.ble.NSManager;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.provider.FirmwareProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.step.BluetoothListFragment;
import com.paramount.bed.util.BluetoothUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.PermissionUtil;
import com.paramount.bed.util.SystemSettingUtil;
import com.paramount.bed.util.WebViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateFirmwareIntroActivity extends BaseActivity {
    public static int FINISH_RESULT_CODE = 155;

    @BindView(R.id.wvIntro)
    WebView wvIntro;

    @BindView(R.id.btnNext)
    Button btnNext;

    @OnClick(R.id.btnCancel)
    void goBack() {
        setResult(isManualUpdate ? 301 : 300);
        finish();
    }

    @OnClick(R.id.btnNext)
    void goNext() {
        showLocationPermissionDialogAlert();
    }

    @BindView(R.id.chkAgree)
    CheckBox chkAgree;

    @OnClick(R.id.chkAgree)
    public void onCheckAgree(){
        agreementCheckTapped();
    }

    @OnClick(R.id.lin_chk_aggree)
    public void onCheckContainerAgree(){
        agreementCheckTapped();
    }

    boolean statusChecked = true;
    boolean isManualUpdate = false;
    boolean isFreedNS = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        shouldHideBackButton = true;
        super.onCreate(savedInstanceState);
        setToolbarTitle(LanguageProvider.getLanguage("UI000731C001"));
        ButterKnife.bind(this);
        setupWebview();
        applyLocalization();
        isManualUpdate = getIntent().getBooleanExtra("isManualUpdate",false);
        isFreedNS = getIntent().getBooleanExtra("isFreedNS",false);
        btnNext.setEnabled(chkAgree.isChecked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnNext.setEnabled(chkAgree.isChecked());
    }
    
    private void agreementCheckTapped(){
        if (statusChecked) {
            chkAgree.setChecked(true);
            btnNext.setEnabled(true);
            statusChecked = false;
        } else {
            statusChecked = true;
            chkAgree.setChecked(false);
            btnNext.setEnabled(false);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebview(){
        WebSettings mWebSettings = wvIntro.getSettings();
        wvIntro.setScrollbarFadingEnabled(true);
        mWebSettings.setJavaScriptEnabled(true);
        wvIntro.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);

        wvIntro.setBackgroundColor(0);

        wvIntro.setOnLongClickListener(v -> true);
        wvIntro.setLongClickable(false);
        wvIntro.setBackgroundColor(Color.TRANSPARENT);
        WebViewUtil.fixWebViewFonts(wvIntro);
        showLoading();
        FirmwareProvider.getIntroContent(this, intro -> {
            hideLoading();
            wvIntro.loadData(intro.getContent(),"text/html", "UTF-8");
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_update_firmware_intro;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @Override
    public void onBackPressed() {
        //prevent back
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    public float getFontScale() {
        return SystemSettingUtil.getFontScale(getContentResolver(), getResources().getConfiguration());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == FINISH_RESULT_CODE){
            setResult(isManualUpdate || isFreedNS ? 301 : 300);
            finish();
        }
    }

    public void showLocationServiceDialogAlert() {
        hideProgress();
        if (NemuriScanModel.get() != null || BluetoothListFragment.selectedNemuriScan != null || UpdateFirmwareScanActivity.selectedNemuriScan != null) {
            PermissionUtil.showLocationServiceDialogAlert(UpdateFirmwareIntroActivity.this, new PermissionUtil.LocationServiceDialogueListener() {
                @Override
                public void onDisabled(DialogInterface dialogInterface) {
                }

                @Override
                public void onEnabled() {

                    if (!BluetoothUtil.isBluetoothEnable()) {
                        NSManager.getInstance(UpdateFirmwareIntroActivity.this,null).requestBluetoothEnable(UpdateFirmwareIntroActivity.this);
                    }else{
                        //check connection availability
                        if (NetworkUtil.isNetworkConnected(UpdateFirmwareIntroActivity.this)) {
                            proceedNavigation();
                        }else{
                            DialogUtil.createSimpleOkDialog(UpdateFirmwareIntroActivity.this, "", LanguageProvider.getLanguage("UI000731C005"), LanguageProvider.getLanguage("UI000731C006"), (dialog, which) -> dialog.dismiss());
                        }
                    }
                }
            });
        }
    }

    public void showLocationPermissionDialogAlert() {
        hideProgress();
        if (NemuriScanModel.get() != null || BluetoothListFragment.selectedNemuriScan != null || UpdateFirmwareScanActivity.selectedNemuriScan != null) {
            PermissionUtil.showLocationPermissionDialogAlert(UpdateFirmwareIntroActivity.this, new PermissionUtil.PermissionDialogueListener() {
                @Override
                public void onPermissionCanceled(DialogInterface dialogInterface) {
                }

                @Override
                public void onPermissionGranted() {
                    showLocationServiceDialogAlert();
                }
            });
        }
    }

    private void proceedNavigation(){
        Intent intent = new Intent(this, UpdateFirmwareActivity.class);
        startActivityForResult(intent, 101);
    }

}
