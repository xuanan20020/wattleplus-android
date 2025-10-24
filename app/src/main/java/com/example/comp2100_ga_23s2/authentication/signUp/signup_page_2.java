package com.example.comp2100_ga_23s2.authentication.signUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.home_page;
import com.example.comp2100_ga_23s2.notification.NotificationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Harry Randall u7499609
 * This page gets the users DOB and username. It runs some checks on them to
 * see if they are valid entries (if not, it provides toast messages).
 * It will then try to authenticate the user (provide appropriate errors)
 * and then it will add all of the details to the database and initiate the user.
 */
public class signup_page_2 extends AppCompatActivity {
//  Predefined variables for android studio
    FirebaseAuth mAuth;
    TextView username_field, user_id;
    Button submit_button, back_button;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page_2);
//      Assigning the previous variables.
        submit_button = findViewById(R.id.submit_button);
        back_button = findViewById(R.id.back_button);
        username_field = findViewById(R.id.username_field);
        user_id = findViewById(R.id.user_id);
        datePicker = findViewById(R.id.datePicker);
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

//      Getting the Username and Password from the previous file
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String emailText = extras.getString("email");
        String passwordText = extras.getString("password");

//      When the user clicks the submit button, run this function
        submit_button.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
//          Check if anything is not filled in
            if (username_field.getText().toString().isEmpty() || user_id.getText().toString().isEmpty()){
                Toast.makeText(signup_page_2.this, "Please fill out all of the fields.", Toast.LENGTH_SHORT).show();
//          Check the username field
            } else if (username_field.getText().toString().length() < 3) {
                Toast.makeText(signup_page_2.this, "Please make sure the username length is >3 characters", Toast.LENGTH_SHORT).show();
//          Check the UID field.
            } else if (user_id.getText().toString().length() != 8 || !user_id.getText().toString().substring(1).matches("\\d{7}") ||
                    user_id.getText().toString().charAt(0) != 'u') {
                Toast.makeText(signup_page_2.this, "Please make sure the follow the UID format: [u1234567]", Toast.LENGTH_SHORT).show();
            } else{
                //          Look through the database and get all of the entries
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                  Set booleans to check if the UID or username already exists
                        boolean usernameExists = false;
                        boolean useridExists = false;
//                  Look through the database at those snapshots to see if it exists.
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            DataSnapshot userDetailsSnapshot = userSnapshot.child("userDetails");
//                      If it exists, set the usernamExists flag to true to run logic later.
                            if (userDetailsSnapshot.exists()) {
                                String db_username = userDetailsSnapshot.child("username").getValue(String.class);
                                String db_uid = userDetailsSnapshot.child("userID").getValue(String.class);
                                if (db_username != null) {
                                    if (db_username.equals(username_field.getText().toString())) {
                                        usernameExists = true;
                                        break;
                                    }
//                                  break here as if it finds one entry, thats all it needs. Saves memory.
                                }
                                /*Do the same for the UID*/
                                if (db_uid != null) {
                                    if (db_uid.equals(user_id.getText().toString())) {
                                        useridExists = true;
                                        break;
                                    }
                                }
                            }
                        }
//          Based on those previous flags, output a toast message to the user.
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (usernameExists && currentUser == null) {
                            Toast.makeText(signup_page_2.this, "Username already exists.", Toast.LENGTH_SHORT).show();
                        } else if (useridExists && currentUser == null) {
                            Toast.makeText(signup_page_2.this, "User ID already exists.", Toast.LENGTH_SHORT).show();
                        } else{
//              If everything is fine, authenticate the user through the below function.
                            authenticate_user(emailText, passwordText);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Error", "onCancelled method");
                    }
                });
            }
        }});
