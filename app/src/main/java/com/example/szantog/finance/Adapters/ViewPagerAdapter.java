package com.example.szantog.finance.Adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.szantog.finance.Fragments.DailyFragment;
import com.example.szantog.finance.Fragments.DailyFragmentFake;
import com.example.szantog.finance.Fragments.HistoryFragment;
import com.example.szantog.finance.Fragments.HistoryFragmentFake;
import com.example.szantog.finance.Fragments.SummarizeFragment;
import com.example.szantog.finance.Fragments.SummarizeFragmentFake;
import com.example.szantog.finance.R;


public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Boolean isFakeMode;

    private DailyFragment dailyFragment;
    private HistoryFragment historyFragment;
    private SummarizeFragment summarizeFragment;

    private DailyFragmentFake dailyFragmentFake;
    private HistoryFragmentFake historyFragmentFake;
    private SummarizeFragmentFake summarizeFragmentFake;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.SHAREDPREF_MAINKEY), 0);
        if (sharedPrefs.getBoolean(context.getString(R.string.fake_data), true)) {
            isFakeMode = true;
            dailyFragmentFake = new DailyFragmentFake();
            historyFragmentFake = new HistoryFragmentFake();
            summarizeFragmentFake = new SummarizeFragmentFake();
        } else {
            isFakeMode = false;
            dailyFragment = new DailyFragment();
            historyFragment = new HistoryFragment();
            summarizeFragment = new SummarizeFragment();
        }
    }


    @Override
    public Fragment getItem(int position) {
        if (isFakeMode) {
            switch (position) {
                case 0:
                    return dailyFragmentFake;
                case 1:
                    return summarizeFragmentFake;
                case 2:
                    return historyFragmentFake;
                default:
                    return null;
            }
        } else {
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
