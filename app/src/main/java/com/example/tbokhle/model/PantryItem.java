package com.example.tbokhle.model;

public class PantryItem {
    public String name;
    public String category;
    public double quantity;
    public String unit;
    public int daysToExpire;

    public PantryItem(String name, String category, double quantity, String unit, int daysToExpire) {
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.unit = unit;
        this.daysToExpire = daysToExpire;
    }

    public String expiryText() {
        if (daysToExpire < 0) return "Expired";
        if (daysToExpire == 0) return "Expires today";
        if (daysToExpire == 1) return "Expires in 1 day";
        return "Expires in " + daysToExpire + " days";
    }

    public String status() {
        if (daysToExpire < 0) return "RED";
        if (daysToExpire <= 3) return "AMBER";
        return "GREEN";
    }
}
