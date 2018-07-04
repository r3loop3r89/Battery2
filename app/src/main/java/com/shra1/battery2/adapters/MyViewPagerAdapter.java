package com.shra1.battery2.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class MyViewPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> l;
    public MyViewPagerAdapter(FragmentManager fm, List<Fragment> l) {
        super(fm);
        this.l=l;
    }

    @Override
    public Fragment getItem(int position) {
        return l.get(position);
    }

    @Override
    public int getCount() {
        return l.size();
    }
}
