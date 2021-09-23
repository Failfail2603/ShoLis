package com.sholis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sholis.Adapter.FragmentAdapter;
import com.sholis.web.WebInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;
    ExtendedFloatingActionButton addItemButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);

        //Implementation TabLayout Navigation using ViewPager2
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.viewPager2);

        addItemButton = findViewById(R.id.floating_action_button_add_item);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonShowItemAddDialog(view);
            }
        });

        setSupportActionBar(toolbar);
        new TaskFillSuperMarketTabs().execute();
    }

    public void onButtonShowItemAddDialog(View view) {
        final BottomSheetDialog addDialog = new BottomSheetDialog(MainActivity.this, R.style.Theme_ShoLis_BottomSheet_Light);

        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addDialog.setCancelable(true);

        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_add_item, (ConstraintLayout)findViewById(R.id.bottomSheetAddItemContainer));
        addDialog.setContentView(bottomSheetView);



        final EditText nameEt = addDialog.findViewById(R.id.itemName);
        final EditText amountEt = addDialog.findViewById(R.id.itemAmount);
        final Button submitButton = addDialog.findViewById(R.id.itemSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TaskAddItem().execute(nameEt.getText().toString(), amountEt.getText().toString());
            }
        });

        addDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle item selection
        switch(item.getItemId()){
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.log_out:
                SharedPreferences sharedPreferences = getSharedPreferences("PRIVATE_PREFERENCES", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("uName", "");
                editor.putString("uPass", "");
                editor.putBoolean("toggle_value_theme", false);
                editor.apply();
                startActivity(new Intent(this, Login.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class TaskFillSuperMarketTabs extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            return WebInterface.getWebData("/Supermarket.php", "", getSharedPreferences("PRIVATE_PREFERENCES", MODE_PRIVATE));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonResult = new JSONArray(result);

                for (int i = 0; i < jsonResult.length(); i++) {
                    tabLayout.addTab(tabLayout.newTab());
                }
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

                fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), tabLayout.getTabCount());

                fragmentAdapter.supermarkets.clear();

                for (int i = 0; i < jsonResult.length(); i++) {
                    JSONObject jo = jsonResult.getJSONObject(i);
                    fragmentAdapter.supermarkets.add(new Supermarket(jo.getString("SUPERMARKET_NAME"), jo.getInt("SUPERMARKET_ID")));
                }

                viewPager2.setAdapter(fragmentAdapter);

                new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        try {
                            JSONObject jo = jsonResult.getJSONObject(position);
                            tab.setText(jo.getString("SUPERMARKET_NAME"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).attach();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private class TaskAddItem extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            Item item = new Item(params[0], params[1]);
            int superMarketId = fragmentAdapter.supermarkets.get(viewPager2.getCurrentItem()).id;
            System.out.println("Add new Item " + item.name + " " + item.amount + " " + superMarketId);
            return WebInterface.addNewItem(item, superMarketId, getSharedPreferences("PRIVATE_PREFERENCES", MODE_PRIVATE));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(result);
            fragmentAdapter.update();
        }
    }

}