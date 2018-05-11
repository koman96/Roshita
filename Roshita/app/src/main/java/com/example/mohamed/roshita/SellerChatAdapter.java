package com.example.mohamed.roshita;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class SellerChatAdapter extends RecyclerView.Adapter<SellerChatAdapter.MyHolder> {
    private Context context;
    private ArrayList<UserModel> users;
    private ArrayList<String> clientsIds;
    private RecyclerView recyclerView;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);
            Intent intent = new Intent(context ,ChatActivity.class);

            intent.putExtra("receiverId" ,clientsIds.get(position) );
            intent.putExtra("receiverType" ,"client");
            intent.putExtra("receiverName" ,users.get(position).getUserName() );

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    };


    public SellerChatAdapter(Context context ,ArrayList<UserModel> users ,ArrayList<String> clientsIds ,RecyclerView recyclerView){
        this.context = context;
        this.users = users;
        this.clientsIds = clientsIds;
        this.recyclerView = recyclerView;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext() ).inflate(R.layout.single_inbox_user ,parent ,false);
        view.setOnClickListener(listener);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        UserModel currUser = users.get(position);
        holder.userName.setText( currUser.getUserName() );

        if (! currUser.isOnline() )
            holder.onlineIcon.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public ImageView onlineIcon;

        public MyHolder(View itemView) {
            super(itemView);

            userName = (TextView)itemView.findViewById(R.id.emailAdd);
            onlineIcon = (ImageView)itemView.findViewById(R.id.onlineIcon);
        }
    }
}