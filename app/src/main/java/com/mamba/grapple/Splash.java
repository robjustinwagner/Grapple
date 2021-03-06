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

    private String token;
    private boolean mBound = false;
    DBService mService;


    LoginManager session;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        // check to see if the user is logged in here
        session = new LoginManager(getApplicationContext());
//        loginCheck();

        if (session.isLoggedIn()) {
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

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBService.LocalBinder binder = (DBService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setSession(session);
            mService.connectSocket();
        }

        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}
