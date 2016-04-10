package com.yoavgibri.cash;

import android.support.annotation.Nullable;

import java.util.Calendar;

/**
 * Created by Yoav on 19/02/16.
 */

public class Expense {
    private String name, place, comment;
    private long time, _id;
    private int amount, month;

    public Expense() {
    }

    public Expense(long _id, String name, String place, String comment, long time, int amount) {
        this._id = _id;
        this.name = name;
        this.place = place;
        this.comment = comment;
        this.time = time;
        this.amount = amount;
        this.month = getMonth(time);
    }

    public Expense(String name, String place, String comment, long time, int amount) {
        this.name = name;
        this.place = place;
        this.comment = comment;
        this.time = time;
        this.amount = amount;
        this.month = getMonth(time);

    }

    private int getMonth(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int yy = c.get(Calendar.YEAR);
        int mm = c.get(Calendar.MONTH);
        return ((yy-2000)*100 + mm) +1;
    }
    @Override
    public String toString() {
        return time+"- "+name+", "+amount+", "+place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public long getId() {
        return _id;
    }

    public void setId(long _id) {
        this._id = _id;
    }

    public int getMonth() {
        return month;
    }

}

