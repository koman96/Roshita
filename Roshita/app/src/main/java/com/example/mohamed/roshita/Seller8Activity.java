package com.example.mohamed.roshita;
//edit and delete med
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Seller8Activity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    DrawerLayout drawer;
    Spinner medsSpin;
    String userId ,medNme ,inputMed ,inputPrice;
    DatabaseReference ref;
    ArrayList<String>medList;
    EditText medName ,medPrice;
    Button edit ,delete ,mainBtn ,addMed ,editData ,messagePage ,ordersPage ,deleteAccPage;
    AlertDialog alert;
    ArrayAdapter<String>medsAdap;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myConnection = new MyConnection(getApplicationContext() );

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        updateOnlineState();

        //get seller's meds to put it in spinner
        ref = FirebaseDatabase.getInstance().getReference("store");
        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                medList = new ArrayList<>();

                for (DataSnapshot object : dataSnapshot.getChildren() )
                    medList.add(object.getKey() );

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        setContentView(R.layout.activity_seller8);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView barHeader = (TextView)findViewById(R.id.toolbar_header);
        barHeader.setText("تعـديل دواء");

        ImageView menuIcon = (ImageView)findViewById(R.id.menu_icon);
        drawer = (DrawerLayout)findViewById(R.id.drawer);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT) )
                    drawer.closeDrawer(Gravity.RIGHT);
                else
                    drawer.openDrawer(Gravity.RIGHT);
            }
        });

        medsSpin = (Spinner)findViewById(R.id.medsSpin);
        medName = (EditText)findViewById(R.id.medNameE);
        medPrice = (EditText)findViewById(R.id.medPriceE);
        edit = (Button)findViewById(R.id.editBtn);
        delete = (Button)findViewById(R.id.delBtn);
        mainBtn = (Button)findViewById(R.id.mainBtn);
        addMed = (Button)findViewById(R.id.addMed);
        editData = (Button)findViewById(R.id.editData);
        messagePage = (Button)findViewById(R.id.messagePage);
        ordersPage = (Button)findViewById(R.id.ordersPage);
        deleteAccPage = (Button) findViewById(R.id.deleteAccPage);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (! medList.isEmpty() ){
                    medsAdap = new ArrayAdapter<> (Seller8Activity.this ,R.layout.spinner_layout1 ,medList);
                    medsSpin.setAdapter(medsAdap);
                }
                else
                    Toast.makeText(Seller8Activity.this ,"لا توجد أدويـة كي يتم تعـديلها" ,Toast.LENGTH_SHORT).show();
            }
        } ,1000);

        medsSpin.setOnItemSelectedListener(this);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMed = medName.getText().toString();
                inputPrice = medPrice.getText().toString();

                if (!inputMed.equals("") && !inputPrice.equals("") ){
                    if (myConnection.getConnection() ) {

                        ref.child(userId).child(medNme).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String myMed = dataSnapshot.getKey();
                                String myPrice = dataSnapshot.getValue(String.class);

                                if (inputMed.equals(myMed) && inputPrice.equals(myPrice) )
                                    Toast.makeText(Seller8Activity.this, "لم يحدث تغيير في بيانات الدواء", Toast.LENGTH_SHORT).show();

                                else {
                                    dataSnapshot.getRef().removeValue();
                                    ref.child(userId).child(inputMed).setValue(inputPrice);

                                    Toast.makeText(Seller8Activity.this, "تم تعديل الدواء بنجاح", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getBaseContext(), Seller5Activity.class));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
                else
                    Toast.makeText(Seller8Activity.this ,"برجاء إدخال جميع البيانات" ,Toast.LENGTH_LONG).show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read the meds one by one and copy them if the med != deleted med , then set value of new copy
                if (myConnection.getConnection() ) {

                    if (!medNme.equals("")) {
                        alert = new AlertDialog.Builder(Seller8Activity.this)
                                .setTitle("حـذف الـدواء")
                                .setMessage("هل أنت متأكد من حذف الدواء :" + medNme + " ؟")
                                .setPositiveButton("نـعـم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        ref.child(userId).child(medNme).removeValue();
                                        alert.dismiss();

                                        Toast.makeText(getApplicationContext() ,"تم حذف الدواء بنجاح" ,Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getBaseContext(), Seller5Activity.class));
                                    }
                                })
                                .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alert.dismiss();
                                    }
                                }).create();
                        alert.show();
                    }
                }
            }
        });

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller5Activity.class) );
            }
        });

        addMed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller6Activity.class) );
            }
        });

        editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller7Activity.class) );
            }
        });

        messagePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inbox
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller9Activity.class) );
            }
        });

        ordersPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller10Activity.class) );

            }
        });

        deleteAccPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller11Activity.class) );

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //update the edit text of med name and its price
        if (myConnection.getConnection() ) {

            medName.setVisibility(View.VISIBLE);
            medPrice.setVisibility(View.VISIBLE);

            medNme = parent.getItemAtPosition(position).toString();
            medName.setText(medNme);

            ref.child(userId).child(medNme).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    medPrice.setText(dataSnapshot.getValue(String.class) );
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        medName.setText("");    medName.setVisibility(View.INVISIBLE);
        medPrice.setText("");   medPrice.setVisibility(View.INVISIBLE);
        medNme = "";
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