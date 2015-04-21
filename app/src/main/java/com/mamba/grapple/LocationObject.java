package com.mamba.grapple;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.List;
import java.util.Locale;

/**
 * Created by vash on 4/8/15.
 */
public class LocationObject implements Parcelable {
    public double xPos;
    public double yPos;
    private String address;
    private String name;

    public LocationObject(String name, String address, Context context) {
        this.name = name;
        this.address = address;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> fromLocationName = null;

        // get the latitude and longitude from an address TODO: Put in separate thread
        try {
            fromLocationName = geocoder.getFromLocationName(this.address, 1);
            if (fromLocationName != null && fromLocationName.size() > 0) {
                Address a = fromLocationName.get(0);
                xPos = a.getLatitude();
                yPos = a.getLongitude();
                Log.v(this.name + " coordinates:", xPos + "," + yPos);
            }
        } catch (java.io.IOException e) {

        }


    }


    protected LocationObject(Parcel in) {
        xPos = in.readDouble();
        yPos = in.readDouble();
        name = in.readString();
        address = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(xPos);
        dest.writeDouble(yPos);
        dest.writeString(name);
        dest.writeString(address);
    }


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LocationObject> CREATOR = new Parcelable.Creator<LocationObject>() {
        @Override
        public LocationObject createFromParcel(Parcel in) {
            return new LocationObject(in);
        }

        @Override
        public LocationObject[] newArray(int size) {
            return new LocationObject[size];
        }
    };


}
