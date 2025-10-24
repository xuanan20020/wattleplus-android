package com.example.comp2100_ga_23s2.dataSampling.dataSampler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.objects.Assessment;
import com.example.comp2100_ga_23s2.objects.Course;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to generate sample course attributes for the database
 * @author Gia Minh Nguyen - u7556893
 */
public class CourseSampler extends AppCompatActivity implements Sampler{
    private ArrayList<Course> courses = new ArrayList<>();  // List of courses for sampling


    /**
     * Add courses and their details from text file "Courses" to be included in the sample.
     * @author Gia Minh Nguyen - u7556893
     */
    public void addCourses() {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.courses)));

            String line;

            while((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                courses.add(new Course(tokens[0].trim(), tokens[1].trim()));
            }

        } catch (IOException e){
            Toast.makeText(this, "data load exception", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Upload the courses info from raw resource to the realtime database
     * @author Gia Minh Nguyen - u7556893
     */
    private void writeToFirebase() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference coursesArray = db.getReference("Courses");

        for(Course c: courses) {
            DatabaseReference courseRef = coursesArray.child(c.getCourseCode());

            // write the assignments of the course
            DatabaseReference asRef = courseRef.child("Assessments");
            List<Map<String, Object>> asList = new ArrayList<>();
            for(Assessment a : c.getAssessments()) {
                Map<String, Object> assessment = new HashMap<>();
                assessment.put(a.getName(), a.getWeight());
                asRef.updateChildren(assessment);
            }

            // write name of the course
            Map<String, Object> details = new HashMap<>();
            details.put("name", c.getName());
            details.put("unit", c.getUnit());
            courseRef.updateChildren(details);
        }
    }


    /**
     * method to write added courses into a json file
     * @author Gia Minh Nguyen - u7556893
     */
    public void sampling() throws IOException {
        addCourses();
        writeToFirebase();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            sampling();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Switch to user sampler to sample users
        Intent intent = new Intent(getApplicationContext(), UserSampler.class);
        startActivity(intent);
    }
}
