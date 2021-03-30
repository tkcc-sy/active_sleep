package com.paramount.bed.ui;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.paramount.bed.util.DisplayUtils;

import static com.paramount.bed.util.LogUtil.Logx;

public class BaseCompatibilityScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBaseContext) {
        super.attachBaseContext(newBaseContext);
        boolean isFixedFontSize = true;
        float normalFont = 1.0f;
        float bigFont = 1.3f;

        String activityName = this.getClass().getSimpleName();
        /* Ovveride settings like this if want implement Custome Scale Setting For Some Activity
        if (activityName.equals(RemoteActivity.class.getSimpleName())) {
            isFixedFontSize = true;
            normalFont = 1.0f;
            bigFont = 1.0f;
        }
        */
        DisplayUtils.applyScreenCompatibility(this, newBaseContext, isFixedFontSize, normalFont, bigFont);
        Logx("BaseCompatibilityScreenActivity",
                activityName + " -> normalFont : " + normalFont + " -> bigFont : " + bigFont
        );
    }
}
