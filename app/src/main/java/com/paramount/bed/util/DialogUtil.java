package com.paramount.bed.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ligl.android.widget.iosdialog.IOSDialog;
import com.paramount.bed.R;
import com.paramount.bed.data.model.ForceLogoutModel;
import com.paramount.bed.data.model.StatusLogin;
import com.paramount.bed.data.model.TutorialShowModel;
import com.paramount.bed.data.model.UserLogin;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.login.LoginEmailActivity;
import com.paramount.bed.ui.main.FaqActivity;

public class DialogUtil {
    @SuppressLint("StaticFieldLeak")
    public static IOSDialogRight.Builder dialogBuilder;

    public static void createSimpleOkDialog(Activity activity, String title, String message) {
        if (!activity.isFinishing()) {
            dialogBuilder = new IOSDialogRight.Builder(activity);
            dialogBuilder.setMessage(message);
            dialogBuilder.setTitle(title);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton(LanguageProvider.getLanguage("UI000802C003"), (dialog, which) -> {
                IOSDialogRight.Builder.isDialogVisible = false;
                dialog.dismiss();
            });
            dialogBuilder.show();
        }
    }

    public static void createSimpleOkDialog(Context context, String title, String message, String okTitle, DialogInterface.OnClickListener callback) {
        if (context != null) {
            dialogBuilder = new IOSDialogRight.Builder(context);

            dialogBuilder.setMessage(message);
            dialogBuilder.setTitle(title);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton(okTitle, callback);

            dialogBuilder.show();
        }
    }

    public static void createCustomYesNo(Activity activity, String title, String message, String NoTitle, DialogInterface.OnClickListener callbackNo, String YesTitle, DialogInterface.OnClickListener callbackYes) {
        if (!activity.isFinishing()) {
            dialogBuilder = new IOSDialogRight.Builder(activity);

            dialogBuilder.setMessage(message);
            dialogBuilder.setTitle(title);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton(textBold(YesTitle), callbackYes);
            dialogBuilder.setNegativeButton(NoTitle, callbackNo);

            dialogBuilder.show();
        }

    }

    public static void createCustomYesNoFixedSize(Activity activity, String title, String message, String NoTitle, DialogInterface.OnClickListener callbackNo, String YesTitle, DialogInterface.OnClickListener callbackYes) {
        if (!activity.isFinishing()) {
            dialogBuilder = new IOSDialogRight.Builder(activity);

            dialogBuilder.setMessage(message);
            dialogBuilder.setTitle(title);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton(textBold(YesTitle), callbackYes);
            dialogBuilder.setNegativeButton(NoTitle, callbackNo);

            dialogBuilder.showFixedSize();
        }

    }

    private static Spanned textBold(String text) {
        String str = "<b>" + text + "</b>";
        return fromHtml(str);
    }

