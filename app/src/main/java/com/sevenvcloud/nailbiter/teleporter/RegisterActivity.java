package com.sevenvcloud.nailbiter.teleporter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class RegisterActivity extends Activity {
    protected static final String TAG = "register-activity";

    protected EditText mUserName;
    protected EditText mUserEmail;
    protected EditText mUserPassword;
    protected Button mRegButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Shared Preference
        final SessionManager mSM = new SessionManager(getApplicationContext());

        // Initialize
        mUserName = (EditText)findViewById(R.id.usernameRegEditText);
        mUserEmail = (EditText)findViewById(R.id.emailRegEditText);
        mUserPassword = (EditText)findViewById(R.id.passwordRegEditText);
        mRegButton = (Button)findViewById(R.id.registerRegButton);

        // Listen to Register Button Click
        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the user data
                final String username = mUserName.getText().toString().trim();
                String password = mUserPassword.getText().toString().trim();
                String email = mUserEmail.getText().toString().trim();

                if(TextUtils.isEmpty(username)||TextUtils.isEmpty(password)||TextUtils.isEmpty(email)){

                    // Blank data. advice user
                    Toast.makeText(RegisterActivity.this,"Please insert data correctly", Toast.LENGTH_LONG).show();

                }else{

                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.put("driver", false);

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                // User Signed Up successfully
                                Toast.makeText(RegisterActivity.this,"Success! Welcome", Toast.LENGTH_LONG).show();

                                // Create login Session
                                mSM.createLoginSession();

                                // Take user to the Homepage
                                mSM.checkLogin();
                                finish();

                            }else{
                                // Error on Signing Up user. advice user
                                Toast.makeText(RegisterActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
