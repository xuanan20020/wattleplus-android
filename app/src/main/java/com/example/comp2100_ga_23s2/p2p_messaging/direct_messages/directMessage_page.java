package com.example.comp2100_ga_23s2.p2p_messaging.direct_messages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.p2p_messaging.contacts.contacts_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Harry Randall u7499609
 * This function represents a realtime p2p messaging feature.
 * It works by saving a user message in the firebase realtime database
 * with a 'sent' or 'recieved' tag. From there, we are able to create a
 * message for both users and load them in using a recyclerview.
 * Further, the block feature was implemented and it is quite simple in
 * essance, you can't see the users messages anymore if you get block
 * them.
 */

public class directMessage_page extends AppCompatActivity {

    ImageButton back_btn, directMessage_send_msg_btn;
    Button directMessageBlockUserButton;
    TextView usernameTextView, send_message_textfield;
    RecyclerView directMessagesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_direct_message_page);
        String username = getIntent().getStringExtra("username");
        String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String topLevelIdOfUser = getIntent().getStringExtra("topLevelUserID");
        ArrayList<String> blockedList = new ArrayList<String>();
        usernameTextView = findViewById(R.id.directMessage_username_textfieid);
        directMessage_send_msg_btn = findViewById(R.id.directMessage_send_msg_btn);
        send_message_textfield = findViewById(R.id.send_message_textfield);
        directMessagesRecyclerView = findViewById(R.id.directMessagesRecyclerView);
        directMessageBlockUserButton = findViewById(R.id.directMessageBlockUserButton);

        usernameTextView.setText(username);
        back_btn = findViewById(R.id.back_btn);
        displayRecyclerView(topLevelIdOfUser);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), contacts_page.class);
                startActivity(intent);
                finish();
            }
        });

        directMessage_send_msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!send_message_textfield.getText().toString().isEmpty()) {
                    addMessageToDatabaseUser1(send_message_textfield.getText().toString(), topLevelIdOfUser);
                    addMessageToDatabaseUser2(send_message_textfield.getText().toString(), topLevelIdOfUser);
                    send_message_textfield.setText("");
                    displayRecyclerView(topLevelIdOfUser);
                } else {
                    Toast.makeText(directMessage_page.this, "Please enter a message to send", Toast.LENGTH_SHORT).show();
                }
            }
        });

/**
 * @Author Harry Randall u7499609
 * This function blocks a user and removes their messages
 * when the block button is clicked. It may also show
 * the messages again if the user was previously blocked
 */
