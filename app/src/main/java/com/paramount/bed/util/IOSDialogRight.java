package com.paramount.bed.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ligl.android.widget.iosdialog.IOSDialog;
import com.paramount.bed.R;

public class IOSDialogRight extends IOSDialog {
    public IOSDialogRight(Context context) {
        super(context);
    }

    public static Context mContext;
    public static IOSDialog mIosDialog;

    public static void Dismiss() {
        try {
            mIosDialog.dismiss();
        } catch (Exception e) {

        }

    }

    public static Boolean getDialogVisibility() {
        if (Builder.isDialogVisible == null) {
            return false;
        }
        return Builder.isDialogVisible;
    }

    public static class Builder {
        public CharSequence mTitle;
        public CharSequence mMessage;
        public CharSequence mPositiveButtonText;
        public CharSequence mNegativeButtonText;
        public View mContentView;
        public OnClickListener mPositiveButtonClickListener;
        public OnClickListener mNegativeButtonClickListener;
        public boolean mCancelable = true;
        public static Boolean isDialogVisible = false;

        public IOSDialogRight.Builder getDialogue() {
            return this;
        }

        public Builder(Context context) {
            mContext = context;
        }

        public IOSDialogRight.Builder setTitle(int titleId) {
            mTitle = mContext.getText(titleId);
            return this;
        }

        public IOSDialogRight.Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public IOSDialogRight.Builder setMessage(int messageId) {
            mMessage = mContext.getText(messageId);
            return this;
        }

        public IOSDialogRight.Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public IOSDialogRight.Builder setPositiveButton(int textId, OnClickListener listener) {
            mPositiveButtonText = mContext.getText(textId);
            mPositiveButtonClickListener = listener;
            return this;
        }

        public IOSDialogRight.Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            mPositiveButtonText = text;
            mPositiveButtonClickListener = listener;
            return this;
        }

        public IOSDialogRight.Builder setNegativeButton(int textId, OnClickListener listener) {
            mNegativeButtonText = mContext.getText(textId);
            mNegativeButtonClickListener = listener;
            return this;
        }

        public IOSDialogRight.Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            mNegativeButtonText = text;
            mNegativeButtonClickListener = listener;
            return this;
        }

        public IOSDialogRight.Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public IOSDialogRight.Builder setContentView(View contentView) {
            mContentView = contentView;
            return this;
        }

        public IOSDialog create() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View dialogView = inflater.inflate(com.ligl.android.widget.iosdialog.R.layout.ios_dialog, null);
//            View dialogView = inflater.inflate(R.layout.ios_dialog_right, null);
            mIosDialog = new IOSDialog(mContext);
            mIosDialog.setCancelable(mCancelable);

            TextView tvTitle = (TextView) dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.title);
            TextView tvMessage = (TextView) dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.message);
            Button btnCancel = (Button) dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.cancel_btn);
            Button btnConfirm = (Button) dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.confirm_btn);

            if(DisplayUtils.FONTS.bigFontStatus(mContext)){
                btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                btnConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            View horizontal_line = dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.horizontal_line);
            View vertical_line = dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.vertical_line);
            View btns_panel = dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.btns_panel);
//            tvMessage.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
//            tvMessage.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE);
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
                        .findViewById(com.ligl.android.widget.iosdialog.R.id.message_layout);
                rl.removeAllViews();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
//                if(rl.getParent() != null) {
//                    ((ViewGroup)rl.getParent()).removeView(rl); // <- fix
//                }
                rl.addView(mContentView, params);
            } else {
                tvMessage.setText(mMessage);
            }
            setLayoutListner(tvMessage);
