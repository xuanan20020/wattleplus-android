package com.example.comp2100_ga_23s2.objects;

import java.io.Serializable;
import java.util.ArrayList;


public class User implements Comparable<User>,Serializable {
    public String username;
    public String email;
    public int uid;
    public String topLevelUserId;
    private String dob;  // date of birth, format: dd-mm-yyyy
    private ArrayList<Course> currentCourses = new ArrayList<>();
    private ArrayList<User> contactList = new ArrayList<>();

    // privacy setting:
    private boolean showUserName;
    private boolean showCurrentCourses;

    public User(String username,String email,int uid,String topLevelUserId){
        this.username = username;
        this.topLevelUserId = topLevelUserId;
        this.uid = uid;
        this.email = email;
    }
    public User(String username, int uid) {
        this.username = username;
        this.uid = uid;
    }

    public boolean getShowUserName() {
        return this.showUserName;
    }

    public boolean getShowCurrentCourses() {
        return this.showCurrentCourses;
    }

    public void addFriend(User newFriend) {
        this.contactList.add(newFriend);
    }

    public void addFriend(ArrayList<User> newFriends) {
        this.contactList.addAll(newFriends);
    }

    public ArrayList<User> getFriends() {
        return this.contactList;
    }

    public void removeFriend(User friend) {
        this.contactList.remove(friend);
    }

    public ArrayList<Course> getCurrentCourses() {
        return currentCourses;
    }

    public void setCurrentCourses(ArrayList<Course> currentCourses) {
        this.currentCourses = currentCourses;
    }
    public int compareTo(User other) {
        if (this.username.compareTo(other.username) < 0){
            return -1;
        } else if (this.username.compareTo(other.username) > 0){
            return 1;
        } else{
            return Integer.compare(this.uid,other.uid);
        }
    }
}
