package com.sevenvcloud.nailbiter.teleporter;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.parse.Parse;

/**
 * Created by nailbiter on 28/04/15.
 */
public class Teleporter extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Parse Connect
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "bu4HYIFnkALslxxtKAm1W3CnZobwpYvCRfuC4T0b", "lEU1Wbggz2z7sQfBBrb3q8nh2kGVDU6MDUCajVZe");

    }
}
