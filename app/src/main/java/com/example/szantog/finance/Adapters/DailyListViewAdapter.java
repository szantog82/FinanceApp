package com.example.szantog.finance.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.R;
import com.example.szantog.finance.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.03.17..
 */

public class DailyListViewAdapter extends BaseAdapter {

    public interface DailyAdapterListener {
        void onEditClicked(EntryItem item);

        void onDeleteClicked(EntryItem item);
    }

    private Context context;
    private ArrayList<EntryItem> items;
    private ArrayList<Integer> selectedPosition;
    private ArrayList<Boolean> isRepetitives;
    private DailyAdapterListener listener;

    private JSONObject categoryIconObject;

    public DailyListViewAdapter(Context context, ArrayList<EntryItem> items, ArrayList<Integer> selectedPosition, ArrayList<Boolean> isRepetitives) {
        this.context = context;
        this.items = items;
        this.selectedPosition = selectedPosition;
        this.isRepetitives = isRepetitives;

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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.daily_listitem_layout, viewGroup, false);
        }

        ImageView icon = view.findViewById(R.id.daily_listitem_icon);
        ImageView repetitiveIcon = view.findViewById(R.id.daily_listitem_repetitive_icon);
        TextView category = view.findViewById(R.id.daily_listitem_category);
        TextView subCategory = view.findViewById(R.id.daily_listitem_subcategory);
        TextView sum = view.findViewById(R.id.daily_listitem_sum);
        ImageView editEntry = view.findViewById(R.id.daily_listitem_editentry);
        ImageView deleteEntry = view.findViewById(R.id.daily_listitem_deleteentry);

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

        if (isRepetitives.size() >= i + 1) {
            if (isRepetitives.get(i)) {
                repetitiveIcon.setVisibility(View.VISIBLE);
            } else {
                repetitiveIcon.setVisibility(View.GONE);
            }
        }

        category.setText(items.get(i).getCategory());
        subCategory.setText(items.get(i).getSubCategory());
        sum.setText(Tools.formatNumber(items.get(i).getSum()));
        RelativeLayout layout = view.findViewById(R.id.daily_listitem_rootlayout);
        if (selectedPosition.size() > 0 && selectedPosition.get(0) == i) {
            layout.setBackgroundResource(R.color.lighterblue);
            editEntry.setVisibility(View.VISIBLE);
            deleteEntry.setVisibility(View.VISIBLE);
            sum.setVisibility(View.GONE);
        } else {
            layout.setBackgroundColor(Color.WHITE);
            editEntry.setVisibility(View.GONE);
            deleteEntry.setVisibility(View.GONE);
            sum.setVisibility(View.VISIBLE);
        }

        if (items.get(i).getSum() < 0) {
            category.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
            subCategory.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
            sum.setTextColor(ContextCompat.getColor(context, R.color.darkRed));
        } else if (items.get(i).getSum() > 0) {
            category.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            subCategory.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            sum.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
        }

        editEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onEditClicked(items.get(i));
                }
            }
        });

        deleteEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDeleteClicked(items.get(i));
                }
            }
        });

        return view;
    }

    public void setListener(DailyAdapterListener listener) {
        this.listener = listener;
    }
}
