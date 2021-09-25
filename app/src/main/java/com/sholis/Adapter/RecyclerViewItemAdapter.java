package com.sholis.Adapter;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sholis.Fragments.ShoppingListTab;
import com.sholis.Item;
import com.sholis.R;
import com.sholis.listeners.DoubleClickListener;
import com.sholis.web.WebInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewItemAdapter extends RecyclerView.Adapter<RecyclerViewItemAdapter.ItemViewHolder> {

    ArrayList<Item> items;
    ShoppingListTab attachedTab;


    public RecyclerViewItemAdapter(ArrayList<Item> items, ShoppingListTab attachedTab) {
        this.items = items;
        this.attachedTab = attachedTab;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerViewItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new ItemViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewItemAdapter.ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.tvName.setText(item.name);
        holder.tvCount.setText(item.amount.equals("null") ? "" : item.amount);
        holder.holderItem = items.get(position);
        holder.attachedTab = attachedTab;
        holder.initialise();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCount;
        Item holderItem;
        int colorUnChecked;
        int colorChecked;
        ShoppingListTab attachedTab;

        public ItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvCount = itemView.findViewById(R.id.tvItemAnzahl);

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                        attachedTab.allowedToUpdate = false;
                        System.out.println("Stopped Updates");
                    } else if (motionEvent.getAction() == android.view.MotionEvent.ACTION_UP) {
                        attachedTab.allowedToUpdate = true;
                        System.out.println("Started Updates");
                    }
                    return true;
                }


            });

            itemView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {

                }

                @Override
                public void onDoubleClick(View v) {
                    holderItem.checked = !holderItem.checked;
                    if (holderItem.checked) {
                        tvName.setBackgroundColor(colorChecked);
                        tvCount.setBackgroundColor(colorChecked);
                    } else {
                        tvName.setBackgroundColor(colorUnChecked);
                        tvCount.setBackgroundColor(colorUnChecked);
                    }

                    int currentPosition = ItemViewHolder.this.getAdapterPosition();
                    synchronized (attachedTab.items) {
                        if (holderItem.checked) {
                            holderItem.index = Integer.MAX_VALUE;
                            int endOfList = attachedTab.items.size() - 1;
                            Collections.rotate(attachedTab.items.subList(currentPosition, endOfList), -1);
                            attachedTab.recyclerView.getAdapter().notifyItemMoved(currentPosition, endOfList);
                        } else {
                            holderItem.index = 1;
                            attachedTab.items.remove(currentPosition);
                            attachedTab.items.add(0, holderItem);

                            attachedTab.recyclerView.getAdapter().notifyItemMoved(currentPosition, 0);
                        }
                    }
                    WebInterface.persistToggleAndIndex(holderItem, v.getContext().getSharedPreferences("PRIVATE_PREFERENCES", 0));
                }
            });

        }

        public void initialise() {
            TypedArray a = itemView.getContext().getTheme().obtainStyledAttributes(R.style.Theme_Sholis_Light, new int[]{R.attr.colorOnPrimary});
            colorUnChecked = a.getColor(0, 0);
            a.recycle();

            a = itemView.getContext().getTheme().obtainStyledAttributes(R.style.Theme_Sholis_Light, new int[]{R.attr.colorPrimary});
            colorChecked = a.getColor(0, 0);
            a.recycle();

            if (holderItem.checked) {

                tvName.setBackgroundColor(colorChecked);
                tvCount.setBackgroundColor(colorChecked);
            } else {
                tvName.setBackgroundColor(colorUnChecked);
                tvCount.setBackgroundColor(colorUnChecked);
            }
        }


    }



}
