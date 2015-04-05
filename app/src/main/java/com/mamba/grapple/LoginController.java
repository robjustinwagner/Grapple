package com.mamba.grapple;

// *android imports*
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.mamba.grapple.R;
import com.mamba.grapple.SignInGooglePlus;

// *socket.io imports*
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*; // for URIexception

// *HTTP imports*
import java.net.URL;
import java.security.cert.Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

// *json imports*
import com.google.gson.Gson;

public class LoginController {

    public static final Gson gson = new Gson();

    /*
    public void submitUserPass(View view) {
        Intent intent = new Intent(this, SignInGooglePlus.class);
        Intent intent1 = new Intent();
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
    */

    public boolean verifyUserPass() {
        //TODO;
        return false;
    }

    public static void attemptLogin(Credentials credentials){

        try{
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

abstract class Networking extends AsyncTask<String, Void, Void>{

    void login() {

    }

    void socketConnect(){

    }
}