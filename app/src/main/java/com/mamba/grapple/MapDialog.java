package com.mamba.grapple;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by vash on 4/15/15.
 */
public class MapDialog extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap meetMap;
    private TutorObject tutor;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private MapFragment mapFragment;

    // received from Intent
    private LocationObject meetingPoint;
    private double tutorLat;
    private double tutorLon;

    // UI elements
    Button acceptBtn;
    Button declineBtn;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_map);

        // grab UI elements
        acceptBtn = (Button) findViewById(R.id.accept_button);
        declineBtn = (Button) findViewById(R.id.decline_button);

        // get current user  location
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        mLastLocation = locationManager.getLastKnownLocation(locationProvider);

        // load the google map
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // extract data sent from intent
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            meetingPoint = extras.getParcelable("meetingPoint");
            tutorLat = extras.getDouble("tutorLat");
            tutorLon = extras.getDouble("tutorLon");
        }


        // bind UI listeners
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send the data to the tutor view and finish
                Intent intent = new Intent(MapDialog.this, Tutor.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("meetingPoint", meetingPoint);
                startActivity(intent);
                finish();

            }
        });


        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // just close for now
            }
        });



    }



    @Override
    public void onMapReady(GoogleMap map) {
        Log.v("Google Map Ready", "Adding tutor marker");
        LatLng tutorLoc = new LatLng(tutorLat, tutorLon);
        LatLng meetPoint = new LatLng(meetingPoint.xPos, meetingPoint.yPos);
        meetMap = map;
        map.addMarker(new MarkerOptions()
                .position(tutorLoc)
                .title("Tutor"));

        map.addMarker(new MarkerOptions()
                .position(meetPoint)
                .title("Meeting Spot"));

        if(mLastLocation != null ){
            LatLng userLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.v("mLastLocation Exists", "Adding user marker");
            map.addMarker(new MarkerOptions()
                    .position(userLoc)
                    .title("You"));


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(tutorLoc).zoom(13).build();

            map.moveCamera( CameraUpdateFactory.newLatLngZoom( meetPoint , 14.0f) );
//            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//            // Create a LatLngBounds that includes tutor/student area (work in progress)
//             LatLngBounds bounds = new LatLngBounds(
//                    new LatLng(tutor.location.xPos, tutor.location.yPos), new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//
//            // Set the camera to the greatest possible zoom level that includes the
//            // bounds
//            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.getCenter(), 10));
        }

    }




    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
        Log.v("gConnected", "Connected to google play services");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        Log.v("latitude", String.valueOf(mLastLocation.getLatitude()));

        meetMap.addMarker(new MarkerOptions()
                .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                .title("You"));

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