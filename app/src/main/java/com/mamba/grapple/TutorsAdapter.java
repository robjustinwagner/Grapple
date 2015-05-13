package com.mamba.grapple;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by vash on 4/2/15.
 */
public class TutorsAdapter extends ArrayAdapter<TutorObject> {

    private Location userLocation;



    public TutorsAdapter(Context context, ArrayList<TutorObject> tutors) {
        super(context, 0, tutors);

    }

    public View getView(int position, View convertView, ViewGroup parent){

        // get the data item for this position
        TutorObject tutor = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tutor_row, parent, false);
        }

        // Look up view for data population
        TextView tutorName = (TextView) convertView.findViewById(R.id.tutorName);
        TextView tutorDistance = (TextView) convertView.findViewById(R.id.tutorDistance);
        TextView tutorPrice = (TextView) convertView.findViewById(R.id.tutorPrice);
        ImageView tutorPic = (ImageView) convertView.findViewById(R.id.profilePic);
        RatingBar tutorRating = (RatingBar) convertView.findViewById(R.id.ratingBar);

        // populate the data into the list item template
        tutorName.setText(tutor.firstName + " " + tutor.lastName);
        tutorDistance.setText(tutor.getDistance(userLocation) + " mi");
        tutorPrice.setText("$" + String.valueOf(tutor.session.price));
        tutorRating.setRating(tutor.rating);


        // TEMP DUMMY TUTORS
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

        // return the completed view to render
        return convertView;
    }


    // use to add in profile picture
    public static Drawable LoadImageFromWebOperations(String url){
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "");
            return d;
        } catch (Exception e) {
            return null;
        }
    }


    public void setUserLocation(Location userLocation){
        if(userLocation != null)
            this.userLocation = userLocation;
    }

}


