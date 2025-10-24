package com.example.comp2100_ga_23s2.p2p_messaging.contacts;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100_ga_23s2.R;
import com.example.comp2100_ga_23s2.p2p_messaging.direct_messages.directMessage_page;

/**
 * @author Harry Randall
 * @date 15/10/2023
 * This class is the body of the recyclerView. It will fetch the information
 * from a list (using the item class) and the build the view.
 */

public class contacts_adapter extends RecyclerView.Adapter<contacts_view_holder> {

    Context context;
    List<contacts_item_class> items;

    public contacts_adapter(Context context, List<contacts_item_class> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public contacts_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new contacts_view_holder(LayoutInflater.from(context).inflate(R.layout.contacts_item_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull contacts_view_holder holder, int position) {
        contacts_item_class item = items.get(position);
        holder.contacts_username.setText(items.get(position).getUsername());
        holder.contacts_email.setText(items.get(position).getEmail());
        holder.contacts_userID.setText(items.get(position).getUserID());
        holder.contacts_image_view.setImageResource(items.get(position).getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, directMessage_page.class);
                intent.putExtra("username", item.getUsername());
                intent.putExtra("email", item.getEmail());
                intent.putExtra("userID", item.getUserID());
                intent.putExtra("topLevelUserID", item.getTopLeverUserID());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
