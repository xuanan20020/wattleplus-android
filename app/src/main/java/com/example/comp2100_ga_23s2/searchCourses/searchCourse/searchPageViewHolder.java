package com.example.comp2100_ga_23s2.searchCourses.searchCourse;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100_ga_23s2.R;

/**
 * @author Harry Randall u7499609
 * This class gest all of the elements from the itemView and
 * makes them accesable for the recycleView.
 */
public class searchPageViewHolder extends RecyclerView.ViewHolder{
    TextView course_code, course_name, course_units;
    ImageView course_search_image_view;
    public searchPageViewHolder(@NonNull View itemView) {
        super(itemView);
        course_code = itemView.findViewById(R.id.course_code);
        course_name = itemView.findViewById(R.id.course_name);
        course_units = itemView.findViewById(R.id.course_units);
        course_search_image_view = itemView.findViewById(R.id.course_search_image_view);
    }
}
