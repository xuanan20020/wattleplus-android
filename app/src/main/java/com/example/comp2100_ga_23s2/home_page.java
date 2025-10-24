package com.example.comp2100_ga_23s2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.comp2100_ga_23s2.authentication.login_page;
import com.example.comp2100_ga_23s2.dataStreamSimulator.UserActionService;
import com.example.comp2100_ga_23s2.notification.NotificationService;
import com.example.comp2100_ga_23s2.p2p_messaging.contacts.contacts_page;
import com.example.comp2100_ga_23s2.searchCourses.searchCourse.searchCoursesPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Home page class.
 * @author Douglas Carroll
 */
public class home_page extends AppCompatActivity {
    Button logout_button, navbar_search_button, navbar_contacts_button, navbar_settings_button;
    ImageButton pfp_button;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");

    int courseImages[] = {R.drawable.course_image, R.drawable.course_image2, R.drawable.course_image3,
            R.drawable.course_image4};

    ListView listView;


    /**
     * Create a notification channel for the notifications, required by Android
     * @author Gia Minh Nguyen - u7556893
     */
    private void createNotificationChannel() {
        CharSequence name = "WattleNotification"; // The user-visible name of the channel.
        String description = "Database listener notification"; // The user-visible description of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("wn1", name, importance);
        channel.setDescription(description);
        channel.setShowBadge(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        // Register the channel with the system;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


    /**
     * Method to check if a service is running
     * @param serviceClass
     * @return true if the service is running
     * @author Gia Minh Nguyen - u7556893
     */
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Handles UI for the home page, including button actions and listView for courses.
     * @author Douglas Carroll - u7483922
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        navbar_contacts_button = findViewById(R.id.navbar_contacts_button);
        pfp_button = findViewById(R.id.profile_image_button);
        navbar_settings_button = findViewById(R.id.navbar_settings_button);
        navbar_search_button = findViewById(R.id.navbar_search_button);

        // Create notification channel
        createNotificationChannel();

        // check notification permission
        if (!shouldShowRequestPermissionRationale("112")){
            try {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 112);
            }catch (Exception e){
                Toast.makeText(this, "failed to request for notification permission", Toast.LENGTH_SHORT).show();
            }
        }

        // If the user is not logged in, they have to go to the login screen! IMPORTANT!
        if (currentUser == null) {
            Intent intent = new Intent(getApplicationContext(), login_page.class);
            startActivity(intent);
            finish();
        }
        navbar_contacts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), contacts_page.class);
                startActivity(intent);
                finish();
            }
        });

        pfp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), profile_page.class);
                startActivity(intent);
                finish();
            }
        });

        navbar_settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), settings_page.class);
                startActivity(intent);
                finish();
            }
        });
        navbar_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), searchCoursesPage.class);
                startActivity(intent);
                finish();
            }
        });

        if (currentUser != null) {
            String userUID = currentUser.getUid();

            DatabaseReference userCoursesRef = userReference.child(userUID).child("Courses");
            userCoursesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String course1 = dataSnapshot.child("0").getValue(String.class);
                        String course2 = dataSnapshot.child("1").getValue(String.class);
                        String course3 = dataSnapshot.child("2").getValue(String.class);
                        String course4 = dataSnapshot.child("3").getValue(String.class);

                        String courseList[] = {course1, course2, course3, course4};

                        listView = (ListView) findViewById(R.id.customListView);
                        CustomBaseAdapter customBaseAdapter = new CustomBaseAdapter(getApplicationContext(), courseList, courseImages);
                        listView.setAdapter(customBaseAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", "onCancelled method");
                }
            });
        }


        // start the notification service if it hasn't already been running
        if(!isServiceRunning(NotificationService.class)) {
            Intent intent = new Intent(this, NotificationService.class);
            startService(intent);
        }


        // start the notification service if it hasn't already been running
        if(!isServiceRunning(UserActionService.class)) {
            Intent intent = new Intent(this, UserActionService.class);
            startService(intent);
        }
    }
}
