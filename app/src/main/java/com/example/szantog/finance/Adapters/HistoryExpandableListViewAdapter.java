package com.example.szantog.finance.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.R;
import com.example.szantog.finance.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HistoryExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<String> groupItems = new ArrayList<>();
    private Map<Integer, ArrayList<EntryItem>> childItems = new HashMap<>();

    private Boolean isFakeMode;
    private Random random = new Random();

    private JSONObject categoryIconObject;

    public HistoryExpandableListViewAdapter(Context context, ArrayList<String> groupItems, Map<Integer, ArrayList<EntryItem>> childItems) {
        this.context = context;
        this.groupItems = groupItems;
        this.childItems = childItems;

        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.SHAREDPREF_MAINKEY), 0);
        try {
            categoryIconObject = new JSONObject(prefs.getString(context.getString(R.string.category_iconassociations_key), null));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (prefs.getBoolean(context.getString(R.string.fake_data), false)) {
            isFakeMode = true;
        } else {
            isFakeMode = false;
        }
    }

    @Override
    public int getGroupCount() {
        return groupItems.size();
    }

    @Override
    public int getChildrenCount(int i) {
        ArrayList<EntryItem> list = childItems.get(i);
        return list.size();
    }

    @Override
    public Object getGroup(int i) {
        return null;
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        }
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(groupItems.get(i));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.summarize_listitem_layout, parent, false);
        }
        ImageView icon = convertView.findViewById(R.id.summarize_listitem_icon);
        TextView category = convertView.findViewById(R.id.summarize_listitem_category);
        TextView sum = convertView.findViewById(R.id.summarize_listitem_sum);
        TextView percent = convertView.findViewById(R.id.summarize_listitem_percent);
/*
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
        percent.setText(String.format("%.2f", items.get(i).getPercent()) + " %");*/
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
