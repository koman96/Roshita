package com.example.mohamed.roshita;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private String userId;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final MyConnection myConnection = new MyConnection(getApplicationContext() );
        myConnection.checkConnection();

        setContentView(R.layout.progress);

        ProgressBar bar = (ProgressBar)findViewById(R.id.progBar);
        bar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_IN );


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            setContentView(R.layout.activity_main);

            Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            //get Xml Views
            Button sellerBtn = (Button) findViewById(R.id.sellerBtn);
            Button clientBtn = (Button) findViewById(R.id.clientBtn);

            sellerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myConnection.getConnection() )
                        startActivity(new Intent(getBaseContext(), Seller1Activity.class) );
                }
            });

            clientBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (myConnection.getConnection() )
                        startActivity(new Intent(getBaseContext(), Client1Activity.class) );
                }
            });
        }
        else {
            userId = user.getUid();
            ref = FirebaseDatabase.getInstance().getReference("clients");

            ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() )
                        startActivity(new Intent(getBaseContext(), Client3Activity.class) );

                    else{
                        ref = FirebaseDatabase.getInstance().getReference("sellers");
                        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                if (dataSnapshot1.exists() ) {  //logged in before

                                    ref = FirebaseDatabase.getInstance().getReference("pharmacy");
                                    ref.child(userId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot2) {
                                            if (dataSnapshot2.exists() ){   //has pharmacy

                                                ref = FirebaseDatabase.getInstance().getReference("store");
                                                ref.child(userId).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot3) {
                                                        if (dataSnapshot3.exists() )    //has meds
                                                            startActivity(new Intent(getBaseContext(), Seller5Activity.class) );
                                                        else
                                                            startActivity(new Intent(getBaseContext(), Seller4Activity.class) );
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {}
                                                });
                                            }
                                            else    //don't have pharmacy
                                                startActivity(new Intent(getBaseContext(), Seller3Activity.class) );
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
}