package com.mamba.grapple;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
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


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.*; // for URIexception
import java.util.HashMap;
import java.util.Properties;


public class DBService extends Service {

    private Socket socket;
    private String token;
    private final IBinder myBinder = new LocalBinder();




// socket thread
//   class connectSocket implements Runnable{
//
//       public void run(){
//           if (socket == null || !socket.connected()){
//               Properties properties = new Properties();
//               // TODO use properties here?
//
//               try {
//                   Log.v("Service", "Attempting Socket Connection..");
//                   socket = IO.socket("http://protected-dawn-4244.herokuapp.com");
//                   socket.on("message", message );
//                   socket.connect();
//                   socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//
//                       public void call(Object... args){
//                           socket.emit("grapple", "It worked!");
//                           Log.v("Socket", "received connection event");
//
//
//
//                       }
//
//                   });
//               }catch (URISyntaxException e){
//                   Log.e("Bad URI", e.getMessage());
//               }
//
//           }
//       }
//    }



    @Override
    public void onCreate() {
        System.out.println("DBService Created");
        super.onCreate();

        // set up socket connection
        if (socket == null || !socket.connected()){
            try {
                socket = IO.socket("http://protected-dawn-4244.herokuapp.com");
            } catch (URISyntaxException e){
                Log.e("Bad URI", e.getMessage());
            }
        }

        // create listeners
        // socket.on("message", message);
        // socket.on("locationUpdate", locationUpdate);
        // socket.on("meetingSuggestion", "meetingSuggestion);
        // socket.on("startSessionRequest", startSessionRequest);

        // socket.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }

    public void setToken(String token){
        this.token = token;
        Log.v("Service received token", token);

    }

    public String getToken(){
        return token;

    }

    private Emitter.Listener message = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONObject data = (JSONObject) args[0];
            // send broadcast to activity and run on the main thread
        }
    };


    public IBinder onBind(Intent intent) {
       return myBinder;
    }

    public class LocalBinder extends Binder {
        public DBService getService() {
            System.out.println("I am in Localbinder ");
            return DBService.this;

        }
    }



}