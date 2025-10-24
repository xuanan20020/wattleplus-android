package com.example.comp2100_ga_23s2.authentication.signUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.authentication.login_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * @author Harry Randall u7499609
 * This is the sign up page. This is the first of two pages. This page does the following
 * 1. Checks to see if the user already has an account. If they do, they get redirected
 * to the login page with their email as the placeholder so they don't need to re-enter it.
 * 2. Checks to see if the password is valid. They then get redirected.
 *
 * All GUI / UI features completed by Harry Randall u7499609
 */
public class signup_page extends AppCompatActivity {
    private static final String TAG = "LoginPage";
//   All IDS
    Button submitButton, cancelButton, signInButton;
    EditText emailText, passwordText, confirmPasswordText;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);
        mAuth = FirebaseAuth.getInstance();
//      Defining all of the views.
        submitButton = findViewById(R.id.signupPage_submit_button);
        cancelButton = findViewById(R.id.signupPage_cancel_button);
        signInButton = findViewById(R.id.signupPage_signup_button);
        emailText = findViewById(R.id.signupPage_email_field);
        passwordText = findViewById(R.id.signupPage_password_field);
        confirmPasswordText = findViewById(R.id.signupPage_confirm_password_field);
        submitButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//          Check if the user has entered in a username, password, a different password and (need to add) the wrong email if not give a Toast message.
            if (emailText.getText() == null || emailText.getText().toString().isEmpty()) {
                Toast.makeText(signup_page.this, "Please enter in a valid email address", Toast.LENGTH_SHORT).show();
                return;
            } else if (passwordText.getText() == null || passwordText.getText().toString().isEmpty()) {
                Toast.makeText(signup_page.this, "Please enter in a password", Toast.LENGTH_SHORT).show();
                return;
            } else if (confirmPasswordText.getText() == null || confirmPasswordText.getText().toString().isEmpty()) {
                Toast.makeText(signup_page.this, "Please enter in a password", Toast.LENGTH_SHORT).show();
                return;
            } else if (!confirmPasswordText.getText().toString().equals(passwordText.getText().toString())) {
                Toast.makeText(signup_page.this, "The passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            checkIfUserExists(emailText.getText().toString(), new UsernameCheckCallback() {
                @Override
                public void onUsernameCheckResult(boolean usernameExists) {
                    if (usernameExists){
                        Toast.makeText(signup_page.this, "An account with the email (" + emailText.getText().toString() +
                                ") already exists. Try to log in!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), login_page.class);
                        intent.putExtra("email", emailText.getText().toString());
                        startActivity(intent);
                    }
                    else{
                        //          Will be plain TEXT when sending between activities.
                        Intent intent = new Intent(getApplicationContext(), signup_page_2.class);
                        intent.putExtra("email", emailText.getText().toString());
                        intent.putExtra("password", confirmPasswordText.getText().toString());
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    });
    //      Cancel button
    cancelButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "You have clicked CANCEL" + emailText.getText() + passwordText.getText());
            Intent intent = new Intent(getApplicationContext(), login_page.class);
            startActivity(intent);
            finish();
        }
    });

    signInButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "You have clicked LogIn" + emailText.getText() + passwordText.getText());
            Intent intent = new Intent(getApplicationContext(), login_page.class);
            startActivity(intent);
            finish();
        }
    });
}

    /**
     * @author Harry Randall u7499609
     * The following code will check if a user exists within the firebase database.
     * If they do, they will be redirected.
     */
public interface UsernameCheckCallback {
    void onUsernameCheckResult(boolean usernameExists);
}

public void checkIfUserExists(String email, final UsernameCheckCallback callback) {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        boolean usernameExists = false;
        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
            DataSnapshot userDetailsSnapshot = userSnapshot.child("Details");
            if (userDetailsSnapshot.exists()) {
                String dbUsername = userDetailsSnapshot.child("email").getValue(String.class);
                if (dbUsername != null && dbUsername.equals(email)) {
                    usernameExists = true;
                    break;
                }
            }
        }
        callback.onUsernameCheckResult(usernameExists);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Log.d("Error", "onCancelled method");
    }
});
}
}