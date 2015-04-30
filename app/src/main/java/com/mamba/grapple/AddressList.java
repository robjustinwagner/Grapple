package com.mamba.grapple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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


}