    private static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static void createSimpleOkDialogLink(Activity activity, String title, String message, String linkTitle, DialogInterface.OnClickListener linkCallback, String okTitle, DialogInterface.OnClickListener okCallback) {
        if (!activity.isFinishing()) {
            dialogBuilder = new IOSDialogRight.Builder(activity);
            dialogBuilder.setTitle(title);
            dialogBuilder.setMessage(message);
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) return;
            View view = layoutInflater.inflate(R.layout.dialoglink, null);
            TextView ilinkMessage = view.findViewById(R.id.message);
            ilinkMessage.setText(message);
            TextView ilinkTitle = view.findViewById(R.id.link);
            ilinkTitle.setText(linkTitle);

            dialogBuilder.setContentView(view);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton(okTitle, okCallback);

            IOSDialog dialog = dialogBuilder.show();
            ilinkTitle.setOnClickListener(view1 -> linkCallback.onClick(dialog, Dialog.BUTTON_NEUTRAL));
        }
    }

    public static void createSimpleOkDialogWithIcon(Activity activity, String title, String message, String okTitle, DialogInterface.OnClickListener okCallback) {
        if (!activity.isFinishing()) {
            dialogBuilder = new IOSDialogRight.Builder(activity);
            dialogBuilder.setTitle("");
            dialogBuilder.setMessage(message);
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) return;
            View view = layoutInflater.inflate(R.layout.dialogwithicon, null);
            TextView itxtTitle = view.findViewById(R.id.txtTitle);
            TextView ilinkMessage = view.findViewById(R.id.message);
            itxtTitle.setText(title);
            ilinkMessage.setText(message);
            itxtTitle.setVisibility(View.VISIBLE);
            if (title.isEmpty()) {
                itxtTitle.setVisibility(View.GONE);
            }
            dialogBuilder.setContentView(view);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton(okTitle, okCallback);

            dialogBuilder.show();
        }
    }

    public static void createYesNoDialogLink(Activity activity, String title, String message, String linkTitle, DialogInterface.OnClickListener linkCallback,
                                             String okTitle, DialogInterface.OnClickListener okCallback, String noTitle, DialogInterface.OnClickListener noCallback) {
        if (!activity.isFinishing()) {
            dialogBuilder = new IOSDialogRight.Builder(activity);
            dialogBuilder.setTitle(title);
            dialogBuilder.setMessage(message);
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null) return;
            View view = layoutInflater.inflate(R.layout.dialoglink, null);
            TextView ilinkMessage = view.findViewById(R.id.message);
            ilinkMessage.setText(message);
            TextView ilinkTitle = view.findViewById(R.id.link);
            ilinkTitle.setText(linkTitle);

            dialogBuilder.setContentView(view);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton(okTitle, okCallback);
            dialogBuilder.setNegativeButton(noTitle, noCallback);

            IOSDialog dialog = dialogBuilder.show();
            ilinkTitle.setOnClickListener(view1 -> linkCallback.onClick(dialog, Dialog.BUTTON_NEUTRAL));
        }
    }

    public static void offlineDialog(Activity activity, Context context) {
        //TODO : REMOVE THIS METHOD!
        DialogUtil.showOfflineDialog(activity);
    }

    public static void showOfflineDialog(Activity activity) {
        DialogUtil.showOfflineDialog(activity, null);
    }

    public static void showOfflineDialog(Activity activity, DialogInterface.OnClickListener callback) {
        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          if (!activity.isFinishing()) {
                                              DialogUtil.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage("UI000802C002"),
                                                      LanguageProvider.getLanguage("UI000802C003"), callback);
                                          }
                                      }
                                  }
                , 200);
    }

    public static void serverFailed(Activity activity, String message, String FAQText, String okButton, String idFaq) {
        if (!activity.isFinishing()) {
            IosDialogCustom.Builder dialogBuilder = new IosDialogCustom.Builder(activity);
            message = message.isEmpty() ? "UI000802C077" : message;
            okButton = okButton.isEmpty() ? "UI000802C079" : okButton;
            FAQText = FAQText.isEmpty() ? "UI000802C078" : FAQText;

            idFaq = FAQText;

            dialogBuilder.setMessage(LanguageProvider.getLanguage(message));
            dialogBuilder.setTitle("");
            dialogBuilder.setCancelable(true);

            dialogBuilder.setPositiveButton(LanguageProvider.getLanguage(okButton), (dialog, which) -> {
                IOSDialogRight.Builder.isDialogVisible = false;
                dialog.dismiss();
            });
            String finalIdFaq = idFaq;
            dialogBuilder.setNegativeButton(LanguageProvider.getLanguage(FAQText), (dialog, which) -> {
                Intent intent = new Intent(activity, FaqActivity.class);
                intent.putExtra("ID_FAQ", finalIdFaq);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                IOSDialogRight.Builder.isDialogVisible = false;
                dialog.dismiss();
            });

            dialogBuilder.show();
        }
    }

    public static void serverFailed(Activity activity, String message, String FAQText, String okButton, String idFaq,DialogUtilListener callback) {
        if (!activity.isFinishing()) {
            IosDialogCustom.Builder dialogBuilder = new IosDialogCustom.Builder(activity);
            message = message.isEmpty() ? "UI000802C077" : message;
            okButton = okButton.isEmpty() ? "UI000802C079" : okButton;
            FAQText = FAQText.isEmpty() ? "UI000802C078" : FAQText;

            idFaq = FAQText;

            dialogBuilder.setMessage(LanguageProvider.getLanguage(message));
            dialogBuilder.setTitle("");
            dialogBuilder.setCancelable(true);

            dialogBuilder.setPositiveButton(LanguageProvider.getLanguage(okButton), (dialog, which) -> {
                IOSDialogRight.Builder.isDialogVisible = false;
                dialog.dismiss();
                callback.onDismiss();
            });
            String finalIdFaq = idFaq;
            dialogBuilder.setNegativeButton(LanguageProvider.getLanguage(FAQText), (dialog, which) -> {
                Intent intent = new Intent(activity, FaqActivity.class);
                intent.putExtra("ID_FAQ", finalIdFaq);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                IOSDialogRight.Builder.isDialogVisible = false;
                dialog.dismiss();
                callback.onDismiss();
            });

            dialogBuilder.show();
        }
    }

    public static void tokenExpireDialog(Context context) {
        DialogUtil.createSimpleOkDialog(context, "", LanguageProvider.getLanguage("UI000802C022"), LanguageProvider.getLanguage("UI000802C023"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                IOSDialogRight.Builder.isDialogVisible = false;
                dialogInterface.dismiss();
                //Reset Data
                DataBaseUtil.wipeUserData(context);
                UserLogin.logout();

                //Set Status Login to False
                StatusLogin.clear();
                StatusLogin statusLogin = new StatusLogin();
                statusLogin.statusLogin = false;
                statusLogin.insert();

                //Dismiss All Notification If Any
                NotificationManager notificationManager = (NotificationManager) context.getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();

                //Open Activity Login
                Intent intent = new Intent(context, LoginEmailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("fromLogin", false);
                context.startActivity(intent);
            }
        });
    }

    public interface DialogUtilListener {
        void onDismiss();
    }
}
