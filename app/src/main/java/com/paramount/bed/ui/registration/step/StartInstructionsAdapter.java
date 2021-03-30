package com.paramount.bed.ui.registration.step;

import android.content.Context;
import androidx.annotation.NonNull;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.util.DisplayUtils;


public class StartInstructionsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final String[] values;
    public StartInstructionsAdapter(@NonNull Context context, String[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.layout_start_instruction, parent, false);

        TextView  tvNumber = (TextView) rowView.findViewById(R.id.tvNumber);
        TextView  tvText = (TextView) rowView.findViewById(R.id.tvText);

        String number = (position+1) + "";
        tvNumber.setText(number);
        tvText.setText(values[position]);
        if(DisplayUtils.FONTS.bigFontStatus(getContext())) {
            tvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        } else {
            tvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
//        tvText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                tvText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//
//                final Layout layout = tvText.getLayout();
//                layout.getLineCount();
//                System.out.println();
//
//            }
//        });
        return rowView;
    }
}
