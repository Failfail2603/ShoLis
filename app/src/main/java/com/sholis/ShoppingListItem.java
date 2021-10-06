package com.sholis;

import org.json.JSONException;
import org.json.JSONObject;

public class ShoppingListItem {
    public int id;

    public String name;
    public String amount;

    public int index;

    public boolean checked;

    public ShoppingListItem(int id, String name, String amount, int index, boolean checked) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.index = index;
        this.checked = checked;
    }

    public ShoppingListItem(int id, String name, String amount, int index) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.index = index;
        this.checked = false;
    }

    public ShoppingListItem(int id, String name, String amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.index = 0;
        this.checked = false;
    }

    public ShoppingListItem(String name, String amount) {
        this.id = 0;
        this.name = name;
        this.amount = amount;
        this.index = 0;
        this.checked = false;
    }

    public JSONObject getJsonSerialisation() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("ITEM_ID", this.id);
            jo.put("ITEM_NAME", this.name);
            jo.put("ITEM_AMOUNT", this.amount);
            jo.put("ITEM_INDEX", this.index);
            jo.put("ITEM_CHECKED", this.checked);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

}
