package com.example.comp2100_ga_23s2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.comp2100_ga_23s2.p2p_messaging.contacts.contacts_page;
import com.example.comp2100_ga_23s2.searchCourses.searchCourse.searchCoursesPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for the settings page.
 * @author Douglas Carroll - u7483922
 */
public class settings_page extends AppCompatActivity {
    // Initialise UI elements
    Button navbar_search_button, navbar_contacts_button, navbar_home_button, systemSettings, save_changes_button;
    Switch DOB_button;
    EditText uid_edit, username_edit, email_edit;
    ImageButton pfp_button;

    // Blacklist
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Define currentUser as the currently authenticated user
    FirebaseUser currentUser = mAuth.getCurrentUser();
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");

    // Handler for the GUI, allows updating profile, can block a user.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        navbar_contacts_button = findViewById(R.id.navbar_contacts_button);
        pfp_button = findViewById(R.id.profile_image_button);
        navbar_search_button = findViewById(R.id.navbar_search_button);
        navbar_home_button = findViewById(R.id.navbar_home_button);
        systemSettings = findViewById(R.id.systemSettingButton);
        save_changes_button = findViewById(R.id.save_changes_button);
        DOB_button = findViewById(R.id.DOB_button);
        uid_edit = findViewById(R.id.UID_editText);
        username_edit = findViewById(R.id.username_editText);
        email_edit = findViewById(R.id.email_editText);

        // First lets' get the blacklist from the database


        // Save changes handler. updates database and blacklist
        save_changes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userUID = currentUser.getUid();
                // Get UID from blacklist plaintext
                String uidInput = uid_edit.getText().toString();
                if (!uidInput.isEmpty()) {
                    // Add user's details
                    if (uidInput.length() != 8 || !uidInput.substring(1).matches("\\d{7}") || uidInput.charAt(0) != 'u') {
                        Toast.makeText(settings_page.this, "Please make sure the follow the UID format: [u1234567]", Toast.LENGTH_SHORT).show();
                    } else {
                        uidInput = uidInput.substring(1);
                        DatabaseReference userDetails = userReference.child(userUID).child("Details");
                        Map<String, Object> userDetailsMap = new HashMap<>();
                        userDetailsMap.put("userID", uidInput);
                        userDetails.updateChildren(userDetailsMap);
                        uid_edit.getText().clear();
                        uid_edit.setHint("Enter new UID");
                    }
                }

                // change username
                String usernameInput = username_edit.getText().toString().replaceAll("\\s", "");
                if (!usernameInput.isEmpty()) {
                    // Add user's details
                    if (usernameInput.length() <= 3) {
                        Toast.makeText(settings_page.this, "Please make sure the username length is >3 characters", Toast.LENGTH_SHORT).show();
                    }else{
                        DatabaseReference userDetails = userReference.child(userUID).child("Details");
                        Map<String, Object> userDetailsMap = new HashMap<>();
                        userDetailsMap.put("username", usernameInput);
                        userDetails.updateChildren(userDetailsMap);
                        // Clear and reset the EditText
                        username_edit.getText().clear();
                        username_edit.setHint("Enter new username");
                    }
                }

                // change email
                String emailInput = email_edit.getText().toString();
                if (!emailInput.isEmpty()) {
                    if (!emailInput.contains("@")){
                        Toast.makeText(settings_page.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    }else{
                        // Add user's details
                        DatabaseReference userDetails = userReference.child(userUID).child("Details");
                        Map<String, Object> userDetailsMap = new HashMap<>();
                        userDetailsMap.put("email", emailInput);
                        userDetails.updateChildren(userDetailsMap);
                        // Clear and reset the EditText
                        email_edit.getText().clear();
                        email_edit.setHint("Enter new email");
                    }
                }
            }
        });
        // Show date of birth switch handler
        DOB_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Handler for contacts button
        navbar_contacts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), contacts_page.class);
                startActivity(intent);
                finish();
            }
        });

        // Handler for Pfp button
        pfp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), profile_page.class);
                startActivity(intent);
                finish();
            }
        });

        // Handler for home button
        navbar_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), home_page.class);
                startActivity(intent);
                finish();
            }
        });

        // Handler for search button
        navbar_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), searchCoursesPage.class);
                startActivity(intent);
                finish();
            }
        });

        // System settings button
        systemSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
            }
        });
    }
}