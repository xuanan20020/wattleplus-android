package com.example.comp2100_ga_23s2.objects;

import android.os.Parcelable;
import android.os.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Course implements Serializable {
    private String courseCode;
    private String name;
    private int unit = 6;
    private ArrayList<Assessment> assessments = new ArrayList<>();


    public Course(String courseCode, String name) {
        this.courseCode = courseCode;
        this.name = name;
        this.assessments = generateAssessments();
    }


    public Course(String courseCode, String name, int unit) {
        this.courseCode = courseCode;
        this.name = name;
        this.unit = unit;
        this.assessments = generateAssessments();
    }


    public Course(String courseCode, String name, int unit, ArrayList<Assessment> assessments) {
        this.courseCode = courseCode;
        this.name = name;
        this.unit = unit;
        this.assessments = assessments;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public int getUnit() {
        return unit;
    }

    public ArrayList<Assessment> getAssessments() {
        return assessments;
    }

    public String getName() {return this.name;}

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    /**
     * Method to create a list of assessments for a course. Weightings are randomized within
     * reasonable range according to uni's policy on assessments
     * @return a list of assessments of the course.
     * @author Gia Minh
     */
    private ArrayList<Assessment> generateAssessments() {
        ArrayList<Assessment> as = new ArrayList<>();
        Random random = new Random();
        int max = 60;
        int min = 25;
        int finalExam = random.nextInt(max - min + 1) + min;
        Assessment a1 = new Assessment("Final Exam", finalExam);
        as.add(a1);

        max = 40;
        min = 10;
        int assignment = random.nextInt(max - min + 1) + min;
        Assessment a2 = new Assessment("Assignments", assignment);
        as.add(a2);

        int other = 100 - assignment - finalExam;
        if (other > 0) {
            Assessment quiz = new Assessment("Weekly quizzes", other);
            as.add(quiz);
        }
        return as;
    }
}
