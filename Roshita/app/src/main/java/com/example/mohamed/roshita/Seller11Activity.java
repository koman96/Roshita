package com.example.mohamed.roshita;
//delete account
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Seller11Activity extends AppCompatActivity {
    private String myId;
    private DatabaseReference ref;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller11);

        initializeViews();
        updateOnlineState();
    }


//Functions
    private void initializeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView header = (TextView) findViewById(R.id.toolbar_header);
        header.setText("حذف الحساب");

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ImageView menuIcon = (ImageView) findViewById(R.id.menu_icon);

        Button confirmBtn = (Button) findViewById(R.id.deleteAcc);
        Button b1 = (Button) findViewById(R.id.mainBtn);
        Button b2 = (Button) findViewById(R.id.addMed);
        Button b3 = (Button) findViewById(R.id.editMed);
        Button b4 = (Button) findViewById(R.id.editData);
        Button b5 = (Button) findViewById(R.id.ordersPage);
        Button b6 = (Button) findViewById(R.id.messagePage);

        myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        myConnection = new MyConnection(getApplicationContext() );


    //Listeners
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(Gravity.RIGHT) )
                    drawer.closeDrawer(Gravity.RIGHT);
                else
                    drawer.openDrawer(Gravity.RIGHT);
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MyDialog dialog = new MyDialog(Seller11Activity.this);
                dialog.show();

                dialog.dialogText.setText("هل أنت متأكد من حذف جميع بياناتك ؟");
                dialog.no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (myConnection.getConnection() ) {

                            ref = FirebaseDatabase.getInstance().getReference();

                            ref.child("store").child(myId).removeValue();
                            ref.child("pharmacy").child(myId).removeValue();
                            ref.child("sellers").child(myId).removeValue();

                            FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                            currUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    dialog.dismiss();

                                    Toast.makeText(getApplicationContext(), "تم حذف الحساب الخاص بك", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                }
                            });
                        }
                    }
                });
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Seller5Activity.class) );
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Seller6Activity.class) );
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Seller8Activity.class) );
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Seller7Activity.class) );
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Seller10Activity.class) );
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Seller9Activity.class) );
            }
        });
    }


    private void updateOnlineState() {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();    //check if i delete account
        if (user != null) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userRef.child("online").setValue(false);
        }
    }
}