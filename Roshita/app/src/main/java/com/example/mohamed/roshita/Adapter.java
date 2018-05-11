package com.example.mohamed.roshita;
//suggested med adapter
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyHolder> {
    private ArrayList<String> medList;

    public Adapter (ArrayList<String> medList){
        this.medList = medList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext() ).inflate(R.layout.suggested_medicine ,parent ,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        holder.medName.setText(medList.get(position) );


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {   //listen for checkbox
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked)
                    holder.medPrice.setVisibility(View.VISIBLE);

                else
                    holder.medPrice.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public int getItemCount() {
        return medList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView medName;
        EditText medPrice;
        CheckBox checkBox;

        private MyHolder(View itemView) {
            super(itemView);
            medName = (TextView)itemView.findViewById(R.id.medName);
            medPrice = (EditText)itemView.findViewById(R.id.medPrice);
            checkBox = (CheckBox) itemView.findViewById(R.id.available);
        }
    }
}