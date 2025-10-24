package com.example.comp2100_ga_23s2.p2p_messaging.contacts;

import com.example.comp2100_ga_23s2.objects.User;

/**
 * @author Harry Randall
 * @date 15/10/2023
 * Auto generated file getting all of the relevant information.
 * Easy to change in the future, just need to add a new field.
 */

public class contacts_item_class {
    User user;
    int image;

    public contacts_item_class(User user, int image) {
        this.user = user;
        this.image = image;
    }
    public String getEmail(){return user.email;}
    public String getUsername(){return user.username;}
    public String getTopLeverUserID(){return user.topLevelUserId;}
    public String getUserID(){return "u" + user.uid;}
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
