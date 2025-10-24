package com.example.comp2100_ga_23s2.p2p_messaging.direct_messages;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.comp2100_ga_23s2.R;

/**
 * @author Harry Randall u7499609
 * This class gest all of the elements from the itemView and
 * makes them accesable for the recycleView.
 */
public class directMessageViewHolder extends RecyclerView.ViewHolder {
    TextView directMessageSentText, directMessageReceivedText;
    LinearLayout leftMessageBubble, rightMessageBubble;
    public directMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        directMessageSentText = itemView.findViewById(R.id.directMessageSentText);
        directMessageReceivedText = itemView.findViewById(R.id.directMessageReceivedText);
        leftMessageBubble = itemView.findViewById(R.id.leftMessageBubble);
        rightMessageBubble = itemView.findViewById(R.id.rightMessageBubble);
    }
}
