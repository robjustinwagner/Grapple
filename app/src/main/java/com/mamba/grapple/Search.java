package com.mamba.grapple;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;


// *json imports*
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;


public class Search extends ListActivity implements ConnectionCallbacks, OnConnectionFailedListener {

    String TUTOR_PATH = "http://protected-dawn-4244.herokuapp.com/tutors";
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


    ListView listView;
    SeekBar seekBar;
    TextView distanceView;
    Button search;
    View selected = null;
    int distance = 0;
    String course;
    String currLat;
    String currLong;

    // temporary until DB load setup (use SimpleCursorAdapter for DB)
    static final String[] COURSES = {"CS302", "Calc 234"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        // grab all the view items and set defaults
        initialize();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // If a previous item was selected unhighlight it
                if(selected != null){
                    selected.setBackgroundColor(Color.TRANSPARENT);
                    parent.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
                }

                selected = view;
                selected.setBackgroundColor(Color.rgb(62, 175, 212));

            }
        });


        // update distance as user slides
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distanceView.setText("Distance: " + distance + " mi");
            }
        });



        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // connect to the instance
        mGoogleApiClient.connect();

    }




    // A private method to help us initialize our default variables and settings
    private void initialize() {
        seekBar = (SeekBar) findViewById(R.id.seekBar2);
        listView = getListView();
        distanceView = (TextView) findViewById(R.id.textView5);
        search = (Button) findViewById(R.id.button);

        //add elements from array to list view
        listView.setAdapter(new ArrayAdapter<String>(this,
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
        distanceView.setText("Distance: " + distance + " mi");
    }


    // on search button click get the relevant  tutor list and show results
    public void tutorSearch(View view){
        course = String.valueOf(((TextView) selected).getText());
        Log.v("distance", "" + distance);
        Log.v("course", course);


        // make sure we have the GPS location
        if(mLastLocation != null){

            currLat = String.valueOf(mLastLocation.getLatitude());
            currLong = String.valueOf(mLastLocation.getLongitude());

            // log the current coordinates
            Log.v("currentLocation", "(" + currLat + "," + currLong + ")");


            //  send the data in a http request
            ConnectivityManager conMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();
            // if there is a network connection create a request thread
            if(networkInfo != null && networkInfo.isConnected()){
               new TutorRetrieval().execute(TUTOR_PATH);
            }else{
                Log.v("no connection", "Failed to connect to internet");
            }



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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
        protected void onPostExecute(String result) {
            Log.v("postResult", result);
            Gson gson = new Gson();
            try{
                JSONArray tutors = new JSONArray(result);
                ArrayList<TutorObject> tutorList = new ArrayList<TutorObject>();
                for(int i = 0; i < tutors.length(); i++){
                    TutorObject tutor = gson.fromJson(tutors.get(i).toString(), TutorObject.class);
                    Log.v("tutorObject", tutor.toString());
                    tutorList.add(tutor);
                }

                Intent intent = new Intent(Search.this, Results.class);
                intent.putParcelableArrayListExtra("tutorList", tutorList);
                startActivity(intent);

            }catch(JSONException e){

            }
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

        myurl += "?" + query;           // appended encoded query to URL
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
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
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