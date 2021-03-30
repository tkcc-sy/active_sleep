package com.paramount.bed.util;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class CloseUtil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            finishAffinity();
        }
    }
}
