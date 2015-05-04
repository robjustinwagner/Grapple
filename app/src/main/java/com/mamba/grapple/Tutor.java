package com.mamba.grapple;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;

import android.media.Image;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tutor extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private MapFragment mapFragment;
    private GoogleMap gMap;


    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private TutorObject tutor;

    // service related variables
    private boolean mBound = false;
    DBService mService;

    private LocationRequest mLocationRequest;

    Marker tutorMarker;
    Marker studentMarker;

    GoogleMap.InfoWindowAdapter iwadapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorselect);
        getActionBar().show();

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        mLastLocation = locationManager.getLastKnownLocation(locationProvider);


//        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Drive.API)
//                .addScope(Drive.SCOPE_FILE)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//        mGoogleApiClient.connect();

//        // Create the LocationRequest object
//        mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
//                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


//        // Define a listener that responds to location updates
//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//                // Called when a new location is found by the network location provider.
//                mLastLocation = location;
//
//
//                if(studentMarker != null){
//                    Log.v("location updated", "Removing Maker");
//                    studentMarker.remove();
//
//                    LatLng userLoc = new LatLng(location.getLatitude(), location.getLongitude());
//                    studentMarker = gMap.addMarker(new MarkerOptions()
//                            .position(userLoc)
//                            .title("You"));
//
//
//                }
//
//
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//            public void onProviderEnabled(String provider) {}
////
////            public void onProviderDisabled(String provider) {}
////        };
//
//        // Register the listener with the Location Manager to receive location updates
//        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // get the tutor data
        retrieveTutorInfo();


        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("locationUpdate"));


    }

    public void onResume() {
        super.onResume();
        Log.v("Tutor View", "Resumed");
        Intent intent = new Intent(this, DBService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        Log.v("Service Bound", "Tutor select bound to service");
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

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };


    // response when meeting point is accepted
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.v("Tutor View", "new intent received ");
            final LocationObject meetingPoint = extras.getParcelable("meetingPoint");
            if (meetingPoint != null) {
                Log.v("Tutor View", "Meeting Point Found");
                final LatLng mP = new LatLng(meetingPoint.xPos, meetingPoint.yPos);
                gMap.addMarker(new MarkerOptions()
                        .position(mP)
                        .title("Meeting Point")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                addRoute(meetingPoint.xPos, meetingPoint.yPos);


                // grab dynamic layout items
                Button grappleButton = (Button) findViewById(R.id.grappleButton);
                ImageButton chatButton = (ImageButton) findViewById(R.id.chatButton);

                // hide the grapple button and show the session/chat buttons
                grappleButton.setVisibility(View.GONE);
                chatButton.setVisibility(View.VISIBLE);


                // change to session infowindow
                iwadapter = new SessionWindowAdapter();
                gMap.setInfoWindowAdapter(iwadapter);
                iwadapter.getInfoWindow(tutorMarker);
                tutorMarker.showInfoWindow();


                gMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(Tutor.this, InSession.class);
                        intent.putExtra("tutor", tutor);
                        startActivity(intent);
                        finish();
                    }
                });


                chatButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Tutor.this, Chat.class);
                        intent.putExtra("selectedTutor", tutor);
                        intent.putExtra("meetingPoint", meetingPoint);  // if the meeting point is added we know the tutor has been grappled
                        startActivity(intent);
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // display locally
                        tutorMarker.setPosition(mP);
                    }
                }, 4000);


            }
        }
    }

    // enters the chat with the tutor
    public void grappleTutor(View view) {
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("selectedTutor", tutor);
        startActivity(intent);
    }

    public void retrieveTutorInfo() {
        // get the tutor data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tutor = extras.getParcelable("selectedTutor");

            // Look up view for data population
            TextView tutorName = (TextView) findViewById(R.id.tutorName);
            TextView tutorDistance = (TextView) findViewById(R.id.tutorDistance);
            TextView tutorPrice = (TextView) findViewById(R.id.tutorPrice);

            RatingBar tutorRating = (RatingBar) findViewById(R.id.ratingBar);
            ImageView tutorPic = (ImageView) findViewById(R.id.imageView);


            String fullName = tutor.firstName + " " + tutor.lastName;

            // populate the data
            tutorName.setText(fullName);
            tutorDistance.setText(tutor.getDistance(mLastLocation) + " mi");
            tutorPrice.setText("$" + String.valueOf(tutor.session.price));
            tutorRating.setRating(tutor.rating);


            // TEMP DUMMY TUTORS
            switch (tutor.firstName) {
                case "Jess":
                    tutorPic.setImageResource(R.drawable.jess);
                    break;
                case "Eric":
                    tutorPic.setImageResource(R.drawable.eric);
                    break;
                case "Robert":
                    tutorPic.setImageResource(R.drawable.robert);
                    break;
                case "Nadia":
                    tutorPic.setImageResource(R.drawable.nadia);
                    break;
            }


        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBService.LocalBinder binder = (DBService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


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
                Intent myIntent = new Intent(Tutor.this, SignIn.class);
                startActivity(myIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.v("Google Map Ready", "Adding tutor marker");
        LatLng tutorLoc = new LatLng(tutor.location.xPos, tutor.location.yPos);
        int zoom;
        gMap = map;
        map.setMyLocationEnabled(true);
        tutorMarker = gMap.addMarker(new MarkerOptions()
                .position(tutorLoc)
                .title("Tutor")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.markersmall)));

        iwadapter = new TutorWindowAdapter();
        gMap.setInfoWindowAdapter(iwadapter);
        iwadapter.getInfoWindow(tutorMarker);

        if (mLastLocation != null) {
            LatLng userLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            Log.v("mLastLocation Exists", "Adding user marker");

            Double distance = Double.parseDouble(tutor.getDistance(userLoc));

            zoom = (distance < 1) ? 14 : 13;

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(tutorLoc).zoom(zoom).build();

            gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    tutorMarker.showInfoWindow();
                }

                @Override
                public void onCancel() {

                }
            });

        }

    }


    @Override
    public void onConnected(Bundle connectionHint) {
//        // Connected to Google Play services!
        // The good stuff goes here.
        Log.v("gConnected", "Connected to google play services");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if (mLastLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }


        Log.v("latitude", String.valueOf(mLastLocation.getLatitude()));

//        gMap.addMarker(new MarkerOptions()
//                .position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
//                .title("You"));

    }

    @Override
    public void onConnectionSuspended(int cause) {
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
        Log.v("fail result", result.toString());

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

    }


    public void addRoute(double meetLat, double meetLong) {

        // we will treat it as though the meeting point is the way point between the tutor and student
        double originLat = mLastLocation.getLatitude();
        double originLong = mLastLocation.getLongitude();
        double destLat = tutor.location.xPos;
        double destLong = tutor.location.yPos;

        // Origin of route
        String str_origin = "origin=" + originLat + "," + originLong;

        // Destination of route
        String str_dest = "destination=" + destLat + "," + destLong;

        // Waypoint (meeting point)
        String waypoints = "waypoints=" + meetLat + "," + meetLong;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        // do http request for route in separate thread
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }


    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Download Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service

            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<LatLng>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<LatLng> doInBackground(String... jsonData) {

            JSONObject result;
            JSONArray routes;
            List<LatLng> lines = new ArrayList<LatLng>();

            try {

                result = new JSONObject(jsonData[0]);
                routes = result.getJSONArray("routes");

                // route distance
                long distanceForSegment = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");

                JSONArray studentSteps = routes.getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(0).getJSONArray("steps");

                JSONArray tutorSteps = routes.getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(1).getJSONArray("steps");

                for (int i = 0; i < studentSteps.length(); i++) {
                    String polyline = studentSteps.getJSONObject(i).getJSONObject("polyline").getString("points");

                    for (LatLng p : decodePolyline(polyline)) {
                        lines.add(p);
                    }
                }

                for (int i = 0; i < tutorSteps.length(); i++) {
                    String polyline = tutorSteps.getJSONObject(i).getJSONObject("polyline").getString("points");

                    for (LatLng p : decodePolyline(polyline)) {
                        lines.add(p);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return lines;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<LatLng> lines) {
            gMap.addPolyline(new PolylineOptions().addAll(lines).width(4).color(Color.CYAN));
        }

        /**
         * POLYLINE DECODER - http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java *
         */
        private List<LatLng> decodePolyline(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();

            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
                poly.add(p);
            }

            return poly;
        }

    }


    class TutorWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        TutorWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.tutorinfowindow, null);
        }


        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tutorName = (TextView) myContentsView.findViewById(R.id.tutorName);
            ImageView profilePic = (ImageView) myContentsView.findViewById(R.id.profilePic);

            tutorName.setText(tutor.firstName + " " + tutor.lastName);
            int x = R.drawable.jess;
            profilePic.setImageResource(x);
            return myContentsView;
        }
    }


    class SessionWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        SessionWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.sessionstart_infowindow, null);
        }


        @Override
        public View getInfoWindow(Marker marker) {
            return null;

        }

        @Override
        public View getInfoContents(Marker marker) {
            ImageView profilePic = (ImageView) myContentsView.findViewById(R.id.profilePic);
            int x = R.drawable.jess;
            profilePic.setImageResource(x);
            return myContentsView;
        }
    }


}
