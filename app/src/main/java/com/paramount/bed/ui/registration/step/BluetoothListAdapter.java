package com.paramount.bed.ui.registration.step;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.util.DisplayUtils;

import java.util.ArrayList;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.BluetoothViewHolder>{
    private ArrayList<String> dataset;
    public BluetoothListAdapter(ArrayList<String> dataSet) {
        this.dataset = dataSet;
    }
    @NonNull
    @Override
    public BluetoothViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_bluetooth_list, parent, false);
        BluetoothViewHolder vh = new BluetoothViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothViewHolder holder, int position) {
        TextView wifiName = (TextView) holder.view.findViewById(R.id.wifiName);
        wifiName.setText(dataset.get(position));
        if(DisplayUtils.FONTS.bigFontStatus(holder.view.getContext())) {
            wifiName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class BluetoothViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public BluetoothViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}