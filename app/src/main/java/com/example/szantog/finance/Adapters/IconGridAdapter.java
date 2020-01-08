package com.example.szantog.finance.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.szantog.finance.R;

public class IconGridAdapter extends BaseAdapter {

    private Context context;

    public static final int[] icons = {R.drawable.a_001_dog, R.drawable.a_002_carrots, R.drawable.a_003_trophy, R.drawable.a_004_tram,
            R.drawable.a_005_cutlery, R.drawable.a_006_shopping_cart, R.drawable.a_007_children, R.drawable.a_008_yoga,
            R.drawable.a_009_car, R.drawable.a_010_people, R.drawable.a_011_drug, R.drawable.a_012_jacket, R.drawable.a_013_piggybank,
            R.drawable.a_014_phone_call, R.drawable.a_015_airplane, R.drawable.a_016_groceries, R.drawable.a_017_luggage,
            R.drawable.a_018_clock, R.drawable.a_019_calendar, R.drawable.a_020_home, R.drawable.a_021_star,
            R.drawable.a_022_push_pin, R.drawable.a_023_settings, R.drawable.a_024_confetti, R.drawable.a_025_woman,
            R.drawable.a_026_disco_ball, R.drawable.a_027_credit_card, R.drawable.a_028_coins, R.drawable.a_029_get_money};

    public IconGridAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int i) {
        return icons[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(icons[i]);
        return imageView;
    }
}
