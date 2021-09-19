package com.sholis.web;

import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sholis.Item;
import com.sholis.ShoppingList;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

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

    public static String getWebData(@org.jetbrains.annotations.NotNull String URL, String parameter, @org.jetbrains.annotations.NotNull SharedPreferences sharedPreferences) {

        String webLocation = "https://krumm.ddns.net/sholis";

        String userName = sharedPreferences.getString("uName", "");//"No name defined" is the default value.
        String userPassword = sharedPreferences.getString("uPass", ""); //0 is the default value.

        if (userName.equals("") && userPassword.equals("")) return null;

        OkHttpClient client = new OkHttpClient();

        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
                    /*
                    HostnameVerifier hv =
                            HttpsURLConnection.getDefaultHostnameVerifier();
                    return hv.verify("*.ddns.net", session);

                     */
            }
        });

        client.setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) {
                String credential = Credentials.basic(userName, userPassword);
                return response.request().newBuilder().header("Authorization", credential).build();
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) {
                return null;
            }
        });

        String requestURL = webLocation + URL + parameter;

        Request request = new Request.Builder().url(requestURL).build();



        System.out.println("Sending request: " + request.urlString());

        String response = "";
        try {
            response = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(response);
    }

    public static boolean authenticateUser(String name, String password, SharedPreferences sharedPreferences) {

    }

}

