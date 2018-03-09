package com.example.hifoz.lab4;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class FriendsListFragment extends Fragment {
    OnUserSelectListener callback;
    ArrayList<String> usernames;

    /**
     * Empty constructor required because Fragment
     */
    public FriendsListFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends_list, container, false);
    }

    public void updateDisplayedUser(View v, int pos, long id) {
        if(pos == 0)
            Toast.makeText(getContext(), "Now showing posts all users", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "Now showing posts from " + usernames.get(pos), Toast.LENGTH_SHORT).show();
    }

    public void updateUserList(ArrayList<DocumentSnapshot> newDocs){
        if(usernames == null){
            usernames = new ArrayList<>();
            usernames.add("Show All");
        }

        for(DocumentSnapshot dc : newDocs){
            String newName = (String)dc.get("u");
            if(!usernames.contains(newName))
                usernames.add((String)dc.get("u"));
        }

        UserListAdapter ula = new UserListAdapter(getContext(), usernames);
        ListView lv = getActivity().findViewById(R.id.friendsLV);
        lv.setAdapter(ula);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                onUserSelect(view, pos, id);
            }
        });
    }

    public interface OnUserSelectListener{
        void onUserSelect(View v, int pos, long id);
    }

    /**
     * Makes sure the OnUserSelectListener is implemented in the parent activity
     * @param context context of the activity
     */
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            callback = (FriendsListFragment.OnUserSelectListener)getActivity();
        } catch (ClassCastException cce){
            throw new ClassCastException(getActivity().toString() + "must implement OnUserSelectListener");
        }
    }



    public void onUserSelect(View v, int pos, long id){
        callback.onUserSelect(v, pos, id);
    };



}
