package com.example.szantog.finance.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.szantog.finance.Database.FinanceDatabaseHandler;
import com.example.szantog.finance.Database.RepetitiveDatabaseHandler;
import com.example.szantog.finance.Models.RepetitiveItem;
import com.example.szantog.finance.R;
import com.example.szantog.finance.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by szantog on 2018.03.29..
 */

public class RepetitiveListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RepetitiveItem> items;
    private ArrayList<Integer> selectedPosition;

    private FinanceDatabaseHandler databaseHandler;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy. MM. dd.");

    public RepetitiveListViewAdapter(Context context, ArrayList<RepetitiveItem> items, ArrayList<Integer> selectedPosition) {
        this.context = context;
        this.items = items;
        this.selectedPosition = selectedPosition;
        databaseHandler = new FinanceDatabaseHandler(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.repetitive_listitem_layout, viewGroup, false);
        }
        TextView categoryText = view.findViewById(R.id.repetitive_listitem_category);
        TextView sumText = view.findViewById(R.id.repetitive_listitem_sum);
        TextView datesText = view.findViewById(R.id.repetitive_listitem_dates);
        TextView turnoverText = view.findViewById(R.id.repetitive_listitem_turnovermonth);
        TextView prevInstancesText = view.findViewById(R.id.repetitive_listitem_previousinstances);


        LinearLayout layout = view.findViewById(R.id.repetitive_listitem_rootlayout);
        if (selectedPosition.size() > 0 && selectedPosition.get(0) == i) {
            layout.setBackgroundResource(R.color.lighterblue);
        } else {
            layout.setBackgroundColor(Color.WHITE);
        }

        if (items.get(i).getSubCategory().length() > 0) {
            categoryText.setText(items.get(i).getCategory() + " - " + items.get(i).getSubCategory());
        } else {
            categoryText.setText(items.get(i).getCategory());
        }

        sumText.setText(Tools.formatNumber(items.get(i).getSum()));

        if (items.get(i).getEndTime() == 0) {
            datesText.setText(simpleDateFormat.format(new Date(items.get(i).getStartTime())) + " - (foly)");
        } else {
            datesText.setText(simpleDateFormat.format(new Date(items.get(i).getStartTime())) + " - " +
                    simpleDateFormat.format(new Date(items.get(i).getEndTime())));
        }
        turnoverText.setText(String.valueOf(items.get(i).getTurnoverMonth()) + " havonta");

        String[] prevInstances = items.get(i).getCollection().split(RepetitiveDatabaseHandler.DIVIDER);
        long sum = 0;
        for (String instance : prevInstances) {
            if (instance.length() > 0) {
                long time = Long.parseLong(instance);
                sum += databaseHandler.getSumByTime(time);
            }
        }

        prevInstancesText.setText(prevInstances.length + " előfordulás, " + Tools.formatNumber(sum));

        if (items.get(i).getSum() < 0) {
            categoryText.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
            sumText.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
            datesText.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
            turnoverText.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
            prevInstancesText.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
        } else {
            categoryText.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            sumText.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            datesText.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            turnoverText.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            prevInstancesText.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
        }

        return view;
    }
}
