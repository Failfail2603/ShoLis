package com.sholis.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sholis.Fragments.*;

public class FragmentAdapter extends FragmentStateAdapter {

    private int NUM_TAB;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int CountTab){
        super(fragmentManager, lifecycle);
        this.NUM_TAB = CountTab;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new Tab1();
            case 1:
                return new Tab2();
            case 2:
                return new Tab3();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return NUM_TAB;
    }
}
