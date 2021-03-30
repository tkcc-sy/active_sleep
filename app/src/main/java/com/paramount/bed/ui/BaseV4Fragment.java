package com.paramount.bed.ui;


import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bigkoo.svprogresshud.listener.OnDismissListener;
import com.paramount.bed.data.provider.LanguageProvider;

import java.util.ArrayList;

public class BaseV4Fragment extends Fragment {

    public void applyLocalization() {
        ViewGroup viewGroup = (ViewGroup) ((ViewGroup) getView());
        ArrayList<View> allChild = getAllChildren(viewGroup);
        for (View child : allChild
        ) {
            if (child instanceof ToggleButton) {
                String textOn = ((ToggleButton) child).getTextOn().toString();
                String textOff = ((ToggleButton) child).getTextOff().toString();
                String content = LanguageProvider.getLanguage(textOn);
                if (content != null && content != "")
                    ((ToggleButton) child).setTextOn(content);
                content = LanguageProvider.getLanguage(textOff);
                if (content != null && content != "")
                    ((ToggleButton) child).setTextOff(content);
            } else if (child instanceof EditText) {
                String tag = ((EditText) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((EditText) child).setText(content);
                if (((EditText) child).getHint() != null) {
                    tag = ((EditText) child).getHint().toString();
                    content = LanguageProvider.getLanguage(tag);
                    if (content != null && content != "")
                        ((EditText) child).setHint(content);
                }
            } else if (child instanceof CheckBox) {
                String tag = ((CheckBox) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((CheckBox) child).setText(content);
            } else if (child instanceof TextView) {
                String tag = ((TextView) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((TextView) child).setText(content);
            } else if (child instanceof Button) {
                String tag = ((Button) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((Button) child).setText(content);
            }
        }
    }

    public void applyLocalization(View rootView) {
        ArrayList<View> allChild = getAllChildren(rootView);
        for (View child : allChild
        ) {
            if (child instanceof ToggleButton) {
                String textOn = ((ToggleButton) child).getTextOn().toString();
                String textOff = ((ToggleButton) child).getTextOff().toString();
                String content = LanguageProvider.getLanguage(textOn);
                ((ToggleButton) child).setText(content);
                if (content != null && content != "")
                    ((ToggleButton) child).setTextOn(content);
                content = LanguageProvider.getLanguage(textOff);
                if (content != null && content != "")
                    ((ToggleButton) child).setTextOff(content);
            } else if (child instanceof EditText) {
                String tag = ((EditText) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((EditText) child).setText(content);
                if (((EditText) child).getHint() != null) {
                    tag = ((EditText) child).getHint().toString();
                    content = LanguageProvider.getLanguage(tag);
                    if (content != null && content != "")
                        ((EditText) child).setHint(content);
                }
            } else if (child instanceof CheckBox) {
                String tag = ((CheckBox) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((CheckBox) child).setText(content);
            } else if (child instanceof TextView) {
                String tag = ((TextView) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((TextView) child).setText(content);
            } else if (child instanceof Button) {
                String tag = ((Button) child).getText().toString();
                String content = LanguageProvider.getLanguage(tag);
                if (content != null && content != "")
                    ((Button) child).setText(content);
            }
        }
    }

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    static SVProgressHUD progressDialog = null;

    public void showLoading() {
        BaseActivity.isLoading = true;
        progressDialog = new SVProgressHUD(getContext());
        progressDialog.show();
        progressDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(SVProgressHUD hud) {
                BaseActivity.isLoading = false;
            }
        });
    }

    public void hideLoading() {
        BaseActivity.isLoading = false;
        if (progressDialog != null) progressDialog.dismissImmediately();
    }
}
