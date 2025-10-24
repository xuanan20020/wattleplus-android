package com.example.comp2100_ga_23s2.p2p_messaging.contacts;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100_ga_23s2.R;


/**
 * @author Harry Randall
 * @date 15/10/2023
 * This file fetches all of the IDS from the itemView.
 */

public class contacts_view_holder extends RecyclerView.ViewHolder {

    public ImageView contacts_image_view;
    public TextView contacts_username;
    public TextView contacts_email;
    public TextView contacts_userID;

    public contacts_view_holder(@NonNull View itemView) {
        super(itemView);
        contacts_image_view = itemView.findViewById(R.id.contacts_image_view);
        contacts_username = itemView.findViewById(R.id.course_code);
        contacts_email = itemView.findViewById(R.id.contacts_email);
        contacts_userID = itemView.findViewById(R.id.contacts_userID);
    }
}