//            tvMessage.post(new Runnable() {
//                @Override
//                public void run() {
//                    int lineCount = tvMessage.getLineCount();
//                    setLayoutListner(tvMessage);
//
//                }
//            });

            // set buttons
            if (mPositiveButtonText == null && mNegativeButtonText == null) {
                setPositiveButton(com.ligl.android.widget.iosdialog.R.string.ios_dialog_default_ok, null);
                btnConfirm.setBackgroundResource(com.ligl.android.widget.iosdialog.R.drawable.iosdialog_sigle_btn_selector);
                btnCancel.setVisibility(View.GONE);
                vertical_line.setVisibility(View.GONE);
            } else if (mPositiveButtonText != null && mNegativeButtonText == null) {
                btnConfirm.setBackgroundResource(com.ligl.android.widget.iosdialog.R.drawable.iosdialog_sigle_btn_selector);
                btnCancel.setVisibility(View.GONE);
                vertical_line.setVisibility(View.GONE);
            } else if (mPositiveButtonText == null && mNegativeButtonText != null) {
                btnConfirm.setVisibility(View.GONE);
                btnCancel.setBackgroundResource(com.ligl.android.widget.iosdialog.R.drawable.iosdialog_sigle_btn_selector);
                vertical_line.setVisibility(View.GONE);
            }
            if (mPositiveButtonText != null) {
                btnConfirm.setText(mPositiveButtonText);
                btnConfirm.setOnClickListener((view) -> {
                    if (mPositiveButtonClickListener != null) {
                        mPositiveButtonClickListener.onClick(mIosDialog,
                                DialogInterface.BUTTON_POSITIVE);
                    }
                    mIosDialog.dismiss();
                });
            }
            if (mNegativeButtonText != null) {
                btnCancel.setText(mNegativeButtonText);
                btnCancel.setOnClickListener((view) -> {
                    if (mNegativeButtonClickListener != null) {
                        mNegativeButtonClickListener.onClick(mIosDialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                    mIosDialog.dismiss();
                });
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
            int maxWidth = (int) (metrics.widthPixels * 0.84);
            ViewGroup.LayoutParams dialogParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // 3.如果高度超限，限制dialog的高度
            // 3. Limit height of dialog if height is exceeded
            if (dialogHeight >= maxHeight) {
                dialogParams.height = maxHeight;
            }
//            dialogParams.width = maxWidth;
            mIosDialog.setContentView(dialogView, dialogParams);
            return mIosDialog;
        }
        public IOSDialog createFixedSize() {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View dialogView = inflater.inflate(R.layout.ios_dialog_fixed_size, null);
//            View dialogView = inflater.inflate(R.layout.ios_dialog_right, null);
            mIosDialog = new IOSDialog(mContext);
            mIosDialog.setCancelable(mCancelable);

            TextView tvTitle = (TextView) dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.title);
            TextView tvMessage = (TextView) dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.message);
            Button btnCancel = (Button) dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.cancel_btn);
            Button btnConfirm = (Button) dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.confirm_btn);

            if(DisplayUtils.FONTS.bigFontStatus(mContext)){
                btnCancel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                btnConfirm.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            }

            View horizontal_line = dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.horizontal_line);
            View vertical_line = dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.vertical_line);
            View btns_panel = dialogView.findViewById(com.ligl.android.widget.iosdialog.R.id.btns_panel);
//            tvMessage.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);
//            tvMessage.setHyphenationFrequency(Layout.HYPHENATION_FREQUENCY_NONE);
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
                        .findViewById(com.ligl.android.widget.iosdialog.R.id.message_layout);
                rl.removeAllViews();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
