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

/**
 * Fragment for the friendslist tab
 */
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

    /**
     * Gives visual feedback in friends list tab to let user know whos' messages are now displayed in the chat tab
     * @param v the view from the listview onitemclicklistener
     * @param pos the position from the listview onitemclicklistener
     * @param id the id from the listview onitemclicklistener
     */
    public void updateDisplayedUser(View v, int pos, long id) {
        if(pos == 0)
            Toast.makeText(getContext(), "Now showing posts all users", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "Now showing posts from " + usernames.get(pos), Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the list of users by extracting user info from new messages
     * @param newDocs from Firestore DB containing all new messages
     */
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

    /**
     * An interface that is required to implement on any activity using this fragment
     * To make sure it has the onUserSelect(View, int, long) function that is called by a button in the fragment XML.
     * This Listener can also be made to serve as a communications channel to the activity and other fragments
     */
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



    /**
     * Need to have this because otherwise this fragment's xml will complain.
     * The function is never actually called because it actually calls a function with the same name in the parent activity.
     */
    public void onUserSelect(View v, int pos, long id){
        callback.onUserSelect(v, pos, id);
    }



}
