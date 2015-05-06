package com.mamba.grapple;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Broadcast extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private OnFragmentInteractionListener mListener;
    private int availableTime = 30;
    private double price = 10.00;
    private int distance = 1;
    String[] courses =  {"Comp Sci 302", "Physics 202"};

    Button broadcastButton;

    public Broadcast() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_broadcast, container, false);
    }

    @Override
    public void onStart(){
       super.onStart();
       broadcastButton = (Button) getView().findViewById(R.id.broadcastButton);


       broadcastButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                startBroadcast();
           }
       });

    }

    public void startBroadcast(){
        Log.v("Starting Broadcast..", "Broadcast initiated");

        ((Main)getActivity()).mService.startBroadcast(availableTime, distance, price, courses);

    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }





    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }




}
