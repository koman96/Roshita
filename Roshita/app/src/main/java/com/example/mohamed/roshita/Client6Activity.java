package com.example.mohamed.roshita;
//request a medication
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Client6Activity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private DatabaseReference ref;
    private UserModel myUserModel;
    private HashMap medOrder;
    private DatabaseReference ordersRef;
    private Location myLoc;
    private String myAdd = "لا يوجد";
    private FusedLocationProviderClient myFused;
    private MyConnection myConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client6);

        Button b1 ,b2 ,b4 ,orderBtn ,b3;
        TextView toolbarHeader;
        final EditText orderMed;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarHeader = (TextView)findViewById(R.id.toolbar_header);
        toolbarHeader.setText("طـلـب دواء");

        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        ImageView menuView = (ImageView)findViewById(R.id.menu_icon);

        orderMed = (EditText)findViewById(R.id.orderMed);
        orderBtn = (Button)findViewById(R.id.orderBtn);

        b1 = (Button)findViewById(R.id.medSrchPage);    //buttons of side list
        b2 = (Button)findViewById(R.id.phrmSrchPage);
        b4 = (Button)findViewById(R.id.messagePage);
        b3 = (Button)findViewById(R.id.myOrdersPage);

        myConnection = new MyConnection(getApplicationContext() );

        updateOnlineState();
        getMyInfo();

        myFused = new FusedLocationProviderClient(this);
        if (!checkPermissions() )
            requestPermissions();
        else
            getLastLocation();


    //Listeners
        menuView.setOnClickListener(new View.OnClickListener() {
            //open the side menu
            @Override
            public void onClick(View v) {
                //if side menu is closed then open it and otherwise
                if(! drawerLayout.isDrawerOpen(Gravity.RIGHT))
                    drawerLayout.openDrawer(Gravity.RIGHT);
                else
                    drawerLayout.closeDrawer(Gravity.RIGHT);
            }
        });

        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String input = orderMed.getText().toString();

                if (! input.equals("") ){
                    if (myConnection.getConnection() ) {

                        //check if app have sellers
                        DatabaseReference sellersRef = FirebaseDatabase.getInstance().getReference("sellers");
                        sellersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() ) {    //we have sellers to notify

                                    medOrder = new HashMap();
                                    medOrder.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid() );
                                    medOrder.put("senderName", FirebaseAuth.getInstance().getCurrentUser().getDisplayName() );
                                    medOrder.put("senderAdd", getAddress(myLoc) );
                                    medOrder.put("medName", input);

                                    ordersRef = FirebaseDatabase.getInstance().getReference().child("orders");
                                    ordersRef.child(String.valueOf(Calendar.getInstance().getTime() )).setValue(medOrder);

                                    notifySellers(input);
                                    orderMed.setText("");

                                    Toast.makeText(getApplicationContext(), "تم إنشاء الطلب الخاص بك", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getApplicationContext(), "لا توجد صيدليات في التطبيق بعد كي يتم إعلامها", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {  //go to search a pharmacy
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext(),Client3Activity.class));
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {  //go to order a medicine
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext(),Client5Activity.class));
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {  //go to chat
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,ClientInboxActivity.class) );
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {  //go to order a medicine
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext(),Client8Activity.class));
            }
        });
    }


//Functions
    private void updateOnlineState() {
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid() );
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

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid() );
        userRef.child("online").setValue(false);
    }

    private void getMyInfo(){
        ref = FirebaseDatabase.getInstance().getReference("clients").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myUserModel = dataSnapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void notifySellers(final String orderedMed){        //push this notification to each seller
        final String date = Calendar.getInstance().getTime().toString();
        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("notifications");

        final HashMap map = new HashMap();
        map.put("sender" ,myUserModel.getUserName() );
        map.put("medName" ,orderedMed);

        ref = FirebaseDatabase.getInstance().getReference("pharmacy");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot object : dataSnapshot.getChildren() ){

                    final String sellerId = object.getKey();
                    orderRef.child(sellerId).child("orders").child(date).setValue(map);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    private String getAddress(Location location){
        if (myLoc != null) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            ArrayList<Address> addresses = new ArrayList<>();

            try {
                addresses = (ArrayList<Address>) geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (!addresses.isEmpty())
                    myAdd = addresses.get(0).getAddressLine(0);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return myAdd;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackBar("برجاء الضغط علي الاعدادات وتفعيل الموقع" ,"الإعدادات", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Build intent that displays the App settings screen.
                        Intent intent = new Intent();
                        intent.setAction(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package",
                                BuildConfig.APPLICATION_ID, null);
                        intent.setData(uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        }
    }


    public boolean checkPermissions(){
        int permissionState = ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void startLocationPermissionRequest(){
        ActivityCompat.requestPermissions(this ,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION} ,REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    public void requestPermissions(){
        boolean shouldProvideRational =
                ActivityCompat.shouldShowRequestPermissionRationale(this , android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRational){
            showSnackBar("برجاء الضغط علي الاعدادات وتفعيل الموقع", "الاعدادات", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package",
                            BuildConfig.APPLICATION_ID, null);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
        else {
            startLocationPermissionRequest();
        }
    }

    public void showSnackBar(final String mainTextString , final String actionString , View.OnClickListener listener){
        Snackbar.make(findViewById(android.R.id.content) ,mainTextString ,
                Snackbar.LENGTH_INDEFINITE).setAction(actionString ,listener);
    }

    public void getLastLocation() {
        myFused.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful() && task.getResult() != null) {
                            myLoc = task.getResult();
                        }
                    }
                });
    }
}