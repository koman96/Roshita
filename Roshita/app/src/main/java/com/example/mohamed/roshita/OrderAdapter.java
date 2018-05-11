package com.example.mohamed.roshita;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyHolder>{
    private Context context;
    private ArrayList<OrderModel> orders;
    private RecyclerView recyclerView;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = recyclerView.getChildAdapterPosition(v);     //position of clicked order
            OrderModel model = orders.get(position);

            Intent intent = new Intent(context ,ChatActivity.class);
            intent.putExtra("receiverId" ,model.getSenderId() );
            intent.putExtra("receiverType" ,"client");
            intent.putExtra("receiverName" ,model.getSenderName() );

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    };

    public OrderAdapter(Context context ,ArrayList<OrderModel> orders ,RecyclerView recyclerView){
        this.context = context;
        this.orders = orders;
        this.recyclerView = recyclerView;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext() ).inflate(R.layout.single_order ,parent ,false);
            view.setOnClickListener(listener);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        OrderModel orderModel = orders.get(position);

        holder.medName.setText(orderModel.getMedName() );
        holder.senderName.setText(orderModel.getSenderName() );
        holder.senderAdd.setText(orderModel.getSenderAdd() );
        holder.date.setText(orderModel.getDate() );
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView medName ,senderName ,senderAdd ,date;

        public MyHolder(View itemView) {
            super(itemView);

            medName = (TextView) itemView.findViewById(R.id.order_med);
            senderName = (TextView) itemView.findViewById(R.id.senderEmail);
            senderAdd = (TextView) itemView.findViewById(R.id.senderAdd);
            date = (TextView) itemView.findViewById(R.id.orderDate);
        }
    }
}