package com.paramount.bed.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.paramount.bed.util.LogUtil.Logx;

public class CertificateUtil {
    public static void getHashKey(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo("com.paramount.bed", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Logx("CertificateUtil:AppHash", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }
    public void getGoogleSignInPlayKey(){
        // GOOGLE PLAY APP SIGNING SHA-1 KEY:- SHA1: BA:3B:44:DF:DA:B1:93:EE:B9:BF:1B:80:42:E6:19:49:64:69:90:BE
        byte[] sha1 = {
                (byte)0xBA, 0x3B, 0x44, (byte)0xDF, (byte)0xDA, (byte)0xB1, (byte)0x93, (byte)0xEE, (byte)0xB9, (byte)0xBF, (byte)0x1B, (byte)0x80, 0x42, (byte)0xE6, 0x19, (byte)0x49, 0x64, 0x69, (byte)0x90, (byte)0xBE
        };
        Logx("CertificateUtil:GooglePlaySignIn", Base64.encodeToString(sha1, Base64.NO_WRAP));
    }
}
