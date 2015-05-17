package com.mamba.grapple;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
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
public class Chat extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {



    private LocationObject presetLoc;
    private BroadcastReceiver mBroadcastReceiver;

    private MessagesAdapter adapter;
    private List<MessageObject> messageList;
    private GoogleApiClient mGoogleApiClient;

    private LocationsAdapter locationsAdapter;
    private List<LocationObject> locationList;
    private LocationObject selectedLocation;


    private UserObject recipient;
    private UserObject currentUser;


    ListView messagesContainer;

    EditText chatInput;
    ImageButton sendButton;
    ImageButton suggestButton;
    View selected;
//    ImageButton locationList;


    private boolean seen = false;

    LoginManager session;


    // service related variables
    private boolean mBound = false;
    DBService mService;



    // service connection event handler
    private ServiceConnection mConnection = new ServiceConnection(){
        public void onServiceConnected(ComponentName className, IBinder service) {
            DBService.LocalBinder binder = (DBService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    // receiver to handle server responses for this activity
    private BroadcastReceiver chatReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // intent can contain any data
            Bundle extras = intent.getExtras();

            if(extras != null){
                String responseType = extras.getString("responseType");
                Log.v("responseType", responseType);
                Log.v("Chat Activity", "received response: " + responseType);

                // if there's a new message add it to the list and display
                if(responseType == "message"){
                    MessageObject msg = extras.getParcelable("msg");
                    messageList.add(msg);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorchat);

        retrieveInfo(); // gets info about other chat member
        dummyPopulate();

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(chatReceiver,
                new IntentFilter("chatReceiver"));


        messagesContainer = (ListView) findViewById(R.id.list_view_messages);
        sendButton = (ImageButton) findViewById(R.id.btnSend);
        suggestButton = (ImageButton) findViewById(R.id.suggestMeetingBtn);
        chatInput = (EditText)  findViewById(R.id.msgInput);


        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                // display locally

                // create message object with params: first name, message text, senderID, recipID, isSelf, loc
//                MessageObject msg = new MessageObject(currentUser.firstName(), msgText , currentUser.getId(), recipient.getId(), true, null);
//                messageList.add(msg);
//                adapter.notifyDataSetChanged();

                // get message from input field
                String msgText = chatInput.getText().toString();

                // send message to server
                mService.sendMessage(currentUser.firstName(), currentUser.getId(), recipient.getId(), msgText);

                // clear input field
                chatInput.setText("");

            }
        });


        suggestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog(v.getContext());
            }
        });




        messageList = new ArrayList<MessageObject>();
        adapter = new MessagesAdapter(this, messageList);
        messagesContainer.setAdapter(adapter);


        messagesContainer.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                MessageObject message = messageList.get(position);

                // handle if location
                if(message.isLocation()){
                    Intent intent = new Intent(Chat.this, MapDialog.class);
                    intent.putExtra("meetingPoint", message.getLocation());
                    intent.putExtra("tutor", recipient);
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

        //get the latest session and user data
        session = new LoginManager(getApplicationContext());
        currentUser = session.getCurrentUser();

        // bind to service
        Intent intent = new Intent(this, DBService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onPause(){
        super.onPause();
        // Unbind from the service
        if (mBound){
            Log.v("Unbinding Service", "Chat Activity");
            unbindService(mConnection);
            mBound = false;
        }
    }



    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


    public void retrieveInfo(){
        // get the connected users data
        Bundle extras = getIntent().getExtras();
        if(extras != null){

            recipient = extras.getParcelable("user");
            getActionBar().setTitle(recipient.getName());
        }
    }



    public void showListDialog(Context context){

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
                if(selectedLocation.lat == 0.0 || selectedLocation.lon == 0.0){
                    selectedLocation.geoCode(getApplicationContext());
                }
                Log.v("Selected Location", String.valueOf(selectedLocation.lat) + "," + String.valueOf(selectedLocation.lon));

            }
        });

        dialog.setView(view);
        final AlertDialog alert = dialog.show();

        suggestBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                alert.cancel();
                if(selectedLocation != null){

                    mService.sendMessage(currentUser.firstName(), currentUser.getId(), recipient.getId(), selectedLocation.getAddress(), selectedLocation.lat, selectedLocation.lon);

                      // client side update
//                    MessageObject msg = new MessageObject(currentUser.firstName(), selectedLocation.getAddress(), currentUser.getId(),  recipient.getId(), true,  selectedLocation);
//                    messageList.add(msg);
//                    adapter.notifyDataSetChanged();

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

//
//    public void sendDummyMsg(){
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // display locally
//                MessageObject msg  = new MessageObject(tutor.firstName(), "Hi!", false, null);
//                messageList.add(msg);
//                adapter.notifyDataSetChanged();
//            }
//        }, 2000);
//
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // display locally
//                MessageObject msg = new MessageObject(tutor.firstName(), "Can you meet here? I've got a table reserved: ", false, null);
//                messageList.add(msg);
//                adapter.notifyDataSetChanged();
//
//            }
//        }, 3200);
//
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // display locally
//                LocationObject loc = new LocationObject(43.071394, -89.408676);
//                MessageObject msg = new MessageObject(tutor.firstName(), "215 N Randall Ave, Madison, WI 53706" , false, loc);
//                messageList.add(msg);
//                adapter.notifyDataSetChanged();
//            }
//        }, 4000);
//
//
//
//
//
//    }
//




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