//                if(rl.getParent() != null) {
//                    ((ViewGroup)rl.getParent()).removeView(rl); // <- fix
//                }
                rl.addView(mContentView, params);
            } else {
                tvMessage.setText(mMessage);
            }
            setLayoutListner(tvMessage);
            // set buttons
            if (mPositiveButtonText == null && mNegativeButtonText == null) {
                setPositiveButton(com.ligl.android.widget.iosdialog.R.string.ios_dialog_default_ok, null);
                btnConfirm.setBackgroundResource(com.ligl.android.widget.iosdialog.R.drawable.iosdialog_sigle_btn_selector);
                btnCancel.setVisibility(View.GONE);
                vertical_line.setVisibility(View.GONE);
            } else if (mPositiveButtonText != null && mNegativeButtonText == null) {
                btnConfirm.setBackgroundResource(com.ligl.android.widget.iosdialog.R.drawable.iosdialog_sigle_btn_selector);
                btnCancel.setVisibility(View.GONE);
                vertical_line.setVisibility(View.GONE);
            } else if (mPositiveButtonText == null && mNegativeButtonText != null) {
                btnConfirm.setVisibility(View.GONE);
                btnCancel.setBackgroundResource(com.ligl.android.widget.iosdialog.R.drawable.iosdialog_sigle_btn_selector);
                vertical_line.setVisibility(View.GONE);
            }
            if (mPositiveButtonText != null) {
                btnConfirm.setText(mPositiveButtonText);
                btnConfirm.setOnClickListener((view) -> {
                    if (mPositiveButtonClickListener != null) {
                        mPositiveButtonClickListener.onClick(mIosDialog,
                                DialogInterface.BUTTON_POSITIVE);
                    }
                    mIosDialog.dismiss();
                });
            }
            if (mNegativeButtonText != null) {
                btnCancel.setText(mNegativeButtonText);
                btnCancel.setOnClickListener((view) -> {
                    if (mNegativeButtonClickListener != null) {
                        mNegativeButtonClickListener.onClick(mIosDialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                    mIosDialog.dismiss();
                });
            }

            dialogView.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int dialogHeight = dialogView.getMeasuredHeight();

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            int maxHeight = (int) (metrics.heightPixels * 0.8);
            ViewGroup.LayoutParams dialogParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if (dialogHeight >= maxHeight) {
                dialogParams.height = maxHeight;
            }
            mIosDialog.setContentView(dialogView, dialogParams);
            return mIosDialog;
        }

        public boolean isTooLarge(TextView text, String newText) {
            float textWidth = text.getPaint().measureText(newText);
            return (textWidth >= text.getMeasuredWidth());
        }

        private void setLayoutListner(final TextView textView) {
            textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    final Layout layout = textView.getLayout();

                    // Loop over all the lines and do whatever you need with
                    // the width of the line
                    for (int i = 0; i < layout.getLineCount(); i++) {
//                        int end = layout.getLineEnd(0);
//                        SpannableString content = new SpannableString(textView.getText().toString());
//                        content.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, end, 0);
//                        content.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), end, content.length(), 0);

                        String allText = textView.getText().toString();
                        System.out.println("Log Num Of Line dialogue : " + allText);
                        if (layout.getLineCount() > 1 && i == layout.getLineCount() - 1) {
                            int lineEnd = layout.getLineCount() - 1;
                            int lineBeforeEnd = lineEnd - 1;

                            int textLengthBefore = layout.getLineEnd(lineBeforeEnd) - layout.getLineStart(lineBeforeEnd);
                            int textLengthEnd = layout.getLineEnd(lineEnd) - layout.getLineStart(lineEnd);
                            if (textLengthEnd < 5) {
                                String startToSplit = allText.substring(0, layout.getLineEnd(lineBeforeEnd) - 5);
                                String SplitToEnd = allText.substring(layout.getLineEnd(lineBeforeEnd) - 5);
                                System.out.println("Log Num Of Line dialogue : " + startToSplit);
                                System.out.println("Log Num Of Line dialogue : " + SplitToEnd);
                                allText = startToSplit + "\n" + SplitToEnd;
                            }
                        }
                        //System.out.println("Log Num Of Line dialogue : " + textLength);
                        textView.setText(allText);
                    }
                }
            });
        }

        public IOSDialog show() {
            if (mIosDialog == null || !mIosDialog.isShowing()) {
                isDialogVisible = true;
                mIosDialog = create();

                mIosDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isDialogVisible = false;
                    }
                });
                mIosDialog.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        isDialogVisible = false;
                    }
                });
                try {
                    mIosDialog.show();
                } catch (Exception e) {

                }
            }
            return mIosDialog;
        }
        public IOSDialog showFixedSize() {
            if (mIosDialog == null || !mIosDialog.isShowing()) {
                isDialogVisible = true;
                mIosDialog = createFixedSize();

                mIosDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        isDialogVisible = false;
                    }
                });
                mIosDialog.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        isDialogVisible = false;
                    }
                });
                try {
                    mIosDialog.show();
                } catch (Exception e) {

                }
            }
            return mIosDialog;
        }
    }

}
