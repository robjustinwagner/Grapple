package com.mamba.grapple;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class Results extends ActionBarActivity {

    ListView listView;
    SharedPreferences sharedPreferences;
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            // get the tutor list from previous activity
            ArrayList<TutorObject> tutorList = extras.getParcelableArrayList("tutorList");
            Log.v("tutorList", String.valueOf(tutorList));

            // populate the list view
            TutorsAdapter adapter = new TutorsAdapter(this, tutorList);
            listView = (ListView)findViewById(R.id.listView);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                 if(!loggedIn){
                     // transfer the user to the register page
                     Intent intent = new Intent(Results.this, Register.class);
                     // we expect the auth response
                     startActivityForResult(intent, 1);
                 }else{
                    Log.v("Login status", "Logged in user");

                 }


                }
            });
        }
    }

    // check login status every time the activity gets shown
    public void onStart(){
        super.onStart();
        loginCheck();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Log.v("Reached", "Auth Activity Result");
            String token = data.getStringExtra("token");
            if(token != null){
                Log.v("Extracted", "token: " + token);
                loginCheck();
            }
        }
    }


    public void loginCheck(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String token = sharedPreferences.getString("token", null);
        if(token != null){
            Log.v("Preference Token", token);
            loggedIn = true;
            Log.v("Login Status", "User has been logged in");


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_results, menu);
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
}
