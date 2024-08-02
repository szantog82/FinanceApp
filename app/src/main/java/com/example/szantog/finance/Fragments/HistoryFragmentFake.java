package com.example.szantog.finance.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.szantog.finance.Activities.SQLQueryActivity;
import com.example.szantog.finance.Adapters.HistoryListViewAdapter;
import com.example.szantog.finance.FakeDataGenerator;
import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.R;
import com.example.szantog.finance.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Created by szantog on 2018.03.16..
 */

public class HistoryFragmentFake extends Fragment implements View.OnClickListener {

    private FakeDataGenerator fakeDataGenerator;

    private ListView historyListView;
    private HistoryListViewAdapter adapter;
    private ArrayList<EntryItem> currentEntryItems = new ArrayList<>();
    private ImageButton prevMonthButton;
    private ImageButton nextMonthButton;
    private Boolean isMonthly = true;
    private TextView monthlyTextView;
    private TextView annualTextView;
    private TextView currentMonthText;
    private TextView monthlyBalanceText;

    private int currentVisibleMonth = 0; //0 if this month (can never be positive)
    private SimpleDateFormat simpleDateFormatMonthly = new SimpleDateFormat("yyyy. MMMM", new Locale("HU"));
    private SimpleDateFormat simpleDateFormatAnnual = new SimpleDateFormat("yyyy.", new Locale("HU"));

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_custom_search) {
            Intent queryIntent = new Intent(getActivity(), SQLQueryActivity.class);
            startActivity(queryIntent);
            getActivity().finish();
        } else if (item.getItemId() == R.id.menu_quit) {
            getActivity().finish();
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.summarize_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fakeDataGenerator = new FakeDataGenerator();

        monthlyTextView = view.findViewById(R.id.historyfragment_monthly);
        annualTextView = view.findViewById(R.id.historyfragment_annual);
        monthlyTextView.setOnClickListener(this);
        annualTextView.setOnClickListener(this);

        historyListView = view.findViewById(R.id.history_listview);
        adapter = new HistoryListViewAdapter(getContext(), currentEntryItems);
        historyListView.setAdapter(adapter);
        currentMonthText = view.findViewById(R.id.historyfragment_currentmonthtext);
        monthlyBalanceText = view.findViewById(R.id.historyfragment_monthlybalancetext);

        prevMonthButton = view.findViewById(R.id.historyfragment_prevmonth);
        nextMonthButton = view.findViewById(R.id.historyfragment_nextmonth);
        prevMonthButton.setOnClickListener(this)
        ;
        nextMonthButton.setOnClickListener(this);

        updateFragment();
    }

    private void updateFragment() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        month += currentVisibleMonth; //currentVisibleDay is a negative integer!!!

        while (month < 0) {
            year--;
            month += 12;
        }
        calendar.set(year, month, 1);

        currentEntryItems.clear();
        ArrayList<EntryItem> items = null;
        if (isMonthly) {
            currentMonthText.setText(simpleDateFormatMonthly.format(calendar.getTimeInMillis()));
            items = fakeDataGenerator.getCertainMonthlyData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        } else {
            currentMonthText.setText(simpleDateFormatAnnual.format(calendar.getTimeInMillis()));
            items = fakeDataGenerator.getCertainYearlyData(calendar.get(Calendar.YEAR));
        }
        currentEntryItems.addAll(items);
        Collections.sort(currentEntryItems);
        long totalSum = 0;
        for (EntryItem item : items) {
            totalSum += item.getSum();
        }
        monthlyBalanceText.setText(Tools.formatNumber(totalSum));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.historyfragment_prevmonth:
                if (isMonthly) {
                    currentVisibleMonth--;
                } else {
                    currentVisibleMonth -= 12;
                }
                updateFragment();
                break;
            case R.id.historyfragment_nextmonth:
                if (isMonthly) {
                    if (currentVisibleMonth < 0) {
                        currentVisibleMonth++;
                    }
                } else {
                    currentVisibleMonth += 12;
                    if (currentVisibleMonth > 0) {
                        currentVisibleMonth = 0;
                    }
                }
                updateFragment();
                break;
            case R.id.historyfragment_monthly:
                isMonthly = true;
                monthlyTextView.setBackgroundResource(R.color.lighterGray);
                annualTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                currentVisibleMonth = 0;
                updateFragment();
                break;
            case R.id.historyfragment_annual:
                isMonthly = false;
                monthlyTextView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                annualTextView.setBackgroundResource(R.color.lighterGray);
                updateFragment();
                break;
            default:
                break;
        }
    }
}
