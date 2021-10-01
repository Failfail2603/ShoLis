package com.sholis.web;

import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sholis.Item;
import com.sholis.ShoppingList;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
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


    public static String postWebData(@NotNull String URI, @NotNull SharedPreferences sharedPreferences, RequestBody postBody) {
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

        String requestURL = webLocation + URI;
        Request request = new Request.Builder()
                .url(requestURL)
                .post(postBody)
                .build();


        String response = "";
        System.out.println("Sending POST to: " + requestURL);
        try {
            response = client.newCall(request).execute().body().string();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
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

        String response = getWebData("/Login.php", "", sharedPreferences);
        if (response.equals("OK")) return true;

        // Delete the the wrong pass and username from preferences
        editor = sharedPreferences.edit();
        editor.putString("uName", "");
        editor.putString("uPass", "");
        editor.apply();

        return false;
    }

    public static String persistItem(Item item, int supermarketId, SharedPreferences sharedPreferences) {
        JSONObject itemJson = item.getJsonSerialisation();

        FormEncodingBuilder body = new FormEncodingBuilder();
        body.add("item", itemJson.toString());
        body.add("supermarketId", Integer.toString(supermarketId));
        body.add("delete", "0");
        body.add("toggleChecked", "0");
        body.add("changeIndex", "0");
        RequestBody rBody = body.build();

        return postWebData("/ShoppingList.php", sharedPreferences, rBody);
    }

    public static String deleteItem(Item item, SharedPreferences sharedPreferences) {
        JSONObject itemJson = item.getJsonSerialisation();

        FormEncodingBuilder body = new FormEncodingBuilder();
        body.add("item", itemJson.toString());
        body.add("delete", "1");
        body.add("toggleChecked", "0");
        body.add("changeIndex", "0");
        RequestBody rBody = body.build();

        return postWebData("/ShoppingList.php", sharedPreferences, rBody);
    }

    public static String persistToggle(Item item, SharedPreferences sharedPreferences) {
        JSONObject itemJson = item.getJsonSerialisation();

        FormEncodingBuilder body = new FormEncodingBuilder();
        body.add("item", itemJson.toString());
        body.add("delete", "0");
        body.add("toggleChecked", "1");
        body.add("changeIndex", "0");
        RequestBody rBody = body.build();

        return postWebData("/ShoppingList.php", sharedPreferences, rBody);
    }

    public static String persistIndex(Item item, SharedPreferences sharedPreferences) {
        JSONObject itemJson = item.getJsonSerialisation();

        FormEncodingBuilder body = new FormEncodingBuilder();
        body.add("item", itemJson.toString());
        body.add("delete", "0");
        body.add("toggleChecked", "0");
        body.add("changeIndex", "1");
        RequestBody rBody = body.build();

        return postWebData("/ShoppingList.php", sharedPreferences, rBody);
    }

    public static String persistToggleAndIndex(Item item, SharedPreferences sharedPreferences) {
        JSONObject itemJson = item.getJsonSerialisation();

        FormEncodingBuilder body = new FormEncodingBuilder();
        body.add("item", itemJson.toString());
        body.add("delete", "0");
        body.add("toggleChecked", "1");
        body.add("changeIndex", "1");
        RequestBody rBody = body.build();

        return postWebData("/ShoppingList.php", sharedPreferences, rBody);
    }

    public static String deleteAllItemsFromList(int supermarketId, SharedPreferences sharedPreferences) {

        FormEncodingBuilder body = new FormEncodingBuilder();
        body.add("supermarketId", String.valueOf(supermarketId));
        body.add("delete", "1");
        body.add("toggleChecked", "0");
        body.add("changeIndex", "0");
        RequestBody rBody = body.build();

        return postWebData("/ShoppingList.php", sharedPreferences, rBody);
    }

}

