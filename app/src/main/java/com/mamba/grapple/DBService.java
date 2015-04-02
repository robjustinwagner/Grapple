package com.mamba.grapple;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

// *socket.io imports*
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.IO.*;
import com.github.nkzawa.socketio.client.SocketIOException;
import com.google.gson.JsonElement;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*; // for URIexception
import java.util.Properties;


public class DBService extends Service {

    private Socket socket;

    private synchronized void connect() {
        if (socket == null || !socket.connected()) {
            Properties properties = new Properties();
            // TODO use properties here?

            try {
                socket = IO.socket("http://protected-dawn-4244.herokuapp.com");
                socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    public void call(Object... args){
                        socket.emit("grapple", "It worked!");
                        Log.v("Socket", "received connection event");
                    }

                 });
            }catch (URISyntaxException e) {
                Log.e("Bad URI", e.getMessage());
            }

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("Service", "onCreate hit");
        connect();
    }

    public void onDisconnect() {

    }

    public void onConnect() {
        socket.emit("grapple", "It's working");
    }

    public void onMessage(String data, Ack ack) {

    }

    public void onMessage(JsonElement json, Ack ack) {

    }

    public void on(String event, Ack ack, JsonElement... args) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("action_on"));
    }

    public void onError(SocketIOException socketIOException) {

    }

    //bullshit methods
    public IBinder onBind(Intent intent) {
        return null;
    }
}