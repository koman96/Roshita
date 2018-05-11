package com.example.mohamed.roshita;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;

public class SugMedAdapter extends RecyclerView.Adapter<SugMedAdapter.SugHolder> {
    ArrayList<String>listModels;

    @Override
    public SugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_medicine ,parent ,false);
        SugHolder sugHolder = new SugHolder(view);
        return sugHolder;
    }

    @Override
    public void onBindViewHolder(SugHolder holder, int position) {
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SugHolder extends RecyclerView.ViewHolder{
        public SugHolder(View itemView) {
            super(itemView);
        }
    }
}