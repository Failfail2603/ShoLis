package com.sholis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sholis.Adapter.FragmentAdapter;
import com.sholis.web.WebInterface;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Implementation TabLayout Navigation using ViewPager2
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.viewPager2);

        setSupportActionBar(toolbar);

        //name tabs
        tabLayout.addTab(tabLayout.newTab().setText("Tab1"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab2"));
        tabLayout.addTab(tabLayout.newTab().setText("Tab3"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle(),tabLayout.getTabCount());

        viewPager2.setAdapter(fragmentAdapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText("Tab" + (position + 1));
            }
        }).attach();


        //test elements
        Item banane = new Item(0,"Banane","1");
        Item apfel = new Item(1,"Apfel","1");

        ArrayList<Item> TestList = new ArrayList<Item>();
        TestList.add(banane);
        TestList.add(apfel);

        ShoppingList testShop = new ShoppingList(1, 1);
        WebInterface.getItemsFromShoppingList(testShop);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle item selection
        switch(item.getItemId()){
            case R.id.item1:
                Toast.makeText(this,"Item1 selected", Toast.LENGTH_SHORT).show();
                //do something
                return true;
            case R.id.item2:
                Toast.makeText(this,"Item2 selected", Toast.LENGTH_SHORT).show();
                //do something else
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}