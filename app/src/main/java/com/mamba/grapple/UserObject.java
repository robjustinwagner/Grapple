package com.mamba.grapple;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by vash on 5/8/15.
 */
public class UserObject implements Parcelable {

    // the attributes of each tutor
    public String id;
    public String firstName;
    public String lastName;
    public String email;
    public int rating;
    public String profilePic;
    public float distance;
    public LocationObject location;
    public TutorSession session;


    // rounds to two decimal places
    DecimalFormat twoDeci = new DecimalFormat("##.00");

    // constructor
    public UserObject(String firstName, String lastName, String id, String email, String profilePic){
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.email = email;
        this.profilePic = profilePic;
        Log.v("Created User: ", firstName + " " + lastName);
    }



    public String getDistance(Location userLocation){

        double lat1 = userLocation.getLatitude();
        double lon1 = userLocation.getLongitude();
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


    protected UserObject(Parcel in){
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        rating = in.readInt();
        profilePic = in.readString();
        distance = in.readFloat();
        location = (LocationObject) in.readValue(LocationObject.class.getClassLoader());
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
        dest.writeString(email);
        dest.writeInt(rating);
        dest.writeString(profilePic);
        dest.writeFloat(distance);
        dest.writeValue(location);
        dest.writeValue(session);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserObject> CREATOR = new Parcelable.Creator<UserObject>() {
        @Override
        public UserObject createFromParcel(Parcel in) {
            return new UserObject(in);
        }

        @Override
        public UserObject[] newArray(int size) {
            return new UserObject[size];
        }
    };





}
