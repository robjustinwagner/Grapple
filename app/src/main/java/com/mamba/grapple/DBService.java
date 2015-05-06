package com.mamba.grapple;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ListView;

// *socket.io imports*
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.IO.*;
import com.github.nkzawa.socketio.client.SocketIOException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonElement;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.*; // for URIexception
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;


public class DBService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Socket socket;
    private String token;
    private final IBinder myBinder = new LocalBinder();
    private final Gson gson = new Gson();
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }



    @Override
    public void onCreate() {
        System.out.println("DBService Created");
        super.onCreate();

        if (!isGooglePlayServicesAvailable()) {
            Log.e("Google Play Services", "Could not connect");
        }

        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        mGoogleApiClient.connect();
        return START_STICKY;
    }

    public void connectSocket(){
        // set up socket connection
        if (socket == null || !socket.connected()){
            try {
                String url = "http://protected-dawn-4244.herokuapp.com" + "?token=" + getToken();
                Log.v("socket url", url);
                socket = IO.socket(url);
            } catch (URISyntaxException e){
                Log.e("Bad URI", e.getMessage());
            }
        }

        // create listeners
        socket.on("message", message);
        socket.on("locationUpdate", locationUpdate);
        socket.on("meetingSuggestion", meetingSuggestion);
        socket.on("startSessionRequest", startSessionRequest);
        socket.on("grapple", grapple);


        socket.connect();
    }

    public void startBroadcast(int time, int distance, double price, String[] courses){

        JSONObject broadcastInfo = new JSONObject();
        JSONArray tutorCourses = new JSONArray();

        try{

            for(String course : courses){
                tutorCourses.put(course);
            }

            broadcastInfo.put("time", time);
            broadcastInfo.put("distance", distance);
            broadcastInfo.put("price", price);
            broadcastInfo.put("courses", tutorCourses);
            Log.v("emitting broadcast", "user available to tutor");
            socket.emit("setAvailable",  broadcastInfo);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }


    public void setToken(String token){
        Log.v("Service received token", token);
        this.token = token;

    }

    public String getToken(){
        return token;
    }

    public void destroyToken() {
        this.token = null;
    }


    public IBinder onBind(Intent intent) {
       return myBinder;
    }



    public class LocalBinder extends Binder {
        public DBService getService() {
            System.out.println("I am in Localbinder ");
            return DBService.this;

        }
    }


    // listener responses
    private Emitter.Listener message = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            // parse data and broadcast

        }
    };

    private Emitter.Listener locationUpdate = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            // parse data and broadcast
//            LocationObject location = gson.fromJson();


            Intent intent = new Intent("locationUpdate");
            // You can also include some extra data.
            intent.putExtra("message", "This is my message!");
        }
    };

    private Emitter.Listener meetingSuggestion = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            // parse data and broadcast
        }
    };


    private Emitter.Listener startSessionRequest = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            // parse data and broadcast
        }
    };


    private Emitter.Listener grapple = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            // parse data and broadcast
        }
    };




    private void broadcast(Intent intent){
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /// Google Play Stuff //////////////////
    @Override
    public void onConnected(Bundle bundle) {
        Log.v("Location Api Connected" , String.valueOf(mGoogleApiClient.isConnected()));
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location Changed", "Firing onLocationChanged...");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.d("New Location: ", "("  + mCurrentLocation.getLatitude() + " , " + mCurrentLocation.getLongitude() + " )");
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v("Connection Failed", ""+connectionResult);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            return false;
        }
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

}