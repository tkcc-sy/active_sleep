package com.paramount.bed.ui.registration.step;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.paramount.bed.R;

import java.util.List;

public class PreviewListAdapter extends RecyclerView.Adapter<PreviewListAdapter.PreviewViewHolder>{
    private List<PreviewFragment.Preview> dataset;
    public PreviewListAdapter(List<PreviewFragment.Preview> dataSet) {
        this.dataset = dataSet;
    }
    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_preview_list, parent, false);
        PreviewViewHolder vh = new PreviewViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        TextView tvLabel = (TextView) holder.view.findViewById(R.id.tvLabel);
        TextView tvContent = (TextView) holder.view.findViewById(R.id.tvContent);
        PreviewFragment.Preview data = dataset.get(position);
        tvLabel.setText(data.label);

        if(data.content != null) {
            if(data.content.trim().length() == 0) {
                tvContent.setText("-");
            } else {
                tvContent.setText(data.content);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class PreviewViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public PreviewViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}