//      Block a user
        directMessageBlockUserButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isUserBlocked(topLevelIdOfUser, new OnCheckBlockedListener() {
            @Override
            public void onCheckBlocked(boolean blocked) {
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
                DatabaseReference blacklistRef = userReference.child(userID).child("Settings").child("BlackList");
                blacklistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<String> blockedList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String blockedUser = snapshot.getValue(String.class);
                        blockedList.add(blockedUser);
                    }
                    if (blocked){
                        blockedList.remove(topLevelIdOfUser);
                        blacklistRef.setValue(blockedList);
                        directMessageBlockUserButton.setText("Block");
                        usernameTextView.setTextColor(Color.parseColor("#000000"));
                        directMessage_send_msg_btn.setVisibility(View.VISIBLE);
                        send_message_textfield.setVisibility(View.VISIBLE);
                    }else{
                        blockedList.add(topLevelIdOfUser);
                        blacklistRef.setValue(blockedList);
                    }
                    displayRecyclerView(topLevelIdOfUser);
                }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Error", "onCancelled method");
                    }
                });
            }

        });
    }});
}

    /**
     * @autor Harry Randall u7499609
     * This function displays the messages from the user. It needs
     * the targetuserid because the messages are stored as children
     * inside of the currently authenticated user.
     * @param targetUserID
     */
    void displayRecyclerView(String targetUserID) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        List<MessageClass> messages = new ArrayList<MessageClass>();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).child("Messages").child(targetUserID);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        directMessagesRecyclerView.setLayoutManager(layoutManager);

        directMessagesRecyclerView.setAdapter(new directMessagesAdapter(getApplicationContext(), messages));

        // Check if the user is blocked
        isUserBlocked(targetUserID, new OnCheckBlockedListener() {
            @Override
            public void onCheckBlocked(boolean blocked) {
                if (blocked) {
                    directMessageBlockUserButton.setText("Unblock");
                    usernameTextView.setTextColor(Color.parseColor("#E6676B"));
                    directMessage_send_msg_btn.setVisibility(View.GONE);
                    send_message_textfield.setVisibility(View.GONE);
                } else {
                    // User is not blocked, proceed to fetch and display messages
                    userReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                            if (dataSnapshot.exists()) {
                                String message = dataSnapshot.child("Message").getValue(String.class);
                                String messageType = dataSnapshot.child("Type").getValue(String.class);
                                String messageAuthor = dataSnapshot.child("From").getValue(String.class);
                                String messageTime = dataSnapshot.child("Time").getValue(String.class);
                                messages.add(0, new MessageClass(message, messageType, messageAuthor, messageTime));
                                directMessagesRecyclerView.getAdapter().notifyItemInserted(0);
                                directMessagesRecyclerView.scrollToPosition(0);
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("Error", "onCancelled method");
                        }
                    });
                }
            }
        });
    }

    /**
     * @author Harry Randall u7499609
     * This function checks the firebase realtime database
     * to see if a user is blocked.
     * @param userId
     * @param listener
     */
    private void isUserBlocked(String userId, final OnCheckBlockedListener listener) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID);
        DatabaseReference blacklistRef = userReference.child("Settings").child("BlackList");
        blacklistRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean blocked = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.exists() && Objects.equals(userSnapshot.getValue(), userId)){
                        blocked = true;
                        break;
                    }
                }
                Log.d("userid", userId);
                listener.onCheckBlocked(blocked);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Error", "onCancelled method");
            }
        });
    }

    // Define an interface for the callback
    interface OnCheckBlockedListener {
        void onCheckBlocked(boolean blocked);
    }


    /**
     * @author Harry Randall u7499609
     * This function adds the message to the database for the user the message
     * is sent to.
     * @param message
     * @param topLevelIdOfUser
     */
    //  All of the database stuff.
    void addMessageToDatabaseUser1(String message, String topLevelIdOfUser) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID)
                .child("Messages").child(topLevelIdOfUser);
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("From", currentUserID);
        messageData.put("Message", message);
        messageData.put("Type", "Sent");
        // Add other message data fields as needed
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy | HH:mm:ss").format(Calendar.getInstance().getTime());
        messageData.put("Time", timeStamp);

        getUserIDFromFirebase(currentUserID, new UserIDCallback() {
            @Override
            public void onUserIDReceived(String authUserID) {
                messageData.put("To", topLevelIdOfUser);
            }
        });
        messagesRef.child("Message" + timeStamp).setValue(messageData);
    }

    /**
     * @author Harry Randall u7499609
     * This function adds the message to the database for the user the message
     * is received from.
     * @param message
     * @param topLevelIdOfUser
     */
    void addMessageToDatabaseUser2(String message, String topLevelIdOfUser) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("Users").child(topLevelIdOfUser)
                .child("Messages").child(currentUserID);
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("From", currentUserID);
        messageData.put("Type", "Received");
        messageData.put("Message", message);
        // Add other message data fields as needed
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("dd-MM-yyyy | HH:mm:ss").format(Calendar.getInstance().getTime());
        messageData.put("Time", timeStamp);

        getUserIDFromFirebase(currentUserID, new UserIDCallback() {
            @Override
            public void onUserIDReceived(String authUserID) {
                messageData.put("To", topLevelIdOfUser);
            }
        });
        messagesRef.child("Message" + timeStamp).setValue(messageData);
    }




    public interface UserIDCallback {
        void onUserIDReceived(String userID);
    }
    private void getUserIDFromFirebase(String authUserID, UserIDCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(authUserID).child("Details");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userID = dataSnapshot.child("userID").getValue(String.class);
                if (userID != null) {
                    callback.onUserIDReceived(userID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

}






//      When the message is sent, run a function that adds the message to the realtime database, the have
//      a ondataupdate function

//    void add_message_to_firebaseDB(String message, String userID){
//        Perhaps we have a message order. E.G user A sends a message (will add a section, message 1)
//        Then user B sends a messsage (message 2) then user B sends a message (message 3)
//        Then we order it based on those messages.
//    }
//    More logic
//    When a user loads the page, we have a list of texts that we return to the user. That way, when a
//    user sends a message, this function will be called and the text will be updated
//    but also when the user loads the data.

//    Okay so we have two users: u7111111 and u999999
//    u7111111 is logged in. u999999 is receiving the message
//    Lets say u7111111 -> sends message to -> u999999
//    We write to u7111111 a new contact u999999 (if it doesn't already exists)
//    We do the same thing for u9999999
//    When u7111111 sends a message to u999999, we get the length of messages from
//    u9999999 and u7111111 and we add them together. E.G 15. We then add 1 and now
//    we have ordered messages.
//
//    void addContactToContacts(String userID) {
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
//        DatabaseReference contactsRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).child("Contacts");
//        contactsRef.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    contactsRef.child(userID).setValue(true);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle errors if needed
//            }
//        });
//    }