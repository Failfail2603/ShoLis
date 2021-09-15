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
    private final int familyId;

    public ArrayList<Supermarket> supermarkets = new ArrayList<>();

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int CountTab, int familyId){
        super(fragmentManager, lifecycle);
        this.NUM_TAB = CountTab;
        this.familyId = familyId;
        System.out.println("FragmentAdapter" + familyId);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        System.out.println("Generating new Tab Fragment with familyId: " + familyId);
        return new ShoppingListTab(familyId, supermarkets.get(position).id);
    }

    @Override
    public int getItemCount() {
        return NUM_TAB;
    }
}
