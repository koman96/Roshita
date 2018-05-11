package com.example.mohamed.roshita;
//pharm result
import android.content.Intent;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
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

public class Client7Activity extends AppCompatActivity {
    private TextView t3 ,t4 ,t5 ,t6 ,t7 ;   //just for decor
    private TextView pharmName ,pharmPhone ,homeDelivery ,pharmAdress ,pharmMap ,mainBar;
    private Intent intent;
    private Button msgBtn;
    private double[] location;
    private String sellerId;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client7);

        updateOnlineState();

        initializeViews();

        showSellerInfo();
    }


//Functions
    private void initializeViews(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mainBar = (TextView)findViewById(R.id.main);
        mainBar.setText("تفاصـيل البحث");

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

        pharmName = (TextView)findViewById(R.id.pharm_Name);
        pharmPhone = (TextView)findViewById(R.id.pharm_Phone);
        homeDelivery = (TextView)findViewById(R.id.pharmDelivery);
        pharmAdress = (TextView)findViewById(R.id.pharm_Addr);
        pharmMap = (TextView)findViewById(R.id.pharm_Map);
        pharmMap.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        msgBtn = (Button)findViewById(R.id.msgBtn);

        myConnection = new MyConnection(getApplicationContext() );


    //Listeners
        pharmMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location != null){
                    if (myConnection.getConnection() ) {

                        Intent mapIntent = new Intent(getApplicationContext(), ResultPharmLocation.class);
                        mapIntent.putExtra("location", location);

                        startActivity(mapIntent);
                    }
                }
            }
        });

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() ) {

                    if (!sellerId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                        chatIntent.putExtra("receiverId", sellerId);
                        chatIntent.putExtra("receiverType", "seller");

                        startActivity(chatIntent);
                    } else
                        Toast.makeText(getApplicationContext(), "لا يمكن أن تحادث نفسـك", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showSellerInfo(){
        intent = getIntent();

        sellerId = intent.getStringExtra("sellerId");
        pharmName.setText( intent.getStringExtra("pharmName") );
        pharmPhone.setText(intent.getStringExtra("pharmPhone") );

        if (intent.getBooleanExtra("delivery" ,false) )
            homeDelivery.setText("متـاح");
        else
            homeDelivery.setText("غير متـاح");

        location = intent.getDoubleArrayExtra("location");
        pharmAdress.setText( getAddress(location[0] ,location[1] ));
    }

    private String getAddress(double latitude ,double longitude){
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
}