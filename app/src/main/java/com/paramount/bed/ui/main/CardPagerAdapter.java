package com.paramount.bed.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CardPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments;

    public CardPagerAdapter(FragmentManager fragmentManager, Fragment[] fragments) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

}
