package com.paramount.bed.ui.front.slider;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.paramount.bed.data.model.SliderModel;

import java.util.List;

public class SliderFragmentAdapter extends FragmentStatePagerAdapter  implements LoopingPagerAdapter{

    List<SliderModel> mFrags;
    public SliderFragmentAdapter(FragmentManager fm, List<SliderModel> frags) {
        super(fm);
        mFrags = frags;
    }

    @Override
    public Fragment getItem(int position) {
        int index = position % mFrags.size();
        return SliderItemFragment.newInstance(mFrags.get(index));
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getRealCount() {
        return mFrags.size();
    }
}

