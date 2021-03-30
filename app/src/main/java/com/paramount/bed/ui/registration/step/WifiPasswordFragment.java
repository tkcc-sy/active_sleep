package com.paramount.bed.ui.registration.step;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.paramount.bed.R;
import com.paramount.bed.ui.registration.RegistrationStepActivity;

public class WifiPasswordFragment extends Fragment{
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_registration_step_wifipassword, container,false );

        EditText etSSID = (EditText) view.findViewById(R.id.etSSID);
        EditText etPassword = (EditText) view.findViewById(R.id.etPassword);

        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        etSSID.setText(activity.selectedWifi);
        etSSID.setEnabled(false);

        Button btnConnect = (Button) view.findViewById(R.id.btnConnect);
        Button btnBack = (Button) view.findViewById(R.id.btnBack);
        btnConnect.setOnClickListener(connect());
        btnBack.setOnClickListener(back());

        return view;
    }

    private View.OnClickListener back() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                activity.onBackPressed();
            }
        };
    }

    private View.OnClickListener connect() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                activity.go(activity.FRAGMENT_WIFI_CONNECT);
            }
        };
    }
}


