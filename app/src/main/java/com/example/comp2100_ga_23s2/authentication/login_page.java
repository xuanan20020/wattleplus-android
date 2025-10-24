package com.example.comp2100_ga_23s2.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.authentication.signUp.signup_page;
import com.example.comp2100_ga_23s2.dataSampling.dataSampler.CourseSampler;
import com.example.comp2100_ga_23s2.home_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author Harry Randall u7499609
 * This page will authenticate the user. It will Fairly simple function
 * that provides toast messages for error handling so the user knows
 * why their username / password combo was incorrect.
 */
public class login_page extends AppCompatActivity {
    private static final String TAG = "LoginPage";
//   All IDS
    Button submitButton, cancelButton, createButton;
    EditText emailText, passwordText;
    FirebaseAuth mAuth;
    ImageView iv;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
//            reload();
            Log.d(TAG, "user is already logged in");
        }
    }

    private View.OnClickListener devUploadSampleData = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), CourseSampler.class);
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mAuth = FirebaseAuth.getInstance();

        // ### START OF DEV OPTIONS ###
        // Dev option for data sampling (not for normal users)
        iv = findViewById(R.id.imageView);
        iv.setOnClickListener(devUploadSampleData);
        // ### END OF DEV OPTIONS ###

//      Defining all of the views.
        submitButton = findViewById(R.id.login_submit_button);
        cancelButton = findViewById(R.id.login_cancel_button);
        createButton = findViewById(R.id.login_signup_button);
        emailText = findViewById(R.id.loginpage_username_field);
        passwordText = findViewById(R.id.loginpage_password_field);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String emailText = extras.getString("username");
            this.emailText.setText(emailText);
        }
        submitButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//          Check if the user has entered in a username or password, if not give a Toast message.
            if (TextUtils.isEmpty(emailText.getText())) {
                Toast.makeText(login_page.this, "Please enter in a username", Toast.LENGTH_SHORT).show();
                return;
            } else if (TextUtils.isEmpty(passwordText.getText())) {
                Toast.makeText(login_page.this, "Please enter in a password", Toast.LENGTH_SHORT).show();
                return;
            }

//          Login auth from the firebase docs.
        mAuth.signInWithEmailAndPassword(String.valueOf(emailText.getText()), String.valueOf(passwordText.getText()))
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(login_page.this, "Authentication success!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), home_page.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(login_page.this, "Authentication failed",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "You have clicked CANCEL" + emailText.getText() + passwordText.getText());
                Intent intent = new Intent(getApplicationContext(), signup_page.class);
                startActivity(intent);
                finish();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "You have clicked SignUp" + emailText.getText() + passwordText.getText());
                Intent intent = new Intent(getApplicationContext(), signup_page.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
