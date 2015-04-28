package com.sevenvcloud.nailbiter.teleporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseUser;


public class LoginActivity extends Activity {

    protected EditText mUserName;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected Button mRegButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Parse Connect
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "bu4HYIFnkALslxxtKAm1W3CnZobwpYvCRfuC4T0b", "lEU1Wbggz2z7sQfBBrb3q8nh2kGVDU6MDUCajVZe");

        // Initialize
        mUserName = (EditText)findViewById(R.id.usernameLoginTextBox);
        mPassword = (EditText)findViewById(R.id.passwordLoginTextBox);
        mLoginButton = (Button)findViewById(R.id.loginLogButton);
        mRegButton = (Button)findViewById(R.id.registerLogButton);

        // Listen to Login Button Click

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the user inputs from edittext and convert to string
                String username = mUserName.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                // login the user using parse sdk
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, com.parse.ParseException e) {
                        if(e == null){
                            // Successfully Login!
                            Toast.makeText(LoginActivity.this,"Welcome Back!", Toast.LENGTH_LONG).show();

                            // Take user to the Homepage
                            Intent takeUserHome = new Intent(LoginActivity.this, HomepageActivity.class);
                            startActivity(takeUserHome);

                        }else{
                            // Error on Login! advice user
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage(e.getMessage());
                            builder.setTitle("Sorry!");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Close the dialog
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                    }
                });
            }
        });

        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takeUserRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(takeUserRegister);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
