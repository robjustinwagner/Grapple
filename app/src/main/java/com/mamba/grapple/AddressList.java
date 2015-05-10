package com.mamba.grapple;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.MapFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vash on 4/8/15.
 */
public class AddressList extends Activity {

    private LocationsAdapter adapter;
    private List<LocationObject> locationList;
    private LocationObject selectedLocation;

    ListView locationsContainer;

    LoginManager session;

    private Location mLastLocation;

    // service related variables
    private boolean mBound = false;
    DBService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresslist);

        dummyPopulate();

        locationsContainer = (ListView) findViewById(R.id.addressList);

        adapter = new LocationsAdapter(this, locationList);
        locationsContainer.setAdapter(adapter);


        locationsContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // send the location back to the parent activity
                selectedLocation = locationList.get(position);

                // make sure the parent activity acknowledges the authorized session
                Intent chatReturn = new Intent();
                chatReturn.putExtra("location", selectedLocation);
                setResult(Activity.RESULT_OK, chatReturn);

                finish();

            }
        });
        session = new LoginManager(getApplicationContext());
    }

    public void onResume() {
        super.onResume();
        if (session.isLoggedIn()) {
            createService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void createService() {
        Log.v("Waiting Page", "Creating Service..");
        startService(new Intent(this, DBService.class));
        bindService(new Intent(this, DBService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.v("Service Bound", "Results bound to new service");
    }

    public void dummyPopulate() {

        locationList = new ArrayList<LocationObject>();
        Context context = getApplicationContext();
        // create dummy location objects for now
        LocationObject loc1 = new LocationObject("College Library", "600 N Park St, Madison, WI", context);
        LocationObject loc2 = new LocationObject("Union South", "1308 W Dayton St, Madison, WI", context);
        LocationObject loc3 = new LocationObject("Chemistry Building", "1101 University Ave, Madison, WI", context);
        LocationObject loc4 = new LocationObject("Grainger Hall", "975 University Ave, Madison, WI", context);

        locationList.add(loc1);
        locationList.add(loc2);
        locationList.add(loc3);
        locationList.add(loc4);
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

}
