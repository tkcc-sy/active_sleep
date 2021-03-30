package com.paramount.bed.ui.registration.step;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.paramount.bed.R;
import com.paramount.bed.data.model.RegisterStep;
import com.paramount.bed.data.model.RegisteringModel;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.data.remote.ApiClient;
import com.paramount.bed.ui.BLEFragment;
import com.paramount.bed.ui.datepicker.DateWheelBuilder;
import com.paramount.bed.ui.datepicker.DateWheelPicker;
import com.paramount.bed.ui.datepicker.OnDateWheelSelectListener;
import com.paramount.bed.ui.registration.RegistrationStepActivity;
import com.paramount.bed.util.DialogUtil;
import com.paramount.bed.util.EmojiUtils;
import com.paramount.bed.util.StringUtil;
import com.paramount.bed.util.ValidationUtils;
import com.paramount.bed.util.ViewUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AccountBioFragment extends BLEFragment {
    EditText etBirthday, etName;
    RadioGroup rgGender;
    RadioButton rbGender0;
    RadioButton rbGender1;
    RadioButton rbGender2;
    DateWheelPicker birthdayPicker;
    View view;
    private Button btnNext;
    private RegistrationStepActivity activity;
    String valNickname, valBirthday;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = (RegistrationStepActivity) getActivity();
        view = inflater.inflate(R.layout.fragment_registration_step_account_bio, container, false);
        btnNext = (Button) view.findViewById(R.id.btnNext);
        etBirthday = (EditText) view.findViewById(R.id.etBirthday);
        etName = (EditText) view.findViewById(R.id.etName);
        etName.setOnKeyListener(watchInput());

        rbGender0 = view.findViewById(R.id.radioButton);
        rbGender1 = view.findViewById(R.id.radioButton2);
        rbGender2 = view.findViewById(R.id.radioButton3);

        rgGender = (RadioGroup) view.findViewById(R.id.rgGender);

        rgGender.setOnCheckedChangeListener(((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.radioButton:
                    activity.GENDER = 1;
                    break;
                case R.id.radioButton2:
                    activity.GENDER = 2;
                    break;
                case R.id.radioButton3:
                    activity.GENDER = 3;
                    break;
            }
        }));

        if (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getNickName() != null) {
            etName.setText(StringUtil.nickName(RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getNickName()));
            etBirthday.setText(RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getBirthDate());

            switch (RegisterStep.getRegisterStepbyEmail(activity.EMAIL).getGender()) {
                case 1:
                    rgGender.check(R.id.radioButton);
                    break;
                case 2:
                    rgGender.check(R.id.radioButton2);
                    break;
                case 3:
                    rgGender.check(R.id.radioButton3);
                    break;
                default:
                    rgGender.check(R.id.radioButton);
                    break;
            }

        }
        etBirthday.setFocusable(false);
        etBirthday.setClickable(true);
        etBirthday.setOnClickListener(onBirthdayClick());
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(1975, 1, 1);

        birthdayPicker = new DateWheelBuilder(getActivity(), onBirthdaySelect())
                .setDate(selectedDate)
                .setCancelText(LanguageProvider.getLanguage("UI000440C018"))
                .setConfirmText(LanguageProvider.getLanguage("UI000440C019"))
                .build();

        btnNext.setOnClickListener(next());

        etName.setText(RegisteringModel.getProfile().getNickName());
        etBirthday.setText(RegisteringModel.getProfile().getBirthDay());
        switch (RegisteringModel.getProfile().getGender()) {
            case 1:
                rgGender.check(R.id.radioButton);
                break;
            case 2:
                rgGender.check(R.id.radioButton2);
                break;
            case 3:
                rgGender.check(R.id.radioButton3);
                break;
            default:
                rgGender.check(R.id.radioButton);
                break;
        }

        applyLocalization(view);
        return view;
    }

    private View.OnKeyListener watchInput() {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        };
    }


    private View.OnClickListener onBirthdayClick() {
        return (view1 -> {
            ViewUtil.hideKeyboardFrom(view.getContext(), view);
            birthdayPicker.show();
        });
    }

    private OnDateWheelSelectListener onBirthdaySelect() {
        return ((date, v) -> {
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            String outputDate = df.format(date);
            etBirthday.setText(outputDate);
        });
    }

    private View.OnClickListener next() {
        return (view1 -> {
//            ApiClient.LogData.setLogNickName(getActivity(), etName.getText().toString());
            valNickname = StringUtil.nickName(etName.getText().toString());
            valBirthday = etBirthday.getText().toString();
            if (isBadName(valNickname)) return;
            if (isBadBirthDay(valBirthday)) return;

            RegisteringModel.updateNickName(valNickname);
            RegisteringModel.updateBirthDay(valBirthday);
            RegisteringModel.updateGender(rbGender0.isChecked() ? 1 : rbGender1.isChecked() ? 2 : rbGender2.isChecked() ? 3 : 1);

            RegistrationStepActivity activity = (RegistrationStepActivity) getActivity();
            activity.NICK_NAME = StringUtil.nickName(etName.getText().toString());
            activity.BIRTH_DAY = etBirthday.getText().toString();

            RegisterStep registerStep = new RegisterStep();
            registerStep.setNickName(StringUtil.nickName(etName.getText().toString()));
            registerStep.setBirthDate(etBirthday.getText().toString());
            registerStep.setGender(activity.GENDER);
            registerStep.update(activity.EMAIL, 9);

            activity.go(activity.FRAGMENT_ACCOUNT_ADDRESS);
        });
    }

    public boolean isBadName(String name) {
        return ValidationUtils.NAME.isBad(name, new ValidationUtils.NAME.BadNameListener() {
            @Override
            public void onNameEmpty(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000440C011")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onNameShorter(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000440C012")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onNameLonger(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000440C022")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onNameHasEmoji(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000440C015")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }

            @Override
            public void onNameHasSpecialChars(ValidationUtils.ShortLongReplacer shortLongReplacer) {
                DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000440C015")
                        .replace(shortLongReplacer.keyShorterReplacer, String.valueOf(shortLongReplacer.shouldShortLength))
                        .replace(shortLongReplacer.keyLongerReplacer, String.valueOf(shortLongReplacer.shouldLongLength)));
            }
        });
    }

    public boolean isBadBirthDay(String birthDay) {
        if (birthDay.isEmpty()) {
            DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000440C013"));
            return true;
        }

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
        DateTime valBirthday = formatter.parseDateTime(birthDay);
        if (valBirthday.isAfterNow()) {
            DialogUtil.createSimpleOkDialog(getActivity(), "", LanguageProvider.getLanguage("UI000496C011"));
            return true;
        }
        return false;
    }

    private final static Set japaneseBlocks = new HashSet();

    static {
        japaneseBlocks.add(Character.UnicodeBlock.KATAKANA);
        japaneseBlocks.add(Character.UnicodeBlock.HIRAGANA);
        japaneseBlocks.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        japaneseBlocks.add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
    }

    public static boolean isAllJapanese(String input) {
        for (int i = 0, max = input.length(); i < max; i++) {
            char c = input.charAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
            if (!japaneseBlocks.contains(block))
                return false;
        }
        return true;
    }

}


