package com.example.comp2100_ga_23s2.searchCourses;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.authentication.login_page;
import com.example.comp2100_ga_23s2.searchCourses.searchCourse.searchCoursesPage;
import com.google.firebase.auth.FirebaseAuth;


/**
 * @author Harry Randall u7499609, Douglas Carroll - u7483922
 * This class displays the course to the user.
 */
public class displayCourse extends AppCompatActivity {
    ImageButton back_button;
    TextView courseNameHolder, courseNamePlaceholder, courseCodePlaceholder, unitValuePlaceholder;
    ImageView courseImageID;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_course);
        courseNamePlaceholder = findViewById(R.id.courseNamePlaceholder);
        courseNameHolder = findViewById(R.id.courseNameHolder);
        courseCodePlaceholder = findViewById(R.id.courseCodePlaceholder);
        unitValuePlaceholder = findViewById(R.id.unitValuePlaceholder);
        courseImageID = findViewById(R.id.courseImageID);
        String courseCode = getIntent().getStringExtra("courseCode");
        String courseName = getIntent().getStringExtra("courseName");
        String courseUnits = getIntent().getStringExtra("courseUnits");

        courseNamePlaceholder.setText(courseName);
        courseNameHolder.setText(courseName);
        courseCodePlaceholder.setText(courseCode);
        unitValuePlaceholder.setText(courseUnits);
        back_button = findViewById(R.id.back_btn);

        // back button handler
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), searchCoursesPage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}