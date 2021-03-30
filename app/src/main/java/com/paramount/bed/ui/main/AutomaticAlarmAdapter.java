package com.paramount.bed.ui.main;

import android.app.Activity;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paramount.bed.R;
import com.suke.widget.SwitchButton;

import java.util.List;

public class AutomaticAlarmAdapter extends RecyclerView.Adapter<AutomaticAlarmAdapter.WifiViewHolder> {
    private List<AutomaticWakeOperationActivity.Alarm> dataset;
    Activity activity;

    public AutomaticAlarmAdapter(Activity activity, List<AutomaticWakeOperationActivity.Alarm> dataSet) {
        this.activity = activity;
        this.dataset = dataSet;
    }

    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_alarm_list, parent, false);
        WifiViewHolder vh = new WifiViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder holder, int position) {
        AutomaticWakeOperationActivity.Alarm data = dataset.get(position);

        TextView tvTime = (TextView) holder.view.findViewById(R.id.tvTime);
        TextView tvDay = (TextView) holder.view.findViewById(R.id.tvDay);
        ImageView imgPull = (ImageView) holder.view.findViewById(R.id.imgPull);
        SwitchButton sbActivate = (SwitchButton) holder.view.findViewById(R.id.sbActivate);

        tvDay.setText(dataset.get(position).day);


        if (position == 0) {
            tvDay.setTextColor(activity.getColor(R.color.red_pressed));
        }

        if (position == 6) {
            tvDay.setTextColor(Color.rgb(2, 188, 211));
        }

        tvTime.setText(data.time.toString("HH:mm"));

        if (data.active) {
            imgPull.setVisibility(View.VISIBLE);
            tvTime.setTextColor(activity.getColor(R.color.colorPrimaryDark));
            imgPull.setClickable(true);
            tvTime.setClickable(true);
            imgPull.setEnabled(true);
            tvTime.setEnabled(true);
        } else {
            imgPull.setVisibility(View.INVISIBLE);
            tvTime.setTextColor(activity.getColor(R.color.slight_blue));
            imgPull.setClickable(false);
            tvTime.setClickable(false);
            imgPull.setEnabled(false);
            tvTime.setEnabled(false);
        }
        tvTime.setOnClickListener((v) -> ((AutomaticWakeOperationActivity) activity).setTimeAlarm(position));
        imgPull.setOnClickListener((v) -> ((AutomaticWakeOperationActivity) activity).setTimeAlarm(position));
        sbActivate.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                ((AutomaticWakeOperationActivity) activity).setSettingAlarm(position, isChecked);
                ((AutomaticWakeOperationActivity) activity).setAlarm(position, isChecked);
                notifyDataSetChanged();
            }
        });


        sbActivate.setChecked(data.active);

        if (!isActivated) {
            tvTime.setEnabled(false);
            imgPull.setVisibility(View.INVISIBLE);
            tvTime.setTextColor(activity.getColor(R.color.slight_blue));
            sbActivate.setEnabled(false);
        }

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

    boolean isActivated = true;

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

}

