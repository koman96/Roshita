package com.example.mohamed.roshita;
//create pharmacy
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Seller3Activity extends AppCompatActivity{

    protected Location myLoc;
    private FusedLocationProviderClient myFused;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    Button saveBtn;
    EditText pharmName, pharmPhone;
    RadioButton currLoc, elseLoc;
    CheckBox homeDelivery;
    String userId;
    DatabaseReference mDatabase;
    double addLongitude, addLatitude;
    private MyConnection myConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myConnection = new MyConnection(getApplicationContext() );

        checkIfHeHasPharmacy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
        int permissionState = ActivityCompat.checkSelfPermission(this ,Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void startLocationPermissionRequest(){
        ActivityCompat.requestPermissions(this ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION} ,REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    public void requestPermissions(){
        boolean shouldProvideRational =
                ActivityCompat.shouldShowRequestPermissionRationale(this ,Manifest.permission.ACCESS_FINE_LOCATION);

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

    private void checkIfHeHasPharmacy(){
        final String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("store");
        ref.child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() )
                    startActivity(new Intent(getApplicationContext() ,Seller5Activity.class) );

                else {  //he don't has meds but maybe he has pharmacy
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("pharmacy");
                    reference.child(myId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            if (dataSnapshot2.exists() )
                                startActivity(new Intent(getApplicationContext() ,Seller4Activity.class) );

                            else {
                                setContentView(R.layout.activity_seller3);
                                initializeViewsAndStartWork();
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

    private void initializeViewsAndStartWork(){
        myFused = LocationServices.getFusedLocationProviderClient(this);
        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        saveBtn = (Button) findViewById(R.id.saveBtn);
        pharmName = (EditText) findViewById(R.id.pharmName);
        pharmPhone = (EditText) findViewById(R.id.pharmPhone);
        currLoc = (RadioButton) findViewById(R.id.currLoc);
        elseLoc = (RadioButton) findViewById(R.id.elseLoc);
        homeDelivery = (CheckBox) findViewById(R.id.homeDelivery);

        if (!checkPermissions() )
            requestPermissions();
        else
            getLastLocation();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //create pharm

                if (!pharmName.getText().toString().equals("") && !pharmPhone.getText().toString().equals("")) {
                    if (myConnection.getConnection() ) {

                        if (myLoc != null) {
                            final String pharm_name = pharmName.getText().toString();
                            final String pharm_phone = pharmPhone.getText().toString();

                            addLongitude = myLoc.getLongitude();
                            addLatitude = myLoc.getLatitude();

                            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference("pharmacy");

                            if (currLoc.isChecked()) {
                                PharmModel pharmModel = new PharmModel(pharm_name, pharm_phone, addLongitude, addLatitude, homeDelivery.isChecked());
                                mDatabase.child(userId).setValue(pharmModel);

                                Toast.makeText(Seller3Activity.this, "تم إنشـاء الصيدلية الخاصـة بك بنجاح", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getBaseContext(), Seller4Activity.class));

                            } else {
                                Intent intent = new Intent(getBaseContext(), SellerMap.class);
                                double[] point = {addLatitude, addLongitude};
                                intent.putExtra("centerMap", point);

                                intent.putExtra("pharmName", pharm_name);
                                intent.putExtra("pharmPhone", pharm_phone);
                                intent.putExtra("delivery", homeDelivery.isChecked());

                                startActivity(intent);
                            }
                        } else
                            Toast.makeText(getBaseContext(), "لم نحصل علي موقعك بنجاح ,برجاء مراجعة ضبط الموقع", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getBaseContext(), "برجاء إدخال جميـع البيانات", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        myConnection.exitApp();
    }
}