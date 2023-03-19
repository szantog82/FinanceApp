package com.example.szantog.finance.Models;

import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by szantog on 2018.03.16..
 */

public class EntryItem implements Comparable<EntryItem> {

    private long time;
    private long sum;
    private int year;
    private int month;
    private int day;
    private String category;
    private String subCategory;
    private int pocket;

    public EntryItem(long time, long sum, int year, int month, int day, String category, String subCategory) {
        this.time = time;
        this.sum = sum;
        this.year = year;
        this.month = month;
        this.day = day;
        this.category = category;
        this.subCategory = subCategory;
        this.pocket = 1;
    }

    public EntryItem(long time, long sum, int year, int month, int day, String category, String subCategory, int pocket) {
        this.time = time;
        this.sum = sum;
        this.year = year;
        this.month = month;
        this.day = day;
        this.category = category;
        this.subCategory = subCategory;
        this.pocket = pocket;
    }

    public long getTime() {
        return time;
    }

    public long getSum() {
        return sum;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getStringDate() {
        return String.valueOf(this.year) + "." + String.valueOf(this.month + 1) + "." + String.valueOf(this.day);
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public int getPocket() {
        return pocket;
    }

    public void setPocket(int pocket) {
        this.pocket = pocket;
    }

    private int getDayCount() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.getYear(), this.getMonth(), this.getDay());
        return (int) (calendar.getTimeInMillis() / 1000 / 3600 / 24);
    }

    @Override
    public int compareTo(@NonNull EntryItem item) {
        if (this.getDayCount() > item.getDayCount()) {
            return 1;
        } else {
            return -1;
        }
    }
}
