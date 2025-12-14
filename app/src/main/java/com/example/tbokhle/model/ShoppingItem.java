package com.example.tbokhle.model;

public class ShoppingItem {
    public String name;
    public double quantity;
    public String unit;
    public String category;
    public String addedBy;
    public boolean done;

    public ShoppingItem(String name, double quantity, String unit, String category, String addedBy, boolean done) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
        this.addedBy = addedBy;
        this.done = done;
    }

    public String metaText() {
        String qty = (quantity == (long) quantity) ? String.valueOf((long) quantity) : String.valueOf(quantity);
        return qty + unit + "  •  " + category + "  •  Added by " + addedBy;
    }
}