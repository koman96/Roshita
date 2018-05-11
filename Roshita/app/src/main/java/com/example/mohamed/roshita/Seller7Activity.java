package com.example.mohamed.roshita;
//edit pharmacy data
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Seller7Activity extends AppCompatActivity {
    EditText pharmName ,pharmPhone;
    CheckBox homeDelivery;
    String userId;
    DrawerLayout drawer;
    PharmModel model;
    DatabaseReference ref;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller7);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView barHeader = (TextView)findViewById(R.id.toolbar_header);
        barHeader.setText("بيانـات الصيدليـة");

        pharmName = (EditText)findViewById(R.id.pharmName1);
        pharmPhone = (EditText)findViewById(R.id.pharmPhone1);
        homeDelivery = (CheckBox)findViewById(R.id.homeDelivery);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myConnection = new MyConnection(getApplicationContext() );

        updateOnlineState();

        ref = FirebaseDatabase.getInstance().getReference("pharmacy");
        ref.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                model = dataSnapshot.getValue(PharmModel.class);

                pharmName.setText(model.getPharmName() );
                pharmPhone.setText(model.getPharmPhone() );
                homeDelivery.setChecked(model.getHomeDelivery() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Seller7Activity.this ,"حـدث خـطأ أثناء تحميل بيانات الصـيدليـة" ,Toast.LENGTH_SHORT).show();
            }
        });

        drawer = (DrawerLayout)findViewById(R.id.drawer);
        ImageView menuIcon = (ImageView)findViewById(R.id.menu_icon);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT) )
                    drawer.closeDrawer(Gravity.RIGHT);
                else
                    drawer.openDrawer(Gravity.RIGHT);
            }
        });

        Button confirmBtn ,b1 ,b2 ,b3 ,b5 ,b4 ,b6;

        b1 = (Button)findViewById(R.id.mainBtn);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller5Activity.class) );
            }
        });

        b2 = (Button)findViewById(R.id.editMed);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller8Activity.class) );
            }
        });

        b3 = (Button)findViewById(R.id.addMed);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller6Activity.class) );
            }
        });

        b4 = (Button)findViewById(R.id.ordersPage);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller10Activity.class) );

            }
        });

        b5 = (Button)findViewById(R.id.messagePage);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller9Activity.class) );
            }
        });

        b6 = (Button)findViewById(R.id.deleteAccPage);
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller11Activity.class) );

            }
        });

        confirmBtn = (Button)findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() ) {

                    String inputPharm = pharmName.getText().toString();
                    String inputPhone = pharmPhone.getText().toString();
                    Boolean delivery = false;

                    if (homeDelivery.isChecked())
                        delivery = true;

                    if (!inputPharm.equals("") && !inputPhone.equals("")) {    //check empty inputs

                        if (model.getPharmName().equals(inputPharm) && model.getPharmPhone().equals(inputPhone) &&
                                (model.getHomeDelivery() == delivery)) {

                            Toast.makeText(Seller7Activity.this, "لم يحدث تغيير في بيانـاتك الأصـلية", Toast.LENGTH_LONG).show();
                        } else {
                            PharmModel newModel = new PharmModel(inputPharm, inputPhone,
                                    model.getAddLongitude(), model.getAddLatitude(), delivery);

                            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            ref = FirebaseDatabase.getInstance().getReference("pharmacy");
                            ref.child(userId).setValue(newModel);

                            startActivity(new Intent(Seller7Activity.this, Seller5Activity.class));
                            Toast.makeText(Seller7Activity.this, "تم تعديـل بيانات الصيدلية بنجاح", Toast.LENGTH_LONG).show();
                        }
                    } else
                        Toast.makeText(Seller7Activity.this, "برجاء إدخـال جميـع البيانات", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateOnlineState() {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers").child(userId);
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

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                userRef.child("online").setValue(true);
            }
        } ,2000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers").child(userId);
        userRef.child("online").setValue(false);
    }
}