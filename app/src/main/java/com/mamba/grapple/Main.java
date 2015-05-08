package com.mamba.grapple;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    LoginManager session;

    // service related variables
    private boolean mBound = false;
    DBService mService;


    SharedPreferences sharedPreferences;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new LoginManager(getApplicationContext());

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

    // check login status every time the activity gets shown
    protected void onResume(){
        super.onResume();
        if(session.isLoggedIn()){
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        if(session.isLoggedIn()){
            Log.v("checking login", "user logged in");
            getMenuInflater().inflate(R.menu.menu_account, menu);
        }else{
            Log.v("checking login", "user not logged in");
           getMenuInflater().inflate(R.menu.menu_signin, menu);
        }

        return super.onCreateOptionsMenu(menu);
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
                session.logout();
                Intent myIntent = new Intent(this, SignIn.class);
                startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}


