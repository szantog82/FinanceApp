package com.example.szantog.finance.Fragments;

import android.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.szantog.finance.Activities.SQLQueryActivity;
import com.example.szantog.finance.Adapters.SummarizeListViewAdapter;
import com.example.szantog.finance.Database.FinanceDatabaseHandler;
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
 * Created by szantog on 2018.03.20..
 */

public class SummarizeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private FinanceDatabaseHandler db;
    private ArrayList<CategorySumPairItem> pairItems;

    private ListView summarizeListView;
    private SummarizeListViewAdapter adapter;

    private ArrayList<EntryItem> items = new ArrayList<>();
    private Boolean isMonthly = true;
    private TextView monthlyTextView;
    private TextView annualTextView;
    private TextView currentMonthText;
    private ImageButton prevMonthButton;
    private ImageButton nextMonthButton;
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
        return inflater.inflate(R.layout.history_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        monthlyTextView = view.findViewById(R.id.historyfragment_monthly);
        annualTextView = view.findViewById(R.id.historyfragment_annual);
        monthlyTextView.setOnClickListener(this);
        annualTextView.setOnClickListener(this);
        currentMonthText = view.findViewById(R.id.historyfragment_currentmonthtext);
        prevMonthButton = view.findViewById(R.id.historyfragment_prevmonth);
        nextMonthButton = view.findViewById(R.id.historyfragment_nextmonth);
        prevMonthButton.setOnClickListener(this);
        nextMonthButton.setOnClickListener(this);
        monthlyBalanceText = view.findViewById(R.id.historyfragment_monthlybalancetext);

        db = new FinanceDatabaseHandler(getActivity());
        pairItems = new ArrayList<>();
        summarizeListView = view.findViewById(R.id.history_listview);
        adapter = new SummarizeListViewAdapter(getActivity(), pairItems);
        summarizeListView.setAdapter(adapter);
        summarizeListView.setOnItemClickListener(this);
        updateFragment();
    }

    private void updateTable() {
        pairItems.clear();
        long totalSum = 0;
        for (EntryItem entryItem : items) {
            if (entryItem.getSum() > 0) {
                continue;
            }
            int index = -1;
            for (int i = 0; i < pairItems.size(); i++) {
                if (entryItem.getCategory().equals(pairItems.get(i).getCategory())) {
                    index = i;
                }
            }
            if (index > -1) {
                long sum = pairItems.get(index).getSum();
                pairItems.get(index).setSum(sum + entryItem.getSum());
            } else {
                pairItems.add(new CategorySumPairItem(entryItem.getCategory(), entryItem.getSum(), 0));
            }
            totalSum += entryItem.getSum();
        }
        for (int i = 0; i < pairItems.size(); i++) {
            pairItems.get(i).setPercent((float) pairItems.get(i).getSum() * 100f / (float) totalSum);
        }
        monthlyBalanceText.setText(Tools.formatNumber(totalSum));
        Collections.sort(pairItems);
        adapter.notifyDataSetChanged();
    }

    private void updateFragment() {
        Date dateSet = new Date(System.currentTimeMillis() + Long.valueOf(currentVisibleMonth) * 1000 * 3600 * 24 * 30);
        //currentVisibleDay is a negative integer!!!

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateSet);
        items.clear();
        if (isMonthly) {
            currentMonthText.setText(simpleDateFormatMonthly.format(dateSet));
            items.addAll(db.getCertainMonthlyData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)));
        } else {
            items.addAll(db.getCertainYearlyData(calendar.get(Calendar.YEAR)));
            currentMonthText.setText(simpleDateFormatAnnual.format(dateSet));
        }
        updateTable();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String category = (String) parent.getItemAtPosition(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ListView listView = new ListView(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMarginStart(10);
        params.setMarginEnd(10);
        listView.setLayoutParams(params);
        builder.setView(listView);
        Date dateSet = new Date(System.currentTimeMillis() + Long.valueOf(currentVisibleMonth) * 1000 * 3600 * 24 * 30);
        //currentVisibleDay is a negative integer!!!
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(dateSet);
        ArrayList<EntryItem> items1 = new ArrayList<>();
        if (isMonthly) {
            items1 = db.getCertainMonthlyDataCategoryDefined(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), category);
        } else {
            items1 = db.getCertainYearlyDataCategoryDefined(calendar1.get(Calendar.YEAR), category);
        }
        ArrayList<CategorySumPairItem> pairItems1 = new ArrayList<>();
        long totalSum = 0;
        for (EntryItem entryItem : items1) {
            if (entryItem.getSum() > 0) {
                continue;
            }
            int index = -1;
            for (int i = 0; i < pairItems1.size(); i++) {
                if (entryItem.getSubCategory().equals(pairItems1.get(i).getCategory())) {
                    index = i;
                }
            }
            if (index > -1) {
                long sum = pairItems1.get(index).getSum();
                pairItems1.get(index).setSum(sum + entryItem.getSum());
            } else {
                pairItems1.add(new CategorySumPairItem(entryItem.getSubCategory(), entryItem.getSum(), 0));
            }
            totalSum += entryItem.getSum();
        }
        for (int i = 0; i < pairItems1.size(); i++) {
            pairItems1.get(i).setPercent((float) pairItems1.get(i).getSum() * 100f / (float) totalSum);
        }
        Collections.sort(pairItems1);
        SummarizeListViewAdapter adapter1 = new SummarizeListViewAdapter(getActivity(), pairItems1);
        listView.setAdapter(adapter1);
        builder.setTitle("Összesítés erre: '" + category + "'");
        builder.show();
    }
}
