package com.sevenvcloud.nailbiter.teleporter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;

/**
 * Created by nailbiter on 29/04/15.
 */
public class SessionManager {
    protected static final String TAG = "session-manager-class";

    Teleporter mT = new Teleporter();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "TeleporterPref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "isLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_USERNAME = "username";

    // Email (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Driver (make variable public to access from outside)
    public static final String IS_DRIVER = "driver";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Create Login Session
    public void createLoginSession(){

        ParseUser currentUser = ParseUser.getCurrentUser();

        Log.d(TAG,"User : "+currentUser.getUsername());
        Log.d(TAG,"Email : "+currentUser.getEmail());
        Log.d(TAG,"Driver : "+currentUser.getBoolean("driver"));

        editor.putString(KEY_USERNAME,currentUser.getUsername());
        editor.putString(KEY_EMAIL,currentUser.getEmail());
        editor.putBoolean(IS_LOGIN, true);
        editor.putBoolean(IS_DRIVER, currentUser.getBoolean("driver"));
        editor.commit();
    }

    public void checkLogin(){
        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser == null){
            Intent i = new Intent(_context,LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }else{


            if(!currentUser.getBoolean("driver")) {
                Intent i = new Intent(_context, HomepageActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);

            }else{
                Intent i = new Intent(_context, DriverHomepageActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
            }
        }
    }

    public void logoutUser(){
        // Clear editor
        editor.clear();
        editor.commit();

        ParseUser.logOut();

        // After logout redirect user to login
        Intent i = new Intent(_context,LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        // user name

        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
        }

}
