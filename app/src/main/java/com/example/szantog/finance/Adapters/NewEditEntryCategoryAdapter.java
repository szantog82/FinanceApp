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

import com.example.szantog.finance.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.05.02..
 */

public class NewEditEntryCategoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> items;
    private int[] selectedItem;
    private Boolean[] isIncome;

    private JSONObject categoryIconObject;

    public NewEditEntryCategoryAdapter(Context context, ArrayList<String> items, int[] selectedItem, Boolean[] isIncome) {
        this.context = context;
        this.items = items;
        this.selectedItem = selectedItem;
        this.isIncome = isIncome;

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
            view = inflater.inflate(R.layout.new_edit_entry_griditem, viewGroup, false);
        }
        ImageView icon = view.findViewById(R.id.new_edit_entry_griditem_icon);
        TextView text = view.findViewById(R.id.new_edit_entry_griditem_text);
        ImageView signImage = view.findViewById(R.id.new_edit_entry_griditem_sign);
        text.setText(items.get(i));

        if (isIncome[0]) {
            try {
                if (categoryIconObject != null && categoryIconObject.getJSONObject(context.getString(R.string.income)).getInt(items.get(i)) >= 0) {
                    icon.setVisibility(View.VISIBLE);
                    icon.setImageResource(IconGridAdapter.icons[categoryIconObject.getJSONObject(context.getString(R.string.income)).getInt(items.get(i))]);
                } else {
                    icon.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (categoryIconObject != null && categoryIconObject.getJSONObject(context.getString(R.string.expenditure)).getInt(items.get(i)) >= 0) {
                    icon.setVisibility(View.VISIBLE);
                    icon.setImageResource(IconGridAdapter.icons[categoryIconObject.getJSONObject(context.getString(R.string.expenditure)).getInt(items.get(i))]);
                } else {
                    icon.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (selectedItem[0] == i) {
            text.setTextColor(ContextCompat.getColor(context, R.color.Red));
            signImage.setVisibility(View.VISIBLE);
        } else {
            text.setTextColor(ContextCompat.getColor(context, R.color.darkGray));
            signImage.setVisibility(View.GONE);
        }
        return view;
    }
}
