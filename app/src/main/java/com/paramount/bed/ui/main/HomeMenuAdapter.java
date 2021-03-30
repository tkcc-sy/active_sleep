package com.paramount.bed.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paramount.bed.R;
import com.paramount.bed.util.DisplayUtils;

public class HomeMenuAdapter extends RecyclerView.Adapter<HomeMenuAdapter.HomeMenuHolder>{
    private String[] menuText;
    private int[] menuIcon;
    Context context;
    public HomeMenuAdapter(Context context, String[] menuText, int[] menuIcon) {
        this.context = context;
        this.menuText = menuText;
        this.menuIcon = menuIcon;
    }
    @NonNull
    @Override
    public HomeMenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_home_menu_list, parent, false);
        HomeMenuHolder vh = new HomeMenuHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeMenuHolder holder, int position) {
        TextView text = (TextView) holder.view.findViewById(R.id.text);
        ImageView icon = (ImageView) holder.view.findViewById(R.id.icon);
        View dividerTop = (View) holder.view.findViewById(R.id.dividerTop);
        if (position == 0) {
            dividerTop.setVisibility(View.VISIBLE);
        }
        text.setText(menuText[position]);
        if(DisplayUtils.FONTS.bigFontStatus(holder.view.getContext()) && position == 5) {
            text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        }
        icon.setImageDrawable(context.getDrawable(menuIcon[position]));
    }

    @Override
    public int getItemCount() {
        return menuText.length;
    }

    public static class HomeMenuHolder extends RecyclerView.ViewHolder {
        public View view;
        public HomeMenuHolder(View view) {
            super(view);
            this.view = view;
        }
    }
}

