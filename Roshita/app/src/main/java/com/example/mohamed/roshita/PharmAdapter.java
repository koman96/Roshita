package com.example.mohamed.roshita;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class PharmAdapter extends RecyclerView.Adapter<PharmAdapter.MyHolder>{
    private ArrayList<PharmModel> result;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<String> sellersId;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int viewPosition = recyclerView.getChildLayoutPosition(v);
            PharmModel pharmModel = result.get(viewPosition);
            String sellerId = sellersId.get(viewPosition);

            double[] location = new double[]{ pharmModel.getAddLatitude() ,pharmModel.getAddLongitude() };

            Intent intent = new Intent(context ,Client7Activity.class);
            intent.putExtra("sellerId" ,sellerId);
            intent.putExtra("location" ,location);
            intent.putExtra("pharmName" ,pharmModel.getPharmName() );
            intent.putExtra("pharmPhone" ,pharmModel.getPharmPhone() );
            intent.putExtra("delivery" ,pharmModel.getHomeDelivery() );

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    };

    public PharmAdapter(ArrayList<PharmModel> result ,RecyclerView recyclerView ,Context context ,ArrayList<String> sellersId){
        this.result = result;
        this.recyclerView = recyclerView;
        this.context = context;
        this.sellersId = sellersId;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_pharm ,parent ,false);
            view.setOnClickListener(listener);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        PharmModel model = result.get(position);

        holder.pharmName.setText(model.getPharmName() );
        holder.pharmAddr.setText( getAddress( model.getAddLatitude() ,model.getAddLongitude() ));
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public String getAddress(double latitude ,double longitude){
        String address = "غير متـاح";

        Geocoder geocoder = new Geocoder(context , Locale.getDefault() );
        ArrayList<Address> addresses = new ArrayList<>();

        try {
            addresses = (ArrayList<Address>) geocoder.getFromLocation(latitude ,longitude ,1);

            if (! addresses.isEmpty() )
                address = addresses.get(0).getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }


    public class MyHolder extends RecyclerView.ViewHolder{
        TextView pharmName ,pharmAddr;

        public MyHolder(View itemView) {
            super(itemView);

            pharmName = (TextView) itemView.findViewById(R.id.pharm_Name);
            pharmAddr = (TextView)itemView.findViewById(R.id.pharm_Addr);
        }
    }
}