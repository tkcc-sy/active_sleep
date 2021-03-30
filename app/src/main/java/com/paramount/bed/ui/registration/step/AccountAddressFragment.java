package com.paramount.bed.ui.registration.step;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.FormPolicyModel;
import com.paramount.bed.data.model.MattressHardnessSettingModel;
import com.paramount.bed.data.model.NemuriScanModel;
import com.paramount.bed.data.model.RegisterStep;
import com.paramount.bed.data.model.RegisteringModel;
import com.paramount.bed.data.model.SettingModel;
import com.paramount.bed.data.provider.FormPolicyProvider;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.ZipResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.BaseFragment;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.GONE;

public class AccountAddressFragment extends BLEFragment {
    EditText etHeight, etZipCode;
    EditText etWeight;
    TextView etAddress;
    LinearLayout etHeightClick, etWeightClick;
    List<String> weightOptions;
    List<String> heightOptions;
    OptionsPickerView weightPicker;
    OptionsPickerView heightPicker;
    UserService userService;
    String valZip;

    EditText etHardness;
    LinearLayout etHardnessClick;
    LinearLayout mattressSettingContainer;
    
    View view;
    public Boolean pressSearch = false;
    public static Button btnNext;

    List<MattressHardnessSettingModel> hardnesOptions = new ArrayList<>();
    OptionsPickerView hardnessPicker;
    MattressHardnessSettingModel selectedHardness = FormPolicyProvider.getDefaultMattressHardnessSetting();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registration_step_account_address, container, false);
        userService = ApiClient.getClient(getContext()).create(UserService.class);

        btnNext = (Button) view.findViewById(R.id.btnNext);
        etWeight = (EditText) view.findViewById(R.id.etWeight);
        etHeight = (EditText) view.findViewById(R.id.etHeight);
        etWeightClick = (LinearLayout) view.findViewById(R.id.etWeightClick);
        etHeightClick = (LinearLayout) view.findViewById(R.id.etHeightClick);
        etZipCode = view.findViewById(R.id.etPostalCode);
        etAddress = view.findViewById(R.id.etAddress);

        etHardness = view.findViewById(R.id.etHardness);
        etHardnessClick = view.findViewById(R.id.etHardnessClick);
        mattressSettingContainer = view.findViewById(R.id.mattressSettingContainer);

        etZipCode.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                //XXX do something
                if (etZipCode.getText().length() == 0) {
                    etAddress.setText("");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //XXX do something

            }

            String tempString;
            char[] stringArray;

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pressSearch = true;
                if (etZipCode.getText().length() == 4) {
                    tempString = etZipCode.getText().toString() + "-";
                    char c = tempString.charAt(tempString.length() - 2);

                    if (c != '-') {
                        stringArray = tempString.toCharArray();
                        stringArray[tempString.length() - 2] = stringArray[tempString.length() - 1];
                        stringArray[tempString.length() - 1] = c;

                        //code to convert charArray back to String..
                        tempString = new String(stringArray);
                        etZipCode.setText(tempString);
                        etZipCode.setSelection(tempString.length());
                        tempString = null;
                    }

                }
            }
        });

