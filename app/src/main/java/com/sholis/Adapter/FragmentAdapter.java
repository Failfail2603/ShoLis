package com.sholis.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sholis.Fragments.*;
import com.sholis.Supermarket;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentStateAdapter {

    private int NUM_TAB;

    public ArrayList<Supermarket> supermarkets = new ArrayList<>();

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int CountTab){
        super(fragmentManager, lifecycle);
        this.NUM_TAB = CountTab;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new ShoppingListTab(supermarkets.get(position).id);
    }

    @Override
    public int getItemCount() {
        return NUM_TAB;
    }
}
