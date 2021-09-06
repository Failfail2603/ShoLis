package com.sholis;

public class Item {
    public int id;

    public String name;
    public String amount;

    public int index;

    public boolean checked;

    public Item(int id, String name, String amount, int index, boolean checked) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.index = index;
        this.checked = checked;
    }

    public Item(int id, String name, String amount, int index) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.index = index;
        this.checked = false;
    }

    public Item(int id, String name, String amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.index = 0;
        this.checked = false;
    }
}
