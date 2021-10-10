package com.sholis.activity;

import android.os.Bundle;

import com.sholis.R;
import com.sholis.adapter.RVSupermarketAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AddShoppingListActivity extends AppCompatActivity {

    private RecyclerView supermarketsRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_list);

        supermarketsRV = findViewById(R.id.rvSupermarkets);

        supermarketsRV.setHasFixedSize(true);   //better performance

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        supermarketsRV.setLayoutManager(layoutManager);

        RVSupermarketAdapter adapter = new RVSupermarketAdapter(this);
        supermarketsRV.setAdapter(adapter);
    }
}