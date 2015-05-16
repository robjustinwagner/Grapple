package com.mamba.grapple;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.MapFragment;

import java.util.concurrent.TimeUnit;


public class InSession extends Activity {

    final int MS_IN_MIN = 60000; // ms in a minute

    TextView textViewTime;
    Button btnPause;
    Button btnStop;

    TutorObject tutor;

    private SessionCounter timer;
    private long sessionRemaining = MS_IN_MIN * 30;    // set from tutor's set max (default 30 min)
    private boolean sessionPaused = false;

    LoginManager session;

    private Location mLastLocation;

    // service related variables
    private boolean mBound = false;
    DBService mService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insession);
        getActionBar().show();
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        btnStop = (Button) findViewById(R.id.endBtn);
        btnPause = (Button) findViewById(R.id.pauseBtn);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("tutor")) {
            tutor = extras.getParcelable("tutor");

            // convert to long in ms
            long sessionLength = MS_IN_MIN * (long) tutor.sessionLength();
            if (sessionLength > sessionRemaining) {
                sessionRemaining = sessionLength;
            }
        }

        startCountdown();

        btnPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // if the session is already paused resume it
                if (sessionPaused) {
                    startCountdown();
                    btnPause.setText("Pause Session");
                    sessionPaused = false;

                } else {
                    timer.cancel();
                    btnPause.setText("Resume Session");
                    sessionPaused = true;
                }
            }

        });


        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                timer.cancel();
                timer.onFinish();

                // go to receipt
                Intent intent = new Intent(InSession.this, PostSession.class);
                intent.putExtra("tutor", tutor);
                startActivity(intent);
                finish();

            }
        });

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

    private void startCountdown() {
        timer = new SessionCounter(sessionRemaining, 1000);
        timer.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_insession, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                //TODO
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class SessionCounter extends CountDownTimer {

        long millis;

        public SessionCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            textViewTime.setText("Completed.");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            sessionRemaining = millisUntilFinished;
            millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println(hms);
            textViewTime.setText(hms);
        }


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



