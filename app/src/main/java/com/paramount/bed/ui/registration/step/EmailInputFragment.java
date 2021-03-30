package com.paramount.bed.ui.registration.step;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.ligl.android.widget.iosdialog.IOSDialog;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.PasswordPolicyModel;
import com.paramount.bed.data.model.RegisterStep;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.model.ValidationEmailModel;
import com.paramount.bed.data.model.ValidationSNSModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.provider.PasswordPolicyProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.data.remote.response.ActivationEmailResponse;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.data.remote.response.PasswordPolicyResponse;
import com.paramount.bed.data.remote.service.UserService;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.ui.BaseFragment;
import com.paramount.bed.ui.main.AutomaticWakeOperationActivity;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.AutoSizeTextUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.data.remote.response.ValidationEmailResponse;
import com.paramount.bed.util.FeatureUtil;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.PasswordPolicyUtil;
import com.paramount.bed.util.ValidationUtils;

import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.paramount.bed.ui.BaseActivity.isLoading;
import static com.paramount.bed.ui.registration.RegistrationStepActivity.mInstance;

public class EmailInputFragment extends BLEFragment {
    public EditText etEmail, etPassword, etConfirmPassword;
    Button btnRegister;
    private RegistrationStepActivity activity;
    int statusRegister = 0;
    ImageView btnBack;
    private PasswordPolicyProvider passwordPolicyProvider;
    public String companyCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_step_email_input, container, false);
        passwordPolicyProvider = new PasswordPolicyProvider(getActivity());
        applyLocalization(view);
        registerView(view);
        registerListener();
        applyUI();

        showLoading();
        passwordPolicyProvider.getPasswordPolicy(companyCode, (passwordPolicyModel, cc) -> {
            getActivity().runOnUiThread(() -> {
                hideLoading();
                applyHint(passwordPolicyModel);
            });
        });
        return view;
    }

    private void registerView(View view) {
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnRegister = view.findViewById(R.id.btnRegister);
        RegistrationStepActivity registrationStepActivity = (RegistrationStepActivity) getActivity();
        btnBack = registrationStepActivity.findViewById(R.id.btnBack);
    }

    private void registerListener() {
        btnRegister.setOnClickListener(view -> {
            //Initializing
            String vDataEmail = etEmail.getText().toString();
            String vDataPassword = etPassword.getText().toString();
            String vDataConfirmPassword = etConfirmPassword.getText().toString();

//            ApiClient.LogData.setLogEmail(getActivity(), etEmail.getText().toString());

            //Validating
            if (isBadEmail(vDataEmail)) return;
            if (isBadPassword(vDataPassword, vDataConfirmPassword)) return;

            //Executing
            nextOnlinePasswordCheck();
        });
    }

    private void applyUI() {
        RegistrationStepActivity activity = (RegistrationStepActivity) mInstance;
        applyHint(PasswordPolicyModel.getFirst());
        companyCode = getActivity().getIntent() != null && getActivity().getIntent().getStringExtra("companyCode") != null ? getActivity().getIntent().getStringExtra("companyCode") : "";
        if (activity.registerData.getEmail() != null && !activity.registerData.getEmail().isEmpty()) {
            etEmail.setEnabled(false);
            etEmail.setText(activity.registerData.getEmail().trim());
            statusRegister = 2;

            if (RegisterStep.getRegisterStepbyEmail(activity.registerData.getEmail()) == null) {
                RegisterStep.clear();
                RegisterStep registerStep = new RegisterStep();
                registerStep.setEmail(activity.registerData.getEmail());
                registerStep.insert();
            }
        } else {
            statusRegister = 1;
            if (RegisterStep.getRegisterStep() != null) {
                etEmail.setText(RegisterStep.getRegisterStep().getEmail().trim());
            }
        }
    }

    public void nextOnlinePasswordCheck() {
        showLoading();
        String passwordType = etPassword.getText().toString();
        passwordPolicyProvider.checkPasswordPolicy(companyCode, passwordType, (isResponse, passwordPolicyModel, baseResponse, e) -> {
            getActivity().runOnUiThread(() -> {
                hideLoading();
                applyHint(passwordPolicyModel);
                if (!isResponse) {
                    if (!NetworkUtil.isNetworkConnected(getActivity())) {
                        DialogUtil.offlineDialog(getActivity(), getActivity());
                        return;
                    }
                    if (MultipleDeviceUtil.isTokenExpired(e)) {
                        DialogUtil.tokenExpireDialog(getActivity());
                        return;
                    }
                    DialogUtil.serverFailed(getActivity(), "UI000802C129", "UI000802C130", "UI000802C131", "UI000802C131");
                    return;
                }

                if (!baseResponse.isSucces()) {
                    String Lang = LanguageProvider.getLanguage(baseResponse.getMessage());
                    String strReplaceDialogue = Lang.replace("%MIN_LEN%", passwordPolicyModel.minLength)
                            .replace("%MAX_LEN%", passwordPolicyModel.maxLength)
                            .replace("%ALLOWED_SYMBOLS%", passwordPolicyModel.allowedSymbols);
                    DialogUtil.createSimpleOkDialog(getActivity(), "", strReplaceDialogue);
                    return;
                }
                nextOnlineEmailCheck();
            });
        });
    }

    public void nextOnlineEmailCheck() {
        btnBack.setEnabled(false);
        activity = (RegistrationStepActivity) getActivity();
        String email = etEmail.getText().toString().trim();

        if (BuildConfig.DEMO_MODE) {
            activity.go(activity.FRAGMENT_TELEPHONE_INPUT);
            return;
        }

        activity.mDisposable = activity.userService.registrationEmailValidation(email, statusRegister, ValidationEmailModel.getByEmail(email).getToken(), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<ValidationEmailResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<com.paramount.bed.data.remote.response.ValidationEmailResponse> emailResponse) {
                        hideLoading();
                        if (emailResponse.isSucces()) {
                            ValidationEmailModel.updateByEmail(etEmail.getText().toString().trim(), emailResponse.getData().getActivation_id(), emailResponse.getData().getToken());
                            btnRegister.setEnabled(false);
                            String email = etEmail.getText().toString().trim();
                            //Check If Account is SNS
                            if (ValidationSNSModel.isSNS()) {
                                mailActivation(email);
                            } else if (statusRegister == 2) {
                                btnBack.setEnabled(true);
                                activity.EMAIL = etEmail.getText().toString().trim();
                                activity.PASSWORD = etPassword.getText().toString();

                                if (RegisterStep.getRegisterStepbyEmail(etEmail.getText().toString().trim()) == null) {
                                    RegisterStep.clear();
                                    RegisterStep registerStep = new RegisterStep();
                                    registerStep.setEmail(etEmail.getText().toString().trim());
                                    registerStep.insert();
                                }

                                activity.go(activity.FRAGMENT_TELEPHONE_INPUT);
                            } else {
                                DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(emailResponse.getMessage()), LanguageProvider.getLanguage("UI000410C019"), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (statusRegister == 1) {
                                            mailActivation(email);
                                        } else if (statusRegister == 2) {
                                            btnBack.setEnabled(true);
                                            btnRegister.setEnabled(true);
                                            activity.EMAIL = etEmail.getText().toString().trim();
                                            activity.PASSWORD = etPassword.getText().toString();

                                            if (RegisterStep.getRegisterStepbyEmail(etEmail.getText().toString().trim()) == null) {
                                                RegisterStep.clear();
                                                RegisterStep registerStep = new RegisterStep();
                                                registerStep.setEmail(etEmail.getText().toString().trim());
                                                registerStep.insert();
                                            }

                                            activity.go(activity.FRAGMENT_TELEPHONE_INPUT);
                                        }
                                    }
                                });
                            }

                        }
                        //ByPass Check Validation Email
                        else if (emailResponse.getMessage().equals("USR07-C006")
//                                || (emailResponse.getMessage().equals("USR07-C007") && statusRegister == 2)
                        ) {
                            btnBack.setEnabled(true);
                            activity.EMAIL = etEmail.getText().toString().trim();
                            activity.PASSWORD = etPassword.getText().toString();

                            if (RegisterStep.getRegisterStepbyEmail(etEmail.getText().toString().trim()) == null) {
                                RegisterStep.clear();
                                RegisterStep registerStep = new RegisterStep();
                                registerStep.setEmail(etEmail.getText().toString().trim());
                                registerStep.insert();
                            }
                            btnRegister.setEnabled(true);
                            activity.go(activity.FRAGMENT_TELEPHONE_INPUT);
                        } else {
                            btnBack.setEnabled(true);
                            btnRegister.setEnabled(true);
                            DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(emailResponse.getMessage()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        btnBack.setEnabled(true);
                        Log.d("abx", e.getMessage());
                        btnRegister.setEnabled(true);
                        if (!NetworkUtil.isNetworkConnected(getContext())) {
                            DialogUtil.offlineDialog(activity, getContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(activity);
                        } else {
                            DialogUtil.serverFailed(activity, "UI000802C129", "UI000802C130", "UI000802C131", "UI000802C132");
                        }
                    }
                });
    }

    public void mailActivation(String email) {
        isLoading = true;
        RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
        activity.mDisposable = activity.userService.registrationEmailActivation(email, ValidationEmailModel.getByEmail(email).getToken(), 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<ActivationEmailResponse>>() {
                    @Override
                    public void onSuccess(BaseResponse<com.paramount.bed.data.remote.response.ActivationEmailResponse> emailResponse) {
                        hideLoading();
                        if (emailResponse.isSucces()) {
                            btnRegister.setEnabled(true);
                            if (ValidationSNSModel.isSNS()) {
                                btnBack.setEnabled(true);
                                activity.EMAIL = etEmail.getText().toString().trim();
                                activity.PASSWORD = etPassword.getText().toString();

                                if (RegisterStep.getRegisterStepbyEmail(etEmail.getText().toString().trim()) == null) {
                                    RegisterStep.clear();
                                    RegisterStep registerStep = new RegisterStep();
                                    registerStep.setEmail(etEmail.getText().toString().trim());
                                    registerStep.insert();
                                }

                                activity.go(activity.FRAGMENT_TELEPHONE_INPUT);
                                isLoading = false;
                            } else {
                                DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage(emailResponse.getMessage()), LanguageProvider.getLanguage("UI000410C019"), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        btnBack.setEnabled(true);
                                        activity.EMAIL = etEmail.getText().toString().trim();
                                        activity.PASSWORD = etPassword.getText().toString();

                                        if (RegisterStep.getRegisterStepbyEmail(etEmail.getText().toString().trim()) == null) {
                                            RegisterStep.clear();
                                            RegisterStep registerStep = new RegisterStep();
                                            registerStep.setEmail(etEmail.getText().toString().trim());
                                            registerStep.insert();
                                        }

                                        activity.go(activity.FRAGMENT_TELEPHONE_INPUT);
                                        isLoading = false;
                                    }
                                });
                            }
                        } else {
//                            if (FeatureUtil.isProduction()) {
//                                DialogUtil.createSimpleOkDialog(activity, "",
//                                        LanguageProvider.getLanguage(emailResponse.getMessage()),
//                                        LanguageProvider.getLanguage("UI000410C019"),
//                                        ((dialogInterface, i) -> mailActivation(email)));
//                                return;
//                            }
                            //#1325
                            DialogUtil.createCustomYesNo(activity, "",
                                    LanguageProvider.getLanguage(emailResponse.getMessage()),
                                    LanguageProvider.getLanguage("UI000410C032"),
                                    (dialogInterface, i) -> {
                                        btnBack.setEnabled(true);
                                        btnRegister.setEnabled(true);
                                        isLoading = false;
                                        dialogInterface.dismiss();
                                    },
                                    LanguageProvider.getLanguage("UI000410C019"),
                                    (dialogInterface, i) -> mailActivation(email));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        btnBack.setEnabled(true);
                        btnRegister.setEnabled(true);
                        if (!NetworkUtil.isNetworkConnected(getContext())) {
                            DialogUtil.offlineDialog(activity, getContext());
                        } else if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(activity);
                        } else {
                            DialogUtil.serverFailed(activity, "UI000802C129", "UI000802C130", "UI000802C131", "UI000802C132");
                        }
                    }
                });


    }

    //#region General Function
    public void applyHint(PasswordPolicyModel passwordPolicyModel) {
        String strReplace = LanguageProvider.getLanguage("UI000410C006").replace("%MIN_LEN%", passwordPolicyModel.minLength)
                .replace("%MAX_LEN%", passwordPolicyModel.maxLength)
                .replace("%ALLOWED_SYMBOLS%", passwordPolicyModel.allowedSymbols);
        etPassword.setHint(strReplace);
        String strReplaceConfirm = LanguageProvider.getLanguage("UI000410C007").replace("%MIN_LEN%", passwordPolicyModel.minLength)
                .replace("%MAX_LEN%", passwordPolicyModel.maxLength)
                .replace("%ALLOWED_SYMBOLS%", passwordPolicyModel.allowedSymbols);
        etConfirmPassword.setHint(strReplaceConfirm);
        AutoSizeTextUtil.setAutoSizeHint(etPassword);
        AutoSizeTextUtil.setAutoSizeHint(etConfirmPassword);
    }
    //#endregion General Function

    //#region Local Validation
    private boolean isBadEmail(String email) {
        return ValidationUtils.EMAIL.isBad(email, new ValidationUtils.EMAIL.BadEmailListener() {
            @Override
            public void onEmailEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000410C026")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onEmailShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000410C028")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onEmailLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000410C029")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onEmailCharsWrong(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000410C021")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }

    private boolean isBadPassword(String password, String confirmPassword) {
        return ValidationUtils.PASSWORD.isBad(password, confirmPassword, new ValidationUtils.PASSWORD.BadPasswordListener() {
            @Override
            public void onPasswordEmpty() {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000410C027"));
            }

            @Override
            public void onPasswordNotMatch() {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000410C023"));
            }
        });
    }
    //#endregion Local Validation
}

