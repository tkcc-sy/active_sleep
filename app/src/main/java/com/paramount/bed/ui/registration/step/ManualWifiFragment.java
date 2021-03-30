package com.paramount.bed.ui.registration.step;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.ValidationUtils;
import com.paramount.bed.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;


public class ManualWifiFragment extends BLEFragment {
    List<String> wifiSecurity;
    OptionsPickerView wifiSecurityPicker;
    EditText etWifiSecurity;
    EditText etSSID;
    EditText etPassword;
    int selectedEncryption = -1;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registration_step_manualwifi, container, false);
        Button btnConnect = view.findViewById(R.id.btnConnect);
        etWifiSecurity = view.findViewById(R.id.etWifiSecurity);
        etPassword = view.findViewById(R.id.etPassword);
        etSSID = view.findViewById(R.id.etSSID);
        BaseActivity.isLoading = false;
        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();

        etSSID.setText(activity.selectedWifi);

        btnConnect.setOnClickListener(connect());

        wifiSecurity = new ArrayList<>();
        wifiSecurity.add(LanguageProvider.getLanguage("UI000330C017"));
        wifiSecurity.add(LanguageProvider.getLanguage("UI000330C018"));
        wifiSecurity.add(LanguageProvider.getLanguage("UI000330C019"));
        wifiSecurity.add(LanguageProvider.getLanguage("UI000330C020"));
        wifiSecurity.add(LanguageProvider.getLanguage("UI000330C021"));
        wifiSecurity.add(LanguageProvider.getLanguage("UI000330C022"));
        wifiSecurity.add(LanguageProvider.getLanguage("UI000330C023"));

        applyLocalization(view);
        wifiSecurityPicker = new OptionsPickerBuilder(getActivity(), onWifiSecuritySelect())
                .setCyclic(false, false, false)
                .setCancelText(LanguageProvider.getLanguage("UI000330C024"))
                .setSubmitText(LanguageProvider.getLanguage("UI000330C025"))
                .build();

        wifiSecurityPicker.setPicker(wifiSecurity);
        etWifiSecurity.setText(wifiSecurity.get(6));

        etWifiSecurity.setOnFocusChangeListener(onWifiSecurityFocus());

        return view;
    }

    private OnOptionsSelectListener onWifiSecuritySelect() {
        return (options1, options2, options3, v) -> {
            etWifiSecurity.setText(wifiSecurity.get(options1));
            selectedEncryption = options1;
        };
    }

    private View.OnFocusChangeListener onWifiSecurityFocus() {
        return (v, hasFocus) -> {
            if (hasFocus) {
                ViewUtil.hideKeyboardFrom(getContext(), view);
                wifiSecurityPicker.show();
            }
        };
    }

    private View.OnClickListener back() {
        return v -> {
            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
            activity.onBackPressed();
        };
    }

    //TODO : Proper data passing
    public static String chosenSSID = "";
    public static String chosenPassword = "";
    public static int chosenEncryption = 0;

    private View.OnClickListener connect() {
        return v -> {
            chosenSSID = etSSID.getText().toString().trim();
            chosenPassword = etPassword.getText().toString().trim();
            if (isBadSSID()) return;
            if (isBadWiFiSecurity()) return;

            if (selectedEncryption > -1 && selectedEncryption < 6) {
                chosenEncryption = selectedEncryption;
            } else if (selectedEncryption == 6) {
                chosenEncryption = 11;
            } else {
                chosenEncryption = 0;
            }
            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
            activity.go(activity.FRAGMENT_WIFI_CONNECT);
        };
    }

    public boolean isBadSSID() {
        String wifiName = etSSID.getText().toString().trim();
        return ValidationUtils.SSID.isBad(wifiName, new ValidationUtils.SSID.BadSSIDListener() {
            @Override
            public void onSSIDEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000330C004")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            //Tag UI000330C004 di masukeun ka dieu atas instruksi Kang @Zakaria walaupun Salome
            @Override
            public void onSSIDShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000330C004")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onSSIDLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000330C016")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }

    private boolean isBadWiFiSecurity() {
        String wifiPassword = etPassword.getText().toString().trim();
        return ValidationUtils.SSID_PASSWORD.isBad(wifiPassword, new ValidationUtils.SSID_PASSWORD.BadSSIDPasswordListener() {
            @Override
            public void onSSIDPasswordShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000330C012")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength))
                );
            }

            @Override
            public void onSSIDPasswordLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000330C012")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength))
                );
            }
        });
    }

    @Override
    protected void onNSDisconnectCancelled() {
        RegistrationStepActivity parentActivity = (RegistrationStepActivity) getActivity();
        if (parentActivity != null) {
            new Handler().postDelayed(() -> {
                parentActivity.poptoFragmentTag(RegistrationStepActivity.FRAGMENT_BLUETOOTH_LIST);
            }, 100);
        }
    }
}

