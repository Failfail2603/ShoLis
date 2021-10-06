package com.sholis;

import java.util.ArrayList;

public class ShoppingList {

    public int familyId;
    public int supermarketId;

    private ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<ShoppingListItem>();

    public ShoppingList(int familyId, int supermarketId, ArrayList<ShoppingListItem> shoppingListItems) {
        this.familyId = familyId;
        this.supermarketId = supermarketId;
        this.shoppingListItems = shoppingListItems;
    }

    public ShoppingList(int familyId, int supermarketId) {
        this.familyId = familyId;
        this.supermarketId = supermarketId;
    }

    public void setShoppingListItems(ArrayList<ShoppingListItem> shoppingListItems) {
        this.shoppingListItems = shoppingListItems;
    }

    public void printToConsole() {
        System.out.println("familyID: " + this.familyId + " superMarketId: " + this.supermarketId);
        for (ShoppingListItem i : shoppingListItems){
            System.out.println(i.name + " " + i.amount);
        }
    }

}
