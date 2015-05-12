package com.sevenvcloud.nailbiter.teleporter;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;


public class HomepageActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "homepage-activity";

    protected TextView mUsernameTextView;
    protected TextView mEmailTextView;
    protected TextView mPositionTextView;
    protected TextView mAddressTextView;
    protected TextView mLastLocUpdateTimeTextView;
    protected Button mLocateButton;
    protected Button mLogoutButton;

    protected Boolean mRequestingLocationUpdates;
    protected Boolean mRequestingAddressUpdates;
    protected String mAddressOutput;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private String mLastLocUpdateTime;
    private AddressResultReceiver mResultReceiver;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Shared Preference
        final SessionManager mSM = new SessionManager(getApplicationContext());

        // Address Result
        mResultReceiver = new AddressResultReceiver(new Handler());

        // Initialize
        mUsernameTextView = (TextView)findViewById(R.id.usernameHomeTextView);
        mEmailTextView = (TextView)findViewById(R.id.emailHomeTextView);
        mPositionTextView = (TextView)findViewById(R.id.positionHomeTextView);
        mAddressTextView = (TextView)findViewById(R.id.addressHomeTextView);
        mLastLocUpdateTimeTextView = (TextView)findViewById(R.id.lastLocUpdateTimeHomeTextView);
        mLocateButton = (Button)findViewById(R.id.locateHomebutton);
        mLogoutButton = (Button)findViewById(R.id.logoutHomebutton);

        // Request Default State
        mRequestingLocationUpdates = false;
        mRequestingAddressUpdates = false;

        // Default Null Value
        mLastLocUpdateTime = "";
        mAddressOutput = "";

        // Show user detail
        mUsernameTextView.setText("Username : "+mSM.pref.getString(mSM.KEY_USERNAME,null));
        mEmailTextView.setText("Email : "+mSM.pref.getString(mSM.KEY_EMAIL,null));

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Build Google API Client
        buildGoogleApiClient();

        // Listen to Locate Button click
        mLocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mRequestingLocationUpdates) {
                    mRequestingLocationUpdates = false;
                    mRequestingAddressUpdates = false;
                    stopLocationUpdates();
                    setLocateButtonsEnabledState();
                } else {
                    mRequestingLocationUpdates = true;
                    mRequestingAddressUpdates = true;
                    startLocationUpdates();
                    startAddressUpdates();
                    setLocateButtonsEnabledState();
                }
            }
        });

        // Listen to Logout Button click
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast logout message
                Toast.makeText(HomepageActivity.this, "Thank you", Toast.LENGTH_LONG).show();

                mSM.logoutUser();
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connecting Google API Client
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Disconnect Google API Client
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume Service
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
            startAddressUpdates();
        }
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

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                setLocateButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastLocUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }

            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mRequestingAddressUpdates = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }

            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }


            updateUI();
        }
    }

    private void setLocateButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mLocateButton.setText("Stop Updating Position");
        } else {
            mLocateButton.setText("Update Position!");
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "Building");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    private void updateUI() {
        Log.d(TAG, "Update Latitude : " + String.valueOf(mCurrentLocation.getLatitude()));
        Log.d(TAG, "Update Longitude : " + String.valueOf(mCurrentLocation.getLongitude()));
        mPositionTextView.setText("Position : " + String.valueOf(mCurrentLocation.getLatitude() + "," + String.valueOf(mCurrentLocation.getLongitude())));
        mLastLocUpdateTimeTextView.setText("Last Update Position : " + mLastLocUpdateTime);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastLocUpdateTime);
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mRequestingAddressUpdates);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG,"I'm Connected!");

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            Log.d(TAG,"Latitude : "+String.valueOf(mLastLocation.getLatitude()));
            Log.d(TAG, "Longitude : " + String.valueOf(mLastLocation.getLongitude()));

            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Log.d(TAG, String.valueOf(R.string.no_geocoder_available));
                return;
            }

            mPositionTextView.setText("Position : " + String.valueOf(mLastLocation.getLatitude() + "," + String.valueOf(mLastLocation.getLongitude())));

            if (mRequestingAddressUpdates) {
                startAddressUpdates();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed :" + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastLocUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        startAddressUpdates();
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.

            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a log message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.d(TAG,getString(R.string.address_found));
            }
        }
    }

    protected void startAddressUpdates() {
        Location mAddressLocation;
        if(mCurrentLocation==null){
            mAddressLocation = mLastLocation;
        }else{
            mAddressLocation = mCurrentLocation;
        }
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        mResultReceiver = new AddressResultReceiver(new Handler());
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mAddressLocation);
        startService(intent);
    }

    protected void displayAddressOutput() {
        mAddressTextView.setText("Address : "+mAddressOutput);
    }

}