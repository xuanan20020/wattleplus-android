package com.example.comp2100_ga_23s2.searchCourses.searchCourse;


/**
 * @author Harry Randall u7499609
 * This is a fairly simple function that users the constructor
 * MessageClass to create a message. I then used the generator
 * for a 'getter and setter' to generate the below voids.
 * This is so we can store the message in an object and fetch items.
 */
public class coursesClass {
    String courseCode;
    String courseName;
    String units;
    int image;

    public coursesClass(String courseCode, String courseName, String units, int image) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.units = units;
        this.image = image;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public String getUnits() {
        return units;
    }
    public void setUnits(String units) {
        this.units = units;
    }
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
