package com.sholis.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sholis.fragment.*;
import com.sholis.Supermarket;

import java.util.ArrayList;

public class FragmentShoppingListAdapter extends FragmentStateAdapter {

    private int NUM_TAB;

    public ArrayList<ShoppingListFragment> tabs = new ArrayList<>();

    public ArrayList<Supermarket> supermarkets = new ArrayList<>();

    public FragmentShoppingListAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int CountTab){
        super(fragmentManager, lifecycle);
        this.NUM_TAB = CountTab;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ShoppingListFragment tab = new ShoppingListFragment(supermarkets.get(position).id);
        tabs.add(tab);
        return tab;
    }



    @Override
    public int getItemCount() {
        return NUM_TAB;
    }

    public void update() {
        for(ShoppingListFragment tab : tabs) {
            tab.updateData();
        }
    }

    public void deleteItem(int position) {
        tabs.remove(position);
        supermarkets.remove(position);
        this.notifyItemRemoved(position);
        NUM_TAB--;
    }


}
