package com.example.hifoz.lab4;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class MessageListAdapter extends ArrayAdapter<Message> {
    private ArrayList<Message> messages;

    public MessageListAdapter(Context context, ArrayList<Message> messages) {
        super(context, R.layout.message_layout, messages);
        this.messages = messages;
    }

    @NonNull
    @Override
    public View getView(int pos, View convertView, @NonNull ViewGroup parent){
        ViewHolder vHolder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.message_layout, parent, false);

            vHolder.messageUser = convertView.findViewById(R.id.messageUser);
            vHolder.messageTimeStamp = convertView.findViewById(R.id.messageTimeStamp);
            vHolder.messageContent = convertView.findViewById(R.id.messageContent);

            convertView.setTag(vHolder);

        } else {
            vHolder = (ViewHolder)convertView.getTag();
        }

        vHolder.messageUser.setText(messages.get(pos).u);
        vHolder.messageTimeStamp.setText(messages.get(pos).d);
        vHolder.messageContent.setText(messages.get(pos).m);
        vHolder.position = pos;

        return convertView;
    }



    private class ViewHolder{
        TextView messageUser;
        TextView messageTimeStamp;
        TextView messageContent;
        int position;
    }
}
