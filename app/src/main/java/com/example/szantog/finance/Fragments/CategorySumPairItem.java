package com.example.szantog.finance.Fragments;

import android.support.annotation.NonNull;

/**
 * Created by szantog on 2018.03.20..
 */

public class CategorySumPairItem implements Comparable<CategorySumPairItem> {

    private String category;
    private long sum;
    private float percent;

    public CategorySumPairItem(String category, long sum, float percent) {
        this.category = category;
        this.sum = sum;
        this.percent = percent;
    }

    public String getCategory() {
        return category;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    @Override
    public int compareTo(@NonNull CategorySumPairItem categorySumPairItem) {
        if (categorySumPairItem.getSum() > this.sum) {
            return -1;
        } else {
            return 1;
        }
    }
}