//        if (etWeight != null && etHeight != null && etZipCode != null) {
//            btnNext.setEnabled(true);
//        } else {
//            btnNext.setEnabled(false);
//        }

        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        if (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getZipCode() != null) {

            if (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getZipCode().toString().equals("")) {
                etZipCode.setText("");
                pressSearch = false;
            } else {
                String zipBegin = RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getZipCode().substring(0, 3);
                String zipLast = RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getZipCode().substring(3, 7);

                etZipCode.setText(zipBegin + "-" + zipLast);
                pressSearch = true;
            }

            if (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getWeight() != 0) {
                etWeight.setText(String.valueOf(RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getWeight()) + " kg");
            }

            if (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getHeight() != 0) {
                etHeight.setText(String.valueOf(RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getHeight()) + " cm");
            }

            if (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getCity() != null) {
                activity.CITY = RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getCity();
            }

            if (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getPrefecture() != null) {
                activity.PREFECTURE = RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getPrefecture();
            }

            if (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getStreetAddress().equals("")) {
                etAddress.setText("");
                activity.ADDRESS = "";
            } else {
                etAddress.setText(RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getStreetAddress());
                activity.ADDRESS = RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getStreetAddress();
            }
        }

        btnNext.setOnClickListener(next());
        etWeight.setOnFocusChangeListener(onWeightFocus());
        etHeight.setOnFocusChangeListener(onHeightFocus());

        etWeightClick.setOnFocusChangeListener(onWeightFocus());
        etHeightClick.setOnFocusChangeListener(onHeightFocus());

        weightOptions = new ArrayList<>();
        heightOptions = new ArrayList<>();
        // set options
        for (int i = 139; i <= 210; i++) {
            if (i == 139) {
                heightOptions.add(LanguageProvider.getLanguage("UI000440C020"));
            } else {
                heightOptions.add(i + " cm");
            }
        }

        for (int i = 19; i <= 140; i++) {
            if (i == 19) {
                weightOptions.add(LanguageProvider.getLanguage("UI000440C021"));
            } else {
                weightOptions.add(i + " kg");
            }
        }


        weightPicker = new OptionsPickerBuilder(getActivity(), onWeightSelect())
                .setSelectOptions(36)
                .setCyclic(false, false, false)
                .setCancelText(LanguageProvider.getLanguage("UI000450C015"))
                .setSubmitText(LanguageProvider.getLanguage("UI000450C016"))
                .build();

        weightPicker.setPicker(weightOptions);

        heightPicker = new OptionsPickerBuilder(getActivity(), onHeightSelect())
                .setSelectOptions(26)
                .setCyclic(false, false, false)
                .setCancelText(LanguageProvider.getLanguage("UI000450C017"))
                .setSubmitText(LanguageProvider.getLanguage("UI000450C018"))
                .build();

        heightPicker.setPicker(heightOptions);

        applyLocalization(view);

        Button btnZipReq = view.findViewById(R.id.btn_zip_req);
        btnZipReq.setOnClickListener((v) -> {
            if (BuildConfig.DEMO_MODE) {
                return;
            }
            if (zipValidation()) {
                reqZipDialog();
            }
        });

        etZipCode.setText(RegisteringModel.getProfile().getZipCode());
        etAddress.setText(RegisteringModel.getProfile().getAddress());
        etHeight.setText(RegisteringModel.getProfile().getHeight() == 0 ? "" : String.valueOf(RegisteringModel.getProfile().getHeight()) + " cm");
        etWeight.setText(RegisteringModel.getProfile().getWeight() == 0 ? "" : String.valueOf(RegisteringModel.getProfile().getWeight()) + " kg");

        //mattress hardness setting
        etHardness.setFocusable(false);
        etHardness.setClickable(true);
        etHardness.setOnClickListener(onHardnessClick());
        etHardnessClick.setOnClickListener(onHardnessClick());

        NemuriScanModel nemuriScanModel = BluetoothListFragment.selectedNemuriScan;
        ArrayList<String> hardnessOptionString = new ArrayList<>();
        int hardnessIndex;
        FormPolicyModel formPolicyModel = FormPolicyModel.getPolicy();
        selectedHardness = formPolicyModel.getMattressHardnessSettingById(RegisteringModel.getProfile().getDesiredHardness());
        hardnessIndex = formPolicyModel.getMattressHardnessSettingIndexById(RegisteringModel.getProfile().getDesiredHardness());
        hardnesOptions.addAll(formPolicyModel.getMattressHardnessSetting());

        for (MattressHardnessSettingModel mattressHardnessSettingModel:hardnesOptions
        ) {
            hardnessOptionString.add(mattressHardnessSettingModel.getValue());
        }

        if(nemuriScanModel != null){
            mattressSettingContainer.setVisibility(nemuriScanModel.isMattressExist() ? View.VISIBLE : GONE);
            etHardness.setText(selectedHardness.getValue());
        }

        hardnessPicker = new OptionsPickerBuilder(getContext(), onHardnessSelect())
                .setBackgroundId(0)
                .setCyclic(false, false, false)
                .setCancelText(LanguageProvider.getLanguage("UI000450C020"))
                .setSubmitText(LanguageProvider.getLanguage("UI000450C021"))
                .setSelectOptions(hardnessIndex)
                .build();

        hardnessPicker.setPicker(hardnessOptionString);

        return view;
    }

    private OnOptionsSelectListener onWeightSelect() {
        return new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                etWeight.setText(weightOptions.get(options1));
                if (options1 == 0) {
                    etWeight.setText("");
                }
                updateData(2);
            }
        };
    }


    private OnOptionsSelectListener onHeightSelect() {
        return new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                etHeight.setText(heightOptions.get(options1));
                if (options1 == 0) {
                    etHeight.setText("");
                }
                updateData(3);
            }
        };
    }

    private View.OnFocusChangeListener onWeightFocus() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ViewUtil.hideKeyboardFrom(getContext(), view);
                    for (int i = 0; i < weightOptions.size(); i++) {
                        if (etWeight.getText().toString().equals(weightOptions.get(i))) {
                            weightPicker.setSelectOptions(i);
                        }
                    }
                    weightPicker.show();
                }
            }
        };
    }

    private OnOptionsSelectListener onHardnessSelect() {
        return (options1, options2, options3, v) -> {
            selectedHardness = hardnesOptions.get(options1);
            etHardness.setText(selectedHardness.getValue());
        };
    }

    private View.OnFocusChangeListener onHeightFocus() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ViewUtil.hideKeyboardFrom(getContext(), view);
                    for (int i = 0; i < heightOptions.size(); i++) {
                        if (etHeight.getText().toString().equals(heightOptions.get(i))) {
                            heightPicker.setSelectOptions(i);
                        }
                    }
                    heightPicker.show();
                }
            }
        };
    }

    private View.OnClickListener onHardnessClick(){
        return view -> {
            ViewUtil.hideKeyboardFrom(getContext(), view);
            hardnessPicker.show();
        };
    }

    public static String iCITY, iPREFECTURE;

    private View.OnClickListener next() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                btnNext.setEnabled(false);
                RegisteringModel.updateDesiredHardness(selectedHardness.getId());
                RegisteringModel.updateZipCode(etZipCode.getText().toString().trim());
                RegisteringModel.updateAddress(etAddress.getText().toString().trim());
                if (etWeight.getText().toString().equals("") || !etWeight.getText().toString().contains("kg")) {
                    RegisteringModel.updateWeight(0);
                } else {
                    RegisteringModel.updateWeight(Integer.valueOf(etWeight.getText().toString().replace(" kg", "").trim()));
                }

                if (etHeight.getText().toString().equals("") || !etHeight.getText().toString().contains("cm")) {
                    RegisteringModel.updateHeight(0);
                } else {
                    RegisteringModel.updateHeight(Integer.valueOf(etHeight.getText().toString().replace(" cm", "").trim()));
                }

                RegistrationStepActivity.mInstance.showLoading();
                valZip = etZipCode.getText().toString().replace("-", "");
                RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                if (valZip.trim().length() != 0) {
                    if (zipValidation() && pressSearchValidation()) {

                        String zipCode = etZipCode.getText().toString().replace("-", "");
                        activity.mDisposable = activity.userService.requestZipLookup(zipCode, 1)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribeWith(new DisposableSingleObserver<BaseResponse<com.paramount.bed.data.remote.response.ZipResponse>>() {
                                    @Override
                                    public void onSuccess(BaseResponse<ZipResponse> zipResponseBaseResponse) {
                                        if (etZipCode.getText().toString().equals("")) {
                                            setData(activity);
                                        } else {
                                            if (zipResponseBaseResponse.isSucces()) {
                                                setData(activity);
                                            } else {
                                                RegistrationStepActivity.mInstance.hideLoading();
                                                btnNext.setEnabled(true);
                                                DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(zipResponseBaseResponse.getMessage()));
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        RegistrationStepActivity.mInstance.hideLoading();
                                        btnNext.setEnabled(true);
                                        if (!NetworkUtil.isNetworkConnected(getContext())) {
                                            DialogUtil.offlineDialog(activity, getContext());
                                        } else {
                                            DialogUtil.serverFailed(activity, "UI000802C121", "UI000802C122", "UI000802C123", "UI000802C124");
                                        }
                                    }
                                });
                    } else {
                        RegistrationStepActivity.mInstance.hideLoading();
                        btnNext.setEnabled(true);
                    }
                } else {
                    etAddress.setText("");
                    setData(activity);
                }
            }
        };
    }

    private void setData(RegistrationStepActivity activity) {
        RegisterStep registerStep = new RegisterStep();
        registerStep.setZipCode(etZipCode.getText().toString().replace("-", ""));
        registerStep.setCity(activity.CITY);
        registerStep.setPrefecture(activity.PREFECTURE);
        registerStep.setStreetAddress(etAddress.getText().toString());
        if (etWeight.getText().toString().equals("") || !etWeight.getText().toString().contains("kg")) {
            registerStep.setWeight(0);
        } else {
            registerStep.setWeight(Integer.valueOf(etWeight.getText().toString().replace(" kg", "").trim()));
        }

        if (etHeight.getText().toString().equals("") || !etHeight.getText().toString().contains("cm")) {
            registerStep.setHeight(0);
        } else {
            registerStep.setHeight(Integer.valueOf(etHeight.getText().toString().replace(" cm", "").trim()));
        }

        activity.ZIP_CODE = etZipCode.getText().toString().replace("-", "");
        activity.ADDRESS = etAddress.getText().toString();
        activity.WEIGHT = etWeight.getText().toString();
        activity.HEIGHT = etHeight.getText().toString();

        registerStep.update(activity.EMAIL, 10);
        RegistrationStepActivity.mInstance.getQuestionnare(true);
    }

    private void reqZipDialog() {
        showLoading();
        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        String zipCode = etZipCode.getText().toString().replace("-", "");
        activity.mDisposable = activity.userService.requestZipLookup(zipCode, 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<com.paramount.bed.data.remote.response.ZipResponse>>() {

                    @Override
                    public void onSuccess(BaseResponse<ZipResponse> zipResponseBaseResponse) {
                        hideLoading();
                        if (etZipCode.getText().toString().equals("")) {
                            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
                            activity.ZIP_CODE = "";
                            activity.CITY = iCITY;
                            activity.PREFECTURE = iPREFECTURE;
//                            etAddress.setText("");
                            activity.ADDRESS = etAddress.getText().toString();

                            if (etHeight.getText().toString().equals("") || !etHeight.getText().toString().contains("cm")) {
                                activity.HEIGHT = "0 cm";
                            } else {
                                activity.HEIGHT = etHeight.getText().toString();
                            }

                            if (etWeight.getText().toString().equals("") || !etWeight.getText().toString().contains("kg")) {
                                activity.WEIGHT = "0 kg";
                            } else {
                                activity.WEIGHT = etWeight.getText().toString();
                            }

                            pressSearch = false;
                            updateData(1);
                        } else {
                            if (zipResponseBaseResponse.isSucces()) {
                                iCITY = zipResponseBaseResponse.getData().getCity();
                                iPREFECTURE = zipResponseBaseResponse.getData().getPrefecture();

                                activity.ZIP_CODE = etZipCode.getText().toString().replace("-", "");
                                activity.CITY = zipResponseBaseResponse.getData().getCity();
                                activity.PREFECTURE = zipResponseBaseResponse.getData().getPrefecture();

                                if (zipResponseBaseResponse.getData().getCity() != null) {
                                    etAddress.setText(zipResponseBaseResponse.getData().getPrefecture() + " " + zipResponseBaseResponse.getData().getCity() + " " + zipResponseBaseResponse.getData().getTown());
                                    activity.ADDRESS = etAddress.getText().toString();
                                    updateData(1);
                                    pressSearch = false;
                                } else {
                                    etAddress.setText("");
                                }
                            } else {
                                DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(zipResponseBaseResponse.getMessage())
                                        .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
                                );
                            }
                        }

                        etAddress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("abx", e.getMessage());
                        hideLoading();
                        if (!NetworkUtil.isNetworkConnected(getContext())) {
                            DialogUtil.offlineDialog(activity, getContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(activity);
                        } else {
                            DialogUtil.serverFailed(activity, "UI000802C121", "UI000802C122", "UI000802C123", "UI000802C124");
                        }
                    }
                });
    }

    public boolean zipValidation() {
        valZip = etZipCode.getText().toString().replace("-", "");
        if (valZip.trim().length() == 0 || valZip.trim().length() != 7) {
            btnNext.setEnabled(true);
            DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000450C012")
                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
            );
            return false;
        } else if (!valZip.matches("[0-9]+") && valZip.length() != 0) {
            btnNext.setEnabled(true);
            DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000450C011")
                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
            );
            return false;
        }
        return true;

    }

    public boolean pressSearchValidation() {
        if (valZip.trim().length() == 0) {
            btnNext.setEnabled(true);
            DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000450C012")
                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
            );
            return false;
        }
        if (pressSearch && !etZipCode.getText().toString().replace("-", "").trim().equals("")) {
            btnNext.setEnabled(true);
            DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000450C014")
                    .replace("%LEN%", String.valueOf(FormPolicyModel.getPolicy().getZipCodeLength()))
            );
            return false;
        } else {
            return true;
        }
    }

    public void updateData(int step) {
        RegisterStep registerStep = new RegisterStep();
        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        switch (step) {
            case 1:
                if (!etWeight.getText().toString().equals("") && !etHeight.getText().toString().equals("")) {
                    registerStep.setZipCode(etZipCode.getText().toString().replace("-", ""));
                    registerStep.setCity(activity.CITY);
                    registerStep.setPrefecture(activity.PREFECTURE);
                    registerStep.setStreetAddress(activity.ADDRESS);

                    String strHeight = "0";
                    if (etHeight.getText().toString().contains("cm")) {
                        strHeight = etHeight.getText().toString().replace(" cm", "").trim();
                    }

                    String strWeight = "0";
                    if (etWeight.getText().toString().contains("kg")) {
                        strWeight = etWeight.getText().toString().replace(" kg", "").trim();
                    }
                    registerStep.setHeight(Integer.valueOf(strHeight));
                    registerStep.setWeight(Integer.valueOf(strWeight));

                    registerStep.update(activity.EMAIL, 10);
                }
                break;
            case 2:
                if (!etZipCode.getText().toString().replace("-", "").trim().equals("") && !etHeight.getText().toString().equals("")) {
                    registerStep.setZipCode(etZipCode.getText().toString().replace("-", ""));
                    registerStep.setCity(activity.CITY);
                    registerStep.setPrefecture(activity.PREFECTURE);
                    registerStep.setStreetAddress(activity.ADDRESS);

                    String strHeight = "0";
                    if (etHeight.getText().toString().contains("cm")) {
                        strHeight = etHeight.getText().toString().replace(" cm", "").trim();
                    }

                    String strWeight = "0";
                    if (etWeight.getText().toString().contains("kg")) {
                        strWeight = etWeight.getText().toString().replace(" kg", "").trim();
                    }
                    registerStep.setHeight(Integer.valueOf(strHeight));
                    registerStep.setWeight(Integer.valueOf(strWeight));

                    registerStep.update(activity.EMAIL, 10);
                }
                break;
            case 3:
                if (!etZipCode.getText().toString().replace("-", "").trim().equals("") && !etWeight.getText().toString().equals("")) {
                    registerStep.setZipCode(etZipCode.getText().toString().replace("-", ""));
                    registerStep.setCity(activity.CITY);
                    registerStep.setPrefecture(activity.PREFECTURE);
                    registerStep.setStreetAddress(activity.ADDRESS);

                    String strHeight = "0";
                    if (etHeight.getText().toString().contains("cm")) {
                        strHeight = etHeight.getText().toString().replace(" cm", "").trim();
                    }

                    String strWeight = "0";
                    if (etWeight.getText().toString().contains("kg")) {
                        strWeight = etWeight.getText().toString().replace(" kg", "").trim();
                    }
                    registerStep.setHeight(Integer.valueOf(strHeight));
                    registerStep.setWeight(Integer.valueOf(strWeight));

                    registerStep.update(activity.EMAIL, 10);
                }
                break;
            default:
                registerStep.setZipCode(etZipCode.getText().toString().replace("-", ""));
                registerStep.setCity(activity.CITY);
                registerStep.setPrefecture(activity.PREFECTURE);
                registerStep.setStreetAddress(activity.ADDRESS);

                String strHeight = "0";
                if (etHeight.getText().toString().contains("cm")) {
                    strHeight = etHeight.getText().toString().replace(" cm", "").trim();
                }

                String strWeight = "0";
                if (etWeight.getText().toString().contains("kg")) {
                    strWeight = etWeight.getText().toString().replace(" kg", "").trim();
                }
                registerStep.setHeight(Integer.valueOf(strHeight));
                registerStep.setWeight(Integer.valueOf(strWeight));
        }
    }
}





