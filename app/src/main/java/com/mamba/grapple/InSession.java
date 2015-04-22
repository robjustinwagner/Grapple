package com.mamba.grapple;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insession);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        btnStop = (Button) findViewById(R.id.endBtn);
        btnPause = (Button) findViewById(R.id.pauseBtn);

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            tutor = extras.getParcelable("tutor");

           // convert to long in ms
           long sessionLength = MS_IN_MIN * (long) tutor.session.maxLength;
           if(sessionLength > sessionRemaining){
               sessionRemaining = sessionLength;
           }
        }


        startCountdown();

        btnPause.setOnClickListener(new View.OnClickListener(){
               public void onClick(View v){
                   // if the session is already paused resume it
                   if(sessionPaused){
                       startCountdown();
                       btnPause.setText("Pause Session");
                       sessionPaused = false;

                   }else{
                       timer.cancel();
                       btnPause.setText("Resume Session");
                       sessionPaused = true;
                   }
               }

        });


        btnStop.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                timer.cancel();
                timer.onFinish();

                // go to receipt
                Intent intent = new Intent(InSession.this, PostSession.class);
                intent.putExtra("tutor", tutor);
                startActivity(intent);
                finish();

            }
        });


    }


    private void startCountdown(){
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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



}



