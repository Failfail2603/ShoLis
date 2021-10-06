package com.sholis.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.sholis.adapter.RVShoppingListItemAdapter;
import com.sholis.ShoppingListItem;
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
 * Use the {@link ShoppingListFragment} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment {

    private int supermarketId;
    // own parameters
    public RecyclerView recyclerView;
    public final ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();
    public boolean allowedToUpdate = true;
    public int checkedItems = 0;
    ExtendedFloatingActionButton deleteAllButton;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    public ShoppingListFragment(int supermarketId) {
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
        deleteAllButton = getActivity().findViewById(R.id.floating_action_button_delete_list);
        recyclerView.setHasFixedSize(true);   //better performance

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        RVShoppingListItemAdapter adapter = new RVShoppingListItemAdapter(shoppingListItems, this);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback dragTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                allowedToUpdate = false;
                System.out.println("Stopped Updates");
                synchronized (shoppingListItems) {
                    int fromPosition = viewHolder.getAdapterPosition();
                    int toPosition = target.getAdapterPosition();

                    Collections.swap(shoppingListItems, fromPosition, toPosition);
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

    public void updateData() {
        new TaskGetItemsFromServer().execute();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void clearItems() {
        this.shoppingListItems.clear();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    public void updateDeleteButton() {
        if (checkedItems == shoppingListItems.size() && shoppingListItems.size() > 0) {
            deleteAllButton.setVisibility(View.VISIBLE);
        } else {
            deleteAllButton.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class TaskGetItemsFromServer extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {
            if (allowedToUpdate) {
                System.out.println("Updating shoppingListItems");
                int index = 1;
                synchronized (shoppingListItems) {
                    for (ShoppingListItem i : shoppingListItems) {
                        if (i.checked) continue;
                        if (i.index != index) {
                            i.index = index;
                            WebInterface.persistIndex(i, getActivity().getSharedPreferences("PRIVATE_PREFERENCES", Context.MODE_PRIVATE));
                        }
                        index++;
                    }
                }

                String response = WebInterface.getWebData("/ShoppingList.php", "?supermarketId=" + supermarketId, getActivity().getSharedPreferences("PRIVATE_PREFERENCES", Context.MODE_PRIVATE));
                System.out.println(response);
                try {
                    JSONArray jsonResult = new JSONArray(response);
                    ArrayList<ShoppingListItem> newShoppingListItems = new ArrayList<>();
                    int newCheckedItems = 0;
                    for (int i = 0; i < jsonResult.length(); i++) {
                        JSONObject jo = jsonResult.getJSONObject(i);
                        ShoppingListItem newShoppingListItem = new ShoppingListItem(jo.getInt("ITEM_ID"), jo.getString("ITEM_NAME"), jo.getString("ITEM_AMOUNT"), jo.getInt("ITEM_INDEX"), (jo.getInt("ITEM_CHECKED") == 1));
                        newShoppingListItems.add(newShoppingListItem);
                        if (newShoppingListItem.checked) newCheckedItems++;
                    }

                    synchronized (shoppingListItems) {
                        shoppingListItems.clear();
                        shoppingListItems.addAll(newShoppingListItems);
                        checkedItems = newCheckedItems;
                    }
                    System.out.println(checkedItems);

                    return response;
                } catch (Exception e) {
                    System.out.println("Couldn't convert web response to JSON Data. Response was: " + response);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            updateDeleteButton();
            recyclerView.getAdapter().notifyDataSetChanged();
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