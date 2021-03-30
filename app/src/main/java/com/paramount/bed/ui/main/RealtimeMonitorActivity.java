package com.paramount.bed.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BaseActivity;

public class RealtimeMonitorActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle(LanguageProvider.getLanguage("UI000550C002"));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_realtime_monitor;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }
}
