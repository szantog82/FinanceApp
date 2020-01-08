package com.example.szantog.finance.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.R;
import com.example.szantog.finance.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.03.16..
 */

public class HistoryListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<EntryItem> items;

    private JSONObject categoryIconObject;

    public HistoryListViewAdapter(Context context, ArrayList<EntryItem> items) {
        this.context = context;
        this.items = items;

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.SHAREDPREF_MAINKEY), 0);
        try {
            categoryIconObject = new JSONObject(prefs.getString(context.getString(R.string.category_iconassociations_key), null));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
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
            view = inflater.inflate(R.layout.history_listitem_layout, viewGroup, false);
        }
        ImageView icon = view.findViewById(R.id.history_listitem_icon);
        TextView date = view.findViewById(R.id.history_listitem_date);
        TextView category = view.findViewById(R.id.history_listitem_category);
        TextView subCategory = view.findViewById(R.id.history_listitem_subcategory);
        TextView sum = view.findViewById(R.id.history_listitem_sum);

        if (items.get(i).getSum() > 0) {
            try {
                if (categoryIconObject != null && categoryIconObject.getJSONObject(context.getString(R.string.income)).getInt(items.get(i).getCategory()) >= 0) {
                    icon.setVisibility(View.VISIBLE);
                    icon.setImageResource(IconGridAdapter.icons[categoryIconObject.getJSONObject(context.getString(R.string.income)).getInt(items.get(i).getCategory())]);
                } else {
                    icon.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (categoryIconObject != null && categoryIconObject.getJSONObject(context.getString(R.string.expenditure)).getInt(items.get(i).getCategory()) >= 0) {
                    icon.setVisibility(View.VISIBLE);
                    icon.setImageResource(IconGridAdapter.icons[categoryIconObject.getJSONObject(context.getString(R.string.expenditure)).getInt(items.get(i).getCategory())]);
                } else {
                    icon.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        date.setText(items.get(i).getStringDate());
        category.setText(items.get(i).getCategory());
        subCategory.setText(items.get(i).getSubCategory());
        sum.setText(Tools.formatNumber(items.get(i).getSum()));
        if (items.get(i).getSum() < 0) {
            category.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
            subCategory.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
            sum.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
        } else if (items.get(i).getSum() > 0) {
            category.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            subCategory.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            sum.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
        }
        return view;
    }
}
