package com.example.mohamed.roshita;
//my orders
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Client8Activity extends AppCompatActivity {
    private String myId;
    private ArrayList<MyOrderModel> myOrders = new ArrayList<>();
    private MyOrderAdapter adapter;
    private RecyclerView myOrdersList;
    private TextView myOrdersText;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client8);

        initializeViews();

        updateOnlineState();

        getMyOrders();
    }


//Functions
    private void initializeViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView header = (TextView) findViewById(R.id.toolbar_header);
        header.setText("طلباتي");

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        ImageView menuIcon = (ImageView) findViewById(R.id.menu_icon);

        myOrdersText = (TextView) findViewById(R.id.myOrdersText);

        myOrdersList = (RecyclerView) findViewById(R.id.myOrdersList);
        myOrdersList.setLayoutManager(new LinearLayoutManager(getApplicationContext() ,LinearLayoutManager.VERTICAL ,false) );

        Button b1 = (Button) findViewById(R.id.medSrchPage);
        Button b2 = (Button) findViewById(R.id.phrmSrchPage);
        Button b3 = (Button) findViewById(R.id.medOrdPage);
        Button b4 = (Button) findViewById(R.id.messagePage);

        myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        adapter = new MyOrderAdapter(this ,myOrders ,myOrdersList);

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

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Client3Activity.class) );
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Client5Activity.class) );
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Client6Activity.class) );
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,ClientInboxActivity.class) );
            }
        });
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
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid() );
                userRef.child("online").setValue(true);
            }
        } ,2000);
    }

    @Override
    protected void onStop() {
        super.onStop();

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("clients").child(myId);
        userRef.child("online").setValue(false);
    }

    private void getMyOrders(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (! dataSnapshot.exists() )
                    Toast.makeText(getApplicationContext() ,"لا توجد طلبات بعد" ,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String senderId = dataSnapshot.child("senderId").getValue(String.class);

                if (senderId.equals(myId) ){    //my order
                    myOrders.add(new MyOrderModel(dataSnapshot.child("medName").getValue(String.class) ,dataSnapshot.getKey() ));
                    myOrdersList.setAdapter(adapter);

                    myOrdersText.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "إضغط علي الطلب طويلا للمسـح", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                myOrdersList.setAdapter(adapter);

                if (myOrders.isEmpty() )
                    myOrdersText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}