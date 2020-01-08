package com.example.szantog.finance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.szantog.finance.Models.SubCategoryListItem;
import com.example.szantog.finance.R;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.03.26..
 */

public class SubCategoryListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SubCategoryListItem> items;

    public SubCategoryListAdapter(Context context, ArrayList<SubCategoryListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i).getName();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.spinner_item_layout, viewGroup, false);
        }
        TextView tv = view.findViewById(R.id.spinner_item_textview);
        tv.setText(items.get(i).getName() + " (" + String.valueOf(items.get(i).getCount()) + ")");
        return view;
    }
}
