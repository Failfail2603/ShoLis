package com.sholis.web;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.loopj.android.http.*;
import com.sholis.Item;
import com.sholis.MainActivity;
import com.sholis.ShoppingList;
import com.sholis.Supermarket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.client.HttpClient;

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
}

