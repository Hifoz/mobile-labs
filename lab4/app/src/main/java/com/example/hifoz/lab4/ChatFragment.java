package com.example.hifoz.lab4;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Fragment containing the chat tab
 */
public class ChatFragment extends Fragment {
    OnMessageSubmitListener callback;
    private ArrayList<Message> messageList;

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


    public void updateMessageList(ArrayList<DocumentSnapshot> documents){
        if(messageList == null)
            messageList = new ArrayList<>();

        for(DocumentSnapshot doc : documents){
            Message newMessage = new Message(doc);
            messageList.add(newMessage);
        }


        MessageListAdapter mla = new MessageListAdapter(getContext(), messageList);
        ListView lv = getActivity().findViewById(R.id.chatLV);
        lv.setAdapter(mla);
        // todo adjust scroll to bottom if user was at the bottom before change, otherwise maybe show indicator for new messages?
    }




    /**
     * Send the message to the server
     */
    public void submitMessage() {

        final EditText et = getActivity().findViewById(R.id.messageET);
        String messageText = et.getText().toString();

        if(messageText.isEmpty())
            return;

        HashMap<String, Object> message = new HashMap<>();
        message.put("d", System.currentTimeMillis());
        message.put("u", FBAuthInfo.user.getDisplayName());
        message.put("m", messageText);

        firestoreDB.collection("messages")
            .add(message)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>(){
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getActivity(), "Message sent.", Toast.LENGTH_SHORT).show();
                    System.out.println("Message sent.");
                    et.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Failed to send message.", Toast.LENGTH_LONG).show();
                    System.out.println("Failed to send message.");
                }
            }
        );


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

        try{
            callback = (OnMessageSubmitListener)getActivity();
        } catch (ClassCastException cce){
            throw new ClassCastException(getActivity().toString() + "must implement OnMessageSubmitListener");
        }
    }


    public void onMessageSubmit(View v){
        callback.onMessageSubmit(v);
    }


}
