package com.mamba.grapple;

/**
 * Created by vash on 4/8/15.
 */
public class MessageObject {

    private String fromName, message;
    private boolean isSelf;
    private boolean isLocation = false;
    private LocationObject location;


    public MessageObject() {
    }

    public MessageObject(String fromName, String message, boolean isSelf, LocationObject loc) {
        this.fromName = fromName;
        this.message = message;
        this.isSelf = isSelf;

        if(loc != null){
            this.isLocation = true;
            location = loc;
        }
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
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

}