//      If the user clicks the back button, go back to the last activity.
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), signup_page.class);
                startActivity(intent);
            }
        });
}
//  Sign the user up with their username and password
    public void authenticate_user(String usernameText, String passwordText) {
        assert usernameText != null; assert passwordText != null;
        mAuth.createUserWithEmailAndPassword(usernameText, passwordText)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                  If it works, go to sign them in.
                    signInUser(usernameText, passwordText);
                } else {
//                  If it doesn't work, give them a toast message as to why.
                    Toast.makeText(signup_page_2.this, "Authentication failed: " +
                            Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                  Will only fail because of the username and password, so bring them back to that page.
                    Intent intent = new Intent(getApplicationContext(), signup_page.class);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * This function will sign the user in from it's username and password.
     * If successful it will send the user to the home page and give them a
     * toast message. if not, it will say "Sign-in failed: " + error msg.
     * @param usernameText
     * @param passwordText
     * @author Harry Randall
     */
    private void signInUser(String usernameText, String passwordText) {
    mAuth.signInWithEmailAndPassword(usernameText, passwordText)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                  If the user is signed in, we need to log all of their information in the realtime DB.
                    int day = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth();
                    int year = datePicker.getYear() - 1900; //not sure why?
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = new Date(year, month, day);
                    String strDate = dateFormatter.format(d);
//                  We have their username, userID, the date and their email
                    add_user_to_db(username_field.getText().toString(), user_id.getText().toString(), strDate);
//                  Let the user know that the authentication was successful.
                    Toast.makeText(signup_page_2.this, "Authentication success!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), home_page.class);
                    startActivity(intent);
                } else {
                    // Sign-in failed, tell the user why
                    Toast.makeText(signup_page_2.this, "Sign-in failed: " +
                            Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * This function will add the current authenticated user to the database.
     * It will only work if the user is authenticated and it will fetch the following
     * parameters:
     * @param DOB
     * @param userID
     * @param username
     * It will also fetch the useremail but it can do that within.
     * @author Harry Randall, Douglas Carroll, Gia Minh Nguyen
     */
    public void add_user_to_db(String username, String userID, String DOB) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userClass = database.getReference("Users");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        String UID = currentUser.getUid();
        DatabaseReference userReference = userClass.child(UID);

        DatabaseReference contactsRef = userReference.child("Contacts");
        DatabaseReference coursesRef = userReference.child("Courses");
        DatabaseReference detailsRef = userReference.child("Details");

//      Add some basic contacts for the user.
        List<String> contacts = new ArrayList<>();
        // todo: add default emails e.g. comp2100 as default contacts
        contacts.add("comp2100");
        contacts.add("comp6442");
//      Add some classes for the user
        List<String> classes = new ArrayList<>();
        classes.add("COMP2100");
        classes.add("COMP2400");
        classes.add("COMP1100");
        classes.add("COMP1110");
//      Add the primary user details.
        Map<String, Object> userDetailsMap = new HashMap<>();
        userDetailsMap.put("username", username);
        userDetailsMap.put("userID", userID.substring(1));
        userDetailsMap.put("DOB", DOB);
        userDetailsMap.put("email", currentUser.getEmail());
        @SuppressLint("SimpleDateFormat") String timeStamp = new
                SimpleDateFormat("dd-MM-yyyy | HH:mm:ss").format(Calendar.getInstance().getTime());
        userDetailsMap.put("created", timeStamp);
        detailsRef.updateChildren(userDetailsMap);
        contactsRef.setValue(contacts);
        // add privacy settings
        DatabaseReference privacy = userReference.child("Settings");
        DatabaseReference showDOBSettings = privacy.child("showDOB");
        showDOBSettings.setValue(true);

//      Add blacklist to database,set as empty list
        DatabaseReference blackList = privacy.child("BlackList");
        ArrayList<String> list = new ArrayList<String>();
        blackList.setValue(list);

        Intent sendMessage = new Intent(this, NotificationService.class);
        sendMessage.putExtra("notificationMessage", ("User " + userID + " has just signed up!"));
        startService(sendMessage);

//      Add everything for the user.
        coursesRef.setValue(classes).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }
}

