package com.example.comp2100_ga_23s2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    String [] courseList;
    int courseImages [];
    LayoutInflater inflater;
    public CustomBaseAdapter(Context context, String [] courseList, int [] courseImages) {
        this.context = context;
        this.courseList = courseList;
        this.courseImages = courseImages;
        inflater = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return courseList.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.home_item_view, null);
        TextView textView = (TextView) convertView.findViewById(R.id.course_code);
        ImageView courseImg = (ImageView) convertView.findViewById(R.id.course_image_view);
        textView.setText(courseList[position]);
        courseImg.setImageResource(courseImages[position]);
        return convertView;
    }
}
