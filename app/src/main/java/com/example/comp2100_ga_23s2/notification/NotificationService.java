package com.example.comp2100_ga_23s2.notification;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.comp2100_ga_23s2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

/**
 * A background service listening for data changes on the realtime database and notify
 * users (Basic requirement 3)
 * @author Gia Minh Nguyen - u7556893
 */
public class NotificationService extends Service {
    private DatabaseReference dataRef;

    /**
     * This method show a custom notification, it can be used by client (other activities)
     * to create a notification by creating an intent with string extra "notificationMessage"
     * and the startService(intent)
     * @author Gia Minh Nguyen - u7556893
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("notificationMessage")) {
            String message = intent.getStringExtra("notificationMessage");
            sendNotification(message, 3);
        }

        return START_NOT_STICKY;
    }


    /**
     * A method to bind intent with the service, however is not used, hence set to null
     * @return null
     * @author Gia Minh Nguyen - u7556893
     */
    @Override
    public IBinder onBind(Intent intent) {
        String message = intent.getStringExtra("notificationMessage");
        sendNotification(message, 3);
        return null;
    }


    /**
     * Build a notification and send it to the user.
     * @param message
     * @author Gia Minh Nguyen - u7556893
     */
    public void sendNotification(String message, int notiID) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "wn1")
                .setSmallIcon(R.drawable.wattle_logo)
                .setContentTitle("Database updated")
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please enable notification for the app", Toast.LENGTH_SHORT).show();
        }
        notificationManager.notify(notiID, builder.build());

        // Add delay so the system won't spam notification
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
        }, 400); // Delay for 0.4s
    }


    /**
     * Active listener for changes in the database and send an appropriate notification
     * @author Gia Minh Nguyen - u7556893
     */
    private ChildEventListener listenForUserChanges = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            sendNotification("An attribute has been added", 1);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            sendNotification("An attribute has been changed", 1);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            sendNotification("an attribute has been removed", 1);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            sendNotification("an attribute has been moved", 1);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(NotificationService.this, "data read error", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * Active listener for new messages sent and send a notification accordingly.
     * Only the 2 users participate in the conversation will receive this notification.
     * @author Gia Minh Nguyen - u7556893
     */
    private ChildEventListener listenForNewMessage = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            sendNotification("A new message has been sent", 2);

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            sendNotification("A new message has been sent", 2);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(NotificationService.this, "data read error", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onCreate() {
        dataRef = FirebaseDatabase.getInstance().getReference("Users");

        // attach listener for general data change on the db
        dataRef.addChildEventListener(listenForUserChanges);

        // attach listener for new messages
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            String currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            dataRef.child(currentUserID).child("Messages").addChildEventListener(listenForNewMessage);
        }

    }


    /**
     * Deallocate listener on service stop to prevent memory leak
     * @author Gia Minh Nguyen - u7556893
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dataRef != null && listenForUserChanges != null) {
            dataRef.removeEventListener(listenForUserChanges);
        }
        if (dataRef != null && listenForNewMessage != null) {
            dataRef.removeEventListener(listenForNewMessage);
        }
    }
}