package ru.ekbapp.intexartwoker.Class;

import java.io.Serializable;

public class WorkItem implements Serializable {
    public final String name;
    public final String count;
    public final String ed;
    public final String price;
    public final String cost;

    public WorkItem(String name, String count, String ed, String price, String cost) {
        this.name = name;
        this.count = count;
        this.ed = ed;
        this.price = price;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public String getCount() {
        return count;
    }

    public String getEd() {
        return ed;
    }

    public String getPrice() {
        return price;
    }

    public String getCost() {
        return cost;
    }
}
