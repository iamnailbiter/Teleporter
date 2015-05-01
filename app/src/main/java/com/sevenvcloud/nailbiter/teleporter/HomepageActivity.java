package com.sevenvcloud.nailbiter.teleporter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashMap;


public class HomepageActivity extends Activity {

    protected TextView mUsernameTextView;
    protected TextView mEmailTextView;
    protected Button mLocateButton;
    protected Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Shared Preference
        final SessionManager mSM = new SessionManager(getApplicationContext());


        // Session checking if user not logged
        mSM.checkLogin();

        // Initialize
        mUsernameTextView = (TextView)findViewById(R.id.usernameHomeTextView);
        mEmailTextView = (TextView)findViewById(R.id.emailHomeTextView);
        mLocateButton = (Button)findViewById(R.id.locateHomebutton);
        mLogoutButton = (Button)findViewById(R.id.logoutHomebutton);

        // Show user detail
        mUsernameTextView.setText("Username : "+mSM.pref.getString(mSM.KEY_USERNAME,null));
        mEmailTextView.setText("Email : "+mSM.pref.getString(mSM.KEY_EMAIL,null));

        // Listen to Locate Button click
        mLocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });




        // Listen to Logout Button click
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomepageActivity.this, "Logout Clicked", Toast.LENGTH_LONG).show();
                mSM.logoutUser();

                // Take user to the Login
                Intent takeUserRegister = new Intent(HomepageActivity.this, LoginActivity.class);
                startActivity(takeUserRegister);
                finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
