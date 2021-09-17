package com.sholis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean darkModeOn = loadToggle(this);
        System.out.println(darkModeOn);

        //saveToggle(getPreferenceManager().getContext(), !darkToggle.isChecked());
        if (darkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);



        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition( R.anim.fade_in, R.anim.fade_out );
    }

    private static Boolean loadToggle(Context context){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("toggle_value_theme", true);
    }

}