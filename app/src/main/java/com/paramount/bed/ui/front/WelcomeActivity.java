package com.paramount.bed.ui.front;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.paramount.bed.R;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.IntentUtil;
import com.paramount.bed.util.LogUserAction;

public class WelcomeActivity extends BaseActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(WelcomeActivity.this, RegistrationStepActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mainIntent.putExtra("is_registration", true);
                String companyCode;
                try {
                    companyCode = getIntent().getStringExtra("companyCode");
                } catch (Exception e) {
                    companyCode = "";
                }
                int iCompanyID = getIntent().getIntExtra("companyId", 0);
//                Log.d("TAG", "run: " + companyCode);
                mainIntent.putExtra("companyCode", companyCode);
                mainIntent.putExtra("companyId", iCompanyID);
                mainIntent.putExtra(IntentUtil.User.IS_KICK_USER, getIntent().getBooleanExtra(IntentUtil.User.IS_KICK_USER, false));
                WelcomeActivity.this.startActivity(mainIntent);
                WelcomeActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
        if (UserLogin.getUserLogin() != null) {
            LogUserAction.InsertLog(userService, String.valueOf(UserLogin.getUserLogin().getId() == null ? 0 : UserLogin.getUserLogin().getId()), "open_screen", "UI000290", new AndroidSystemUtil().getDeviceType(), new AndroidSystemUtil().getOsVersion(), UserLogin.getUserLogin().getScanSerialNumber(),"UI000290");
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_welcome;
    }

}
