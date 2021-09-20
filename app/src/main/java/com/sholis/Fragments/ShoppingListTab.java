package com.sholis.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sholis.Adapter.FragmentAdapter;
import com.sholis.Adapter.RecyclerViewItemAdapter;
import com.sholis.Item;
import com.sholis.R;
import com.sholis.Supermarket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListTab} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListTab extends Fragment {

    private int familyId;
    private int supermarketId;
    // own parameters
    RecyclerView recyclerView;
    ArrayList<Item> items = new ArrayList<>();

    public ShoppingListTab() {
        // Required empty public constructor
    }

    public ShoppingListTab(int familyId, int supermarketId) {
        this.familyId = familyId;
        this.supermarketId = supermarketId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //runUpdateCycle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_shoppinglist, container, false);
        recyclerView = v.findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);   //better performance

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerViewItemAdapter adapter = new RecyclerViewItemAdapter(items);
        recyclerView.setAdapter(adapter);



        //new TaskGetItemsFromServer().execute();
        return v;
    }

    private class TaskGetItemsFromServer extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {

                StringBuilder sb = new StringBuilder("http://krumm.ddns.net/ShoppingList.php");
                sb.append("?familyId=").append(familyId);
                sb.append("&supermarketId=").append(supermarketId);

                int index = 0;
                for(Item i : items) {
                    sb.append("&syncedItems[");
                    sb.append(index);
                    sb.append("]=");
                    sb.append(i.id);
                    index++;
                }

                URL url = new URL(sb.toString());
                System.out.println("Requesting update with: " + url.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                }

                String response = buffer.toString();
                System.out.println(response);
                JSONArray jsonResult = new JSONArray(response);
                for(int i = 0; i < jsonResult.length(); i++) {
                    JSONObject jo = jsonResult.getJSONObject(i);
                    items.add(new Item(jo.getInt("ITEM_ID"), jo.getString("ITEM_NAME"), jo.getString("ITEM_AMOUNT"), jo.getInt("ITEM_INDEX"), (jo.getInt("ITEM_CHECKED") == 1)));
                }


                return response;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    private void runUpdateCycle() {
        Timer timer = new Timer();

        //Updates the simulation cycleTime is set on init
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //if (isVisible()) new TaskGetItemsFromServer().execute();
            }
        }, 0, 5000);

    }
}