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

        Item banane = new Item(0,"Banane","1");
        Item apfel = new Item(1,"Apfel","1");

        ArrayList<Item> TestList = new ArrayList<Item>();
        TestList.add(banane);
        TestList.add(apfel);

        ShoppingList testShop = new ShoppingList(0,0,TestList);



        WebInterface.getItemsFromShoppingList(testShop, 1, 1);


    }
}