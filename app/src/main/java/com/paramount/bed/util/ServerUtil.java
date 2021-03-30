package com.paramount.bed.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.ServerModel;
import com.paramount.bed.ui.BaseCompatibilityScreenActivity;

public class ServerUtil extends BaseCompatibilityScreenActivity {
    public EditText etHost;
    public TextView save, close, versionAPP;
    public final static String SERVER_WEBAPI = "https://dev1.infinitec.co.jp/webapi/index.php/api/v1/";
    public final static String SERVER_ASSDEBUG = "https://dev1.infinitec.co.jp/assdebug/index.php/api/v1/";
    public final static String SERVER_ASSQC = "https://dev1.infinitec.co.jp/assqc/index.php/api/v1/";
    public final static String SERVER_ASAPI = "https://asmobile.paramount.co.jp/asapi/index.php/api/v1/";
    public final static String SERVER_MAN1 = "http://27.86.2.229/asapi/api/v1/";
    public RadioGroup rgServer;
    public RadioButton rbServerWEBAPI, rbServerASSDEBUG, rbServerASSQC, rbServerASAPI, rbServerOTHER;
    public ImageView actionCopy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_layout);
        rgServer = findViewById(R.id.rgServer);
        rbServerWEBAPI = findViewById(R.id.rbServerWEBAPI);
        rbServerASSDEBUG = findViewById(R.id.rbServerASSDEBUG);
        rbServerASSQC = findViewById(R.id.rbServerASSQC);
        rbServerASAPI = findViewById(R.id.rbServerASAPI);
        rbServerOTHER = findViewById(R.id.rbServerOTHER);
        actionCopy = findViewById(R.id.actionCopy);
        etHost = findViewById(R.id.etHost);
        versionAPP = findViewById(R.id.versionAPP);
        save = findViewById(R.id.save);
        close = findViewById(R.id.close);
        applyView();
        applyListener();
    }

    private void applyView() {
        versionAPP.setText(getResources().getString(R.string.app_name) + " - v " + BuildConfig.VERSION_NAME);
        String serverURL = ServerModel.getHost().getUrl();
        rgServer.check(R.id.rbServerOTHER);
        etHost.setEnabled(true);
        if (serverURL.equals(SERVER_WEBAPI)) {
            rgServer.check(R.id.rbServerWEBAPI);
            etHost.setEnabled(false);
        }
        if (serverURL.equals(SERVER_ASSDEBUG)) {
            rgServer.check(R.id.rbServerASSDEBUG);
            etHost.setEnabled(false);
        }
        if (serverURL.equals(SERVER_ASSQC)) {
            rgServer.check(R.id.rbServerASSQC);
            etHost.setEnabled(false);
        }
        if (serverURL.equals(SERVER_ASAPI)) {
            rgServer.check(R.id.rbServerASAPI);
            etHost.setEnabled(false);
        }
        etHost.setText(serverURL);
    }

    private void applyListener() {
        save.setOnClickListener((view -> {
            changeServer();
            close.performClick();
        }));

        close.setOnClickListener((view -> finish()));
        rgServer.setOnCheckedChangeListener(((radioGroup, i) -> {
            String serverURL = ServerModel.getHost().getUrl();
            if (radioGroup.getCheckedRadioButtonId() == R.id.rbServerOTHER) {
                etHost.setEnabled(true);
            }

            if (radioGroup.getCheckedRadioButtonId() == R.id.rbServerWEBAPI) {
                etHost.setEnabled(false);
                serverURL = SERVER_WEBAPI;
            }

            if (radioGroup.getCheckedRadioButtonId() == R.id.rbServerASSDEBUG) {
                etHost.setEnabled(false);
                serverURL = SERVER_ASSDEBUG;
            }

            if (radioGroup.getCheckedRadioButtonId() == R.id.rbServerASSQC) {
                etHost.setEnabled(false);
                serverURL = SERVER_ASSQC;
            }
            if (radioGroup.getCheckedRadioButtonId() == R.id.rbServerASAPI) {
                etHost.setEnabled(false);
                serverURL = SERVER_ASAPI;
            }
            etHost.setText(serverURL);
        }));
        actionCopy.setOnClickListener((view -> {
            String textToCopy = etHost.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Server URL", textToCopy);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(ServerUtil.this, "Server URL \"" + textToCopy + "\"Copied", Toast.LENGTH_LONG).show();
        }));
    }

    private void changeServer() {
        String serverURL = ServerModel.getHost().getUrl();

        if (rgServer.getCheckedRadioButtonId() == R.id.rbServerOTHER) {
            serverURL = etHost.getText().toString();
        }

        if (rgServer.getCheckedRadioButtonId() == R.id.rbServerWEBAPI) {
            serverURL = SERVER_WEBAPI;
        }

        if (rgServer.getCheckedRadioButtonId() == R.id.rbServerASSDEBUG) {
            serverURL = SERVER_ASSDEBUG;
        }

        if (rgServer.getCheckedRadioButtonId() == R.id.rbServerASSQC) {
            serverURL = SERVER_ASSQC;
        }
        if (rgServer.getCheckedRadioButtonId() == R.id.rbServerASAPI) {
            serverURL = SERVER_ASAPI;
        }

        ServerModel.clear();
        ServerModel serverModel = new ServerModel();
        serverModel.setUrl(serverURL);
        serverModel.insert();
    }

    @Override
    public void onBackPressed() {
        return;
    }
}
