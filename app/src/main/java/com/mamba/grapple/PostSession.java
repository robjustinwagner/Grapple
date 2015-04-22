package com.mamba.grapple;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by vash on 4/18/15.
 */
public class PostSession extends Activity {

    TextView rateTutor;
    Button doneButton;
    ImageView tutorPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postsession);

        rateTutor = (TextView) findViewById(R.id.rateTutor);
        doneButton = (Button) findViewById(R.id.doneBtn);
        tutorPic = (ImageView) findViewById(R.id.profilePic);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: get tutor rating

                // return to search and finish
                Intent intent = new Intent(PostSession.this, Search.class);
                startActivity(intent);
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();

        if(extras != null){
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

    }


}
