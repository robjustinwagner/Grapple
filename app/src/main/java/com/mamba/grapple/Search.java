package com.mamba.grapple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;


// *json imports*
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Search extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private boolean loggedIn = false;
    SharedPreferences sharedPreferences;

    // UI Elements
    ListView listView;
    SeekBar seekBar;
    TextView distanceView;
    Button search;
    View selected = null;

    // Request Params
    int distance = 0;
    String course;
    String currLat;
    String currLong;

//    // service related variables
//    private boolean mBound = false;
//    DBService mService;

    // temporary until DB load setup (use SimpleCursorAdapter for DB)
    static final String[] COURSES = {"Chemistry 103", "Comp Sci 302", "French 4", "Math 234", "Physics 202"};
    // current url path for tutor list retrieval
    static final String TUTOR_PATH = "http://protected-dawn-4244.herokuapp.com/tutors";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // grab all the view items and set defaults
        initialize();

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        mLastLocation = locationManager.getLastKnownLocation(locationProvider);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // If a previous item was selected unhighlight it
                if(selected != null){
                    selected.setBackgroundColor(Color.TRANSPARENT);
                    parent.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
                }

                // highlight the selected item
                selected = view;
                selected.setBackgroundColor(Color.rgb(62, 175, 212));

            }
        });


        // update distance as user slides
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceView.setText("Travel Distance: " + distance + " mi");
            }
        });




//        // Create a GoogleApiClient instance
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//
//        // connect to the instance
//        mGoogleApiClient.connect();

    }


//    // check login status every time the activity gets shown
//    protected void onResume(){
//        super.onResume();
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String token = sharedPreferences.getString("token", null);
//        if(token != null) {
//            loggedIn = true;
//            Log.v("Search Login Status", "User has been logged in");
//            Intent intent = new Intent(this, DBService.class);
//            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//
//        }
//    }
//
//    protected void onPause(){
//        super.onPause();
//        // Unbind from the service
//        if (mBound){
//            Log.v("Unbinding Service", "Search Activity");
//            unbindService(mConnection);
//            mBound = false;
//        }
//    }



    // A private method to help us initialize our default variables and settings
    private void initialize() {
        seekBar = (SeekBar) getView().findViewById(R.id.seekBar2);
        listView = (ListView) getView().findViewById(R.id.list);
        distanceView = (TextView) getView().findViewById(R.id.textView5);
        search = (Button) getView().findViewById(R.id.button);

        //add elements from array to list view
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.row, COURSES){

            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                final View renderer = super.getView(position, convertView, parent);
                if (position == 0)
                {
                    // highlight the first list item by default
                    renderer.setBackgroundColor(Color.rgb(62, 175, 212));
                }
                return renderer;
            }
        });


        // select first list item
        selected = listView.getAdapter().getView(0, null, listView);


        // set initial distance
        distance = seekBar.getProgress();
        distanceView.setText("Travel Distance: " + distance + " mi");

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tutorSearch(v);
            }
        });

    }


    // on search button click get the relevant  tutor list and show results
    public void tutorSearch(View view){

        course = String.valueOf(((TextView) selected).getText());
        Log.v("distance", "" + distance);
        Log.v("course", course);


        // make sure we have the GPS location
        if(mLastLocation != null) {

            currLat = String.valueOf(mLastLocation.getLatitude());
            currLong = String.valueOf(mLastLocation.getLongitude());

        }else {
            currLat = "43.076592";
            currLong = "-89.412487";
        }


            // log the current coordinates
            Log.v("currentLocation", "(" + currLat + "," + currLong + ")");


            //  send the data in a http request
            ConnectivityManager conMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
            // if there is a network connection create a request thread
            if(networkInfo != null && networkInfo.isConnected()){
               new TutorRetrieval().execute(TUTOR_PATH);
            }else{
                Log.v("no connection", "Failed to connect to internet");
            }




    }

