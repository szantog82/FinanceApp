package com.example.szantog.finance.Models;

public class InitialBalance {

    private int pocket_id;
    private long sum;

    public InitialBalance(int pocket_id, long sum) {
        this.pocket_id = pocket_id;
        this.sum = sum;
    }

    public int getPocket_id() {
        return pocket_id;
    }

    public void setPocket_id(int pocket_id) {
        this.pocket_id = pocket_id;
    }

    public long getSum() {
        return sum;
    }

    public void setSum(long sum) {
        this.sum = sum;
    }
}
