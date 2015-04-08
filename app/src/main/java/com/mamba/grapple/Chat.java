package com.mamba.grapple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by vash on 4/8/15.
 */
public class Chat extends Activity {



    private TutorObject tutor;

    EditText locSuggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorchat);

        retrieveTutorInfo();
        locSuggest = (EditText)findViewById(R.id.locationInput);


        locSuggest.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                // clear the default input text on first focus
                if(!hasFocus){
                    locSuggest.setText("");
                }
            }
        });

    }

    public void retrieveTutorInfo(){
        // get the tutor data
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            tutor = extras.getParcelable("selectedTutor");

            // Look up view for data population
            TextView tutorName = (TextView)findViewById(R.id.tutorName);
            TextView tutorDistance = (TextView)findViewById(R.id.tutorDistance);
            TextView tutorPrice = (TextView)findViewById(R.id.tutorPrice);


            // populate the data
            tutorName.setText(tutor.firstName + " " + tutor.lastName);
            tutorDistance.setText(String.valueOf(tutor.distance) + " mi");
            tutorPrice.setText("$" + String.valueOf(tutor.session.price));


        }
    }




}
