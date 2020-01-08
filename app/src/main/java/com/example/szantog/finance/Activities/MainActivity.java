package com.example.szantog.finance.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.szantog.finance.Adapters.ViewPagerAdapter;
import com.example.szantog.finance.R;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabs;
    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = (TabLayout) findViewById(R.id.wrapper_tabs);
        //  tabs.setTabTextColors(ContextCompat.getColor(this, R.color.middleyellow), ContextCompat.getColor(this, R.color.lightyellow));
        // tabs.setBackgroundColor(ContextCompat.getColor(this, R.color.darkblue));
        pager = (ViewPager) findViewById(R.id.wrapper_pager);
        pager.setOffscreenPageLimit(1);
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }
}
