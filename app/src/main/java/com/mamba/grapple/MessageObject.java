package com.mamba.grapple;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by vash on 4/8/15.
 */
public class MessageObject implements Parcelable {

    private String senderName, message;
    private String senderID, recipID;

    private boolean isSelf;
    private boolean isLocation = false;
    private LocationObject location;


    public MessageObject(String senderName, String message, String senderID, String recipID,  boolean isSelf, LocationObject loc) {
        this.senderName = senderName;
        this.message = message;
        this.isSelf = isSelf;
        this.senderID = senderID;
        this.recipID = recipID;

        //if the message is a location message
        if(loc != null){
            this.isLocation = true;
            location = loc;
        }
    }

    public String getFromName() {
        return senderName;
    }

    public void setFromName(String fromName) {
        this.senderName = fromName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public boolean isLocation(){ return isLocation; }

    public LocationObject getLocation(){
        return location;
    }

    public void setSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }

    protected MessageObject(Parcel in){
        senderName = in.readString();
        senderID= in.readString();
        recipID = in.readString();
        isSelf = in.readByte() != 0;
        recipID = in.readString();
        message = in.readString();
        location = (LocationObject) in.readValue(LocationObject.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(senderName);
        dest.writeString(senderID);
        dest.writeString(recipID);
        dest.writeString(message);
        dest.writeByte((byte) (isSelf ? 1 : 0));
        dest.writeValue(location);
    }

   }