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
        for (Item i : items) {
            if (i.checked) attachedTab.checkedItems++;
        }
        if (attachedTab.checkedItems == items.size()) attachedTab.enableDeleteButton();
        else attachedTab.disableDeleteButton();
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

                private float startX;
                private float startY;
                long lastClickTime = 0;

                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startX = motionEvent.getX();
                            startY = motionEvent.getY();
                            attachedTab.allowedToUpdate = false;
                            System.out.println("Stopped Updates");
                            break;
                        case MotionEvent.ACTION_UP:
                            float endX = motionEvent.getX();
                            float endY = motionEvent.getY();
                            attachedTab.allowedToUpdate = true;
                            System.out.println("Started Updates");
                            if (isAClick(startX, endX, startY, endY)) {
                                long clickTime = System.currentTimeMillis();
                                long doubleClickTimeDelta = 300; //milliseconds
                                if (clickTime - lastClickTime < doubleClickTimeDelta){
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
                            attachedTab.checkedItems++;
                            holderItem.index = Integer.MAX_VALUE;
                            int endOfList = attachedTab.items.size() > 0 ? attachedTab.items.size() - 1 : 0;
                            Collections.rotate(attachedTab.items.subList(currentPosition, endOfList), -1);
                            attachedTab.recyclerView.getAdapter().notifyItemMoved(currentPosition, endOfList);

                        } else {
                            attachedTab.checkedItems--;
                            holderItem.index = 1;
                            attachedTab.items.remove(currentPosition);
                            attachedTab.items.add(0, holderItem);

                            attachedTab.recyclerView.getAdapter().notifyItemMoved(currentPosition, 0);
                        }
                    }
                    if (attachedTab.checkedItems == attachedTab.items.size()) attachedTab.enableDeleteButton();
                    else attachedTab.disableDeleteButton();
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
