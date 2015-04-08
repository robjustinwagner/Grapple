package com.mamba.grapple;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

public class Tutor extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {

    private MapFragment mapFragment;
    private GoogleMap map;


    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private TutorObject tutor;

    //UI Elements
    TextView tutorName;
    TextView tutorPrice;
    TextView maxSession;
    TextView distance;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorselect);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // get the tutor data
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            tutor = extras.getParcelable("selectedTutor");

            // Look up view for data population
            TextView tutorName = (TextView)findViewById(R.id.tutorName);
            TextView tutorDistance = (TextView)findViewById(R.id.tutorDistance);
            TextView tutorPrice = (TextView)findViewById(R.id.tutorPrice);
            TextView maxSession = (TextView) findViewById(R.id.maxSession);

            // populate the data
            tutorName.setText(tutor.firstName + " " + tutor.lastName);
            tutorDistance.setText(String.valueOf(tutor.distance) + " mi");
            tutorPrice.setText("$" + String.valueOf(tutor.session.price));
            maxSession.setText(String.valueOf(tutor.session.period));
        }

        // Create a GoogleApiClient instance to collect GPS location
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // connect to the instance
        mGoogleApiClient.connect();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutor, menu);
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

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(tutor.location.xPos, tutor.location.yPos))
                .title("Tutor"));
        if(mLastLocation != null ){
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    .title("You"));


            // Create a LatLngBounds that includes Australia.
             LatLngBounds bounds = new LatLngBounds(
                    new LatLng(tutor.location.xPos, tutor.location.yPos), new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

            // Set the camera to the greatest possible zoom level that includes the
            // bounds
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
        }

    }



    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
        Log.v("gConnected", "Connected to google play services");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

    }

    @Override
    public void onConnectionSuspended(int cause){
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Log.v("fail", "Connection to Google Services Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.
        Log.v("fail", "Connection to Google Services Failed");

    }



}
