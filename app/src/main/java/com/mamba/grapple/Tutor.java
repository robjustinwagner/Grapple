package com.mamba.grapple;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

public class Tutor extends FragmentActivity implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener {

    private MapFragment mapFragment;
    private GoogleMap sessionMap;


    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private TutorObject tutor;





    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorselect);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        mLastLocation = locationManager.getLastKnownLocation(locationProvider);


//        // Create a GoogleApiClient instance to collect GPS location
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        Log.v("Play Services", "Connecting on tutor page..");
//        // connect to the instance
//        mGoogleApiClient.connect();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // get the tutor data
        retrieveTutorInfo();


    }

//    public void onResume(){
//        super.onResume();
//        Log.v("Tutor View", "Resumed");
//        Bundle extras = getIntent().getExtras();
//        if(extras != null){
//            Log.v("Tutor View Extras", extras.keySet().toString());
//            LocationObject meetingPoint = extras.getParcelable("meetingPoint");
//             if(meetingPoint != null){
//                 Log.v("Tutor View", "Meeting Point Found");
//                 LatLng mP = new LatLng(meetingPoint.xPos, meetingPoint.yPos);
//                 sessionMap.addMarker(new MarkerOptions()
//                         .position(mP)
//                         .title("Meeting Point"));
//             }
//
//
//        }
//    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if(extras != null){
            Log.v("Tutor View", "new intent recieved ");
            LocationObject meetingPoint = extras.getParcelable("meetingPoint");
             if(meetingPoint != null){
                 Log.v("Tutor View", "Meeting Point Found");
                 LatLng mP = new LatLng(meetingPoint.xPos, meetingPoint.yPos);
                 sessionMap.addMarker(new MarkerOptions()
                         .position(mP)
                         .title("Meeting Point")
                         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                 addRoute(meetingPoint.xPos, meetingPoint.yPos);
                 Button grappleButton = (Button) findViewById(R.id.grappleButton);
                 grappleButton.setText("Start Session");
             }
        }
    }

    // enters the chat with the tutor
    public void grappleTutor(View view){
        Intent intent = new Intent(this, Chat.class);
        intent.putExtra("selectedTutor", tutor);
        startActivity(intent);
    }

    public void retrieveTutorInfo(){
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
            maxSession.setText("Max Session: " + String.valueOf(tutor.session.period) + " min" );

        }
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
        Log.v("Google Map Ready", "Adding tutor marker");
        LatLng tutorLoc = new LatLng(tutor.location.xPos, tutor.location.yPos);
        sessionMap = map;
        map.addMarker(new MarkerOptions()
                .position(tutorLoc)
                .title("Tutor"));
        if(mLastLocation != null ){
            LatLng userLoc = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            Log.v("mLastLocation Exists", "Adding user marker");
            map.addMarker(new MarkerOptions()
                    .position(userLoc)
                    .title("You"));


            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(tutorLoc).zoom(13).build();


            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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

        sessionMap.addMarker(new MarkerOptions()
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


    public void addRoute(double meetLat, double meetLong){

        // we will treat it as though the meeting point is the way point between the tutor and student
        double originLat = mLastLocation.getLatitude();
        double originLong = mLastLocation.getLongitude();
        double destLat = tutor.location.xPos;
        double destLong = tutor.location.yPos;

        // Origin of route
        String str_origin = "origin="+originLat+","+originLong;

        // Destination of route
        String str_dest = "destination="+destLat+","+destLong;

        // Waypoint (meeting point)
        String waypoints = "waypoints="+meetLat+","+meetLong;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        // do http request for route in separate thread
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

    }


    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Download Exception", e.toString());
        }finally{
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

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
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

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<LatLng> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<LatLng> doInBackground(String... jsonData) {

            JSONObject result;
            JSONArray routes;
            List<LatLng> lines =  new ArrayList<LatLng>();

            try{

                result  = new JSONObject(jsonData[0]);
                routes = result.getJSONArray("routes");

                // route distance
                long distanceForSegment = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");

                JSONArray studentSteps = routes.getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(0).getJSONArray("steps");

                JSONArray tutorSteps = routes.getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(1).getJSONArray("steps");

                for(int i=0; i < studentSteps.length(); i++) {
                    String polyline = studentSteps.getJSONObject(i).getJSONObject("polyline").getString("points");

                    for(LatLng p : decodePolyline(polyline)) {
                        lines.add(p);
                    }
                }

                for(int i=0; i < tutorSteps.length(); i++) {
                    String polyline = tutorSteps.getJSONObject(i).getJSONObject("polyline").getString("points");

                    for(LatLng p : decodePolyline(polyline)) {
                        lines.add(p);
                    }
                }


            }catch(Exception e){
                e.printStackTrace();
            }

            return lines;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<LatLng> lines) {
             sessionMap.addPolyline(new PolylineOptions().addAll(lines).width(4).color(Color.CYAN));

        }

        /** POLYLINE DECODER - http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java **/
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




}