//    private ServiceConnection mConnection = new ServiceConnection(){
//        public void onServiceConnected(ComponentName className, IBinder service){
//            DBService.LocalBinder binder = (DBService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//
//        }
//
//        public void onServiceDisconnected(ComponentName arg0){
//            mBound = false;
//        }
//    };






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


    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class TutorRetrieval extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);

            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result){
            Log.v("postResult", result);
            Gson gson = new Gson();

            ArrayList<TutorObject> tutorList = new ArrayList<>();
            Type resultType = new TypeToken<ArrayList<TutorObject>>(){}.getType();
            tutorList = gson.fromJson(result, resultType);

            Intent intent = new Intent(getActivity(), Results.class);

            Log.v("tutorList", String.valueOf(tutorList.size()));

            // dummy populate the empty list for now
            if(tutorList.size() < 1){
                dummyPopulate(tutorList);
            }

            // send the tutorList along with login status on to the results activity
            intent.putParcelableArrayListExtra("tutorList", tutorList);
            intent.putExtra("distance", distance);
            startActivity(intent);

        }
    }



    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;
        String line;

        // add all tutor request data to params list and build url query
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("course", course )
                .appendQueryParameter("distance", String.valueOf(distance))
                .appendQueryParameter("lat", currLat )
                .appendQueryParameter("lon", currLong);
        String query = builder.build().getEncodedQuery();

        myurl += "?" + query;           // append encoded query to URL
        Log.v("queriedURL", myurl);
        try {
            Log.v("url", myurl);
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.v("url", String.valueOf(url));
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);


            // Starts the query
            conn.connect();

            int response = conn.getResponseCode();

            Log.v("response", String.valueOf(response));

            is = conn.getInputStream();

            // Convert the InputStream into a JSON string
            bufferedReader = new BufferedReader(new InputStreamReader(is));
            stringBuilder = new StringBuilder();


            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + '\n');
            }
            String jsonString = stringBuilder.toString();
            return jsonString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }


    private void dummyPopulate(ArrayList<TutorObject> tutorList){

        LocationObject loc1 = new LocationObject(43.0719139, -89.4081352);
        TutorSession session1 = new TutorSession(15, 60, true);
        TutorObject tutor1 = new TutorObject("Jess", "Kannon", 5, loc1, session1);

        LocationObject loc2 = new LocationObject(43.0767057, -89.4010609);
        TutorSession session2 = new TutorSession(15, 60, true);
        TutorObject tutor2 = new TutorObject("Eric", "Trac", 3, loc2, session2);

        LocationObject loc3 = new LocationObject(43.0726811,-89.40169209999999);
        TutorSession session3 = new TutorSession(16, 60, true);
        TutorObject tutor3 = new TutorObject("Robert", "Williams", 4, loc3, session3);

        LocationObject loc4 = new LocationObject(43.0726811,-89.40169209999999);
        TutorSession session4 = new TutorSession(18, 60, true);
        TutorObject tutor4 = new TutorObject("Nadia", "Martinez", 5, loc4, session4);

        tutorList.add(tutor1);
        tutorList.add(tutor2);
        tutorList.add(tutor3);
        tutorList.add(tutor4);

    }


}

//////////////////// EXPANDABLE LIST CODE //////////////////////////////////////////////////////////////////////
//  private ArrayList<String> parentItems = new ArrayList<String>();
//  private ArrayList<Object> childItems = new ArrayList<Object>();
///
//       //  ON CREATE /////////////////////
//
//        ExpandableListView expandableList = getExpandableListView();
//
//        expandableList.setDividerHeight(2);
//        expandableList.setGroupIndicator(null);
//        expandableList.setClickable(true);
//
//        setGroupParents();
//        setChildData();
//
//        ExpandableAdapter adapter = new ExpandableAdapter(parentItems, childItems);
//
//        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
//        expandableList.setAdapter(adapter);
//        expandableList.setOnChildClickListener(this);
//
//        //////////////////////////////////

//    public void setGroupParents() {
//        parentItems.add("Computer Sciences");
//        parentItems.add("Mathematics");
//        parentItems.add("Physics");
//        parentItems.add("etc.");
//    }
//
//    public void setChildData() {
//
//        // Computer Sciences
//        ArrayList<String> child = new ArrayList<String>();
//        child.add("CS302- Intro to Java");
//        //child.add("");
//        child.add("CS540- Intro to AI");
//        child.add("CS577- Into to Algorithms");
//        childItems.add(child);
//
//        // Mathematics
//        child = new ArrayList<String>();
//        child.add("MATH221-Calc I");
//        childItems.add(child);
//
//        // Physics
//        child = new ArrayList<String>();
//        child.add("PHYS201- Physics I");
//        childItems.add(child);
//
//        // etc.
//        child = new ArrayList<String>();
//        child.add("test");
//        childItems.add(child);
//    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////