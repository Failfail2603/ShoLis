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

import org.jetbrains.annotations.NotNull;
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
                    JSONArray result = new JSONArray(new String(response));
                    ArrayList<Item> items = new ArrayList<>();
                    for (int i = 0; i < result.length(); i++) {
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

    public static String getWebData(@NotNull String URI, String parameter, @NotNull SharedPreferences sharedPreferences) {

        String webLocation = "https://krumm.ddns.net/sholis";

        String userName = sharedPreferences.getString("uName", "");//"No name defined" is the default value.
        String userPassword = sharedPreferences.getString("uPass", ""); //0 is the default value.

        if (userName.equals("") && userPassword.equals("")) return "";

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

        String requestURL = webLocation + URI + parameter;

        Request request = new Request.Builder().url(requestURL).build();

        String response = "";
        try {
            response = client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public static boolean authenticateUser(String name, String password, SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uName", name);
        editor.putString("uPass", password);
        editor.apply();

        String response = getWebData("/login.php", "", sharedPreferences);
        if (response.equals("OK")) return true;

        // Delete the the wrong pass and username from preferences
        editor = sharedPreferences.edit();
        editor.putString("uName", "");
        editor.putString("uPass", "");
        editor.apply();

        return false;
    }

}

