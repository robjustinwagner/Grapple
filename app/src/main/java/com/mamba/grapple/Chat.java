package com.mamba.grapple;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;




import java.util.ArrayList;
import java.util.List;

/**
 * Created by vash on 4/8/15.
 */
public class Chat extends Activity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private TutorObject tutor;
    private LocationObject presetLoc;
    private BroadcastReceiver mBroadcastReceiver ;

    private MessagesAdapter adapter;
    private List<MessageObject> messageList;
    private GoogleApiClient mGoogleApiClient;

    private LocationsAdapter locationsAdapter;
    private List<LocationObject> locationList;
    private LocationObject selectedLocation;


    ListView messagesContainer;
    EditText locInput;
    EditText chatInput;
    Button sendButton;
    Button suggestButton;
    View selected;
//    ImageButton locationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorchat);

        retrieveTutorInfo();
        dummyPopulate();

        messagesContainer = (ListView) findViewById(R.id.list_view_messages);
        sendButton = (Button) findViewById(R.id.btnSend);
        suggestButton = (Button) findViewById(R.id.suggestMeetingBtn);
//        locInput = (EditText)findViewById(R.id.locationInput);
        chatInput = (EditText)  findViewById(R.id.msgInput);
//        locationList = (ImageButton) findViewById(R.id.viewRecommended);

//        locInput.setHint("Suggest where to meet");

        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                // TODO: send message to server

                // display locally
                MessageObject msg = new MessageObject("Student", chatInput.getText().toString(), true, null);
                messageList.add(msg);
                adapter.notifyDataSetChanged();

                // clear input field
                chatInput.setText("");

            }
        });

        suggestButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
//                // TODO: actually wait for other person to accept suggestion before continuing
//                if(presetLoc != null){
//                    Intent intent = new Intent(Chat.this, Tutor.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                    intent.putExtra("meetingPoint", presetLoc);
//                    startActivity(intent);

//                }

                  showDialog(v.getContext());


            }
        });

//        locationList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // create a result activity involving the list
//                // transfer the user to the register page
//                Intent intent = new Intent(Chat.this, AddressList.class);
//                // we expect an address
//                startActivityForResult(intent, 1);
//            }
//        });



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


        messagesContainer.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                MessageObject message = messageList.get(position);

                // handle if location
                if(message.isLocation()){

                    Intent intent = new Intent(Chat.this, Tutor.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra("meetingPoint", message.getLocation());
                    startActivity(intent);
                }

            }
        });


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            Log.v("Chat Activity", "Location Result Received");
            presetLoc = data.getParcelableExtra("location");
            locInput.setText(presetLoc.getAddress());
        }
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



    public void showDialog(Context context){
        Log.v("Dialog", "Show dialog hit");
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setCancelable(true);

        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.activity_addresslist, null);

        ListView list = (ListView) view.findViewById(R.id.addressList);
        Button suggestBtn = (Button) view.findViewById(R.id.suggestBtn);
        locationsAdapter = new LocationsAdapter(this, locationList);
        TextView title = new TextView(this);

        // title info
        title.setText("Input or Choose a Meeting Location");
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);

        dialog.setCustomTitle(title);

        list.setAdapter(locationsAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // If a previous item was selected unhighlight it
                if (selected != null) {
                    selected.setBackgroundColor(Color.TRANSPARENT);
                    parent.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
                }

                // highlight the selected item
                selected = view;
                selected.setBackgroundColor(Color.rgb(62, 175, 212));

                // get the selected location
                selectedLocation = locationList.get(position);
                Log.v("Selected Location", String.valueOf(selectedLocation.xPos) + "," + String.valueOf(selectedLocation.yPos));

            }
        });






        dialog.setView(view);
        final AlertDialog alert = dialog.show();

        suggestBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alert.cancel();
                if(selectedLocation != null){
                    MessageObject msg = new MessageObject("Student", selectedLocation.getAddress(), true, selectedLocation);
                    messageList.add(msg);
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }


    public void dummyPopulate(){

        locationList =  new ArrayList<LocationObject>();
        final Context context = getApplicationContext();



        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                // create dummy location objects for now
                final LocationObject loc1 = new LocationObject("College Library", "600 N Park St, Madison, WI", context);
                final LocationObject loc2 = new LocationObject("Union South", "1308 W Dayton St, Madison, WI", context);
                final LocationObject loc3 = new LocationObject("Chemistry Building", "1101 University Ave, Madison, WI", context);
                final LocationObject loc4 = new LocationObject("Grainger Hall", "975 University Ave, Madison, WI", context);

                locationList.add(loc1);
                locationList.add(loc2);
                locationList.add(loc3);
                locationList.add(loc4);


            }
        });


        thread.start();

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
