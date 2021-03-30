package com.paramount.bed.util;

import android.util.Patterns;

import com.paramount.bed.data.model.FormPolicyModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {
    public static class LenReplacer {
        public int currentLength;
        public int shouldLength;
        public String keyReplacer;

        public LenReplacer(int currentLength, int shouldLength, String keyReplacer) {
            this.currentLength = currentLength;
            this.shouldLength = shouldLength;
            this.keyReplacer = keyReplacer;
        }

        public int getCurrentLength() {
            return currentLength;
        }

        public int getShouldLength() {
            return shouldLength;
        }

        public String getKeyReplacer() {
            return keyReplacer;
        }
    }

    public static class ShortLongReplacer {
        public int currentLength;
        public int shouldShortLength;
        public String keyShorterReplacer;
        public int shouldLongLength;
        public String keyLongerReplacer;

        public ShortLongReplacer(int currentLength, int shouldShortLength, String keyShorterReplacer, int shouldLongLength, String keyLongerReplacer) {
            this.currentLength = currentLength;
            this.shouldShortLength = shouldShortLength;
            this.keyShorterReplacer = keyShorterReplacer;
            this.shouldLongLength = shouldLongLength;
            this.keyLongerReplacer = keyLongerReplacer;
        }

        public int getCurrentLength() {
            return currentLength;
        }

        public int getShouldShortLength() {
            return shouldShortLength;
        }

        public String getKeyShorterReplacer() {
            return keyShorterReplacer;
        }

        public int getShouldLongLength() {
            return shouldLongLength;
        }

        public String getKeyLongerReplacer() {
            return keyLongerReplacer;
        }
    }

    public static Boolean containSpecialChars(String s) {
        final String regExSpecialChars = "<([{\\^-=$@!|]})?*+.>";
        final String regExSpecialCharsRE = regExSpecialChars.replaceAll(".", "\\\\$0");
        final Pattern reCharsREP = Pattern.compile("[" + regExSpecialCharsRE + "]");
        Matcher m = reCharsREP.matcher(s);
        return m.find();
    }

    public static class SSID {
        public static boolean isBad(String ssid, BadSSIDListener badSSIDListener) {
            ssid = ssid.trim();
            ShortLongReplacer shortLongReplacer = new ShortLongReplacer(ssid.length(), FormPolicyModel.getPolicy().getSsidMinLength(), "%MIN_LEN%", FormPolicyModel.getPolicy().getSsidMaxLength(), "%MAX_LEN%");
            if (ssid.isEmpty()) {
                badSSIDListener.onSSIDEmpty(shortLongReplacer);
                return true;
            }
            if (ssid.length() < FormPolicyModel.getPolicy().getSsidMinLength()) {
                badSSIDListener.onSSIDShorter(shortLongReplacer);
                return true;
            }
            if (ssid.length() > FormPolicyModel.getPolicy().getSsidMaxLength()) {
                badSSIDListener.onSSIDLonger(shortLongReplacer);
                return true;
            }
            return false;
        }

        public interface BadSSIDListener {
            void onSSIDEmpty(ShortLongReplacer shortLongReplacer);

            void onSSIDShorter(ShortLongReplacer shortLongReplacer);

            void onSSIDLonger(ShortLongReplacer shortLongReplacer);
        }
    }

    public static class SSID_PASSWORD {
        public static boolean isBad(String password, BadSSIDPasswordListener badSSIDPasswordListener) {
            ShortLongReplacer shortLongReplacer = new ShortLongReplacer(password.length(), FormPolicyModel.getPolicy().getSsidPassMinLength(), "%MIN_LEN%", FormPolicyModel.getPolicy().getSsidPassMaxLength(), "%MAX_LEN%");
            if (password.length() < FormPolicyModel.getPolicy().getSsidPassMinLength()) {
                badSSIDPasswordListener.onSSIDPasswordShorter(shortLongReplacer);
                return true;
            }
            if (password.length() > FormPolicyModel.getPolicy().getSsidPassMaxLength()) {
                badSSIDPasswordListener.onSSIDPasswordLonger(shortLongReplacer);
                return true;
            }
            return false;
        }

        public interface BadSSIDPasswordListener {
            void onSSIDPasswordShorter(ShortLongReplacer shortLongReplacer);

            void onSSIDPasswordLonger(ShortLongReplacer shortLongReplacer);
        }
    }

    public static class NAME {
        public static boolean isBad(String name, BadNameListener badNameListener) {
            name = name.trim();
            ShortLongReplacer shortLongReplacer = new ShortLongReplacer(name.length(), FormPolicyModel.getPolicy().getNicknameMinLength(), "%MIN_LEN%", FormPolicyModel.getPolicy().getNicknameMaxLength(), "%MAX_LEN%");
            if (name.isEmpty()) {
                badNameListener.onNameEmpty(shortLongReplacer);
                return true;
            }
            if (name.length() < FormPolicyModel.getPolicy().getNicknameMinLength()) {
                badNameListener.onNameShorter(shortLongReplacer);
                return true;
            }
            if (name.length() > FormPolicyModel.getPolicy().getNicknameMaxLength()) {
                badNameListener.onNameLonger(shortLongReplacer);
                return true;
            }
            if (EmojiUtils.containsEmoji(name)) {
                badNameListener.onNameHasEmoji(shortLongReplacer);
                return true;
            }
            if (Pattern.compile("[^a-zA-Z 0-9]+｜[^一-龠]+[^ぁ-ゔ]+[^ァ-ヴー]+|[-!@#$%^&*()_+|~=`{}\\[\\]:\";'<>?,.\\/]+").matcher(name).find()) {
                badNameListener.onNameHasSpecialChars(shortLongReplacer);
                return true;
            }
            return false;
        }

        public interface BadNameListener {
            void onNameEmpty(ShortLongReplacer shortLongReplacer);

            void onNameShorter(ShortLongReplacer shortLongReplacer);

            void onNameLonger(ShortLongReplacer shortLongReplacer);

            void onNameHasEmoji(ShortLongReplacer shortLongReplacer);

            void onNameHasSpecialChars(ShortLongReplacer shortLongReplacer);
        }
    }

    public static class INQUIRY {
        public static boolean isBad(String inquiry, BadInquiryListener badInquiryListener) {
            inquiry = inquiry.trim();
            ShortLongReplacer shortLongReplacer = new ShortLongReplacer(inquiry.length(), FormPolicyModel.getPolicy().getInquiryMinLength(), "%MIN_LEN%", FormPolicyModel.getPolicy().getInquiryMaxLength(), "%MAX_LEN%");
            if (inquiry.isEmpty()) {
                badInquiryListener.onInquiryEmpty(shortLongReplacer);
                return true;
            }
            if (inquiry.length() < FormPolicyModel.getPolicy().getInquiryMinLength()) {
                badInquiryListener.onInquiryShorter(shortLongReplacer);
                return true;
            }
            if (inquiry.length() > FormPolicyModel.getPolicy().getInquiryMaxLength()) {
                badInquiryListener.onInquiryLonger(shortLongReplacer);
                return true;
            }
            if (EmojiUtils.containsEmoji(inquiry)) {
                badInquiryListener.onInquiryHasEmoji(shortLongReplacer);
                return true;
            }
            return false;
        }

        public interface BadInquiryListener {
            void onInquiryEmpty(ShortLongReplacer shortLongReplacer);

            void onInquiryShorter(ShortLongReplacer shortLongReplacer);

            void onInquiryLonger(ShortLongReplacer shortLongReplacer);

            void onInquiryHasEmoji(ShortLongReplacer shortLongReplacer);
        }
    }

    public static class COMPANY {
        public static boolean isBad(String companyCode, BadCompanyListener badCompanyListener) {
            LenReplacer lenReplacer = new LenReplacer(companyCode.length(), FormPolicyModel.getPolicy().getCompanyCodeLength(), "%LEN%");
            if (companyCode.isEmpty()) {
                badCompanyListener.onCompanyEmpty(lenReplacer);
                return true;
            }
            if (companyCode.length() != FormPolicyModel.getPolicy().getCompanyCodeLength()) {
                badCompanyListener.onCompanyLengthWrong(lenReplacer);
                return true;
            }
            if (Pattern.compile("[^a-zA-Z0-9]").matcher(companyCode).find()) {
                badCompanyListener.onCompanyCharsWrong(lenReplacer);
                return true;
            }
            return false;
        }

        public interface BadCompanyListener {
            void onCompanyEmpty(LenReplacer lenReplacer);

            void onCompanyLengthWrong(LenReplacer lenReplacer);

            void onCompanyCharsWrong(LenReplacer lenReplacer);
        }
    }

    public static class EMAIL {
        public static boolean isBad(String email, BadEmailListener badEmailListener) {
            ShortLongReplacer shortLongReplacer = new ShortLongReplacer(email.length(), FormPolicyModel.getPolicy().getEmailMinLength(), "%MIN_LEN%", FormPolicyModel.getPolicy().getEmailMaxLength(), "%MAX_LEN%");
            if (email.isEmpty()) {
                badEmailListener.onEmailEmpty(shortLongReplacer);
                return true;
            }
            if (email.length() < FormPolicyModel.getPolicy().getEmailMinLength()) {
                badEmailListener.onEmailShorter(shortLongReplacer);
                return true;
            }
            if (email.length() > FormPolicyModel.getPolicy().getEmailMaxLength()) {
                badEmailListener.onEmailLonger(shortLongReplacer);
                return true;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                badEmailListener.onEmailCharsWrong(shortLongReplacer);
                return true;
            }
            return false;
        }

        public interface BadEmailListener {
            void onEmailEmpty(ShortLongReplacer shortLongReplacer);

            void onEmailShorter(ShortLongReplacer shortLongReplacer);

            void onEmailLonger(ShortLongReplacer shortLongReplacer);

            void onEmailCharsWrong(ShortLongReplacer shortLongReplacer);
        }
    }

    public static class PASSWORD {
        public static boolean isBad(String password, String confirmPassword, BadPasswordListener badPasswordListener) {
            password = password.trim();
            confirmPassword = confirmPassword.trim();
            if (password.isEmpty()) {
                badPasswordListener.onPasswordEmpty();
                return true;
            }
            if (!password.equals(confirmPassword)) {
                badPasswordListener.onPasswordNotMatch();
                return true;
            }
            return false;
        }

        public interface BadPasswordListener {
            void onPasswordEmpty();

            void onPasswordNotMatch();
        }
    }

    public static class PHONE {
        public static boolean isBad(String phoneNumber, BadPhoneListener badPHONEListener) {
            ShortLongReplacer shortLongReplacer = new ShortLongReplacer(phoneNumber.length(), FormPolicyModel.getPolicy().getPhoneNumberMinLength(), "%MIN_LEN%", FormPolicyModel.getPolicy().getPhoneNumberMaxLength(), "%MAX_LEN%");
            if (phoneNumber.isEmpty()) {
                badPHONEListener.onPhoneEmpty(shortLongReplacer);
                return true;
            }
            if (phoneNumber.length() < FormPolicyModel.getPolicy().getPhoneNumberMinLength()) {
                badPHONEListener.onPhoneShorter(shortLongReplacer);
                return true;
            }
            if (phoneNumber.length() > FormPolicyModel.getPolicy().getPhoneNumberMaxLength()) {
                badPHONEListener.onPhoneLonger(shortLongReplacer);
                return true;
            }
            if (!Pattern.compile("^\\+?[0-9. ()-]{10,25}$").matcher(phoneNumber).matches()) {
                badPHONEListener.onPhoneCharsWrong(shortLongReplacer);
                return true;
            }
            return false;
        }

        public interface BadPhoneListener {
            void onPhoneEmpty(ShortLongReplacer shortLongReplacer);

            void onPhoneShorter(ShortLongReplacer shortLongReplacer);

            void onPhoneLonger(ShortLongReplacer shortLongReplacer);

            void onPhoneCharsWrong(ShortLongReplacer shortLongReplacer);
        }
    }

    public static class PIN {
        public static boolean isBad(String pin, BadPINListener badPINListener) {
            LenReplacer lenReplacer = new LenReplacer(pin.length(), FormPolicyModel.getPolicy().getPinLength(), "%LEN%");
            if (pin.isEmpty()) {
                badPINListener.onPINEmpty(lenReplacer);
                return true;
            }
            if (pin.length() != FormPolicyModel.getPolicy().getPinLength()) {
                badPINListener.onPINLengthWrong(lenReplacer);
                return true;
            }
            return false;
        }

        public interface BadPINListener {
            void onPINEmpty(LenReplacer lenReplacer);

            void onPINLengthWrong(LenReplacer lenReplacer);
        }
    }


    public static class ZIP {
        public static boolean isBad(String zip, BadZIPListener badZIPListener) {
            LenReplacer lenReplacer = new LenReplacer(zip.length(), FormPolicyModel.getPolicy().getZipCodeLength(), "%LEN%");
            if (zip.length() != FormPolicyModel.getPolicy().getZipCodeLength()) {
                badZIPListener.onZIPLengthWrong(lenReplacer);
                return true;
            }
            return false;
        }

        public interface BadZIPListener {
            void onZIPLengthWrong(LenReplacer lenReplacer);
        }
    }

    public static class STRING {
        public static boolean isEmpty(String chars, StringEmptyListener stringEmptyListener) {
            chars = chars.trim();
            if (chars.isEmpty()) {
                stringEmptyListener.onStringEmpty();
                return true;
            }
            return false;
        }

        public interface StringEmptyListener {
            void onStringEmpty();
        }
    }
}
