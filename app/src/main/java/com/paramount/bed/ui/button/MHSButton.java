package com.paramount.bed.ui.button;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.paramount.bed.data.model.MHSModel;

public class MHSButton extends ToggleButton {
    public MHSModel associatedMHS;
    public MHSButton(Context context) {
        super(context);
    }

    public MHSButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
