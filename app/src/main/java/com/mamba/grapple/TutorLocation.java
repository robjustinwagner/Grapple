package com.mamba.grapple;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vash on 4/2/15.
 */


public class TutorLocation implements Parcelable {
    public float xPos;
    public float yPos;

    protected TutorLocation(Parcel in) {
        xPos = in.readFloat();
        yPos = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(xPos);
        dest.writeFloat(yPos);
    }


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TutorLocation> CREATOR = new Parcelable.Creator<TutorLocation>() {
        @Override
        public TutorLocation createFromParcel(Parcel in) {
            return new TutorLocation(in);
        }

        @Override
        public TutorLocation[] newArray(int size) {
            return new TutorLocation[size];
        }
    };

}