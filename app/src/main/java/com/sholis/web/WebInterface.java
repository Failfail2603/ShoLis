package com.sholis.web;

import com.loopj.android.http.*;
import com.sholis.Item;
import com.sholis.ShoppingList;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class WebInterface {


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

    public static void getItemsFromShoppingList(ShoppingList shoppingList, int familyId, int supermarketId) {

        AsyncHttpClient client = new AsyncHttpClient();
        StringBuilder url = new StringBuilder("http://krumm.ddns.net/ShoppingList.php");
        url.append("?familyId=").append(familyId);
        url.append("&supermarketId=").append(supermarketId);
        System.out.println("Sending request with " + url.toString());
        client.get(url.toString(), new AsyncHttpResponseHandler() {

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

}
