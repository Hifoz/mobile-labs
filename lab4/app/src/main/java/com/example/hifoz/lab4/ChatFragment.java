package com.example.hifoz.lab4;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Fragment for the chat tab
 */
public class ChatFragment extends Fragment {
    OnMessageSubmitListener callback;
    private ArrayList<Message> messageList;
    private ArrayList<Message> displayedMessagesList;
    String displayedName = "Show All";
    Context context;
    ListenerRegistration snapshotListener;

    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();

    /**
     * Required empty constructor because Fragment
     */
    public ChatFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    /**
     * Updates list of messages based on the documents sent in
     * @param documents documents from Firestore DB containg all new messages
     */
    public void updateMessageList(ArrayList<DocumentSnapshot> documents){
        if(messageList == null)
            messageList = new ArrayList<>();
        if(displayedMessagesList == null)
            displayedMessagesList = new ArrayList<>();

        for(DocumentSnapshot doc : documents){
            Message newMessage = new Message(doc);
            messageList.add(newMessage);
            if(newMessage.u.equalsIgnoreCase(displayedName) || displayedName.equalsIgnoreCase("show all"))
                displayedMessagesList.add(newMessage);
        }
        updateMessagesListView();
        BackgroundService.messageCount = messageList.size();
    }

    /**
     * Refreshes the list view.
     */
    private void updateMessagesListView(){
        MessageListAdapter mla = new MessageListAdapter(context, displayedMessagesList);
        ListView lv = ((MainActivity)context).findViewById(R.id.chatLV);
        lv.setAdapter(mla);
    }

    /**
     * Updates which messages are actually displayed on screen
     * @param name name of the user who's messages you want to show, "show all" will show messages from all users
     */
    public void updateDisplayedList(String name) {
        System.out.println(name);
        displayedName = name;
        if(name.equalsIgnoreCase("show all")){
            displayedMessagesList = new ArrayList<>(messageList);
        } else{
            displayedMessagesList = new ArrayList<>();
            for(Message message : messageList){
                if(message.u.equalsIgnoreCase(name))
                    displayedMessagesList.add(message);
            }
        }
        updateMessagesListView();
    }

    /**
     * Send the message to the Firestore DB
     */
    public void submitMessage() {

        final EditText et = getActivity().findViewById(R.id.messageET);
        String messageText = et.getText().toString();

        if(messageText.isEmpty())
            return;

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        String name = sharedPrefs.getString("username", "NE");

        // Format the message for sending
        HashMap<String, Object> message = new HashMap<>();
        message.put("d", System.currentTimeMillis());
        message.put("u", name);
        message.put("m", messageText);

        // Send the message
        firestoreDB.collection("messages")
            .add(message)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getActivity(), "Message sent.", Toast.LENGTH_SHORT).show();
                    System.out.println("Message sent.");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed to send message.", Toast.LENGTH_LONG).show();
                    System.out.println("Failed to send message.");
                }
            }
        );
        et.setText("");
    }



    /**
     * An interface that is required to implement on any activity using this fragment
     * To make sure it has the onMessageSubmit(View) function that is called by a button in the fragment XML.
     * This Listener can also be made to serve as a communications channel to the activity and other fragments
     */
    public interface OnMessageSubmitListener{
        void onMessageSubmit(View v);
    }

    /**
     * Makes sure the OnMessageSubmitListener is implemented in the parent activity
     * @param context context of the activity
     */
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;

        try{
            callback = (OnMessageSubmitListener)getActivity();
        } catch (ClassCastException cce){
            throw new ClassCastException(getActivity().toString() + "must implement OnMessageSubmitListener");
        }
    }

    /**
     * Need to have this because otherwise this fragment's xml will complain.
     * The function is never actually called because it actually calls a function with the same name in the parent activity.
     */
    public void onMessageSubmit(View v){
        callback.onMessageSubmit(v);
    }

    @Override
    public void onResume() {
        //BackgroundService.appIsActive = true;
        setupSnapshotListener();
        super.onResume();
    }

    @Override
    public void onPause() {
        //BackgroundService.appIsActive = false;
        snapshotListener.remove();
        super.onPause();
    }


    /**
     * Setup a listener to listen for new messages
     * On first call, this will get all messages stored
     */
    private void setupSnapshotListener() {
        messageList = null; // Make sure the list is empty
        displayedMessagesList = null;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        snapshotListener = db.collection("messages").orderBy("d").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.out.println("Snapshot listener fail");
                    return;
                } else if (documentSnapshots == null) {
                    System.out.println("Snapshots are null");
                    return;
                }
                ArrayList<DocumentSnapshot> newDocs = new ArrayList<>();
                for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED)
                        newDocs.add(dc.getDocument());
                }
                if (!newDocs.isEmpty()) {
                    updateMessageList(newDocs);
                    ((FriendsListFragment)((MainActivity)getActivity()).fragments[1]).updateUserList(newDocs);
                }
            }
        });
    }


}
