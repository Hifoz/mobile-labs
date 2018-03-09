package com.example.hifoz.lab4;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter<String> {
    private ArrayList<String> usernames;

    public UserListAdapter(@NonNull Context context, ArrayList<String> usernames) {
        super(context, R.layout.user_layout, usernames);
        this.usernames = new ArrayList<String>();
        this.usernames.addAll(usernames);
    }


    @NonNull
    @Override
    public View getView(int pos, View convertView, @NonNull ViewGroup parent){
        ViewHolder vHolder = new ViewHolder();

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_layout, parent, false);

            vHolder.username = convertView.findViewById(R.id.usernameTV);

            convertView.setTag(vHolder);

        } else {
            vHolder = (UserListAdapter.ViewHolder)convertView.getTag();
        }

        vHolder.username.setText(usernames.get(pos));
        vHolder.position = pos;

        return convertView;
    }



    public class ViewHolder{
        public TextView username;
        public int position;
    }
}
