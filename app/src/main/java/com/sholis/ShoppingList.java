package com.sholis;

import java.util.ArrayList;

public class ShoppingList {

    public int familyId;
    public int supermarketId;

    private ArrayList<Item> items = new ArrayList<Item>();

    public ShoppingList(int familyId, int supermarketId, ArrayList<Item> items) {
        this.familyId = familyId;
        this.supermarketId = supermarketId;
        this.items = items;
    }

    public ShoppingList(int familyId, int supermarketId) {
        this.familyId = familyId;
        this.supermarketId = supermarketId;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void printToConsole() {
        System.out.println("familyID: " + this.familyId + " superMarketId: " + this.supermarketId);
        for (Item i : items){
            System.out.println(i.name + " " + i.amount);
        }
    }

}
