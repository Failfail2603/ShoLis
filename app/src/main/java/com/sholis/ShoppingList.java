package com.sholis;

import java.util.ArrayList;

public class ShoppingList {

    private int familyId;
    private int supermarketId;

    private ArrayList<Item> items = new ArrayList<Item>();

    public ShoppingList (int familyId, int supermarketId, ArrayList<Item> items) {
        this.familyId = familyId;
        this.supermarketId = supermarketId;
        this.items = items;
    }

    public ShoppingList (int familyId, int supermarketId) {
        this.familyId = familyId;
        this.supermarketId = supermarketId;
    }
}
