package com.example.comp2100_ga_23s2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.comp2100_ga_23s2.authentication.login_page;
import com.example.comp2100_ga_23s2.p2p_messaging.contacts.contacts_page;
import com.example.comp2100_ga_23s2.searchCourses.searchCourse.searchCoursesPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Class for the profile page.
 * @author Douglas Carroll - u7483922
 */
public class profile_page extends AppCompatActivity {

    // Initialise UI elements
    Button logout_button, navbar_search_button, navbar_contacts_button, navbar_settings_button, navbar_home_button;
    ListView courses_listview;
    ArrayList<String> user_courses;
    ArrayAdapter<String> adapter;

    // Initialize Firebase Authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Define currentUser as the currently authenticated user
    FirebaseUser currentUser = mAuth.getCurrentUser();
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");

    // Init strings to hold database values
    private String uid;
    private String username;
    private String dob;
    private String email;
    private String course1;
    private String course2;
    private String course3;
    private String course4;

    // Handles all navbar buttons, and updates the user data from the database.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        logout_button = findViewById(R.id.home_logout_button);
        navbar_contacts_button = findViewById(R.id.navbar_contacts_button);
        navbar_home_button = findViewById(R.id.navbar_home_button);
        navbar_settings_button = findViewById(R.id.navbar_settings_button);
        navbar_search_button = findViewById(R.id.navbar_search_button);
        courses_listview = findViewById(R.id.profile_courses);
        user_courses = new ArrayList<>();

        // Logout button
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If the user clicks 'log out' log them out and go to the login page.
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), login_page.class);
                startActivity(intent);
                finish();
            }
        });

        // Button to take to contacts page
        navbar_contacts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), contacts_page.class);
                startActivity(intent);
                finish();
            }
        });

        // Button to take to home page
        navbar_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), home_page.class);
                startActivity(intent);
                finish();
            }
        });

        // Button to take to settings page
        navbar_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), settings_page.class);
                startActivity(intent);
                finish();
            }
        });
        // Button to take to search page
        navbar_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), searchCoursesPage.class);
                startActivity(intent);
                finish();
            }
        });

        // Update profile page data for Username, DOB, email, UID, from the database.
        if (currentUser != null) {
            String userUID = currentUser.getUid();

            // Reference to the user's details in the database
            DatabaseReference userDetailRef = userReference.child(userUID).child("Details");

            userDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        username = dataSnapshot.child("username").getValue(String.class);
                        email = dataSnapshot.child("email").getValue(String.class);
                        uid = dataSnapshot.child("userID").getValue(String.class);
                        dob = dataSnapshot.child("DOB").getValue(String.class);

                        // Update the info on the profile page to the data from the database
                        TextView usernameTextView = findViewById(R.id.profile_username);
                        usernameTextView.setText(username);
                        TextView emailTextView = findViewById(R.id.profile_email);
                        emailTextView.setText(email);
                        TextView uidTextView = findViewById(R.id.profile_uid);
                        uidTextView.setText(uid);
                        TextView dobTextView = findViewById(R.id.profile_dob);
                        dobTextView.setText(dob);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", "onCancelled method");
                }
            });

            // Updates Courses from database
            DatabaseReference userCoursesRef = userReference.child(userUID).child("Courses");
            userCoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        course1 = dataSnapshot.child("0").getValue(String.class);
                        course2 = dataSnapshot.child("1").getValue(String.class);
                        course3 = dataSnapshot.child("2").getValue(String.class);
                        course4 = dataSnapshot.child("3").getValue(String.class);

                        // Data to add to listview
                        user_courses.add(course1);
                        user_courses.add(course2);
                        user_courses.add(course3);
                        user_courses.add(course4);

                        //Initialise adapter for listview
                        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, user_courses);
                        courses_listview.setAdapter(adapter);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", "onCancelled method");
                }
            });
        }

    }
}

