package com.paramount.bed.util;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

public class AutoSizeTextUtil {

    public static void resizeHint(EditText editText) {
        if (!isFitSize(editText)) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, editText.getTextSize() - 1);
            resizeHint(editText);
        } else {
            if (DisplayUtils.FONTS.bigFontStatus(editText.getContext())) {
                editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, editText.getTextSize() - 1);
            }
        }
    }

    public static Boolean isFitSize(EditText editText) {
        Rect bounds = new Rect();
        Paint textPaint = editText.getPaint();
        textPaint.getTextBounds(editText.getHint().toString(), 0, editText.getHint().toString().length(), bounds);
        int stringWidth = bounds.width();
        int containerWidth = editText.getWidth();
        System.out.println("TEXTSIZE : Content " + String.valueOf(stringWidth));
        System.out.println("TEXTSIZE : Container " + String.valueOf(containerWidth));
        if (stringWidth > containerWidth) {
            return false;
        }
        return true;
    }

    public static void setAutoSizeHint(final EditText editText) {
        float normalSizePX = editText.getTextSize();
        editText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                editText.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                final Layout layout = editText.getLayout();

                editText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        if (arg0.length() == 0) {
                            resizeHint(editText);
                        } else {
                            if (DisplayUtils.FONTS.bigFontStatus(editText.getContext())) {
                                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                return;
                            }
                            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSizePX);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        if (arg0.length() == 0) {
                            resizeHint(editText);
                        } else {
                            if (DisplayUtils.FONTS.bigFontStatus(editText.getContext())) {
                                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                return;
                            }
                            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSizePX);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence arg0, int start, int before,
                                              int count) {
                        if (arg0.length() == 0) {
                            resizeHint(editText);
                        } else {
                            if (DisplayUtils.FONTS.bigFontStatus(editText.getContext())) {
                                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                return;
                            }
                            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSizePX);
                        }
                    }
                });
                resizeHint(editText);
            }
        });
    }

    public static void setAutoSizeHintEditEmail(final EditText editText) {
        float normalSizePX = editText.getTextSize();
        editText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                editText.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                final Layout layout = editText.getLayout();

                editText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                        if (arg0.length() == 0) {
                            resizeHint(editText);
                        } else {
                            if (DisplayUtils.FONTS.bigFontStatus(editText.getContext())) {
                                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                                return;
                            }
                            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSizePX);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1,
                                                  int arg2, int arg3) {
                        // TODO Auto-generated method stub
                        if (arg0.length() == 0) {
                            resizeHint(editText);
                        } else {
                            if (DisplayUtils.FONTS.bigFontStatus(editText.getContext())) {
                                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                                return;
                            }
                            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSizePX);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence arg0, int start, int before,
                                              int count) {
                        if (arg0.length() == 0) {
                            resizeHint(editText);
                        } else {
                            if (DisplayUtils.FONTS.bigFontStatus(editText.getContext())) {
                                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                                return;
                            }
                            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, normalSizePX);
                        }
                    }
                });
                resizeHint(editText);
            }
        });
    }

    public static void resizeText(TextView textView) {
        if (!isFitSize(textView)) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textView.getTextSize() - 1);
            resizeText(textView);
        }
    }

    public static Boolean isFitSize(TextView textView) {
        Rect bounds = new Rect();
        Paint textPaint = textView.getPaint();
        textPaint.getTextBounds(textView.getText().toString(), 0, textView.getText().toString().length(), bounds);
        int stringWidth = bounds.width();
        int containerWidth = textView.getWidth();
        System.out.println("TEXTSIZE : Content " + String.valueOf(stringWidth));
        System.out.println("TEXTSIZE : Container " + String.valueOf(containerWidth));
        if (stringWidth > containerWidth) {
            return false;
        }
        return true;
    }

    public static void setAutoSizeText(final TextView textView) {
        float normalSizePX = textView.getTextSize();
        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                final Layout layout = textView.getLayout();
                resizeText(textView);
            }
        });
    }
}
