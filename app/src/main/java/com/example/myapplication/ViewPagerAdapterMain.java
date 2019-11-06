package com.example.myapplication;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapterMain extends FragmentPagerAdapter {


    private final List<Fragment> fragmentListMain = new ArrayList<>();
    private final List<String> FragmentListTitlesMain = new ArrayList<>();

    public ViewPagerAdapterMain(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentListMain.get(position);
    }

    @Override
    public int getCount() {
        return FragmentListTitlesMain.size();
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return FragmentListTitlesMain.get(position);
    }

    public void AddFragmentMain(Fragment fragment, String title){
        fragmentListMain.add(fragment);
        FragmentListTitlesMain.add(title);
    }

}