package com.example.mohamed.roshita;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyHolder> {
    ArrayList<String[]> array;

    public StoreAdapter (ArrayList<String[]> array){
        this.array = array;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.store_meds ,parent ,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.name.setText( array.get(position)[0] );
        holder.price.setText( array.get(position)[1] );
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView name ,price;

        public MyHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.med_name);
            price = (TextView)itemView.findViewById(R.id.med_price);
        }
    }
}