package com.sholis.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.Preference;
import android.preference.PreferenceManager;
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
import com.sholis.web.WebInterface;

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

    private int supermarketId;
    // own parameters
    RecyclerView recyclerView;
    ArrayList<Item> items = new ArrayList<>();

    public ShoppingListTab() {
        // Required empty public constructor
    }

    public ShoppingListTab(int supermarketId) {
        this.supermarketId = supermarketId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runUpdateCycle();
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



        new TaskGetItemsFromServer().execute();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new TaskGetItemsFromServer().execute();
    }

    private class TaskGetItemsFromServer extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            StringBuilder parameterBuilder = new StringBuilder("?supermarketId=");
            parameterBuilder.append(supermarketId);

            int index = 0;
            for(Item i : items) {
                parameterBuilder.append("&syncedItems[");
                parameterBuilder.append(index);
                parameterBuilder.append("]=");
                parameterBuilder.append(i.id);
                index++;
            }
            String response = WebInterface.getWebData("/ShoppingList.php", parameterBuilder.toString(), getActivity().getSharedPreferences("PRIVATE_PREFERENCES",  getActivity().MODE_PRIVATE));
            System.out.println(response);
            try {

                JSONArray jsonResult = new JSONArray(response);
                for(int i = 0; i < jsonResult.length(); i++) {
                    JSONObject jo = jsonResult.getJSONObject(i);
                    items.add(new Item(jo.getInt("ITEM_ID"), jo.getString("ITEM_NAME"), jo.getString("ITEM_AMOUNT"), jo.getInt("ITEM_INDEX"), (jo.getInt("ITEM_CHECKED") == 1)));
                }

                return response;
            } catch (Exception e) {
                e.printStackTrace();
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
                if (isVisible()) new TaskGetItemsFromServer().execute();
            }
        }, 0, 5000);

    }
}