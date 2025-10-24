package com.example.comp2100_ga_23s2.p2p_messaging.direct_messages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100_ga_23s2.R;

import java.util.List;

/**
 * @author Harry Randall u7499609
 * This java class is the message adapter for the recyclerView
 * It will make the messages appear on the appropriate view.
 * If the message is sent, it will remove one of the messageBubbles
 * and display the current message.
 */
public class directMessagesAdapter extends RecyclerView.Adapter<directMessageViewHolder> {
    Context context;
    List<MessageClass> messages;

    public directMessagesAdapter(Context context, List<MessageClass> items) {
        this.context = context;
        this.messages = items;
    }

    @NonNull
    @Override
    public directMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new directMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.direct_messages_recycler_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull directMessageViewHolder holder, int position) {
        if (messages.get(position).getMessageType().equals("Sent")){
//           Sent message
            holder.directMessageSentText.setText(messages.get(position).getMessage());
            holder.rightMessageBubble.setVisibility(View.VISIBLE);
            holder.leftMessageBubble.setVisibility(View.GONE);
        } else if (messages.get(position).getMessageType().equals("Received")) {
//            Received
            holder.directMessageReceivedText.setText(messages.get(position).getMessage());
            holder.leftMessageBubble.setVisibility(View.VISIBLE);
            holder.rightMessageBubble.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
