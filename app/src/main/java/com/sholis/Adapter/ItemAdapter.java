package com.sholis.Adapter;

import android.content.Context;
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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private ArrayList<Item> items;
    ItemClicked activity;

    public interface ItemClicked{
        void onItemClicked(int index);
    }

    public ItemAdapter (Context context, ArrayList<Item> items){
        this.items = items;
        activity = (ItemClicked) context; //muss noch in Activity implementiert werden, heißt -> ... implements ItemAdapter.ItemClicked fuer Interface
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvItemBild, tvItemName, tvItemAnzahl;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvItemBild = itemView.findViewById(R.id.tvItemBild);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemAnzahl = itemView.findViewById(R.id.tvItemAnzahl);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onItemClicked(items.indexOf((Item)view.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview1_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(items.get(position));

        holder.tvItemName.setText(items.get(position).name);
        holder.tvItemAnzahl.setText(items.get(position).amount);
        holder.tvItemBild.setText(items.get(position).name.charAt(0) + "");  //Anstatt Bild erstmal nur der Anfangsbuchstabe
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
