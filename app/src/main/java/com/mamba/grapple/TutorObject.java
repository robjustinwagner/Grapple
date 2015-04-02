package com.mamba.grapple;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
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
    public TutorLocation location;
    public TutorSession session;


    public String toString(){
        return "[id=" + id + " firstName=" + firstName + " lastName=" + lastName +
                " rating=" + rating + " distance" + distance +
                " location=" + location.xPos + "," + location.yPos + "]" +
                " session= {price: " + session.price + ", minLength: " + session.minLength +
                ", period: " + session.period +" }]" ;
    }


    protected TutorObject(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        rating = in.readInt();
        profilePic = in.readString();
        distance = in.readFloat();
        location = (TutorLocation) in.readValue(TutorLocation.class.getClassLoader());
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