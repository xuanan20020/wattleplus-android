package com.example.comp2100_ga_23s2.searchCourses.searchCourse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.p2p_messaging.direct_messages.directMessage_page;
import com.example.comp2100_ga_23s2.searchCourses.displayCourse;

import java.util.List;

/**
 * @author Harry Randall u7499609
 *  This java class is the message adapter for the recyclerView
 *  It will make the messages appear on the appropriate view.
 */
public class searchPageAdapter extends RecyclerView.Adapter<searchPageViewHolder>{
    Context context;
    List<coursesClass> courses;

    public searchPageAdapter(Context context, List<coursesClass> courses){
        this.context = context;
        this.courses = courses;
    }

    @NonNull
    @Override
    public searchPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new searchPageViewHolder(LayoutInflater.from(context).inflate(R.layout.search_courses_recycler_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull searchPageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.course_code.setText(courses.get(position).getCourseCode());
        holder.course_name.setText(courses.get(position).getCourseName());
        holder.course_units.setText(courses.get(position).getUnits());
        holder.course_search_image_view.setImageResource(courses.get(position).getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, displayCourse.class);
                intent.putExtra("courseCode", courses.get(position).getCourseCode());
                intent.putExtra("courseName", courses.get(position).getCourseName());
                intent.putExtra("courseUnits", courses.get(position).getUnits());
                intent.putExtra("imageID", courses.get(position).getImage());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
