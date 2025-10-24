package com.example.comp2100_ga_23s2.searchEngine;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user's query in the search engine.
 * This class encapsulates the details about a user, including their username, user ID, and courses.
 * @author Xuan An
 */
public class UserQuery {
    private String username; // The username of the user
    private String uid;      // The unique identifier (UID) for the user
    private final List<String> courses = new ArrayList<>();  // List to store courses associated with the user

    /**
     * Sets the username for the user.
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the UID for the user.
     * @param uid The UID to set.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Adds a course to the user's list of courses.
     * @param course The course to add.
     */
    public void addCourse(String course) {
        this.courses.add(course);
    }

    /**
     * Returns the UID of the user.
     * @return The user's UID.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Returns the username of the user.
     * @return The user's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the list of courses associated with the user.
     * @return The user's courses.
     */
    public List<String> getCourses() {
        return courses;
    }

    /**
     * Returns a string representation of the user query.
     * @return A string representation of the user's details.
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder c = new StringBuilder();
        for (String s:courses){
            c.append(s).append(" ");
        }
        return "UserQuery{" +
                "username='" + username + '\'' +
                ", uid='" + uid + '\'' +
                ", courses=" + c +
                '}';
    }
}
