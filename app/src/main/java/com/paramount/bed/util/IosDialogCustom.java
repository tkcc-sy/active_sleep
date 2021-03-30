package com.paramount.bed.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.paramount.bed.R;

/**
 * @author itlgl
 */
public class IosDialogCustom extends Dialog {

    private static Context mContext;
    private static IosDialogCustom mIosDialog;

    public IosDialogCustom(Context context) {
        super(context, R.style.ios_dialog_style);
    }

    public static class Builder {
        private CharSequence mTitle;
        private CharSequence mMessage;
        private CharSequence mPositiveButtonText;
        private CharSequence mNegativeButtonText;
        private View mContentView;
        private OnClickListener mPositiveButtonClickListener;
        private OnClickListener mNegativeButtonClickListener;
        private boolean mCancelable = true;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(int titleId) {
            this.mTitle = mContext.getText(titleId);
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.mTitle = title;
            return this;
        }

        public Builder setMessage(int messageId) {
            this.mMessage = mContext.getText(messageId);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.mMessage = message;
            return this;
        }

        public Builder setPositiveButton(int textId, OnClickListener listener) {
            this.mPositiveButtonText = mContext.getText(textId);
            this.mPositiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            this.mPositiveButtonText = text;
            this.mPositiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int textId, OnClickListener listener) {
            this.mNegativeButtonText = mContext.getText(textId);
            this.mNegativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            this.mNegativeButtonText = text;
            this.mNegativeButtonClickListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.mCancelable = cancelable;
            return this;
        }

        public Builder setContentView(View contentView) {
            this.mContentView = contentView;
            return this;
        }

        public IosDialogCustom create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View dialogView = inflater.inflate(R.layout.ios_dialog_custom, null);
            mIosDialog = new IosDialogCustom(mContext);
            mIosDialog.setCancelable(mCancelable);

            TextView tvTitle = (TextView) dialogView.findViewById(R.id.title);
            TextView tvMessage = (TextView) dialogView.findViewById(R.id.message);
            Button btnCancel = (Button) dialogView.findViewById(R.id.cancel_btn);
            Button btnConfirm = (Button) dialogView.findViewById(R.id.confirm_btn);
            View horizontal_line = dialogView.findViewById(R.id.horizontal_line);
            View vertical_line = dialogView.findViewById(R.id.vertical_line);
            View btns_panel = dialogView.findViewById(R.id.btns_panel);

            // set title
            // fix #1,if title is null,set title visibility GONE
            if (TextUtils.isEmpty(mTitle)) {
                tvTitle.setVisibility(View.GONE);
            } else {
                tvTitle.setText(mTitle);
            }
            // set content view
            if (mContentView != null) {
                // if no message set add the contentView to the dialog body
                LinearLayout rl = (LinearLayout) dialogView
                        .findViewById(R.id.message_layout);
                rl.removeAllViews();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                rl.addView(mContentView, params);
            } else {
                tvMessage.setText(mMessage);
            }
            // set buttons
            if (mPositiveButtonText == null && mNegativeButtonText == null) {
                setPositiveButton(R.string.ios_dialog_default_ok, null);
                btnConfirm.setBackgroundResource(R.drawable.iosdialog_sigle_btn_selector);
                btnCancel.setVisibility(View.GONE);
                vertical_line.setVisibility(View.GONE);
            } else if (mPositiveButtonText != null && mNegativeButtonText == null) {
                btnConfirm.setBackgroundResource(R.drawable.iosdialog_sigle_btn_selector);
                btnCancel.setVisibility(View.GONE);
                vertical_line.setVisibility(View.GONE);
            } else if (mPositiveButtonText == null && mNegativeButtonText != null) {
                btnConfirm.setVisibility(View.GONE);
                btnCancel.setBackgroundResource(R.drawable.iossheet_middle_btn_selector);
                vertical_line.setVisibility(View.GONE);
            }
            if (mPositiveButtonText != null) {
                btnConfirm.setText(mPositiveButtonText);
                btnConfirm.setOnClickListener((view -> {
                    if (mPositiveButtonClickListener != null) {
                        mPositiveButtonClickListener.onClick(mIosDialog,
                                DialogInterface.BUTTON_POSITIVE);
                    }
                    mIosDialog.dismiss();
                }));
            }
            if (mNegativeButtonText != null) {
                btnCancel.setText(mNegativeButtonText);
                btnCancel.setOnClickListener((view -> {
                    if (mNegativeButtonClickListener != null) {
                        mNegativeButtonClickListener.onClick(mIosDialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                    mIosDialog.dismiss();
                }));
            }

            // 调整一下dialog的高度，如果高度充满屏幕不好看
            // Adjust the height of the dialog, if the height is full, the screen is not good.
            // 计算一下Dialog的高度,如果超过屏幕的4/5，则最大高度限制在4/5
            // Calculate the height of the Dialog. If it exceeds 4/5 of the screen, the maximum height is limited to 4/5.
            // 1.计算dialog的高度
            // 1. Calculate the height of the dialog
            // TODO 测试发现的问题：如果放入一大串没有换行的文本到message区域，会导致测量出来的高度偏小，从而导致实际显示出来dialog充满了整个屏幕
            // TODO The problem found in the test: If you put a large number of texts without line breaks into the message area, the measured height will be too small, which will cause the actual display dialog to fill the entire screen.
            dialogView.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int dialogHeight = dialogView.getMeasuredHeight();
            // 2.得到屏幕高度
            // 2. Get the screen height
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            int maxHeight = (int) (metrics.heightPixels * 0.8);
            ViewGroup.LayoutParams dialogParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // 3.如果高度超限，限制dialog的高度
            // 3. Limit height of dialog if height is exceeded
            if (dialogHeight >= maxHeight) {
                dialogParams.height = maxHeight;
            }
            mIosDialog.setContentView(dialogView, dialogParams);

            return mIosDialog;
        }

        public IosDialogCustom show() {
            if (mIosDialog == null || !mIosDialog.isShowing()) {
                IOSDialogRight.Builder.isDialogVisible = true;
                mIosDialog = create();

                mIosDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        IOSDialogRight.Builder.isDialogVisible = false;
                    }
                });
                mIosDialog.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        IOSDialogRight.Builder.isDialogVisible = false;
                    }
                });
                mIosDialog.show();
            }
            return mIosDialog;

//            mIosDialog = create();
//            mIosDialog.show();
//            return mIosDialog;
        }
    }
}
