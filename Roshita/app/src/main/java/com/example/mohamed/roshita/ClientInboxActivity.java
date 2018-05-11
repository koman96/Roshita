package com.example.mohamed.roshita;

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

public class ClientInboxActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView menuIcon;
    private TextView header;
    private DrawerLayout drawer;
    private RecyclerView recyclerView;
    private Button b1 ,b2 ,b3 ,b4;
    private ArrayList<String> sellersIds = new ArrayList<>();
    private ArrayList<UserModel> users = new ArrayList<>();
    private DatabaseReference ref;
    private MyConnection myConnection;
    private ClientChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_inbox);

        updateOnlineState();
        initializeViews();

        getChat();
        deleteMessageNotifications();
    }


    //Functions
    private void initializeViews() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        header = (TextView)findViewById(R.id.toolbar_header);
        header.setText("الرسـائل");

        menuIcon = (ImageView)findViewById(R.id.menu_icon);
        drawer = (DrawerLayout)findViewById(R.id.drawer);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext() ,LinearLayoutManager.VERTICAL ,false) );

        b1 = (Button)findViewById(R.id.medSrchPage);
        b2 = (Button)findViewById(R.id.phrmSrchPage);
        b3 = (Button)findViewById(R.id.medOrdPage);
        b4 = (Button) findViewById(R.id.myOrdersPage);

        myConnection = new MyConnection(getApplicationContext() );
        adapter = new ClientChatAdapter(getApplicationContext() ,users ,sellersIds ,recyclerView);


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
                startActivity(new Intent(getApplicationContext() ,Client8Activity.class) );
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

    private void getChat(){
        String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference("messages");

        //check if there is no messages
        ref.child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (! dataSnapshot.exists() )
                    Toast.makeText(getApplicationContext() ,"لا توجد رسائل بعد" ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //listen for messages
        ref.child(myId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String sellerId = dataSnapshot.getKey();    //id of seller iam chating with

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("sellers");
                userRef.child(sellerId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        sellersIds.add(sellerId);
                        users.add(dataSnapshot.getValue(UserModel.class) );

                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
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

    private void deleteMessageNotifications() {
        DatabaseReference messRef = FirebaseDatabase.getInstance().getReference("notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid() ).child("messages");
        messRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
}