package com.mamba.grapple;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vash on 4/29/15.
 */
public class Main extends FragmentActivity {

    private TabHost mTabHost;

    ActionBar actionBar;
    ViewPager viewPager;
    FragmentPagerAdapter fragPageAdapter;

    // service related variables
    private boolean mBound = false;
    DBService mService;

    private boolean loggedIn = false;

    SharedPreferences sharedPreferences;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mTabHost = (TabHost) findViewById(R.id.tabHost);
        mTabHost.setup();

        TabHost.TabSpec ts = mTabHost.newTabSpec("student");
        ts.setContent(R.id.tab1);
        ts.setIndicator("Student");

        mTabHost.addTab(ts);

        ts = mTabHost.newTabSpec("tutor");
        ts.setContent(R.id.tab2);
        ts.setIndicator("Tutor");
        mTabHost.addTab(ts);
        mTabHost.setBackgroundColor(Color.WHITE);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
               Log.v("tab switch ", tabId);
            }
        });


    }



//    /**
//     * Used to create tabview object and setting parameters
//     *
//     * @param context
//     * @param text
//     * @return
//     */
//    private View createTabView(final Context context, final String text) {
//        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout,
//                null);
//        Button _button = (Button) view.findViewById(R.id.tabText);
//        _button.setText(text);
//        return view;
//    }


    // check login status every time the activity gets shown
    protected void onResume(){
        super.onResume();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sharedPreferences.getString("token", null);
        if(token != null) {
            loggedIn = true;
            Log.v("Search Login Status", "User has been logged in");
            Intent intent = new Intent(this, DBService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        }
    }

    protected void onPause(){
        super.onPause();
        // Unbind from the service
        if (mBound){
            Log.v("Unbinding Service", "Search Activity");
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection(){
        public void onServiceConnected(ComponentName className, IBinder service){
            DBService.LocalBinder binder = (DBService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

        }

        public void onServiceDisconnected(ComponentName arg0){
            mBound = false;
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



}


