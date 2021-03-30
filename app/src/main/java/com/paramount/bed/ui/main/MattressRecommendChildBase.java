package com.paramount.bed.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.paramount.bed.data.model.MattressSettingModel;
import com.paramount.bed.ui.BaseV4Fragment;

import java.util.List;

import io.realm.RealmList;

public class MattressRecommendChildBase  extends BaseV4Fragment {

    protected boolean isInitialized = false;
    protected MattressSettingModel mattressSettingModel;
    public MattressRecommendChildBase() {
        // Required empty public constructor
    }

    protected void initUI(View view){
        isInitialized = true;
        applyLocalization(view);
        applyMHSValues();
    }

    @SuppressLint("SetTextI18n")
    protected void setMHSLabels(RealmList<Integer> values, List<TextView> segments){
        Activity activity = getActivity();
        if(values.size() == 6 && segments.size() == 6 && activity != null){
            activity.runOnUiThread(() -> {
                for (int i = 0; i < 6; i++) {
                    Integer value = values.get(i);
                    if(value != null){
                        segments.get(i).setText(value.toString());
                    }
                }
            });
        }
    }

    protected void disableMHSLabels(List<TextView> segments){
        Activity activity = getActivity();
        if (segments.size() == 6 && activity != null){
            activity.runOnUiThread(() -> {
                for (int i = 0; i < 6; i++) {
                    segments.get(i).setText("");
                }
            });
        }
    }

    protected void applyMHSValues(){
        //STUB
    }

    public void setMattressSettingModel(MattressSettingModel mattressSettingModel) {
        this.mattressSettingModel = mattressSettingModel;
        if(isInitialized) {
            applyMHSValues();
        }
    }
}