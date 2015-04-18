package com.mamba.grapple;

import android.app.Application;
import android.location.Location;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by vash on 4/17/15.
 */
public class App extends Application{

    public void onCreate(){
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }


}
