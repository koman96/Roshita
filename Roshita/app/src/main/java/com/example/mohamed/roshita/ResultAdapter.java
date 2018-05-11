package com.example.mohamed.roshita;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyHolder> {
    private ArrayList<SearchObject> result;
    private Context context;
    private RecyclerView recyclerView;
    private String searchMed;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context ,Client4Activity.class);
            SearchObject object = result.get( recyclerView.getChildLayoutPosition(v) );

            intent.putExtra("pharmOwner" ,object.getPharmOwner() )
                    .putExtra("medName" ,searchMed)
                    .putExtra("medPrice" ,object.getMedPrice() );

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    };

    public ResultAdapter(Context context ,RecyclerView recyclerView ,ArrayList<SearchObject> result ,String searchMed){
        this.context = context;
        this.recyclerView = recyclerView;
        this.result = result;
        this.searchMed = searchMed;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext() ).inflate(R.layout.one_pharm_result ,parent ,false);
        MyHolder holder = new MyHolder(view);

        view.setOnClickListener(listener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.pharmName.setText( result.get(position).getPharmName() );
        holder.medPrice.setText( result.get(position).getMedPrice() );
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public void setResult(ArrayList<SearchObject> result) {
        this.result = result;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView pharmName ,medPrice;

        public MyHolder(View itemView) {
            super(itemView);
            pharmName = (TextView)itemView.findViewById(R.id.pharmNameTextView);
            medPrice = (TextView)itemView.findViewById(R.id.medPriceTextView);
        }
    }
}