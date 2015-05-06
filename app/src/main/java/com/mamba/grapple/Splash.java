package com.mamba.grapple;

// *android imports*

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.mamba.grapple.DBService;

public class Splash extends Activity {

    /**
     * Duration of wait *
     */
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    public final static String EXTRA_MESSAGE = "com.mamba.grapple.MESSAGE";

    private boolean loggedIn = false;
    private String token;
    private boolean mBound = false;
    DBService mService;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getActionBar().hide();

        // check to see if the user is logged in here
        loginCheck();

        if (loggedIn) {
            // start the background networking thread and open up socket connection
            Log.v("DBService", "Binding DBService from Splash..");
            // we must start and bind the service so we have control of its lifecycle
            startService(new Intent(this, DBService.class));
            bindService(new Intent(this, DBService.class), mConnection, Context.BIND_AUTO_CREATE);
        }

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Splash.this, Main.class);
                mainIntent.putExtra("loggedIn", loggedIn);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    public void loginCheck() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sharedPreferences.getString("token", null);
        Log.v("Splash token", token);
        if (token != null) {
            Log.v("Preference Token", token);
            loggedIn = true;
            Log.v("Login Status", "User has been logged in");

        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBService.LocalBinder binder = (DBService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            // send the token
            mService.setToken(token);
            mService.connectSocket();
        }

        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}
