package com.mamba.grapple;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class Results extends Activity {

    ArrayList<TutorObject> tutorList;
    ListView listView;
    SharedPreferences sharedPreferences;
    private boolean loggedIn = false;
    private boolean newService = false;


    // service related variables
    private boolean mBound = false;
    DBService mService;

    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        getActionBar().show();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            int distance = extras.getInt("distance");
            String distString = (distance == 1) ? distance + " Mile " : distance + " Miles";

            getActionBar().setTitle("Tutors Within " + distString);

            // get the tutor list from previous activity
            tutorList = extras.getParcelableArrayList("tutorList");
            Log.v("tutorList", String.valueOf(tutorList));


            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            mLastLocation = locationManager.getLastKnownLocation(locationProvider);


            // populate the list view
            TutorsAdapter adapter = new TutorsAdapter(this, tutorList);
            adapter.setUserLocation(mLastLocation);
            listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if (!loggedIn) {
                        // transfer the user to the register page
                        Intent intent = new Intent(Results.this, Register.class);
                        // we expect the auth response
                        startActivityForResult(intent, 1);
                    } else {
                        Log.v("Login status", "Logged in user");
                        TutorObject selectedTutor = tutorList.get(position);
                        Log.v("selected tutor", String.valueOf(selectedTutor));
                        // transition to specific tutors page
                        Intent intent = new Intent(Results.this, Tutor.class);
                        intent.putExtra("selectedTutor", selectedTutor);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    // check login status every time the activity gets shown
    public void onResume() {
        super.onResume();
        loginCheck();
    }

    protected void onPause() {
        super.onPause();
        // Unbind from the service
        if (mBound) {
            Log.v("Unbinding Service", "Results Activity");
            unbindService(mConnection);
            mBound = false;
        }
    }


    // handles the result of login/registration
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Log.v("Reached", "Auth Activity Result");
            String token = data.getStringExtra("token");
            if (token != null) {
                // creates a new service with session token
                createService(token);
            }
        }
    }


    public void loginCheck() {
        String token = getToken();
        if (token != null) {
            Log.v("Preference Token", token);
            loggedIn = true;
            Log.v("Login Status", "User has been logged in");
            Intent intent = new Intent(this, DBService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.v("Service Bound", "Results bound to service");
        }
    }

    public String getToken() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getString("token", null);
    }

    public void createService(String token) {
        Log.v("Preference Token", token);
        loggedIn = true;
        newService = true;
        Log.v("Login Status", "User has been logged in");
        startService(new Intent(this, DBService.class));
        bindService(new Intent(this, DBService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.v("Service Bound", "Results bound to new service");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generic, menu);

        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                //TODO
            case R.id.action_signout:
                Intent myIntent = new Intent(Results.this, SignIn.class);
                startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBService.LocalBinder binder = (DBService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            if (newService) {
                // send the token
                mService.setToken(getToken());
                newService = false;
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}
