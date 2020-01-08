package com.example.szantog.finance;

import com.example.szantog.finance.Models.EntryItem;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by szantog on 2018.03.16..
 */

public class Tools {

    public static final String SUBCATEGORYLIST_KEY = "subcategorylist_key";
    public static final String DIVIDER = "__div__";

    private static DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    public static String formatNumber(long number) {
        return decimalFormat.format(number) + " Ft";
    }

    public static String calculateBalanceFromList(ArrayList<EntryItem> items) {
        long sum = 0;
        for (EntryItem item : items) {
            sum += item.getSum();
        }
        return decimalFormat.format(sum) + " Ft";
    }

    public static String calculateBalanceFromList(ArrayList<EntryItem> items, long number) {
        long sum = 0;
        for (EntryItem item : items) {
            sum += item.getSum();
        }
        return decimalFormat.format(sum + number) + " Ft";
    }

}
