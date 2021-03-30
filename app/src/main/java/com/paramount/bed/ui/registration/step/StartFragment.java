package com.paramount.bed.ui.registration.step;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.BaseFragment;
import com.paramount.bed.ui.main.FaqActivity;
import com.paramount.bed.ui.main.HomeActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.BluetoothUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.LogUserAction;

public class StartFragment extends BaseFragment {
    public static boolean isOpenSettings;
    public static boolean isFromBluetoothList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_start, container, false);
        Button btnContinue = (Button) view.findViewById(R.id.btnContinue);
        TextView heading = (TextView) view.findViewById(R.id.heading);
        isOpenSettings = false;
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getContext().registerReceiver(mBroadcastReceiver1, filter1);
        btnContinue.setOnClickListener(next());

        ListView listView = (ListView) view.findViewById(R.id.listview);
        String[] values = new String[]{
                LanguageProvider.getLanguage("UI000310C003"),
                LanguageProvider.getLanguage("UI000310C004"),
                LanguageProvider.getLanguage("UI000310C005")
        };
        applyLocalization(view);
        StartInstructionsAdapter adapter = new StartInstructionsAdapter(getActivity(), values);
        listView.setEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);
        listView.setAdapter(adapter);
        if(DisplayUtils.FONTS.bigFontStatus(getContext())) {
            btnContinue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            heading.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        activity.statusForStartFragment = true;
        if (activity.statusForStartFragment && isOpenSettings == false) {
//            activity.btnBack.setEnabled(false);
            if (BluetoothUtil.isBluetoothEnable() && !isFromBluetoothList) {

//                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000310C013"), LanguageProvider.getLanguage("UI000310C014"), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
//                        activity.go(activity.FRAGMENT_BLUETOOTH_LIST);
//                        getContext().unregisterReceiver(mBroadcastReceiver1);
//                        dialogInterface.dismiss();
//
//                    }
//                });

            }
            activity.statusForStartFragment = false;
        }
        if (isFromBluetoothList) {
            isFromBluetoothList = false;
        }
    }

    private View.OnClickListener next() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (BluetoothUtil.isBluetoothEnable()) {
                    DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000310C013"), LanguageProvider.getLanguage("UI000310C014"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                            activity.go(activity.FRAGMENT_BLUETOOTH_LIST);
                            getContext().unregisterReceiver(mBroadcastReceiver1);
                            dialogInterface.dismiss();

                        }
                    });
                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, 100);
                    isOpenSettings = true;
                }
            }
        };
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:

                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:

                        break;
                    case BluetoothAdapter.STATE_ON:

                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        if (!isOpenSettings) {
                            DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000310C013"), LanguageProvider.getLanguage("UI000310C014"), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                                    activity.go(activity.FRAGMENT_BLUETOOTH_LIST);
                                }
                            });
                        }
                        break;
                }

            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            checkNextResult();
        }
    }

    public void checkNextResult() {
        if (BluetoothUtil.isBluetoothEnable()) {
            DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000310C011"), LanguageProvider.getLanguage("UI000310C012"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                    activity.go(activity.FRAGMENT_BLUETOOTH_LIST);
                    getContext().unregisterReceiver(mBroadcastReceiver1);
                }
            });
        } else {
            DialogUtil.createSimpleOkDialogLink(getActivity(), "", LanguageProvider.getLanguage("UI000310C008"), LanguageProvider.getLanguage("UI000310C009"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent faqIntent = new Intent(RegistrationStepActivity.mInstance, FaqActivity.class);
                    faqIntent.putExtra("ID_FAQ", "UI000310C009");
                    faqIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(faqIntent);
                    dialogInterface.dismiss();
                }
            }, LanguageProvider.getLanguage("UI000310C010"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        }
    }

}

