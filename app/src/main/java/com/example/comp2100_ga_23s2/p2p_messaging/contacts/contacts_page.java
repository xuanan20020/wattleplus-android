package com.example.comp2100_ga_23s2.p2p_messaging.contacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.home_page;
import com.example.comp2100_ga_23s2.objects.AVLTree;
import com.example.comp2100_ga_23s2.objects.Course;
import com.example.comp2100_ga_23s2.objects.User;
import com.example.comp2100_ga_23s2.profile_page;
import com.example.comp2100_ga_23s2.searchCourses.searchCourse.searchCoursesPage;
import com.example.comp2100_ga_23s2.searchEngine.SearchEngine;
import com.example.comp2100_ga_23s2.searchEngine.UserQuery;
import com.example.comp2100_ga_23s2.searchEngine.UserQueryParser;
import com.example.comp2100_ga_23s2.searchEngine.UserQueryToken;
import com.example.comp2100_ga_23s2.searchEngine.UserQueryTokenizer;
import com.example.comp2100_ga_23s2.settings_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contacts page class. Allows users to search for other users on the database in order to message
 * them.
 * @author Harry Randall, Douglas Carroll - u7483922
 */
public class contacts_page extends AppCompatActivity {
    RecyclerView contacts_recycler_view;
    int recycleLimit = 100;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    EditText searchText;
    ImageButton searchButton;

    // Navbar section, won't have an input for the contacts button
    Button navbar_home_button, navbar_search_button, navbar_settings_button, navbar_contacts_button;
    ImageButton pfp_button;
    ArrayList<String> blockedlist;
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
    FirebaseUser currentUser = mAuth.getCurrentUser();

    // User Data Structure
    AVLTree<User> avu = new AVLTree<>();

