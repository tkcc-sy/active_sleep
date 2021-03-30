package com.paramount.bed.ui.login;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.IntentConstant;
import com.paramount.bed.util.ProgressDrawable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ForgotIDResetActivity extends BaseActivity {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.btnReturn)
    Button btnReturn;

    private String iDataEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000498C001"));

        applyIntentData(getIntent());
        applyUI();
        applyUIListener();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_forgot_id_reset;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    private void applyIntentData(Intent intent) {
        iDataEmail = intent.getStringExtra(IntentConstant.EMAIL);
    }

    private void applyProgressBar() {
        int progressBarSegment = 3;
        int currentSegment = 3;
        int fgColor = Color.parseColor("#00c2d9");
        int bgColor = Color.parseColor("#cfdee7");
        Drawable d = new ProgressDrawable(fgColor, bgColor, progressBarSegment);
        progressBar.setProgressDrawable(d);
        progressBar.setProgress(1000 * currentSegment / progressBarSegment);
    }

    private void applyUI() {
        applyProgressBar();
        etEmail.setText(iDataEmail);
    }

    private void applyUIListener() {
        btnReturn.setOnClickListener((v) -> sendIntentData());
    }

    void sendIntentData() {
        Intent intent = new Intent(this, LoginEmailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}