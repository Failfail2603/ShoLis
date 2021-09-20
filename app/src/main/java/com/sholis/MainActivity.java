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
            return WebInterface.getWebData("/Supermarket.php", "", getSharedPreferences("PRIVATE_PREFERENCES", MODE_PRIVATE));
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