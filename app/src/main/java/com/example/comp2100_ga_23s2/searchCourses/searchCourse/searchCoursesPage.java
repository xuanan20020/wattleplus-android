package com.example.comp2100_ga_23s2.searchCourses.searchCourse;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.home_page;
import com.example.comp2100_ga_23s2.p2p_messaging.contacts.contacts_page;
import com.example.comp2100_ga_23s2.profile_page;
import com.example.comp2100_ga_23s2.settings_page;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harry Randall u7499609
 * This page gets all of the courses and displays them
 * to the user.
 */
public class searchCoursesPage extends AppCompatActivity {
    Button navbar_home_button, navbar_contacts_button, navbar_settings_button;
    ImageButton pfp_button;
    int[] courseImages = {R.drawable.course_image, R.drawable.course_image2, R.drawable.course_image3,
            R.drawable.course_image4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        navbar_home_button = findViewById(R.id.navbar_home_button);
        navbar_contacts_button = findViewById(R.id.navbar_contacts_button);
        navbar_settings_button = findViewById(R.id.navbar_settings_button);
        pfp_button = findViewById(R.id.profile_image_button);
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Courses");

        RecyclerView recyclerView = findViewById(R.id.searchRecyclerView);
        List<coursesClass> courses = new ArrayList<coursesClass>();

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<coursesClass> courses = new ArrayList<coursesClass>();

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String courseCode = userSnapshot.getKey();
                    String courseName = String.valueOf(userSnapshot.child("name").getValue());
                    String courseUnits = "units:" + String.valueOf(userSnapshot.child("unit").getValue());
                    int courseCode2 = Integer.parseInt(courseCode.substring(courseCode.length() - 4));
                    courseCode2 = courseCode2 % 4;
                    Log.d("image", String.valueOf(courseImages[courseCode2]));
                    courses.add(new coursesClass(courseCode, courseName, courseUnits, courseImages[courseCode2]));
                }

                RecyclerView recyclerView = findViewById(R.id.searchRecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(new searchPageAdapter(getApplicationContext(), courses));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });


        // Navbar handler
        navbar_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), home_page.class);
                startActivity(intent);
                finish();
            }
        });

        // Contacts button handler
        navbar_contacts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), contacts_page.class);
                startActivity(intent);
                finish();
            }
        });

        // Settings button handler
        navbar_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), settings_page.class);
                startActivity(intent);
                finish();
            }
        });

        // Pfp button handler
        pfp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), profile_page.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
