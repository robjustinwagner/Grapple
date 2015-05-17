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
    public double lat;
    public double lon;
    private String address;
    private String name;



    public LocationObject(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }


    public LocationObject(double lat, double lon, String name, String address){
        this.name =  name;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
    }


    // if only address and name available we determine coordinates in construction
    public LocationObject(String name, String address, Context context) {
        this.name =  name;
        this.address = address;


    }



    protected LocationObject(Parcel in) {
        lat = in.readDouble();
        lon = in.readDouble();
        name = in.readString();
        address = in.readString();
    }

    public String getName(){
        return name;
    }

    public String getAddress(){
        return address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(name);
        dest.writeString(address);
    }


    public void geoCode(Context context){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> fromLocationName = null;

        try{
            fromLocationName = geocoder.getFromLocationName(this.address,   1);
            if (fromLocationName != null && fromLocationName.size() > 0) {
                Address a = fromLocationName.get(0);
                this.lat =  a.getLatitude();
                this.lon =  a.getLongitude();
                Log.v(this.address+ " coordinates:" , lat + "," + lon);
            }
        }catch(java.io.IOException e){

        }

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
