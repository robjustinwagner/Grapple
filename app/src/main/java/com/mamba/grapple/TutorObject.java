package com.mamba.grapple;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.text.DecimalFormat;

/**
 * Created by vash on 4/1/15.
 */
// client side tutor model, used for gson -> json translation, implements parcelable for data passage between activities
public class TutorObject implements Parcelable {

    // the attributes of each tutor
    public String id;
    public String firstName;
    public String lastName;
    public int rating;
    public String profilePic;
    public float distance;
    public LocationObject location;
    public TutorSession session;


    // rounds to two decimal places
    DecimalFormat twoDeci = new DecimalFormat("##.00");

    // constructor
    public TutorObject(String firstName, String lastName, int rating, LocationObject location, TutorSession session){
        this.firstName = firstName;
        this.lastName = lastName;
        this.rating = rating;
        this.location = location;
        this.session = session;
        this.profilePic = firstName + "_" + lastName;
        Log.v("Session Price: ", String.valueOf(this.session.price));
    }

    public void setId(String ID){
        this.id = ID;
    }





    public String getDistance(Location userLocation){

        double lat1;
        double lon1;

        if(userLocation != null){
            lat1 = userLocation.getLatitude();
            lon1 = userLocation.getLongitude();
        }else{
            // create dummy lat/lon for emulator
            lat1 = 43.076592;
            lon1 = -89.412487;
        }

        double lat2 = this.location.xPos;
        double lon2 = this.location.yPos;

        Log.v("Calculating distance", "(" + lat1 + "," + lon1 + ") & " + "(" + lat2 + "," + lon2 + ")" );

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return twoDeci.format(dist);
    }

    public String getDistance(LatLng location){

        double lat1 = location.latitude;
        double lon1 = location.longitude;
        double lat2 = this.location.xPos;
        double lon2 = this.location.yPos;

        Log.v("Calculating distance", "(" + lat1 + "," + lon1 + ") & " + "(" + lat2 + "," + lon2 + ")" );

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return twoDeci.format(dist);
    }


    // conversions
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }


    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    public String toString(){
        return "[id=" + id + " firstName=" + firstName + " lastName=" + lastName +
                " rating=" + rating + " distance" + distance +
                " location=" + location.xPos + "," + location.yPos + "]" +
                " session= {price: " + session.price + ", minLength: " + session.maxLength + " }]";
    }


    protected TutorObject(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        rating = in.readInt();
        profilePic = in.readString();
        distance = in.readFloat();
        location = (LocationObject) in.readValue(TutorObject.class.getClassLoader());
        session = (TutorSession) in.readValue(TutorSession.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeInt(rating);
        dest.writeString(profilePic);
        dest.writeFloat(distance);
        dest.writeValue(location);
        dest.writeValue(session);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TutorObject> CREATOR = new Parcelable.Creator<TutorObject>() {
        @Override
        public TutorObject createFromParcel(Parcel in) {
            return new TutorObject(in);
        }

        @Override
        public TutorObject[] newArray(int size) {
            return new TutorObject[size];
        }
    };




}