package com.paramount.bed.ui.main;

import android.content.Intent;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.IOSDialogRight;
import com.paramount.bed.util.TokenExpiredReceiver;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class AutomaticOperationActivity extends BaseActivity {
    TokenExpiredReceiver tokenExpiredReceiver = new TokenExpiredReceiver();

    @OnClick(R.id.sleepContainer)
    void settingSleep() {
        Intent intent = new Intent(this, AutomaticSleepOperationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @OnClick(R.id.wakeContainer)
    void settingWake() {
        Intent intent = new Intent(this, AutomaticWakeOperationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        setToolbarTitle(LanguageProvider.getLanguage("UI000740C001"));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_automatic_operation;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        tokenExpiredReceiver = TokenExpiredReceiver.register(this, tokenExpiredReceiver);
        isLoading = false;
        IOSDialogRight.Builder.isDialogVisible = false;
        AlarmsQuizModule.run(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TokenExpiredReceiver.unregister(this, tokenExpiredReceiver);
        HomeActivity.drawerLayout.closeDrawer(GravityCompat.START, false);
    }

    @Override
    public void finish() {
        super.finish();
        if (getIntent().getBooleanExtra("FROM_DRIVER", false)) {
            overridePendingTransition(0, R.anim.godown);
            HomeActivity.activity.alarmOpen = false;
            HomeActivity.activity.dialogSettingAuto.setVisibility(View.GONE);
            HomeActivity.activity.btnHamburger.setEnabled(true);
            HomeActivity.activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}
