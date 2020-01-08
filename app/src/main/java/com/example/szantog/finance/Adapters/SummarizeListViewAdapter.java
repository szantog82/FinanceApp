package com.example.szantog.finance.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.szantog.finance.Fragments.CategorySumPairItem;
import com.example.szantog.finance.R;
import com.example.szantog.finance.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.03.20..
 */

public class SummarizeListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CategorySumPairItem> items;

    private JSONObject categoryIconObject;

    public SummarizeListViewAdapter(Context context, ArrayList<CategorySumPairItem> items) {
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
        return items.get(i).getCategory();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.summarize_listitem_layout, viewGroup, false);
        }
        ImageView icon = view.findViewById(R.id.summarize_listitem_icon);
        TextView category = view.findViewById(R.id.summarize_listitem_category);
        TextView sum = view.findViewById(R.id.summarize_listitem_sum);
        TextView percent = view.findViewById(R.id.summarize_listitem_percent);

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
        if (items.get(i).getCategory().length() == 0) {
            category.setText("(üres)");
        } else {
            category.setText(items.get(i).getCategory());
        }
        sum.setText(Tools.formatNumber(items.get(i).getSum()));
        percent.setText(String.format("%.2f", items.get(i).getPercent()) + " %");
        return view;
    }
}
