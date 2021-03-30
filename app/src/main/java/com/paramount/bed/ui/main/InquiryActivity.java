package com.paramount.bed.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.paramount.bed.BuildConfig;
import com.paramount.bed.R;
import com.paramount.bed.data.model.InquiryContentModel;
import com.paramount.bed.data.model.InquiryProductModel;
import com.paramount.bed.data.model.InquiryTypeModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.response.BaseResponse;
import com.paramount.bed.ui.BaseActivity;
import com.paramount.bed.util.AndroidSystemUtil;
import com.paramount.bed.util.AutoSizeTextUtil;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.DisplayUtils;
import com.paramount.bed.util.EmojiUtils;
import com.paramount.bed.util.IntentConstant;
import com.paramount.bed.util.LogUserAction;
import com.paramount.bed.util.MultipleDeviceUtil;
import com.paramount.bed.util.NetworkUtil;
import com.paramount.bed.util.StringUtil;
import com.paramount.bed.util.ValidationUtils;
import com.paramount.bed.util.ViewUtil;
import com.paramount.bed.util.alarms.AlarmsQuizModule;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class InquiryActivity extends BaseActivity {
    //region Binding Component
    @BindView(R.id.etInquiryProduct)
    EditText etInquiryProduct;
    @BindView(R.id.etInquiryType)
    EditText etInquiryType;
    @BindView(R.id.etInquiry)
    EditText etInquiry;
    @BindView(R.id.textView1)
    TextView textContent;

    @BindView(R.id.tvInquiryType)
    TextView tvInquiryType;
    @BindView(R.id.tvProduct)
    TextView tvProduct;
    //endregion

    List<String> inquiryProductOptions = new ArrayList<>();
    List<String> inquiryTypeOptions = new ArrayList<>();

    List<InquiryTypeModel> inquiryTypeModels = new ArrayList<>();

    OptionsPickerView inquiryProductPicker;
    OptionsPickerView inquiryTypePicker;

    InquiryTypeModel inquiryTypeModel;
    InquiryProductModel inquiryProductModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setToolbarTitle(LanguageProvider.getLanguage("UI000780C001"));
        textContent.setText(InquiryContentModel.getFirst().getContent());
        getTextContent();
        etInquiryProduct.setFocusable(false);
        etInquiryProduct.setClickable(true);
        etInquiryProduct.setOnClickListener(onInquiryProductClick());
        etInquiryType.setFocusable(false);
        etInquiryType.setClickable(true);
        etInquiryType.setOnClickListener(onInquiryTypeClick());

        inquiryProductPicker = new OptionsPickerBuilder(this, onInquiryProductSelect())
                .setSelectOptions(0)
                .setCyclic(false, false, false)
                .setCancelText(LanguageProvider.getLanguage("UI000780C023"))
                .setSubmitText(LanguageProvider.getLanguage("UI000780C024"))
                .build();

        inquiryTypePicker = new OptionsPickerBuilder(this, onInquiryTypeSelect())
                .setSelectOptions(0)
                .setCyclic(false, false, false)
                .setCancelText(LanguageProvider.getLanguage("UI000780C025"))
                .setSubmitText(LanguageProvider.getLanguage("UI000780C026"))
                .build();

        inquiryTypeModel = new InquiryTypeModel();
        inquiryProductModel = new InquiryProductModel();

        if (BuildConfig.DEMO_MODE) {
            setDefaultInquiryProduct();
            setDefaultInquiryType();
            return;
        }
        showLoading();
        fetchInquiryProduct();
        if(DisplayUtils.FONTS.bigFontStatus(InquiryActivity.this)) {
            etInquiryType.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            etInquiryProduct.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvInquiryType.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            tvProduct.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        }
    }

    private void getTextContent() {
        mDisposable = inquiryService.getTextContent(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<String>>() {
                    @Override
                    public void onSuccess(BaseResponse<String> response) {
                        if (response.isSucces()) {
                            InquiryContentModel inquiryContentModel = new InquiryContentModel();
                            inquiryContentModel.setContent(response.getData());
                            inquiryContentModel.insert();
                            textContent.setText(response.getData());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(InquiryActivity.this);
                        }
                        textContent.setText(InquiryContentModel.getFirst().getContent());
                    }
                });
    }

    //region Layout Settings
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_inquiry;
    }

    @Override
    protected boolean useToolbar() {
        return true;
    }

    //endregion Layout Settings
    //region InquiryProduct
    private void setDefaultInquiryProduct() {
        inquiryProductOptions.clear();
        inquiryProductOptions.add("眠りSCAN");
        inquiryProductOptions.add("Next INTIME");
        inquiryProductOptions.add("Active Sleep Bed");
        inquiryProductOptions.add("Active Sleep Mattres");
        inquiryProductPicker.setPicker(inquiryProductOptions);
        etInquiryProduct.setText(LanguageProvider.getLanguage("UI000780C028"));

        AutoSizeTextUtil.setAutoSizeText(etInquiryProduct);
    }

    private OnOptionsSelectListener onInquiryProductSelect() {
        return ((options1, options2, options3, v) -> {
            etInquiryProduct.setText(inquiryProductOptions.get(options1));
            AutoSizeTextUtil.setAutoSizeText(etInquiryProduct);
        });
    }

    private View.OnClickListener onInquiryProductClick() {
        return (view -> {
            ViewUtil.hideKeyboardFrom(view.getContext(), view);
            inquiryProductPicker.show();
        });
    }

    private void fetchInquiryProduct() {
        mDisposable = inquiryService.getInquiryProduct(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> response) {
                        if (response.isSucces()) {
                            inquiryProductOptions.clear();
                            for (String item : response.getData()) {
                                inquiryProductOptions.add(item);
                            }
                            inquiryProductPicker.setPicker(inquiryProductOptions);
                            inquiryProductModel.clear();
                            for (int i = 0; i < response.getData().size(); i++) {
                                inquiryProductModel.setData(response.getData().get(i));
                                inquiryProductModel.insert();
                            }
                            onFetchDataSuccess();
                        }
                        fetchInquiryType();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(InquiryActivity.this);
                        }
                        routingInquiryProduct();
                        fetchInquiryType();
                    }
                });
    }

    private void routingInquiryProduct() {
        if (inquiryProductModel.getProduct() != null) {
            inquiryProductOptions.clear();
            ArrayList<InquiryProductModel> data = inquiryProductModel.getAll();
            for (int i = 0; i < data.size(); i++) {
                inquiryProductOptions.add(data.get(i).getData());
            }
            inquiryProductPicker.setPicker(inquiryProductOptions);
            etInquiryProduct.setText(LanguageProvider.getLanguage("UI000780C028"));
        } else {
            setDefaultInquiryProduct();
        }
    }

    //endregion InquiryProduct
    //region InquiryType
    private void setDefaultInquiryType() {
        inquiryTypeOptions.clear();
        inquiryTypeOptions.add("Category 1");
        inquiryTypeOptions.add("Category 2");
        inquiryTypeOptions.add("Category 3");
        inquiryTypePicker.setPicker(inquiryTypeOptions);
        etInquiryType.setText(LanguageProvider.getLanguage("UI000780C027"));
        AutoSizeTextUtil.setAutoSizeText(etInquiryType);
    }

    private OnOptionsSelectListener onInquiryTypeSelect() {
        return ((options1, options2, options3, v) -> {
            etInquiryType.setText(inquiryTypeOptions.get(options1));
            AutoSizeTextUtil.setAutoSizeText(etInquiryType);
        });
    }

    private View.OnClickListener onInquiryTypeClick() {
        return (view -> {
            ViewUtil.hideKeyboardFrom(view.getContext(), view);
            inquiryTypePicker.show();
        });
    }

    private void fetchInquiryType() {
        mDisposable = inquiryService.getInquiryType(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableSingleObserver<BaseResponse<List<InquiryTypeModel>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<InquiryTypeModel>> response) {
                        hideLoading();
                        if (response.isSucces()) {
                            inquiryTypeModels.clear();
                            inquiryTypeOptions.clear();
                            for (InquiryTypeModel type : response.getData()) {
                                inquiryTypeOptions.add(type.getLabel());
                            }
                            inquiryTypeModels.addAll(response.getData());
                            inquiryTypePicker.setPicker(inquiryTypeOptions);

                            InsertInquiryTypeToDatabase(response.getData());
                            onFetchDataSuccess();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (MultipleDeviceUtil.isTokenExpired(e)) {
                            DialogUtil.tokenExpireDialog(InquiryActivity.this);
                        }
                        hideLoading();

                        routingInquiryType();
                    }
                });
    }

    private void InsertInquiryTypeToDatabase(List<InquiryTypeModel> data) {
        InquiryTypeModel model = new InquiryTypeModel();
        model.clear();
        for (int i = 0; i < data.size(); i++) {
            model.setId(data.get(i).getId());
            model.setLabel(data.get(i).getLabel());
            model.insert();
        }
    }

    private void routingInquiryType() {
        if (inquiryTypeModel.getInquiry() != null) {
            ArrayList<InquiryTypeModel> data = inquiryTypeModel.getAll();
            for (int i = 0; i < data.size(); i++) {
                inquiryTypeOptions.add(data.get(i).getLabel());
            }
            inquiryTypeModels.addAll(data);
            inquiryTypePicker.setPicker(inquiryTypeOptions);
            etInquiryType.setText(LanguageProvider.getLanguage("UI000780C027"));
        } else {
            setDefaultInquiryType();
        }
    }

    private Integer getSelectedInquiryTypeId() {
        for (InquiryTypeModel item : inquiryTypeModels) {
            if (etInquiryType.getText().toString().equals(item.getLabel())) {
                return item.getId();
            }
        }
        return 0;
    }

    //endregion InquiryType
    private void onFetchDataSuccess() {
        if (inquiryProductOptions.size() > 0 && inquiryTypeOptions.size() > 0) {
            etInquiryProduct.setText(LanguageProvider.getLanguage("UI000780C028"));
            etInquiryType.setText(LanguageProvider.getLanguage("UI000780C027"));
        }
    }

    //region Action Event
    @OnClick(R.id.btnNext)
    void onNext() {
        if (etInquiryType.getText().toString().equals(LanguageProvider.getLanguage("UI000780C027"))) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000780C029"));
            return;
        }
        if (etInquiryProduct.getText().toString().equals(LanguageProvider.getLanguage("UI000780C028"))) {
            DialogUtil.createSimpleOkDialog(this, "", LanguageProvider.getLanguage("UI000780C030"));
            return;
        }
        if (isBadInquiry(etInquiry.getText().toString())) return;

        Intent intent = new Intent(this, InquiryPreviewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(IntentConstant.INQUIRY_TYPE_ID, getSelectedInquiryTypeId());
        intent.putExtra(IntentConstant.INQUIRY_TYPE, etInquiryType.getText().toString());
        intent.putExtra(IntentConstant.PRODUCT, etInquiryProduct.getText().toString());
        intent.putExtra(IntentConstant.INQUIRY, etInquiry.getText().toString());
        if (UserLogin.getUserLogin() == null || UserLogin.getUserLogin().getEmail() == null) {
            intent.putExtra(IntentConstant.EMAIL, "");
            intent.putExtra(IntentConstant.FIRST_NAME, "");
            intent.putExtra(IntentConstant.LAST_NAME, "");
            intent.putExtra(IntentConstant.NICK_NAME, "");
            intent.putExtra(IntentConstant.PHONE_NUMBER, "");

        } else {
            intent.putExtra(IntentConstant.EMAIL, UserLogin.getUserLogin().getEmail());
            intent.putExtra(IntentConstant.FIRST_NAME, UserLogin.getUserLogin().getNickname());
            intent.putExtra(IntentConstant.LAST_NAME, UserLogin.getUserLogin().getNickname());
            intent.putExtra(IntentConstant.NICK_NAME, UserLogin.getUserLogin().getNickname());
            intent.putExtra(IntentConstant.PHONE_NUMBER, UserLogin.getUserLogin().getPhoneNumber());

        }
        startActivity(intent);
    }
    //endregion

    @Override
    protected void onResume() {
        super.onResume();
        AlarmsQuizModule.run(this);
    }

    public boolean isBadInquiry(String inquiry) {
        inquiry = inquiry.replaceAll("\n", "").trim();
        return ValidationUtils.INQUIRY.isBad(inquiry, new ValidationUtils.INQUIRY.BadInquiryListener() {
            @Override
            public void onInquiryEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(InquiryActivity.this, "", LanguageProvider.getLanguage("UI000780C017")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onInquiryShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(InquiryActivity.this, "", LanguageProvider.getLanguage("UI000780C020")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onInquiryLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(InquiryActivity.this, "", LanguageProvider.getLanguage("UI000780C021")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onInquiryHasEmoji(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(InquiryActivity.this, "", LanguageProvider.getLanguage("UI000780C022")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }
}
