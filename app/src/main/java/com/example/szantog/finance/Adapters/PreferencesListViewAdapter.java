package com.example.szantog.finance.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.szantog.finance.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.03.19..
 */

public class PreferencesListViewAdapter extends BaseAdapter {

    public interface PreferencesDeleteItemListener {
        void onDelClicked(String selectedCategory);

        void onIconButtonClicked(String selectedCategory);
    }

    private Context context;
    private ArrayList<String> items;
    private Boolean[] isIncomeCategory;
    private PreferencesDeleteItemListener listener;

    private JSONObject categoryIconObject;

    public PreferencesListViewAdapter(Context context, ArrayList<String> items, JSONObject categoryIconObject, Boolean[] isIncomeCategory) {
        this.context = context;
        this.items = items;
        this.categoryIconObject = categoryIconObject;
        this.isIncomeCategory = isIncomeCategory;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.preferences_categories_listitem_layout, viewGroup, false);
        }
        ImageView iconImage = view.findViewById(R.id.preferences_categories_listitem_icon);
        if (isIncomeCategory[0]) {
            try {
                if (categoryIconObject.getJSONObject(context.getString(R.string.income)).getInt(items.get(i)) >= 0) {
                    iconImage.setImageResource(IconGridAdapter.icons[categoryIconObject.getJSONObject(context.getString(R.string.income)).getInt(items.get(i))]);
                } else {
                    iconImage.setImageResource(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (categoryIconObject.getJSONObject(context.getString(R.string.expenditure)).getInt(items.get(i)) >= 0) {
                    iconImage.setImageResource(IconGridAdapter.icons[categoryIconObject.getJSONObject(context.getString(R.string.expenditure)).getInt(items.get(i))]);
                } else {
                    iconImage.setImageResource(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        TextView textView = view.findViewById(R.id.preferences_categories_listitem_text);
        textView.setText(items.get(i));

        ImageView del_btn = view.findViewById(R.id.preferences_categories_listitem_del);
        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDelClicked(items.get(i));
                }
            }
        });

        Button add_icon_btn = view.findViewById(R.id.preferences_categories_listitem_addicon);
        add_icon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onIconButtonClicked(items.get(i));
                }
            }
        });


        return view;
    }

    public void setOnDeleteListener(PreferencesDeleteItemListener listener) {
        this.listener = listener;
    }

}
