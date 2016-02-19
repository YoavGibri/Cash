package com.yoavgibri.cash;

/**
 * Created by Yoav on 19/02/16.
 */

public class Expense {
    private String name, place, comment;
    private long time;

    public Expense(String name, String place, String comment, long time) {
        this.name = name;
        this.place = place;
        this.comment = comment;
        this.time = time;
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
}

