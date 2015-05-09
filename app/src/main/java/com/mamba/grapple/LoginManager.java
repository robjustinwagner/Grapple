package com.mamba.grapple;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by vash on 5/8/15.
 */
public class LoginManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

   // Sharedpref file name
    private static final String PREF_NAME = "SessionData";

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String AUTH_TOKEN = "token";



    // Constructor
    public LoginManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // Create a login session by storing auth token
    public void login(String token){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(AUTH_TOKEN, token);
        editor.commit();
    }


    // clear the token and current user data
    public void logout(){
        Log.v("Removing Session Token ", pref.getString(AUTH_TOKEN, null));
        editor.clear();
        editor.commit();
    }

    // check if current user is logged in
    public boolean isLoggedIn(){
        Log.v("Checking Logged in", ""+pref.getBoolean(IS_LOGIN, false));
        return pref.getBoolean(IS_LOGIN, false);
    }

    // return auth token
    public String getToken(){
        return pref.getString(AUTH_TOKEN, null);
    }


}
