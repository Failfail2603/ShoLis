package com.sholis.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sholis.Item;
import com.sholis.R;
import com.sholis.listeners.DoubleClickListener;
import com.sholis.web.WebInterface;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RecyclerViewItemAdapter extends RecyclerView.Adapter<RecyclerViewItemAdapter.ItemViewHolder> {

    ArrayList<Item> items;

    public RecyclerViewItemAdapter(ArrayList<Item> items){
        this.items = items;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerViewItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new ItemViewHolder(v);
    }



    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerViewItemAdapter.ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.tvName.setText(item.name);
        holder.tvCount.setText(item.amount.equals("null") ? "" : item.amount);
        holder.holderItem = items.get(position);
        holder.initialise();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvCount;
        Item holderItem;
        int colorUnChecked;
        int colorChecked;

        public ItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);



            tvName = itemView.findViewById(R.id.tvItemName);
            tvCount = itemView.findViewById(R.id.tvItemAnzahl);

            itemView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {

                }

                @Override
                public void onDoubleClick(View v) {
                    WebInterface.toggleItemChecked(holderItem, v.getContext().getSharedPreferences("PRIVATE_PREFERENCES", 0));
                    holderItem.checked = !holderItem.checked;

                    if(holderItem.checked) {
                        tvName.setBackgroundColor(colorChecked);
                        tvCount.setBackgroundColor(colorChecked);
                    } else {
                        tvName.setBackgroundColor(colorUnChecked);
                        tvCount.setBackgroundColor(colorUnChecked);
                    }
                }
            });
        }

        public void initialise() {


            TypedArray a = itemView.getContext().getTheme().obtainStyledAttributes(R.style.Theme_Sholis_Light, new int[] { R.attr.colorOnPrimary });
            colorUnChecked = a.getColor(0, 0);
            a.recycle();

            a = itemView.getContext().getTheme().obtainStyledAttributes(R.style.Theme_Sholis_Light, new int[] { R.attr.colorPrimary });
            colorChecked = a.getColor(0, 0);
            a.recycle();

            if(holderItem.checked) {

                tvName.setBackgroundColor(colorChecked);
                tvCount.setBackgroundColor(colorChecked);
            } else {
                tvName.setBackgroundColor(colorUnChecked);
                tvCount.setBackgroundColor(colorUnChecked);
            }
        }
    }

}
