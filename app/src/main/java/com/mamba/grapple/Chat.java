package com.mamba.grapple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by vash on 4/8/15.
 */
public class Chat extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TutorObject tutor;
    private LocationObject presetLoc;

    private MessagesAdapter adapter;
    private List<MessageObject> messageList;
    private GoogleApiClient mGoogleApiClient;


    ListView messagesContainer;
    EditText locInput;
    EditText chatInput;
    Button sendButton;
    Button suggestButton;
    ImageButton locationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorchat);

        retrieveTutorInfo();

        messagesContainer = (ListView) findViewById(R.id.list_view_messages);
        sendButton = (Button) findViewById(R.id.btnSend);
        suggestButton = (Button) findViewById(R.id.suggestLocation);
        locInput = (EditText) findViewById(R.id.locationInput);
        chatInput = (EditText) findViewById(R.id.msgInput);
        locationList = (ImageButton) findViewById(R.id.viewRecommended);

        locInput.setHint("Suggest where to meet");

        locInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {

                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: send message to server

                // display locally
                MessageObject msg = new MessageObject("Student", chatInput.getText().toString(), true);
                messageList.add(msg);
                adapter.notifyDataSetChanged();

                // clear input field
                chatInput.setText("");

            }
        });

        suggestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: actually wait for other person to accept suggestion before continuing
                if (presetLoc != null) {
                    Intent intent = new Intent(Chat.this, Tutor.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("meetingPoint", presetLoc);
                    startActivity(intent);
                }

            }
        });

        locationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a result activity involving the list
                // transfer the user to the register page
                Intent intent = new Intent(Chat.this, AddressList.class);
                // we expect an address
                startActivityForResult(intent, 1);
            }
        });


        messageList = new ArrayList<MessageObject>();
        adapter = new MessagesAdapter(this, messageList);
        messagesContainer.setAdapter(adapter);


        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    // places the selected chosen location from the address list into the location suggest input
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Log.v("Chat Activity", "Location Result Received");
            presetLoc = data.getParcelableExtra("location");
            locInput.setText(presetLoc.getAddress());
        }
    }


    public void retrieveTutorInfo() {
        // get the tutor data
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tutor = extras.getParcelable("selectedTutor");

            // Look up view for data population
            TextView tutorName = (TextView) findViewById(R.id.tutorName);
            TextView tutorDistance = (TextView) findViewById(R.id.tutorDistance);
            TextView tutorPrice = (TextView) findViewById(R.id.tutorPrice);


            // populate the data
            tutorName.setText(tutor.firstName + " " + tutor.lastName);
            tutorDistance.setText(String.valueOf(tutor.distance) + " mi");
            tutorPrice.setText("$" + String.valueOf(tutor.session.price));


        }
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

    }

}
