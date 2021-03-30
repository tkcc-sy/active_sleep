package com.paramount.bed.ui.main;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paramount.bed.R;
import com.paramount.bed.data.model.MHSModel;
import com.paramount.bed.data.model.MattressSettingModel;
import com.paramount.bed.ui.BaseV4Fragment;

public class MatresRecommendFragment extends BaseV4Fragment implements MatresRecommendFragmentChild.MatressRecommendEventListener, MatresRecommendHistoryFragmentChild.MatressRecommendHistoryEventListener{

    MatresRecommendFragmentChild matresRecommendFragmentChild;
    MatresRecommendHistoryFragmentChild matresRecommendHistoryFragmentChild;
    MattressSettingModel mattressSettingModel;
    MHSModel selectedMHSModel;
    private boolean isHistoryShowing;
    public MattressRecommendEventListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_matress_recommend, container, false);

        matresRecommendFragmentChild = new MatresRecommendFragmentChild();
        matresRecommendFragmentChild.listener = this;
        matresRecommendFragmentChild.setMattressSettingModel(mattressSettingModel);

        matresRecommendHistoryFragmentChild = new MatresRecommendHistoryFragmentChild();
        matresRecommendHistoryFragmentChild.listener = this;
        matresRecommendHistoryFragmentChild.setMattressSettingModel(mattressSettingModel);

        if (matresRecommendFragmentChild != null) setupFragment(matresRecommendFragmentChild);

        return view;
    }

    private void setupFragment(Fragment fragment) {
        isHistoryShowing = fragment == matresRecommendHistoryFragmentChild;
        if(listener != null){
            if(isHistoryShowing) {
                listener.onHistoryShow();
            }else{
                listener.onHistoryHide();
            }
        }
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container_mattress_recommend, fragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onMattresRecommendHistoryTapped() {
        setupFragment(matresRecommendHistoryFragmentChild);
    }

    @Override
    public void onMattresRecommendHistoryCloseTapped() {
        setupFragment(matresRecommendFragmentChild);
    }

    @Override
    public void onMattressSelectionChanged(MHSModel newSelection) {
        selectedMHSModel = newSelection;
        if(listener != null){
            listener.onMattressMHSSelected(newSelection);
        }
    }

    public void setMattressSettingModel(MattressSettingModel mattressSettingModel) {
        this.mattressSettingModel = mattressSettingModel;

        if (matresRecommendFragmentChild != null){
            matresRecommendFragmentChild.setMattressSettingModel(mattressSettingModel);
        }
        if (matresRecommendHistoryFragmentChild != null){
            matresRecommendHistoryFragmentChild.setMattressSettingModel(mattressSettingModel);
        }
    }

    public void disableAllButton() {
        if (matresRecommendFragmentChild != null) {
            matresRecommendFragmentChild.disableAllButton();
        }
    }

    public void enableAllButton() {
        if (matresRecommendFragmentChild != null) {
            matresRecommendFragmentChild.enableAllButton();
        }
    }

    public void enableUI() {
        if (matresRecommendFragmentChild != null) {
            matresRecommendFragmentChild.enableUI();
        }
    }

    public void disableUI() {
        if (matresRecommendFragmentChild != null) {
            matresRecommendFragmentChild.disableUI();
        }
    }

    public void clearSelection() {
        selectedMHSModel = null;
        if (matresRecommendFragmentChild != null) matresRecommendFragmentChild.uncheckAllButtons();
    }

    public boolean isHistoryShowing() {
        return isHistoryShowing;
    }

    public interface MattressRecommendEventListener {
        void onMattressMHSSelected(MHSModel selectedMHS);
        void onHistoryShow();
        void onHistoryHide();

    }
}