package com.example.comp2100_ga_23s2.dataStreamSimulator;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.comp2100_ga_23s2.notification.NotificationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A service to simulate other users' interaction on the app, which updates the database
 * User will receive notification upon database updates.
 * @author Gia Minh Nguyen - u7556893
 */
public class UserActionService extends Service {
    private Handler handler;
    private Random random;

    private int uidStart;
    private int currentID;

    private long delay1;
    private long delay2;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * This function will add the current authenticated user to the database.
     * Adapted from the method with same name in sign_up_2
     * parameters:
     * @param DOB
     * @param userID
     * @param username
     * It will also fetch the useremail but it can do that within.
     * @author Harry Randall, Douglas Carroll, Gia Minh Nguyen
     * @date 13/10/2023
     */
    public void add_user_to_db(String username, String userID, String DOB) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userClass = database.getReference("Users");
        String UID = userID;
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
        userDetailsMap.put("email", "u" + userID);
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


    /**
     * Simulate deleting a user account on the database
     * @param uid of the user being removed
     * @author Gia Minh Nguyen
     */
    public void removeUserFromDatabase(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference uRef = userRef.child("u" + uid);
        uRef.removeValue();
    }


    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        random = new Random();
        uidStart = 7510000;
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        simulateUserActions();
        return START_NOT_STICKY;
    }


    /**
     * Simulate database changes regularly with some delay in between
     * @author Gia Minh Nguyen - u7556893
     */
    private void simulateUserActions() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentID = random.nextInt(10000) + uidStart;
                // add a dummy user to db
                add_user_to_db("streamTest", ("u" + currentID), "12-12-2004");
                Handler handler = new Handler(Looper.getMainLooper());

                // Random delay to make the actions feel more natural
                delay1 = random.nextInt(30000) + 25000;
                delay2 = random.nextInt(30000) + 25000;

                // remove the user after some delay to ensure db does not expand indefinitely.
                handler.postDelayed(() -> {
                    removeUserFromDatabase(String.valueOf(currentID));
                    // Call simulateUserActions again to continue the loop
                    simulateUserActions();}, delay1);
            }}, delay2);  // Generate a new user every 30-60 seconds for demonstration.
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
