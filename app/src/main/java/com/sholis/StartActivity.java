package com.sholis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;

import com.sholis.web.WebInterface;

import java.util.concurrent.ExecutionException;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean darkModeOn = loadToggle(this);

        //saveToggle(getPreferenceManager().getContext(), !darkToggle.isChecked());
        if (darkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // Auto Login if the correct username and password is already stored locally
            SharedPreferences sharedPreferences = getSharedPreferences("PRIVATE_PREFERENCES", MODE_PRIVATE);
            String userName = sharedPreferences.getString("uName", "");//"No name defined" is the default value.
            String userPassword = sharedPreferences.getString("uPass", ""); //0 is the default value.

            boolean accepted = false;
            try {
                accepted = new StartActivity.TaskLogin().execute(userName, userPassword).get().equals("true");
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            if (accepted) {
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, Login.class));
            }
            overridePendingTransition( R.anim.fade_in, R.anim.fade_out );
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        finish();
    }

    private static Boolean loadToggle(Context context){
        final SharedPreferences sharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("toggle_value_theme", true);
    }

    private class TaskLogin extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            boolean accepted = WebInterface.authenticateUser(params[0], params[1], getSharedPreferences("PRIVATE_PREFERENCES", MODE_PRIVATE));
            return accepted ? "true" : "false";

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

}