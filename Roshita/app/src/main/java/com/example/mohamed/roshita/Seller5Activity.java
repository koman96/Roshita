package com.example.mohamed.roshita;
//seller main activity
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
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

public class Seller5Activity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    Button b1 ,b2 ,b3 ,b4 ,b5 ,b6;
    String userId;
    DatabaseReference ref;
    ArrayList<String[]> medArray = new ArrayList<>();
    StoreAdapter adapter;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller5);

        myConnection = new MyConnection(getApplicationContext() );

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbarHeader = (TextView)findViewById(R.id.toolbar_header);
        toolbarHeader.setText("القائمـة الرئيـسيـة");

        ImageView menuIcon = (ImageView)findViewById(R.id.menu_icon);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerOpen(Gravity.RIGHT) )
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                else
                    drawerLayout.openDrawer(Gravity.RIGHT);

            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.listView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext() ,LinearLayoutManager.VERTICAL ,false) );

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        updateOnlineState();

        getSellerMeds();    //and put it in list

        listenForIncomingMessages();
        listenForMedOrders();

        b1 = (Button)findViewById(R.id.addMed);
        b2 = (Button)findViewById(R.id.editData);
        b3 = (Button)findViewById(R.id.editMed);
        b4 = (Button)findViewById(R.id.messagePage);
        b5 = (Button)findViewById(R.id.ordersPage);
        b6 = (Button)findViewById(R.id.deleteAccPage);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                    startActivity(new Intent(getBaseContext() ,Seller6Activity.class));
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                    startActivity(new Intent(getBaseContext() ,Seller7Activity.class) );
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                    startActivity(new Intent(getBaseContext() ,Seller8Activity.class) );
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                    startActivity(new Intent(getBaseContext() ,Seller9Activity.class) );
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                    startActivity(new Intent(getBaseContext() ,Seller10Activity.class) );

            }
        });

        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                    startActivity(new Intent(getBaseContext() ,Seller11Activity.class) );

            }
        });
    }

    private void listenForIncomingMessages() {

        DatabaseReference messRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId).child("messages");
        messRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                final String sender = dataSnapshot.child("sender").getValue(String.class);
                final String senderId = dataSnapshot.child("senderId").getValue(String.class);

                createMessNotification(sender ,senderId);
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

    private void listenForMedOrders() {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId).child("orders");
        orderRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String medName = dataSnapshot.child("medName").getValue(String.class);
                String sender = dataSnapshot.child("sender").getValue(String.class);

                showOrderNotification(medName ,sender);
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

    private void showOrderNotification(String medName ,String sender) {
        String messageBody = "قام العميل " + sender + " بطلب الدواء " + medName;

        Intent intent = new Intent(this ,Seller10Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this ,0 ,intent ,Intent.FLAG_ACTIVITY_NEW_TASK);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.logo)
                .setStyle( new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("طلب دواء")
                        .bigText(messageBody) )
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0 ,builder.build() );
    }

    public void getSellerMeds(){
        adapter = new StoreAdapter(medArray);
        ref = FirebaseDatabase.getInstance().getReference("store");

        ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (! dataSnapshot.exists() ) {
                    Toast.makeText(getApplicationContext(), "لا يوجد لديك أدوية", Toast.LENGTH_SHORT).show();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext() ,Seller4Activity.class) );
                        }
                    } ,1000);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        ref.child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                medArray.add(new String[]{  dataSnapshot.getKey() ,dataSnapshot.getValue().toString() });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                medArray.remove(new String[]{   dataSnapshot.getKey() ,dataSnapshot.getValue().toString() });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

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

    @Override
    public void onBackPressed() {
        myConnection.exitApp();
    }

    private void createMessNotification(String sender ,String senderId){
        String messageBody = "لديك رسالة جديدة من " + sender;

        Intent intent = new Intent(this ,ChatActivity.class);
        intent.putExtra("receiverId" ,senderId);
        intent.putExtra("receiverType" ,"client");
        intent.putExtra("receiverName" ,sender);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this ,0 ,intent ,Intent.FLAG_ACTIVITY_NEW_TASK);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.logo)
                .setStyle( new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("رسالة جديدة")
                        .bigText(messageBody) )
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0 ,builder.build() );
    }
}