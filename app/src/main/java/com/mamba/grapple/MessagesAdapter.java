package com.mamba.grapple;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by vash on 4/8/15.
 */
public class MessagesAdapter extends BaseAdapter{

    private Context context;
    private List<MessageObject> messages;


    public MessagesAdapter(Context context, List<MessageObject> navDrawerItems){
        this.context = context;
        this.messages = navDrawerItems;
    }

    public int getCount(){
        return messages.size();
    }
    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        MessageObject m = messages.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Identifying the message owner
        if (messages.get(position).isSelf()) {

            if(messages.get(position).isLocation()){
                // message belongs to you, so load the right aligned layout
                convertView = mInflater.inflate(R.layout.location_message_right, // location message
                        null);

            }else{
                // message belongs to you, so load the right aligned layout
                convertView = mInflater.inflate(R.layout.chat_message_right,
                        null);

            }

        } else {

            if(messages.get(position).isLocation()){
                // message belongs to you, so load the right aligned layout
                convertView = mInflater.inflate(R.layout.location_message_left, // location message
                        null);

            }else {
                // message belongs to other person, load the left aligned layout
                convertView = mInflater.inflate(R.layout.chat_message_left,
                        null);
            }


            ImageView profilePic = (ImageView) convertView.findViewById(R.id.profilePic);
            profilePic.setImageResource(R.drawable.jess);
        }

        // TODO: have a different message type for notification messages

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

        txtMsg.setText(m.getMessage());
        lblFrom.setText(m.getFromName());

        return convertView;
    }

}
