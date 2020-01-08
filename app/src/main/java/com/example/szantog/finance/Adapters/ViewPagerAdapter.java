package com.example.szantog.finance.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.szantog.finance.Fragments.DailyFragment;
import com.example.szantog.finance.Fragments.HistoryFragment;
import com.example.szantog.finance.Fragments.SummarizeFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private DailyFragment dailyFragment;
    private HistoryFragment historyFragment;
    private SummarizeFragment summarizeFragment;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        dailyFragment = new DailyFragment();
        historyFragment = new HistoryFragment();
        summarizeFragment = new SummarizeFragment();
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return dailyFragment;
            case 1:
                return summarizeFragment;
            case 2:
                return historyFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 0:
                title = "Egyenleg";
                break;
            case 1:
                title = "Áttekintő";
                break;
            case 2:
                title = "Történet";
                break;
            default:
                title = null;
        }
        return title;
    }
}
