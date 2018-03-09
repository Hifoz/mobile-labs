package com.example.hifoz.lab4;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

// TODO make tabs have pretty icons
/**
 * Adapter for the tab pager
 */
class TabPagerAdapter extends FragmentPagerAdapter {
    private Fragment[] fragments;

    public TabPagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position){
        String[] tabnames = {
          "Chat", "Friends"
        };
        return tabnames[position];
    }

}
