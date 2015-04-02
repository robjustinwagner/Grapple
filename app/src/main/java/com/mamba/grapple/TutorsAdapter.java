package com.mamba.grapple;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by vash on 4/2/15.
 */
public class TutorsAdapter extends ArrayAdapter<TutorObject> {
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


        // populate the data into the list item template
        tutorName.setText(tutor.firstName + " " + tutor.lastName);
        tutorDistance.setText(String.valueOf(tutor.distance));

        // return the completed view to render
        return convertView;
    }


    // use to add in profile picture
    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

}


