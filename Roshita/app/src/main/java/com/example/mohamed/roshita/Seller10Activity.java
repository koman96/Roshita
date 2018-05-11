package com.example.mohamed.roshita;
//meds orders
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

public class Seller10Activity extends AppCompatActivity {
    private TextView mainBar;
    private DrawerLayout drawer;
    private ImageView menuIcon;
    private TextView medOrdersText;
    private RecyclerView ordersList;
    private DatabaseReference ordersRef;
    private ArrayList<OrderModel> orders = new ArrayList<>();
    private OrderAdapter adapter;
    private Button b1 ,b2 ,b3 ,b4 ,b5 ,b6;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller10);

        initializeViews();
        updateOnlineState();

        loadOrders();
        deleteOrdersNotifications();
    }

    private void deleteOrdersNotifications() {
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ordersRef = FirebaseDatabase.getInstance().getReference("notifications").child(myId).child("orders");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() ){
                    dataSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void initializeViews(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mainBar = (TextView) findViewById(R.id.toolbar_header);
        mainBar.setText("طلبات الأدوية");

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        menuIcon = (ImageView) findViewById(R.id.menu_icon);

        medOrdersText = (TextView) findViewById(R.id.medsOrdersText);
        ordersList = (RecyclerView) findViewById(R.id.ordersList);

        ordersList.setLayoutManager(new LinearLayoutManager(getApplicationContext() ,LinearLayoutManager.VERTICAL ,false) );
        adapter = new OrderAdapter(getApplicationContext() ,orders ,ordersList);

        b1 = (Button) findViewById(R.id.mainBtn);
        b2 = (Button) findViewById(R.id.addMed);
        b3 = (Button) findViewById(R.id.editMed);
        b4 = (Button) findViewById(R.id.editData);
        b5 = (Button) findViewById(R.id.messagePage);
        b6 = (Button)findViewById(R.id.deleteAccPage);

        myConnection = new MyConnection(getApplicationContext() );

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
                startActivity(new Intent(getApplicationContext() ,Seller9Activity.class) );
            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Seller11Activity.class) );

            }
        });

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
    }


//Functions
    private void loadOrders(){
        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (! dataSnapshot.exists() )
                    Toast.makeText(getApplicationContext(), "لا توجد طلبات بعد", Toast.LENGTH_SHORT).show();

                else {
                    medOrdersText.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "إضغط للرد علي الطلـب", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        //listener to orders
        ordersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String medName = dataSnapshot.child("medName").getValue(String.class);
                String senderId = dataSnapshot.child("senderId").getValue(String.class);
                String senderName = dataSnapshot.child("senderName").getValue(String.class);
                String senderAdd = dataSnapshot.child("senderAdd").getValue(String.class);

                orders.add(new OrderModel(medName ,senderId ,senderName ,senderAdd ,dataSnapshot.getKey() ));

                ordersList.setAdapter(adapter);
                medOrdersText.setVisibility(View.VISIBLE);
                ordersList.scrollToPosition(orders.size()-1);   //scroll to bottom
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
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

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.child("online").setValue(false);
    }
}