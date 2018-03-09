package com.example.hifoz.lab4;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;



/**
 * Fragment containing the chat tab
 */
public class ChatFragment extends Fragment {
    OnMessageSubmitListener callback;

    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }


    /**
     * Send the message to the server
     */
    public void submitMessage() {

        EditText et = getActivity().findViewById(R.id.messageET);
        String messageText = et.getText().toString();
        System.out.println(messageText);

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
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to send message.", Toast.LENGTH_LONG).show();
                        System.out.println("Failed to send message.");
                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                System.out.println("complete");
            }
        });

        et.setText("");

    }


    /**
     * An interface that is required to implement oin any activity using this fragment
     * To make sure it has the onMessageSubmit(View) function that is called by a button in the fragment XML
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
}
