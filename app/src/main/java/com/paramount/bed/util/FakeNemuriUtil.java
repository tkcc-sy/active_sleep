package com.paramount.bed.util;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.NemuriScanCheckResponse;
import com.paramount.bed.data.remote.service.NemuriScanService;
import com.paramount.bed.ui.BaseCompatibilityScreenActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;

import static com.paramount.bed.ui.registration.step.BluetoothListFragment.isFromFakeNemuriUtil;

public class FakeNemuriUtil extends BaseCompatibilityScreenActivity {
    public static EditText etSerialNumber;
    public static TextView save;
    public static TextView close;
    public NemuriScanService nemuriScanService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_nemuri_util);
        nemuriScanService = ApiClient.getClient(getApplicationContext()).create(NemuriScanService.class);
        etSerialNumber = findViewById(R.id.etSerialNumber);
        save = findViewById(R.id.save);
        save.setEnabled(true);
        save.setOnClickListener((view -> {
            save.setEnabled(false);
            setResult(1);
            isFromFakeNemuriUtil = true;
            finish();
            RegistrationStepActivity.checkNemuri(etSerialNumber.getText().toString());
        }));
        close = findViewById(R.id.close);
        close.setOnClickListener((view -> {
            setResult(0);
            isFromFakeNemuriUtil = true;
            finish();
        }));
    }

    public void successCheck(NemuriScanCheckResponse response) {
        save.setEnabled(true);
        RegistrationStepActivity.SERIAL_NUMBER = "F" + etSerialNumber.getText().toString();
        RegistrationStepActivity.NEMURI_SCAN_CHECK = response;
        setResult(1);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(0);
        isFromFakeNemuriUtil = true;
        save.setEnabled(true);
    }
}
