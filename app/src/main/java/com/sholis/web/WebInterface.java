package com.sholis.web;

import com.loopj.android.http.*;
import com.sholis.Item;
import com.sholis.ShoppingList;
import com.sholis.Supermarket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class WebInterface {

    /*
    public static void getItemsFromShoppingList(ShoppingList shoppingList, int familyId, int supermarketId, int[] syncedItems) {
        AsyncHttpClient client = new AsyncHttpClient();
        StringBuilder url = new StringBuilder("https://www.google.com");
        url.append("?familyId=").append(familyId);
        url.append("&supermarketId=").append(supermarketId);
        client.get("https://www.google.com", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                System.out.println(new String(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
     */

    public static void getItemsFromShoppingList(@org.jetbrains.annotations.NotNull ShoppingList shoppingList) {

        AsyncHttpClient client = new AsyncHttpClient();
        StringBuilder url = new StringBuilder("http://krumm.ddns.net/ShoppingList.php");
        url.append("?familyId=").append(shoppingList.familyId);
        url.append("&supermarketId=").append(shoppingList.supermarketId);
        System.out.println("Sending request with " + url.toString());
        client.get(url.toString(), new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONArray result = new JSONArray(new String (response));
                    ArrayList<Item> items = new ArrayList<>();
                    for(int i = 0; i < result.length(); i++) {
                        JSONObject jo = result.getJSONObject(i);
                        items.add(new Item(jo.getInt("ITEM_ID"), jo.getString("ITEM_NAME"), jo.getString("ITEM_AMOUNT")));
                    }
                    shoppingList.setItems(items);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    public static ArrayList<Supermarket> getSupermarkets() {
        AsyncHttpClient client = new AsyncHttpClient();
        ArrayList<Supermarket> supermarkets = new ArrayList<>();
        StringBuilder url = new StringBuilder("http://krumm.ddns.net/Supermarket.php");
        client.get(url.toString(), new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    JSONArray result = new JSONArray(new String (response));

                    for(int i = 0; i < result.length(); i++) {
                        JSONObject jo = result.getJSONObject(i);
                        supermarkets.add(new Supermarket(jo.getString("SUPERMARKET_NAME"), jo.getInt("SUPERMARKET_ID")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
        return supermarkets;
    }

}
