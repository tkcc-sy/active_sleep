package com.paramount.bed.ui.registration.step;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.paramount.bed.ui.registration.RegistrationStepActivity;

public class RegistrationFragment extends Fragment {
    RegistrationStepActivity parentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (RegistrationStepActivity)getActivity();
    }
}