    /**
     * Handles UI for contacts page, such as button handling. Retrieves blacklist from database.
     * @author Douglas Carroll - u7483922, Harry Randall
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);
        navbar_home_button = findViewById(R.id.navbar_home_button);
        navbar_contacts_button = findViewById(R.id.navbar_contacts_button);
        searchText = findViewById(R.id.search_text);
        searchButton = findViewById(R.id.search_button);
        pfp_button = findViewById(R.id.profile_image_button);
        navbar_settings_button = findViewById(R.id.navbar_settings_button);
        navbar_search_button = findViewById(R.id.navbar_search_button);
        blockedlist = new ArrayList<>();

        // First lets' get the blacklist from the database
        if (currentUser != null) {
            String userUID = currentUser.getUid();

            DatabaseReference blacklistRef = userReference.child(userUID).child("Settings").child("BlackList");
            blacklistRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        long blacklistSize = dataSnapshot.getChildrenCount();
                        for (int i = 0; i < blacklistSize; i++) {
                            String blockedUser = dataSnapshot.child(String.valueOf(i)).getValue(String.class);
                            blockedlist.add(blockedUser);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", "onCancelled method");
                }
            });
        }

        // RecyclerView
        contacts_recycler_view = findViewById(R.id.contacts_recycler_view);

        // Load data to Tree then update Contacts
        updateAVLTree();
        refreshContacts();

        navbar_contacts_button.setOnClickListener(view -> refreshContacts());

        searchButton.setOnClickListener(view -> {
            String searchQuery = searchText.getText().toString();
            searchForUser(searchQuery);
        });

//      Navbar button handling.
        navbar_home_button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), home_page.class);
            startActivity(intent);
            finish();
        });

        navbar_home_button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), home_page.class);
            startActivity(intent);
            finish();
        });

        pfp_button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), profile_page.class);
            startActivity(intent);
            finish();
        });

        navbar_settings_button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), settings_page.class);
            startActivity(intent);
            finish();
        });
        navbar_search_button.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), searchCoursesPage.class);
            startActivity(intent);
            finish();
        });

    }

    private void updateAVLTree(){
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot userDetailsSnapshot = userSnapshot.child("Details");
                    DataSnapshot userCoursesSnapshot = userSnapshot.child("Courses");
                    if (userDetailsSnapshot.exists() && userCoursesSnapshot.exists()) {
                        if (Objects.equals(userSnapshot.getKey(), currentUser.getUid())) {
                            continue;
                        }
                        // Details
                        String dbUsername = userDetailsSnapshot.child("username").getValue(String.class);
                        String dbEmail = userDetailsSnapshot.child("email").getValue(String.class);
                        String dbTopLevelUserId = userSnapshot.getKey();
                        String dbUserID = userDetailsSnapshot.child("userID").getValue(String.class);
                        assert dbUserID != null;
                        int uid = Integer.parseInt(dbUserID);
                        // Courses
                        ArrayList<Course> userCourses = new ArrayList<>();
                        for (DataSnapshot courseSnapshot : userCoursesSnapshot.getChildren()) {
                            String courseCode = courseSnapshot.getValue(String.class);
                            Course course = new Course(courseCode, "");
                            userCourses.add(course);
                        }
                        // Create a User object and add it to the AVL tree
                        User user = new User(dbUsername, dbEmail, uid, dbTopLevelUserId);
                        user.setCurrentCourses(userCourses); // Assuming User has a method to set courses
                        avu = avu.insert(user);
                    }
                }
            }
            // Error handling.
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", "onCancelled method");
            }
        });
    }

    /**
     * Refreshes the contacts page whenever the database is updated. If user is blocked, they will
     * not appear.
     * @author Harry Randall, Douglas Carroll - u7483922, Xuan An
     */
    private void refreshContacts() {
        // Will still have to wrap around this to listen for remote changes -> userReference
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Create a list to store contacts from the AVL tree (avu)
                List<contacts_item_class> items = new ArrayList<>();

                // Iterate through the nodes in the AVL tree (avu)
                for (User user : avu.inOrderLimit(recycleLimit)) { // Assuming inOrderTraversal returns users in some order
                    String dbUserID = "u" + user.uid; // Get user ID from User object
                    // Create contacts_item_class objects and add them to the items list
                    if (!(blockedlist.contains(dbUserID))) {
                        items.add(new contacts_item_class(user, R.drawable.contacts_image));
                    }
                }
                // Display the recyclerView.
                updateRecyclerView(items);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Error", "onCancelled method");
            }
        });
    }

    /**
     * Searches for users based on the provided query string and updates the RecyclerView
     * with the search results.
     * @author Xuan An
     * @param query The input query string to search users.
     */
    private void searchForUser(String query) {

        // Return early if the query string is empty.
        if (query.equals("")) {
            return;
        }

        // List to store contact items for the RecyclerView.
        List<contacts_item_class> items = new ArrayList<>();

        try {
            // Tokenize the input query string.
            List<UserQueryToken> tokens = UserQueryTokenizer.tokenize(query);

            // Parse the tokenized input to create a structured user query.
            UserQueryParser parser = new UserQueryParser(tokens);
            UserQuery userQuery = parser.parse();

            // Search for users matching the query.
            SearchEngine searchEngine = new SearchEngine();
            List<User> users = searchEngine.findUserInTree(userQuery, avu);

            // Process each user result.
            for (User user : users) {
                String dbUserID = "u" + user.uid;

                // Add the user to the items list if not blocked.
                if (!blockedlist.contains(dbUserID)) {
                    items.add(new contacts_item_class(user, R.drawable.contacts_image));
                }
            }

            // Update the RecyclerView with the prepared list of items.
            updateRecyclerView(items);

        } catch (UserQueryToken.IllegalTokenException e) {
            // Handle tokenization errors and notify the user.
            Toast.makeText(contacts_page.this, "Please Check Token Formatting", Toast.LENGTH_SHORT).show();

        } catch (RuntimeException e) {
            // Handle parsing errors and notify the user.
            Toast.makeText(contacts_page.this, "Invalid Expression", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates the RecyclerView with the provided list of contact items.
     *
     * @param items The list of contact items to display in the RecyclerView.
     * @author Xuan An
     */
    private void updateRecyclerView(List<contacts_item_class> items) {
        // Set up the RecyclerView with a linear layout.
        contacts_recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        // Attach the data using the custom adapter.
        contacts_recycler_view.setAdapter(new contacts_adapter(getApplicationContext(), items));
    }

}
