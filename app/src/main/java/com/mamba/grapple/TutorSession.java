package com.mamba.grapple;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vash on 4/2/15.
 */

public class TutorSession implements Parcelable {
    public int price;
    public int maxLength;
    public boolean available;

    public TutorSession(int price, int maxLength, boolean available){
        this.price = price;
        this.maxLength = maxLength;
        this.available = available;
    }


    protected TutorSession(Parcel in) {
        price = in.readInt();
        maxLength = in.readInt();
        available = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(price);
        dest.writeInt(maxLength);
        dest.writeByte((byte) (available ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TutorSession> CREATOR = new Parcelable.Creator<TutorSession>() {
        @Override
        public TutorSession createFromParcel(Parcel in) {
            return new TutorSession(in);
        }

        @Override
        public TutorSession[] newArray(int size) {
            return new TutorSession[size];
        }
    };


}