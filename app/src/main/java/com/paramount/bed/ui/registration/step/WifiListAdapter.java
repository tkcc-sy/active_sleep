package com.paramount.bed.ui.registration.step;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paramount.bed.R;

import java.util.ArrayList;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.WifiViewHolder>{
    private ArrayList<String> dataset;
    public WifiListAdapter(ArrayList<String> dataSet) {
        this.dataset = dataSet;
    }
    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_wifi_list, parent, false);
        WifiViewHolder vh = new WifiViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder holder, int position) {
        TextView wifiName = (TextView) holder.view.findViewById(R.id.wifiName);
        Log.d("shocking",dataset.get(position));
        wifiName.setText(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class WifiViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public WifiViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}
