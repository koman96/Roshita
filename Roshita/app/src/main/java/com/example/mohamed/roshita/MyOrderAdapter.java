package com.example.mohamed.roshita;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyHolder> {
    private Context context;
    private ArrayList<MyOrderModel> myOrders;
    private RecyclerView recyclerView;
    private Boolean response;

    private View.OnLongClickListener listener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(final View myView) {
            //response = false;

            final MyDialog dialog = new MyDialog(context);
            dialog.show();

            dialog.dialogText.setText("هل أنت متأكد من حذف الطلب ؟");
            dialog.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyOrderModel deletedOrder = myOrders.get( recyclerView.getChildLayoutPosition(myView) );

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders");
                    ref.child(deletedOrder.getDate() ).removeValue();
                    myOrders.remove(deletedOrder);

                    dialog.dismiss();
                    Toast.makeText(context ,"تم حذف الطلب بنجاح" ,Toast.LENGTH_SHORT).show();

                  //  response = true;
                }
            });
            dialog.no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            return true;
        }
    };

    public MyOrderAdapter (Context context ,ArrayList<MyOrderModel> myOrders ,RecyclerView recyclerView){
        this.context = context;
        this.myOrders = myOrders;
        this.recyclerView = recyclerView;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext() ).inflate(R.layout.single_my_order ,parent ,false);
            view.setOnLongClickListener(listener);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.medName.setText( myOrders.get(position).getMedName() );
        holder.date.setText( myOrders.get(position).getDate() );
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        public TextView medName;
        public TextView date;

        public MyHolder(View itemView) {
            super(itemView);

            medName = (TextView) itemView.findViewById(R.id.order_med);
            date = (TextView) itemView.findViewById(R.id.orderDate);
        }
    }
}