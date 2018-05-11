package com.example.mohamed.roshita;
//search info
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class Client4Activity extends AppCompatActivity {
    private TextView t1 ,t2 ,t3 ,t4 ,t5 ,t6 ,t7 ;   //just for decor
    private TextView medName ,medPrice ,pharmName ,pharmPhone ,homeDelivery ,pharmAdress ,pharmMap;
    private TextView mainBar;
    private Intent intent;
    private PharmModel pharmModel;
    private String sellerId;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client4);

        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mainBar = (TextView)findViewById(R.id.main);
        mainBar.setText("تفاصـيل البحث");

        t1 = (TextView)findViewById(R.id.t1);
            t1.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        t2 = (TextView)findViewById(R.id.t2);
            t2.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        t3 = (TextView)findViewById(R.id.t3);
            t3.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        t4 = (TextView)findViewById(R.id.t4);
            t4.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        t5 = (TextView)findViewById(R.id.t5);
            t5.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        t6 = (TextView)findViewById(R.id.t6);
            t6.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        t7 = (TextView)findViewById(R.id.t7);
            t7.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        medName = (TextView)findViewById(R.id.med_Name);
        medPrice = (TextView)findViewById(R.id.med_Price);
        pharmName = (TextView)findViewById(R.id.pharm_Name);
        pharmPhone = (TextView)findViewById(R.id.pharm_Phone);
        homeDelivery = (TextView)findViewById(R.id.pharmDelivery);
        pharmAdress = (TextView)findViewById(R.id.pharm_Addr);
        pharmMap = (TextView)findViewById(R.id.pharm_Map);
        pharmMap.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        Button msgBtn = (Button)findViewById(R.id.msgBtn);

        myConnection = new MyConnection(getApplicationContext() );
        updateOnlineState();

        intent = getIntent();
        sellerId = intent.getStringExtra("pharmOwner");


        new InfoTask().execute();


    //Listeners
        pharmMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get location of pharmacy from dataBase ====>
                //go to map location ====>
                if (myConnection.getConnection() ) {
                    if (pharmModel != null) {
                        Intent intent = new Intent(getApplicationContext(), ResultPharmLocation.class);
                        intent.putExtra("location", new double[]{pharmModel.getAddLatitude(), pharmModel.getAddLongitude()});

                        startActivity(intent);
                    }
                }
            }
        });

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (! sellerId.equals( FirebaseAuth.getInstance().getCurrentUser().getUid() )) {    // seller is not the client
                    if (myConnection.getConnection() ) {

                        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                        intent.putExtra("receiverId", sellerId);
                        intent.putExtra("receiverType", "seller");

                        startActivity(intent);
                    }
                }
                else
                    Toast.makeText(getApplicationContext() ,"لا يمكن أن تحادث نفسـك" ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPharmacyInfo(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("pharmacy");
        ref.child(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pharmModel = dataSnapshot.getValue(PharmModel.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void showResults(){
        if (pharmModel != null){
            medName.setText( intent.getStringExtra("medName") );
            medPrice.setText( intent.getStringExtra("medPrice") );

            pharmName.setText(pharmModel.getPharmName() );
            pharmPhone.setText(pharmModel.getPharmPhone() );

            if (pharmModel.getHomeDelivery() )
                homeDelivery.setText("متـاح");
            else
                homeDelivery.setText("غير متـاح");

            pharmAdress.setText( getAddress(pharmModel.getAddLatitude() ,pharmModel.getAddLongitude() ));
        }
    }

    public String getAddress(double latitude ,double longitude){
        String address = "لا يوجد";
        Geocoder geocoder = new Geocoder(getApplicationContext() , Locale.getDefault() );
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

    private void updateOnlineState() {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userRef.child("online").onDisconnect().setValue(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                userRef.child("online").setValue(true);
            }
        } ,2000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.child("online").setValue(false);
    }


    private class InfoTask extends AsyncTask <Void ,Void ,String>{
        @Override
        protected String doInBackground(Void... params) {
            getPharmacyInfo();
            publishProgress();

            return "done";
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    showResults();
                }
            } ,500);
        }
    }
}