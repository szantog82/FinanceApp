package com.example.szantog.finance.Models;

/**
 * Created by szantog on 2018.03.29..
 */

public class RepetitiveItem {

    private long time;
    private long sum;
    private long startTime;
    private long endTime;
    private int turnoverMonth;
    private String collection;
    private String category;
    private String subCategory;

    public RepetitiveItem(long time, long sum, long startTime, long endTime, int turnoverMonth, String collection, String category, String subCategory) {
        this.time = time;
        this.sum = sum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.turnoverMonth = turnoverMonth;
        this.collection = collection;
        this.category = category;
        this.subCategory = subCategory;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getTurnoverMonth() {
        return turnoverMonth;
    }

    public void setTurnoverMonth(int turnoverMonth) {
        this.turnoverMonth = turnoverMonth;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }
}
