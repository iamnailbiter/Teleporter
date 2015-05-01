package com.sevenvcloud.nailbiter.teleporter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // Shared Preference
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("TeleporterPref", MODE_PRIVATE);

        // Sleep Splash
        new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {

                // Session checking if user not logged
                if(pref.getBoolean("isLogged", false)==false){

                    // Take user to the Login
                    Intent takeUserRegister = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(takeUserRegister);
                    finish();

                }else{

                    // Take user to the Home
                    Intent takeUserRegister = new Intent(SplashActivity.this, HomepageActivity.class);
                    startActivity(takeUserRegister);
                    finish();

                }
            }
        }, 1*1000); // wait for 1 seconds
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
