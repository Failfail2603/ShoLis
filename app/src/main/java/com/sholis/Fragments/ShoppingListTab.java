package com.sholis.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sholis.Adapter.RecyclerViewItemAdapter;
import com.sholis.Item;
import com.sholis.R;
import com.sholis.web.WebInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListTab} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListTab extends Fragment {

    private int supermarketId;
    // own parameters
    public RecyclerView recyclerView;
    public final ArrayList<Item> items = new ArrayList<>();
    public boolean allowedToUpdate = true;

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

        RecyclerViewItemAdapter adapter = new RecyclerViewItemAdapter(items, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback dragTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                allowedToUpdate = false;
                System.out.println("Stopped Updates");
                synchronized (items) {
                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = target.getAdapterPosition();

                    Collections.swap(items, fromPosition, toPosition);
                    recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                }
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                allowedToUpdate = true;
                System.out.println("Started Updates");
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(dragTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        new TaskGetItemsFromServer().execute();
    }

    public void update() {
        new TaskGetItemsFromServer().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class TaskGetItemsFromServer extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {
            if (allowedToUpdate) {
                System.out.println("Updating items");
                int index = 1;
                synchronized (items) {
                    for (Item i : items) {
                        if (i.checked) continue;
                        if (i.index != index) {
                            i.index = index;
                            WebInterface.persistIndex(i, getActivity().getSharedPreferences("PRIVATE_PREFERENCES", Context.MODE_PRIVATE));
                        }
                        index++;
                    }
                }

                String response = WebInterface.getWebData("/ShoppingList.php", "?supermarketId=" + supermarketId, getActivity().getSharedPreferences("PRIVATE_PREFERENCES", Context.MODE_PRIVATE));
                try {

                    JSONArray jsonResult = new JSONArray(response);
                    ArrayList<Item> newItems = new ArrayList<>();
                    for (int i = 0; i < jsonResult.length(); i++) {
                        JSONObject jo = jsonResult.getJSONObject(i);
                        Item newItem = new Item(jo.getInt("ITEM_ID"), jo.getString("ITEM_NAME"), jo.getString("ITEM_AMOUNT"), jo.getInt("ITEM_INDEX"), (jo.getInt("ITEM_CHECKED") == 1));
                        newItems.add(newItem);
                    }
                    synchronized (items) {
                        items.clear();
                        items.addAll(newItems);
                    }


                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void runUpdateCycle() {
        Timer timer = new Timer();

        //Updates the simulation cycleTime is set on init
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isVisible()) {
                    new TaskGetItemsFromServer().execute();
                }
            }
        }, 0, 5000);

    }


}