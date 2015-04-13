package com.mamba.grapple;

import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class InSession extends ActionBarActivity {

    TextView textViewTime;
    Button btnPause;
    Button btnStop;
    Button btnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insession);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        btnStop = (Button) findViewById(R.id.endBtn);
        btnPause = (Button) findViewById(R.id.pauseBtn);
        btnResume = (Button) findViewById(R.id.resumeBtn);

        SessionCounter timer = new SessionCounter(180000,1000);

        timer.start();




        btnResume.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                timer.start();
                btnPause.setVisibility(View.VISIBLE);
                btnResume.setVisibility(View.GONE);
            }
        });


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
            millis = millisUntilFinished;
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            System.out.println(hms);
            textViewTime.setText(hms);
        }

        public void resume(){

        }






    }



}



