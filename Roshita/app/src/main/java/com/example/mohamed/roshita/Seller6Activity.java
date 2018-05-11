package com.example.mohamed.roshita;
//add med activity
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

public class Seller6Activity extends AppCompatActivity {
    Button b1 ,b2 ,b3 ,b4 ,b5 ,addBtn ,b6;
    EditText medName1 ,medPrice1;
    ImageView menuView;
    DrawerLayout drawer;
    String userId;
    DatabaseReference ref;
    Boolean confirm = true;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller6);

        myConnection = new MyConnection(getApplicationContext() );

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView barHeader = (TextView)findViewById(R.id.toolbar_header);
        barHeader.setText("إضـافـة دواء");

        drawer = (DrawerLayout)findViewById(R.id.drawer);
        menuView = (ImageView)findViewById(R.id.menu_icon);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        updateOnlineState();


        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT) )
                    drawer.closeDrawer(Gravity.RIGHT);
                else
                    drawer.openDrawer(Gravity.RIGHT);
            }
        });

        medName1 = (EditText)findViewById(R.id.medName1);
        medPrice1 = (EditText)findViewById(R.id.medPrice1);

        addBtn = (Button)findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            //when seller add med
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() ) {
                    addBtn.setEnabled(false);

                    String inputMed = medName1.getText().toString();
                    String inputPrice = medPrice1.getText().toString();

                    if (! inputMed.equals("") && ! inputPrice.equals("") ){
                        checkMedExistance(inputMed);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (confirm) {  //med isn't exist

                                    MedInfo med = new MedInfo(medName1.getText().toString(), medPrice1.getText().toString());
                                    ref = FirebaseDatabase.getInstance().getReference("store");

                                    ref.child(userId).child(med.getMedName() ).setValue(med.getMedPrice() );

                                    Toast.makeText(getApplicationContext(), "تم إضافة الدواء بنجاح", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), Seller5Activity.class));
                                }
                            }
                        } ,1000);
                    }
                    else
                        Toast.makeText(Seller6Activity.this, "بـرجاء إدخـال جميـع بيانات الـدواء", Toast.LENGTH_SHORT).show();

                    addBtn.setEnabled(true);
                }
            }
        });

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
                //editMed
            }
        });

        b3 = (Button)findViewById(R.id.editData);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                    startActivity(new Intent(getBaseContext() ,Seller7Activity.class) );
                //editData
            }
        });

        b4 = (Button)findViewById(R.id.messagePage);
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                    startActivity(new Intent(getBaseContext() ,Seller9Activity.class) );
                //messagePage
            }
        });

        b5 = (Button)findViewById(R.id.ordersPage);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                    startActivity(new Intent(getBaseContext() ,Seller10Activity.class) );

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
    }

    public void checkMedExistance(final String medName){
        ref = FirebaseDatabase.getInstance().getReference("store");

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(medName) ) {
                    confirm = false;
                    Toast.makeText(Seller6Activity.this ,"الدواء موجـود بالفعل" ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
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