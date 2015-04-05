package com.mamba.grapple;

// *android imports*
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.mamba.grapple.DBService;

public class Splash extends ActionBarActivity {

    /**
     * Duration of wait *
     */
    private final int SPLASH_DISPLAY_LENGTH = 5000;
    public final static String EXTRA_MESSAGE = "com.mamba.grapple.MESSAGE";

    private boolean loggedIn = false;
    private String token;
    private boolean mBound = false;
    DBService mService;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        // check to see if the user is logged in here
        loginCheck();

        if(loggedIn){
            // start the background networking thread and open up socket connection
            Log.v("DBService", "Binding DBService from Splash..");
            // we must start and bind the service so we have control of its lifecycle
            startService(new Intent(this, DBService.class));
            bindService(new Intent(this, DBService.class), mConnection, Context.BIND_AUTO_CREATE );
        }

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Splash.this, Search.class);
                mainIntent.putExtra("loggedIn", loggedIn);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
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


    public void loginCheck(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sharedPreferences.getString("token", null);
        if(token != null){
            Log.v("Preference Token", token);
            loggedIn = true;
            Log.v("Login Status", "User has been logged in");

        }
    }

    private ServiceConnection mConnection = new ServiceConnection(){
        public void onServiceConnected(ComponentName className, IBinder service){
           DBService.LocalBinder binder = (DBService.LocalBinder) service;
           mService = binder.getService();
           mBound = true;

           // send the token
           mService.setToken(token);
        }

        public void onServiceDisconnected(ComponentName arg0){
            mBound = false;
        }
    };



}
/* REMOVE THE ABOVE } TO FIX!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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

    public void submitUserPass(View view) {
        Intent intent = new Intent(this, SignInGooglePlus.class);
        EditText userLogin = (EditText) findViewById(R.id.email);
        String login = userLogin.getText().toString();
        EditText userPass = (EditText) findViewById(R.id.password);
        String pass = userPass.getText().toString();

    //  intent.putExtra(EXTRA_MESSAGE, login);
    //  intent.putExtra(EXTRA_MESSAGE, pass);

        if (!verifyUserPass()) {
        }
        Credentials credentials = new Credentials(login, pass);
        attemptLogin(credentials);
        talkToDB(credentials);
        startActivity(intent);
    }

    public boolean verifyUserPass() {
        //TODO;
        return false;
    }

    public void attemptLogin(Credentials credentials){

        try {
            Log.v("http", "attempting http connection");
            final String json = gson.toJson(credentials);
            URL url = new URL("http://protected-dawn-4244.herokuapp.com/login");
            //HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            HttpsURLConnection connection = new HttpsURLConnection(url) {
                @Override
                public String getCipherSuite() {
                    return null;
                }

                @Override
                public Certificate[] getLocalCertificates() {
                    return new Certificate[0];
                }

                @Override
                public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
                    return new Certificate[0];
                }

                @Override
                public void disconnect() {

                }

                @Override
                public boolean usingProxy() {
                    return false;
                }

                @Override
                public void connect() {
                    try {
                        Log.v("connection", "inside connection.connect()");
                        OutputStream output = new BufferedOutputStream(this.getOutputStream()); // TODO fix error
                        //Log.v("","");
                        output.write(json.getBytes());
                        Log.v("http", "wrote to output stream");
                        output.flush();
                        Log.v("http", "flushed output stream");
                        Log.v("http", "accessed output stream");
                        //writeStream(output);
                        Log.v("http", "sent credentials");
                    }
                    catch (IOException e) {Log.v("http","IOexcpetion in connection.connect");}
                }
            };
            Log.v("http", "opened connection to " + connection.getURL());
            connection.setDoOutput(true); // switch from GET to POST mode
            Log.v("http", "tried to set post, POST = " + connection.getDoOutput());
            connection.setChunkedStreamingMode(0);
            Log.v("http", "setChunkedStreamingMode");
            connection.connect();
            Log.v("http", "connected");
            //Log.v("http", "about to post, POST=" + connection.getDoOutput());
            //OutputStream stream = connection.getOutputStream(); // TODO fix error
            OutputStream output = new BufferedOutputStream(connection.getOutputStream()); // TODO fix error
            //Log.v("","");
            output.write(json.getBytes());
            Log.v("http", "wrote to output stream");
            output.flush();
            Log.v("http", "flushed output stream");
            Log.v("http","accessed output stream");
            //writeStream(output);
            Log.v("http", "sent credentials");
        }
        catch (MalformedURLException e){ Log.v("http", "caught MalformedURLException");}
        catch (IOException e){ Log.v("http", "caught IOException");}

    }

    public void talkToDB(Object obj) {
        final Socket socket;
        final Gson gson = new Gson();
        final String dbData = "";

        try {
            socket = IO.socket("http://protected-dawn-4244.herokuapp.com").connect();
            final String json = gson.toJson(obj);

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    socket.emit("Login", json);
                    Log.v("socket", "sent login credentials");
                }

            }).on("Hello", new Emitter.Listener() {

                @Override
                public void call(Object... args) {
                    Log.v("socket", "Recognized event 'Hello'");
                    Object ret = args[0];
                    Log.v("socket", "received " + gson.toJson(ret));
                }

            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {

                }

            });
        }
        catch (URISyntaxException e){}

    }

}

abstract class Networking extends AsyncTask<String, Void, Void>{

    void login() {

    }

    void socketConnect(){

    }
}

 class Credentials{
        String user;
        String pass;

        // constructor
        Credentials(String user,String pass)
        {
        this.user=user;
        this.pass=pass;
        }
 }

*/