package com.sholis.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sholis.Item;
import com.sholis.R;

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
        holder.tvCount.setText(item.amount);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView tvName, tvCount;

        public ItemViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvItemName);
            tvCount = itemView.findViewById(R.id.tvItemAnzahl);
        }
    }

}
