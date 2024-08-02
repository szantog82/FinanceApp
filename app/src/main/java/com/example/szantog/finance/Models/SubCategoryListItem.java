package com.example.szantog.finance.Models;

/**
 * Created by szantog on 2018.03.26..
 */

public class SubCategoryListItem implements Comparable<SubCategoryListItem> {

    private String name;
    private Integer count;

    public SubCategoryListItem(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void incrCount() {
        this.count++;
    }

    public int compareTo(SubCategoryListItem item) {
        return item.getCount().compareTo(this.count);
    }

}
