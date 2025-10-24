package com.example.comp2100_ga_23s2.dataSampling.dataSampler;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.authentication.login_page;
import com.example.comp2100_ga_23s2.objects.Course;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Implements method to sample dummy users and add them to the database.
 * @author Gia Minh Nguyen - u7556893, Douglas Carroll - u7483922
 */
public class UserSampler extends AppCompatActivity implements Sampler {
    private ArrayList<String> maleFirstList = new ArrayList<>();
    private ArrayList<String> femaleFirstList = new ArrayList<>();
    private ArrayList<String> lastNamesList = new ArrayList<>();
    private ArrayList<Course> courses = new ArrayList<>();
    /**
     * Get name lists from txt file to randomize names
     * @throws IOException
     * @author Gia Minh Nguyen - u7556893
     */
    private void readNamesFromFile() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.names)));
        String name = br.readLine();  // skip first line
        while(!(name = br.readLine()).equals("###Female###")) {
            maleFirstList.add(name);
        }
        while(!(name = br.readLine()).equals("###LastNames###")) {
            femaleFirstList.add(name);
        }
        while((name = br.readLine()) != null) {
            lastNamesList.add(name);
        }
    }


    /** Randomize a human name by composing a random first name and a random last name
     * @param firstName
     * @param lastName
     * @return a random full name
     * @author Gia Minh Nguyen - u7556893
     */
    private String composeName(ArrayList<String> firstName, ArrayList<String> lastName) {
        Random random = new Random();
        return (firstName.get(random.nextInt(firstName.size())) + " " + lastName.get(random.nextInt(lastName.size())));
    }


    /**
     * Randomly make a valid dob
     * @return a random dob in a reasonable time frame
     * @author Gia Minh Nguyen - u7556893
     */
    private String makeDob() {
        Random r = new Random();
        return r.nextInt(29) + "-" + (r.nextInt(12) + 1) + "-" + (r.nextInt(7) + 1998);
    }


    /**
     * Randomly create a valid date and time composition
     * @return A string showing "date | time"
     * @author Gia Minh Nguyen - u7556893, Douglas Carroll - u7483922
     */
    private String makeDate() {
        Random r = new Random();
        String date = r.nextInt(29) + "-" + (r.nextInt(9) + 1) + "-" + (2023 - r.nextInt(7));
        String time = r.nextInt(25) + ":" + r.nextInt(61) + ":" + r.nextInt(61);
        return date + " | " + time;
    }


    /**
     * Method to add a user to realtime database
     * @param ggID Firebase authentication uid
     * @param un username
     * @param uid student uid
     * @param email student email
     * @param contactList list of emails that are in the user's current contact
     * @author Gia Minh Nguyen - u7556893
     */
    private void addUser(String ggID, String un, String uid, String email, List<String> contactList) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference usersArray = db.getReference("Users");
        DatabaseReference userRef = usersArray.child(ggID);
        Random ran = new Random();

        // Add user's details
        DatabaseReference userDetails = userRef.child("Details");
        Map<String, Object> details = new HashMap<>();
        details.put("username", un);
        details.put("userID", uid);
        details.put("email", email);
        details.put("DOB", makeDob());
        details.put("created", makeDate());
        userDetails.updateChildren(details);

        // add user's contacts
        DatabaseReference contacts = userRef.child("Contacts");
        contacts.setValue(contactList);

        // add user's courses
        DatabaseReference dbCourses = userRef.child("Courses");
        List<Course> temp = (List<Course>) courses.clone();
        List<String> courses = new ArrayList<>();
        for(int k = 0; k < 4; k++) {
            Course tmpCourse = temp.get(ran.nextInt(temp.size()));
            courses.add(tmpCourse.getCourseCode());
            temp.remove(tmpCourse);
        }
        dbCourses.setValue(courses);

        // add privacy settings
        DatabaseReference privacy = userRef.child("Settings");
        DatabaseReference showDOBSettings = privacy.child("showDOB");
        showDOBSettings.setValue(true);

        // Add blacklist
        DatabaseReference blackList = privacy.child("BlackList");
        ArrayList<String> list = new ArrayList<String>();
        blackList.setValue(list);
    }

    /**
     * Add the two default users as required by the specs
     * @author Gia Minh Nguyen
     */
    private void addDefaultUsers() {
        List<String> contact = new ArrayList<>();
        contact.add("comp2100@anu.edu.au");
        addUser("ghYwpjtvy0aSp4upXZBe5tv6ITu2",
                "comp6442",
                "6442",
                "comp6442@anu.edu.au",
                contact);

        contact.clear();
        contact.add("comp6442@anu.edu.au");
        addUser("09kBj9pfYiOiAm5A1MTJTORwZCE2",
                "comp2100",
                "2100",
                "comp2100@anu.edu.au",
                contact);
    }


    /**
     * Add a large amount of dummy users to the database as data samples
     * @author Gia Minh Nguyen - u7556893
     */
    private void createUsers() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference usersArray = db.getReference("Users");
        usersArray.removeValue();


        int startingUID = 7540000;
        int currentUID = startingUID;
        int sampleAmount = 400;

        for(int i = 0; i < sampleAmount; i++) {
            //DatabaseReference userRef = usersArray.child(String.valueOf(currentUID));
            Random ran = new Random();
            String email = "u" + currentUID + "@anu.edu.au";
            List<String> cList = new ArrayList<>();
            for(int j = 0; j < 3; j++) {
                cList.add("u" + (ran.nextInt(sampleAmount) + startingUID));
            }
            if(i > sampleAmount / 2) {
                addUser(String.valueOf(currentUID), composeName(maleFirstList, lastNamesList), String.valueOf(currentUID), email, cList);
            } else {
                addUser(String.valueOf(currentUID), composeName(femaleFirstList, lastNamesList), String.valueOf(currentUID), email, cList);
            }
            currentUID += 1;
        }

    }


    /**
     * Add courses and their details from text file "Courses" to be included in the sample.
     * @author Gia Minh Nguyen
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
     * A method integrating different helper functions in the class. User sampling wizard.
     * @throws IOException
     * @author Gia Minh Nguyen - u7556893
     */
    public void sampling() throws IOException {
        readNamesFromFile();
        addCourses();
        createUsers();
        addDefaultUsers();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            sampling();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Go back to login page
        Intent intent = new Intent(getApplicationContext(), login_page.class);
        startActivity(intent);
    }
}
