package com.paramount.bed.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.paramount.bed.data.remote.response.NewsResponse;
import com.paramount.bed.ui.front.slider.LoopingPagerAdapter;

import java.util.List;

public class SliderNewsFragmentAdapter extends FragmentStatePagerAdapter implements LoopingPagerAdapter {

    List<NewsResponse> mFrags;
    public SliderNewsFragmentAdapter(FragmentManager fm, List<NewsResponse> frags) {
        super(fm);
        mFrags = frags;
    }

    @Override
    public Fragment getItem(int position) {
        return SliderItemNewsFragment.newInstance(mFrags.get(position),mFrags.get(position).key);
    }

    @Override
    public int getCount() {
        //return Integer.MAX_VALUE;
        if(mFrags!=null)return mFrags.size();
        else return 0;
    }

    @Override
    public int getRealCount() {
        return mFrags.size();
    }
}

/*interface LoopingPagerAdapter {
    int getRealCount();
}*/

