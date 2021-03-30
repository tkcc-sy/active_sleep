package com.paramount.bed.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ligl.android.widget.iosdialog.IOSDialog;
import com.paramount.bed.R;
import com.paramount.bed.data.provider.LanguageProvider;
import com.paramount.bed.ui.main.FaqActivity;

public class DialogUtilBackup {
    public static void createSimpleOkCancelDialog(Activity activity, String title, String message) {
        IOSDialog.Builder dialogBuilder = new IOSDialog.Builder(activity);

        dialogBuilder.setMessage(title);
        dialogBuilder.setTitle(message);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton(LanguageProvider.getLanguage("UI000802C003"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.show();
    }

    public static void createSimpleYesNoDialog(Activity activity, String title, String message) {
        IOSDialog.Builder dialogBuilder = new IOSDialog.Builder(activity);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.show();

    }

    public static void createYesNoDialog(Activity activity, String title, String message, DialogInterface.OnClickListener callback) {
        IOSDialog.Builder dialogBuilder = new IOSDialog.Builder(activity);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(message);
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton("YES", callback);

        dialogBuilder.setNegativeButton("NO", callback);
        dialogBuilder.show();

    }

    public static void createSimpleOkDialog(Activity activity, String title, String message) {
        IOSDialog.Builder dialogBuilder = new IOSDialog.Builder(activity);

        dialogBuilder.setMessage(message);
        dialogBuilder.setTitle(title);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(LanguageProvider.getLanguage("UI000802C003"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.show();
    }

    public static void createSimpleOkDialog(Activity activity, String title, String message, String okTitle, DialogInterface.OnClickListener callback) {
        IOSDialog.Builder dialogBuilder = new IOSDialog.Builder(activity);

        dialogBuilder.setMessage(message);
        dialogBuilder.setTitle(title);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(okTitle, callback);

        dialogBuilder.show();
    }

    public static void createCustomYesNo(Activity activity, String title, String message, String NoTitle, DialogInterface.OnClickListener callbackNo, String YesTitle, DialogInterface.OnClickListener callbackYes) {
        IOSDialog.Builder dialogBuilder = new IOSDialog.Builder(activity);

        dialogBuilder.setMessage(message);
        dialogBuilder.setTitle(title);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton(textBold(YesTitle), callbackYes);
        dialogBuilder.setNegativeButton(NoTitle, callbackNo);

        dialogBuilder.show();

    }

    public static Spanned textBold(String text) {
        String str = "<b>" + text + "</b>";
        Spanned txtBold = fromHtml(str);
        return txtBold;
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

    public static void showAlarmDialogue(Activity context) {
        try {
            IOSDialog.Builder dialogBuilder = new IOSDialog.Builder(context);

            dialogBuilder.setMessage("");
            dialogBuilder.setTitle("アラーム");
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.alarmlayout, null);
            dialogBuilder.setContentView(view);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("停止", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            dialogBuilder.show();
        } catch (Exception e) {

        }
    }

    public static void createSimpleOkDialogLink(Activity activity, String title, String message, String linkTitle, DialogInterface.OnClickListener linkCallback, String okTitle, DialogInterface.OnClickListener okCallback) {
        try {
            IOSDialog.Builder dialogBuilder = new IOSDialog.Builder(activity);
            dialogBuilder.setTitle(title);
            dialogBuilder.setMessage(message);
            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.dialoglink, null);
            TextView ilinkMessage = view.findViewById(R.id.message);
            ilinkMessage.setText(message);
            TextView ilinkTitle = view.findViewById(R.id.link);
            ilinkTitle.setText(linkTitle);

            dialogBuilder.setContentView(view);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton(okTitle, okCallback);

            IOSDialog dialog = dialogBuilder.show();
            ilinkTitle.setOnClickListener((view1 -> linkCallback.onClick(dialog, Dialog.BUTTON_NEUTRAL)));
        } catch (Exception e) {

        }
    }

    public static void offlineDialog(Activity activity, Context context) {
        DialogUtilBackup.createSimpleOkDialog(activity, "", LanguageProvider.getLanguage("UI000802C002"));
    }

    public static void serverFailed(Activity activity) {
        IosDialogCustom.Builder dialogBuilder = new IosDialogCustom.Builder(activity);

        dialogBuilder.setMessage(LanguageProvider.getLanguage("UI000802C001"));
        dialogBuilder.setTitle("");
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton(LanguageProvider.getLanguage("UI000802C003"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setNegativeButton("FAQ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(activity, FaqActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                dialog.dismiss();
            }
        });

        dialogBuilder.show();
    }

}
