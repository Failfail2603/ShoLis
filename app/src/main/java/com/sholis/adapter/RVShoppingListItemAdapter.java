package com.sholis.adapter;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sholis.fragment.ShoppingListFragment;
import com.sholis.ShoppingListItem;
import com.sholis.R;
import com.sholis.web.WebInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RVShoppingListItemAdapter extends RecyclerView.Adapter<RVShoppingListItemAdapter.ItemViewHolder> {

    ArrayList<ShoppingListItem> shoppingListItems;
    ShoppingListFragment attachedTab;


    public RVShoppingListItemAdapter(ArrayList<ShoppingListItem> shoppingListItems, ShoppingListFragment attachedTab) {
        this.shoppingListItems = shoppingListItems;


        this.attachedTab = attachedTab;
        for (ShoppingListItem i : shoppingListItems) {
            if (i.checked) attachedTab.checkedItems++;
        }
        attachedTab.updateDeleteButton();
    }

    @NonNull
    @NotNull
    @Override
    public RVShoppingListItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item_view, parent, false);
        return new ItemViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull RVShoppingListItemAdapter.ItemViewHolder holder, int position) {
        ShoppingListItem shoppingListItem = shoppingListItems.get(position);
        holder.tvName.setText(shoppingListItem.name);
        holder.tvCount.setText(shoppingListItem.amount.equals("null") ? "" : shoppingListItem.amount);
        holder.holderShoppingListItem = shoppingListItems.get(position);
        holder.attachedTab = attachedTab;
        holder.initialise();
    }

    @Override
    public int getItemCount() {
        return shoppingListItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCount;
        ShoppingListItem holderShoppingListItem;
        int colorUnChecked;
        int colorChecked;
        ShoppingListFragment attachedTab;

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
                    holderShoppingListItem.checked = !holderShoppingListItem.checked;
                    if (holderShoppingListItem.checked) {
                        tvName.setBackgroundColor(colorChecked);
                        tvCount.setBackgroundColor(colorChecked);
                    } else {
                        tvName.setBackgroundColor(colorUnChecked);
                        tvCount.setBackgroundColor(colorUnChecked);
                    }

                    int currentPosition = ItemViewHolder.this.getAdapterPosition();
                    synchronized (attachedTab.shoppingListItems) {
                        if (holderShoppingListItem.checked) {
                            attachedTab.checkedItems++;
                            holderShoppingListItem.index = Integer.MAX_VALUE;
                            int endOfList = attachedTab.shoppingListItems.size() > 0 ? attachedTab.shoppingListItems.size() - 1 : 0;
                            Collections.rotate(attachedTab.shoppingListItems.subList(currentPosition, endOfList), -1);
                            attachedTab.recyclerView.getAdapter().notifyItemMoved(currentPosition, endOfList);

                        } else {
                            attachedTab.checkedItems--;
                            holderShoppingListItem.index = 1;
                            attachedTab.shoppingListItems.remove(currentPosition);
                            attachedTab.shoppingListItems.add(0, holderShoppingListItem);

                            attachedTab.recyclerView.getAdapter().notifyItemMoved(currentPosition, 0);
                        }
                    }
                    attachedTab.updateDeleteButton();
                    WebInterface.persistToggleAndIndex(holderShoppingListItem, v.getContext().getSharedPreferences("PRIVATE_PREFERENCES", 0));
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

            if (holderShoppingListItem.checked) {

                tvName.setBackgroundColor(colorChecked);
                tvCount.setBackgroundColor(colorChecked);
            } else {
                tvName.setBackgroundColor(colorUnChecked);
                tvCount.setBackgroundColor(colorUnChecked);
            }
        }


    }



}
