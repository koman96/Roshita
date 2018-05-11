package com.example.mohamed.roshita;
//client main page 'search medicine'
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Client3Activity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private RadioButton radioSearch1 ,radioSearch2;
    private RecyclerView clientRecyclerView;
    private DrawerLayout drawerLayout;
    private Button b1 ,b2 ,b3 ,b4 ,searchBtn;
    private AutoCompleteTextView searchText;
    private TextView toolbarHeader ,resultInfo;
    private Location myLoc ,searchLoc ,location ,sortLoc;
    private FusedLocationProviderClient myFused;
    private DatabaseReference ref;
    private ArrayList<SearchObject> result = new ArrayList<>();
    private String inputMed;
    private Intent myIntent;
    private ProgressBar bar;
    private ResultAdapter adapter;
    private int i;
    private MyConnection myConnection;
    private ArrayList<SearchPharmModel> appPharms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client3);

        bar = (ProgressBar)findViewById(R.id.progBar);
        bar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this, R.color.gold), PorterDuff.Mode.SRC_IN );

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarHeader = (TextView)findViewById(R.id.toolbar_header);
        toolbarHeader.setText("بحـث عـن دواء");

        searchText = (AutoCompleteTextView) findViewById(R.id.searchMed);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<> (getApplicationContext() ,R.layout.autocomplete_text ,getAutoTexts() );
            searchText.setAdapter(arrayAdapter);

        searchBtn = (Button)findViewById(R.id.searchBtn);
        resultInfo = (TextView)findViewById(R.id.resultInfo);

        ImageView menuView = (ImageView)findViewById(R.id.menu_icon);
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

        radioSearch1 = (RadioButton)findViewById(R.id.radioSearch1);
        radioSearch2 = (RadioButton)findViewById(R.id.radioSearch2);

        clientRecyclerView = (RecyclerView)findViewById(R.id.clientRecyclerView);
        clientRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext() ,LinearLayoutManager.VERTICAL ,false) );

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        b2 = (Button)findViewById(R.id.phrmSrchPage);   //buttons of side list
        b3 = (Button)findViewById(R.id.medOrdPage);
        b4 = (Button)findViewById(R.id.messagePage);
        b1 = (Button) findViewById(R.id.myOrdersPage);

        myFused = new FusedLocationProviderClient(this);
        myConnection = new MyConnection(getApplicationContext() );

        updateOnlineState();
        listenForIncomingMessages(FirebaseAuth.getInstance().getCurrentUser().getUid() );


        //check which Activity i came from
        myIntent = getIntent();

        if (myIntent.hasExtra("cameFrom") ){
            radioSearch2.setChecked(true);

            double[] searchPoint = myIntent.getDoubleArrayExtra("searchLoc");
            searchLoc = new Location("مكان البحث");

            searchLoc.setLatitude(searchPoint[0] );
            searchLoc.setLongitude(searchPoint[1] );

            Toast.makeText(Client3Activity.this ,"تم تحديد مكان البحث. ادخل إسم الدواء المراد البحث عنه" ,Toast.LENGTH_LONG).show();
        }
        else {
            if (! checkPermissions() )
                requestPermissions();
            else
                getLastLocation();
        }

        loadAppPharms();


    //Listeners
        radioSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions() ){

                    if (myLoc != null) {
                        Intent intent = new Intent(Client3Activity.this, ClientSelectMap.class);
                        intent.putExtra("centerPoint", new double[]{    myLoc.getLatitude() ,myLoc.getLongitude()   });
                        startActivity(intent);
                    }
                }else
                    Toast.makeText(Client3Activity.this ,"برجاء الذهاب للإعدادات لتفعيل الموقع" ,Toast.LENGTH_SHORT).show();
            }
        });

        radioSearch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get my location
                if (! checkPermissions() )
                    requestPermissions();
                else
                    getLastLocation();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputMed = searchText.getText().toString();

                if(! inputMed.equals("") ){    //search query is not empty
                    if (myConnection.getConnection() ) {

                        resultInfo.setVisibility(View.INVISIBLE);
                        clientRecyclerView.setAdapter(null);

                        if (! appPharms.isEmpty() ){

                            if (radioSearch1.isChecked() )
                                location = myLoc;
                            else
                                location = searchLoc;


                            if (location != null){
                                //first sort location

                                if (sortLoc != null){   //  searched before
                                    if (location.distanceTo(sortLoc) != 0)      //location changed
                                        sort_pharms_by_my_location(location);
                                }
                                else
                                    sort_pharms_by_my_location(location);

                                //now check if sellers have med and print results
                                new SearchTask().execute();

                            }
                            else
                                Toast.makeText(Client3Activity.this, "غير قادر علي الحصول علي موقعك. برجاء مراجعة اعدادات الموقع", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getApplicationContext() ,"لا توجد صيدليات في التطبيق بعد" ,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {  //go to search a pharmacy
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Client5Activity.class) );
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {  //go to order a medicine
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getBaseContext() ,Client6Activity.class) );
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {  //go to chat
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,ClientInboxActivity.class) );
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {  //go to chat
            @Override
            public void onClick(View v) {
                if (myConnection.getConnection() )
                startActivity(new Intent(getApplicationContext() ,Client8Activity.class) );
            }
        });
    }

    private void listenForIncomingMessages(final String myId) {
        DatabaseReference messRef = FirebaseDatabase.getInstance().getReference("notifications").child(myId).child("messages");
        messRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String sender = dataSnapshot.child("sender").getValue(String.class);
                String senderId = dataSnapshot.child("senderId").getValue(String.class);

                showMessNotification(sender ,senderId);
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

    private void showMessNotification(String sender ,String senderId) {

        String messageBody = "لديك رسالة جديدة من " + sender;

        Intent intent = new Intent(this ,ChatActivity.class);
        intent.putExtra("receiverId" ,senderId);
        intent.putExtra("receiverType" ,"seller");
        intent.putExtra("receiverName" ,sender);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this ,0 ,intent ,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.logo)
                .setStyle( new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("رسالة جديدة")
                        .bigText(messageBody) )
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis() ,builder.build() );
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
        int permissionState = ActivityCompat.checkSelfPermission(this , android.Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void startLocationPermissionRequest(){
        ActivityCompat.requestPermissions(this ,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION} ,REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    public void requestPermissions(){
        boolean shouldProvideRational =
                ActivityCompat.shouldShowRequestPermissionRationale(this , Manifest.permission.ACCESS_FINE_LOCATION);

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
                            myLoc = task.getResult();
                        }
                    }
                });
    }

    public void locationOfSortPharms(Location location){
        sortLoc = location;
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

    @Override
    public void onBackPressed() {
        myConnection.exitApp();
    }

    private ArrayList<String> getAutoTexts(){
        final ArrayList<String> suggMeds = new ArrayList<>();
        StorageReference ref = FirebaseStorage.getInstance().getReference("sugMeds.txt");

        try {
            final File sugFile = File.createTempFile("sugMeds" ,"txt");
            ref.getFile(sugFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    //file has downloaded succesfully
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader(sugFile) );
                        String medName;

                        while ( (medName = reader.readLine() ) != null){
                            suggMeds.add(medName);
                        }
                        reader.close();

                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext() ,"حدث خطأ أثناء عرض الأدوية المقترحة" ,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext() ,"حدث خطأ أثناء عرض الأدويـة المقترحة" ,Toast.LENGTH_SHORT).show();
        }

        return suggMeds;
    }


    private void loadAppPharms(){

        ref = FirebaseDatabase.getInstance().getReference("pharmacy");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (! dataSnapshot.exists() )
                    Toast.makeText(getApplicationContext() ,"لا يوجد صيدليات في التطبيق بعد" ,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                String ownerId = dataSnapshot.getKey();
                String pharmName = dataSnapshot.child("pharmName").getValue(String.class);
                double addLatitude = dataSnapshot.child("addLatitude").getValue(Double.class);
                double addLongitude = dataSnapshot.child("addLongitude").getValue(Double.class);

                appPharms.add(new SearchPharmModel(ownerId ,pharmName ,addLatitude ,addLongitude ,0) );
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                String ownerId = dataSnapshot.getKey();
                String pharmName = dataSnapshot.child("pharmName").getValue(String.class);
                double addLatitude = dataSnapshot.child("addLatitude").getValue(Double.class);
                double addLongitude = dataSnapshot.child("addLongitude").getValue(Double.class);


                for (int i=0 ; i<appPharms.size() ; i++){
                    SearchPharmModel oldModel = appPharms.get(i);

                    if (oldModel.getOwnerId().equals(ownerId) ){     //remove old one and add new one
                        appPharms.remove(oldModel);
                        appPharms.add(new SearchPharmModel(ownerId ,pharmName ,addLatitude ,addLongitude ,0) );
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                String ownerId = dataSnapshot.getKey();
                String pharmName = dataSnapshot.child("pharmName").getValue(String.class);
                double addLatitude = dataSnapshot.child("addLatitude").getValue(Double.class);
                double addLongitude = dataSnapshot.child("addLongitude").getValue(Double.class);

                appPharms.remove(new SearchPharmModel(ownerId ,pharmName ,addLatitude ,addLongitude ,0) );
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void sort_pharms_by_my_location(Location myLocation){
        locationOfSortPharms(myLocation);       //set sort location to this location

        //first assign distance to each pharmacy
        for (int i=0 ; i<appPharms.size() ; i++){
            SearchPharmModel pharmModel = appPharms.get(i);

            Location pharmLoc = new Location("pharmLocation");
            pharmLoc.setLatitude(pharmModel.getAddLatitude() );
            pharmLoc.setLongitude(pharmModel.getAddLongitude() );

            float distance = myLocation.distanceTo(pharmLoc);
            pharmModel.setDistance(distance);
        }

        //now sort those pharms so the min distance comes first
        for (int x=0 ; x<appPharms.size() ; x++){
            SearchPharmModel model1 = appPharms.get(x);

            for (int y=x+1 ; y<appPharms.size() ; y++){
                SearchPharmModel model2 = appPharms.get(y);

                if (model2.getDistance() < model1.getDistance() ){      //replace them
                    appPharms.set(x ,model2);
                    appPharms.set(y ,model1);
                }
            }
        }
    }

    private void searchForTheMed(){
        result = new ArrayList<>();
        ref = FirebaseDatabase.getInstance().getReference("store");

        for (i=0 ; i<appPharms.size() ; i++){
            final SearchPharmModel model = appPharms.get(i);

            ref.child(model.getOwnerId() ).child(inputMed).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists() )       //this pharm has med
                        result.add(new SearchObject(model.getPharmName() ,model.getOwnerId() ,dataSnapshot.getValue(String.class) ));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

    private void showResults(){
        resultInfo.setVisibility(View.VISIBLE);

        adapter = new ResultAdapter(getApplicationContext(), clientRecyclerView, result, inputMed);
        clientRecyclerView.setAdapter(adapter);

        Toast.makeText(Client3Activity.this, "إضغط علي نتائج البحث لمزيد من المعلومات", Toast.LENGTH_LONG).show();
    }


    private class SearchTask extends AsyncTask< Void ,Void ,String >{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            searchForTheMed();

            publishProgress();
            return "done";
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (result.isEmpty() )
                        Toast.makeText(getApplicationContext() ,"لم نجد الدواء الخاص بك" ,Toast.LENGTH_SHORT).show();
                    else
                        showResults();

                    bar.setVisibility(View.INVISIBLE);
                }
            } ,500);
        }
    }
}