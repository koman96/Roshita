package com.example.mohamed.roshita;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private Context context;
    private ArrayList<Messages> messageList;
    private String myId;

    public MessageAdapter(Context context ,ArrayList<Messages> messageList){
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext() ).inflate(R.layout.single_message ,parent ,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Messages m = messageList.get(position);

        String fromUser = m.getFrom();
        String msgType = m.getType();

        if (msgType.equals("text") ){

            if (fromUser.equals(myId) ){    //i sent the message
                holder.userIcon.setVisibility(View.INVISIBLE);

                holder.msgLayout.setGravity(Gravity.RIGHT);     //set text to the right
            }
            else{
                holder.messageText.setBackgroundResource(R.drawable.other_messages_background);
            }

            holder.messageText.setText(m.getMessage() );
        }
        else {  // image
            //Glide.with(context).load(m.getMessage() ).centerCrop().into(holder.imageMsg);
            Glide.with(context).load(m.getMessage() ).fitCenter().into(holder.imageMsg);

            holder.messageText.setVisibility(View.INVISIBLE);

            if (fromUser.equals(myId) ){    // i sent the image
                holder.userIcon.setVisibility(View.INVISIBLE);

                holder.msgLayout.setGravity(Gravity.RIGHT);    //set image to the right
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        public ImageView userIcon ,imageMsg;
        public LinearLayout msgLayout;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.messageTxt);
            userIcon = (ImageView) itemView.findViewById(R.id.userIcon);
            imageMsg = (ImageView) itemView.findViewById(R.id.imageMsg);
            msgLayout = (LinearLayout) itemView.findViewById(R.id.messageLayout);
        }
    }
}