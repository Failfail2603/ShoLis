package com.sholis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sholis.web.WebInterface;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ShoppingList testShop = new ShoppingList(1,1);
        WebInterface.getItemsFromShoppingList(testShop);


    }
}