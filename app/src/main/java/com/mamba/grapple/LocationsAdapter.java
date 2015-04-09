package com.mamba.grapple;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vash on 4/8/15.
 */
public class LocationsAdapter extends BaseAdapter {
    private Context context;
    private List<LocationObject> locations;



    public int getCount(){
        return locations.size();
    }
    @Override
    public Object getItem(int position) {
        return locations.get(position);
    }

    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent){

        LocationObject loc = locations.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.address_list_row, null);
        }


        // TODO: have a different message type for notification messages

        TextView locName = (TextView) convertView.findViewById(R.id.locationName);
        TextView locAddress = (TextView) convertView.findViewById(R.id.locationAddress);

        locName.setText(loc.getName());
        locAddress.setText(loc.getAddress());

        return convertView;
    }

    public LocationsAdapter(Context context, List<LocationObject> navDrawerItems){
        this.context = context;
        this.locations= navDrawerItems;
    }




}
