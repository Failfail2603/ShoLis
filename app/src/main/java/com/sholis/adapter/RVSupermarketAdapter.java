package com.sholis.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sholis.R;
import com.sholis.ShoppingListItem;
import com.sholis.Supermarket;
import com.sholis.web.WebInterface;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RVSupermarketAdapter extends RecyclerView.Adapter<RVSupermarketAdapter.ItemViewHolder> {

    ArrayList<Supermarket> supermarkets = new ArrayList<>();
    Context context;

    public RVSupermarketAdapter(Context context) {
        this.context = context;
        new TaskGetSuperMarkets().execute();
    }

    @NonNull
    @NotNull
    @Override
    public RVSupermarketAdapter.ItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item_view, parent, false);
        return new RVSupermarketAdapter.ItemViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RVSupermarketAdapter.ItemViewHolder holder, int position) {
        holder.tvSupermarketName.setText(supermarkets.get(position).name);
        holder.supermarketId = supermarkets.get(position).id;
        holder.context = context;
    }

    @Override
    public int getItemCount() {
        return supermarkets.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvSupermarketName;
        int supermarketId;
        Context context;

        public ItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvSupermarketName = itemView.findViewById(R.id.tvItemName);

            itemView.setOnTouchListener(new View.OnTouchListener() {

                private float startX;
                private float startY;
                long lastClickTime = 0;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = motionEvent.getX();
                            startY = motionEvent.getY();
                            break;
                        case MotionEvent.ACTION_UP:
                            float endX = motionEvent.getX();
                            float endY = motionEvent.getY();
                            if (isAClick(startX, endX, startY, endY)) {
                                long clickTime = System.currentTimeMillis();
                                long doubleClickTimeDelta = 300; //milliseconds
                                if (clickTime - lastClickTime < doubleClickTimeDelta) {
                                    // Double Click
                                    onDoubleClick(view);
                                }  // else Single Click
                                lastClickTime = clickTime;
                            }
                            break;
                    }
                    return true;
                }

                private boolean isAClick(float startX, float endX, float startY, float endY) {
                    float differenceX = Math.abs(startX - endX);
                    float differenceY = Math.abs(startY - endY);
                    int CLICK_ACTION_THRESHOLD = 200;
                    return !(differenceX > CLICK_ACTION_THRESHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHOLD);
                }

                private void onDoubleClick(View v) {
                    new TaskAddShoppingList().execute();
                }

            });
        }

        @SuppressLint("StaticFieldLeak")
        private class TaskAddShoppingList extends AsyncTask<String, String, String> {

            protected void onPreExecute() {
                super.onPreExecute();

            }

            protected String doInBackground(String... params) {
                return WebInterface.addShoppingList(supermarketId, context.getSharedPreferences("PRIVATE_PREFERENCES", Context.MODE_PRIVATE));
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ((Activity)context).finish();
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class TaskGetSuperMarkets extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... params) {
            String response = WebInterface.getAllSupermarkets(context.getSharedPreferences("PRIVATE_PREFERENCES", Context.MODE_PRIVATE));
            System.out.println(response);
            try {
                JSONArray jsonResult = new JSONArray(response);
                for (int i = 0; i < jsonResult.length(); i++) {
                    JSONObject jo = jsonResult.getJSONObject(i);
                    supermarkets.add(new Supermarket(jo.getString("SUPERMARKET_NAME"), jo.getInt("SUPERMARKET_ID")));
                }
                System.out.println(getItemCount());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            notifyDataSetChanged();
        }
    }



}