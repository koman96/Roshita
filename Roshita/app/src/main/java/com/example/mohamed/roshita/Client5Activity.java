package com.example.mohamed.roshita;
//search for a pharmacy
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Client5Activity extends AppCompatActivity {
    private RadioButton myLoc ,anotherLoc;
    private DrawerLayout drawerLayout;
    private Button b1 ,b2 ,b3 ,b4 ,searchBtn;
    private TextView toolbarHeader ,resultInfo;
    private EditText search;
    private RecyclerView recyclerView;
    private ProgressBar bar;
    private DatabaseReference ref;
    private ArrayList<PharmModel> result;
    private FusedLocationProviderClient myFused;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private Location myLocation ,searchLoc ,location;
    private Intent intent;
    private ArrayList<String> sellersId;
    private MyConnection myConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client5);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarHeader = (TextView)findViewById(R.id.toolbar_header);
        toolbarHeader.setText("بحـث عـن صـيـدليـة");

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        ImageView menuView = (ImageView)findViewById(R.id.menu_icon);

        myLoc = (RadioButton)findViewById(R.id.radioSearch1);
        anotherLoc = (RadioButton)findViewById(R.id.radioSearch2);

        search = (EditText)findViewById(R.id.search);
        searchBtn = (Button)findViewById(R.id.searchBtn);
        resultInfo = (TextView)findViewById(R.id.resultInfo);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext() ,LinearLayoutManager.VERTICAL ,false) );
        bar = (ProgressBar)findViewById(R.id.progBar);
        bar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_IN );
        b1 = (Button)findViewById(R.id.medSrchPage);    //buttons of side list
        b3 = (Button)findViewById(R.id.medOrdPage);
        b4 = (Button)findViewById(R.id.messagePage);
        b2 = (Button) findViewById(R.id.myOrdersPage);

        myConnection = new MyConnection(getApplicationContext() );
        updateOnlineState();

    //see which activity we came from
        intent = getIntent();
        if (intent.hasExtra("cameFrom") ){
            anotherLoc.setChecked(true);

            double[] searchPoint = intent.getDoubleArrayExtra("searchLoc");
            searchLoc = new Location("مكان البحث");

            searchLoc.setLatitude(searchPoint[0] );
            searchLoc.setLongitude(searchPoint[1] );

            Toast.makeText(getApplicationContext() ,"تم تحديد مكان البحث. ادخل إسم الصيدلية المراد البحث عنها" ,Toast.LENGTH_LONG).show();
        }
        else {
            myFused = new FusedLocationProviderClient(this);
            if (! checkPermissions() )
                requestPermissions();
            else
                getLastLocation();
        }


    //Listeners
        menuView.setOnClickListener(new View.OnClickListener() {
            //open the side menu
            @Override
            public void onClick(View v) {
                //if side menu is closed then open it and otherwise
                if(drawerLayout.isDrawerOpen(Gravity.RIGHT))
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                else
                    drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });

        anotherLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationFromMap();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = search.getText().toString();

                if (! input.equals("") ){
                    if (myConnection.getConnection() ) {

                        if (myLoc.isChecked())
                            location = myLocation;
                        else
                            location = searchLoc;

                        if (location != null) {
                            resultInfo.setVisibility(View.INVISIBLE);
                            recyclerView.setAdapter(null);

                            bar.setVisibility(View.VISIBLE);
                            searchForPharm(input);
                        } else
                            Toast.makeText(getApplicationContext(), "غير قادر علي الحصول علي موقعك. برجاء مراجعة اعدادات الموقع", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {  //go to search a pharmacy
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext(),Client3Activity.class) );
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {  //go to order a medicine
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext(),Client6Activity.class) );
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {  //go to chat
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,ClientInboxActivity.class) );
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {  //go to order a medicine
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext(),Client8Activity.class) );
            }
        });
    }


    //Functions
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
            showSnackBar("برجاء الضغط علي الاعدادات وتفعيل الموقع" ,"الاعدادات", new View.OnClickListener() {
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
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Location> task) {

                        if (task.isSuccessful() && task.getResult() != null) {
                            myLocation = task.getResult();
                        }
                    }
                });
    }

    public void searchForPharm(final String input){
        result = new ArrayList<>();
        sellersId = new ArrayList<>();

        ref = FirebaseDatabase.getInstance().getReference("pharmacy");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot pharm : dataSnapshot.getChildren() ){

                    if (input.equals( pharm.child("pharmName").getValue().toString() )){
                        PharmModel pharmModel = pharm.getValue(PharmModel.class);

                        result.add(pharmModel);
                        sellersId.add(pharm.getKey() );
                    }
                }

                sortResultsByNearest();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void sortResultsByNearest(){
        if (! result.isEmpty() ){
            if (result.size() > 1){

                for (int i=0 ; i<result.size() ; i++) {
                    for (int j = i + 1; j < result.size(); j++) {
                        PharmModel model1 = result.get(i);
                        PharmModel model2 = result.get(j);

                        Location location1 = new Location("loc1");
                        location1.setLatitude(model1.getAddLatitude());
                        location1.setLongitude(model1.getAddLongitude());
                        float distance1 = location.distanceTo(location1);

                        Location location2 = new Location("loc2");
                        location2.setLatitude(model2.getAddLatitude());
                        location2.setLongitude(model2.getAddLongitude());
                        float distance2 = location.distanceTo(location2);

                        if (distance2 < distance1) {     //replace them
                            result.set(i, model2);
                            result.set(j, model1);
                        }
                    }
                }
            }
            showResults();
        }
        else
            Toast.makeText(getApplicationContext() ,"لم نجد صيدلية بهذا الإسـم" ,Toast.LENGTH_SHORT).show();

        bar.setVisibility(View.INVISIBLE);
    }

    public void showResults(){
        resultInfo.setVisibility(View.VISIBLE);
        PharmAdapter adapter = new PharmAdapter(result ,recyclerView ,getApplicationContext() ,sellersId);

        recyclerView.setAdapter(adapter);
        Toast.makeText(getApplicationContext() ,"إضغط علي نتيجة البحث لمزيد من التفاصيل" ,Toast.LENGTH_LONG).show();
    }

    public void getLocationFromMap(){
        if (checkPermissions() ){

            if (myLocation != null) {
                Intent intent = new Intent(getApplicationContext(), ClientSelectMap.class);
                intent.putExtra("centerPoint", new double[]{ myLocation.getLatitude() ,myLocation.getLongitude() })
                    .putExtra("cameFrom" ,"Client5Activity");
                startActivity(intent);
            }
        }else
            Toast.makeText(getApplicationContext() ,"برجاء الذهاب للإعدادات لتفعيل الموقع" ,Toast.LENGTH_SHORT).show();
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
}