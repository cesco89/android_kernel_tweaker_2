package com.dsht.kerneltweaker.adapters;

import com.dsht.kerneltweaker.fragments.PerAppApplicationsFragment;
import com.dsht.kerneltweaker.fragments.PerAppProfilesFragment;

import android.app.FragmentManager;
import android.app.Fragment;
import android.support.v13.app.FragmentPagerAdapter;

public class PerAppPagerAdapter extends FragmentPagerAdapter {

    private String[] TITLES;
    
    public PerAppPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.TITLES = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
        case 0:
            return new PerAppApplicationsFragment();
        case 1:
            return new PerAppProfilesFragment();
        }
        return null;
    }

}
