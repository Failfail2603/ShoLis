package com.sholis.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sholis.Adapter.RecyclerViewItemAdapter;
import com.sholis.Item;
import com.sholis.R;

import java.util.ArrayList;

public class Tab1 extends Fragment{

    // own parameters
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tab1, container, false);
        recyclerView = v.findViewById(R.id.recyclerview);
        System.out.println(recyclerView);
        recyclerView.setHasFixedSize(true);   //better performance

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //testdata
        Item item1 = new Item(0,"Hallo","20");
        Item item2 = new Item(1,"Test","5");
        Item item3 = new Item(2,"Hi","1");
        Item item4 = new Item(3,"Ok","10");
        Item item5 = new Item(4,"hifes","7");


        ArrayList<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
        items.add(item5);

        RecyclerViewItemAdapter adapter = new RecyclerViewItemAdapter(items);
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return v;
    }
}