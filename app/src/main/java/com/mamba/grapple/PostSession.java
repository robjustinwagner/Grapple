package com.mamba.grapple;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.maps.MapFragment;

import org.json.JSONObject;

/**
 * Created by vash on 4/18/15.
 */
public class PostSession extends Activity {

    TextView rateTutor;
    RatingBar rating;
    Button doneButton;
    ImageView tutorPic;

    LoginManager session;
    private Location mLastLocation;

    // service related variables
    private boolean mBound = false;
    DBService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postsession);

        rateTutor = (TextView) findViewById(R.id.rateTutor);
        rating = (RatingBar) findViewById(R.id.ratingBar2);
        doneButton = (Button) findViewById(R.id.doneBtn);
        tutorPic = (ImageView) findViewById(R.id.profilePic);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int numStars = rating.getNumStars();
                mService.startBroadcast(session.getCurrentUser().getId(), numStars);
                // return to search and finish
                Intent intent = new Intent(PostSession.this, Main.class);
                startActivity(intent);
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("tutor")) {
            TutorObject tutor = extras.getParcelable("tutor");
            // TEMP DUMMY TUTORS

            rateTutor.setText("Rate " + tutor.firstName + ":");

            switch (tutor.firstName){
                case "Jess": tutorPic.setImageResource(R.drawable.jess);
                    break;
                case "Eric": tutorPic.setImageResource(R.drawable.eric);
                    break;
                case "Robert": tutorPic.setImageResource(R.drawable.robert);
                    break;
                case "Nadia": tutorPic.setImageResource(R.drawable.nadia);
                    break;
            }
        }
        session = new LoginManager(getApplicationContext());
    }

    public void onResume() {
        super.onResume();
        if (session.isLoggedIn()) {
            createService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void createService() {
        Log.v("Waiting Page", "Creating Service..");
        startService(new Intent(this, DBService.class));
        bindService(new Intent(this, DBService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.v("Service Bound", "Results bound to new service");
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBService.LocalBinder binder = (DBService.LocalBinder) service;
            mService = binder.getService();
            mService.setSession(session);
            mBound = true;
            mLastLocation = mService.getLocation();
        }
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

}
