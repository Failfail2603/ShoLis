package com.sholis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.loopj.android.http.Base64;
import com.sholis.Adapter.FragmentAdapter;
import com.sholis.web.WebInterface;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;

    protected int familyId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Implementation TabLayout Navigation using ViewPager2
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.viewPager2);

        new TaskFillSuperMarketTabs().execute();

        setSupportActionBar(toolbar);

        /*
        System.out.println(allSupermarkets.size());
        for(Supermarket sm : allSupermarkets) {

            tabLayout.addTab(tabLayout.newTab().setText(sm.name));
        }

         */

        //test elements
        Item banane = new Item(0, "Banane", "1");
        Item apfel = new Item(1, "Apfel", "1");

        ArrayList<Item> TestList = new ArrayList<Item>();
        TestList.add(banane);
        TestList.add(apfel);

        ShoppingList testShop = new ShoppingList(1, 1);
        WebInterface.getItemsFromShoppingList(testShop);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle item selection
        switch(item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                //do something
                return true;
            case R.id.item2:
                Toast.makeText(this, "Item2 selected", Toast.LENGTH_SHORT).show();
                //do something else
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class TaskFillSuperMarketTabs extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

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
                public Request authenticate(Proxy proxy, Response response) throws IOException {
                    String credential = Credentials.basic("Krumm", "2603");
                    return response.request().newBuilder().header("Authorization", credential).build();
                }

                @Override
                public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                    return null;
                }
            });

            Request request = new Request.Builder()
                    .url("https://krumm.ddns.net/sholis/Supermarket.php")
                    .build();

            System.out.println("Sending request: " + request.urlString());

            String response = "";
            try {
                response = client.newCall(request).execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(response);


            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonResult = new JSONArray(result);

                for (int i = 0; i < jsonResult.length(); i++) {
                    tabLayout.addTab(tabLayout.newTab());
                }
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), tabLayout.getTabCount(), familyId);

                for (int i = 0; i < jsonResult.length(); i++) {
                    JSONObject jo = jsonResult.getJSONObject(i);
                    fragmentAdapter.supermarkets.add(new Supermarket(jo.getString("SUPERMARKET_NAME"), jo.getInt("SUPERMARKET_ID")));
                }

                viewPager2.setAdapter(fragmentAdapter);

                new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        try {
                            JSONObject jo = jsonResult.getJSONObject(position);
                            tab.setText(jo.getString("SUPERMARKET_NAME"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).attach();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}