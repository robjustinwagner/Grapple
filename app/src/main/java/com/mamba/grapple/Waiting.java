package com.mamba.grapple;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;


public class Waiting extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private MapFragment mapFragment;
    Button grappleButton;
    LoginManager session;

    private Location mLastLocation;

    // service related variables
    private boolean mBound = false;
    DBService mService;


    // receiver to handle server responses for this activity
    private BroadcastReceiver waitingReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // intent can contain any data
            Bundle extras = intent.getExtras();

            if(extras != null){
                String responseType = extras.getString("responseType");
                Log.v("responseType", responseType);
                Log.v("Waiting Activity", "received response: " + responseType);
                if(responseType == "grapple"){
                    // take them to chat view with user
                    UserObject user = extras.getParcelable("user");
                    Intent chatIntent = new Intent(Waiting.this, Chat.class);
                    chatIntent.putExtra("user", user);
                    startActivity(chatIntent);
                }

            }
        }
    };


    // receiver to handle multicast responses
    private BroadcastReceiver multicastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {

            // intent can contain any data
            Bundle extras = intent.getExtras();

            if(extras != null){
                String responseType = extras.getString("responseType");
                Log.v("responseType", responseType);
                Log.v("Waiting Activity", "received response: " + responseType);
            }
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorselect);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("location")) {
            mLastLocation = extras.getParcelable("location");
            Log.v("Current user location",  mLastLocation.getLatitude() +  " , " + mLastLocation.getLongitude());
        }

        grappleButton = (Button) findViewById(R.id.grappleButton);
        grappleButton.setVisibility(View.GONE);

        // track user session data
        session = new LoginManager(getApplicationContext());

        //create map
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }


    public void onResume(){
        super.onResume();
        if (session.isLoggedIn()) {
            createService();
        }

        // register broadcast receiver for this activity
        LocalBroadcastManager.getInstance(this).registerReceiver(waitingReceiver,
                new IntentFilter("waitingReceiver"));


        // register broadcast receiver for this activity
        LocalBroadcastManager.getInstance(this).registerReceiver(multicastReceiver,
                new IntentFilter("multicastReceiver"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(multicastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(waitingReceiver);
    }

    @Override
    public void onBackPressed(){
        endBroadcastPrompt();
    }

    public void createService(){
        Log.v("Waiting Page", "Creating Service..");
        startService(new Intent(this, DBService.class));
        bindService(new Intent(this, DBService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.v("Service Bound", "Results bound to new service");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signin, menu);

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
                Intent myIntent = new Intent(Waiting.this, SignIn.class);
                myIntent.putExtra("destroy_token", "true");
                startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        LatLng userLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLoc, 11));

        // TODO: get distance travelled radius from current user data and show it on map
        Float distance = session.getCurrentUser().travelDistance();
        Log.v("Tutor travel dist", distance +"");
        googleMap.addCircle(new CircleOptions()
                .center(userLoc)
                .radius(distance * 1609.34)
                .strokeColor(Color.DKGRAY)
                .strokeWidth(2)
                .fillColor(Color.parseColor("#500084d3"))
                .visible(true));

    }


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBService.LocalBinder binder = (DBService.LocalBinder) service;
            mService = binder.getService();
            mService.setSession(session);
            mBound = true;
            mLastLocation = mService.getLocation();
        }
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    private void endBroadcastPrompt(){
        new AlertDialog.Builder(this)
                .setTitle("Stop Broadcast")
                .setMessage("Are you sure you want to stop broadcasting?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with ending broadcast
                        mService.endBroadcast();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}
